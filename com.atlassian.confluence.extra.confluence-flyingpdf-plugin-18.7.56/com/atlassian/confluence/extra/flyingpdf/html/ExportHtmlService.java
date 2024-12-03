/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import java.io.Writer;
import org.apache.velocity.context.Context;

public interface ExportHtmlService {
    public void renderTemplateWithoutSwallowingErrors(String var1, Context var2, Writer var3) throws Exception;
}

