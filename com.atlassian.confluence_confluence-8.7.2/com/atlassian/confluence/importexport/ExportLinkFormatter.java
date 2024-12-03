/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.links.Link
 */
package com.atlassian.confluence.importexport;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.links.Link;

@Deprecated
public interface ExportLinkFormatter {
    public boolean isFormatSupported(Link var1);

    public String format(Link var1, PageContext var2);
}

