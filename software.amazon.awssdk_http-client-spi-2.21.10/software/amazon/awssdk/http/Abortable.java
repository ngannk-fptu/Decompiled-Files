/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.http;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public interface Abortable {
    public void abort();
}

