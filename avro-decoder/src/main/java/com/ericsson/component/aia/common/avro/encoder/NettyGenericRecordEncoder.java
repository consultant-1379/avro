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

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import com.ericsson.component.aia.common.avro.NettyGenericDatumWriter;
import com.ericsson.component.aia.common.avro.netty.ByteBufEncoder;
import com.ericsson.component.aia.common.avro.utils.ByteBufUtils;

import io.netty.buffer.ByteBuf;

/**
 * The Class NettyGenericRecordEncoder. Encodes GenericRecords using custom NettyGenericRecordEncoders
 */
public class NettyGenericRecordEncoder extends GenericRecordEncoder {

    private static final long serialVersionUID = 3061603101739527727L;

    /**
     * Transform avro record into byte array.
     *
     * @param avroRecord
     *            the avro record
     * @param avroSchema
     *            the avro schema
     * @return the byte[]
     */
    @Override
    protected byte[] serializeAvroRecord(final GenericRecord avroRecord, final Schema avroSchema) {

        final NettyGenericDatumWriter<GenericRecord> avroWriter = new NettyGenericDatumWriter<>(avroSchema);
        final ByteBuf buffer = ByteBufUtils.allocate();
        final ByteBufEncoder byteBufEncoder = new ByteBufEncoder();
        byteBufEncoder.setBuffer(buffer);
        try {
            avroWriter.write(avroRecord, byteBufEncoder);
            byteBufEncoder.flush();
        } catch (final IOException e) {
            LOGGER.error("Exception occured, when trying to transform avro record into byte array and the exception is: ", e);
        }
        return ByteBufUtils.toArray(buffer);
    }

}
