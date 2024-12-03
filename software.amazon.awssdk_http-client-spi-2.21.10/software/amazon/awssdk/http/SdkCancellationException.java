/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.http;

import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public final class SdkCancellationException
extends RuntimeException {
    public SdkCancellationException(String message) {
        super(message);
    }
}

