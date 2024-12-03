/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.LocationUtils;
import java.util.LinkedHashMap;

public class LocatableFactory<T>
extends Located
implements Factory<T> {
    private Class implementation;
    private Class type;
    private String name;
    private Scope scope;

    public LocatableFactory(String name, Class type, Class implementation, Scope scope, Object location) {
        this.implementation = implementation;
        this.type = type;
        this.name = name;
        this.scope = scope;
        this.setLocation(LocationUtils.getLocation(location));
    }

    @Override
    public T create(Context context) {
        Object obj = context.getContainer().inject(this.implementation);
        return obj;
    }

    @Override
    public Class<? extends T> type() {
        return this.implementation;
    }

    public String toString() {
        String fields = new LinkedHashMap<String, Object>(){
            {
                this.put("type", LocatableFactory.this.type);
                this.put("name", LocatableFactory.this.name);
                this.put("implementation", LocatableFactory.this.implementation);
                this.put("scope", LocatableFactory.this.scope);
            }
        }.toString();
        StringBuilder sb = new StringBuilder(fields);
        sb.append(super.toString());
        sb.append(" defined at ");
        sb.append(this.getLocation().toString());
        return sb.toString();
    }
}

