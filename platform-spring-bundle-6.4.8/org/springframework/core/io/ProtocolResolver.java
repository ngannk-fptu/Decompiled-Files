/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface ProtocolResolver {
    @Nullable
    public Resource resolve(String var1, ResourceLoader var2);
}

