package graphql.adapter.in.graphql;

import java.util.UUID;

record Book(UUID id, String title, UUID authorId) {
}
