/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public interface SdkAutoCloseable
extends AutoCloseable {
    @Override
    public void close();
}

