/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.interceptor.trait;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public class HttpChecksumRequired {
    private HttpChecksumRequired() {
    }

    public static HttpChecksumRequired create() {
        return new HttpChecksumRequired();
    }
}

