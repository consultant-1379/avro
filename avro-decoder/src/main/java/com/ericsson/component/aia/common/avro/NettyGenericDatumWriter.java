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

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;

import com.ericsson.component.aia.common.avro.netty.ByteBufEncoder;

import io.netty.buffer.ByteBuf;

/**
 * The Class NettyGenericDatumWriter, optimized DatumWriter for GenericRecords encoded using ByteBufEncoders.
 *
 * @param <D>
 *            the generic type
 */
public class NettyGenericDatumWriter<D> extends DefaultGenericDatumWriter<D> {

    /** The default. */
    final ByteBuffer DEFAULT = ByteBuffer.wrap(new byte[0]);

    /**
     * Default constructor.
     *
     * @param schema
     *            schema
     */
    public NettyGenericDatumWriter(final Schema schema) {
        super(schema);
    }

    /**
     * Write bytes.
     *
     * @param datum
     *            the datum
     * @param out
     *            the out
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    protected void writeBytes(final Object datum, final Encoder out) throws IOException {
        if (datum instanceof ByteBuffer) {
            out.writeBytes((ByteBuffer) datum);
        } else if (datum instanceof ByteBuf && out instanceof ByteBufEncoder) {
            ((ByteBufEncoder) out).writeBytes((ByteBuf) datum);
        } else {
            throw new AvroTypeException("can't write bytes! type: " + datum.getClass().getCanonicalName());
        }
    }
}
