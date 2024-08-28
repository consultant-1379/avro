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
package com.ericsson.component.aia.common.avro.decoder;

import static com.ericsson.component.aia.common.avro.utils.AvroWrapperUtils.*;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.avro.encoder.GenericRecordEncoder;
import com.ericsson.component.aia.common.avro.utils.AvroWrapperUtils;

/**
 * The Class GenericRecordDecoder, for decoding avro encoded byte arrays.
 */
public class GenericRecordDecoder implements BaseDecoder<GenericRecord> {

    /** The Constant LOGGER. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(GenericRecordEncoder.class);

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 716019567389108489L;

    /** The client. */
    private final AvroClient client;

    /**
     * Instantiates a new generic record decoder.
     */
    public GenericRecordDecoder() {
        this(new DefaultAvroClient());
    }

    /**
     * Instantiates a new generic record decoder.
     *
     * @param client
     *            the Avroclient
     */
    public GenericRecordDecoder(final AvroClient client) {
        this.client = client;
    }

    /**
     * {@inheritDoc} of type GenericRecord
     *
     * @param wrappedRecord
     *            the wrapped record
     * @return the generic record
     */
    @Override
    public GenericRecord decode(final byte[] wrappedRecord) {
        final ByteBuffer byteBuffer = new ByteBuffer(wrappedRecord);
        final int wrappedRecordLength = byteBuffer.readShort();
        final int recordLength = getRecordLength(wrappedRecordLength);
        checkPayloadEncodingSchemeByte(byteBuffer.readByte());
        final long schemaIdentifier = byteBuffer.readLong();
        final Schema schema = client.lookupSchema(schemaIdentifier);

        return readGenericRecord(wrappedRecord, schema, recordLength);
    }

    /**
     * Read generic record.
     *
     * @param wrappedRecord
     *            the wrappedRecord
     * @param avroSchema
     *            the avro schema
     * @param recordLength
     *            the record length
     * @return the generic record
     */
    protected GenericRecord readGenericRecord(final byte[] wrappedRecord, final Schema avroSchema, final int recordLength) {
        GenericRecord record = null;
        try {
            final BinaryDecoder binaryDecoder = getBinaryDecoder(wrappedRecord, recordLength);
            final DatumReader<GenericRecord> reader = new GenericDatumReader<>(avroSchema);
            record = reader.read(null, binaryDecoder);
        } catch (final IOException e) {
            LOGGER.error("Exception occured, when trying to read generic record and the exception is: ", e);
        }
        return record;
    }

    /**
     * Gets the binary decoder.
     *
     * @param wrappedRecord
     *            the wrappedRecord
     * @param recordLength
     *            the record length
     * @return the BinaryDecoder
     */
    protected BinaryDecoder getBinaryDecoder(final byte[] wrappedRecord, final int recordLength) {
        return DecoderFactory.get().binaryDecoder(wrappedRecord, AvroWrapperUtils.EVENT_HEADER_BYTES, recordLength, null);
    }
}
