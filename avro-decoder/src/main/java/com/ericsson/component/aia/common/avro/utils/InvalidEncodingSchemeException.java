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
 * The Class InvalidEncodingSchemeException. Thrown when the encoded Payload Encoding Scheme (PES) byte is not equal to the encoding scheme used.
 */
public class InvalidEncodingSchemeException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5885827266572759880L;

    /**
     * Instantiates a new invalid encoding scheme exception.
     *
     * @param message
     *            the message
     */
    public InvalidEncodingSchemeException(final String message) {
        super(message);
    }

}
