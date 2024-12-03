/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.ContainerProvider;
import com.opensymphony.xwork2.config.providers.StrutsDefaultConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;

public class XWorkTestCaseHelper {
    public static ConfigurationManager setUp() throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager("default");
        configurationManager.addContainerProvider(new StrutsDefaultConfigurationProvider());
        Configuration config = configurationManager.getConfiguration();
        Container container = config.getContainer();
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getActionContext().withContainer(container).withValueStack(stack).bind();
        return configurationManager;
    }

    public static ConfigurationManager loadConfigurationProviders(ConfigurationManager configurationManager, ConfigurationProvider ... providers) {
        try {
            XWorkTestCaseHelper.tearDown(configurationManager);
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot clean old configuration", e);
        }
        configurationManager = new ConfigurationManager("default");
        configurationManager.addContainerProvider(new ContainerProvider(){

            @Override
            public void destroy() {
            }

            @Override
            public void init(Configuration configuration) throws ConfigurationException {
            }

            @Override
            public boolean needsReload() {
                return false;
            }

            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                builder.setAllowDuplicates(true);
            }
        });
        configurationManager.addContainerProvider(new StrutsDefaultConfigurationProvider());
        for (ConfigurationProvider prov : providers) {
            if (prov instanceof XmlConfigurationProvider) {
                ((XmlConfigurationProvider)prov).setThrowExceptionOnDuplicateBeans(false);
            }
            configurationManager.addContainerProvider(prov);
        }
        Container container = configurationManager.getConfiguration().getContainer();
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getActionContext().withContainer(container).withValueStack(stack).bind();
        return configurationManager;
    }

    public static void tearDown(ConfigurationManager configurationManager) throws Exception {
        if (configurationManager != null) {
            configurationManager.destroyConfiguration();
        }
        ActionContext.clear();
    }
}

