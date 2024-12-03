/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.namespace;

public interface NamespaceListener {
    public void namespaceRemapped(String var1, String var2, String var3);

    public void namespaceAdded(String var1, String var2);

    public void namespaceRemoved(String var1);
}

