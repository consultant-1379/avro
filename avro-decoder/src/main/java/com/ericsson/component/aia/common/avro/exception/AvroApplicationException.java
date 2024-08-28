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
package com.ericsson.component.aia.common.avro.exception;

/**
 * AvroApplicationException is a runtime exception thrown by Avro decoder application when a internal error or unexpected error occurs.
 */
public class AvroApplicationException extends RuntimeException {

    private static final long serialVersionUID = -1634784390456192367L;

    /**
     * Generated SerialVersionUID for AvroApplicationException.
     */

    public AvroApplicationException() {
        super();
    }

    /**
     * Instantiates a new avro application exception.
     *
     * @param throwable
     *            the throwable
     */
    public AvroApplicationException(final Throwable throwable) {
        super(throwable);
    }

    /**
     * Instantiates a new avro application exception.
     *
     * @param message
     *            the message
     */
    public AvroApplicationException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new avro application exception.
     *
     * @param message
     *            the message
     * @param throwable
     *            the throwable
     */
    public AvroApplicationException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
