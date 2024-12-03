/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
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

