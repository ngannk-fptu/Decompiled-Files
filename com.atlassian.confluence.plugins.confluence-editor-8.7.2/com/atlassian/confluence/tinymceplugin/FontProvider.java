/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.tinymceplugin;

import java.awt.Font;

public interface FontProvider {
    public Font getFirstAvailableFont(String ... var1);

    public Font getConfluenceFont(String var1);
}

