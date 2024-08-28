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

import org.apache.avro.Schema;

/**
 * For providing avro schemas.
 */

public interface AvroClient extends Serializable {

    /**
     * Look up a schema by id.
     *
     * @param id
     *            the id
     * @return the schema
     */
    Schema lookupSchema(long id);
}
