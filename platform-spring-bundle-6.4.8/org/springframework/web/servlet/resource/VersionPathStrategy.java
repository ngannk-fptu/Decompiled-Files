/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.resource;

import org.springframework.lang.Nullable;

public interface VersionPathStrategy {
    @Nullable
    public String extractVersion(String var1);

    public String removeVersion(String var1, String var2);

    public String addVersion(String var1, String var2);
}

