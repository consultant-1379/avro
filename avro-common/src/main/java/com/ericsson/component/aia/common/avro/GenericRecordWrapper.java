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
package com.ericsson.component.aia.common.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

/**
 * The Class GenericRecordWrapper. A wrapper class that holds a GenericRecord object and a hashId of the schema name used to create that
 * GenericRecord. This hashId can used to retrieve the schema for that GenericRecord from a schemaRegistryClient.
 */
public class GenericRecordWrapper implements GenericRecord {

    /** The hash id. */
    final long hashId;

    /** The wrapped GenericRecord. */
    final GenericRecord record;

    /**
     * Instantiates a new generic record wrapper.
     *
     * @param hashId
     *            the hash id
     * @param record
     *            the wrapped GenericRecord
     */
    public GenericRecordWrapper(final long hashId, final GenericRecord record) {
        this.hashId = hashId;
        this.record = record;
    }

    /**
     * Instantiates a new generic record wrapper.
     *
     * @param hashId
     *            the hash id
     * @param schema
     *            the schema
     */
    public GenericRecordWrapper(final long hashId, final Schema schema) {
        this.hashId = hashId;
        this.record = new GenericData.Record(schema);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final int key, final Object value) {
        this.record.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(final int key) {
        return this.record.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return this.record.getSchema();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(final String key, final Object value) {
        this.record.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(final String key) {
        return this.record.get(key);
    }

    /**
     * Gets the schema id.
     *
     * @return the schema id
     */
    public long getSchemaId() {
        return this.hashId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.record.toString();
    }

    /**
     * Gets the GenericRecord.
     *
     * @return the generic record
     */
    public GenericRecord getGenericRecord() {
        return this.record;

    }

}
