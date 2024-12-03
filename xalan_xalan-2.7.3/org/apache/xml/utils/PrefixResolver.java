/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import org.w3c.dom.Node;

public interface PrefixResolver {
    public String getNamespaceForPrefix(String var1);

    public String getNamespaceForPrefix(String var1, Node var2);

    public String getBaseIdentifier();

    public boolean handlesNullPrefixes();
}

