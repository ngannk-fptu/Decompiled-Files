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
import org.apache.struts2.components.URL;
import org.apache.struts2.views.jsp.ContextBeanTag;

public class URLTag
extends ContextBeanTag {
    private static final long serialVersionUID = 1722460444125206226L;
    protected String includeParams;
    protected String scheme;
    protected String value;
    protected String action;
    protected String namespace;
    protected String method;
    protected String encode;
    protected String includeContext;
    protected String escapeAmp;
    protected String portletMode;
    protected String windowState;
    protected String portletUrlType;
    protected String anchor;
    protected String forceAddSchemeHostAndPort;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new URL(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        URL url = (URL)this.component;
        url.setIncludeParams(this.includeParams);
        url.setScheme(this.scheme);
        url.setValue(this.value);
        url.setMethod(this.method);
        url.setNamespace(this.namespace);
        url.setAction(this.action);
        url.setPortletMode(this.portletMode);
        url.setPortletUrlType(this.portletUrlType);
        url.setWindowState(this.windowState);
        url.setAnchor(this.anchor);
        if (this.encode != null) {
            url.setEncode(Boolean.valueOf(this.encode));
        }
        if (this.includeContext != null) {
            url.setIncludeContext(Boolean.valueOf(this.includeContext));
        }
        if (this.escapeAmp != null) {
            url.setEscapeAmp(Boolean.valueOf(this.escapeAmp));
        }
        if (this.forceAddSchemeHostAndPort != null) {
            url.setForceAddSchemeHostAndPort(Boolean.valueOf(this.forceAddSchemeHostAndPort));
        }
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public void setIncludeContext(String includeContext) {
        this.includeContext = includeContext;
    }

    public void setEscapeAmp(String escapeAmp) {
        this.escapeAmp = escapeAmp;
    }

    public void setIncludeParams(String name) {
        this.includeParams = name;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    public void setPortletUrlType(String portletUrlType) {
        this.portletUrlType = portletUrlType;
    }

    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public void setForceAddSchemeHostAndPort(String forceAddSchemeHostAndPort) {
        this.forceAddSchemeHostAndPort = forceAddSchemeHostAndPort;
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
        this.includeParams = null;
        this.scheme = null;
        this.value = null;
        this.action = null;
        this.namespace = null;
        this.method = null;
        this.encode = null;
        this.includeContext = null;
        this.escapeAmp = null;
        this.portletMode = null;
        this.windowState = null;
        this.portletUrlType = null;
        this.anchor = null;
        this.forceAddSchemeHostAndPort = null;
    }
}

