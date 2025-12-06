package com.dmytrozah.profitsoft.service.impl;

import com.dmytrozah.profitsoft.domain.dto.ReportGenerationDto;
import com.dmytrozah.profitsoft.domain.dto.author.AuthorDetailsDto;
import com.dmytrozah.profitsoft.domain.dto.book.*;
import com.dmytrozah.profitsoft.domain.entity.BookAuthorData;
import com.dmytrozah.profitsoft.domain.entity.BookData;
import com.dmytrozah.profitsoft.domain.entity.mapper.AuthorMapper;
import com.dmytrozah.profitsoft.domain.entity.mapper.BookMapper;
import com.dmytrozah.profitsoft.domain.repository.BookRepository;
import com.dmytrozah.profitsoft.service.BookAuthorService;
import com.dmytrozah.profitsoft.service.BookService;
import com.dmytrozah.profitsoft.service.exception.BookNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final AuthorMapper authorMapper;

    private final BookMapper bookMapper;

    private final BookRepository bookRepository;

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

    private BookData getOrThrow(final long id){
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
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
        final AuthorDetailsDto detailsDto = authorService.resolveAuthorDetails(saveDto.getAuthorId());
        final BookAuthorData authorData = authorMapper.toEntity(detailsDto);
        data.setAuthor(authorData);

        this.bookMapper.updateEntityFromDto(saveDto, data, authorData);

        bookRepository.save(data);
        bookRepository.flush();
    }

    private BookData fromSaveDto(BookSaveDto saveDto){
        final AuthorDetailsDto detailsDto = authorService.resolveAuthorDetails(saveDto.getAuthorId());
        final BookData data = this.bookMapper.toEntity(saveDto);

        data.setAuthor(authorMapper.toEntity(detailsDto));

        return data;
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
        final List<BookInfoDto> infos = data.stream().map(bookMapper::toInfoDto).toList();

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
