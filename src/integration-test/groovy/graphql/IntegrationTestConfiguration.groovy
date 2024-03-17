package graphql


import org.springframework.context.annotation.Configuration
import spock.mock.DetachedMockFactory

@Configuration
class IntegrationTestConfiguration {

    private final DetachedMockFactory mockFactory = new DetachedMockFactory()

}