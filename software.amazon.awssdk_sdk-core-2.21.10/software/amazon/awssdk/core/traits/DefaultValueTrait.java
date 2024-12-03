/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.traits;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.IdempotentUtils;

@SdkProtectedApi
public final class DefaultValueTrait
implements Trait {
    private final Supplier<?> defaultValueSupplier;

    private DefaultValueTrait(Supplier<?> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
    }

    public Object resolveValue(Object val) {
        return val != null ? val : this.defaultValueSupplier.get();
    }

    public static DefaultValueTrait create(Supplier<?> supplier) {
        return new DefaultValueTrait(supplier);
    }

    public static DefaultValueTrait idempotencyToken() {
        return new DefaultValueTrait(IdempotentUtils.getGenerator());
    }
}

