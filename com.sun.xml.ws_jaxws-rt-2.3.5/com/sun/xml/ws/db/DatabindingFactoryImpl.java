/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.db;

import com.oracle.webservices.api.databinding.Databinding;
import com.oracle.webservices.api.databinding.DatabindingModeFeature;
import com.oracle.webservices.api.databinding.WSDLGenerator;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.databinding.DatabindingConfig;
import com.sun.xml.ws.api.databinding.DatabindingFactory;
import com.sun.xml.ws.api.databinding.MetadataReader;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.db.DatabindingProviderImpl;
import com.sun.xml.ws.spi.db.DatabindingProvider;
import com.sun.xml.ws.util.ServiceFinder;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;

public class DatabindingFactoryImpl
extends DatabindingFactory {
    static final String WsRuntimeFactoryDefaultImpl = "com.sun.xml.ws.db.DatabindingProviderImpl";
    protected Map<String, Object> properties = new HashMap<String, Object>();
    protected DatabindingProvider defaultRuntimeFactory;
    protected List<DatabindingProvider> providers;

    private static List<DatabindingProvider> providers() {
        ArrayList<DatabindingProvider> factories = new ArrayList<DatabindingProvider>();
        for (DatabindingProvider p : ServiceFinder.find(DatabindingProvider.class)) {
            factories.add(p);
        }
        return factories;
    }

    @Override
    public Map<String, Object> properties() {
        return this.properties;
    }

    <T> T property(Class<T> propType, String propName) {
        if (propName == null) {
            propName = propType.getName();
        }
        return propType.cast(this.properties.get(propName));
    }

    public DatabindingProvider provider(DatabindingConfig config) {
        String mode = this.databindingMode(config);
        if (this.providers == null) {
            this.providers = DatabindingFactoryImpl.providers();
        }
        DatabindingProvider provider = null;
        if (this.providers != null) {
            for (DatabindingProvider p : this.providers) {
                if (!p.isFor(mode)) continue;
                provider = p;
            }
        }
        if (provider == null) {
            provider = new DatabindingProviderImpl();
        }
        return provider;
    }

    @Override
    public Databinding createRuntime(DatabindingConfig config) {
        DatabindingProvider provider = this.provider(config);
        return provider.create(config);
    }

    public WSDLGenerator createWsdlGen(DatabindingConfig config) {
        DatabindingProvider provider = this.provider(config);
        return provider.wsdlGen(config);
    }

    String databindingMode(DatabindingConfig config) {
        if (config.getMappingInfo() != null && config.getMappingInfo().getDatabindingMode() != null) {
            return config.getMappingInfo().getDatabindingMode();
        }
        if (config.getFeatures() != null) {
            for (WebServiceFeature f : config.getFeatures()) {
                if (!(f instanceof DatabindingModeFeature)) continue;
                DatabindingModeFeature dmf = (DatabindingModeFeature)f;
                config.properties().putAll(dmf.getProperties());
                return dmf.getMode();
            }
        }
        return null;
    }

    ClassLoader classLoader() {
        ClassLoader classLoader = this.property(ClassLoader.class, null);
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return classLoader;
    }

    Properties loadPropertiesFile(String fileName) {
        ClassLoader classLoader = this.classLoader();
        Properties p = new Properties();
        try {
            InputStream is = null;
            is = classLoader == null ? ClassLoader.getSystemResourceAsStream(fileName) : classLoader.getResourceAsStream(fileName);
            if (is != null) {
                p.load(is);
            }
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
        return p;
    }

    @Override
    public Databinding.Builder createBuilder(Class<?> contractClass, Class<?> endpointClass) {
        return new ConfigBuilder(this, contractClass, endpointClass);
    }

    static class ConfigBuilder
    implements Databinding.Builder {
        DatabindingConfig config;
        DatabindingFactoryImpl factory;

        ConfigBuilder(DatabindingFactoryImpl f, Class<?> contractClass, Class<?> implBeanClass) {
            this.factory = f;
            this.config = new DatabindingConfig();
            this.config.setContractClass(contractClass);
            this.config.setEndpointClass(implBeanClass);
        }

        @Override
        public Databinding.Builder targetNamespace(String targetNamespace) {
            this.config.getMappingInfo().setTargetNamespace(targetNamespace);
            return this;
        }

        @Override
        public Databinding.Builder serviceName(QName serviceName) {
            this.config.getMappingInfo().setServiceName(serviceName);
            return this;
        }

        @Override
        public Databinding.Builder portName(QName portName) {
            this.config.getMappingInfo().setPortName(portName);
            return this;
        }

        @Override
        public Databinding.Builder wsdlURL(URL wsdlURL) {
            this.config.setWsdlURL(wsdlURL);
            return this;
        }

        @Override
        public Databinding.Builder wsdlSource(Source wsdlSource) {
            this.config.setWsdlSource(wsdlSource);
            return this;
        }

        @Override
        public Databinding.Builder entityResolver(EntityResolver entityResolver) {
            this.config.setEntityResolver(entityResolver);
            return this;
        }

        @Override
        public Databinding.Builder classLoader(ClassLoader classLoader) {
            this.config.setClassLoader(classLoader);
            return this;
        }

        @Override
        public Databinding.Builder feature(WebServiceFeature ... f) {
            this.config.setFeatures(f);
            return this;
        }

        @Override
        public Databinding.Builder property(String name, Object value) {
            this.config.properties().put(name, value);
            if (this.isfor(BindingID.class, name, value)) {
                this.config.getMappingInfo().setBindingID((BindingID)value);
            }
            if (this.isfor(WSBinding.class, name, value)) {
                this.config.setWSBinding((WSBinding)value);
            }
            if (this.isfor(WSDLPort.class, name, value)) {
                this.config.setWsdlPort((WSDLPort)value);
            }
            if (this.isfor(MetadataReader.class, name, value)) {
                this.config.setMetadataReader((MetadataReader)value);
            }
            return this;
        }

        boolean isfor(Class<?> type, String name, Object value) {
            return type.getName().equals(name) && type.isInstance(value);
        }

        @Override
        public Databinding build() {
            return this.factory.createRuntime(this.config);
        }

        @Override
        public WSDLGenerator createWSDLGenerator() {
            return this.factory.createWsdlGen(this.config);
        }
    }
}

