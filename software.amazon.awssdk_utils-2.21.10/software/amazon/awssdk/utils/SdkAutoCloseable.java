/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.utils;

import software.amazon.awssdk.annotations.SdkPublicApi;

@SdkPublicApi
public interface SdkAutoCloseable
extends AutoCloseable {
    @Override
    public void close();
}

