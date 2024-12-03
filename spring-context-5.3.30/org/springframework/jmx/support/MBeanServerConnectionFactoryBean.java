/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.TargetSource
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.target.AbstractLazyCreationTargetSource
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
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
import org.springframework.aop.TargetSource;
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
        CollectionUtils.mergePropertiesIntoMap((Properties)environment2, this.environment);
    }

    public void setEnvironmentMap(@Nullable Map<String, ?> environment2) {
        if (environment2 != null) {
            this.environment.putAll(environment2);
        }
    }

    public void setConnectOnStartup(boolean connectOnStartup) {
        this.connectOnStartup = connectOnStartup;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

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
        Assert.state((this.serviceUrl != null ? 1 : 0) != 0, (String)"No JMXServiceURL set");
        this.connector = JMXConnectorFactory.connect(this.serviceUrl, this.environment);
        this.connection = this.connector.getMBeanServerConnection();
    }

    private void createLazyConnection() {
        this.connectorTargetSource = new JMXConnectorLazyInitTargetSource();
        MBeanServerConnectionLazyInitTargetSource connectionTargetSource = new MBeanServerConnectionLazyInitTargetSource();
        this.connector = (JMXConnector)new ProxyFactory(JMXConnector.class, (TargetSource)this.connectorTargetSource).getProxy(this.beanClassLoader);
        this.connection = (MBeanServerConnection)new ProxyFactory(MBeanServerConnection.class, (TargetSource)connectionTargetSource).getProxy(this.beanClassLoader);
    }

    @Nullable
    public MBeanServerConnection getObject() {
        return this.connection;
    }

    public Class<? extends MBeanServerConnection> getObjectType() {
        return this.connection != null ? this.connection.getClass() : MBeanServerConnection.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() throws IOException {
        if (this.connector != null && (this.connectorTargetSource == null || this.connectorTargetSource.isInitialized())) {
            this.connector.close();
        }
    }

    private class MBeanServerConnectionLazyInitTargetSource
    extends AbstractLazyCreationTargetSource {
        private MBeanServerConnectionLazyInitTargetSource() {
        }

        protected Object createObject() throws Exception {
            Assert.state((MBeanServerConnectionFactoryBean.this.connector != null ? 1 : 0) != 0, (String)"JMXConnector not initialized");
            return MBeanServerConnectionFactoryBean.this.connector.getMBeanServerConnection();
        }

        public Class<?> getTargetClass() {
            return MBeanServerConnection.class;
        }
    }

    private class JMXConnectorLazyInitTargetSource
    extends AbstractLazyCreationTargetSource {
        private JMXConnectorLazyInitTargetSource() {
        }

        protected Object createObject() throws Exception {
            Assert.state((MBeanServerConnectionFactoryBean.this.serviceUrl != null ? 1 : 0) != 0, (String)"No JMXServiceURL set");
            return JMXConnectorFactory.connect(MBeanServerConnectionFactoryBean.this.serviceUrl, MBeanServerConnectionFactoryBean.this.environment);
        }

        public Class<?> getTargetClass() {
            return JMXConnector.class;
        }
    }
}

