package com.dmytrozah.profitsoft.domain.dto.author;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class AuthorSaveDto {

    @JsonProperty("name")
    @NotNull
    private AuthorNameDto name;

    @JsonProperty("address")
    private AuthorAddressDto address;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

}
