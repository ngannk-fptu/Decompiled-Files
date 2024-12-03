/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="link", tldTagClass="org.apache.struts2.views.jsp.ui.LinkTag", description="Link tag automatically adds nonces to link elements - should be used in combination with Struts' CSP Interceptor.", allowDynamicAttributes=true)
public class Link
extends UIBean {
    private static final String TEMPLATE = "link";
    protected String href;
    protected String hreflang;
    protected String rel;
    protected String media;
    protected String referrerpolicy;
    protected String sizes;
    protected String crossorigin;
    protected String type;
    protected String as;

    public Link(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @StrutsTagAttribute(description="HTML link href attribute")
    public void setHref(String href) {
        this.href = href;
    }

    @StrutsTagAttribute(description="HTML link hreflang attribute")
    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    @StrutsTagAttribute(description="HTML link rel attribute")
    public void setRel(String rel) {
        this.rel = rel;
    }

    @StrutsTagAttribute(description="HTML link sizes attribute")
    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    @StrutsTagAttribute(description="HTML link crossorigin attribute")
    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }

    @StrutsTagAttribute(description="HTML link type attribute")
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description="HTML link as attribute")
    public void setAs(String as) {
        this.as = as;
    }

    @StrutsTagAttribute(description="HTML link media attribute")
    public void setMedia(String media) {
        this.media = media;
    }

    @StrutsTagAttribute(description="HTML link referrerpolicy attribute")
    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.href != null) {
            this.addParameter("href", this.findString(this.href));
        }
        if (this.hreflang != null) {
            this.addParameter("hreflang", this.findString(this.hreflang));
        }
        if (this.rel != null) {
            this.addParameter("rel", this.findString(this.rel));
        }
        if (this.media != null) {
            this.addParameter("media", this.findString(this.media));
        }
        if (this.referrerpolicy != null) {
            this.addParameter("referrerpolicy", this.findString(this.referrerpolicy));
        }
        if (this.sizes != null) {
            this.addParameter("sizes", this.findString(this.sizes));
        }
        if (this.crossorigin != null) {
            this.addParameter("crossorigin", this.findString(this.crossorigin));
        }
        if (this.type != null) {
            this.addParameter("type", this.findString(this.type));
        }
        if (this.as != null) {
            this.addParameter("as", this.findString(this.as));
        }
        if (this.disabled != null) {
            this.addParameter("disabled", this.findString(this.disabled));
        }
        if (this.title != null) {
            this.addParameter("title", this.findString(this.title));
        }
    }
}

