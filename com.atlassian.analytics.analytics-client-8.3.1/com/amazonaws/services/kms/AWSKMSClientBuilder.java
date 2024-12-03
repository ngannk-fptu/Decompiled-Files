/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms;

import com.amazonaws.ClientConfigurationFactory;
import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.client.AwsSyncClientParams;
import com.amazonaws.client.builder.AwsSyncClientBuilder;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;

@NotThreadSafe
public final class AWSKMSClientBuilder
extends AwsSyncClientBuilder<AWSKMSClientBuilder, AWSKMS> {
    private static final ClientConfigurationFactory CLIENT_CONFIG_FACTORY = new ClientConfigurationFactory();

    public static AWSKMSClientBuilder standard() {
        return new AWSKMSClientBuilder();
    }

    public static AWSKMS defaultClient() {
        return (AWSKMS)AWSKMSClientBuilder.standard().build();
    }

    private AWSKMSClientBuilder() {
        super(CLIENT_CONFIG_FACTORY);
    }

    @Override
    protected AWSKMS build(AwsSyncClientParams params) {
        return new AWSKMSClient(params);
    }
}

