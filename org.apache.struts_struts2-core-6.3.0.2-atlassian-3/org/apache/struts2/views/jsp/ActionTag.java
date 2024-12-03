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
import org.apache.struts2.components.ActionComponent;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ContextBeanTag;

public class ActionTag
extends ContextBeanTag {
    private static final long serialVersionUID = -5384167073331678855L;
    protected String name;
    protected String namespace;
    protected boolean executeResult;
    protected boolean ignoreContextParams;
    protected boolean flush = true;
    protected boolean rethrowException;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ActionComponent(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        ActionComponent action = (ActionComponent)this.component;
        action.setName(this.name);
        action.setNamespace(this.namespace);
        action.setExecuteResult(this.executeResult);
        action.setIgnoreContextParams(this.ignoreContextParams);
        action.setFlush(this.flush);
        action.setRethrowException(this.rethrowException);
    }

    protected void addParameter(String name, Object value) {
        ActionComponent ac = (ActionComponent)this.component;
        ac.addParameter(name, value);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    public void setIgnoreContextParams(boolean ignoreContextParams) {
        this.ignoreContextParams = ignoreContextParams;
    }

    public void setFlush(boolean flush) {
        this.flush = flush;
    }

    public boolean getFlush() {
        return this.flush;
    }

    public void setRethrowException(boolean rethrowException) {
        this.rethrowException = rethrowException;
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
        this.name = null;
        this.namespace = null;
        this.executeResult = false;
        this.ignoreContextParams = false;
        this.flush = true;
        this.rethrowException = false;
    }
}

