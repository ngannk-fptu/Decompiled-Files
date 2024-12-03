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
import org.apache.struts2.util.TextProviderHelper;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="label", tldTagClass="org.apache.struts2.views.jsp.ui.LabelTag", description="Render a label that displays read-only information", allowDynamicAttributes=true)
public class Label
extends UIBean {
    public static final String TEMPLATE = "label";
    protected String forAttr;

    public Label(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.forAttr != null) {
            this.addParameter("for", this.findString(this.forAttr));
        }
        if (this.value != null) {
            this.addParameter("nameValue", this.findString(this.value));
        } else if (this.key != null) {
            Object nameValue = this.parameters.get("nameValue");
            if (nameValue == null || nameValue.toString().length() == 0) {
                String providedLabel = TextProviderHelper.getText(this.key, this.key, this.stack);
                this.addParameter("nameValue", providedLabel);
            }
        } else if (this.name != null) {
            String expr = this.completeExpression(this.name);
            this.addParameter("nameValue", expr);
        }
    }

    @StrutsTagAttribute(description=" HTML for attribute")
    public void setFor(String forAttr) {
        this.forAttr = forAttr;
    }
}

