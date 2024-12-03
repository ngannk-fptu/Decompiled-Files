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
import org.apache.struts2.components.InputTransferSelect;
import org.apache.struts2.views.jsp.ui.AbstractListTag;

public class InputTransferSelectTag
extends AbstractListTag {
    private static final long serialVersionUID = 250474334495763536L;
    protected String size;
    protected String multiple;
    protected String allowRemoveAll;
    protected String allowUpDown;
    protected String leftTitle;
    protected String rightTitle;
    protected String buttonCssClass;
    protected String buttonCssStyle;
    protected String addLabel;
    protected String removeLabel;
    protected String removeAllLabel;
    protected String upLabel;
    protected String downLabel;
    protected String headerKey;
    protected String headerValue;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new InputTransferSelect(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        InputTransferSelect inputTransferSelect = (InputTransferSelect)this.component;
        inputTransferSelect.setSize(this.size);
        inputTransferSelect.setMultiple(this.multiple);
        inputTransferSelect.setAllowRemoveAll(this.allowRemoveAll);
        inputTransferSelect.setAllowUpDown(this.allowUpDown);
        inputTransferSelect.setLeftTitle(this.leftTitle);
        inputTransferSelect.setRightTitle(this.rightTitle);
        inputTransferSelect.setButtonCssClass(this.buttonCssClass);
        inputTransferSelect.setButtonCssStyle(this.buttonCssStyle);
        inputTransferSelect.setAddLabel(this.addLabel);
        inputTransferSelect.setRemoveLabel(this.removeLabel);
        inputTransferSelect.setRemoveAllLabel(this.removeAllLabel);
        inputTransferSelect.setUpLabel(this.upLabel);
        inputTransferSelect.setDownLabel(this.downLabel);
        inputTransferSelect.setHeaderKey(this.headerKey);
        inputTransferSelect.setHeaderValue(this.headerValue);
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMultiple() {
        return this.multiple;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public String getAllowRemoveAll() {
        return this.allowRemoveAll;
    }

    public void setAllowRemoveAll(String allowRemoveAll) {
        this.allowRemoveAll = allowRemoveAll;
    }

    public String getAllowUpDown() {
        return this.allowUpDown;
    }

    public void setAllowUpDown(String allowUpDown) {
        this.allowUpDown = allowUpDown;
    }

    public String getLeftTitle() {
        return this.leftTitle;
    }

    public void setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
    }

    public String getRightTitle() {
        return this.rightTitle;
    }

    public void setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
    }

    public String getButtonCssClass() {
        return this.buttonCssClass;
    }

    public void setButtonCssClass(String buttonCssClass) {
        this.buttonCssClass = buttonCssClass;
    }

    public String getButtonCssStyle() {
        return this.buttonCssStyle;
    }

    public void setButtonCssStyle(String buttonCssStyle) {
        this.buttonCssStyle = buttonCssStyle;
    }

    public String getAddLabel() {
        return this.addLabel;
    }

    public void setAddLabel(String addLabel) {
        this.addLabel = addLabel;
    }

    public String getRemoveLabel() {
        return this.removeLabel;
    }

    public void setRemoveLabel(String removeLabel) {
        this.removeLabel = removeLabel;
    }

    public String getRemoveAllLabel() {
        return this.removeAllLabel;
    }

    public void setRemoveAllLabel(String removeAllLabel) {
        this.removeAllLabel = removeAllLabel;
    }

    public String getUpLabel() {
        return this.upLabel;
    }

    public void setUpLabel(String upLabel) {
        this.upLabel = upLabel;
    }

    public String getDownLabel() {
        return this.downLabel;
    }

    public void setDownLabel(String downLabel) {
        this.downLabel = downLabel;
    }

    public String getHeaderKey() {
        return this.headerKey;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public String getHeaderValue() {
        return this.headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
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
        this.size = null;
        this.multiple = null;
        this.allowRemoveAll = null;
        this.allowUpDown = null;
        this.leftTitle = null;
        this.rightTitle = null;
        this.buttonCssClass = null;
        this.buttonCssStyle = null;
        this.addLabel = null;
        this.removeLabel = null;
        this.removeAllLabel = null;
        this.upLabel = null;
        this.downLabel = null;
        this.headerKey = null;
        this.headerValue = null;
    }
}

