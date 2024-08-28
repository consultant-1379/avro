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

import static com.ericsson.component.aia.common.avro.utils.ByteBufUtils.*;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import com.ericsson.component.aia.common.avro.NettyGenericDatumReader;
import com.ericsson.component.aia.common.avro.netty.ByteBufDecoder;
import com.ericsson.component.aia.common.avro.utils.AvroWrapperUtils;

import io.netty.buffer.ByteBuf;

/**
 * The Class NettyGenericRecordDecoder. Should be used to decode byte arrays encoded with NettyGenericRecordEncoder.
 */
public class NettyGenericRecordDecoder extends GenericRecordDecoder {

    private static final long serialVersionUID = -7855911590092690046L;

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
    @Override
    protected GenericRecord readGenericRecord(final byte[] wrappedRecord, final Schema avroSchema, final int recordLength) {
        GenericRecord record = null;
        final ByteBuf buffer = toByteBuf(wrappedRecord, AvroWrapperUtils.EVENT_HEADER_BYTES, recordLength);

        final ByteBufDecoder byteBufDecoder = new ByteBufDecoder();
        byteBufDecoder.setBuffer(buffer);
        try {
            final NettyGenericDatumReader<GenericRecord> reader = new NettyGenericDatumReader<>(avroSchema);
            record = reader.read(null, byteBufDecoder);
        } catch (final IOException e) {
            LOGGER.error("Exception occured, when trying to read generic record and the exception is: ", e);
        } finally {
            buffer.release();
        }
        return record;
    }

}
