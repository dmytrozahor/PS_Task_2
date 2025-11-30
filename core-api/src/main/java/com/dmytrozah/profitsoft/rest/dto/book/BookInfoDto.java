package com.dmytrozah.profitsoft.rest.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Short information on {@link com.dmytrozah.profitsoft.domain.entity.BookData}
 * to display in a list
 */

@Getter
@Builder
@AllArgsConstructor
public class BookInfoDto {

    @JsonProperty("id")
    private long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("full_author_name")
    private String fullAuthorName;

    @JsonProperty("author_id")
    private long authorId;

}
