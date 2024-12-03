/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms;

import com.amazonaws.ClientConfigurationFactory;
import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.client.AwsAsyncClientParams;
import com.amazonaws.client.builder.AwsAsyncClientBuilder;
import com.amazonaws.services.kms.AWSKMSAsync;
import com.amazonaws.services.kms.AWSKMSAsyncClient;

@NotThreadSafe
public final class AWSKMSAsyncClientBuilder
extends AwsAsyncClientBuilder<AWSKMSAsyncClientBuilder, AWSKMSAsync> {
    private static final ClientConfigurationFactory CLIENT_CONFIG_FACTORY = new ClientConfigurationFactory();

    public static AWSKMSAsyncClientBuilder standard() {
        return new AWSKMSAsyncClientBuilder();
    }

    public static AWSKMSAsync defaultClient() {
        return (AWSKMSAsync)AWSKMSAsyncClientBuilder.standard().build();
    }

    private AWSKMSAsyncClientBuilder() {
        super(CLIENT_CONFIG_FACTORY);
    }

    @Override
    protected AWSKMSAsync build(AwsAsyncClientParams params) {
        return new AWSKMSAsyncClient(params);
    }
}

