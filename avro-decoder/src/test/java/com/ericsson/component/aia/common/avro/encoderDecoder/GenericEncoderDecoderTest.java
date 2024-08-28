package com.ericsson.component.aia.common.avro.encoderDecoder;

import static com.ericsson.component.aia.common.avro.utils.ByteBufUtils.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.component.aia.common.avro.GenericRecordWrapper;
import com.ericsson.component.aia.common.avro.decoder.*;
import com.ericsson.component.aia.common.avro.encoder.*;
import com.ericsson.component.aia.common.avro.utils.ByteBufUtils;
import com.ericsson.component.aia.model.registry.client.SchemaRegistryClient;
import com.ericsson.component.aia.model.registry.exception.SchemaRetrievalException;
import com.ericsson.component.aia.model.registry.impl.SchemaRegistryClientFactory;

import io.netty.buffer.ByteBuf;

public class GenericEncoderDecoderTest {
    private static final String SOME_BYTES = "SOME_BYTES";
    private static final String SOME_STRING = "SOME_STRING";
    private static final String SOME_INT = "SOME_INT";
    private static final String SOME_FLOAT = "SOME_FLOAT";
    private static final String SOME_DOUBLE = "SOME_DOUBLE";
    private static final String SOME_LONG = "SOME_LONG";
    private static final String SOME_BOOLEAN = "SOME_BOOLEAN";
    private static final byte AVRO_ENCODING_SCHEME_BYTE = 2;
    private static final long EXPECTED_SCHEMA_HASH_ID = -5397233366664110056L;
    private static final int TIMESTAMP_HOUR_VALUE = 2;
    private static final long TIMESTAMP_VALUE = 12345L;
    private static final String NE_VALUE = "sampleNE";
    private static final String TIMESTAMP_HOUR = "TIMESTAMP_HOUR";
    private static final String TIMESTAMP = "_TIMESTAMP";
    private static final String NE = "_NE";
    private static final String SAMPLE_SCHEMA_FILE_PATH = "src/test/resources/avro/some.namespace.value/SAMPLE_SCHEMA.avsc";
    private static SchemaRegistryClient SCHEMA_REGISTRY_CLIENT;
    private final GenericRecordWrapper wrapper = createGenericRecordWrapper(SAMPLE_SCHEMA_FILE_PATH);

    @BeforeClass
    public static void before() {
        System.setProperty("schemaRegistry.address", "src/test/resources/avro/");
        SCHEMA_REGISTRY_CLIENT = SchemaRegistryClientFactory.newSchemaRegistryClientInstance();
    }

    @Test
    public void testGenericRecordEncodingDecoding() {
        final GenericRecordEncoder encoder = new GenericRecordEncoder();
        final byte[] encodedRecord = encoder.encode(wrapper);
        assertByteArrayIsCorrect(encodedRecord);

        final GenericRecordDecoder decoder = new GenericRecordDecoder();
        final GenericRecord record = decoder.decode(encodedRecord);
        assertRecordIsCorrect(record);
    }

    @Test
    public void testReuseGenericRecordEncodingDecoding() {
        final GenericRecordEncoder encoder = new ThreadUnSafeGenericRecordEncoder();
        final byte[] encodedRecord = encoder.encode(wrapper);
        assertByteArrayIsCorrect(encodedRecord);

        final GenericRecordDecoder decoder = new ThreadUnSafeGenericRecordDecoder();
        final GenericRecord record = decoder.decode(encodedRecord);
        assertRecordIsCorrect(record);
    }

    @Test
    public void testNettyGenericRecordEncodingDecoding() {
        final GenericRecordEncoder encoder = new NettyGenericRecordEncoder();
        final byte[] encodedRecord = encoder.encode(wrapper);
        assertByteArrayIsCorrect(encodedRecord);

        final GenericRecordDecoder decoder = new NettyGenericRecordDecoder();
        final GenericRecord record = decoder.decode(encodedRecord);
        assertRecordIsCorrect(record);
    }

    private GenericRecordWrapper createGenericRecordWrapper(final String schemaPath) {
        final Schema schema = getAvroSchema(schemaPath);
        long hashId = 0;
        try {
            hashId = SCHEMA_REGISTRY_CLIENT.lookup(schema.getFullName()).getSchemaId();
        } catch (final SchemaRetrievalException e) {
            fail("Got exception while trying to do a Schema lookup for " + schema.getFullName() + " and exception is " + e.getMessage());
        }
        final GenericRecord record = new GenericData.Record(schema);
        record.put(NE, NE_VALUE);
        record.put(TIMESTAMP, TIMESTAMP_VALUE);
        record.put(TIMESTAMP_HOUR, TIMESTAMP_HOUR_VALUE);
        record.put(SOME_BYTES, ByteBuffer.wrap(new byte[] { 1, 2 }));
        record.put(SOME_STRING, "Hello, World!");
        record.put(SOME_INT, Integer.MAX_VALUE);
        record.put(SOME_FLOAT, Float.MAX_VALUE);
        record.put(SOME_DOUBLE, Double.MAX_VALUE);
        record.put(SOME_LONG, Long.MAX_VALUE);
        record.put(SOME_BOOLEAN, true);

        return new GenericRecordWrapper(hashId, record);
    }

    private Schema getAvroSchema(final String schemaPath) {
        Schema schema = null;
        try {
            schema = new Schema.Parser().parse(new File(schemaPath));
        } catch (final IOException e) {
            fail("Schema can't be parsed from the file " + new File(schemaPath).getAbsolutePath());
        }
        return schema;
    }

    private void assertRecordIsCorrect(final GenericRecord record) {
        assertNotNull(record);
        assertEquals(NE_VALUE, record.get(NE).toString());
        assertEquals(TIMESTAMP_VALUE, record.get(TIMESTAMP));
        assertEquals(TIMESTAMP_HOUR_VALUE, record.get(TIMESTAMP_HOUR));
        assertEquals(Integer.MAX_VALUE, record.get(SOME_INT));
        assertEquals(Float.MAX_VALUE, record.get(SOME_FLOAT));
        assertEquals(Double.MAX_VALUE, record.get(SOME_DOUBLE));
        assertEquals(Long.MAX_VALUE, record.get(SOME_LONG));
        assertEquals(true, record.get(SOME_BOOLEAN));
        assertArrayEquals(new byte[] { 1, 2 }, getBytesFromBuffer(record.get(SOME_BYTES)));
        assertEquals("Hello, World!", record.get(SOME_STRING).toString());
    }

    private void assertByteArrayIsCorrect(final byte[] encodedRecord) {
        assertNotNull(encodedRecord);
        final ByteBuf buffer = toByteBuf(encodedRecord);
        assertEquals("Record length field value does not match size of record", encodedRecord.length, buffer.readShort());
        assertEquals("Payload Decoding Scheme value does not match avro id value", AVRO_ENCODING_SCHEME_BYTE, buffer.readByte());
        assertEquals("Schema hash id does not match expected value", EXPECTED_SCHEMA_HASH_ID, buffer.readLong());
    }

    private byte[] getBytesFromBuffer(final Object buffer) {
        if (buffer instanceof ByteBuf) {
            return ByteBufUtils.toArray((ByteBuf) buffer);
        } else if (buffer instanceof ByteBuffer) {
            return ((ByteBuffer) buffer).array();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
