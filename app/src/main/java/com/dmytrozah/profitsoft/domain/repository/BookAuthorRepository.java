package com.dmytrozah.profitsoft.domain.repository;

import com.dmytrozah.profitsoft.domain.entity.BookAuthorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthorData, Long> {

    boolean existsByNameContainingIgnoreCase(final String name);

    Optional<BookAuthorData> findByName(String displayName);

}
