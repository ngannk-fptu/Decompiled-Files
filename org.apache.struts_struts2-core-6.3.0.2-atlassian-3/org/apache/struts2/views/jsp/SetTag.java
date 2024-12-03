/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Set;
import org.apache.struts2.views.jsp.ContextBeanTag;

public class SetTag
extends ContextBeanTag {
    private static final long serialVersionUID = -5074213926790716974L;
    protected String scope;
    protected String value;
    protected boolean trimBody = true;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Set(stack);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Set set = (Set)this.component;
        set.setScope(this.scope);
        set.setValue(this.value);
    }

    public void setName(String name) {
        this.setVar(name);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTrimBody(boolean trimBody) {
        this.trimBody = trimBody;
    }

    @Override
    protected String getBody() {
        if (this.trimBody) {
            if (this.bodyContent == null) {
                return null;
            }
            return this.bodyContent.getString().trim();
        }
        return this.bodyContent == null ? null : this.bodyContent.getString();
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
        this.scope = null;
        this.value = null;
        this.trimBody = true;
    }
}

