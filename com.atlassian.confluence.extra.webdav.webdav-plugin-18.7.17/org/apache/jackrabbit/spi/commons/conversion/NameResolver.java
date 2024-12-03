/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.conversion;

import javax.jcr.NamespaceException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.conversion.IllegalNameException;

public interface NameResolver {
    public Name getQName(String var1) throws IllegalNameException, NamespaceException;

    public String getJCRName(Name var1) throws NamespaceException;
}

