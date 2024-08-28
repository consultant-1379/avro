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

/**
 * The Class AvroWrapperUtils.
 */
public final class AvroWrapperUtils {

    /** The Constant EVENT_HEADER_BYTES. */
    public static final int EVENT_HEADER_BYTES = 11;

    private static final byte AVRO_ENCODING_SCHEME_BYTE = 2;

    private AvroWrapperUtils() {
    }

    /**
     * Gets the record length.
     *
     * @param wrappedEventLength
     *            the wrapped event length
     * @return the record length
     */
    public static int getRecordLength(final int wrappedEventLength) {
        return wrappedEventLength - EVENT_HEADER_BYTES;
    }

    /**
     * Prepend extra header bytes for wrapped event length and hash ID.
     *
     * @param encodedEvent
     *            the encoded event as a byte array
     * @param hashId
     *            the hash id
     * @return the byte[]
     */
    public static byte[] prependExtraBytes(final byte[] encodedEvent, final long hashId) {
        final byte[] eventLengthWithHashId = generateExtraBytes(encodedEvent, hashId);
        final byte[] allBytes = new byte[eventLengthWithHashId.length + encodedEvent.length];

        System.arraycopy(eventLengthWithHashId, 0, allBytes, 0, eventLengthWithHashId.length);
        System.arraycopy(encodedEvent, 0, allBytes, eventLengthWithHashId.length, encodedEvent.length);
        return allBytes;
    }

    /**
     * Check that the payload encoding scheme used for the record is Avro.
     *
     * @param pesByteValue
     *            the pes byte value
     */
    public static void checkPayloadEncodingSchemeByte(final byte pesByteValue) {
        if (pesByteValue != AVRO_ENCODING_SCHEME_BYTE) {
            throw new InvalidEncodingSchemeException("Encoding scheme used is not Avro. PES byte value=" + pesByteValue);
        }
    }

    private static byte[] generateExtraBytes(final byte[] encodedEvent, final long hashId) {
        final short wrappedEventLength = (short) (encodedEvent.length + EVENT_HEADER_BYTES);
        final byte[] eventLengthWithHashId = new byte[] { (byte) (wrappedEventLength >> 8), (byte) wrappedEventLength, AVRO_ENCODING_SCHEME_BYTE,
                (byte) (hashId >> 56), (byte) (hashId >> 48), (byte) (hashId >> 40), (byte) (hashId >> 32), (byte) (hashId >> 24),
                (byte) (hashId >> 16), (byte) (hashId >> 8), (byte) hashId };
        return eventLengthWithHashId;
    }

}
