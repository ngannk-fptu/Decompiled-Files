/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ui.context;

import org.springframework.lang.Nullable;
import org.springframework.ui.context.Theme;

public interface ThemeSource {
    @Nullable
    public Theme getTheme(String var1);
}

