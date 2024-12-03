/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.ui.context;

import org.springframework.lang.Nullable;
import org.springframework.ui.context.ThemeSource;

public interface HierarchicalThemeSource
extends ThemeSource {
    public void setParentThemeSource(@Nullable ThemeSource var1);

    @Nullable
    public ThemeSource getParentThemeSource();
}

