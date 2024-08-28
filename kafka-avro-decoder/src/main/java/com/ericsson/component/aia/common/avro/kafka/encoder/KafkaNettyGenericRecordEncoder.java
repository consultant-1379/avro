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
package com.ericsson.component.aia.common.avro.kafka.encoder;

import com.ericsson.component.aia.common.avro.encoder.NettyGenericRecordEncoder;

import kafka.utils.VerifiableProperties;

/**
 * The Class KafkaNettyGenericRecordEncoder.
 */
public class KafkaNettyGenericRecordEncoder extends KafkaGenericRecordEncoder {

    /**
     * Instantiates a new kafka Netty generic record encoder.
     */
    public KafkaNettyGenericRecordEncoder() {
        super(new NettyGenericRecordEncoder());
    }

    /**
     * Instantiates a new kafka netty generic record encoder. Even though this constructor takes an argument and does nothing with it, this exact
     * constructor is require for Kafka serializers to compile.
     *
     * @param props
     *            the props
     */
    public KafkaNettyGenericRecordEncoder(final VerifiableProperties props) {
        this();
    }

}
