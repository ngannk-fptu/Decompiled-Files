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
import org.apache.struts2.components.TextArea;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public class TextareaTag
extends AbstractUITag {
    private static final long serialVersionUID = -4107122506712927927L;
    protected String cols;
    protected String readonly;
    protected String rows;
    protected String wrap;
    protected String maxlength;
    protected String minlength;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new TextArea(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        TextArea textArea = (TextArea)this.component;
        textArea.setCols(this.cols);
        textArea.setReadonly(this.readonly);
        textArea.setRows(this.rows);
        textArea.setWrap(this.wrap);
        textArea.setMaxlength(this.maxlength);
        textArea.setMinlength(this.minlength);
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public void setWrap(String wrap) {
        this.wrap = wrap;
    }

    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    public void setMinlength(String minlength) {
        this.minlength = minlength;
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
        this.cols = null;
        this.readonly = null;
        this.rows = null;
        this.wrap = null;
        this.maxlength = null;
        this.minlength = null;
    }
}

