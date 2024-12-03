/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.bootstrap;

import javax.validation.Configuration;
import javax.validation.ValidationProviderResolver;

public interface GenericBootstrap {
    public GenericBootstrap providerResolver(ValidationProviderResolver var1);

    public Configuration<?> configure();
}

