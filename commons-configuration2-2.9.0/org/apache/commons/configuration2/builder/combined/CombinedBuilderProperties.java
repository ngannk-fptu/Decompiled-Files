/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.DefaultParametersHandler;
import org.apache.commons.configuration2.builder.DefaultParametersManager;
import org.apache.commons.configuration2.builder.combined.ConfigurationBuilderProvider;

public interface CombinedBuilderProperties<T> {
    public T setInheritSettings(boolean var1);

    public T setDefinitionBuilder(ConfigurationBuilder<? extends HierarchicalConfiguration<?>> var1);

    public T registerProvider(String var1, ConfigurationBuilderProvider var2);

    public T setBasePath(String var1);

    public T setDefinitionBuilderParameters(BuilderParameters var1);

    public T setChildDefaultParametersManager(DefaultParametersManager var1);

    public <D> T registerChildDefaultsHandler(Class<D> var1, DefaultParametersHandler<? super D> var2);

    public <D> T registerChildDefaultsHandler(Class<D> var1, DefaultParametersHandler<? super D> var2, Class<?> var3);
}

