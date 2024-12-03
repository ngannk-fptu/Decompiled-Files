/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.Buildable;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkProtectedApi
public final class SdkPojoBuilder<T extends SdkPojo>
implements SdkPojo,
Buildable {
    private final T delegate;

    public SdkPojoBuilder(T delegate) {
        Validate.isTrue(delegate.sdkFields().isEmpty(), "Delegate must be empty.", new Object[0]);
        Validate.isTrue(!(delegate instanceof ToCopyableBuilder), "Delegate already has a builder.", new Object[0]);
        Validate.isTrue(!(delegate instanceof Buildable), "Delegate is already a builder.", new Object[0]);
        this.delegate = delegate;
    }

    @Override
    public List<SdkField<?>> sdkFields() {
        return Collections.emptyList();
    }

    @Override
    public boolean equalsBySdkFields(Object other) {
        return this.delegate.equalsBySdkFields(other);
    }

    public T build() {
        return this.delegate;
    }
}

