package com.dmytrozah.profitsoft.rest.dto.author;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import lombok.*;


@AllArgsConstructor
@Getter @Setter
@Builder
public class AuthorQueryDto {

    // offset <=> from
    @Builder.Default
    @JsonProperty(defaultValue = "0")
    private Integer page = 0;

    // limit <=> size
    @Builder.Default
    @JsonProperty(defaultValue = "50")
    private Integer size = 50;

}
