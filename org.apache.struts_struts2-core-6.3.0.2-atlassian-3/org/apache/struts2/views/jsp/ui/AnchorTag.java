/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.BooleanUtils
 */
package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.struts2.components.Anchor;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

public class AnchorTag
extends AbstractClosingTag {
    private static final long serialVersionUID = -1034616578492431113L;
    protected String href;
    protected String includeParams;
    protected String scheme;
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
    protected String escapeHtmlBody;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Anchor(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Anchor tag = (Anchor)this.component;
        tag.setHref(this.href);
        tag.setIncludeParams(this.includeParams);
        tag.setScheme(this.scheme);
        tag.setValue(this.value);
        tag.setMethod(this.method);
        tag.setNamespace(this.namespace);
        tag.setAction(this.action);
        tag.setPortletMode(this.portletMode);
        tag.setPortletUrlType(this.portletUrlType);
        tag.setWindowState(this.windowState);
        tag.setAnchor(this.anchor);
        if (this.encode != null) {
            tag.setEncode(BooleanUtils.toBoolean((String)this.encode));
        }
        if (this.includeContext != null) {
            tag.setIncludeContext(BooleanUtils.toBoolean((String)this.includeContext));
        }
        if (this.escapeAmp != null) {
            tag.setEscapeAmp(BooleanUtils.toBoolean((String)this.escapeAmp));
        }
        if (this.forceAddSchemeHostAndPort != null) {
            tag.setForceAddSchemeHostAndPort(BooleanUtils.toBoolean((String)this.forceAddSchemeHostAndPort));
        }
        if (this.escapeHtmlBody != null) {
            tag.setEscapeHtmlBody(this.escapeHtmlBody);
        }
    }

    public void setHref(String href) {
        this.href = href;
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

    @Override
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

    public void setEscapeHtmlBody(String escapeHtmlBody) {
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
        this.href = null;
        this.includeParams = null;
        this.scheme = null;
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

