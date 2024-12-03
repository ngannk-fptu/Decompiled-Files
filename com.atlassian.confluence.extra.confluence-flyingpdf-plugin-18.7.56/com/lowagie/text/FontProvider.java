/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text;

import com.lowagie.text.Font;
import java.awt.Color;
import javax.annotation.Nullable;

public interface FontProvider {
    public boolean isRegistered(String var1);

    public Font getFont(@Nullable String var1, String var2, boolean var3, float var4, int var5, @Nullable Color var6);
}

