package com.dmytrozah.profitsoft.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "book_author_data")
@RequiredArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class BookAuthorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String name;

    private String contactAddress;

    private String email;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookData> books;

}
