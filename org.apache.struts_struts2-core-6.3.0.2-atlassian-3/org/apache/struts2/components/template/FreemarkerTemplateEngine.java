/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  freemarker.core.ParseException
 *  freemarker.template.Configuration
 *  freemarker.template.Template
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components.template;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.template.BaseTemplateEngine;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.ScopesHashModel;

public class FreemarkerTemplateEngine
extends BaseTemplateEngine {
    static Class bodyContent = null;
    protected FreemarkerManager freemarkerManager;
    private static final Logger LOG;

    @Inject
    public void setFreemarkerManager(FreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        Writer writer;
        Object action;
        ValueStack stack = templateContext.getStack();
        ActionContext context = stack.getActionContext();
        ServletContext servletContext = context.getServletContext();
        HttpServletRequest req = context.getServletRequest();
        HttpServletResponse res = context.getServletResponse();
        Configuration config = this.freemarkerManager.getConfiguration(servletContext);
        List<Template> templates = templateContext.getTemplate().getPossibleTemplates(this);
        freemarker.template.Template template = null;
        String templateName = null;
        Throwable exception = null;
        for (Template t : templates) {
            templateName = this.getFinalTemplateName(t);
            try {
                template = config.getTemplate(templateName);
                break;
            }
            catch (ParseException e) {
                exception = e;
                break;
            }
            catch (IOException e) {
                if (exception != null) continue;
                exception = e;
            }
        }
        if (template == null) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Could not load the FreeMarker template named '{}':", (Object)templateContext.getTemplate().getName());
                for (Template t : templates) {
                    LOG.error("Attempted: {}", (Object)this.getFinalTemplateName(t));
                }
                LOG.error("The TemplateLoader provided by the FreeMarker Configuration was a: {}", (Object)config.getTemplateLoader().getClass().getName());
            }
            if (exception != null) {
                throw exception;
            }
            return;
        }
        LOG.debug("Rendering template: {}", templateName);
        ActionInvocation ai = ActionContext.getContext().getActionInvocation();
        Object object = action = ai == null ? null : ai.getAction();
        if (action == null) {
            LOG.warn("Rendering tag {} out of Action scope, accessing directly JSPs is not recommended! Please read https://struts.apache.org/security/#never-expose-jsp-files-directly", (Object)templateName);
        }
        ScopesHashModel model = this.freemarkerManager.buildTemplateModel(stack, action, servletContext, req, res, config.getObjectWrapper());
        model.put("tag", templateContext.getTag());
        model.put("themeProperties", this.getThemeProps(templateContext.getTemplate()));
        final Writer wrapped = writer = templateContext.getWriter();
        writer = new Writer(){

            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                wrapped.write(cbuf, off, len);
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
                wrapped.close();
            }
        };
        LOG.debug("Push tag on top of the stack");
        stack.push(templateContext.getTag());
        try {
            template.process((Object)model, writer);
        }
        finally {
            LOG.debug("Removes tag from top of the stack");
            stack.pop();
        }
    }

    @Override
    protected String getSuffix() {
        return "ftl";
    }

    static {
        try {
            bodyContent = ClassLoaderUtil.loadClass("javax.servlet.jsp.tagext.BodyContent", FreemarkerTemplateEngine.class);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        LOG = LogManager.getLogger(FreemarkerTemplateEngine.class);
    }
}

