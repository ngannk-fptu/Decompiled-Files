/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.combined;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.combined.ConfigurationDeclaration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public interface ConfigurationBuilderProvider {
    public ConfigurationBuilder<? extends Configuration> getConfigurationBuilder(ConfigurationDeclaration var1) throws ConfigurationException;
}

