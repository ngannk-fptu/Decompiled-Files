/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.util.List;
import javax.validation.spi.ValidationProvider;

public interface ValidationProviderResolver {
    public List<ValidationProvider<?>> getValidationProviders();
}

