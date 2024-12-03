/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.ListUIBean;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

public abstract class AbstractListTag
extends AbstractUITag {
    protected String list;
    protected String listKey;
    protected String listValue;
    protected String listValueKey;
    protected String listLabelKey;
    protected String listCssClass;
    protected String listCssStyle;
    protected String listTitle;

    @Override
    protected void populateParams() {
        super.populateParams();
        ListUIBean listUIBean = (ListUIBean)this.component;
        listUIBean.setList(this.list);
        listUIBean.setListKey(this.listKey);
        listUIBean.setListValue(this.listValue);
        listUIBean.setListValueKey(this.listValueKey);
        listUIBean.setListLabelKey(this.listLabelKey);
        listUIBean.setListCssClass(this.listCssClass);
        listUIBean.setListCssStyle(this.listCssStyle);
        listUIBean.setListTitle(this.listTitle);
    }

    public void setList(String list) {
        this.list = list;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    public void setListValueKey(String listValueKey) {
        this.listValueKey = listValueKey;
    }

    public void setListLabelKey(String listLabelKey) {
        this.listLabelKey = listLabelKey;
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
        this.listKey = null;
        this.listValue = null;
        this.listValueKey = null;
        this.listLabelKey = null;
        this.listCssClass = null;
        this.listCssStyle = null;
        this.listTitle = null;
    }
}

