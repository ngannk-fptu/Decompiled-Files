/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.bootstrap;

import javax.validation.Configuration;
import javax.validation.ValidationProviderResolver;

public interface ProviderSpecificBootstrap<T extends Configuration<T>> {
    public ProviderSpecificBootstrap<T> providerResolver(ValidationProviderResolver var1);

    public T configure();
}

