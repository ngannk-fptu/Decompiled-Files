/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.core.ParseException
 *  freemarker.ext.jsp.TaglibFactory
 *  freemarker.ext.servlet.AllHttpScopesHashModel
 *  freemarker.ext.servlet.HttpRequestHashModel
 *  freemarker.ext.servlet.HttpRequestParametersHashModel
 *  freemarker.ext.servlet.HttpSessionHashModel
 *  freemarker.ext.servlet.ServletContextHashModel
 *  freemarker.template.Configuration
 *  freemarker.template.DefaultObjectWrapperBuilder
 *  freemarker.template.ObjectWrapper
 *  freemarker.template.SimpleHash
 *  freemarker.template.Template
 *  freemarker.template.TemplateException
 *  javax.servlet.GenericServlet
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.BeanInitializationException
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.context.ApplicationContextException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.servlet.view.freemarker;

import freemarker.core.ParseException;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

public class FreeMarkerView
extends AbstractTemplateView {
    @Nullable
    private String encoding;
    @Nullable
    private Configuration configuration;
    @Nullable
    private TaglibFactory taglibFactory;
    @Nullable
    private ServletContextHashModel servletContextHashModel;

    public void setEncoding(@Nullable String encoding) {
        this.encoding = encoding;
    }

    @Nullable
    protected String getEncoding() {
        return this.encoding;
    }

    public void setConfiguration(@Nullable Configuration configuration) {
        this.configuration = configuration;
    }

    @Nullable
    protected Configuration getConfiguration() {
        return this.configuration;
    }

    protected Configuration obtainConfiguration() {
        Configuration configuration = this.getConfiguration();
        Assert.state((configuration != null ? 1 : 0) != 0, (String)"No Configuration set");
        return configuration;
    }

    protected void initServletContext(ServletContext servletContext) throws BeansException {
        if (this.getConfiguration() != null) {
            this.taglibFactory = new TaglibFactory(servletContext);
        } else {
            FreeMarkerConfig config = this.autodetectConfiguration();
            this.setConfiguration(config.getConfiguration());
            this.taglibFactory = config.getTaglibFactory();
        }
        GenericServletAdapter servlet = new GenericServletAdapter();
        try {
            servlet.init(new DelegatingServletConfig());
        }
        catch (ServletException ex) {
            throw new BeanInitializationException("Initialization of GenericServlet adapter failed", (Throwable)ex);
        }
        this.servletContextHashModel = new ServletContextHashModel((GenericServlet)servlet, this.getObjectWrapper());
    }

    protected FreeMarkerConfig autodetectConfiguration() throws BeansException {
        try {
            return (FreeMarkerConfig)BeanFactoryUtils.beanOfTypeIncludingAncestors((ListableBeanFactory)this.obtainApplicationContext(), FreeMarkerConfig.class, (boolean)true, (boolean)false);
        }
        catch (NoSuchBeanDefinitionException ex) {
            throw new ApplicationContextException("Must define a single FreeMarkerConfig bean in this web application context (may be inherited): FreeMarkerConfigurer is the usual implementation. This bean may be given any name.", (Throwable)ex);
        }
    }

    protected ObjectWrapper getObjectWrapper() {
        ObjectWrapper ow = this.obtainConfiguration().getObjectWrapper();
        return ow != null ? ow : new DefaultObjectWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).build();
    }

    @Override
    public boolean checkResource(Locale locale) throws Exception {
        String url = this.getUrl();
        Assert.state((url != null ? 1 : 0) != 0, (String)"'url' not set");
        try {
            this.getTemplate(url, locale);
            return true;
        }
        catch (FileNotFoundException ex) {
            return false;
        }
        catch (ParseException ex) {
            throw new ApplicationContextException("Failed to parse [" + url + "]", (Throwable)ex);
        }
        catch (IOException ex) {
            throw new ApplicationContextException("Failed to load [" + url + "]", (Throwable)ex);
        }
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.exposeHelpers(model, request);
        this.doRender(model, request, response);
    }

    protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
    }

    protected void doRender(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.exposeModelAsRequestAttributes(model, request);
        SimpleHash fmModel = this.buildTemplateModel(model, request, response);
        Locale locale = RequestContextUtils.getLocale(request);
        this.processTemplate(this.getTemplate(locale), fmModel, response);
    }

    protected SimpleHash buildTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
        AllHttpScopesHashModel fmModel = new AllHttpScopesHashModel(this.getObjectWrapper(), this.getServletContext(), request);
        fmModel.put("JspTaglibs", (Object)this.taglibFactory);
        fmModel.put("Application", (Object)this.servletContextHashModel);
        fmModel.put("Session", (Object)this.buildSessionModel(request, response));
        fmModel.put("Request", (Object)new HttpRequestHashModel(request, response, this.getObjectWrapper()));
        fmModel.put("RequestParameters", (Object)new HttpRequestParametersHashModel(request));
        fmModel.putAll(model);
        return fmModel;
    }

    private HttpSessionHashModel buildSessionModel(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return new HttpSessionHashModel(session, this.getObjectWrapper());
        }
        return new HttpSessionHashModel(null, request, response, this.getObjectWrapper());
    }

    protected Template getTemplate(Locale locale) throws IOException {
        String url = this.getUrl();
        Assert.state((url != null ? 1 : 0) != 0, (String)"'url' not set");
        return this.getTemplate(url, locale);
    }

    protected Template getTemplate(String name, Locale locale) throws IOException {
        return this.getEncoding() != null ? this.obtainConfiguration().getTemplate(name, locale, this.getEncoding()) : this.obtainConfiguration().getTemplate(name, locale);
    }

    protected void processTemplate(Template template, SimpleHash model, HttpServletResponse response) throws IOException, TemplateException {
        template.process((Object)model, (Writer)response.getWriter());
    }

    private class DelegatingServletConfig
    implements ServletConfig {
        private DelegatingServletConfig() {
        }

        @Nullable
        public String getServletName() {
            return FreeMarkerView.this.getBeanName();
        }

        @Nullable
        public ServletContext getServletContext() {
            return FreeMarkerView.this.getServletContext();
        }

        @Nullable
        public String getInitParameter(String paramName) {
            return null;
        }

        public Enumeration<String> getInitParameterNames() {
            return Collections.enumeration(Collections.emptySet());
        }
    }

    private static class GenericServletAdapter
    extends GenericServlet {
        private GenericServletAdapter() {
        }

        public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
        }
    }
}

