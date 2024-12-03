/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import org.w3c.dom.Element;

public interface ServiceConfigurationParser<T> {
    public T parse(Element var1);
}

