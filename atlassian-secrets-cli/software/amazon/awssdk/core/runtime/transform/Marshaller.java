/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.runtime.transform;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkProtectedApi
public interface Marshaller<InputT> {
    public SdkHttpFullRequest marshall(InputT var1);
}

