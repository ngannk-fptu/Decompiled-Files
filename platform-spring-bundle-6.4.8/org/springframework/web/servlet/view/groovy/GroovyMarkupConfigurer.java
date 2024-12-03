/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.text.markup.MarkupTemplateEngine
 *  groovy.text.markup.MarkupTemplateEngine$TemplateResource
 *  groovy.text.markup.TemplateConfiguration
 *  groovy.text.markup.TemplateResolver
 */
package org.springframework.web.servlet.view.groovy;

import groovy.text.markup.MarkupTemplateEngine;
import groovy.text.markup.TemplateConfiguration;
import groovy.text.markup.TemplateResolver;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Locale;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfig;

public class GroovyMarkupConfigurer
extends TemplateConfiguration
implements GroovyMarkupConfig,
ApplicationContextAware,
InitializingBean {
    private String resourceLoaderPath = "classpath:";
    @Nullable
    private MarkupTemplateEngine templateEngine;
    @Nullable
    private ApplicationContext applicationContext;

    public void setResourceLoaderPath(String resourceLoaderPath) {
        this.resourceLoaderPath = resourceLoaderPath;
    }

    public String getResourceLoaderPath() {
        return this.resourceLoaderPath;
    }

    public void setTemplateEngine(MarkupTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public MarkupTemplateEngine getTemplateEngine() {
        Assert.state(this.templateEngine != null, "No MarkupTemplateEngine set");
        return this.templateEngine;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected ApplicationContext getApplicationContext() {
        Assert.state(this.applicationContext != null, "No ApplicationContext set");
        return this.applicationContext;
    }

    public void setLocale(Locale locale) {
        super.setLocale(locale);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.templateEngine == null) {
            this.templateEngine = this.createTemplateEngine();
        }
    }

    protected MarkupTemplateEngine createTemplateEngine() throws IOException {
        if (this.templateEngine == null) {
            ClassLoader templateClassLoader = this.createTemplateClassLoader();
            this.templateEngine = new MarkupTemplateEngine(templateClassLoader, (TemplateConfiguration)this, (TemplateResolver)new LocaleTemplateResolver());
        }
        return this.templateEngine;
    }

    protected ClassLoader createTemplateClassLoader() throws IOException {
        String[] paths = StringUtils.commaDelimitedListToStringArray(this.getResourceLoaderPath());
        ArrayList<URL> urls = new ArrayList<URL>();
        for (String path : paths) {
            Resource[] resources2 = this.getApplicationContext().getResources(path);
            if (resources2.length <= 0) continue;
            for (Resource resource : resources2) {
                if (!resource.exists()) continue;
                urls.add(resource.getURL());
            }
        }
        ClassLoader classLoader = this.getApplicationContext().getClassLoader();
        Assert.state(classLoader != null, "No ClassLoader");
        return !urls.isEmpty() ? new URLClassLoader(urls.toArray(new URL[0]), classLoader) : classLoader;
    }

    protected URL resolveTemplate(ClassLoader classLoader, String templatePath) throws IOException {
        Locale locale;
        MarkupTemplateEngine.TemplateResource resource = MarkupTemplateEngine.TemplateResource.parse((String)templatePath);
        URL url = classLoader.getResource(resource.withLocale(StringUtils.replace((locale = LocaleContextHolder.getLocale()).toString(), "-", "_")).toString());
        if (url == null) {
            url = classLoader.getResource(resource.withLocale(locale.getLanguage()).toString());
        }
        if (url == null) {
            url = classLoader.getResource(resource.withLocale(null).toString());
        }
        if (url == null) {
            throw new IOException("Unable to load template:" + templatePath);
        }
        return url;
    }

    private class LocaleTemplateResolver
    implements TemplateResolver {
        @Nullable
        private ClassLoader classLoader;

        private LocaleTemplateResolver() {
        }

        public void configure(ClassLoader templateClassLoader, TemplateConfiguration configuration) {
            this.classLoader = templateClassLoader;
        }

        public URL resolveTemplate(String templatePath) throws IOException {
            Assert.state(this.classLoader != null, "No template ClassLoader available");
            return GroovyMarkupConfigurer.this.resolveTemplate(this.classLoader, templatePath);
        }
    }
}

