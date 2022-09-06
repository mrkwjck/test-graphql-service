package edu.infrastructure.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KafkaTopic {

    INPUT_WORDS("WORDS_INPUT", false),
    OUTPUT_WORDS_COUNT("WORDS_COUNT_OUTPUT", true),
    INPUT_PERSON("PERSONS_INPUT", false),
    INPUT_ADDRESS("ADDRESSES_INPUT", false),
    OUTPUT_PERSONAL_DATA("PERSONAL_DATA_OUTPUT", true);

    private final String topicName;
    private final boolean compact;

}
