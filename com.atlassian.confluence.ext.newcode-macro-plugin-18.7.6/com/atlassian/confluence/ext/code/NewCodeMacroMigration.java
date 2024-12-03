/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 */
package com.atlassian.confluence.ext.code;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public class NewCodeMacroMigration
implements MacroMigration {
    public MacroDefinition migrate(MacroDefinition macro, ConversionContext context) {
        macro.setName("code");
        return macro;
    }
}

