package com.dmytrozah.profitsoft.service.impl;

import com.dmytrozah.profitsoft.domain.entity.BookAuthorData;
import com.dmytrozah.profitsoft.domain.entity.BookData;
import com.dmytrozah.profitsoft.domain.repository.BookAuthorRepository;
import com.dmytrozah.profitsoft.domain.repository.BookRepository;
import com.dmytrozah.profitsoft.rest.dto.BookListDto;
import com.dmytrozah.profitsoft.rest.dto.ReportGenerationDto;
import com.dmytrozah.profitsoft.rest.dto.author.AuthorDetailsDto;
import com.dmytrozah.profitsoft.rest.dto.author.AuthorInfoDto;
import com.dmytrozah.profitsoft.rest.dto.book.*;
import com.dmytrozah.profitsoft.service.BookAuthorService;
import com.dmytrozah.profitsoft.service.BookService;
import com.dmytrozah.profitsoft.service.exception.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    static final ObjectMapper mapper = new ObjectMapper();

    private final BookRepository bookRepository;

    private final BookAuthorRepository authorRepository;

    private final BookAuthorService authorService;

    @Override
    public long createBook(BookSaveDto bookSaveDto) {
        this.validateBook(bookSaveDto);

        return bookRepository.save(this.fromSaveDto(bookSaveDto)).getId();
    }

    @Override
    public void updateBook(long id, BookSaveDto dto) {
        this.validateBook(dto);

        BookData book = this.getOrThrow(id);

        this.updateFromDto(book, dto);
    }

    @Override
    public BookDetailsDto getBook(long id) {
        return toDetailsDto(id);
    }

    @Override
    public BookUploadResultDto uploadFromFile(final MultipartFile file) throws FileUploadException {
        try {
            byte[] bytes = file.getBytes();

            List<BookUploadDto> uploads = mapper.readValue(bytes, new TypeReference<>() {});
            List<BookData> data = uploads.stream()
                    .map(this::convertFromUpload)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            final int successfulUploads = bookRepository.saveAll(data).size();

            return BookUploadResultDto.builder()
                    .successfulUploads(successfulUploads)
                    .failedUploads(uploads.size() - successfulUploads)
                    .build();
        } catch (IOException e){
            throw new FileUploadException(e.getMessage());
        }
    }

    private BookData getOrThrow(final long id){
        return bookRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Requested Book %s not found.".formatted(id)));
    }

    private void validateBook(final BookSaveDto saveDto){
        if (Objects.nonNull(saveDto.getPublishDate())
                && saveDto.getPublishDate().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Publish Date is after Now");
        }
    }

    private BookDetailsDto toDetailsDto(long bookId){
        BookData book = this.getOrThrow(bookId);

        return BookDetailsDto.builder()
                .title(book.getTitle())
                .lastUpdateTime(book.getLastUpdateTime())
                .publication(book.getPublication())
                .build();
    }

    private void updateFromDto(final BookData data, final BookSaveDto saveDto){
        data.setAuthor(authorService.resolveAuthor(saveDto.getAuthor()));
        data.setTitle(saveDto.getTitle());
        data.setPublication(saveDto.getPublishDate());
        data.setGenres(saveDto.getGenres());

        bookRepository.save(data);
        bookRepository.flush();
    }

    private BookData fromSaveDto(BookSaveDto saveDto){
        final BookData bookData = new BookData();

        if (!authorService.existsAuthor(saveDto.getAuthor()))
            throw new EntityNotFoundException("Author %s not found.".formatted(saveDto.getAuthor()));

        bookData.setTitle(saveDto.getTitle());
        bookData.setAuthor(authorService.resolveAuthor(saveDto.getAuthor()));
        bookData.setPublication(saveDto.getPublishDate());
        bookData.setGenres(saveDto.getGenres());

        return bookData;
    }

    private BookInfoDto toInfoDto(final BookData data){
        return BookInfoDto.builder()
                .id(data.getId())
                .title(data.getTitle())
                .fullAuthorName(data.getAuthor().getName())
                .authorId(data.getAuthor().getId())
                .build();
    }

    private Optional<BookData> convertFromUpload(BookUploadDto bookUploadDto) throws EntityNotFoundException {
        final BookData bookData = new BookData();

        if (!authorRepository.existsByNameContainingIgnoreCase(bookUploadDto.getAuthor()))
            return Optional.empty();

        if (bookRepository.existsByTitleAndAuthorName(bookUploadDto.getTitle(), bookUploadDto.getAuthor()))
            return Optional.empty();

        bookData.setTitle(bookUploadDto.getTitle());
        bookData.setAuthor(authorService.resolveAuthor(bookUploadDto.getAuthor()));
        bookData.setGenres(bookUploadDto.getGenres());

        return Optional.of(bookData);
    }

    @Override
    public BookListDto listQuery(BookQueryDto queryDto) {
        final Page<BookData> page;

        if (queryDto.getAuthorId() == null) {
            page = bookRepository.findAll(PageRequest.of(queryDto.getPage(), queryDto.getSize()));
        } else  {
            page = bookRepository.findAllByAuthorId(
                    Long.parseLong(queryDto.getAuthorId()), PageRequest.of(queryDto.getPage(), queryDto.getSize()));
        }

        final List<BookData> data = page.getContent();
        final List<BookInfoDto> infos = data.stream().map(this::toInfoDto).toList();

        return BookListDto.builder()
                .totalPages(page.getTotalPages())
                .infos(infos)
                .build();
    }

    @Override
    public void delete(long id) {
        bookRepository.delete(getOrThrow(id));
    }

    @Override
    public void generateReport(ReportGenerationDto dto, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.csv");

        try {
            StringBuilder builder = new StringBuilder();
            List<BookData> books;

            books = dto == null ? bookRepository.findAll() :
                    bookRepository.findAllByAuthorId(dto.getAuthorId());

            builder.append("Title,Author\n");

            for (BookData book : books) {
                final BookAuthorData authorData = book.getAuthor();

                builder.append(book.getTitle())
                        .append(",").append(authorData.getName())
                        .append("\n");
            }

            final OutputStream outputStream = response.getOutputStream();
            outputStream.write(builder.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Error generating report file", e);
        }
    }
}
