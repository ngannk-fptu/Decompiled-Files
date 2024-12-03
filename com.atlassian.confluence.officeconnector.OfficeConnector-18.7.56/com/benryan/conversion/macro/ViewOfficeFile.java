/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.ResourceAware
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.thumbnail.Dimensions
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.apache.commons.lang3.StringUtils
 */
package com.benryan.conversion.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.ResourceAware;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.thumbnail.Dimensions;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import com.benryan.components.ConverterSemaphore;
import com.benryan.conversion.ConverterFactory;
import com.benryan.conversion.ConverterHelper;
import com.benryan.conversion.macro.ConverterMacro;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ViewOfficeFile
implements Macro,
EditorImagePlaceholder,
ResourceAware {
    private static final int HEIGHT = 300;
    private static final int WIDTH = 380;
    private final PluginAccessor pluginAccessor;
    private final AttachmentManager attachmentManager;
    private final WebResourceManager webResourceManager;
    private final ConverterSemaphore converterSemaphore;
    private final ConverterFactory converterFactory;
    private final ConverterHelper converterHelper;
    private final TemplateRenderer templateRenderer;
    private String resourcePath;

    public ViewOfficeFile(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport AttachmentManager attachmentManager, @ComponentImport WebResourceManager webResourceManager, ConverterSemaphore converterSemaphore, ConverterFactory converterFactory, ConverterHelper converterHelper, TemplateRenderer templateRenderer) {
        this.pluginAccessor = pluginAccessor;
        this.attachmentManager = attachmentManager;
        this.webResourceManager = webResourceManager;
        this.converterSemaphore = converterSemaphore;
        this.converterFactory = converterFactory;
        this.converterHelper = converterHelper;
        this.templateRenderer = templateRenderer;
    }

    public String execute(Map<String, String> params, String body, ConversionContext conversionContext) throws MacroExecutionException {
        ConverterMacro macro = new ConverterMacro(this.pluginAccessor, this.attachmentManager, this.webResourceManager, this.converterSemaphore, this.converterFactory, this.converterHelper, this.templateRenderer);
        try {
            return macro.execute(params, body, (RenderContext)(conversionContext != null ? conversionContext.getPageContext() : null));
        }
        catch (MacroException ex) {
            throw new MacroExecutionException(ex.getMessage(), ex.getCause());
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> params, ConversionContext conversionContext) {
        String name = params.get("name");
        if (name == null) {
            name = params.get("0");
        }
        if (StringUtils.isBlank((CharSequence)name)) {
            return new DefaultImagePlaceholder(this.resourcePath + "/images/generic-editor-placeholder-small.png", new Dimensions(380, 300), true);
        }
        int dotIdx = name.lastIndexOf(46);
        String type = null;
        if (dotIdx != -1) {
            type = name.substring(dotIdx + 1).toLowerCase();
        }
        if (type == null) {
            return new DefaultImagePlaceholder(this.resourcePath + "/images/generic-editor-placeholder-small.png", new Dimensions(380, 300), true);
        }
        if (type.startsWith("doc")) {
            return new DefaultImagePlaceholder(this.resourcePath + "/images/word-editor-placeholder.png", new Dimensions(380, 300), true);
        }
        if (type.startsWith("xls")) {
            return new DefaultImagePlaceholder(this.resourcePath + "/images/excel-editor-placeholder.png", new Dimensions(380, 300), true);
        }
        if (type.startsWith("ppt")) {
            return new DefaultImagePlaceholder(this.resourcePath + "/images/powerpoint-editor-placeholder.png", new Dimensions(380, 300), true);
        }
        if (type.equals("pdf")) {
            return new DefaultImagePlaceholder(this.resourcePath + "/images/pdf-editor-placeholder.png", new Dimensions(380, 300), true);
        }
        return new DefaultImagePlaceholder(this.resourcePath + "/images/generic-editor-placeholder-small.png", new Dimensions(380, 300), true);
    }
}

