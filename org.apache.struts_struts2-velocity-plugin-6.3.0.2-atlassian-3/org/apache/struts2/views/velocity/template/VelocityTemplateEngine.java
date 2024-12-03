/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.inject.Inject
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.struts2.components.template.BaseTemplateEngine
 *  org.apache.struts2.components.template.Template
 *  org.apache.struts2.components.template.TemplateEngine
 *  org.apache.struts2.components.template.TemplateRenderingContext
 *  org.apache.velocity.Template
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 */
package org.apache.struts2.views.velocity.template;

import com.opensymphony.xwork2.inject.Inject;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.template.BaseTemplateEngine;
import org.apache.struts2.components.template.TemplateEngine;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

public class VelocityTemplateEngine
extends BaseTemplateEngine {
    private static final Logger LOG = LogManager.getLogger(VelocityTemplateEngine.class);
    private VelocityManager velocityManager;

    @Inject
    public void setVelocityManager(VelocityManager mgr) {
        this.velocityManager = mgr;
    }

    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        Map actionContext = templateContext.getStack().getContext();
        ServletContext servletContext = (ServletContext)actionContext.get("com.opensymphony.xwork2.dispatcher.ServletContext");
        HttpServletRequest req = (HttpServletRequest)actionContext.get("com.opensymphony.xwork2.dispatcher.HttpServletRequest");
        HttpServletResponse res = (HttpServletResponse)actionContext.get("com.opensymphony.xwork2.dispatcher.HttpServletResponse");
        this.velocityManager.init(servletContext);
        VelocityEngine velocityEngine = this.velocityManager.getVelocityEngine();
        List templates = templateContext.getTemplate().getPossibleTemplates((TemplateEngine)this);
        Template template = null;
        String templateName = null;
        Exception exception = null;
        for (org.apache.struts2.components.template.Template t : templates) {
            templateName = this.getFinalTemplateName(t);
            try {
                template = velocityEngine.getTemplate(templateName);
                break;
            }
            catch (Exception e) {
                if (exception != null) continue;
                exception = e;
            }
        }
        if (template == null) {
            LOG.error("Could not load template {}", (Object)templateContext.getTemplate());
            if (exception != null) {
                throw exception;
            }
            return;
        }
        LOG.debug("Rendering template {}", templateName);
        Context context = this.velocityManager.createContext(templateContext.getStack(), req, res);
        Writer outputWriter = templateContext.getWriter();
        context.put("tag", (Object)templateContext.getTag());
        context.put("parameters", (Object)templateContext.getParameters());
        template.merge(context, outputWriter);
    }

    protected String getSuffix() {
        return "vm";
    }
}

