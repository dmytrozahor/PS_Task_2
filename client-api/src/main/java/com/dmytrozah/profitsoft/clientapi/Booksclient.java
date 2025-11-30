package com.dmytrozah.profitsoft.clientapi;

import com.dmytrozah.profitsoft.rest.dto.RestResponse;
import com.dmytrozah.profitsoft.rest.dto.book.BookSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class Booksclient {

    public static final String ENDPOINT_BOOKS_CREATE = "/books";

    private final RestTemplate restTemplate;

    @Value("${booksService.baseUri:https://localhost:8080/api}")
    private String booksBaseUri;

    public int createBook(BookSaveDto saveDto){
        ResponseEntity<RestResponse> response = restTemplate.exchange(
                booksBaseUri + ENDPOINT_BOOKS_CREATE,
                HttpMethod.POST,
                new HttpEntity<>(saveDto),
                ParameterizedTypeReference.forType(RestResponse.class)
        );

        String id = response.getBody().getMessage();

        return Integer.parseInt(id);
    }

}
