/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import org.apache.commons.configuration2.ex.ConfigurationException;

@FunctionalInterface
public interface ConfigurationConsumer<T> {
    public void accept(T var1) throws ConfigurationException;
}

