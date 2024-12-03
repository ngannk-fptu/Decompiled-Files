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
import org.apache.struts2.components.DateTextField;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public class DateTextFieldTag
extends AbstractUITag {
    private static final long serialVersionUID = 5811285953670562288L;
    protected String format;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DateTextField(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        DateTextField textField = (DateTextField)this.component;
        textField.setFormat(this.format);
    }

    public void setFormat(String format) {
        this.format = format;
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
        this.format = null;
    }
}

