/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.attachments;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ImagePreviewRenderer {
    private static final String VIEW_FILE_WIDTH = "310";
    private static final String VIEW_FILE_HEIGHT = "250";
    private static final Set<String> VIEW_FILE_MACRO_EXTENSIONS = ImmutableSet.of((Object)"ppt", (Object)"pptx", (Object)"pdf");
    private static final Set<String> IMAGE_EXTENSIONS = ImmutableSet.of((Object)"png", (Object)"jpg", (Object)"jpeg", (Object)"gif");
    private final VelocityHelperService velocityHelperService;
    private final XhtmlContent xhtmlContent;

    public ImagePreviewRenderer(@ComponentImport VelocityHelperService velocityHelperService, @ComponentImport XhtmlContent xhtmlContent) {
        this.velocityHelperService = velocityHelperService;
        this.xhtmlContent = xhtmlContent;
    }

    public boolean willBeRendered(Attachment attachment) {
        String ext = this.getFileExtension(attachment);
        return VIEW_FILE_MACRO_EXTENSIONS.contains(ext) || IMAGE_EXTENSIONS.contains(ext);
    }

    public String render(Attachment attachment, ConversionContext conversionContext) throws XhtmlException {
        String ext = this.getFileExtension(attachment);
        if (VIEW_FILE_MACRO_EXTENSIONS.contains(ext)) {
            return this.renderViewFileMacro(attachment, conversionContext);
        }
        if (IMAGE_EXTENSIONS.contains(ext)) {
            return this.renderPreviewImage(attachment);
        }
        return "";
    }

    private String getFileExtension(Attachment attachment) {
        int index = attachment.getFileName().lastIndexOf(".");
        return attachment.getFileName().substring(index + 1).toLowerCase();
    }

    private String renderPreviewImage(Attachment attachment) {
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        contextMap.put("attachment", attachment);
        return this.velocityHelperService.getRenderedTemplate("templates/extra/attachments/imagepreview.vm", contextMap);
    }

    private String renderViewFileMacro(Attachment attachment, ConversionContext conversionContext) throws XhtmlException {
        MacroDefinitionBuilder builder = MacroDefinition.builder();
        builder.withName("viewfile");
        builder.withParameter("name", attachment.getFileName());
        builder.withParameter("width", VIEW_FILE_WIDTH);
        builder.withParameter("height", VIEW_FILE_HEIGHT);
        return this.xhtmlContent.convertMacroDefinitionToView(builder.build(), conversionContext);
    }
}

