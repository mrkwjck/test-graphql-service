package edu.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Profile("!test")
@Configuration
@EnableKafka
@EnableKafkaStreams
class KafkaConfiguration {

    @Bean(name = "customKafkaAdmin")
    KafkaAdmin kafkaAdmin(final KafkaProperties kafkaProperties) {
        final Map<String, Object> parameters = Map.of(
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers()
        );
        final KafkaAdmin kafkaAdmin = new KafkaAdmin(parameters);
        final List<NewTopic> topics = Stream.of(KafkaTopic.values())
                .map(this::createTopicSpecification)
                .toList();
        kafkaAdmin.createOrModifyTopics(topics.toArray(new NewTopic[]{}));
        return kafkaAdmin;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration kafkaStreamsConfiguration(final KafkaProperties kafkaProperties) {
        final Map<String, Object> parameters = Map.of(
                StreamsConfig.APPLICATION_ID_CONFIG, kafkaProperties.getStreams().getApplicationId(),
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
                StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName()
        );
        return new KafkaStreamsConfiguration(parameters);
    }

    @Bean
    PersonalDataProcessor personalDataProcessor(final StreamsBuilder streamsBuilder, final ObjectMapper objectMapper) {
        return new PersonalDataProcessor(streamsBuilder, objectMapper);
    }

    @Bean
    WordCountProcessor wordCountProcessor(final StreamsBuilder streamsBuilder) {
        return new WordCountProcessor(streamsBuilder);
    }

    private NewTopic createTopicSpecification(final KafkaTopic topic) {
        final TopicBuilder topicBuilder = TopicBuilder.name(topic.getTopicName());
        if (topic.isCompact()) {
            topicBuilder.config("delete.retention.ms", "100");
            topicBuilder.config("cleanup.policy", "compact");
            topicBuilder.config("segment.ms", "100");
            topicBuilder.config("min.cleanable.dirty.ratio", "0.001");
        }
        return topicBuilder.build();
    }

}