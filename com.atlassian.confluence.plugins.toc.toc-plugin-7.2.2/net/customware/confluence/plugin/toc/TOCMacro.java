/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.customware.confluence.plugin.toc;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import net.customware.confluence.plugin.toc.AbstractTOCMacro;
import net.customware.confluence.plugin.toc.ClientTocMacroTemplateModel;
import net.customware.confluence.plugin.toc.StaxDocumentOutlineCreator;
import net.customware.confluence.plugin.toc.TocMacroImplementationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TOCMacro
extends AbstractTOCMacro
implements StreamableMacro {
    private static final Logger log = LoggerFactory.getLogger(TOCMacro.class);
    private static final String SOY_TEMPLATES_MODULE_NAME = "org.randombits.confluence.toc:server-soy-templates";
    private static final String CLIENT_IMPL_CONTAINER_TEMPLATE = "Confluence.Plugins.TableOfContents.Server.clientSideTocContainer.soy";
    private final XhtmlContent xhtmlContent;
    private final PageBuilderService pageBuilderService;
    private final TemplateRenderer templateRenderer;
    private final EventPublisher eventPublisher;

    public TOCMacro(StaxDocumentOutlineCreator staxDocumentOutlineCreator, XhtmlContent xhtmlContent, HtmlToXmlConverter htmlToXmlConverter, SettingsManager settingsManager, LocaleManager localeManager, I18NBeanFactory i18nBeanFactory, PageBuilderService pageBuilderService, TemplateRenderer templateRenderer, EventPublisher eventPublisher) {
        super(staxDocumentOutlineCreator, htmlToXmlConverter, settingsManager, localeManager, i18nBeanFactory, pageBuilderService);
        this.xhtmlContent = xhtmlContent;
        this.pageBuilderService = pageBuilderService;
        this.templateRenderer = templateRenderer;
        this.eventPublisher = eventPublisher;
    }

    public Streamable executeToStream(Map<String, String> macroParameters, Streamable macroBody, ConversionContext context) throws MacroExecutionException {
        TocMacroImplementationType implementationType = TocMacroImplementationType.selectImplementation(context);
        this.eventPublisher.publish((Object)implementationType.createEvent());
        switch (implementationType) {
            case SERVER: {
                return this.renderServerSideImplementation(macroParameters, macroBody, context);
            }
            case CLIENT: {
                return this.renderClientSideImplementation(macroParameters);
            }
        }
        throw new MacroExecutionException("Failed to select TOCMacro implementation");
    }

    private Streamable renderServerSideImplementation(Map<String, String> macroParameters, Streamable macroBody, ConversionContext context) throws MacroExecutionException {
        return Streamables.from((String)this.execute(macroParameters, Streamables.writeToString((Streamable)macroBody), context));
    }

    private Streamable renderClientSideImplementation(Map<String, String> macroParameters) {
        this.pageBuilderService.assembler().resources().requireContext("toc-macro-client-impl");
        ImmutableMap<String, Object> templateModel = ClientTocMacroTemplateModel.buildTemplateModel(macroParameters);
        return new Streamable((Map)templateModel){
            final /* synthetic */ Map val$templateModel;
            {
                this.val$templateModel = map;
            }

            public void writeTo(Writer writer) throws IOException {
                TOCMacro.this.templateRenderer.renderTo((Appendable)writer, TOCMacro.SOY_TEMPLATES_MODULE_NAME, TOCMacro.CLIENT_IMPL_CONTAINER_TEMPLATE, this.val$templateModel);
            }
        };
    }

    @Override
    protected String getContent(Map<String, String> parameters, String body, ConversionContext conversionContext) {
        ContentEntityObject contentEntity = conversionContext.getEntity();
        if (contentEntity == null) {
            log.warn("There was an error converting the preview content to view - content entity object was null.");
            return "";
        }
        try {
            return this.xhtmlContent.convertStorageToView(contentEntity.getBodyAsString(), conversionContext);
        }
        catch (Exception ex) {
            log.warn("There was an error converting the content for id " + contentEntity.getId() + " to storage format.", (Throwable)ex);
            return "";
        }
    }

    @Override
    protected String createOutput(Map<String, String> parameters, String body, String toc) {
        return toc;
    }

    @Override
    protected String getDefaultType() {
        return "list";
    }

    @Override
    protected String getUnprintableHtml(String body) {
        return "";
    }

    public boolean hasBody() {
        return false;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }
}

