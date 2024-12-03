/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.support;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.AbstractLazyCreationTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

public class MBeanServerConnectionFactoryBean
implements FactoryBean<MBeanServerConnection>,
BeanClassLoaderAware,
InitializingBean,
DisposableBean {
    @Nullable
    private JMXServiceURL serviceUrl;
    private Map<String, Object> environment = new HashMap<String, Object>();
    private boolean connectOnStartup = true;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private JMXConnector connector;
    @Nullable
    private MBeanServerConnection connection;
    @Nullable
    private JMXConnectorLazyInitTargetSource connectorTargetSource;

    public void setServiceUrl(String url) throws MalformedURLException {
        this.serviceUrl = new JMXServiceURL(url);
    }

    public void setEnvironment(Properties environment2) {
        CollectionUtils.mergePropertiesIntoMap(environment2, this.environment);
    }

    public void setEnvironmentMap(@Nullable Map<String, ?> environment2) {
        if (environment2 != null) {
            this.environment.putAll(environment2);
        }
    }

    public void setConnectOnStartup(boolean connectOnStartup) {
        this.connectOnStartup = connectOnStartup;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void afterPropertiesSet() throws IOException {
        if (this.serviceUrl == null) {
            throw new IllegalArgumentException("Property 'serviceUrl' is required");
        }
        if (this.connectOnStartup) {
            this.connect();
        } else {
            this.createLazyConnection();
        }
    }

    private void connect() throws IOException {
        Assert.state(this.serviceUrl != null, "No JMXServiceURL set");
        this.connector = JMXConnectorFactory.connect(this.serviceUrl, this.environment);
        this.connection = this.connector.getMBeanServerConnection();
    }

    private void createLazyConnection() {
        this.connectorTargetSource = new JMXConnectorLazyInitTargetSource();
        MBeanServerConnectionLazyInitTargetSource connectionTargetSource = new MBeanServerConnectionLazyInitTargetSource();
        this.connector = (JMXConnector)new ProxyFactory(JMXConnector.class, this.connectorTargetSource).getProxy(this.beanClassLoader);
        this.connection = (MBeanServerConnection)new ProxyFactory(MBeanServerConnection.class, connectionTargetSource).getProxy(this.beanClassLoader);
    }

    @Override
    @Nullable
    public MBeanServerConnection getObject() {
        return this.connection;
    }

    @Override
    public Class<? extends MBeanServerConnection> getObjectType() {
        return this.connection != null ? this.connection.getClass() : MBeanServerConnection.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws IOException {
        if (this.connector != null && (this.connectorTargetSource == null || this.connectorTargetSource.isInitialized())) {
            this.connector.close();
        }
    }

    private class MBeanServerConnectionLazyInitTargetSource
    extends AbstractLazyCreationTargetSource {
        private MBeanServerConnectionLazyInitTargetSource() {
        }

        @Override
        protected Object createObject() throws Exception {
            Assert.state(MBeanServerConnectionFactoryBean.this.connector != null, "JMXConnector not initialized");
            return MBeanServerConnectionFactoryBean.this.connector.getMBeanServerConnection();
        }

        @Override
        public Class<?> getTargetClass() {
            return MBeanServerConnection.class;
        }
    }

    private class JMXConnectorLazyInitTargetSource
    extends AbstractLazyCreationTargetSource {
        private JMXConnectorLazyInitTargetSource() {
        }

        @Override
        protected Object createObject() throws Exception {
            Assert.state(MBeanServerConnectionFactoryBean.this.serviceUrl != null, "No JMXServiceURL set");
            return JMXConnectorFactory.connect(MBeanServerConnectionFactoryBean.this.serviceUrl, MBeanServerConnectionFactoryBean.this.environment);
        }

        @Override
        public Class<?> getTargetClass() {
            return JMXConnector.class;
        }
    }
}

