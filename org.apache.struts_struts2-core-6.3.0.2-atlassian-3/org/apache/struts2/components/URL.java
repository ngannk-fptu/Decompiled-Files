/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.ComponentUrlProvider;
import org.apache.struts2.components.ContextBean;
import org.apache.struts2.components.ExtraParameterProvider;
import org.apache.struts2.components.UrlProvider;
import org.apache.struts2.components.UrlRenderer;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="url", tldTagClass="org.apache.struts2.views.jsp.URLTag", description="This tag is used to create a URL")
public class URL
extends ContextBean {
    private UrlProvider urlProvider;
    private UrlRenderer urlRenderer;

    public URL(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.urlProvider = new ComponentUrlProvider(this, this.parameters);
        this.urlProvider.setHttpServletRequest(req);
        this.urlProvider.setHttpServletResponse(res);
    }

    @Inject(value="struts.url.includeParams")
    public void setUrlIncludeParams(String urlIncludeParams) {
        this.urlProvider.setUrlIncludeParams(urlIncludeParams);
    }

    @Inject
    public void setUrlRenderer(UrlRenderer urlRenderer) {
        this.urlProvider.setUrlRenderer(urlRenderer);
        this.urlRenderer = urlRenderer;
    }

    @Inject(required=false)
    public void setExtraParameterProvider(ExtraParameterProvider provider) {
        this.urlProvider.setExtraParameterProvider(provider);
    }

    @Override
    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        this.urlRenderer.beforeRenderUrl(this.urlProvider);
        return result;
    }

    @Override
    public boolean end(Writer writer, String body) {
        this.urlRenderer.renderUrl(writer, this.urlProvider);
        return super.end(writer, body);
    }

    @Override
    public String findString(String expr) {
        return super.findString(expr);
    }

    public UrlProvider getUrlProvider() {
        return this.urlProvider;
    }

    @StrutsTagAttribute(description="The includeParams attribute may have the value 'none', 'get' or 'all'", defaultValue="none")
    public void setIncludeParams(String includeParams) {
        this.urlProvider.setIncludeParams(includeParams);
    }

    @StrutsTagAttribute(description="Set scheme attribute")
    public void setScheme(String scheme) {
        this.urlProvider.setScheme(scheme);
    }

    @StrutsTagAttribute(description="The target value to use, if not using action")
    public void setValue(String value) {
        this.urlProvider.setValue(value);
    }

    @StrutsTagAttribute(description="The action to generate the URL for, if not using value")
    public void setAction(String action) {
        this.urlProvider.setAction(action);
    }

    @StrutsTagAttribute(description="The namespace to use")
    public void setNamespace(String namespace) {
        this.urlProvider.setNamespace(namespace);
    }

    @StrutsTagAttribute(description="The method of action to use")
    public void setMethod(String method) {
        this.urlProvider.setMethod(method);
    }

    @StrutsTagAttribute(description="Whether to encode parameters", type="Boolean", defaultValue="true")
    public void setEncode(boolean encode) {
        this.urlProvider.setEncode(encode);
    }

    @StrutsTagAttribute(description="Whether actual context should be included in URL", type="Boolean", defaultValue="true")
    public void setIncludeContext(boolean includeContext) {
        this.urlProvider.setIncludeContext(includeContext);
    }

    @StrutsTagAttribute(description="The resulting portlet mode")
    public void setPortletMode(String portletMode) {
        this.urlProvider.setPortletMode(portletMode);
    }

    @StrutsTagAttribute(description="The resulting portlet window state")
    public void setWindowState(String windowState) {
        this.urlProvider.setWindowState(windowState);
    }

    @StrutsTagAttribute(description="Specifies if this should be a portlet render or action URL. Default is \"render\". To create an action URL, use \"action\".")
    public void setPortletUrlType(String portletUrlType) {
        this.urlProvider.setPortletUrlType(portletUrlType);
    }

    @StrutsTagAttribute(description="The anchor for this URL")
    public void setAnchor(String anchor) {
        this.urlProvider.setAnchor(anchor);
    }

    @StrutsTagAttribute(description="Specifies whether to escape ampersand (&amp;) to (&amp;amp;) or not", type="Boolean", defaultValue="true")
    public void setEscapeAmp(boolean escapeAmp) {
        this.urlProvider.setEscapeAmp(escapeAmp);
    }

    @StrutsTagAttribute(description="Specifies whether to force the addition of scheme, host and port or not", type="Boolean", defaultValue="false")
    public void setForceAddSchemeHostAndPort(boolean forceAddSchemeHostAndPort) {
        this.urlProvider.setForceAddSchemeHostAndPort(forceAddSchemeHostAndPort);
    }
}

