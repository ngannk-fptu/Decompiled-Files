/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import javax.annotation.Nonnull;

interface HtmlTagFormatter {
    @Nonnull
    public String format(@Nonnull ResourceUrls var1);

    public boolean matches(@Nonnull String var1);
}

