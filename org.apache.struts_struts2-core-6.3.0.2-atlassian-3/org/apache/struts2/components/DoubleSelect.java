/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.components.DoubleListUIBean;
import org.apache.struts2.views.annotations.StrutsTag;

@StrutsTag(name="doubleselect", tldTagClass="org.apache.struts2.views.jsp.ui.DoubleSelectTag", description="Renders two HTML select elements with second one changing displayed values depending on selected entry of first one.")
public class DoubleSelect
extends DoubleListUIBean {
    public static final String TEMPLATE = "doubleselect";

    public DoubleSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        StringBuilder onchangeParam = new StringBuilder();
        onchangeParam.append(this.getParameters().get("id")).append("Redirect(this.selectedIndex)");
        if (StringUtils.isNotEmpty((CharSequence)this.onchange)) {
            onchangeParam.append(";").append(this.onchange);
        }
        this.addParameter("onchange", onchangeParam.toString());
    }
}

