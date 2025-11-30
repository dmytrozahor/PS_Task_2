package com.dmytrozah.profitsoft.rest.dto;

import com.dmytrozah.profitsoft.rest.dto.book.BookInfoDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BookListDto {

    @JsonProperty("list")
    private List<BookInfoDto> infos;

    @JsonProperty("total_pages")
    private int totalPages;

}
