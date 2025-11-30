package com.dmytrozah.profitsoft.rest.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;


/**
 * Information on a particular {@link com.dmytrozah.profitsoft.domain.entity.BookData}
 * to save / update a book card
 */

@Getter
@Setter
@Builder
@Jacksonized
@AllArgsConstructor
public class BookSaveDto {

    @NotNull(message = "Title shouldn't be null!")
    @NotBlank(message = "Title shouldn't be blank!")
    private String title;

    @JsonProperty("publish_date")
    private LocalDate publishDate;

    @NotNull(message = "Author shouldn't be null!")
    @JsonProperty("author")
    private String author;

    @JsonProperty("genres")
    private String genres;

}
