/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.spring.container.ContainerManager;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RichTextMacroMigration
implements MacroMigration {
    private static final Logger log = LoggerFactory.getLogger(RichTextMacroMigration.class);
    private final MacroManager xhtmlMacroManager;

    public RichTextMacroMigration(MacroManager xhtmlMacroManager) {
        this.xhtmlMacroManager = xhtmlMacroManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext conversionContext) {
        String macroName = macroDefinition.getName();
        log.debug("Rich text migration for macro: {}", (Object)macroName);
        PageContext pageContext = conversionContext.getPageContext();
        ExceptionTolerantMigrator wikiToXhtmlMigrator = (ExceptionTolerantMigrator)ContainerManager.getComponent((String)"wikiToXhtmlMigrator");
        pageContext.pushRenderMode(RenderMode.ALL);
        MacroDefinition migratedMacroDefinition = new MacroDefinition(macroDefinition);
        migratedMacroDefinition.setStorageVersion("2");
        ArrayList<RuntimeException> exceptions = new ArrayList<RuntimeException>();
        try {
            migratedMacroDefinition.setBody(RichTextMacroBody.withStorage(Streamables.from(wikiToXhtmlMigrator.migrate(Streamables.writeToString(macroDefinition.getStorageBodyStream()), pageContext, exceptions))));
        }
        finally {
            pageContext.popRenderMode();
        }
        if (!exceptions.isEmpty() && log.isDebugEnabled()) {
            log.debug("Error migrating the body of a '" + macroName + "' macro on '" + pageContext.getEntity(), (Throwable)exceptions.get(0));
        }
        if (this.getMacroOutputType(macroName) == Macro.OutputType.INLINE && "inline".equals(conversionContext.getPropertyAsString("macro-declaration-position"))) {
            migratedMacroDefinition.setParameter("atlassian-macro-output-type", Macro.OutputType.INLINE.name());
            migratedMacroDefinition.setTypedParameter("atlassian-macro-output-type", Macro.OutputType.INLINE.name());
        }
        return migratedMacroDefinition;
    }

    private Macro.OutputType getMacroOutputType(String macroName) {
        Macro xhtmlMacro = this.xhtmlMacroManager.getMacroByName(macroName);
        if (xhtmlMacro == null) {
            throw new RuntimeException("XHTML version of macro " + macroName + " not found. It is required for migration.");
        }
        return xhtmlMacro.getOutputType();
    }
}

