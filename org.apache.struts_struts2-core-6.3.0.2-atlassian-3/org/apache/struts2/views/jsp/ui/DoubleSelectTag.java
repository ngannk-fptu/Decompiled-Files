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
import org.apache.struts2.components.DoubleSelect;
import org.apache.struts2.views.jsp.ui.AbstractDoubleListTag;

public class DoubleSelectTag
extends AbstractDoubleListTag {
    private static final long serialVersionUID = 7426011596359509386L;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new DoubleSelect(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        DoubleSelect doubleSelect = (DoubleSelect)this.component;
        doubleSelect.setEmptyOption(this.emptyOption);
        doubleSelect.setHeaderKey(this.headerKey);
        doubleSelect.setHeaderValue(this.headerValue);
        doubleSelect.setMultiple(this.multiple);
        doubleSelect.setSize(this.size);
    }

    @Override
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }
}

