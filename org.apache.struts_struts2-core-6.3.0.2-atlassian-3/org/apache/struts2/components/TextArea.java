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

@StrutsTag(name="textarea", tldTagClass="org.apache.struts2.views.jsp.ui.TextareaTag", description="Render HTML textarea tag.", allowDynamicAttributes=true)
public class TextArea
extends UIBean {
    public static final String TEMPLATE = "textarea";
    protected String cols;
    protected String readonly;
    protected String rows;
    protected String wrap;
    protected String maxlength;
    protected String minlength;

    public TextArea(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.readonly != null) {
            this.addParameter("readonly", this.findValue(this.readonly, Boolean.class));
        }
        if (this.cols != null) {
            this.addParameter("cols", this.findString(this.cols));
        }
        if (this.rows != null) {
            this.addParameter("rows", this.findString(this.rows));
        }
        if (this.wrap != null) {
            this.addParameter("wrap", this.findString(this.wrap));
        }
        if (this.maxlength != null) {
            this.addParameter("maxlength", this.findString(this.maxlength));
        }
        if (this.minlength != null) {
            this.addParameter("minlength", this.findString(this.minlength));
        }
    }

    @StrutsTagAttribute(description="HTML cols attribute", type="Integer")
    public void setCols(String cols) {
        this.cols = cols;
    }

    @StrutsTagAttribute(description="Whether the textarea is readonly", type="Boolean", defaultValue="false")
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    @StrutsTagAttribute(description="HTML rows attribute", type="Integer")
    public void setRows(String rows) {
        this.rows = rows;
    }

    @StrutsTagAttribute(description="HTML wrap attribute")
    public void setWrap(String wrap) {
        this.wrap = wrap;
    }

    @StrutsTagAttribute(description="HTML maxlength attribute", type="Integer")
    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    @StrutsTagAttribute(description="HTML minlength attribute", type="Integer")
    public void setMinlength(String minlength) {
        this.minlength = minlength;
    }
}

