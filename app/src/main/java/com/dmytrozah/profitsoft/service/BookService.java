package com.dmytrozah.profitsoft.service;

import com.dmytrozah.profitsoft.rest.dto.BookListDto;
import com.dmytrozah.profitsoft.rest.dto.ReportGenerationDto;
import com.dmytrozah.profitsoft.rest.dto.book.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {

    long createBook(BookSaveDto bookSaveDto);

    void updateBook(long id, BookSaveDto dto);

    BookDetailsDto getBook(long id);

    BookUploadResultDto uploadFromFile(final MultipartFile file) throws FileUploadException;

    BookListDto listQuery(final BookQueryDto queryDto);

    void delete(long id);

    void generateReport(ReportGenerationDto dto, HttpServletResponse response);
}
