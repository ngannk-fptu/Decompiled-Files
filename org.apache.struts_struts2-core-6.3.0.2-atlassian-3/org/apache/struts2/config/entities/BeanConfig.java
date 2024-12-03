/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.config.entities;

import com.opensymphony.xwork2.inject.Scope;

public class BeanConfig {
    private final Class<?> clazz;
    private final String name;
    private final Class<?> type;
    private final Scope scope;
    private final boolean onlyStatic;
    private final boolean optional;

    public BeanConfig(Class<?> clazz) {
        this(clazz, "default");
    }

    public BeanConfig(Class<?> clazz, String name) {
        this(clazz, name, clazz);
    }

    public BeanConfig(Class<?> clazz, String name, Class<?> type) {
        this(clazz, name, type, Scope.SINGLETON, false, false);
    }

    public BeanConfig(Class<?> clazz, String name, Class<?> type, Scope scope, boolean onlyStatic, boolean optional) {
        this.clazz = clazz;
        this.name = name;
        this.type = type;
        this.scope = scope;
        this.onlyStatic = onlyStatic;
        this.optional = optional;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public Scope getScope() {
        return this.scope;
    }

    public boolean isOnlyStatic() {
        return this.onlyStatic;
    }

    public boolean isOptional() {
        return this.optional;
    }
}

