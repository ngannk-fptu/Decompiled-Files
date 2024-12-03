/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public interface MacroMigration {
    public MacroDefinition migrate(MacroDefinition var1, ConversionContext var2);
}

