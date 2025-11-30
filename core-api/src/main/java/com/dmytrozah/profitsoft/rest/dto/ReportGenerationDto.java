package com.dmytrozah.profitsoft.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReportGenerationDto {

    @JsonProperty("author_id")
    private long authorId;

}
