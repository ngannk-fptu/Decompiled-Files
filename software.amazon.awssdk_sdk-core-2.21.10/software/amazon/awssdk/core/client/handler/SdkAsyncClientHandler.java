/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 */
package software.amazon.awssdk.core.client.handler;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOptionValidation;
import software.amazon.awssdk.core.client.handler.AsyncClientHandler;
import software.amazon.awssdk.core.internal.handler.BaseAsyncClientHandler;
import software.amazon.awssdk.core.internal.http.AmazonAsyncHttpClient;

@Immutable
@ThreadSafe
@SdkProtectedApi
public class SdkAsyncClientHandler
extends BaseAsyncClientHandler
implements AsyncClientHandler {
    public SdkAsyncClientHandler(SdkClientConfiguration clientConfiguration) {
        super(clientConfiguration, new AmazonAsyncHttpClient(clientConfiguration));
        SdkClientOptionValidation.validateAsyncClientOptions(clientConfiguration);
    }
}

