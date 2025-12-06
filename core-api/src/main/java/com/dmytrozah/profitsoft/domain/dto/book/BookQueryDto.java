package com.dmytrozah.profitsoft.domain.dto.book;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Lookup criteria for {@link com.dmytrozah.profitsoft.domain.entity.BookData}
 * to query in the database
 */

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookQueryDto {

    @Builder.Default
    @JsonProperty("author_id")
    private String authorId = "-1";

    // offset <=> from
    @Builder.Default
    @JsonProperty(defaultValue = "0")
    private Integer page = 0;

    // limit <=> size
    @Builder.Default
    @JsonProperty(defaultValue = "50")
    private Integer size = 50;

}
