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
import org.apache.struts2.components.Form;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

public class FormTag
extends AbstractClosingTag {
    private static final long serialVersionUID = 2792301046860819658L;
    protected String action;
    protected String target;
    protected String enctype;
    protected String method;
    protected String namespace;
    protected String validate;
    protected String onsubmit;
    protected String onreset;
    protected String portletMode;
    protected String windowState;
    protected String acceptcharset;
    protected String focusElement;
    protected boolean includeContext = true;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Form(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Form form = (Form)this.component;
        form.setAction(this.action);
        form.setTarget(this.target);
        form.setEnctype(this.enctype);
        form.setMethod(this.method);
        form.setNamespace(this.namespace);
        form.setValidate(this.validate);
        form.setOnreset(this.onreset);
        form.setOnsubmit(this.onsubmit);
        form.setPortletMode(this.portletMode);
        form.setWindowState(this.windowState);
        form.setAcceptcharset(this.acceptcharset);
        form.setFocusElement(this.focusElement);
        form.setIncludeContext(this.includeContext);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public void setOnsubmit(String onsubmit) {
        this.onsubmit = onsubmit;
    }

    public void setOnreset(String onreset) {
        this.onreset = onreset;
    }

    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    public void setAcceptcharset(String acceptcharset) {
        this.acceptcharset = acceptcharset;
    }

    public void setFocusElement(String focusElement) {
        this.focusElement = focusElement;
    }

    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
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
        this.target = null;
        this.enctype = null;
        this.method = null;
        this.namespace = null;
        this.validate = null;
        this.onsubmit = null;
        this.onreset = null;
        this.portletMode = null;
        this.windowState = null;
        this.acceptcharset = null;
        this.focusElement = null;
        this.includeContext = true;
    }
}

