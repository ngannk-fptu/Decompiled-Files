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
import org.apache.struts2.components.Reset;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public class ResetTag
extends AbstractUITag {
    private static final long serialVersionUID = 4742704832277392108L;
    protected String action;
    protected String method;
    protected String type;
    protected String src;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Reset(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Reset reset = (Reset)this.component;
        reset.setAction(this.action);
        reset.setMethod(this.method);
        reset.setType(this.type);
        reset.setSrc(this.src);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSrc(String src) {
        this.src = src;
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

