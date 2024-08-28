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
package com.ericsson.component.aia.common.avro.decoder;

import java.io.Serializable;

/**
 * The Interface BaseDecoder for transforming byte arrays to <T> objects.
 *
 * @param <T>
 *            the generic type
 */
public interface BaseDecoder<T> extends Serializable {

    /**
     * Transform a byte array to an <T> object
     *
     * @param encodedBytes
     *            a byte array to be transformed
     *
     * @return transformed object
     */
    T decode(final byte[] encodedBytes);
}
