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
import org.apache.struts2.components.OptGroup;
import org.apache.struts2.views.jsp.ComponentTagSupport;

public class OptGroupTag
extends ComponentTagSupport {
    private static final long serialVersionUID = 7367401003498678762L;
    protected String list;
    protected String label;
    protected String disabled;
    protected String listKey;
    protected String listValue;
    protected String listCssClass;
    protected String listCssStyle;
    protected String listTitle;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new OptGroup(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        OptGroup optGroup = (OptGroup)this.component;
        optGroup.setList(this.list);
        optGroup.setLabel(this.label);
        optGroup.setDisabled(this.disabled);
        optGroup.setListKey(this.listKey);
        optGroup.setListValue(this.listValue);
        optGroup.setListCssClass(this.listCssClass);
        optGroup.setListCssStyle(this.listCssStyle);
        optGroup.setListTitle(this.listTitle);
    }

    public void setList(String list) {
        this.list = list;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    public void setListCssClass(String listCssClass) {
        this.listCssClass = listCssClass;
    }

    public void setListCssStyle(String listCssStyle) {
        this.listCssStyle = listCssStyle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
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
        this.label = null;
        this.disabled = null;
        this.listKey = null;
        this.listValue = null;
        this.listCssClass = null;
        this.listCssStyle = null;
        this.listTitle = null;
    }
}

