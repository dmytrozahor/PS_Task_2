package com.dmytrozah.profitsoft.rest.dto.author;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AuthorDetailsDto {

    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("contact_address")
    private String contactAddress;

    @JsonProperty("books_published")
    private int books;

}
