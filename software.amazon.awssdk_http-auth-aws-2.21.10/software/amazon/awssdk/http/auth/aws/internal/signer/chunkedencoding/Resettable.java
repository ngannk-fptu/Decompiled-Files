/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public interface Resettable {
    default public void reset() {
    }
}

