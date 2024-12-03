/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.embedded.EmbeddedResourceRendererManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.AttachmentMimeTypeTranslator
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.plugins.macros.multimedia;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.macros.multimedia.OldMultimediaMacro;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceRendererManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.AttachmentMimeTypeTranslator;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class MultimediaMacro
implements Macro {
    private final OldMultimediaMacro oldMultimediaMacro;

    public MultimediaMacro(EmbeddedResourceRendererManager embeddedResourceRendererManager, PageManager pageManager, AttachmentManager attachmentManager, SettingsManager settingsManager, AttachmentMimeTypeTranslator mimeTypeTranslator) {
        this.oldMultimediaMacro = new OldMultimediaMacro(embeddedResourceRendererManager, pageManager, attachmentManager, settingsManager, mimeTypeTranslator);
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        try {
            return this.oldMultimediaMacro.execute(parameters, body, (RenderContext)(context == null ? null : context.getPageContext()));
        }
        catch (MacroException e) {
            throw new MacroExecutionException((Throwable)e);
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }
}

