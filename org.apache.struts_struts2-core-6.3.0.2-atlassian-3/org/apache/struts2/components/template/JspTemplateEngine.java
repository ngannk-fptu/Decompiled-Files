/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.PageContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components.template;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.Include;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.components.template.BaseTemplateEngine;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateRenderingContext;

public class JspTemplateEngine
extends BaseTemplateEngine {
    private static final Logger LOG = LogManager.getLogger(JspTemplateEngine.class);
    String encoding;

    @Inject(value="struts.i18n.encoding")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        Template template = templateContext.getTemplate();
        LOG.debug("Trying to render template [{}], repeating through parents until we succeed", (Object)template);
        UIBean tag = templateContext.getTag();
        ValueStack stack = templateContext.getStack();
        stack.push(tag);
        PageContext pageContext = (PageContext)stack.getContext().get("com.opensymphony.xwork2.dispatcher.PageContext");
        List<Template> templates = template.getPossibleTemplates(this);
        Exception exception = null;
        boolean success = false;
        for (Template t : templates) {
            try {
                Include.include(this.getFinalTemplateName(t), (Writer)pageContext.getOut(), pageContext.getRequest(), (HttpServletResponse)pageContext.getResponse(), this.encoding);
                success = true;
                break;
            }
            catch (Exception e) {
                if (exception != null) continue;
                exception = e;
            }
        }
        if (!success) {
            LOG.error("Could not render JSP template {}", (Object)templateContext.getTemplate());
            if (exception != null) {
                throw exception;
            }
            return;
        }
        stack.pop();
    }

    @Override
    protected String getSuffix() {
        return "jsp";
    }
}

