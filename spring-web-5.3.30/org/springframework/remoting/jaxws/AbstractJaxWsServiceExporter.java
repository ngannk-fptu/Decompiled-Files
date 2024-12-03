/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.jws.WebService
 *  javax.xml.ws.Endpoint
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.WebServiceProvider
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.CannotLoadBeanClassException
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.remoting.jaxws;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.WebServiceProvider;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractJaxWsServiceExporter
implements BeanFactoryAware,
InitializingBean,
DisposableBean {
    @Nullable
    private Map<String, Object> endpointProperties;
    @Nullable
    private Executor executor;
    @Nullable
    private String bindingType;
    @Nullable
    private WebServiceFeature[] endpointFeatures;
    @Nullable
    private ListableBeanFactory beanFactory;
    private final Set<Endpoint> publishedEndpoints = new LinkedHashSet<Endpoint>();

    public void setEndpointProperties(Map<String, Object> endpointProperties) {
        this.endpointProperties = endpointProperties;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
    }

    public void setEndpointFeatures(WebServiceFeature ... endpointFeatures) {
        this.endpointFeatures = endpointFeatures;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ListableBeanFactory)) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " requires a ListableBeanFactory");
        }
        this.beanFactory = (ListableBeanFactory)beanFactory;
    }

    public void afterPropertiesSet() throws Exception {
        this.publishEndpoints();
    }

    public void publishEndpoints() {
        Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"No BeanFactory set");
        LinkedHashSet beanNames = new LinkedHashSet(this.beanFactory.getBeanDefinitionCount());
        Collections.addAll(beanNames, this.beanFactory.getBeanDefinitionNames());
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            Collections.addAll(beanNames, ((ConfigurableBeanFactory)this.beanFactory).getSingletonNames());
        }
        for (String beanName : beanNames) {
            try {
                Class type = this.beanFactory.getType(beanName);
                if (type == null || type.isInterface()) continue;
                WebService wsAnnotation = type.getAnnotation(WebService.class);
                WebServiceProvider wsProviderAnnotation = type.getAnnotation(WebServiceProvider.class);
                if (wsAnnotation == null && wsProviderAnnotation == null) continue;
                Endpoint endpoint = this.createEndpoint(this.beanFactory.getBean(beanName));
                if (this.endpointProperties != null) {
                    endpoint.setProperties(this.endpointProperties);
                }
                if (this.executor != null) {
                    endpoint.setExecutor(this.executor);
                }
                if (wsAnnotation != null) {
                    this.publishEndpoint(endpoint, wsAnnotation);
                } else {
                    this.publishEndpoint(endpoint, wsProviderAnnotation);
                }
                this.publishedEndpoints.add(endpoint);
            }
            catch (CannotLoadBeanClassException cannotLoadBeanClassException) {}
        }
    }

    protected Endpoint createEndpoint(Object bean) {
        return this.endpointFeatures != null ? Endpoint.create((String)this.bindingType, (Object)bean, (WebServiceFeature[])this.endpointFeatures) : Endpoint.create((String)this.bindingType, (Object)bean);
    }

    protected abstract void publishEndpoint(Endpoint var1, WebService var2);

    protected abstract void publishEndpoint(Endpoint var1, WebServiceProvider var2);

    public void destroy() {
        for (Endpoint endpoint : this.publishedEndpoints) {
            endpoint.stop();
        }
    }
}

