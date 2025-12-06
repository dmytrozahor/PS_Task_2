package com.dmytrozah.profitsoft.domain.dto.author;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


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
