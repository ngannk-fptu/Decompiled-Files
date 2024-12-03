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
import org.apache.struts2.components.ComboBox;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.TextFieldTag;

public class ComboBoxTag
extends TextFieldTag {
    private static final long serialVersionUID = 3509392460170385605L;
    protected String list;
    protected String listKey;
    protected String listValue;
    protected String headerKey;
    protected String headerValue;
    protected String emptyOption;

    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ComboBox(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        ((ComboBox)this.component).setList(this.list);
        ((ComboBox)this.component).setListKey(this.listKey);
        ((ComboBox)this.component).setListValue(this.listValue);
        ((ComboBox)this.component).setHeaderKey(this.headerKey);
        ((ComboBox)this.component).setHeaderValue(this.headerValue);
        ((ComboBox)this.component).setEmptyOption(this.emptyOption);
    }

    public void setList(String list) {
        this.list = list;
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
        this.list = null;
        this.listKey = null;
        this.listValue = null;
        this.headerKey = null;
        this.headerValue = null;
        this.emptyOption = null;
    }
}

