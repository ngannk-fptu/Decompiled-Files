/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

public abstract class ClosingUIBean
extends UIBean {
    private static final Logger LOG = LogManager.getLogger(ClosingUIBean.class);
    String openTemplate;

    protected ClosingUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public abstract String getDefaultOpenTemplate();

    @StrutsTagAttribute(description="Set template to use for opening the rendered html.")
    public void setOpenTemplate(String openTemplate) {
        this.openTemplate = openTemplate;
    }

    @Override
    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        try {
            this.evaluateParams();
            this.mergeTemplate(writer, this.buildTemplateName(this.openTemplate, this.getDefaultOpenTemplate()));
        }
        catch (Exception e) {
            LOG.error("Could not open template", (Throwable)e);
        }
        return result;
    }
}

