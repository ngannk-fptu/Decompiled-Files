/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jndi.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.jndi.TypeMismatchNamingException;
import org.springframework.lang.Nullable;

public class SimpleJndiBeanFactory
extends JndiLocatorSupport
implements BeanFactory {
    private final Set<String> shareableResources = new HashSet<String>();
    private final Map<String, Object> singletonObjects = new HashMap<String, Object>();
    private final Map<String, Class<?>> resourceTypes = new HashMap();

    public SimpleJndiBeanFactory() {
        this.setResourceRef(true);
    }

    public void addShareableResource(String shareableResource) {
        this.shareableResources.add(shareableResource);
    }

    public void setShareableResources(String ... shareableResources) {
        Collections.addAll(this.shareableResources, shareableResources);
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return this.getBean(name, Object.class);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        try {
            if (this.isSingleton(name)) {
                return this.doGetSingleton(name, requiredType);
            }
            return this.lookup(name, requiredType);
        }
        catch (NameNotFoundException ex) {
            throw new NoSuchBeanDefinitionException(name, "not found in JNDI environment");
        }
        catch (TypeMismatchNamingException ex) {
            throw new BeanNotOfRequiredTypeException(name, ex.getRequiredType(), ex.getActualType());
        }
        catch (NamingException ex) {
            throw new BeanDefinitionStoreException("JNDI environment", name, "JNDI lookup failed", ex);
        }
    }

    @Override
    public Object getBean(String name, Object ... args) throws BeansException {
        if (args != null) {
            throw new UnsupportedOperationException("SimpleJndiBeanFactory does not support explicit bean creation arguments");
        }
        return this.getBean(name);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return this.getBean(requiredType.getSimpleName(), requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object ... args) throws BeansException {
        if (args != null) {
            throw new UnsupportedOperationException("SimpleJndiBeanFactory does not support explicit bean creation arguments");
        }
        return this.getBean(requiredType);
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(final Class<T> requiredType) {
        return new ObjectProvider<T>(){

            @Override
            public T getObject() throws BeansException {
                return SimpleJndiBeanFactory.this.getBean(requiredType);
            }

            @Override
            public T getObject(Object ... args) throws BeansException {
                return SimpleJndiBeanFactory.this.getBean(requiredType, args);
            }

            @Override
            @Nullable
            public T getIfAvailable() throws BeansException {
                try {
                    return SimpleJndiBeanFactory.this.getBean(requiredType);
                }
                catch (NoUniqueBeanDefinitionException ex) {
                    throw ex;
                }
                catch (NoSuchBeanDefinitionException ex) {
                    return null;
                }
            }

            @Override
            @Nullable
            public T getIfUnique() throws BeansException {
                try {
                    return SimpleJndiBeanFactory.this.getBean(requiredType);
                }
                catch (NoSuchBeanDefinitionException ex) {
                    return null;
                }
            }
        };
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
        throw new UnsupportedOperationException("SimpleJndiBeanFactory does not support resolution by ResolvableType");
    }

    @Override
    public boolean containsBean(String name) {
        if (this.singletonObjects.containsKey(name) || this.resourceTypes.containsKey(name)) {
            return true;
        }
        try {
            this.doGetType(name);
            return true;
        }
        catch (NamingException ex) {
            return false;
        }
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return this.shareableResources.contains(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return !this.shareableResources.contains(name);
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = this.getType(name);
        return type != null && typeToMatch.isAssignableFrom(type);
    }

    @Override
    public boolean isTypeMatch(String name, @Nullable Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        Class<?> type = this.getType(name);
        return typeToMatch == null || type != null && typeToMatch.isAssignableFrom(type);
    }

    @Override
    @Nullable
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return this.getType(name, true);
    }

    @Override
    @Nullable
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
        try {
            return this.doGetType(name);
        }
        catch (NameNotFoundException ex) {
            throw new NoSuchBeanDefinitionException(name, "not found in JNDI environment");
        }
        catch (NamingException ex) {
            return null;
        }
    }

    @Override
    public String[] getAliases(String name) {
        return new String[0];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> T doGetSingleton(String name, @Nullable Class<T> requiredType) throws NamingException {
        Map<String, Object> map = this.singletonObjects;
        synchronized (map) {
            Object singleton = this.singletonObjects.get(name);
            if (singleton != null) {
                if (requiredType != null && !requiredType.isInstance(singleton)) {
                    throw new TypeMismatchNamingException(this.convertJndiName(name), requiredType, singleton.getClass());
                }
                return (T)singleton;
            }
            T jndiObject = this.lookup(name, requiredType);
            this.singletonObjects.put(name, jndiObject);
            return jndiObject;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Class<?> doGetType(String name) throws NamingException {
        if (this.isSingleton(name)) {
            return this.doGetSingleton(name, null).getClass();
        }
        Map<String, Class<?>> map = this.resourceTypes;
        synchronized (map) {
            Class<?> type = this.resourceTypes.get(name);
            if (type == null) {
                type = this.lookup(name, null).getClass();
                this.resourceTypes.put(name, type);
            }
            return type;
        }
    }
}

