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
import org.apache.struts2.components.Select;
import org.apache.struts2.views.jsp.ui.AbstractRequiredListTag;

public class SelectTag
extends AbstractRequiredListTag {
    private static final long serialVersionUID = 6121715260335609618L;
    protected String emptyOption;
    protected String headerKey;
    protected String headerValue;
    protected String multiple;
    protected String size;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Select(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Select select = (Select)this.component;
        select.setEmptyOption(this.emptyOption);
        select.setHeaderKey(this.headerKey);
        select.setHeaderValue(this.headerValue);
        select.setMultiple(this.multiple);
        select.setSize(this.size);
    }

    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public void setSize(String size) {
        this.size = size;
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
        this.emptyOption = null;
        this.headerKey = null;
        this.headerValue = null;
        this.multiple = null;
        this.size = null;
    }
}

