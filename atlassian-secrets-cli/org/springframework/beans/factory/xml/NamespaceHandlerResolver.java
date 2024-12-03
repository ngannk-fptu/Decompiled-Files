/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface NamespaceHandlerResolver {
    @Nullable
    public NamespaceHandler resolve(String var1);
}

