package edu.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.springframework.kafka.support.serializer.JsonSerde;

import javax.annotation.PostConstruct;

@Slf4j
@RequiredArgsConstructor
class PersonalDataProcessor {

    private final StreamsBuilder streamsBuilder;

    @PostConstruct
    void buildTopology() {
        final JsonSerde<Person> personSerde = new JsonSerde<>(Person.class);
        final JsonSerde<Address> addressSerde = new JsonSerde<>(Address.class);
        final JsonSerde<PersonalData> personalDataSerde = new JsonSerde<>(PersonalData.class);

        final KStream<String, Person> personStream = streamsBuilder.stream(
                KafkaTopic.INPUT_PERSON.getTopicName(), Consumed.with(Serdes.String(), personSerde));
        final GlobalKTable<String, Address> addressGlobalTable = streamsBuilder.globalTable(
                KafkaTopic.INPUT_ADDRESS.getTopicName(), Materialized.with(Serdes.String(), addressSerde));

        personStream
                .join(addressGlobalTable, getPersonAddressKeyMapper(), getPersonAddressJoiner())
                .selectKey((personalDataKey, personalDataValue) -> personalDataValue.person().id())
                .to(KafkaTopic.OUTPUT_PERSONAL_DATA.getTopicName(), Produced.with(Serdes.String(), personalDataSerde));
    }

    private ValueJoiner<Person, Address, PersonalData>  getPersonAddressJoiner() {
        return PersonalData::new;
    }

    private KeyValueMapper<String, Person, String> getPersonAddressKeyMapper() {
        return (personKey, personValue) -> personValue.addressId();
    }

}
