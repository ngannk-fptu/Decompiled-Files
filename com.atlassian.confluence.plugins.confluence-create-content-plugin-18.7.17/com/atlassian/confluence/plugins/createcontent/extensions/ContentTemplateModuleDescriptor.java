/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.plugin.descriptor.web.ConfluenceWebFragmentHelper
 *  com.atlassian.confluence.plugin.module.PluginModuleHolder
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.Resources
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.elements.ResourceLocation
 *  com.atlassian.plugin.loaders.LoaderUtils
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.NoOpContextProvider
 *  com.atlassian.plugin.web.conditions.ConditionLoadingException
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.input.BoundedInputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.createcontent.extensions;

import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.plugin.descriptor.web.ConfluenceWebFragmentHelper;
import com.atlassian.confluence.plugin.module.PluginModuleHolder;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.Resources;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.loaders.LoaderUtils;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.NoOpContextProvider;
import com.atlassian.plugin.web.conditions.ConditionLoadingException;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentTemplateModuleDescriptor
extends AbstractModuleDescriptor<PageTemplate> {
    private static final int MAX_TEMPLATE_SIZE = 10240;
    private static final Logger log = LoggerFactory.getLogger(ContentTemplateModuleDescriptor.class);
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final RequestFactory<?> requestFactory;
    private ContextProvider contextProvider;
    private ContextProviderConfig contextProviderConfig;
    private URL templateLocator;
    private ModuleCompleteKey moduleCompleteKey;
    private String nameKey;
    private PluginModuleHolder<PageTemplate> pluginModuleHolder;

    public ContentTemplateModuleDescriptor(@ComponentImport ModuleFactory moduleFactory, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport RequestFactory<?> requestFactory) {
        super(moduleFactory);
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.requestFactory = requestFactory;
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) throws PluginParseException {
        super.init(plugin, element);
        if (StringUtils.isBlank((CharSequence)this.getKey())) {
            throw new PluginParseException("key is a required attribute of <content-template>.");
        }
        if (StringUtils.isBlank((CharSequence)this.getI18nNameKey())) {
            log.warn("i18n-name-key is a required attribute of <content-template> for module: " + this.getCompleteKey());
        }
        this.nameKey = StringUtils.isBlank((CharSequence)this.getI18nNameKey()) ? "create.content.plugin.plugin.default-template-name" : this.getI18nNameKey();
        this.contextProviderConfig = this.getContextProviderConfig(element);
        this.templateLocator = this.getTemplateLocator(element);
        this.moduleCompleteKey = new ModuleCompleteKey(this.getCompleteKey());
        this.pluginModuleHolder = PluginModuleHolder.getInstance(() -> {
            PageTemplate result = new PageTemplate();
            result.setBodyType(BodyType.XHTML);
            result.setContent(this.getTemplateContent(this.templateLocator));
            result.setModuleCompleteKey(this.moduleCompleteKey);
            I18NBean i18nBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
            result.setName(i18nBean.getText(this.nameKey));
            if (StringUtils.isNotBlank((CharSequence)this.getDescriptionKey())) {
                result.setDescription(i18nBean.getText(this.getDescriptionKey()));
            }
            return result;
        });
    }

    /*
     * Exception decompiling
     */
    private String getTemplateContent(URL templateLocator) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private String error(Throwable e) {
        StringWriter writer = new StringWriter();
        writer.append("<h1>An error occurred creating content from template</h1><p>Template: ");
        writer.append(this.getNameKey());
        writer.append("</p>");
        writer.append("<ac:structured-macro ac:name=\"expand\"><ac:rich-text-body><pre>");
        e.printStackTrace(new PrintWriter(writer, true));
        writer.append("</pre></ac:rich-text-body></ac:structured-macro>");
        return writer.toString();
    }

    public void enabled() {
        super.enabled();
        this.contextProvider = this.getContextProvider(this.contextProviderConfig);
        this.pluginModuleHolder.enabled(this.getModuleClass());
    }

    public void disabled() {
        this.contextProvider = null;
        this.pluginModuleHolder.disabled();
        super.disabled();
    }

    private ContextProvider getContextProvider(ContextProviderConfig config) throws PluginParseException {
        if (config == null) {
            return new NoOpContextProvider();
        }
        try {
            ContextProvider context = new ConfluenceWebFragmentHelper().loadContextProvider(config.contextProviderClassName, this.getPlugin());
            context.init(config.contextProviderParams);
            return context;
        }
        catch (ClassCastException ex) {
            throw new PluginParseException("Configured context-provider class does not implement the ContextProvider interface", (Throwable)ex);
        }
        catch (ConditionLoadingException ex) {
            throw new PluginParseException("Unable to load the module's display conditions: " + ex.getMessage(), (Throwable)ex);
        }
    }

    private URL getTemplateLocator(Element element) {
        Resources resources = Resources.fromXml((Element)element);
        ResourceLocation templateLocation = resources.getResourceLocation("download", "template");
        if (templateLocation == null) {
            throw new PluginParseException("You must specify a template resource for the <content-template> tag. Add <resource name=\"template\" type=\"download\" location=\"<insert-path-to-your-template>/template.xml\"/> as a child element of <content-template>.");
        }
        String location = templateLocation.getLocation();
        URL templateLocator = this.getPlugin().getResource(location);
        if (templateLocator == null) {
            try {
                templateLocator = new URL(location);
                String protocol = templateLocator.getProtocol();
                if (!"http".equals(protocol) && !"https".equals(protocol)) {
                    throw new PluginParseException("Invalid protocol for remote template: " + protocol);
                }
            }
            catch (MalformedURLException e) {
                throw new PluginParseException("Could not load template XML at: " + location);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("found resource for content-template [ {} ] at [ {} ]", (Object)this.nameKey, (Object)templateLocator);
        }
        return templateLocator;
    }

    private ContextProviderConfig getContextProviderConfig(Element element) {
        Element contextProviderElement = element.element("context-provider");
        if (contextProviderElement == null) {
            return null;
        }
        String contextProviderClassName = contextProviderElement.attributeValue("class");
        Map contextProviderParams = LoaderUtils.getParams((Element)contextProviderElement);
        return new ContextProviderConfig(contextProviderClassName, contextProviderParams);
    }

    public ContextProvider getContextProvider() {
        return this.contextProvider;
    }

    public PageTemplate getModule() {
        return (PageTemplate)this.pluginModuleHolder.getModule();
    }

    public String getNameKey() {
        return this.nameKey;
    }

    public URL getTemplateLocator() {
        return this.templateLocator;
    }

    public Class<PageTemplate> getModuleClass() {
        return PageTemplate.class;
    }

    private static class ContextProviderConfig {
        final String contextProviderClassName;
        final Map<String, String> contextProviderParams;

        ContextProviderConfig(String contextProviderClassName, Map<String, String> contextProviderParams) {
            this.contextProviderClassName = contextProviderClassName;
            this.contextProviderParams = contextProviderParams;
        }
    }

    private static class ContentTemplateResponeHandler<T extends Response>
    implements ResponseHandler<T> {
        private String body;

        private ContentTemplateResponeHandler() {
        }

        public void handle(T response) throws ResponseException {
            try {
                InputStream stream = response.getResponseBodyAsStream();
                try {
                    this.body = IOUtils.toString((InputStream)new BoundedInputStream(stream, 10240L));
                    if (stream.available() > 0) {
                        throw new ResponseException("Template too big (size>10240)");
                    }
                }
                finally {
                    IOUtils.closeQuietly((InputStream)stream);
                }
            }
            catch (IOException e) {
                throw new ResponseException((Throwable)e);
            }
        }

        public String getBody() {
            return this.body;
        }
    }
}

