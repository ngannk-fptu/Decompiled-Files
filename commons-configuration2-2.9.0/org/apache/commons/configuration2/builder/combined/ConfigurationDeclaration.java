/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import java.util.Set;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.beanutils.XMLBeanDeclaration;
import org.apache.commons.configuration2.builder.combined.CombinedConfigurationBuilder;

public class ConfigurationDeclaration
extends XMLBeanDeclaration {
    private final CombinedConfigurationBuilder configurationBuilder;

    public ConfigurationDeclaration(CombinedConfigurationBuilder builder, HierarchicalConfiguration<?> config) {
        super(config);
        this.configurationBuilder = builder;
    }

    public CombinedConfigurationBuilder getConfigurationBuilder() {
        return this.configurationBuilder;
    }

    public String getAt() {
        String result = this.getConfiguration().getString("[@config-at]");
        return result == null ? this.getConfiguration().getString("[@at]") : result;
    }

    public boolean isOptional() {
        Boolean value = this.getConfiguration().getBoolean("[@config-optional]", null);
        if (value == null) {
            value = this.getConfiguration().getBoolean("[@optional]", Boolean.FALSE);
        }
        return value;
    }

    public boolean isForceCreate() {
        return this.getConfiguration().getBoolean("[@config-forceCreate]", false);
    }

    public boolean isReload() {
        return this.getConfiguration().getBoolean("[@config-reload]", false);
    }

    public String getName() {
        return this.getConfiguration().getString("[@config-name]");
    }

    @Override
    public String getBeanFactoryName() {
        return CombinedConfigurationBuilder.CONFIG_BEAN_FACTORY_NAME;
    }

    @Override
    public String getBeanClassName() {
        return null;
    }

    @Override
    protected boolean isReservedAttributeName(String name) {
        if (super.isReservedAttributeName(name)) {
            return true;
        }
        Set<String> attributes = this.getAttributeNames();
        return "at".equals(name) && !attributes.contains("config-at") || "optional".equals(name) && !attributes.contains("config-optional");
    }
}

