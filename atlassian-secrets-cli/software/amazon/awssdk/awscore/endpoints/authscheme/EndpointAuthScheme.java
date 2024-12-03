/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.endpoints.authscheme;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public interface EndpointAuthScheme {
    public String name();

    default public String schemeId() {
        throw new UnsupportedOperationException();
    }
}

