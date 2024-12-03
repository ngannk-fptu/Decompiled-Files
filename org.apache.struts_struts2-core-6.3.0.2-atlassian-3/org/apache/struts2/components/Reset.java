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
import org.apache.struts2.components.FormButton;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="reset", tldTagClass="org.apache.struts2.views.jsp.ui.ResetTag", description="Render a reset button", allowDynamicAttributes=true)
public class Reset
extends FormButton {
    public static final String TEMPLATE = "reset";
    protected String src;

    public Reset(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    public String getDefaultOpenTemplate() {
        return "empty";
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.src != null) {
            this.addParameter("src", this.findString(this.src));
        }
    }

    @Override
    public void evaluateParams() {
        if (this.value == null) {
            this.value = this.key != null ? "%{getText('" + this.key + "')}" : "Reset";
        }
        super.evaluateParams();
    }

    @Override
    protected boolean supportsImageType() {
        return false;
    }

    @Override
    @StrutsTagAttribute(description="Supply a reset button text apart from reset value. Will have no effect for <i>input</i> type reset, since button text will always be the value parameter.")
    public void setLabel(String label) {
        super.setLabel(label);
    }

    @StrutsTagAttribute(description="Supply an image src for <i>image</i> type reset button. Will have no effect for types <i>input</i> and <i>button</i>.")
    public void setSrc(String src) {
        this.src = src;
    }
}

