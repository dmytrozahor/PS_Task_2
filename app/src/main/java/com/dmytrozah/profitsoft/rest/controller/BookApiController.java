package com.dmytrozah.profitsoft.rest.controller;

import com.dmytrozah.profitsoft.rest.dto.BookListDto;
import com.dmytrozah.profitsoft.rest.dto.ReportGenerationDto;
import com.dmytrozah.profitsoft.rest.dto.RestResponse;
import com.dmytrozah.profitsoft.rest.dto.book.BookDetailsDto;
import com.dmytrozah.profitsoft.rest.dto.book.BookQueryDto;
import com.dmytrozah.profitsoft.rest.dto.book.BookSaveDto;
import com.dmytrozah.profitsoft.rest.dto.book.BookUploadResultDto;
import com.dmytrozah.profitsoft.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookApiController {

    private final BookService bookService;

    @PostMapping("_list")
    public BookListDto list(@RequestBody BookQueryDto queryDto){
        return bookService.listQuery(queryDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createBook(@Valid @RequestBody BookSaveDto bookSaveDto){
        return RestResponse.builder()
                .message(Long.toString(bookService.createBook(bookSaveDto)))
                .build();
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RestResponse updateBook(@PathVariable long id, @Valid @RequestBody BookSaveDto dto){
        bookService.updateBook(id, dto);
        return RestResponse.builder().message("OK").build();
    }

    @GetMapping("{id}")
    public BookDetailsDto getBook(@PathVariable long id){
        return bookService.getBook(id);
    }

    // MultiPartFile <-> Form data
    @PostMapping("upload")
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse uploadFromFile(@RequestParam("file") final MultipartFile file)
            throws FileUploadException {
        BookUploadResultDto uploadResult = bookService.uploadFromFile(file);

        if (uploadResult.getSuccessfulUploads() > 0)
            return RestResponse.builder()
                .message("%d books uploaded successfully (%d failures)"
                        .formatted(uploadResult.getSuccessfulUploads(), uploadResult.getFailedUploads()))
                .build();

        return  RestResponse.builder().message("No books could be uploaded.").build();
    }

    @PostMapping(value = "_report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void generateReport(@RequestBody(required = false) ReportGenerationDto dto, HttpServletResponse response) {
        bookService.generateReport(dto, response);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id){
        bookService.delete(id);
    }
}
