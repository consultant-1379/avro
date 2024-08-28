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
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.Encoder;

/**
 * The Class DefaultGenericDatumWriter, overriding GenericDatumWriter to improve performance.
 *
 * @param <T>
 *            the generic type
 */
public class DefaultGenericDatumWriter<T> extends GenericDatumWriter<T> {

    /** The default value used when a BYTES field is null. */
    final ByteBuffer DEFAULT = ByteBuffer.wrap(new byte[0]);

    /**
     * Instantiates a new default generic datum writer.
     *
     * @param schema
     *            the schema
     */
    public DefaultGenericDatumWriter(final Schema schema) {
        super(schema);
    }

    /**
     * Called when record is null. Written to optimize write performance. Writes default values to record when datum object is null.
     *
     * @param schema
     *            the schema
     * @param datum
     *            the datum
     * @param out
     *            the out
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected void writeDefault(final Schema schema, final Object datum, final Encoder out) throws IOException {
        try {
            switch (schema.getType()) {
                case RECORD:
                    this.writeRecord(schema, datum, out);
                    break;
                case ENUM:
                    this.writeEnum(schema, datum, out);
                    break;
                case ARRAY:
                    this.writeArray(schema, datum, out);
                    break;
                case MAP:
                    this.writeMap(schema, datum, out);
                    break;
                case UNION:
                    final int index = this.resolveUnion(schema, datum);
                    out.writeIndex(index);
                    this.write(schema.getTypes().get(index), datum, out);
                    break;
                case FIXED:
                    this.writeFixed(schema, datum, out);
                    break;
                case STRING:
                    this.writeString(schema, "", out);
                    break;
                case BYTES:
                    this.writeBytes(this.DEFAULT, out);
                    break;
                case INT:
                    out.writeInt((0));
                    break;
                case LONG:
                    out.writeLong(0);
                    break;
                case FLOAT:
                    out.writeFloat(0);
                    break;
                case DOUBLE:
                    out.writeDouble(0);
                    break;
                case BOOLEAN:
                    out.writeBoolean(false);
                    break;
                case NULL:
                    out.writeNull();
                    break;
                default:
                    this.error(schema, datum);
            }
        } catch (final NullPointerException e) {
            throw this.npe(e, " of " + schema.getFullName());
        }
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    @Deprecated
    protected void write(final Schema schema, final Object datum, final Encoder out) throws IOException {
        if (datum == null) {
            this.writeDefault(schema, datum, out);
        } else {
            super.write(schema, datum, out);
        }

    }

    /**
     * Same error handling as super class, method error is private in superclass however so we need to re-implement it here
     *
     * @param schema
     *            the schema
     * @param datum
     *            the datum
     */
    private void error(final Schema schema, final Object datum) {
        throw new AvroTypeException("Not a " + schema + ": " + datum);
    }
}
