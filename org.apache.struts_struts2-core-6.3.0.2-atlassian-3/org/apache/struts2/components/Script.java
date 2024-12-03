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
import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="script", tldTagClass="org.apache.struts2.views.jsp.ui.ScriptTag", description="Script tag automatically adds nonces to script blocks - should be used in combination with Struts' CSP Interceptor.", allowDynamicAttributes=true)
public class Script
extends ClosingUIBean {
    protected String async;
    protected String charset;
    protected String defer;
    protected String src;
    protected String type;
    protected String referrerpolicy;
    protected String nomodule;
    protected String integrity;
    protected String crossorigin;
    private static final String TEMPLATE = "script-close";
    private static final String OPEN_TEMPLATE = "script";

    public Script(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @StrutsTagAttribute(description="HTML script async attribute")
    public void setAsync(String async) {
        this.async = async;
    }

    @StrutsTagAttribute(description="HTML script charset attribute")
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @StrutsTagAttribute(description="HTML script defer attribute")
    public void setDefer(String defer) {
        this.defer = defer;
    }

    @StrutsTagAttribute(description="HTML script src attribute")
    public void setSrc(String src) {
        this.src = src;
    }

    @StrutsTagAttribute(description="HTML script type attribute")
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description="HTML script referrerpolicy attribute")
    public void setReferrerpolicy(String referrerpolicy) {
        this.referrerpolicy = referrerpolicy;
    }

    @StrutsTagAttribute(description="HTML script nomodule attribute")
    public void setNomodule(String nomodule) {
        this.nomodule = nomodule;
    }

    @StrutsTagAttribute(description="HTML script integrity attribute")
    public void setIntegrity(String integrity) {
        this.integrity = integrity;
    }

    @StrutsTagAttribute(description="HTML script crossorigin attribute")
    public void setCrossorigin(String crossorigin) {
        this.crossorigin = crossorigin;
    }

    @Override
    public boolean usesBody() {
        return true;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.async != null) {
            this.addParameter("async", this.findString(this.async));
        }
        if (this.charset != null) {
            this.addParameter("charset", this.findString(this.charset));
        }
        if (this.defer != null) {
            this.addParameter("defer", this.findString(this.defer));
        }
        if (this.src != null) {
            this.addParameter("src", this.findString(this.src));
        }
        if (this.type != null) {
            this.addParameter("type", this.findString(this.type));
        }
        if (this.referrerpolicy != null) {
            this.addParameter("referrerpolicy", this.findString(this.referrerpolicy));
        }
        if (this.nomodule != null) {
            this.addParameter("nomodule", this.findString(this.nomodule));
        }
        if (this.integrity != null) {
            this.addParameter("integrity", this.findString(this.integrity));
        }
        if (this.crossorigin != null) {
            this.addParameter("crossorigin", this.findString(this.crossorigin));
        }
    }
}

