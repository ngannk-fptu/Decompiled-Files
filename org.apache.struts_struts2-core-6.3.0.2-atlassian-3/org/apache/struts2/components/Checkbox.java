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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="checkbox", tldTagClass="org.apache.struts2.views.jsp.ui.CheckboxTag", description="Render a checkbox input field", allowDynamicAttributes=true)
public class Checkbox
extends UIBean {
    private static final String ATTR_SUBMIT_UNCHECKED = "submitUnchecked";
    public static final String TEMPLATE = "checkbox";
    private String submitUncheckedGlobal;
    protected String fieldValue;
    protected String submitUnchecked;

    public Checkbox(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        if (this.fieldValue != null) {
            this.addParameter("fieldValue", this.findString(this.fieldValue));
        } else {
            this.addParameter("fieldValue", "true");
        }
        if (this.submitUnchecked != null) {
            Object parsedValue = this.findValue(this.submitUnchecked, Boolean.class);
            this.addParameter(ATTR_SUBMIT_UNCHECKED, parsedValue == null ? Boolean.valueOf(this.submitUnchecked) : parsedValue);
        } else if (this.submitUncheckedGlobal != null) {
            this.addParameter(ATTR_SUBMIT_UNCHECKED, Boolean.parseBoolean(this.submitUncheckedGlobal));
        } else {
            this.addParameter(ATTR_SUBMIT_UNCHECKED, false);
        }
    }

    @Override
    protected Class<?> getValueClassType() {
        return Boolean.class;
    }

    @Inject(value="struts.ui.checkbox.submitUnchecked", required=false)
    public void setSubmitUncheckedGlobal(String submitUncheckedGlobal) {
        this.submitUncheckedGlobal = submitUncheckedGlobal;
    }

    @StrutsTagAttribute(description="The actual HTML value attribute of the checkbox.", defaultValue="true")
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    @StrutsTagAttribute(description="If set to true, unchecked elements will be submitted with the form. Since Struts 6.1.1 you can use a constant \"struts.ui.checkbox.submitUnchecked\" to set this attribute globally", type="Boolean", defaultValue="false")
    public void setSubmitUnchecked(String submitUnchecked) {
        this.submitUnchecked = submitUnchecked;
    }

    @Override
    @StrutsTagAttribute(description="Define label position of form element (top/left), also 'right' is supported when using 'xhtml' theme")
    public void setLabelPosition(String labelPosition) {
        super.setLabelPosition(labelPosition);
    }
}

