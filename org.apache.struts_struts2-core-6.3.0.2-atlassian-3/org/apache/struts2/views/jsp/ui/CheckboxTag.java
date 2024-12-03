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
import org.apache.struts2.components.Checkbox;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public class CheckboxTag
extends AbstractUITag {
    private static final long serialVersionUID = -350752809266337636L;
    protected String fieldValue;
    protected String submitUnchecked;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Checkbox(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        ((Checkbox)this.component).setFieldValue(this.fieldValue);
        ((Checkbox)this.component).setSubmitUnchecked(this.submitUnchecked);
    }

    public void setFieldValue(String aValue) {
        this.fieldValue = aValue;
    }

    public void setSubmitUnchecked(String aValue) {
        this.submitUnchecked = aValue;
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
        this.fieldValue = null;
    }
}

