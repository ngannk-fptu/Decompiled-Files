/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.InternalServerException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.renderer.RenderContext
 *  com.ctc.wstx.exc.WstxException
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.impl.ReadOnlyAndReadWriteTransactionConversionTemplate;
import com.atlassian.confluence.api.impl.service.content.ContentBodyConversionManager;
import com.atlassian.confluence.api.impl.service.content.factory.WebResourceDependenciesFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.WikiToXhtmlMigrator;
import com.atlassian.confluence.content.render.xhtml.view.embed.InlineStyleHelper;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugin.webresource.WebResourceDependenciesRecorder;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.user.AuthenticatedUserImpersonator;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.renderer.RenderContext;
import com.ctc.wstx.exc.WstxException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.transaction.PlatformTransactionManager;

public class ContentBodyConversionManagerImpl
implements ContentBodyConversionManager {
    private final FormatConverter formatConverter;
    private final ExceptionTolerantMigrator xhtmlRoundTripMigrator;
    private final WikiToXhtmlMigrator wikiToXhtmlMigrator;
    private final Renderer viewRenderer;
    private final InlineStyleHelper inlineStyleHelper;
    private final WebResourceDependenciesRecorder webResourceDependenciesRecorder;
    private final WebResourceDependenciesFactory resourceDependenciesFactory;
    private final PlatformTransactionManager transactionManager;

    public ContentBodyConversionManagerImpl(FormatConverter formatConverter, ExceptionTolerantMigrator xhtmlRoundTripMigrator, WikiToXhtmlMigrator wikiToXhtmlMigrator, Renderer viewRenderer, TemplateRenderer templateRenderer, I18NBeanFactory i18nBeanFactory, WebResourceDependenciesRecorder webResourceDependenciesRecorder, WebResourceDependenciesFactory resourceDependenciesFactory, PlatformTransactionManager transactionManager) {
        this.formatConverter = formatConverter;
        this.xhtmlRoundTripMigrator = xhtmlRoundTripMigrator;
        this.wikiToXhtmlMigrator = wikiToXhtmlMigrator;
        this.webResourceDependenciesRecorder = webResourceDependenciesRecorder;
        this.viewRenderer = viewRenderer;
        this.resourceDependenciesFactory = resourceDependenciesFactory;
        this.transactionManager = transactionManager;
        this.inlineStyleHelper = new InlineStyleHelper(templateRenderer, i18nBeanFactory);
    }

    private Iterable<String> computeWebresourceContextOverrides(ContentRepresentation toFormat) {
        ImmutableList.Builder contextOverrides = ImmutableList.builder();
        contextOverrides.add((Object)"viewcontent");
        if (ContentRepresentation.EDITOR.equals((Object)toFormat)) {
            contextOverrides.add((Object)"editor-content");
        }
        contextOverrides.add((Object)"preview");
        return contextOverrides.build();
    }

    @Override
    public Pair<String, Reference<WebResourceDependencies>> convert(ContentRepresentation fromFormat, String value, ContentRepresentation toFormat, ContentEntityObject ceo, Expansion ... expansions) {
        Pair<Option<String>, WebResourceDependenciesRecorder.RecordedResources> conversionResources = this.computeConversionResources(fromFormat, value, toFormat, ceo, expansions);
        Reference<WebResourceDependencies> resources = this.computeRequiredWebResources((WebResourceDependenciesRecorder.RecordedResources)conversionResources.right(), expansions);
        return Pair.pair((Object)((String)((Option)conversionResources.left()).getOrElse((Object)"")), resources);
    }

    private Pair<Option<String>, WebResourceDependenciesRecorder.RecordedResources> computeConversionResources(ContentRepresentation fromFormat, String value, ContentRepresentation toFormat, ContentEntityObject ceo, Expansion ... expansions) {
        ReadOnlyAndReadWriteTransactionConversionTemplate<Pair> template = new ReadOnlyAndReadWriteTransactionConversionTemplate<Pair>(this.transactionManager);
        return template.executeInReadWrite(() -> {
            try {
                return this.webResourceDependenciesRecorder.record(this.computeWebresourceContextOverrides(toFormat), this.computeWebresourceOverrides(), this.computeExcludedContexts(), this.computeExcludedResources(), false, () -> Option.option((Object)this.computeConvertedValue(fromFormat, toFormat, value, ceo)));
            }
            catch (ServiceException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ServiceException((Throwable)e);
            }
        });
    }

    private Iterable<String> computeExcludedResources() {
        return ImmutableList.of();
    }

    private Iterable<String> computeExcludedContexts() {
        return ImmutableList.of();
    }

    private Reference<WebResourceDependencies> computeRequiredWebResources(WebResourceDependenciesRecorder.RecordedResources recorded, Expansion ... es) {
        Expansions expansions = new Expansions(es);
        if (!expansions.canExpand("webresource")) {
            return Reference.collapsed(WebResourceDependencies.class);
        }
        return Reference.to((Object)this.resourceDependenciesFactory.build(recorded, expansions.getSubExpansions("webresource")));
    }

    private Iterable<String> computeWebresourceOverrides() {
        return Lists.newArrayList((Object[])new String[]{"com.atlassian.auiplugin:aui-reset", "com.atlassian.auiplugin:aui-page-typography", "com.atlassian.auiplugin:aui-page-layout", "com.atlassian.auiplugin:aui-avatars", "com.atlassian.auiplugin:aui-page-header", "com.atlassian.auiplugin:aui-experimental-iconfont", "confluence.web.resources:panel-styles", "confluence.web.resources:content-styles", "confluence.web.resources:master-styles", "confluence.web.resources:event", "com.atlassian.confluence.themes.default:styles", "confluence.web.resources:view-content", "confluence.macros.advanced:export-styles"});
    }

    private String computeConvertedValue(ContentRepresentation fromFormat, ContentRepresentation toFormat, String inputValue, ContentEntityObject ceo) throws ServiceException {
        if (toFormat == ContentRepresentation.STORAGE) {
            if (fromFormat == ContentRepresentation.EDITOR) {
                RenderContext renderContext = this.getRenderContext(ceo);
                return this.formatConverter.validateAndConvertToStorageFormat(inputValue, renderContext);
            }
            if (fromFormat == ContentRepresentation.STORAGE) {
                return this.migrateStorageFormat(inputValue, ceo);
            }
            if (fromFormat == ContentRepresentation.WIKI) {
                return this.processMigrationResult(this.wikiToXhtmlMigrator.migrate(inputValue, this.getConversionContext(ceo)));
            }
        } else if (toFormat == ContentRepresentation.EDITOR) {
            if (fromFormat == ContentRepresentation.STORAGE) {
                return this.formatConverter.convertToEditorFormat(inputValue, this.getRenderContext(ceo));
            }
        } else if (toFormat == ContentRepresentation.VIEW) {
            if (fromFormat == ContentRepresentation.STORAGE) {
                return this.viewRenderer.render(inputValue, this.getConversionContext(ceo));
            }
        } else if (toFormat == ContentRepresentation.EXPORT_VIEW) {
            if (fromFormat == ContentRepresentation.STORAGE) {
                return this.viewRenderer.render(inputValue, this.getConversionContextForExportView(ceo));
            }
        } else if (toFormat == ContentRepresentation.STYLED_VIEW) {
            if (fromFormat == ContentRepresentation.STORAGE) {
                ConversionContext conversionContext = this.getConversionContext(ceo);
                String render = this.viewRenderer.render(inputValue, conversionContext);
                return this.inlineStyleHelper.render(render, conversionContext.getPageContext());
            }
        } else if (toFormat == ContentRepresentation.ANONYMOUS_EXPORT_VIEW && fromFormat == ContentRepresentation.STORAGE) {
            ContentEntityObject contentEntityObject = ceo;
            return AuthenticatedUserImpersonator.REQUEST_AWARE.asAnonymousUser(() -> this.viewRenderer.render(inputValue, this.getConversionContextForExportView(contentEntityObject)));
        }
        throw new UnsupportedOperationException(String.format("Cannot convert from %s to %s", fromFormat, toFormat));
    }

    private RenderContext getRenderContext(ContentEntityObject ceo) {
        return ceo == null ? new PageContext() : ceo.toPageContext();
    }

    private RenderContext getRenderContextForExportView(ContentEntityObject ceo) {
        PageContext context = ceo == null ? new PageContext() : ceo.toPageContext();
        context.setOutputType("email");
        context.setOutputDeviceType("email");
        return context;
    }

    private ConversionContext getConversionContextForExportView(ContentEntityObject ceo) {
        return new DefaultConversionContext(this.getRenderContextForExportView(ceo));
    }

    private String migrateStorageFormat(String xhtmlContent, ContentEntityObject ceo) throws ServiceException {
        ConversionContext context = this.getConversionContext(ceo);
        return this.processMigrationResult(this.xhtmlRoundTripMigrator.migrate(xhtmlContent, context));
    }

    private ConversionContext getConversionContext(ContentEntityObject ceo) {
        PageContext pageContext = ceo == null ? new PageContext() : ceo.toPageContext();
        return new DefaultConversionContext(pageContext);
    }

    private String processMigrationResult(ExceptionTolerantMigrator.MigrationResult result) {
        List<RuntimeException> exceptions = result.getExceptions();
        if (!exceptions.isEmpty()) {
            RuntimeException firstException = exceptions.get(0);
            if (firstException.getCause() instanceof WstxException) {
                WstxException wstxException = (WstxException)firstException.getCause();
                throw new BadRequestException("Error parsing xhtml: " + wstxException.getMessage(), (Throwable)wstxException);
            }
            if (exceptions.size() == 1) {
                throw new InternalServerException((Throwable)firstException);
            }
            throw new InternalServerException("Multiple exceptions occurred, only the first is returned: " + firstException.getMessage(), (Throwable)firstException);
        }
        return result.getContent();
    }
}

