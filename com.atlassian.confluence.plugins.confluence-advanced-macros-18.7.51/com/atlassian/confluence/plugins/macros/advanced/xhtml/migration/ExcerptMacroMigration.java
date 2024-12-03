/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPropertyManager
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 */
package com.atlassian.confluence.plugins.macros.advanced.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public class ExcerptMacroMigration
implements MacroMigration {
    private ContentPropertyManager contentPropertyManager;
    private MacroMigration richTextMacroMigration;

    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext conversionContext) {
        ContentEntityObject entity;
        PageContext pageContext = conversionContext != null ? conversionContext.getPageContext() : null;
        ContentEntityObject contentEntityObject = entity = pageContext != null ? pageContext.getEntity() : null;
        if (entity != null && this.contentPropertyManager.getStringProperty(entity, "confluence.excerpt") != null) {
            this.contentPropertyManager.removeProperty(entity, "confluence.excerpt");
        }
        return this.richTextMacroMigration.migrate(macroDefinition, conversionContext);
    }

    public void setContentPropertyManager(ContentPropertyManager contentPropertyManager) {
        this.contentPropertyManager = contentPropertyManager;
    }

    public void setRichTextMacroMigration(MacroMigration richTextMacroMigration) {
        this.richTextMacroMigration = richTextMacroMigration;
    }
}

