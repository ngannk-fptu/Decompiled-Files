/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery;

import org.apache.commons.discovery.ResourceNameIterator;

public interface ResourceNameDiscover {
    public ResourceNameIterator findResourceNames(String var1);

    public ResourceNameIterator findResourceNames(ResourceNameIterator var1);
}

