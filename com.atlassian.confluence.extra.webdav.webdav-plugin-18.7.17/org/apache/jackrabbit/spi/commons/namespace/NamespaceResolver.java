/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.namespace;

import javax.jcr.NamespaceException;

public interface NamespaceResolver {
    public String getURI(String var1) throws NamespaceException;

    public String getPrefix(String var1) throws NamespaceException;
}

