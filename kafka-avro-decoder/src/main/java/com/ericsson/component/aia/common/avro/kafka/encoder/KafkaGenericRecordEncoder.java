/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.common.avro.kafka.encoder;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.avro.GenericRecordWrapper;
import com.ericsson.component.aia.common.avro.encoder.GenericRecordEncoder;
import com.ericsson.component.aia.common.avro.encoder.ThreadUnSafeGenericRecordEncoder;

/**
 * The Class KafkaGenericRecordEncoder. A wrapper for a GenericRecordDecoder which will allow Kafka to use the decoder internally as a serializer
 * class. The methods configure() and close() are left unimplemented, as is the standard industry practice when implementing Kafka Serializers. The
 * serialize() method is similarly boiler plate code we must implement, even though it won't be used.
 */
public class KafkaGenericRecordEncoder implements Serializer<GenericRecordWrapper> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(KafkaGenericRecordEncoder.class);

    /** The encoder. */
    private final GenericRecordEncoder encoder;

    /**
     * Instantiates a new kafka generic record encoder.
     */
    public KafkaGenericRecordEncoder() {
        this.encoder = new ThreadUnSafeGenericRecordEncoder();
    }

    /**
     * Instantiates a new kafka generic record encoder.
     *
     * @param encoder
     *            the encoder
     */
    public KafkaGenericRecordEncoder(final GenericRecordEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * Serialize.
     *
     * @param topic
     *            the topic
     * @param data
     *            the data
     * @return the byte[]
     */
    @Override
    public byte[] serialize(final String topic, final GenericRecordWrapper data) {
        return this.encoder.encode(data);
    }

    /**
     * Configure.
     *
     * @param configs
     *            the configs
     * @param isKey
     *            the is key
     */
    @Override
    public void configure(final Map<String, ?> configs, final boolean isKey) {
        LOGGER.info("'Configure' method was invoked but it hasn't been implemented for {}", this.getClass().getSimpleName());
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        LOGGER.info("'Close' method was invoked but it hasn't been implemented for {}", this.getClass().getSimpleName());
    }
}
