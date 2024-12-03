/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.ui.context;

import org.springframework.lang.Nullable;
import org.springframework.ui.context.Theme;

public interface ThemeSource {
    @Nullable
    public Theme getTheme(String var1);
}

