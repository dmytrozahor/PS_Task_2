package com.dmytrozah.profitsoft.service.impl;

import com.dmytrozah.profitsoft.domain.entity.BookAuthorData;
import com.dmytrozah.profitsoft.domain.repository.BookAuthorRepository;
import com.dmytrozah.profitsoft.domain.repository.BookRepository;
import com.dmytrozah.profitsoft.rest.dto.author.AuthorDetailsDto;
import com.dmytrozah.profitsoft.rest.dto.author.AuthorInfoDto;
import com.dmytrozah.profitsoft.rest.dto.author.AuthorQueryDto;
import com.dmytrozah.profitsoft.rest.dto.author.AuthorSaveDto;
import com.dmytrozah.profitsoft.service.BookAuthorService;
import com.dmytrozah.profitsoft.service.exception.EntityNotFoundException;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements BookAuthorService {
    private final BookAuthorRepository repository;

    private final BookRepository bookRepository;

    @Override
    public long createAuthor(final AuthorSaveDto saveDto) {
        if (repository.existsByNameContainingIgnoreCase(saveDto.getName())){
            throw new EntityExistsException("Author already exists.");
        }

        this.validateAuthor(saveDto);

        return repository.save(fromDto(saveDto)).getId();
    }

    @Override
    public void updateAuthor(final AuthorSaveDto saveDto){
        this.validateAuthor(saveDto);

        repository.save(updateFromDto(saveDto));
    }

    @Override
    public BookAuthorData resolveAuthor(String displayName) {
        return getOrThrow(displayName);
    }

    @Override
    public boolean existsAuthor(String displayName) {
        return resolveAuthor(displayName) != null;
    }

    @Override
    public AuthorInfoDto resolveAuthorInfo(long id) {
        return toInfoDto(getOrThrow(id));
    }

    @Override
    public AuthorDetailsDto resolveAuthorDetails(long id){
        return toDetailsDto(getOrThrow(id));
    }

    @Override
    public List<AuthorInfoDto> query(final AuthorQueryDto queryDto) {
        return repository.findAll(
                PageRequest.of(queryDto.getPage(), queryDto.getSize())
        ).map(this::toInfoDto).toList();
    }

    @Override
    public void delete(long id) {
        repository.delete(getOrThrow(id));
    }

    private BookAuthorData updateFromDto(AuthorSaveDto saveDto){
        BookAuthorData author = getOrThrow(saveDto.getName());
        author.setEmail(saveDto.getEmail());
        author.setContactAddress(saveDto.getContactAddress());

        return author;
    }

    private AuthorDetailsDto toDetailsDto(BookAuthorData author){
        return AuthorDetailsDto.builder()
                .id(author.getId())
                .name(author.getName())
                .email(author.getEmail())
                .contactAddress(author.getContactAddress())
                .books(bookRepository.findAllByAuthorId(author.getId()).size())
                .build();
    }

    private AuthorInfoDto toInfoDto(BookAuthorData author) {
        return AuthorInfoDto.builder()
                .id(author.getId())
                .name(author.getName())
                .build();
    }

    private BookAuthorData fromDto(AuthorSaveDto authorSaveDto) {
        final BookAuthorData authorData = new BookAuthorData();

        authorData.setName(authorSaveDto.getName());

        return authorData;
    }

    private BookAuthorData getOrThrow(String name) {
        return repository.findByName(name).orElseThrow(() ->
                new EntityNotFoundException("Author with nane %s not found".formatted(name)));
    }

    private BookAuthorData getOrThrow(long id){
        return repository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Requested author %s not found.".formatted(id))
        );
    }

    private void validateAuthor(final AuthorSaveDto dto){
        // What to validate lol
    }
}
