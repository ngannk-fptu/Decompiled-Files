/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.cache.ClassTemplateLoader
 *  freemarker.cache.TemplateLoader
 *  freemarker.ext.jsp.TaglibFactory
 *  freemarker.template.Configuration
 *  freemarker.template.TemplateException
 *  javax.servlet.ServletContext
 */
package org.springframework.web.servlet.view.freemarker;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

public class FreeMarkerConfigurer
extends FreeMarkerConfigurationFactory
implements FreeMarkerConfig,
InitializingBean,
ResourceLoaderAware,
ServletContextAware {
    @Nullable
    private Configuration configuration;
    @Nullable
    private TaglibFactory taglibFactory;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.taglibFactory = new TaglibFactory(servletContext);
    }

    @Override
    public void afterPropertiesSet() throws IOException, TemplateException {
        if (this.configuration == null) {
            this.configuration = this.createConfiguration();
        }
    }

    @Override
    protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
        templateLoaders.add((TemplateLoader)new ClassTemplateLoader(FreeMarkerConfigurer.class, ""));
    }

    @Override
    public Configuration getConfiguration() {
        Assert.state(this.configuration != null, "No Configuration available");
        return this.configuration;
    }

    @Override
    public TaglibFactory getTaglibFactory() {
        Assert.state(this.taglibFactory != null, "No TaglibFactory available");
        return this.taglibFactory;
    }
}

