package edu.infrastructure.kafka.processing.words;

import edu.infrastructure.kafka.KafkaTopic;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

class WordCountProcessorTest {

    TestInputTopic<String, String> inputTopic;
    TestOutputTopic<String, String> outputTopic;
    TopologyTestDriver topologyTestDriver;

    @BeforeEach
    void setup() {
        final StreamsBuilder streamsBuilder = new StreamsBuilder();
        final WordCountProcessor processor = new WordCountProcessor(streamsBuilder);

        processor.buildTopology();

        topologyTestDriver = new TopologyTestDriver(streamsBuilder.build(), getDriverProperties());
        inputTopic = topologyTestDriver.createInputTopic(KafkaTopic.INPUT_WORDS.getTopicName(),
                Serdes.String().serializer(), Serdes.String().serializer());
        outputTopic = topologyTestDriver.createOutputTopic(KafkaTopic.OUTPUT_WORDS_COUNT.getTopicName(),
                Serdes.String().deserializer(), Serdes.String().deserializer());
    }

    @AfterEach
    void cleanup() {
        topologyTestDriver.close();
    }

    @Test
    @DisplayName("should publish correct words counts")
    void should_publish_correct_words_counts() {

        //when

        inputTopic.pipeInput("test1");
        inputTopic.pipeInput("test1");
        inputTopic.pipeInput("test1");
        inputTopic.pipeInput("test2");
        inputTopic.pipeInput("test2");

        //then

        final List<Pair<String, String>> results = outputTopic.readRecordsToList().stream()
                .map(record -> Pair.of(record.key(), record.value()))
                .toList();

        Assertions.assertThat(results)
                .contains(Pair.of("test1", "3"), Pair.of("test2", "2"));

    }

    private Properties getDriverProperties() {
        final Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return properties;
    }

}