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
package com.ericsson.component.aia.common.avro.encoder;

import java.io.Serializable;

/**
 * The Interface BaseEncoder for transforming <T> objects to byte arrays.
 *
 * @param <T>
 *            the generic type
 */
public interface BaseEncoder<T> extends Serializable {

    /**
     * Transforms an object
     *
     * @param t
     *            the t
     * @return the byte[]
     */
    byte[] encode(T t);
}