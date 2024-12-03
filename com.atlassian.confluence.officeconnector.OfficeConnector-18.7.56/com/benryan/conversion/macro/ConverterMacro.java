/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.confluence.macro.StreamableMacroAdapter
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.apache.commons.collections4.SetUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.benryan.conversion.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.macro.StreamableMacroAdapter;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.benryan.components.AutoCloseableSemaphore;
import com.benryan.components.ConverterSemaphore;
import com.benryan.conversion.ConverterFactory;
import com.benryan.conversion.ConverterHelper;
import com.benryan.conversion.macro.ConverterMacroClientSideRenderer;
import com.benryan.conversion.macro.ConverterMacroRenderer;
import com.benryan.conversion.macro.ConverterMacroServerSideRenderer;
import com.benryan.conversion.macro.MacroParameters;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConverterMacro
extends BaseMacro
implements StreamableMacro {
    public static final Logger log = LoggerFactory.getLogger(ConverterMacro.class);
    public static final Set<String> SUPP_LOWERCASE_EXT = SetUtils.unmodifiableSet((Object[])new String[]{"pdf", "pptx", "ppt"});
    protected final PluginAccessor pluginAccessor;
    protected final AttachmentManager attachmentManager;
    private final WebResourceManager webResourceManager;
    private final ConverterSemaphore converterSemaphore;
    private final ConverterFactory converterFactory;
    private final ConverterHelper converterHelper;
    private final TemplateRenderer templateRenderer;

    public ConverterMacro(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport AttachmentManager attachmentManager, @ComponentImport WebResourceManager webResourceManager, ConverterSemaphore converterSemaphore, ConverterFactory converterFactory, ConverterHelper converterHelper, @ComponentImport TemplateRenderer templateRenderer) {
        this.pluginAccessor = pluginAccessor;
        this.attachmentManager = attachmentManager;
        this.webResourceManager = webResourceManager;
        this.converterSemaphore = converterSemaphore;
        this.converterFactory = converterFactory;
        this.converterHelper = converterHelper;
        this.templateRenderer = templateRenderer;
    }

    public String execute(Map args, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)args, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    private Streamable executeConverter(Map<String, String> args, ConversionContext conversionContext) throws MacroExecutionException {
        try {
            ConverterMacroRenderer renderer;
            boolean useServerSideRenderer;
            Map<String, Object> argsMap = this.converterHelper.validateArguments(args, conversionContext);
            String typeName = (String)argsMap.get("type");
            boolean isDisplayOutputType = "display".equalsIgnoreCase(conversionContext.getOutputType());
            boolean isPageGadgetOutput = conversionContext.getOutputType().equals("page_gadget");
            boolean bl = useServerSideRenderer = isPageGadgetOutput || !isDisplayOutputType || SUPP_LOWERCASE_EXT.contains(typeName.toLowerCase());
            if (useServerSideRenderer) {
                renderer = new ConverterMacroServerSideRenderer(new MacroParameters(argsMap), this.converterFactory.create(typeName));
                this.webResourceManager.requireResource("com.atlassian.confluence.extra.officeconnector:slide-viewer-resources");
            } else {
                HashMap<String, String> clientSideArgs = new HashMap<String, String>(args);
                clientSideArgs.put("pageID", (String)argsMap.get("pageID"));
                renderer = new ConverterMacroClientSideRenderer(this.templateRenderer, new MacroParameters(clientSideArgs));
            }
            return renderer::render;
        }
        catch (Exception e) {
            throw new MacroExecutionException((Throwable)e);
        }
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public boolean hasBody() {
        return false;
    }

    public Streamable executeToStream(Map<String, String> args, Streamable streamable, ConversionContext conversionContext) throws MacroExecutionException {
        return this.executeConverter(args, conversionContext);
    }

    public String execute(Map<String, String> args, String body, ConversionContext conversionContext) throws MacroExecutionException {
        try (AutoCloseableSemaphore semaphore = this.converterSemaphore.acquire();){
            String string = StreamableMacroAdapter.executeFromStream((StreamableMacro)this, args, (String)body, (ConversionContext)conversionContext);
            return string;
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.RICH_TEXT;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }
}

