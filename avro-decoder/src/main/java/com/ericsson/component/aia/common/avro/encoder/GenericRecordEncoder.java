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
package com.ericsson.component.aia.common.avro.encoder;

import static com.ericsson.component.aia.common.avro.utils.AvroWrapperUtils.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.avro.DefaultGenericDatumWriter;
import com.ericsson.component.aia.common.avro.GenericRecordWrapper;

/**
 * The Class GenericRecordEncoder, for encoding GenericRecords as byte arrays.
 */
public class GenericRecordEncoder implements BaseEncoder<GenericRecordWrapper> {

    /** The Constant LOGGER. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(GenericRecordEncoder.class);

    private static final long serialVersionUID = 37269435053178826L;

    /**
     * {@inheritDoc} of type GenericRecordWrapper to byte array
     *
     * @param record
     *            the record
     * @return the byte[]
     */
    @Override
    public byte[] encode(final GenericRecordWrapper record) {
        final byte[] encodedRecord = this.serializeAvroRecord(record.getGenericRecord(), record.getSchema());
        final byte[] encodedAvroWrappedRecord = prependExtraBytes(encodedRecord, record.getSchemaId());
        return encodedAvroWrappedRecord;
    }

    /**
     * Serialize avro record.
     *
     * @param avroRecord
     *            the avro record
     * @param avroSchema
     *            the avro schema
     * @return the byte[]
     */
    protected byte[] serializeAvroRecord(final GenericRecord avroRecord, final Schema avroSchema) {
        byte[] serializedBytes = null;

        final GenericDatumWriter<GenericRecord> avroWriter = getGenericDatumWriter(avroSchema);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BinaryEncoder encoder = EncoderFactory.get().directBinaryEncoder(out, null);

        try {
            avroWriter.write(avroRecord, encoder);
            encoder.flush();
            serializedBytes = out.toByteArray();
        } catch (final IOException e) {
            LOGGER.error("Exception occured, when trying to serialize avro record and the exception is: ", e);
        } finally {
            try {
                out.close();
            } catch (final IOException e) {
                LOGGER.error("Exception occured, when trying to close ByteArrayOutputStream and the exception is: ", e);
            }
        }

        return serializedBytes;
    }

    /**
     * Gets a specific GenericDatumWriter.
     *
     * @param avroSchema
     *            the schema
     * @return the GenericDatumWriter
     */
    protected GenericDatumWriter<GenericRecord> getGenericDatumWriter(final Schema avroSchema) {
        return new DefaultGenericDatumWriter<>(avroSchema);
    }

}
