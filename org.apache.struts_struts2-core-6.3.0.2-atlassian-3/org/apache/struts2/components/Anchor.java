/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.components.ComponentUrlProvider;
import org.apache.struts2.components.ExtraParameterProvider;
import org.apache.struts2.components.UrlProvider;
import org.apache.struts2.components.UrlRenderer;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="a", tldTagClass="org.apache.struts2.views.jsp.ui.AnchorTag", description="Render a HTML href element", allowDynamicAttributes=true)
public class Anchor
extends ClosingUIBean {
    private static final Logger LOG = LogManager.getLogger(Anchor.class);
    public static final String OPEN_TEMPLATE = "a";
    public static final String TEMPLATE = "a-close";
    public static final String COMPONENT_NAME = Anchor.class.getName();
    protected String href;
    protected UrlProvider urlProvider;
    protected UrlRenderer urlRenderer;
    protected boolean processingTagBody = false;
    protected Map urlParameters = new LinkedHashMap();

    public Anchor(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.urlProvider = new ComponentUrlProvider(this, this.urlParameters);
        this.urlProvider.setHttpServletRequest(request);
        this.urlProvider.setHttpServletResponse(response);
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public boolean usesBody() {
        return true;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.href != null) {
            this.addParameter("href", this.ensureAttributeSafelyNotEscaped(this.findString(this.href)));
        } else {
            StringWriter sw = new StringWriter();
            this.urlRenderer.beforeRenderUrl(this.urlProvider);
            this.urlRenderer.renderUrl(sw, this.urlProvider);
            String builtHref = sw.toString();
            if (StringUtils.isNotEmpty((CharSequence)builtHref)) {
                this.addParameter("href", this.ensureAttributeSafelyNotEscaped(builtHref));
            }
        }
        this.addParameter("escapeHtmlBody", this.escapeHtmlBody);
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
        this.processingTagBody = true;
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean end(Writer writer, String body) {
        this.processingTagBody = false;
        this.evaluateParams();
        try {
            this.addParameter("body", body);
            this.mergeTemplate(writer, this.buildTemplateName(this.template, this.getDefaultTemplate()));
        }
        catch (Exception e) {
            LOG.error("error when rendering", (Throwable)e);
        }
        finally {
            this.popComponentStack();
        }
        return false;
    }

    @Override
    public void addParameter(String key, Object value) {
        if (this.processingTagBody) {
            this.urlParameters.put(key, value);
        } else {
            super.addParameter(key, value);
        }
    }

    public void addAllParameters(Map params) {
        if (this.processingTagBody) {
            this.urlParameters.putAll(params);
        } else {
            super.addAllParameters(params);
        }
    }

    public UrlProvider getUrlProvider() {
        return this.urlProvider;
    }

    @StrutsTagAttribute(description="The URL.")
    public void setHref(String href) {
        this.href = href;
    }

    @StrutsTagAttribute(description="The includeParams attribute may have the value 'none', 'get' or 'all'", defaultValue="none")
    public void setIncludeParams(String includeParams) {
        this.urlProvider.setIncludeParams(includeParams);
    }

    @StrutsTagAttribute(description="Set scheme attribute")
    public void setScheme(String scheme) {
        this.urlProvider.setScheme(scheme);
    }

    @Override
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

    @StrutsTagAttribute(description="Specifies whether to HTML-escape the tag body or not", type="Boolean", defaultValue="false")
    public void setEscapeHtmlBody(boolean escapeHtmlBody) {
        this.escapeHtmlBody = escapeHtmlBody;
    }
}

