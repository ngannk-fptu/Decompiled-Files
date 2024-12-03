/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

@FunctionalInterface
public interface ObjectFactory<T> {
    public T getObject() throws BeansException;
}

