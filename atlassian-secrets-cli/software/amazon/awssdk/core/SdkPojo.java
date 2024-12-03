/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.util.List;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkField;

@SdkProtectedApi
public interface SdkPojo {
    public List<SdkField<?>> sdkFields();

    default public boolean equalsBySdkFields(Object other) {
        throw new UnsupportedOperationException();
    }
}

