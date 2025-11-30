package com.dmytrozah.profitsoft.service;

import com.dmytrozah.profitsoft.domain.entity.BookAuthorData;
import com.dmytrozah.profitsoft.rest.dto.author.AuthorDetailsDto;
import com.dmytrozah.profitsoft.rest.dto.author.AuthorInfoDto;
import com.dmytrozah.profitsoft.rest.dto.author.AuthorQueryDto;
import com.dmytrozah.profitsoft.rest.dto.author.AuthorSaveDto;

import java.util.List;

public interface BookAuthorService {

    long createAuthor(AuthorSaveDto dto);

    void updateAuthor(AuthorSaveDto saveDto);

    BookAuthorData resolveAuthor(String displayName);

    boolean existsAuthor(String displayName);

    AuthorInfoDto resolveAuthorInfo(long id);

    AuthorDetailsDto resolveAuthorDetails(long id);

    List<AuthorInfoDto> query(AuthorQueryDto dto);

    void delete(long id);
}
