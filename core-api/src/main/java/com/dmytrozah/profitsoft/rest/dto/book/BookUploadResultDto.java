package com.dmytrozah.profitsoft.rest.dto.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class BookUploadResultDto {

    private int successfulUploads;

    private int failedUploads;

}
