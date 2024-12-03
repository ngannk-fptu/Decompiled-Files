/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.contributors.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.contributors.macro.BaseContributionMacro;
import com.atlassian.confluence.contributors.macro.ContributorsMacroClientSideRenderer;
import com.atlassian.confluence.contributors.macro.ContributorsMacroHelper;
import com.atlassian.confluence.contributors.macro.ContributorsMacroRenderer;
import com.atlassian.confluence.contributors.macro.ContributorsMacroServerSideRenderer;
import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.confluence.contributors.search.PageSearcher;
import com.atlassian.confluence.contributors.util.PageProcessor;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;

public class ContributorsMacro
extends BaseContributionMacro {
    private final PageBuilderService pageBuilderService;
    private final TemplateRenderer templateRenderer;
    private final EventPublisher eventPublisher;
    private final ContributorsMacroHelper macroHelper;

    public ContributorsMacro(@ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @Qualifier(value="pageSearcher") PageSearcher pageSearcher, PageProcessor pageProcessor, @ComponentImport PageBuilderService pageBuilderService, @ComponentImport TemplateRenderer templateRenderer, @ComponentImport EventPublisher eventPublisher) {
        super(localeManager, i18NBeanFactory);
        this.pageBuilderService = pageBuilderService;
        this.templateRenderer = templateRenderer;
        this.eventPublisher = eventPublisher;
        this.macroHelper = new ContributorsMacroHelper(pageProcessor, pageSearcher);
    }

    public Streamable executeToStream(Map<String, String> macroParameters, Streamable macroBody, ConversionContext conversionContext) throws MacroExecutionException {
        ContentEntityObject contentObject = conversionContext.getEntity();
        if (contentObject instanceof AbstractPage) {
            return this.execute(conversionContext, new MacroParameterModel(macroParameters, (SpaceContentEntityObject)contentObject));
        }
        if (contentObject instanceof Comment) {
            ContentEntityObject commentContainer = ((Comment)contentObject).getContainer();
            return this.execute(conversionContext, new MacroParameterModel(macroParameters, (SpaceContentEntityObject)commentContainer));
        }
        if (contentObject instanceof Draft) {
            return this.error("error.preview");
        }
        return this.error("error.unsupportedcontent");
    }

    private Streamable error(String errorKey) {
        return Streamables.from((String)RenderUtils.blockError((String)this.getText(errorKey, Collections.singletonList(this.getText("com.atlassian.confluence.contributors.contributors.label"))), (String)""));
    }

    private Streamable execute(ConversionContext conversionContext, MacroParameterModel macroParameterModel) {
        this.pageBuilderService.assembler().resources().requireWebResource("com.atlassian.confluence.contributors:contributors-web-resources");
        ContributorsMacroRenderer renderer = this.selectRenderer(conversionContext);
        return writer -> renderer.render(writer, macroParameterModel);
    }

    private ContributorsMacroRenderer selectRenderer(ConversionContext conversionContext) {
        boolean isDisplayOutputType = ConversionContextOutputType.DISPLAY.toString().equalsIgnoreCase(conversionContext.getOutputType());
        return !isDisplayOutputType ? new ContributorsMacroServerSideRenderer(this.templateRenderer, this.macroHelper, this.eventPublisher) : new ContributorsMacroClientSideRenderer(this.templateRenderer);
    }
}

