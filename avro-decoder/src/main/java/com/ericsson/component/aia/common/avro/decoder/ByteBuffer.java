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
package com.ericsson.component.aia.common.avro.decoder;

import java.util.Arrays;

/**
 * Convert byte[] to different data types
 */
public class ByteBuffer {
    private static final int LONG_NUMBER_OF_BYTES = 8;
    private static final int MASK = 0x00000000000000ff;
    private int index;
    private byte[] data;

    /**
     * Constructs a ByteBuffer which will convert byte[] to different data types
     *
     * @param data
     *            the byte array
     */
    public ByteBuffer(final byte[] data) {
        this.data = data;
        this.index = 0;
    }

    /**
     * read short
     *
     * @return short
     */
    public short readShort() {
        final short value = (short) ((data[index] & 0xff) << 8 | (data[index + 1] & 0xff));
        index += 2;
        return value;
    }

    /**
     * read byte
     *
     * @return byte
     */
    public byte readByte() {
        final byte value = data[index];
        index++;
        return value;
    }

    /**
     * read long
     *
     * @return long
     */
    public long readLong() {
        long paramValue = 0;
        for (int i = index; i < index + LONG_NUMBER_OF_BYTES; i++) {
            paramValue = paramValue << 8 | MASK & data[i];
        }
        index += LONG_NUMBER_OF_BYTES;
        return paramValue;
    }

    @Override
    public String toString() {
        return "ByteBuffer [index=" + index + ", data=" + Arrays.toString(data) + "]";
    }
}