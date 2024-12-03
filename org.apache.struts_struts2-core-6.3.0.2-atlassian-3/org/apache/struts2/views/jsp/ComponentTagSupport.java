/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.jsp.JspException
 */
package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

public abstract class ComponentTagSupport
extends StrutsBodyTagSupport {
    protected Component component;

    public abstract Component getBean(ValueStack var1, HttpServletRequest var2, HttpServletResponse var3);

    @Override
    public int doEndTag() throws JspException {
        this.component.end((Writer)this.pageContext.getOut(), this.getBody());
        this.component = null;
        this.clearTagStateForTagPoolingServers();
        return 6;
    }

    public int doStartTag() throws JspException {
        ValueStack stack = this.getStack();
        this.component = this.getBean(stack, (HttpServletRequest)this.pageContext.getRequest(), (HttpServletResponse)this.pageContext.getResponse());
        Container container = stack.getActionContext().getContainer();
        container.inject(this.component);
        this.populateParams();
        boolean evalBody = this.component.start((Writer)this.pageContext.getOut());
        if (evalBody) {
            return this.component.usesBody() ? 2 : 1;
        }
        return 0;
    }

    protected void populateParams() {
        this.populatePerformClearTagStateForTagPoolingServersParam();
    }

    protected void populatePerformClearTagStateForTagPoolingServersParam() {
        if (this.component != null) {
            this.component.setPerformClearTagStateForTagPoolingServers(super.getPerformClearTagStateForTagPoolingServers());
        }
    }

    public Component getComponent() {
        return this.component;
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
        if (!this.getPerformClearTagStateForTagPoolingServers()) {
            return;
        }
        super.clearTagStateForTagPoolingServers();
        this.component = null;
    }
}

