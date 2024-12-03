/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.Buildable
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
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
        Validate.isTrue((boolean)delegate.sdkFields().isEmpty(), (String)"Delegate must be empty.", (Object[])new Object[0]);
        Validate.isTrue((!(delegate instanceof ToCopyableBuilder) ? 1 : 0) != 0, (String)"Delegate already has a builder.", (Object[])new Object[0]);
        Validate.isTrue((!(delegate instanceof Buildable) ? 1 : 0) != 0, (String)"Delegate is already a builder.", (Object[])new Object[0]);
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

