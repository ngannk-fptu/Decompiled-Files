/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.viewfile.macro.marshaller;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.viewfile.macro.marshaller.FilePlaceholderMarshaller;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.user.User;
import java.lang.invoke.CallSite;
import java.util.HashMap;
import java.util.Map;

public class UnknownAttachmentFilePlaceholderMarshaller
implements FilePlaceholderMarshaller {
    private static final String UNKNOWN_ATTACHMENT_PLACEHOLDER = "/plugins/servlet/confluence/placeholder/unknown-attachment?";
    private final LocaleManager localeManager;
    private final ContextPathHolder contextPathHolder;
    private final SoyTemplateRenderer soyTemplateRenderer;

    public UnknownAttachmentFilePlaceholderMarshaller(LocaleManager localeManager, ContextPathHolder contextPathHolder, SoyTemplateRenderer soyTemplateRenderer) {
        this.localeManager = localeManager;
        this.contextPathHolder = contextPathHolder;
        this.soyTemplateRenderer = soyTemplateRenderer;
    }

    @Override
    public ImagePlaceholder getImagePlaceholder(Attachment attachment, Map<String, String> params) {
        return new DefaultImagePlaceholder(this.getUrlForUnknownAttachment(), false, null);
    }

    @Override
    public boolean handles(Attachment attachment) {
        return attachment == null;
    }

    @Override
    public Streamable getRenderedContentStreamable(Attachment attachment, Map<String, String> params, ConversionContext conversionContext) {
        HashMap<String, CallSite> data = new HashMap<String, CallSite>();
        data.put("placeholderSrc", (CallSite)((Object)(this.contextPathHolder.getContextPath() + this.getUrlForUnknownAttachment())));
        try {
            return Streamables.from((String)this.soyTemplateRenderer.render("com.atlassian.confluence.plugins.confluence-view-file-macro:view-file-macro-embedded-file-view-soy-resources", "Confluence.ViewFileMacro.Templates.embeddedUnknownFile", data));
        }
        catch (SoyException exp) {
            throw new RuntimeException("Cannot render unknown file placeholder", exp);
        }
    }

    private String getUrlForUnknownAttachment() {
        return UNKNOWN_ATTACHMENT_PLACEHOLDER + this.getCachingParameters();
    }

    private String getCachingParameters() {
        return "locale=" + this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()).toString();
    }
}

