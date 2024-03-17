package graphql.adapter.in.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
class BooksController {

    @QueryMapping
    public Book bookByTitle(@Argument final String title) {
        return Library.getBookByTitle(title);
    }

    @SchemaMapping
    public Author author(Book book) {
        return Library.getAuthorById(book.authorId());
    }

}
