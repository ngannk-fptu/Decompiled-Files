/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.token.credentials.SdkToken
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.awscore.internal.token;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkInternalApi
public interface TokenManager<T extends SdkToken>
extends SdkAutoCloseable {
    public Optional<T> loadToken();

    default public void storeToken(T token) {
        throw new UnsupportedOperationException();
    }
}

