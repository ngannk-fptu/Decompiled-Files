/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Submit;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

public class SubmitTag
extends AbstractClosingTag {
    private static final long serialVersionUID = 2179281109958301343L;
    protected String action;
    protected String method;
    protected String type;
    protected String src;
    protected boolean escapeHtmlBody = false;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Submit(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Submit submit = (Submit)this.component;
        submit.setAction(this.action);
        submit.setMethod(this.method);
        submit.setType(this.type);
        submit.setSrc(this.src);
        submit.setEscapeHtmlBody(this.escapeHtmlBody);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setEscapeHtmlBody(boolean escapeHtmlBody) {
        this.escapeHtmlBody = escapeHtmlBody;
    }

    @Override
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
        if (!this.getPerformClearTagStateForTagPoolingServers()) {
            return;
        }
        super.clearTagStateForTagPoolingServers();
        this.action = null;
        this.method = null;
        this.type = null;
        this.src = null;
    }
}

