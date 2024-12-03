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
import org.apache.struts2.components.TextField;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public class TextFieldTag
extends AbstractUITag {
    private static final long serialVersionUID = 5811285953670562288L;
    protected String maxlength;
    protected String readonly;
    protected String size;
    protected String type;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TextField(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        TextField textField = (TextField)this.component;
        textField.setMaxlength(this.maxlength);
        textField.setReadonly(this.readonly);
        textField.setSize(this.size);
        textField.setType(this.type);
    }

    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setType(String type) {
        this.type = type;
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
        this.maxlength = null;
        this.readonly = null;
        this.size = null;
        this.type = null;
    }
}

