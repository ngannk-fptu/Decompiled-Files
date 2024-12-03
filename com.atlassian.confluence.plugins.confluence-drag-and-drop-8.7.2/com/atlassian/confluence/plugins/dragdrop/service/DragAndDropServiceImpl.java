/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink
 *  com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.xhtml.api.EditorFormatService
 *  com.atlassian.confluence.xhtml.api.EmbeddedImage
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.capabilities.api.CapabilityService
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.dragdrop.service;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.dragdrop.service.DragAndDropService;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.capabilities.api.CapabilityService;
import com.atlassian.renderer.RenderContext;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DragAndDropServiceImpl
implements DragAndDropService {
    private static final Set<String> SUPPORTED_IMAGE_EXTENSIONS = ImmutableSet.of((Object)"png", (Object)"gif", (Object)"jpg", (Object)"jpeg", (Object)"bmp");
    private static final Set<String> SUPPORTED_MULTIMEDIA_EXTENSIONS = ImmutableSet.of((Object)"rm", (Object)"ram", (Object)"mpeg", (Object)"mpg", (Object)"wmv", (Object)"wma", (Object[])new String[]{"mpeg", "swf", "mov", "mp4", "mp3", "avi"});
    private static final Set<String> ALWAYS_SUPPORTED_VFM_EXTENSIONS = ImmutableSet.of((Object)"mp3", (Object)"mp4");
    private static final Set<String> UNSUPPORTED_MULTIMEDIA_EXTENSIONS_IN_CLOUD = ImmutableSet.of((Object)"swf");
    private static final String VIEW_FILE_MACRO_KEY = "com.atlassian.confluence.plugins.confluence-view-file-macro";
    private EditorFormatService editorFormatService;
    private CapabilityService capabilityService;
    private PluginAccessor pluginAccessor;

    @Autowired
    public DragAndDropServiceImpl(@ComponentImport EditorFormatService editorFormatService, @ComponentImport CapabilityService capabilityService, @ComponentImport PluginAccessor pluginAccessor) {
        this.editorFormatService = editorFormatService;
        this.capabilityService = capabilityService;
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public String getAttachmentEditorHtml(String filename, ContentEntityObject container) throws Exception {
        boolean viewFileMacroEnable = this.pluginAccessor.isPluginEnabled(VIEW_FILE_MACRO_KEY);
        return this.getAttachmentEditorHtml(filename, container, viewFileMacroEnable, null);
    }

    @Override
    public String getAttachmentEditorHtml(String filename, ContentEntityObject container, boolean viewFileMacroEnable, String renderContentPlace) throws Exception {
        DefaultConversionContext context = new DefaultConversionContext((RenderContext)container.toPageContext());
        String extension = StringUtils.substringAfterLast((String)filename, (String)".").toLowerCase();
        AttachmentResourceIdentifier attachmentResourceIdentifier = new AttachmentResourceIdentifier(filename);
        if (SUPPORTED_IMAGE_EXTENSIONS.contains(extension)) {
            return this.editorFormatService.convertEmbeddedImageToEdit((EmbeddedImage)new DefaultEmbeddedImage((NamedResourceIdentifier)attachmentResourceIdentifier), (ConversionContext)context);
        }
        if (this.shouldUseViewFileMacro(viewFileMacroEnable, extension)) {
            return this.getMacroHtml("view-file", this.getVFMPlaceholderParams(filename, renderContentPlace), context, false);
        }
        String macro = this.getOldCompatibleMacroName(extension);
        if (macro != null) {
            return this.getMacroHtml(macro, Collections.singletonMap("name", filename), context);
        }
        return this.editorFormatService.convertLinkToEdit((Link)new DefaultLink((ResourceIdentifier)attachmentResourceIdentifier, null), (ConversionContext)context);
    }

    private String getOldCompatibleMacroName(String extension) {
        if (SUPPORTED_MULTIMEDIA_EXTENSIONS.contains(extension)) {
            return "multimedia";
        }
        if (extension.contains("doc")) {
            return "viewdoc";
        }
        if (extension.contains("xls")) {
            return "viewxls";
        }
        if (extension.contains("ppt")) {
            return "viewppt";
        }
        if (extension.contains("pdf")) {
            return "viewpdf";
        }
        return null;
    }

    private boolean shouldUseViewFileMacro(boolean viewFileMacroEnable, String extension) {
        if (viewFileMacroEnable) {
            boolean supportedMultimedia = SUPPORTED_MULTIMEDIA_EXTENSIONS.contains(extension);
            boolean alwaysSupportedInVfm = ALWAYS_SUPPORTED_VFM_EXTENSIONS.contains(extension);
            boolean supportedInCloud = !UNSUPPORTED_MULTIMEDIA_EXTENSIONS_IN_CLOUD.contains(extension);
            boolean cloudConversionsEnabled = this.capabilityService.getHostApplication().hasCapability("file.conversions.cloud");
            return cloudConversionsEnabled ? supportedInCloud : alwaysSupportedInVfm || !supportedMultimedia;
        }
        return false;
    }

    private String getMacroHtml(String macroName, Map<String, String> params, DefaultConversionContext conversionContext) throws XhtmlException {
        return this.getMacroHtml(macroName, params, conversionContext, true);
    }

    private String getMacroHtml(String macroName, Map<String, String> params, DefaultConversionContext conversionContext, boolean insertNewLineAfter) throws XhtmlException {
        return this.editorFormatService.convertMacroDefinitionToEdit(MacroDefinition.builder((String)macroName).withParameters(params).withStorageVersion("1").build(), (ConversionContext)conversionContext) + (insertNewLineAfter ? "<br/>" : "");
    }

    private Map<String, String> getVFMPlaceholderParams(String filename, String renderPlace) {
        String height = "comment".equals(renderPlace) ? "150" : "250";
        return ImmutableMap.of((Object)"name", (Object)filename, (Object)"height", (Object)height);
    }
}

