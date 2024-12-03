/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.ext;

import org.apache.xml.security.stax.ext.ResourceResolver;

public interface ResourceResolverLookup {
    public ResourceResolverLookup canResolve(String var1, String var2);

    public ResourceResolver newInstance(String var1, String var2);
}

