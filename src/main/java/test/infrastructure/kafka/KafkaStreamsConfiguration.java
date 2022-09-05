package test.infrastructure.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Map;

import static org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import static org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME;

@Configuration
@EnableKafka
@EnableKafkaStreams
class KafkaStreamsConfiguration {

    @Bean(name = DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    org.springframework.kafka.config.KafkaStreamsConfiguration kafkaStreamsConfiguration(final KafkaProperties kafkaProperties) {
        final Map<String, Object> parameters = Map.of(
                APPLICATION_ID_CONFIG, kafkaProperties.getStreams().getApplicationId(),
                BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers(),
                DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName(),
                DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName()
        );
        return new org.springframework.kafka.config.KafkaStreamsConfiguration(parameters);
    }

}