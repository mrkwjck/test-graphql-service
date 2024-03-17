package graphql.adapter.in.graphql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Library {

    private static final UUID AUTHOR_1_ID = UUID.randomUUID();
    private static final UUID AUTHOR_2_ID = UUID.randomUUID();
    private static final UUID AUTHOR_3_ID = UUID.randomUUID();
    private static final List<Author> AUTHORS = List.of(
            new Author(AUTHOR_1_ID, "Stephen", "King"),
            new Author(AUTHOR_2_ID, "Maria", "Curie"),
            new Author(AUTHOR_3_ID, "Julian", "Tuwim")
    );
    private static final List<Book> BOOKS = List.of(
            new Book(UUID.randomUUID(), "Title 1", AUTHOR_1_ID),
            new Book(UUID.randomUUID(), "Title 2", AUTHOR_2_ID),
            new Book(UUID.randomUUID(), "Title 3", AUTHOR_3_ID)
    );

    public static Book getBookByTitle(final String title) {
        return BOOKS.stream()
                .filter(book -> StringUtils.equals(book.title(), title))
                .findAny()
                .orElse(null);
    }

    public static Author getAuthorById(final UUID authorId) {
        return AUTHORS.stream()
                .filter(author -> author.id().equals(authorId))
                .findAny()
                .orElse(null);
    }

}
