/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.token.credentials.SdkToken
 */
package software.amazon.awssdk.awscore.internal.token;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.awscore.AwsResponse;

@SdkInternalApi
public interface TokenTransformer<T extends SdkToken, R extends AwsResponse> {
    public T transform(R var1);
}

