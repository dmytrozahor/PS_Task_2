package com.dmytrozah.profitsoft.domain.repository;

import com.dmytrozah.profitsoft.domain.entity.BookData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookData, Long> {

    List<BookData> findAllByAuthorId(Long authorId);

    BookData findByAuthorCanonicalNameAndTitle(String canonicalName, String title);

    Page<BookData> findAllByAuthorId(Long authorId, PageRequest pageable);

    boolean existsByTitleAndAuthorCanonicalName(final String title, final String authorName);

    int countByAuthorId(Long authorId);

}
