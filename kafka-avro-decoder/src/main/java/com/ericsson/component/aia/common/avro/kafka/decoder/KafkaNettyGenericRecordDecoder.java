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
package com.ericsson.component.aia.common.avro.kafka.decoder;

import com.ericsson.component.aia.common.avro.decoder.NettyGenericRecordDecoder;

import kafka.utils.VerifiableProperties;

/**
 * The Class KafkaNettyGenericRecordDecoder.
 */
public class KafkaNettyGenericRecordDecoder extends KafkaGenericRecordDecoder {

    /**
     * Instantiates a new kafka netty generic record decoder.
     */
    public KafkaNettyGenericRecordDecoder() {
        super(new NettyGenericRecordDecoder());
    }

    /**
     * Instantiates a new kafka generic record decoder.Even though this constructor takes an argument and does nothing with it, this exact constructor
     * is require for Kafka serializers to compile.
     *
     * @param props
     *            the props
     */
    public KafkaNettyGenericRecordDecoder(final VerifiableProperties props) {
        this();
    }

}
