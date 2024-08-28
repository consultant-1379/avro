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

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.avro.utils.AvroWrapperUtils;

/**
 * The Class ThreadUnSafeGenericRecordDecoder, for decoding avro encoded byte arrays. BinaryDecoder is re-used every time. Not safe for multi-thread
 * access
 */
public class ThreadUnSafeGenericRecordDecoder extends GenericRecordDecoder {

    /** The Constant LOGGER. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(ThreadUnSafeGenericRecordDecoder.class);

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2573602358798375649L;

    BinaryDecoder binaryDecoder;

    /**
     * Instantiates a new generic record decoder.
     */
    public ThreadUnSafeGenericRecordDecoder() {
        this(new DefaultAvroClient());
    }

    /**
     * Instantiates a new generic record decoder.
     *
     * @param client
     *            the Avroclient
     */
    public ThreadUnSafeGenericRecordDecoder(final AvroClient client) {
        super(client);
        binaryDecoder = DecoderFactory.get().binaryDecoder(EMPTY_BYTE_ARRAY, binaryDecoder);
    }

    @Override
    protected BinaryDecoder getBinaryDecoder(final byte[] wrappedRecord, final int recordLength) {
        binaryDecoder = DecoderFactory.get().binaryDecoder(wrappedRecord, AvroWrapperUtils.EVENT_HEADER_BYTES, recordLength, binaryDecoder);
        return binaryDecoder;
    }

}
