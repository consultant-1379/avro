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
package com.ericsson.component.aia.common.avro.netty;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.avro.io.Decoder;
import org.apache.avro.util.Utf8;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * The Class ByteBufDecoder. Used by NettyGenericRecordDecoder.
 */
public class ByteBufDecoder extends Decoder {

    /** The buffer. */
    private ByteBuf buffer;

    /**
     * Read null. Never called since the corresponding DatumWriter, NettyGenericDatumWriter, writes default values instead of null.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void readNull() throws IOException {
        // noop
    }

    /**
     * Read boolean.
     *
     * @return true, if successful
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public boolean readBoolean() throws IOException {
        return this.buffer.readBoolean();
    }

    /**
     * Read int.
     *
     * @return the int
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public int readInt() throws IOException {
        int n = 0;
        int b;
        int shift = 0;
        do {
            b = this.buffer.readUnsignedByte();
            if (b >= 0) {
                n |= (b & 0x7F) << shift;
                if ((b & 0x80) == 0) {
                    return (n >>> 1) ^ -(n & 1); // back to two's-complement
                }
            } else {
                throw new EOFException();
            }
            shift += 7;
        } while (shift < 32);

        throw new IOException("Invalid int encoding");
    }

    /**
     * Read long.
     *
     * @return the long
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public long readLong() throws IOException {
        long n = 0;
        int b;
        int shift = 0;
        do {
            b = this.buffer.readUnsignedByte();
            if (b >= 0) {
                n |= (b & 0x7FL) << shift;
                if ((b & 0x80) == 0) {
                    return (n >>> 1) ^ -(n & 1); // back to two's-complement
                }
            } else {
                throw new EOFException();
            }
            shift += 7;
        } while (shift < 64);
        throw new IOException("Invalid long encoding");
    }

    /**
     * Read float.
     *
     * @return the float
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public float readFloat() throws IOException {
        final int n = (this.buffer.readUnsignedByte() & 0xff) | ((this.buffer.readUnsignedByte() & 0xff) << 8)
                | ((this.buffer.readUnsignedByte() & 0xff) << 16) | ((this.buffer.readUnsignedByte() & 0xff) << 24);
        return Float.intBitsToFloat(n);
    }

    /**
     * Read double.
     *
     * @return the double
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public double readDouble() throws IOException {
        final long n = (((long) this.buffer.readUnsignedByte()) & 0xff) | ((((long) this.buffer.readUnsignedByte()) & 0xff) << 8)
                | ((((long) this.buffer.readUnsignedByte()) & 0xff) << 16) | ((((long) this.buffer.readUnsignedByte()) & 0xff) << 24)
                | ((((long) this.buffer.readUnsignedByte()) & 0xff) << 32) | ((((long) this.buffer.readUnsignedByte()) & 0xff) << 40)
                | ((((long) this.buffer.readUnsignedByte()) & 0xff) << 48) | ((((long) this.buffer.readUnsignedByte()) & 0xff) << 56);
        return Double.longBitsToDouble(n);
    }

    /**
     * Read string.
     *
     * @param old
     *            the old
     * @return the utf 8
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public Utf8 readString(final Utf8 old) throws IOException {
        final int length = this.readInt();
        final Utf8 result = old != null ? old : new Utf8();
        result.setByteLength(length);
        if (0 != length) {
            this.buffer.readBytes(result.getBytes());
        }
        return result;
    }

    /**
     * Read string.
     *
     * @return the string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public String readString() throws IOException {
        return this.readString(null).toString();
    }

    /**
     * Skip string.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void skipString() throws IOException {
        this.doSkipBytes(this.readInt());
    }

    /**
     * Read bytes.
     *
     * @param old
     *            the old
     * @return the byte buffer
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public ByteBuffer readBytes(final ByteBuffer old) throws IOException {
        final int length = this.readInt();
        final ByteBuffer result;

        if (old != null && length <= old.capacity()) {
            result = old;
            result.clear();
        } else {
            // not sure how to deal with this!
            result = ByteBuffer.allocate(length);
        }

        if (length > 0) {
            this.buffer.readBytes(result);
        }
        result.limit(length);
        result.flip();

        return result;
    }

    /**
     * Read bytes.
     *
     * @param old
     *            {@link ByteBuf} to be reused
     * @return {@link ByteBuf}
     * @throws IOException
     *             in case of error
     * @see {link {@link #readBytes(ByteBuffer)}
     */
    public ByteBuf readBytes(final ByteBuf old) throws IOException {
        final int length = this.readInt();

        final ByteBuf result;
        if (old != null) {
            result = old;
            result.clear();
            if (result.capacity() != length) {
                result.capacity(length);
            }
        } else {
            result = ByteBufAllocator.DEFAULT.buffer(length);
        }

        if (length > 0) {
            this.buffer.readBytes(result);
        }

        return result;
    }

    /**
     * Skip bytes.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void skipBytes() throws IOException {
        this.doSkipBytes(this.readInt());
    }

    /**
     * Read fixed.
     *
     * @param bytes
     *            the bytes
     * @param start
     *            the start
     * @param length
     *            the length
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void readFixed(final byte[] bytes, final int start, final int length) throws IOException {
        this.buffer.readBytes(bytes, start, length);
    }

    /**
     * Skip fixed.
     *
     * @param length
     *            the length
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public void skipFixed(final int length) throws IOException {
        this.doSkipBytes(length);
    }

    /**
     * Read enum.
     *
     * @return the int
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public int readEnum() throws IOException {
        return this.readInt();
    }

    /**
     * Read array start.
     *
     * @return the long
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public long readArrayStart() throws IOException {
        return this.doReadItemCount();
    }

    /**
     * Array next.
     *
     * @return the long
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public long arrayNext() throws IOException {
        return this.doReadItemCount();
    }

    /**
     * Skip array.
     *
     * @return the long
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public long skipArray() throws IOException {
        return this.doSkipItems();
    }

    /**
     * Read map start.
     *
     * @return the long
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public long readMapStart() throws IOException {
        return this.doReadItemCount();
    }

    /**
     * Map next.
     *
     * @return the long
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public long mapNext() throws IOException {
        return this.doReadItemCount();
    }

    /**
     * Skip map.
     *
     * @return the long
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public long skipMap() throws IOException {
        return this.doSkipItems();
    }

    /**
     * Read index.
     *
     * @return the int
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    public int readIndex() throws IOException {
        return this.readInt();
    }

    /**
     * Do skip bytes.
     *
     * @param length
     *            the length
     */
    private void doSkipBytes(final int length) {
        final int readerIndex = this.buffer.readerIndex();
        this.buffer.readerIndex(readerIndex + length);
    }

    /**
     * Do read item count.
     *
     * @return the long
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private long doReadItemCount() throws IOException {
        long result = this.readLong();
        if (result < 0) {
            this.readLong(); // Consume byte-count if present
            result = -result;
        }
        return result;
    }

    /**
     * Do skip items.
     *
     * @return the long
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private long doSkipItems() throws IOException {
        int result = this.readInt();
        while (result < 0) {
            final long bytecount = this.readLong();
            this.doSkipBytes((int) bytecount);
            result = this.readInt();
        }
        return result;
    }

    /**
     * Gets the buffer.
     *
     * @return the buffer
     */
    public ByteBuf getBuffer() {
        return this.buffer;
    }

    /**
     * Sets the buffer.
     *
     * @param buffer
     *            the new buffer
     */
    public void setBuffer(final ByteBuf buffer) {
        this.buffer = buffer;
    }

}