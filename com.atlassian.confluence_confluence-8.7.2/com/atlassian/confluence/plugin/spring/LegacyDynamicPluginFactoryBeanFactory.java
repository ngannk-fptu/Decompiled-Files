/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.factories.LegacyDynamicPluginFactory
 *  org.springframework.beans.factory.FactoryBean
 */
package com.atlassian.confluence.plugin.spring;

import com.atlassian.confluence.plugin.PluginDirectoryProvider;
import com.atlassian.plugin.factories.LegacyDynamicPluginFactory;
import org.springframework.beans.factory.FactoryBean;

public class LegacyDynamicPluginFactoryBeanFactory
implements FactoryBean {
    private String pluginDescriptorFileName;
    private PluginDirectoryProvider pluginDirectoryProvider;

    public Object getObject() throws Exception {
        return new LegacyDynamicPluginFactory(this.pluginDescriptorFileName, this.pluginDirectoryProvider.getPluginTempDirectory());
    }

    public Class getObjectType() {
        return LegacyDynamicPluginFactory.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setPluginDescriptorFileName(String pluginDescriptorFileName) {
        this.pluginDescriptorFileName = pluginDescriptorFileName;
    }

    public void setPluginDirectoryProvider(PluginDirectoryProvider pluginDirectoryProvider) {
        this.pluginDirectoryProvider = pluginDirectoryProvider;
    }
}

