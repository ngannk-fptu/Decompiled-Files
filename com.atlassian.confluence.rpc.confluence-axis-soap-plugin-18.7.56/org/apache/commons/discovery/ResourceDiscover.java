/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery;

import org.apache.commons.discovery.ResourceIterator;
import org.apache.commons.discovery.ResourceNameDiscover;
import org.apache.commons.discovery.ResourceNameIterator;

public interface ResourceDiscover
extends ResourceNameDiscover {
    public ResourceIterator findResources(String var1);

    public ResourceIterator findResources(ResourceNameIterator var1);
}

