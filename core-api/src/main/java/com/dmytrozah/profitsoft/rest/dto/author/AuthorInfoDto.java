package com.dmytrozah.profitsoft.rest.dto.author;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Setter;

@Builder
@Setter
public class AuthorInfoDto {

    @JsonProperty("id")
    private long id;

    @JsonProperty("name")
    private String name;

}
