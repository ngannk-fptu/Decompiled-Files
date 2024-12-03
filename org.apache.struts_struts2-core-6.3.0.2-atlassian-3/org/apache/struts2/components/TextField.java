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

@StrutsTag(name="textfield", tldTagClass="org.apache.struts2.views.jsp.ui.TextFieldTag", description="Render an HTML input field of type text", allowDynamicAttributes=true)
public class TextField
extends UIBean {
    public static final String TEMPLATE = "text";
    protected String maxlength;
    protected String readonly;
    protected String size;
    protected String type;

    public TextField(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.size != null) {
            this.addParameter("size", this.findString(this.size));
        }
        if (this.maxlength != null) {
            this.addParameter("maxlength", this.findString(this.maxlength));
        }
        if (this.readonly != null) {
            this.addParameter("readonly", this.findValue(this.readonly, Boolean.class));
        }
        if (this.type != null) {
            this.addParameter("type", this.findString(this.type));
        }
    }

    @StrutsTagAttribute(description="HTML maxlength attribute", type="Integer")
    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    @StrutsTagAttribute(description="Deprecated. Use maxlength instead.", type="Integer")
    public void setMaxLength(String maxlength) {
        this.maxlength = maxlength;
    }

    @StrutsTagAttribute(description="Whether the input is readonly", type="Boolean", defaultValue="false")
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    @StrutsTagAttribute(description="HTML size attribute", type="Integer")
    public void setSize(String size) {
        this.size = size;
    }

    @StrutsTagAttribute(description="Specifies the html5 type element to display. e.g. text, email, url", defaultValue="text")
    public void setType(String type) {
        this.type = type;
    }
}

