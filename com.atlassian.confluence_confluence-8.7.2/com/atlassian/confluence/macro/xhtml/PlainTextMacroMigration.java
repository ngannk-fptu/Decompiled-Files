/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlainTextMacroMigration
implements MacroMigration {
    private static final Logger log = LoggerFactory.getLogger(PlainTextMacroMigration.class);

    @Override
    public MacroDefinition migrate(MacroDefinition macro, ConversionContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Plain text migration for macro: " + macro.getName());
        }
        return macro;
    }
}

