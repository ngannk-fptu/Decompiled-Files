/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.util.Map;
import org.apache.commons.configuration2.ConfigurationConsumer;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.PropertiesBuilderProperties;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class PropertiesBuilderParametersImpl
extends FileBasedBuilderParametersImpl
implements PropertiesBuilderProperties<PropertiesBuilderParametersImpl> {
    private static final String PROP_INCLUDE_LISTENER = "includeListener";
    private static final String PROP_INCLUDES_ALLOWED = "includesAllowed";
    private static final String PROP_LAYOUT = "layout";
    private static final String PROP_IO_FACTORY = "IOFactory";

    @Override
    public PropertiesBuilderParametersImpl setIncludeListener(ConfigurationConsumer<ConfigurationException> includeListener) {
        this.storeProperty(PROP_INCLUDE_LISTENER, includeListener);
        return this;
    }

    @Override
    public PropertiesBuilderParametersImpl setIncludesAllowed(boolean f) {
        this.storeProperty(PROP_INCLUDES_ALLOWED, f);
        return this;
    }

    @Override
    public void inheritFrom(Map<String, ?> source) {
        super.inheritFrom(source);
        this.copyPropertiesFrom(source, PROP_INCLUDES_ALLOWED, PROP_INCLUDE_LISTENER, PROP_IO_FACTORY);
    }

    @Override
    public PropertiesBuilderParametersImpl setLayout(PropertiesConfigurationLayout layout) {
        this.storeProperty(PROP_LAYOUT, layout);
        return this;
    }

    @Override
    public PropertiesBuilderParametersImpl setIOFactory(PropertiesConfiguration.IOFactory factory) {
        this.storeProperty(PROP_IO_FACTORY, factory);
        return this;
    }
}

