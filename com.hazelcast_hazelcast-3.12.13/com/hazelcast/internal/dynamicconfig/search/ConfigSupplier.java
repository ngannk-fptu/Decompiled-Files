/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.hazelcast.internal.dynamicconfig.search;

import com.hazelcast.config.Config;
import com.hazelcast.internal.dynamicconfig.ConfigurationService;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ConfigSupplier<T extends IdentifiedDataSerializable> {
    @Nullable
    public T getDynamicConfig(@Nonnull ConfigurationService var1, @Nonnull String var2);

    @Nullable
    public T getStaticConfig(@Nonnull Config var1, @Nonnull String var2);

    public Map<String, T> getStaticConfigs(@Nonnull Config var1);
}

