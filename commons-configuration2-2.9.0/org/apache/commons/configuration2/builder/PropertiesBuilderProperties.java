/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.ConfigurationConsumer;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.ex.ConfigurationException;

public interface PropertiesBuilderProperties<T> {
    default public T setIncludeListener(ConfigurationConsumer<ConfigurationException> includeListener) {
        return (T)this;
    }

    public T setIncludesAllowed(boolean var1);

    public T setLayout(PropertiesConfigurationLayout var1);

    public T setIOFactory(PropertiesConfiguration.IOFactory var1);
}

