/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Param;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

@StrutsTag(name="fielderror", tldTagClass="org.apache.struts2.views.jsp.ui.FieldErrorTag", description="Render field error (all or partial depending on param tag nested)if they exists")
public class FieldError
extends UIBean
implements Param.UnnamedParametric {
    private List<String> errorFieldNames = new ArrayList<String>();
    private boolean escape = true;
    private static final String TEMPLATE = "fielderror";

    public FieldError(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (this.errorFieldNames != null) {
            this.addParameter("errorFieldNames", this.errorFieldNames);
        }
        this.addParameter("escape", this.escape);
    }

    @Override
    public void addParameter(Object value) {
        if (value != null) {
            this.errorFieldNames.add(value.toString());
        }
    }

    public List<String> getFieldErrorFieldNames() {
        return this.errorFieldNames;
    }

    @StrutsTagAttribute(description="Field name for single field attribute usage", type="String")
    public void setFieldName(String fieldName) {
        this.addParameter(fieldName);
    }

    @StrutsTagAttribute(description=" Whether to escape HTML", type="Boolean", defaultValue="true")
    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}

