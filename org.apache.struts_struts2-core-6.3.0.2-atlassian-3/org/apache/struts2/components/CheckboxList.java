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
import org.apache.struts2.components.ListUIBean;
import org.apache.struts2.views.annotations.StrutsTag;

@StrutsTag(name="checkboxlist", tldTagClass="org.apache.struts2.views.jsp.ui.CheckboxListTag", description="Render a list of checkboxes", allowDynamicAttributes=true)
public class CheckboxList
extends ListUIBean {
    public static final String TEMPLATE = "checkboxlist";

    public CheckboxList(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
    }

    @Override
    protected boolean lazyEvaluation() {
        return true;
    }
}

