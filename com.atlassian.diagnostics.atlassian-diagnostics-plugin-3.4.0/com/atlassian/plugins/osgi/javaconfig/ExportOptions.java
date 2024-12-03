/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugins.osgi.javaconfig;

import com.atlassian.annotations.PublicApi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@PublicApi
public class ExportOptions {
    private final List<Class<?>> serviceInterfaces = new ArrayList();
    private final Map<String, Object> properties = new HashMap<String, Object>();

    public static ExportOptions as(Class<?> firstInterface, Class<?> ... otherInterfaces) {
        return new ExportOptions(firstInterface, otherInterfaces);
    }

    private ExportOptions(Class<?> firstInterface, Class<?> ... otherInterfaces) {
        this.addServiceInterface(firstInterface);
        Arrays.stream(otherInterfaces).forEach(this::addServiceInterface);
    }

    private void addServiceInterface(Class<?> serviceInterface) {
        Objects.requireNonNull(serviceInterface);
        if (!serviceInterface.isInterface() || serviceInterface.isEnum()) {
            throw new IllegalArgumentException(serviceInterface.getName() + " is not an interface");
        }
        this.serviceInterfaces.add(serviceInterface);
    }

    public ExportOptions withProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public Class<?>[] getInterfaces() {
        return new ArrayList(this.serviceInterfaces).toArray(new Class[0]);
    }

    public Map<String, Object> getProperties() {
        return new HashMap<String, Object>(this.properties);
    }
}

