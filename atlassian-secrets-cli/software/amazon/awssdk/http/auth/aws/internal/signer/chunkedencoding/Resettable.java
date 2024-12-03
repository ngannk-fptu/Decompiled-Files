/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public interface Resettable {
    default public void reset() {
    }
}

