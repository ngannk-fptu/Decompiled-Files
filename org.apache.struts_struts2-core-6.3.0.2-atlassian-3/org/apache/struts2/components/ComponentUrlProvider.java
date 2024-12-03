/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.components.ExtraParameterProvider;
import org.apache.struts2.components.UrlProvider;
import org.apache.struts2.components.UrlRenderer;

public class ComponentUrlProvider
implements UrlProvider {
    protected HttpServletRequest httpServletRequest;
    protected HttpServletResponse httpServletResponse;
    protected String includeParams;
    protected String scheme;
    protected String value;
    protected String action;
    protected String namespace;
    protected String method;
    protected boolean encode = true;
    protected boolean includeContext = true;
    protected boolean escapeAmp = true;
    protected String portletMode;
    protected String windowState;
    protected String portletUrlType;
    protected String anchor;
    protected boolean forceAddSchemeHostAndPort;
    protected String urlIncludeParams;
    protected ExtraParameterProvider extraParameterProvider;
    protected UrlRenderer urlRenderer;
    protected Component component;
    private Map parameters;

    public ComponentUrlProvider(Component component, Map parameters) {
        this.component = component;
        this.parameters = parameters;
    }

    public String determineActionURL(String action, String namespace, String method, HttpServletRequest req, HttpServletResponse res, Map parameters, String scheme, boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort, boolean escapeAmp) {
        return this.component.determineActionURL(action, namespace, method, req, res, parameters, scheme, includeContext, encodeResult, forceAddSchemeHostAndPort, escapeAmp);
    }

    @Override
    public String determineNamespace(String namespace, ValueStack stack, HttpServletRequest req) {
        return this.component.determineNamespace(namespace, stack, req);
    }

    @Override
    public String findString(String expr) {
        return this.component.findString(expr);
    }

    public Map getParameters() {
        return this.parameters;
    }

    @Override
    public HttpServletRequest getHttpServletRequest() {
        return this.httpServletRequest;
    }

    @Override
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public HttpServletResponse getHttpServletResponse() {
        return this.httpServletResponse;
    }

    @Override
    public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public String getIncludeParams() {
        return this.includeParams;
    }

    @Override
    public void setIncludeParams(String includeParams) {
        this.includeParams = includeParams;
    }

    @Override
    public String getScheme() {
        return this.scheme;
    }

    @Override
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public boolean isPutInContext() {
        return this.component instanceof ContextBean;
    }

    @Override
    public String getVar() {
        return this.isPutInContext() ? ((ContextBean)this.component).getVar() : null;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getAction() {
        return this.action;
    }

    @Override
    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public boolean isEncode() {
        return this.encode;
    }

    @Override
    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    @Override
    public boolean isIncludeContext() {
        return this.includeContext;
    }

    @Override
    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }

    @Override
    public boolean isEscapeAmp() {
        return this.escapeAmp;
    }

    @Override
    public void setEscapeAmp(boolean escapeAmp) {
        this.escapeAmp = escapeAmp;
    }

    @Override
    public String getPortletMode() {
        return this.portletMode;
    }

    @Override
    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    @Override
    public String getWindowState() {
        return this.windowState;
    }

    @Override
    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    @Override
    public String getPortletUrlType() {
        return this.portletUrlType;
    }

    @Override
    public ValueStack getStack() {
        return this.component.getStack();
    }

    @Override
    public void setPortletUrlType(String portletUrlType) {
        this.portletUrlType = portletUrlType;
    }

    @Override
    public String getAnchor() {
        return this.anchor;
    }

    @Override
    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    @Override
    public boolean isForceAddSchemeHostAndPort() {
        return this.forceAddSchemeHostAndPort;
    }

    @Override
    public void setForceAddSchemeHostAndPort(boolean forceAddSchemeHostAndPort) {
        this.forceAddSchemeHostAndPort = forceAddSchemeHostAndPort;
    }

    @Override
    public void putInContext(String result) {
        if (this.isPutInContext()) {
            ((ContextBean)this.component).putInContext(result);
        }
    }

    @Override
    public String getUrlIncludeParams() {
        return this.urlIncludeParams;
    }

    @Override
    public void setUrlIncludeParams(String urlIncludeParams) {
        this.urlIncludeParams = urlIncludeParams;
    }

    @Override
    public ExtraParameterProvider getExtraParameterProvider() {
        return this.extraParameterProvider;
    }

    @Override
    public void setExtraParameterProvider(ExtraParameterProvider extraParameterProvider) {
        this.extraParameterProvider = extraParameterProvider;
    }

    public UrlRenderer getUrlRenderer() {
        return this.urlRenderer;
    }

    @Override
    public void setUrlRenderer(UrlRenderer urlRenderer) {
        this.urlRenderer = urlRenderer;
    }
}

