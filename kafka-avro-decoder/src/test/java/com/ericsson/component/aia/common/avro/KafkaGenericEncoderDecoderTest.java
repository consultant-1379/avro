package com.ericsson.component.aia.common.avro;

import static com.ericsson.component.aia.common.avro.utils.ByteBufUtils.toByteBuf;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.avro.encoder.GenericRecordEncoder;
import com.ericsson.component.aia.common.avro.kafka.decoder.KafkaGenericRecordDecoder;
import com.ericsson.component.aia.common.avro.kafka.decoder.KafkaNettyGenericRecordDecoder;
import com.ericsson.component.aia.common.avro.kafka.encoder.KafkaGenericRecordEncoder;
import com.ericsson.component.aia.common.avro.kafka.encoder.KafkaNettyGenericRecordEncoder;
import com.ericsson.component.aia.model.registry.client.SchemaRegistryClient;
import com.ericsson.component.aia.model.registry.exception.SchemaRetrievalException;
import com.ericsson.component.aia.model.registry.impl.SchemaRegistryClientFactory;

public class KafkaGenericEncoderDecoderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaGenericEncoderDecoderTest.class);

    private static final int TIMESTAMP_HOUR_VALUE = 2;
    private static final long TIMESTAMP_VALUE = 12345L;
    private static final String NE_VALUE = "sampleNE";
    private static final String TIMESTAMP_HOUR = "TIMESTAMP_HOUR";
    private static final String TIMESTAMP = "_TIMESTAMP";
    private static final String NE = "_NE";
    private static final String RANDOM_TOPIC = "random";
    private static final byte AVRO_ENCODING_SCHEME_BYTE = 2;
    private static final long EXPECTED_SCHEMA_HASH_ID = -5397233366664110056L;
    private static final String SAMPLE_SCHEMA_FILE_PATH = "src/test/resources/avro/some.namespace.value/SAMPLE_SCHEMA.avsc";
    private final GenericRecordWrapper wrapper = createGenericRecordWrapper(SAMPLE_SCHEMA_FILE_PATH);
    private static SchemaRegistryClient SCHEMA_REGISTRY_CLIENT;
    private final GenericRecordEncoder genericRecordEncoder = new GenericRecordEncoder();

    @BeforeClass
    public static void before() {
        System.setProperty("schemaRegistry.address", "src/test/resources/avro/");
        SCHEMA_REGISTRY_CLIENT = SchemaRegistryClientFactory.newSchemaRegistryClientInstance();
    }

    @Test
    public void testKafkaGenericRecordEncodingDecoding() {
        try (final KafkaGenericRecordEncoder encoder = new KafkaGenericRecordEncoder();
                final KafkaGenericRecordDecoder decoder = new KafkaGenericRecordDecoder();) {

            final byte[] encodedRecord = genericRecordEncoder.encode(wrapper);
            assertArrayEquals(encodedRecord, encoder.serialize(RANDOM_TOPIC, wrapper));
            assertByteArrayIsCorrect(encodedRecord);

            final GenericRecord record = decoder.fromBytes(encodedRecord);
            assertEquals(record, decoder.deserialize(RANDOM_TOPIC, encodedRecord));
            assertRecordIsCorrect(record);
        }
    }

    @Test
    public void testKafkaNettyGenericRecordEncodingDecoding() {
        try (final KafkaGenericRecordEncoder encoder = new KafkaNettyGenericRecordEncoder();
                final KafkaGenericRecordDecoder decoder = new KafkaNettyGenericRecordDecoder();) {

            final byte[] encodedRecord = genericRecordEncoder.encode(wrapper);
            assertArrayEquals(encodedRecord, encoder.serialize(RANDOM_TOPIC, wrapper));
            assertByteArrayIsCorrect(encodedRecord);

            final GenericRecord record = decoder.fromBytes(encodedRecord);
            assertEquals(record, decoder.deserialize(RANDOM_TOPIC, encodedRecord));
            assertRecordIsCorrect(record);
        }
    }

    private GenericRecordWrapper createGenericRecordWrapper(final String schemaPath) {
        final Schema schema = getAvroSchema(schemaPath);
        long hashId = 0;
        try {
            hashId = SCHEMA_REGISTRY_CLIENT.lookup(schema.getFullName()).getSchemaId();
        } catch (final SchemaRetrievalException e) {
            LOGGER.error("Got exception while trying to do a Schema lookup", e);
            fail();
        }
        final GenericRecord record = new GenericData.Record(schema);
        record.put(NE, NE_VALUE);
        record.put(TIMESTAMP, TIMESTAMP_VALUE);
        record.put(TIMESTAMP_HOUR, TIMESTAMP_HOUR_VALUE);
        return new GenericRecordWrapper(hashId, record);
    }

    private void assertByteArrayIsCorrect(final byte[] encodedRecord) {
        assertNotNull(encodedRecord);
        final ByteBuf buffer = toByteBuf(encodedRecord);
        assertEquals("Record length field value does not match size of record", encodedRecord.length, buffer.readShort());
        assertEquals("Payload Decoding Scheme value does not match avro id value", AVRO_ENCODING_SCHEME_BYTE, buffer.readByte());
        assertEquals("Schema hash id does not match expected value", EXPECTED_SCHEMA_HASH_ID, buffer.readLong());
    }

    private Schema getAvroSchema(final String schemaPath) {
        Schema schema = null;
        try {
            schema = new Schema.Parser().parse(new File(schemaPath));
        } catch (final IOException e) {
            LOGGER.error("Schema can't be parsed", e);
        }
        return schema;
    }

    private void assertRecordIsCorrect(final GenericRecord record) {
        assertNotNull(record);
        assertEquals(NE_VALUE, record.get(NE).toString());
        assertEquals(TIMESTAMP_VALUE, record.get(TIMESTAMP));
        assertEquals(TIMESTAMP_HOUR_VALUE, record.get(TIMESTAMP_HOUR));
    }

}
