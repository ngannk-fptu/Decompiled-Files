/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

public class PropertiesBeanDefinitionReader
extends AbstractBeanDefinitionReader {
    public static final String TRUE_VALUE = "true";
    public static final String SEPARATOR = ".";
    public static final String CLASS_KEY = "(class)";
    public static final String PARENT_KEY = "(parent)";
    public static final String SCOPE_KEY = "(scope)";
    public static final String SINGLETON_KEY = "(singleton)";
    public static final String ABSTRACT_KEY = "(abstract)";
    public static final String LAZY_INIT_KEY = "(lazy-init)";
    public static final String REF_SUFFIX = "(ref)";
    public static final String REF_PREFIX = "*";
    public static final String CONSTRUCTOR_ARG_PREFIX = "$";
    @Nullable
    private String defaultParentBean;
    private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

    public PropertiesBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public void setDefaultParentBean(@Nullable String defaultParentBean) {
        this.defaultParentBean = defaultParentBean;
    }

    @Nullable
    public String getDefaultParentBean() {
        return this.defaultParentBean;
    }

    public void setPropertiesPersister(@Nullable PropertiesPersister propertiesPersister) {
        this.propertiesPersister = propertiesPersister != null ? propertiesPersister : new DefaultPropertiesPersister();
    }

    public PropertiesPersister getPropertiesPersister() {
        return this.propertiesPersister;
    }

    @Override
    public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(new EncodedResource(resource), null);
    }

    public int loadBeanDefinitions(Resource resource, @Nullable String prefix) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(new EncodedResource(resource), prefix);
    }

    public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(encodedResource, null);
    }

    public int loadBeanDefinitions(EncodedResource encodedResource, @Nullable String prefix) throws BeanDefinitionStoreException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Loading properties bean definitions from " + encodedResource);
        }
        Properties props = new Properties();
        try {
            try (InputStream is = encodedResource.getResource().getInputStream();){
                if (encodedResource.getEncoding() != null) {
                    this.getPropertiesPersister().load(props, new InputStreamReader(is, encodedResource.getEncoding()));
                } else {
                    this.getPropertiesPersister().load(props, is);
                }
            }
            int count = this.registerBeanDefinitions(props, prefix, encodedResource.getResource().getDescription());
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Loaded " + count + " bean definitions from " + encodedResource);
            }
            return count;
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("Could not parse properties from " + encodedResource.getResource(), ex);
        }
    }

    public int registerBeanDefinitions(ResourceBundle rb) throws BeanDefinitionStoreException {
        return this.registerBeanDefinitions(rb, null);
    }

    public int registerBeanDefinitions(ResourceBundle rb, @Nullable String prefix) throws BeanDefinitionStoreException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Enumeration<String> keys = rb.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, rb.getObject(key));
        }
        return this.registerBeanDefinitions(map, prefix);
    }

    public int registerBeanDefinitions(Map<?, ?> map) throws BeansException {
        return this.registerBeanDefinitions(map, null);
    }

    public int registerBeanDefinitions(Map<?, ?> map, @Nullable String prefix) throws BeansException {
        return this.registerBeanDefinitions(map, prefix, "Map " + map);
    }

    public int registerBeanDefinitions(Map<?, ?> map, @Nullable String prefix, String resourceDescription) throws BeansException {
        if (prefix == null) {
            prefix = "";
        }
        int beanCount = 0;
        for (Object key : map.keySet()) {
            if (!(key instanceof String)) {
                throw new IllegalArgumentException("Illegal key [" + key + "]: only Strings allowed");
            }
            String keyString = (String)key;
            if (!keyString.startsWith(prefix)) continue;
            String nameAndProperty = keyString.substring(prefix.length());
            int propKeyIdx = nameAndProperty.indexOf("[");
            int sepIdx = propKeyIdx != -1 ? nameAndProperty.lastIndexOf(SEPARATOR, propKeyIdx) : nameAndProperty.lastIndexOf(SEPARATOR);
            if (sepIdx != -1) {
                String beanName = nameAndProperty.substring(0, sepIdx);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Found bean name '" + beanName + "'");
                }
                if (this.getRegistry().containsBeanDefinition(beanName)) continue;
                this.registerBeanDefinition(beanName, map, prefix + beanName, resourceDescription);
                ++beanCount;
                continue;
            }
            if (!this.logger.isDebugEnabled()) continue;
            this.logger.debug("Invalid bean name and property [" + nameAndProperty + "]");
        }
        return beanCount;
    }

    protected void registerBeanDefinition(String beanName, Map<?, ?> map, String prefix, String resourceDescription) throws BeansException {
        String className = null;
        String parent = null;
        String scope = "singleton";
        boolean isAbstract = false;
        boolean lazyInit = false;
        ConstructorArgumentValues cas = new ConstructorArgumentValues();
        MutablePropertyValues pvs = new MutablePropertyValues();
        String prefixWithSep = prefix + SEPARATOR;
        int beginIndex = prefixWithSep.length();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = StringUtils.trimWhitespace((String)entry.getKey());
            if (!key.startsWith(prefixWithSep)) continue;
            String property = key.substring(beginIndex);
            if (CLASS_KEY.equals(property)) {
                className = StringUtils.trimWhitespace((String)entry.getValue());
                continue;
            }
            if (PARENT_KEY.equals(property)) {
                parent = StringUtils.trimWhitespace((String)entry.getValue());
                continue;
            }
            if (ABSTRACT_KEY.equals(property)) {
                String val = StringUtils.trimWhitespace((String)entry.getValue());
                isAbstract = TRUE_VALUE.equals(val);
                continue;
            }
            if (SCOPE_KEY.equals(property)) {
                scope = StringUtils.trimWhitespace((String)entry.getValue());
                continue;
            }
            if (SINGLETON_KEY.equals(property)) {
                String val = StringUtils.trimWhitespace((String)entry.getValue());
                scope = !StringUtils.hasLength(val) || TRUE_VALUE.equals(val) ? "singleton" : "prototype";
                continue;
            }
            if (LAZY_INIT_KEY.equals(property)) {
                String val = StringUtils.trimWhitespace((String)entry.getValue());
                lazyInit = TRUE_VALUE.equals(val);
                continue;
            }
            if (property.startsWith(CONSTRUCTOR_ARG_PREFIX)) {
                if (property.endsWith(REF_SUFFIX)) {
                    int index = Integer.parseInt(property.substring(1, property.length() - REF_SUFFIX.length()));
                    cas.addIndexedArgumentValue(index, new RuntimeBeanReference(entry.getValue().toString()));
                    continue;
                }
                int index = Integer.parseInt(property.substring(1));
                cas.addIndexedArgumentValue(index, this.readValue(entry));
                continue;
            }
            if (property.endsWith(REF_SUFFIX)) {
                property = property.substring(0, property.length() - REF_SUFFIX.length());
                String ref = StringUtils.trimWhitespace((String)entry.getValue());
                RuntimeBeanReference val = new RuntimeBeanReference(ref);
                pvs.add(property, val);
                continue;
            }
            pvs.add(property, this.readValue(entry));
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Registering bean definition for bean name '" + beanName + "' with " + pvs);
        }
        if (parent == null && className == null && !beanName.equals(this.defaultParentBean)) {
            parent = this.defaultParentBean;
        }
        try {
            AbstractBeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(parent, className, this.getBeanClassLoader());
            bd.setScope(scope);
            bd.setAbstract(isAbstract);
            bd.setLazyInit(lazyInit);
            bd.setConstructorArgumentValues(cas);
            bd.setPropertyValues(pvs);
            this.getRegistry().registerBeanDefinition(beanName, bd);
        }
        catch (ClassNotFoundException ex) {
            throw new CannotLoadBeanClassException(resourceDescription, beanName, className, ex);
        }
        catch (LinkageError err) {
            throw new CannotLoadBeanClassException(resourceDescription, beanName, className, err);
        }
    }

    private Object readValue(Map.Entry<?, ?> entry) {
        String strVal;
        Object val = entry.getValue();
        if (val instanceof String && (strVal = (String)val).startsWith(REF_PREFIX)) {
            String targetName = strVal.substring(1);
            val = targetName.startsWith(REF_PREFIX) ? targetName : new RuntimeBeanReference(targetName);
        }
        return val;
    }
}

