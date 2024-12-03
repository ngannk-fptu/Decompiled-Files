/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.tools;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.discovery.DiscoveryException;
import org.apache.commons.discovery.tools.ClassUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SPInterface<T> {
    private final Class<T> spi;
    private final String propertyName;
    private final Class<?>[] paramClasses;
    private final Object[] params;

    public static <T> SPInterface<T> newSPInterface(Class<T> provider) {
        return SPInterface.newSPInterface(provider, provider.getName());
    }

    public static <T> SPInterface<T> newSPInterface(Class<T> provider, String propertyName) {
        return new SPInterface<T>(provider, propertyName);
    }

    public static <T> SPInterface<T> newSPInterface(Class<T> provider, Class<?>[] constructorParamClasses, Object[] constructorParams) {
        return SPInterface.newSPInterface(provider, provider.getName(), constructorParamClasses, constructorParams);
    }

    public static <T> SPInterface<T> newSPInterface(Class<T> provider, String propertyName, Class<?>[] constructorParamClasses, Object[] constructorParams) {
        return new SPInterface<T>(provider, propertyName, constructorParamClasses, constructorParams);
    }

    public SPInterface(Class<T> provider) {
        this(provider, provider.getName());
    }

    public SPInterface(Class<T> spi, String propertyName) {
        this.spi = spi;
        this.propertyName = propertyName;
        this.paramClasses = null;
        this.params = null;
    }

    public SPInterface(Class<T> provider, Class<?>[] constructorParamClasses, Object[] constructorParams) {
        this(provider, provider.getName(), constructorParamClasses, constructorParams);
    }

    public SPInterface(Class<T> spi, String propertyName, Class<?>[] constructorParamClasses, Object[] constructorParams) {
        this.spi = spi;
        this.propertyName = propertyName;
        this.paramClasses = constructorParamClasses;
        this.params = constructorParams;
    }

    public String getSPName() {
        return this.spi.getName();
    }

    public Class<T> getSPClass() {
        return this.spi;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public <S extends T> S newInstance(Class<S> impl) throws DiscoveryException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        this.verifyAncestory(impl);
        return ClassUtils.newInstance(impl, this.paramClasses, this.params);
    }

    public <S extends T> void verifyAncestory(Class<S> impl) {
        ClassUtils.verifyAncestory(this.spi, impl);
    }
}

