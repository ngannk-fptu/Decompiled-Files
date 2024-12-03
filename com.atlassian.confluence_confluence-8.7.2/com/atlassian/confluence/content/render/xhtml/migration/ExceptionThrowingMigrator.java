/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.renderer.RenderContext;

public interface ExceptionThrowingMigrator {
    public String migrate(String var1, RenderContext var2) throws XhtmlException;
}

