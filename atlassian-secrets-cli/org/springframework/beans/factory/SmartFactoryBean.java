/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.beans.factory.FactoryBean;

public interface SmartFactoryBean<T>
extends FactoryBean<T> {
    default public boolean isPrototype() {
        return false;
    }

    default public boolean isEagerInit() {
        return false;
    }
}

