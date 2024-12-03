/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.themes;

import java.util.Optional;
import javax.annotation.Nullable;

public interface ThemeKeyDao {
    public Optional<String> getGlobalThemeKey();

    public void setGlobalThemeKey(String var1);

    public Optional<String> getSpaceThemeKey(@Nullable String var1);

    public void setSpaceThemeKey(String var1, String var2);
}

