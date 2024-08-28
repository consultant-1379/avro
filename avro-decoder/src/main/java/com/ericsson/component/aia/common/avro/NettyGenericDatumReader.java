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

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.*;

import io.netty.buffer.ByteBuf;

/**
 * The Class NettyGenericDatumReader, optimized DatumReader for GenericRecords encoded using ByteBufEncoders.
 *
 * @param <D>
 *            the generic type
 */
public class NettyGenericDatumReader<D> extends GenericDatumReader<D> {
    /**
     * Default constructor.
     *
     * @param root
     *            schema reader
     */
    public NettyGenericDatumReader(final Schema root) {
        this.setSchema(root);
    }

    /**
     *
     * {@inheritDoc}. Overridden to use ByteBufResolvingDecoder as the decoder
     *
     * @param reuse
     *            the reuse
     * @param in
     *            the in
     * @return the d
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    @SuppressWarnings("unchecked")
    public D read(final D reuse, final Decoder in) throws IOException {
        final ResolvingDecoder resolver = new ByteBufResolvingDecoder(Schema.applyAliases(this.getSchema(), this.getExpected()), this.getExpected(),
                in);

        final D result = (D) this.read(reuse, this.getExpected(), resolver);
        resolver.drain();
        return result;
    }

    @Override
    protected Object readBytes(final Object old, final Decoder in) throws IOException {
        if (in instanceof ByteBufResolvingDecoder) {
            return ((ByteBufResolvingDecoder) in).readBytes(old instanceof ByteBuf ? (ByteBuf) old : null);
        } else {
            return in.readBytes(old instanceof ByteBuffer ? (ByteBuffer) old : null);
        }
    }
}