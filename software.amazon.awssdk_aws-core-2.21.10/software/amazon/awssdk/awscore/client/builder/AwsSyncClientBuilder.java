/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.client.builder.SdkSyncClientBuilder
 */
package software.amazon.awssdk.awscore.client.builder;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.client.builder.SdkSyncClientBuilder;

@SdkPublicApi
public interface AwsSyncClientBuilder<B extends AwsSyncClientBuilder<B, C>, C>
extends SdkSyncClientBuilder<B, C> {
}

