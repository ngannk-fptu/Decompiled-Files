/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.After
 *  org.junit.Before
 */
package com.opensymphony.xwork2;

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
import org.junit.After;
import org.junit.Before;

public abstract class XWorkJUnit4TestCase {
    protected ConfigurationManager configurationManager;
    protected Configuration configuration;
    protected Container container;
    protected ActionProxyFactory actionProxyFactory;

    @Before
    public void setUp() throws Exception {
        this.configurationManager = XWorkTestCaseHelper.setUp();
        this.configuration = this.configurationManager.getConfiguration();
        this.container = this.configuration.getContainer();
        this.actionProxyFactory = this.container.getInstance(ActionProxyFactory.class);
    }

    @After
    public void tearDown() throws Exception {
        XWorkTestCaseHelper.tearDown(this.configurationManager);
    }

    protected void loadConfigurationProviders(ConfigurationProvider ... providers) {
        this.configurationManager = XWorkTestCaseHelper.loadConfigurationProviders(this.configurationManager, providers);
        this.configuration = this.configurationManager.getConfiguration();
        this.container = this.configuration.getContainer();
        this.actionProxyFactory = this.container.getInstance(ActionProxyFactory.class);
    }

    protected void loadButAdd(Class<?> type, Object impl) {
        this.loadButAdd(type, "default", impl);
    }

    protected void loadButAdd(final Class<?> type, final String name, final Object impl) {
        this.loadConfigurationProviders(new StubConfigurationProvider(){

            @Override
            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                builder.factory(type, name, new Factory(){

                    public Object create(Context context) throws Exception {
                        return impl;
                    }

                    public Class type() {
                        return impl.getClass();
                    }
                }, Scope.SINGLETON);
            }
        });
    }
}

