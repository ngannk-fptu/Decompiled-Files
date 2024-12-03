/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.FieldError;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public class FieldErrorTag
extends AbstractUITag {
    private static final long serialVersionUID = -182532967507726323L;
    protected String fieldName;
    protected boolean escape = true;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new FieldError(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        FieldError fieldError = (FieldError)this.component;
        fieldError.setFieldName(this.fieldName);
        fieldError.setEscape(this.escape);
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }

    @Override
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
        if (!this.getPerformClearTagStateForTagPoolingServers()) {
            return;
        }
        super.clearTagStateForTagPoolingServers();
        this.fieldName = null;
        this.escape = true;
    }
}

