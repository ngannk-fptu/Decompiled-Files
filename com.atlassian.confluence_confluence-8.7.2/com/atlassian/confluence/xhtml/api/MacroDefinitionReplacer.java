/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public interface MacroDefinitionReplacer {
    public String replace(MacroDefinition var1) throws XhtmlException;
}

