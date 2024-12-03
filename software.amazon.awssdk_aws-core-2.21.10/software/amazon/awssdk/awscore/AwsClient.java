/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.core.SdkClient
 */
package software.amazon.awssdk.awscore;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.AwsServiceClientConfiguration;
import software.amazon.awssdk.core.SdkClient;

@SdkPublicApi
@ThreadSafe
public interface AwsClient
extends SdkClient {
    default public AwsServiceClientConfiguration serviceClientConfiguration() {
        throw new UnsupportedOperationException();
    }
}

