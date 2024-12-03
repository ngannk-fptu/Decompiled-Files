/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.client.builder.SdkAsyncClientBuilder
 */
package software.amazon.awssdk.awscore.client.builder;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.client.builder.SdkAsyncClientBuilder;

@SdkPublicApi
public interface AwsAsyncClientBuilder<B extends AwsAsyncClientBuilder<B, C>, C>
extends SdkAsyncClientBuilder<B, C> {
}

