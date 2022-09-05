package test.infrastructure.kafka.processing;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
class WordCountProcessor {

    private static final Serde<String> STRING_SERDE = Serdes.String();
    private static final String INPUT_TOPIC = "TEST_INPUT";
    private static final String OUTPUT_TOPIC = "TEST_OUTPUT";

    private final StreamsBuilder streamsBuilder;

    @PostConstruct
    void buildTopology() {
        final KStream<String, String> messageStream = streamsBuilder
                .stream(INPUT_TOPIC, Consumed.with(STRING_SERDE, STRING_SERDE));

        final KTable<String, String> wordCounts = messageStream
                .filter(((key, value) -> StringUtils.isNotEmpty(value)))
                .mapValues((ValueMapper<String, String>) String::toLowerCase)
                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .groupBy((key, word) -> word, Grouped.with(STRING_SERDE, STRING_SERDE))
                .count()
                .mapValues(String::valueOf);

        wordCounts.toStream().to(OUTPUT_TOPIC, Produced.with(STRING_SERDE, Serdes.String()));
    }

}
