/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.impl.common.PrefixResolver;

public interface NamespaceManager
extends PrefixResolver {
    public String find_prefix_for_nsuri(String var1, String var2);
}

