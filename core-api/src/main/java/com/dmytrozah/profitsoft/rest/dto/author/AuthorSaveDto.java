package com.dmytrozah.profitsoft.rest.dto.author;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor
public class AuthorSaveDto {

    @JsonProperty("name")
    @NotEmpty @NotBlank @NotNull
    private String name;

    @JsonProperty("contact_address")
    private String contactAddress;

    @JsonProperty("email")
    private String email;

}
