/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.common.avro.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ThreadUnsafeGenericRecordEncoder, for encoding GenericRecords as byte arrays.. BinaryEncoder is re-used every time. Not safe for
 * multi-thread access
 */
public class ThreadUnSafeGenericRecordEncoder extends GenericRecordEncoder {

    /** The Constant LOGGER. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(ThreadUnSafeGenericRecordEncoder.class);

    private static final long serialVersionUID = -3642081400471607996L;

    BinaryEncoder encoder;

    final ByteArrayOutputStream out;

    /**
     * Creates a new instance of GenericRecordEncoder
     */
    public ThreadUnSafeGenericRecordEncoder() {
        out = new ByteArrayOutputStream();
        encoder = EncoderFactory.get().directBinaryEncoder(out, encoder);
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
    @Override
    protected byte[] serializeAvroRecord(final GenericRecord avroRecord, final Schema avroSchema) {
        byte[] serializedBytes = null;

        final GenericDatumWriter<GenericRecord> avroWriter = getGenericDatumWriter(avroSchema);
        out.reset();
        encoder = EncoderFactory.get().directBinaryEncoder(out, encoder);

        try {
            avroWriter.write(avroRecord, encoder);
            encoder.flush();
            serializedBytes = out.toByteArray();
        } catch (final IOException e) {
            LOGGER.error("Exception occured, when trying to serialize avro record and the exception is: ", e);
        }

        return serializedBytes;
    }
}
