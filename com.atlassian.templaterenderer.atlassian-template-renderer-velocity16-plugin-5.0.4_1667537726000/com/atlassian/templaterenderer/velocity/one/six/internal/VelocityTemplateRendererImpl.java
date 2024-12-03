/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.templaterenderer.RenderingException
 *  com.atlassian.templaterenderer.TemplateContextFactory
 *  com.atlassian.util.profiling.Metrics
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.velocity.htmlsafe.HtmlSafeDirective
 *  com.atlassian.velocity.htmlsafe.directive.DisableHtmlEscaping
 *  com.atlassian.velocity.htmlsafe.directive.EnableHtmlEscaping
 *  com.atlassian.velocity.htmlsafe.event.referenceinsertion.DisableHtmlEscapingDirectiveHandler
 *  com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxingUberspect
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.collections.ExtendedProperties
 *  org.apache.velocity.Template
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.app.event.EventCartridge
 *  org.apache.velocity.app.event.EventHandler
 *  org.apache.velocity.app.event.ReferenceInsertionEventHandler
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.runtime.log.CommonsLogLogChute
 *  org.apache.velocity.runtime.resource.ResourceCacheImpl
 *  org.apache.velocity.runtime.resource.ResourceManagerImpl
 *  org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
 */
package com.atlassian.templaterenderer.velocity.one.six.internal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateContextFactory;
import com.atlassian.templaterenderer.velocity.CompositeClassLoader;
import com.atlassian.templaterenderer.velocity.DynamicParserPool;
import com.atlassian.templaterenderer.velocity.TemplateRendererAnnotationBoxingUberspect;
import com.atlassian.templaterenderer.velocity.TemplateRendererHtmlAnnotationEscaper;
import com.atlassian.templaterenderer.velocity.one.six.VelocityTemplateRenderer;
import com.atlassian.util.profiling.Metrics;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.velocity.htmlsafe.HtmlSafeDirective;
import com.atlassian.velocity.htmlsafe.directive.DisableHtmlEscaping;
import com.atlassian.velocity.htmlsafe.directive.EnableHtmlEscaping;
import com.atlassian.velocity.htmlsafe.event.referenceinsertion.DisableHtmlEscapingDirectiveHandler;
import com.atlassian.velocity.htmlsafe.introspection.AnnotationBoxingUberspect;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.EventHandler;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.log.CommonsLogLogChute;
import org.apache.velocity.runtime.resource.ResourceCacheImpl;
import org.apache.velocity.runtime.resource.ResourceManagerImpl;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityTemplateRendererImpl
implements VelocityTemplateRenderer {
    public static final String DEFAULT_ENCODING = "UTF-8";
    private final EventPublisher eventPublisher;
    private final ClassLoader classLoader;
    private final String pluginKey;
    private final TemplateContextFactory templateContextFactory;
    private final VelocityEngine velocity;

    public VelocityTemplateRendererImpl(ClassLoader classLoader, EventPublisher eventPublisher, String pluginKey, Map<String, String> properties, TemplateContextFactory templateContextFactory) {
        this.classLoader = classLoader;
        this.eventPublisher = eventPublisher;
        this.pluginKey = pluginKey;
        this.templateContextFactory = templateContextFactory;
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        CompositeClassLoader compositeClassLoader = new CompositeClassLoader(this.getClass().getClassLoader(), AnnotationBoxingUberspect.class.getClassLoader(), classLoader);
        Thread.currentThread().setContextClassLoader(compositeClassLoader);
        try {
            this.velocity = new VelocityEngine();
            Throwable throwable = null;
            try (InputStream productVelocityPropertiesStream = compositeClassLoader.getResourceAsStream("velocity.properties");){
                if (productVelocityPropertiesStream != null) {
                    ExtendedProperties productVelocityProperties = new ExtendedProperties();
                    productVelocityProperties.load(productVelocityPropertiesStream);
                    this.velocity.setExtendedProperties(productVelocityProperties);
                }
            }
            catch (Throwable throwable2) {
                Throwable throwable3 = throwable2;
                throw throwable2;
            }
            this.overrideProperty("runtime.log.logsystem.class", CommonsLogLogChute.class.getName());
            this.overrideProperty("resource.loader", "classpath");
            this.overrideProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            this.overrideProperty("runtime.introspector.uberspect", TemplateRendererAnnotationBoxingUberspect.class.getName());
            this.overrideProperty("parser.pool.class", DynamicParserPool.class.getName());
            this.overrideProperty("userdirective", EnableHtmlEscaping.class.getName());
            this.overrideProperty("userdirective", DisableHtmlEscaping.class.getName());
            this.overrideProperty("userdirective", HtmlSafeDirective.class.getName());
            this.overrideProperty("resource.manager.class", ResourceManagerImpl.class.getName());
            this.overrideProperty("resource.manager.cache.class", ResourceCacheImpl.class.getName());
            for (Map.Entry entry : properties.entrySet()) {
                this.overrideProperty((String)entry.getKey(), entry.getValue());
            }
            this.handleCache();
            this.velocity.clearProperty("velocimacro.library");
            this.velocity.init();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    public void render(String templateName, Writer writer) throws RenderingException, IOException {
        this.render(templateName, Collections.emptyMap(), writer);
    }

    public void render(String templateName, Map<String, Object> context, Writer writer) throws RenderingException, IOException {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.classLoader);
        try (Ticker ignored = Metrics.metric((String)"webTemplateRenderer").tag("templateRenderer", "velocity").tag("templateName", templateName).fromPluginKey(this.pluginKey).withAnalytics().startTimer();){
            Template template = this.velocity.getTemplate(templateName, DEFAULT_ENCODING);
            template.merge((Context)this.createContext(context), writer);
            writer.flush();
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RenderingException((Throwable)e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    public String renderFragment(String fragment, Map<String, Object> context) {
        try {
            StringWriter tempWriter = new StringWriter(fragment.length());
            this.velocity.evaluate((Context)this.createContext(context), (Writer)tempWriter, "renderFragment", fragment);
            return tempWriter.toString();
        }
        catch (Exception e) {
            throw new RenderingException((Throwable)e);
        }
    }

    private void handleCache() {
        String IS_DEV_MODE = Boolean.toString(this.isDevMode());
        String IS_PROD_MODE = Boolean.toString(!this.isDevMode());
        this.overrideProperty("classpath.resource.loader.cache", IS_PROD_MODE);
        this.overrideProperty("plugin.resource.loader.cache", IS_PROD_MODE);
        this.overrideProperty("velocimacro.library.autoreload", IS_DEV_MODE);
    }

    private boolean isDevMode() {
        return Boolean.getBoolean("atlassian.dev.mode");
    }

    private VelocityContext createContext(Map<String, Object> contextParams) {
        VelocityContext velocityContext = new VelocityContext(this.templateContextFactory.createContext(this.pluginKey, contextParams));
        velocityContext.attachEventCartridge(this.createCartridgeFrom((List<? extends EventHandler>)ImmutableList.of((Object)new DisableHtmlEscapingDirectiveHandler((ReferenceInsertionEventHandler)new TemplateRendererHtmlAnnotationEscaper()))));
        return velocityContext;
    }

    private EventCartridge createCartridgeFrom(List<? extends EventHandler> eventHandlers) {
        EventCartridge cartridge = new EventCartridge();
        for (EventHandler eventHandler : eventHandlers) {
            cartridge.addEventHandler(eventHandler);
        }
        return cartridge;
    }

    private void overrideProperty(String key, Object value) {
        if (key.equals("userdirective")) {
            this.velocity.addProperty(key, value);
        } else {
            this.velocity.setProperty(key, value);
        }
    }

    public boolean resolve(String templateName) {
        return this.classLoader.getResource(templateName) != null;
    }
}

