package com.ericsson.component.aia.common.avro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.component.aia.model.registry.client.SchemaRegistryClient;
import com.ericsson.component.aia.model.registry.exception.SchemaRetrievalException;

public class GenericRecordWrapperTest {
    private static final String NE_VALUE = "sampleNE";
    private static final String NE = "_NE";
    private static final String EVENT_FILE_PATH = "src/test/resources/avro/celltrace.s.ab11/INTERNAL_EVENT_ADMISSION_BLOCKING_STARTED.avsc";

    @BeforeClass
    public static void before() {
        System.setProperty("schemaRegistry.address", "src/test/resources/avro/");
    }

    @Test
    public void testGenericRecordWrapper() {
        final GenericRecord genericRecord = createGenericRecord();
        final GenericRecordWrapper genericRecordWrapper = new GenericRecordWrapper(getHashId(genericRecord), genericRecord);
        assertGenericWrapperMatchesGenericRecord(genericRecord, genericRecordWrapper);
    }

    /**
     * @param genericRecord
     * @param genericRecordWrapper
     */
    private void assertGenericWrapperMatchesGenericRecord(final GenericRecord genericRecord, final GenericRecordWrapper genericRecordWrapper) {
        assertEquals(genericRecord.get(NE), genericRecordWrapper.get(NE));
        assertEquals(genericRecord.getSchema(), genericRecordWrapper.getSchema());
        assertEquals(getHashId(genericRecord), genericRecordWrapper.getSchemaId());
        assertEquals(genericRecord.toString(), genericRecordWrapper.getGenericRecord().toString());
        assertEquals(genericRecordWrapper.toString(), createGenericRecordWrapper(genericRecord).toString());
    }

    private GenericRecordWrapper createGenericRecordWrapper(final GenericRecord record) {
        final GenericRecordWrapper wrapper = new GenericRecordWrapper(getHashId(record), record.getSchema());
        populateGenericRecord(wrapper);
        return wrapper;
    }

    /**
     * @param record
     * @return
     */
    private long getHashId(final GenericRecord record) {
        long hashId = 0;
        try {
            hashId = SchemaRegistryClient.INSTANCE.lookup(record.getSchema().getFullName()).getSchemaId();
        } catch (final SchemaRetrievalException e) {
            fail("Got exception while trying to do a Schema lookup for " + record.getSchema().getFullName() + " and exception is " + e.getMessage());
        }
        return hashId;
    }

    private GenericRecord createGenericRecord() {
        final Schema schema = getAvroSchema();
        final GenericRecord record = new GenericData.Record(schema);
        populateGenericRecord(record);
        return record;
    }

    private Schema getAvroSchema() {
        Schema schema = null;
        try {
            schema = new Schema.Parser().parse(new File(EVENT_FILE_PATH));
        } catch (final IOException e) {
            fail("Schema can't be parsed from the file " + new File(EVENT_FILE_PATH).getAbsolutePath());
        }
        return schema;
    }

    /**
     * @param record
     */
    private void populateGenericRecord(final GenericRecord record) {
        record.put(NE, NE_VALUE);
    }

}
