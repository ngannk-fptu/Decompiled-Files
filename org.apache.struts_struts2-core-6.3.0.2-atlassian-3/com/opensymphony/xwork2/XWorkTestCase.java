/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.TestCase
 *  org.apache.commons.lang3.ClassUtils
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.util.XWorkTestCaseHelper;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import java.util.Locale;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.commons.lang3.ClassUtils;

public abstract class XWorkTestCase
extends TestCase {
    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected ActionProxyFactory actionProxyFactory;

    protected void setUp() throws Exception {
        this.configurationManager = XWorkTestCaseHelper.setUp();
        this.configuration = this.configurationManager.getConfiguration();
        this.container = this.configuration.getContainer();
        this.actionProxyFactory = this.container.getInstance(ActionProxyFactory.class);
    }

    protected void tearDown() throws Exception {
        XWorkTestCaseHelper.tearDown(this.configurationManager);
    }

    protected void loadConfigurationProviders(ConfigurationProvider ... providers) {
        this.configurationManager = XWorkTestCaseHelper.loadConfigurationProviders(this.configurationManager, providers);
        this.configuration = this.configurationManager.getConfiguration();
        this.container = this.configuration.getContainer();
        this.actionProxyFactory = this.container.getInstance(ActionProxyFactory.class);
    }

    protected <T> void loadButAdd(Class<T> type, T impl) {
        this.loadButAdd(type, "default", impl);
    }

    protected <T> void loadButAdd(final Class<T> type, final String name, final T impl) {
        this.loadConfigurationProviders(new StubConfigurationProvider(){

            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                if (impl instanceof String || ClassUtils.isPrimitiveOrWrapper(impl.getClass())) {
                    props.setProperty(name, "" + impl);
                } else {
                    builder.factory(type, name, new Factory<T>(){

                        @Override
                        public T create(Context context) throws Exception {
                            return impl;
                        }

                        @Override
                        public Class<T> type() {
                            return impl.getClass();
                        }
                    }, Scope.SINGLETON);
                }
            }
        });
    }

    protected Map<String, Object> createContextWithLocale(Locale locale) {
        return ActionContext.of().withLocale(locale).getContextMap();
    }
}

