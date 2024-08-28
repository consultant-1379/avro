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
package com.ericsson.component.aia.common.avro.utils;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.*;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;

/**
 * Helper class for using ByteBufs.
 */
public class ByteBufUtils {

    /** The logger. */
    private static Logger logger = LoggerFactory.getLogger(ByteBufUtil.class);

    /** The Constant DEFAULT. */
    private static final ByteBufAllocator DEFAULT;

    static {

        String allocType = SystemPropertyUtil.get("io.netty.allocator.type", PlatformDependent.isAndroid() ? "unpooled" : "pooled");
        allocType = allocType.toLowerCase(Locale.US).trim();

        final ByteBufAllocator alloc;
        if ("unpooled".equals(allocType)) {
            alloc = UnpooledByteBufAllocator.DEFAULT;
            logger.debug("-Dio.netty.allocator.type: {}", allocType);
        } else if ("pooled".equals(allocType)) {
            alloc = PooledByteBufAllocator.DEFAULT;
            logger.debug("-Dio.netty.allocator.type: {}", allocType);
        } else {
            alloc = PooledByteBufAllocator.DEFAULT;
            logger.debug("-Dio.netty.allocator.type: pooled (unknown: {})", allocType);
        }
        DEFAULT = alloc;
    }

    /**
     * Instantiates a new byte buf utils.
     */
    private ByteBufUtils() {
    }

    /**
     * Allocates a new ByteBuf with a specified size.
     *
     * @param size
     *            of array to allocate
     * @return a ByteBuf
     */
    public static ByteBuf allocate(final int size) {
        return DEFAULT.buffer(size);
    }

    /**
     * Allocates a new ByteBuf.
     *
     * @return a ByteBuf
     */
    public static ByteBuf allocate() {
        return DEFAULT.buffer();
    }

    /**
     * some buffer implementations are not backed by byte[] thus we have to check if there is buffer {@code ByteBuf#hasArray()} before we can call
     * {@code ByteBuf#array()}.
     *
     * @param buffer
     *            to convert to byte[]
     * @return bytes
     */
    public static byte[] toArray(final ByteBuf buffer) {
        final byte[] bytes = buffer.hasArray() ? buffer.array() : readIntoNewByteArray(buffer);
        buffer.release();
        return bytes;
    }

    /**
     * if buffer isn't backed by byte[], create a new one and read buffered bytes into it.
     *
     * @param buffer
     *            the buffer
     * @return bytes
     */
    private static byte[] readIntoNewByteArray(final ByteBuf buffer) {
        final byte[] result = new byte[buffer.readableBytes()];
        buffer.readBytes(result);
        return result;
    }

    /**
     * convert byte[] to ByteBuf.
     *
     * @param encodedRecord
     *            byte[] to be converted to ByteBuf
     * @return buffered byte array
     */
    public static ByteBuf toByteBuf(final byte[] encodedRecord) {
        return ByteBufUtils.allocate(encodedRecord.length).writeBytes(encodedRecord);
    }

    /**
     * convert byte[] to ByteBuf.
     *
     * @param encodedRecord
     *            byte[] to be converted to ByteBuf
     * @param srcIndex
     *            source index to start from
     * @param length
     *            length of record
     * @return buffered byte array
     */
    public static ByteBuf toByteBuf(final byte[] encodedRecord, final int srcIndex, final int length) {
        return ByteBufUtils.allocate(encodedRecord.length).writeBytes(encodedRecord, srcIndex, length);
    }

}
