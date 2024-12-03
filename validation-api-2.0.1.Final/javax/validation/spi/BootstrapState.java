/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.spi;

import javax.validation.ValidationProviderResolver;

public interface BootstrapState {
    public ValidationProviderResolver getValidationProviderResolver();

    public ValidationProviderResolver getDefaultValidationProviderResolver();
}

