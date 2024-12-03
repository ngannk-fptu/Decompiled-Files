/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.DoubleListUIBean;
import org.apache.struts2.views.jsp.ui.AbstractRequiredListTag;

public abstract class AbstractDoubleListTag
extends AbstractRequiredListTag {
    protected String doubleList;
    protected String doubleListKey;
    protected String doubleListValue;
    protected String doubleListCssClass;
    protected String doubleListCssStyle;
    protected String doubleListTitle;
    protected String doubleName;
    protected String doubleValue;
    protected String formName;
    protected String emptyOption;
    protected String headerKey;
    protected String headerValue;
    protected String multiple;
    protected String size;
    protected String doubleId;
    protected String doubleDisabled;
    protected String doubleMultiple;
    protected String doubleSize;
    protected String doubleHeaderKey;
    protected String doubleHeaderValue;
    protected String doubleEmptyOption;
    protected String doubleCssClass;
    protected String doubleCssStyle;
    protected String doubleOnclick;
    protected String doubleOndblclick;
    protected String doubleOnmousedown;
    protected String doubleOnmouseup;
    protected String doubleOnmouseover;
    protected String doubleOnmousemove;
    protected String doubleOnmouseout;
    protected String doubleOnfocus;
    protected String doubleOnblur;
    protected String doubleOnkeypress;
    protected String doubleOnkeydown;
    protected String doubleOnkeyup;
    protected String doubleOnselect;
    protected String doubleOnchange;
    protected String doubleAccesskey;

    @Override
    protected void populateParams() {
        super.populateParams();
        DoubleListUIBean bean = (DoubleListUIBean)this.component;
        bean.setDoubleList(this.doubleList);
        bean.setDoubleListKey(this.doubleListKey);
        bean.setDoubleListValue(this.doubleListValue);
        bean.setDoubleListCssClass(this.doubleListCssClass);
        bean.setDoubleListCssStyle(this.doubleListCssStyle);
        bean.setDoubleListTitle(this.doubleListTitle);
        bean.setDoubleName(this.doubleName);
        bean.setDoubleValue(this.doubleValue);
        bean.setFormName(this.formName);
        bean.setDoubleId(this.doubleId);
        bean.setDoubleDisabled(this.doubleDisabled);
        bean.setDoubleMultiple(this.doubleMultiple);
        bean.setDoubleSize(this.doubleSize);
        bean.setDoubleHeaderKey(this.doubleHeaderKey);
        bean.setDoubleHeaderValue(this.doubleHeaderValue);
        bean.setDoubleEmptyOption(this.doubleEmptyOption);
        bean.setDoubleCssClass(this.doubleCssClass);
        bean.setDoubleCssStyle(this.doubleCssStyle);
        bean.setDoubleOnclick(this.doubleOnclick);
        bean.setDoubleOndblclick(this.doubleOndblclick);
        bean.setDoubleOnmousedown(this.doubleOnmousedown);
        bean.setDoubleOnmouseup(this.doubleOnmouseup);
        bean.setDoubleOnmouseover(this.doubleOnmouseover);
        bean.setDoubleOnmousemove(this.doubleOnmousemove);
        bean.setDoubleOnmouseout(this.doubleOnmouseout);
        bean.setDoubleOnfocus(this.doubleOnfocus);
        bean.setDoubleOnblur(this.doubleOnblur);
        bean.setDoubleOnkeypress(this.doubleOnkeypress);
        bean.setDoubleOnkeydown(this.doubleOnkeydown);
        bean.setDoubleOnkeyup(this.doubleOnkeyup);
        bean.setDoubleOnselect(this.doubleOnselect);
        bean.setDoubleOnchange(this.doubleOnchange);
        bean.setDoubleAccesskey(this.doubleAccesskey);
        bean.setEmptyOption(this.emptyOption);
        bean.setHeaderKey(this.headerKey);
        bean.setHeaderValue(this.headerValue);
        bean.setMultiple(this.multiple);
        bean.setSize(this.size);
    }

    public void setDoubleList(String list) {
        this.doubleList = list;
    }

    public void setDoubleListKey(String listKey) {
        this.doubleListKey = listKey;
    }

    public void setDoubleListValue(String listValue) {
        this.doubleListValue = listValue;
    }

    public void setDoubleListCssClass(String doubleListCssClass) {
        this.doubleListCssClass = doubleListCssClass;
    }

    public void setDoubleListCssStyle(String doubleListCssStyle) {
        this.doubleListCssStyle = doubleListCssStyle;
    }

    public void setDoubleListTitle(String doubleListTitle) {
        this.doubleListTitle = doubleListTitle;
    }

    public void setDoubleName(String aName) {
        this.doubleName = aName;
    }

    public void setDoubleValue(String doubleValue) {
        this.doubleValue = doubleValue;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getDoubleCssClass() {
        return this.doubleCssClass;
    }

    public void setDoubleCssClass(String doubleCssClass) {
        this.doubleCssClass = doubleCssClass;
    }

    public String getDoubleCssStyle() {
        return this.doubleCssStyle;
    }

    public void setDoubleCssStyle(String doubleCssStyle) {
        this.doubleCssStyle = doubleCssStyle;
    }

    public String getDoubleDisabled() {
        return this.doubleDisabled;
    }

    public void setDoubleDisabled(String doubleDisabled) {
        this.doubleDisabled = doubleDisabled;
    }

    public String getDoubleEmptyOption() {
        return this.doubleEmptyOption;
    }

    public void setDoubleEmptyOption(String doubleEmptyOption) {
        this.doubleEmptyOption = doubleEmptyOption;
    }

    public String getDoubleHeaderKey() {
        return this.doubleHeaderKey;
    }

    public void setDoubleHeaderKey(String doubleHeaderKey) {
        this.doubleHeaderKey = doubleHeaderKey;
    }

    public String getDoubleHeaderValue() {
        return this.doubleHeaderValue;
    }

    public void setDoubleHeaderValue(String doubleHeaderValue) {
        this.doubleHeaderValue = doubleHeaderValue;
    }

    public String getDoubleId() {
        return this.doubleId;
    }

    public void setDoubleId(String doubleId) {
        this.doubleId = doubleId;
    }

    public String getDoubleMultiple() {
        return this.doubleMultiple;
    }

    public void setDoubleMultiple(String doubleMultiple) {
        this.doubleMultiple = doubleMultiple;
    }

    public String getDoubleOnblur() {
        return this.doubleOnblur;
    }

    public void setDoubleOnblur(String doubleOnblur) {
        this.doubleOnblur = doubleOnblur;
    }

    public String getDoubleOnchange() {
        return this.doubleOnchange;
    }

    public void setDoubleOnchange(String doubleOnchange) {
        this.doubleOnchange = doubleOnchange;
    }

    public String getDoubleOnclick() {
        return this.doubleOnclick;
    }

    public void setDoubleOnclick(String doubleOnclick) {
        this.doubleOnclick = doubleOnclick;
    }

    public String getDoubleOndblclick() {
        return this.doubleOndblclick;
    }

    public void setDoubleOndblclick(String doubleOndblclick) {
        this.doubleOndblclick = doubleOndblclick;
    }

    public String getDoubleOnfocus() {
        return this.doubleOnfocus;
    }

    public void setDoubleOnfocus(String doubleOnfocus) {
        this.doubleOnfocus = doubleOnfocus;
    }

    public String getDoubleOnkeydown() {
        return this.doubleOnkeydown;
    }

    public void setDoubleOnkeydown(String doubleOnkeydown) {
        this.doubleOnkeydown = doubleOnkeydown;
    }

    public String getDoubleOnkeypress() {
        return this.doubleOnkeypress;
    }

    public void setDoubleOnkeypress(String doubleOnkeypress) {
        this.doubleOnkeypress = doubleOnkeypress;
    }

    public String getDoubleOnkeyup() {
        return this.doubleOnkeyup;
    }

    public void setDoubleOnkeyup(String doubleOnkeyup) {
        this.doubleOnkeyup = doubleOnkeyup;
    }

    public String getDoubleOnmousedown() {
        return this.doubleOnmousedown;
    }

    public void setDoubleOnmousedown(String doubleOnmousedown) {
        this.doubleOnmousedown = doubleOnmousedown;
    }

    public String getDoubleOnmousemove() {
        return this.doubleOnmousemove;
    }

    public void setDoubleOnmousemove(String doubleOnmousemove) {
        this.doubleOnmousemove = doubleOnmousemove;
    }

    public String getDoubleOnmouseout() {
        return this.doubleOnmouseout;
    }

    public void setDoubleOnmouseout(String doubleOnmouseout) {
        this.doubleOnmouseout = doubleOnmouseout;
    }

    public String getDoubleOnmouseover() {
        return this.doubleOnmouseover;
    }

    public void setDoubleOnmouseover(String doubleOnmouseover) {
        this.doubleOnmouseover = doubleOnmouseover;
    }

    public String getDoubleOnmouseup() {
        return this.doubleOnmouseup;
    }

    public void setDoubleOnmouseup(String doubleOnmouseup) {
        this.doubleOnmouseup = doubleOnmouseup;
    }

    public String getDoubleOnselect() {
        return this.doubleOnselect;
    }

    public void setDoubleOnselect(String doubleOnselect) {
        this.doubleOnselect = doubleOnselect;
    }

    public String getDoubleSize() {
        return this.doubleSize;
    }

    public void setDoubleSize(String doubleSize) {
        this.doubleSize = doubleSize;
    }

    public String getDoubleList() {
        return this.doubleList;
    }

    public String getDoubleListKey() {
        return this.doubleListKey;
    }

    public String getDoubleListValue() {
        return this.doubleListValue;
    }

    public String getDoubleName() {
        return this.doubleName;
    }

    public String getDoubleValue() {
        return this.doubleValue;
    }

    public String getFormName() {
        return this.formName;
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

    public void setDoubleAccesskey(String doubleAccesskey) {
        this.doubleAccesskey = doubleAccesskey;
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
        this.doubleList = null;
        this.doubleListKey = null;
        this.doubleListValue = null;
        this.doubleListCssClass = null;
        this.doubleListCssStyle = null;
        this.doubleListTitle = null;
        this.doubleName = null;
        this.doubleValue = null;
        this.formName = null;
        this.emptyOption = null;
        this.headerKey = null;
        this.headerValue = null;
        this.multiple = null;
        this.size = null;
        this.doubleId = null;
        this.doubleDisabled = null;
        this.doubleMultiple = null;
        this.doubleSize = null;
        this.doubleHeaderKey = null;
        this.doubleHeaderValue = null;
        this.doubleEmptyOption = null;
        this.doubleCssClass = null;
        this.doubleCssStyle = null;
        this.doubleOnclick = null;
        this.doubleOndblclick = null;
        this.doubleOnmousedown = null;
        this.doubleOnmouseup = null;
        this.doubleOnmouseover = null;
        this.doubleOnmousemove = null;
        this.doubleOnmouseout = null;
        this.doubleOnfocus = null;
        this.doubleOnblur = null;
        this.doubleOnkeypress = null;
        this.doubleOnkeydown = null;
        this.doubleOnkeyup = null;
        this.doubleOnselect = null;
        this.doubleOnchange = null;
        this.doubleAccesskey = null;
    }
}

