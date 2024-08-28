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

import java.util.Properties;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.common.avro.exception.AvroApplicationException;
import com.ericsson.component.aia.model.registry.client.SchemaRegistryClient;
import com.ericsson.component.aia.model.registry.exception.SchemaRetrievalException;
import com.ericsson.component.aia.model.registry.impl.SchemaRegistryClientFactory;

/**
 * For providing avro schemas from local filesystem.
 */
public class DefaultAvroClient implements AvroClient {

    /** The Constant LOGGER. */
    protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultAvroClient.class);
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5215243111728581532L;

    /** The client. */
    private final SchemaRegistryClient client;

    /**
     * Instantiates a new default avro client.
     */
    public DefaultAvroClient() {
        this.client = SchemaRegistryClientFactory.newSchemaRegistryClientInstance();
    }

    /**
     * Instantiates a new default avro client.
     *
     * @param properties
     *            the properties
     */
    public DefaultAvroClient(final Properties properties) {
        this.client = SchemaRegistryClientFactory.newSchemaRegistryClientInstance(properties);
    }

    /**
     * Lookup schema.
     *
     * @param id
     *            the id
     * @return the schema
     */
    @Override
    public Schema lookupSchema(final long id) {
        try {
            return client.lookup(id);
        } catch (final SchemaRetrievalException e) {
            final AvroApplicationException avroApplicationException = new AvroApplicationException(e);
            LOGGER.error("Unable to find schema {} during lookup. And exception is: ", id, avroApplicationException);
            throw avroApplicationException;
        }
    }
}
