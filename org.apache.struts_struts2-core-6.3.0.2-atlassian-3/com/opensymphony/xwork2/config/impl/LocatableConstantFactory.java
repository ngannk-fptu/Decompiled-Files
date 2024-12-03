/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.LocationUtils;

public class LocatableConstantFactory<T>
extends Located
implements Factory {
    T constant;

    public LocatableConstantFactory(T constant, Object location) {
        this.constant = constant;
        this.setLocation(LocationUtils.getLocation(location));
    }

    @Override
    public T create(Context ignored) {
        return this.constant;
    }

    public Class type() {
        return this.constant.getClass();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" defined at ");
        sb.append(this.getLocation().toString());
        return sb.toString();
    }
}

