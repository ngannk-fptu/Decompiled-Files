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
import org.apache.struts2.components.TextField;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="password", tldTagClass="org.apache.struts2.views.jsp.ui.PasswordTag", description="Render an HTML input tag of type password", allowDynamicAttributes=true)
public class Password
extends TextField {
    public static final String TEMPLATE = "password";
    protected String showPassword;

    public Password(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.showPassword != null) {
            this.addParameter("showPassword", this.findValue(this.showPassword, Boolean.class));
        }
    }

    @StrutsTagAttribute(description="Whether to show input", type="Boolean", defaultValue="false")
    public void setShowPassword(String showPassword) {
        this.showPassword = showPassword;
    }
}

