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

@StrutsTag(name="datetextfield", tldTagClass="org.apache.struts2.views.jsp.ui.DateTextFieldTag", description="Render an HTML input fields with the date time", allowDynamicAttributes=true)
public class DateTextField
extends UIBean {
    public static final String TEMPLATE = "datetextfield";
    protected String format;

    public DateTextField(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.format != null) {
            this.addParameter("format", this.findValue(this.format, String.class));
        }
    }

    @StrutsTagAttribute(description="Date format attribute", required=true, type="String")
    public void setFormat(String format) {
        this.format = format;
    }

    protected Class getValueClassType() {
        return null;
    }
}

