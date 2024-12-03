/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.text.Template
 *  groovy.text.markup.MarkupTemplateEngine
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.web.util.NestedServletException
 */
package org.springframework.web.servlet.view.groovy;

import groovy.text.Template;
import groovy.text.markup.MarkupTemplateEngine;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfig;
import org.springframework.web.util.NestedServletException;

public class GroovyMarkupView
extends AbstractTemplateView {
    @Nullable
    private MarkupTemplateEngine engine;

    public void setTemplateEngine(MarkupTemplateEngine engine) {
        this.engine = engine;
    }

    protected void initApplicationContext(ApplicationContext context) {
        super.initApplicationContext();
        if (this.engine == null) {
            this.setTemplateEngine(this.autodetectMarkupTemplateEngine());
        }
    }

    protected MarkupTemplateEngine autodetectMarkupTemplateEngine() throws BeansException {
        try {
            return ((GroovyMarkupConfig)BeanFactoryUtils.beanOfTypeIncludingAncestors((ListableBeanFactory)this.obtainApplicationContext(), GroovyMarkupConfig.class, (boolean)true, (boolean)false)).getTemplateEngine();
        }
        catch (NoSuchBeanDefinitionException ex) {
            throw new ApplicationContextException("Expected a single GroovyMarkupConfig bean in the current Servlet web application context or the parent root context: GroovyMarkupConfigurer is the usual implementation. This bean may have any name.", (Throwable)ex);
        }
    }

    @Override
    public boolean checkResource(Locale locale) throws Exception {
        Assert.state((this.engine != null ? 1 : 0) != 0, (String)"No MarkupTemplateEngine set");
        try {
            this.engine.resolveTemplate(this.getUrl());
        }
        catch (IOException ex) {
            return false;
        }
        return true;
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = this.getUrl();
        Assert.state((url != null ? 1 : 0) != 0, (String)"'url' not set");
        Template template = this.getTemplate(url);
        template.make(model).writeTo((Writer)new BufferedWriter(response.getWriter()));
    }

    protected Template getTemplate(String viewUrl) throws Exception {
        Assert.state((this.engine != null ? 1 : 0) != 0, (String)"No MarkupTemplateEngine set");
        try {
            return this.engine.createTemplateByPath(viewUrl);
        }
        catch (ClassNotFoundException ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            throw new NestedServletException("Could not find class while rendering Groovy Markup view with name '" + this.getUrl() + "': " + ex.getMessage() + "'", cause);
        }
    }
}

