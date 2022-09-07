package edu

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin
import spock.mock.DetachedMockFactory

@Configuration
class IntegrationTestConfiguration {

    private final DetachedMockFactory mockFactory = new DetachedMockFactory()

    @Bean("customKafkaAdmin")
    KafkaAdmin kafkaAdmin() {
        return mockFactory.Stub(KafkaAdmin)
    }

}