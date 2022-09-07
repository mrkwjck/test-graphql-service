package edu.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

class PersonalDataProcessorTest {

    private TopologyTestDriver topologyTestDriver;
    private TestInputTopic<String, String> personTopic;
    private TestInputTopic<String, String> addressTopic;
    private TestOutputTopic<String, String> personalDataTopic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        final StreamsBuilder streamsBuilder = new StreamsBuilder();
        final PersonalDataProcessor processor = new PersonalDataProcessor(streamsBuilder);

        processor.buildTopology();

        topologyTestDriver = new TopologyTestDriver(streamsBuilder.build(), getDriverProperties());
        personTopic = topologyTestDriver.createInputTopic(KafkaTopic.INPUT_PERSON.getTopicName(),
                Serdes.String().serializer(), Serdes.String().serializer());
        addressTopic = topologyTestDriver.createInputTopic(KafkaTopic.INPUT_ADDRESS.getTopicName(),
                Serdes.String().serializer(), Serdes.String().serializer());
        personalDataTopic = topologyTestDriver.createOutputTopic(KafkaTopic.OUTPUT_PERSONAL_DATA.getTopicName(),
                Serdes.String().deserializer(), Serdes.String().deserializer());
    }

    @AfterEach
    void cleanup() {
        topologyTestDriver.close();
    }

    @Test
    void should_join_person_with_address() {

        //when

        addressTopic.pipeInput("adr1", parseObject(address1()));
        addressTopic.pipeInput("adr2", parseObject(address2()));
        personTopic.pipeInput("per1", parseObject(person1()));
        personTopic.pipeInput("per2", parseObject(person2()));
        personTopic.pipeInput("per3", parseObject(person3()));

        //then

        final List<Pair<String, String>> results = personalDataTopic.readRecordsToList().stream()
                .map(record -> Pair.of(record.key(), record.value()))
                .toList();

        Assertions.assertThat(results)
                .hasSize(2)
                .contains(Pair.of("per1", parseObject(personalData1())), Pair.of("per2", parseObject(personalData2())));

    }

    private Properties getDriverProperties() {
        final Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return properties;
    }

    @SneakyThrows
    private String parseObject(final Object objectClass) {
        return objectMapper.writeValueAsString(objectClass);
    }

    private static Person person1() {
        return new Person("per1", "John", "Doe", "adr1");
    }

    private static Person person2() {
        return new Person("per2", "Jane", "Doe", "adr2");
    }

    private static Person person3() {
        return new Person("per3", "Maria", "Doe", "adr3");
    }

    private static Address address1() {
        return new Address(
                "adr1",
                "Test1",
                "1",
                "",
                "London",
                "012340",
                "United Kingdom");
    }

    private static Address address2() {
        return new Address(
                "adr2",
                "Test2",
                "1",
                "3",
                "New York",
                "012340",
                "United States of America");
    }

    private static PersonalData personalData1() {
        return new PersonalData(person1(), address1());
    }

    private static PersonalData personalData2() {
        return new PersonalData(person2(), address2());
    }

}