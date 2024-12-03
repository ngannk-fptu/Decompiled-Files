/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.identity.spi.TokenIdentity
 */
package software.amazon.awssdk.auth.token.credentials.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.identity.spi.TokenIdentity;

@SdkInternalApi
public class TokenUtils {
    private TokenUtils() {
    }

    public static SdkToken toSdkToken(TokenIdentity tokenIdentity) {
        if (tokenIdentity == null) {
            return null;
        }
        if (tokenIdentity instanceof SdkToken) {
            return (SdkToken)tokenIdentity;
        }
        return () -> tokenIdentity.token();
    }
}

