/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  io.atlassian.util.concurrent.Timeout
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.BatchedRenderRequest;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.DummyRenderingEventPublisher;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.RenderingEventPublisher;
import com.atlassian.confluence.content.render.xhtml.XhtmlTimeoutException;
import com.atlassian.confluence.content.render.xhtml.compatibility.LegacyV2RendererContextInitialiser;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.content.render.xhtml.view.BatchedRenderResult;
import com.atlassian.confluence.content.render.xhtml.view.RenderResult;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.render.prefetch.ContentResourcePrefetcher;
import com.atlassian.confluence.impl.content.render.xhtml.analytics.MarshallerMetricsAccumulatorStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import io.atlassian.util.concurrent.Timeout;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRenderer
implements Renderer {
    private static final Logger log = LoggerFactory.getLogger(DefaultRenderer.class);
    private final Transformer transformer;
    private final I18NBeanFactory i18nBeanFactory;
    private final LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter;
    private final SettingsManager settingsManager;
    private final RenderingEventPublisher renderingEventPublisher;
    private final Optional<ContentResourcePrefetcher> contentResourcePrefetcher;
    private final String outputDeviceType;

    @Deprecated
    public DefaultRenderer(Transformer transformer, I18NBeanFactory i18nBeanFactory, LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter, SettingsManager settingsManager) {
        this(transformer, i18nBeanFactory, legacyV2RendererConfigurationPropertySetter, settingsManager, "desktop");
    }

    @Deprecated
    public DefaultRenderer(Transformer transformer, I18NBeanFactory i18nBeanFactory, LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter, SettingsManager settingsManager, String outputDeviceType) {
        this(transformer, i18nBeanFactory, legacyV2RendererConfigurationPropertySetter, settingsManager, (RenderingEventPublisher)new DummyRenderingEventPublisher(), outputDeviceType);
    }

    @Deprecated
    public DefaultRenderer(Transformer transformer, I18NBeanFactory i18nBeanFactory, LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter, SettingsManager settingsManager, RenderingEventPublisher renderingEventPublisher) {
        this(transformer, i18nBeanFactory, legacyV2RendererConfigurationPropertySetter, settingsManager, renderingEventPublisher, "desktop", null);
    }

    public DefaultRenderer(Transformer transformer, I18NBeanFactory i18nBeanFactory, LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter, SettingsManager settingsManager, RenderingEventPublisher renderingEventPublisher, ContentResourcePrefetcher contentResourcePrefetcher) {
        this(transformer, i18nBeanFactory, legacyV2RendererConfigurationPropertySetter, settingsManager, renderingEventPublisher, "desktop", contentResourcePrefetcher);
    }

    @Deprecated
    public DefaultRenderer(Transformer transformer, I18NBeanFactory i18nBeanFactory, LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter, SettingsManager settingsManager, RenderingEventPublisher renderingEventPublisher, String outputDeviceType) {
        this(transformer, i18nBeanFactory, legacyV2RendererConfigurationPropertySetter, settingsManager, renderingEventPublisher, outputDeviceType, null);
    }

    public DefaultRenderer(Transformer transformer, I18NBeanFactory i18nBeanFactory, LegacyV2RendererContextInitialiser legacyV2RendererConfigurationPropertySetter, SettingsManager settingsManager, RenderingEventPublisher renderingEventPublisher, String outputDeviceType, ContentResourcePrefetcher contentResourcePrefetcher) {
        this.transformer = transformer;
        this.i18nBeanFactory = i18nBeanFactory;
        this.legacyV2RendererConfigurationPropertySetter = legacyV2RendererConfigurationPropertySetter;
        this.settingsManager = settingsManager;
        this.renderingEventPublisher = renderingEventPublisher == null ? new DummyRenderingEventPublisher() : renderingEventPublisher;
        this.outputDeviceType = outputDeviceType;
        this.contentResourcePrefetcher = Optional.ofNullable(contentResourcePrefetcher);
    }

    private ConversionContext createDefaultConversionContext(ContentEntityObject content) {
        Timeout timeout = Timeout.getMillisTimeout((long)this.settingsManager.getGlobalSettings().getPageTimeout(), (TimeUnit)TimeUnit.SECONDS);
        return new DefaultConversionContext(PageContext.newContextWithTimeout(content, timeout), this.outputDeviceType);
    }

    @Override
    public String render(ContentEntityObject content) {
        return this.render(content, this.createDefaultConversionContext(content));
    }

    @Override
    public String render(ContentEntityObject contentEntityObject, ConversionContext conversionContext) {
        this.prefetchContentResources(contentEntityObject, conversionContext);
        return this.render(contentEntityObject.getBodyAsString(), conversionContext);
    }

    private void prefetchContentResources(ContentEntityObject contentEntityObject, ConversionContext conversionContext) {
        this.contentResourcePrefetcher.ifPresent(prefetcher -> {
            if (prefetcher.isEnabled()) {
                prefetcher.prefetchContentResources(contentEntityObject.getBodyContent(), conversionContext);
            }
        });
    }

    @Override
    public String render(String content, ConversionContext conversionContext) {
        try (Ticker ignored = Timers.start((String)"DefaultRenderer.render");){
            RenderResult renderResult = this.renderWithResult(content, conversionContext);
            String string = renderResult.getRender();
            return string;
        }
    }

    @Override
    public RenderResult renderWithResult(String content, ConversionContext conversionContext) {
        try (Ticker ignored = Timers.start((String)"DefaultRenderer.renderWithResult");){
            if (StringUtils.isBlank((CharSequence)content)) {
                RenderResult renderResult = RenderResult.success("");
                return renderResult;
            }
            this.legacyV2RendererConfigurationPropertySetter.initialise(conversionContext);
            MarshallerMetricsAccumulatorStack.pushNewMetricsAccumulator(conversionContext);
            RenderResult renderResult = this.renderWithoutMetrics(content, conversionContext);
            if (renderResult.isSuccessful()) {
                this.renderingEventPublisher.publish(this, conversionContext);
            }
            MarshallerMetricsAccumulatorStack.pop(conversionContext);
            RenderResult renderResult2 = renderResult;
            return renderResult2;
        }
    }

    @Override
    public List<BatchedRenderResult> render(BatchedRenderRequest ... renderRequests) {
        return Arrays.stream(renderRequests).map(this::renderOneBatch).collect(Collectors.toList());
    }

    private BatchedRenderResult renderOneBatch(BatchedRenderRequest renderRequest) {
        if (renderRequest.getStorageFragments().isEmpty()) {
            return new BatchedRenderResult();
        }
        ConversionContext context = renderRequest.getContext();
        this.legacyV2RendererConfigurationPropertySetter.initialise(context);
        MarshallerMetricsAccumulatorStack.pushNewMetricsAccumulator(context);
        List<RenderResult> results = renderRequest.getStorageFragments().stream().map(s -> this.renderWithoutMetrics((String)s, context)).collect(Collectors.toList());
        BatchedRenderResult batchedRenderResult = new BatchedRenderResult(results);
        if (batchedRenderResult.isSuccessful()) {
            this.renderingEventPublisher.publish(this, context);
        }
        MarshallerMetricsAccumulatorStack.pop(context);
        return batchedRenderResult;
    }

    private RenderResult renderWithoutMetrics(String content, ConversionContext conversionContext) {
        try {
            String result = this.transformer.transform(new StringReader(content), conversionContext);
            return RenderResult.success(result);
        }
        catch (XhtmlTimeoutException e) {
            log.error(e.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("The content that could not be rendered:\n {}", (Object)content);
                e.printStackTrace();
            }
            return RenderResult.failure(this.generateErrorDiv(e));
        }
        catch (Exception e) {
            log.error("Error rendering content for view: " + e.getMessage(), (Throwable)e);
            if (log.isDebugEnabled()) {
                log.debug("The content that could not be rendered:\n {}", (Object)content);
                e.printStackTrace();
            }
            return RenderResult.failure(this.generateErrorDiv(e));
        }
    }

    private String generateErrorDiv(Exception ex) {
        return "<div class=\"error fatal-render-error\">" + this.i18nBeanFactory.getI18NBean().getText("xhtml.catch.all.error", new Object[]{ex.getMessage()}) + "</div>";
    }
}

