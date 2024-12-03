/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 */
package software.amazon.awssdk.core.runtime.transform;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkProtectedApi
public interface Marshaller<InputT> {
    public SdkHttpFullRequest marshall(InputT var1);
}

