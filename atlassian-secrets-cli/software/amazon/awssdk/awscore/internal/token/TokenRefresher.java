/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.internal.token;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkInternalApi
public interface TokenRefresher<T extends SdkToken>
extends SdkAutoCloseable {
    public T refreshIfStaleAndFetch();
}

