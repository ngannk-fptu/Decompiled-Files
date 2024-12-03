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
import org.apache.struts2.components.OptionTransferSelect;
import org.apache.struts2.views.jsp.ui.AbstractDoubleListTag;

public class OptionTransferSelectTag
extends AbstractDoubleListTag {
    private static final long serialVersionUID = 250474334495763536L;
    protected String allowAddToLeft;
    protected String allowAddToRight;
    protected String allowAddAllToLeft;
    protected String allowAddAllToRight;
    protected String allowSelectAll;
    protected String allowUpDownOnLeft;
    protected String allowUpDownOnRight;
    protected String leftTitle;
    protected String rightTitle;
    protected String buttonCssClass;
    protected String buttonCssStyle;
    protected String addToLeftLabel;
    protected String addToRightLabel;
    protected String addAllToLeftLabel;
    protected String addAllToRightLabel;
    protected String selectAllLabel;
    protected String leftUpLabel;
    protected String leftDownLabel;
    protected String rightUpLabel;
    protected String rightDownLabel;
    protected String addToLeftOnclick;
    protected String addToRightOnclick;
    protected String addAllToLeftOnclick;
    protected String addAllToRightOnclick;
    protected String selectAllOnclick;
    protected String upDownOnLeftOnclick;
    protected String upDownOnRightOnclick;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new OptionTransferSelect(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        OptionTransferSelect optionTransferSelect = (OptionTransferSelect)this.component;
        optionTransferSelect.setAllowAddToLeft(this.allowAddToLeft);
        optionTransferSelect.setAllowAddToRight(this.allowAddToRight);
        optionTransferSelect.setAllowAddAllToLeft(this.allowAddAllToLeft);
        optionTransferSelect.setAllowAddAllToRight(this.allowAddAllToRight);
        optionTransferSelect.setAllowSelectAll(this.allowSelectAll);
        optionTransferSelect.setAllowUpDownOnLeft(this.allowUpDownOnLeft);
        optionTransferSelect.setAllowUpDownOnRight(this.allowUpDownOnRight);
        optionTransferSelect.setAddToLeftLabel(this.addToLeftLabel);
        optionTransferSelect.setAddToRightLabel(this.addToRightLabel);
        optionTransferSelect.setAddAllToLeftLabel(this.addAllToLeftLabel);
        optionTransferSelect.setAddAllToRightLabel(this.addAllToRightLabel);
        optionTransferSelect.setSelectAllLabel(this.selectAllLabel);
        optionTransferSelect.setLeftUpLabel(this.leftUpLabel);
        optionTransferSelect.setLeftDownLabel(this.leftDownLabel);
        optionTransferSelect.setRightUpLabel(this.rightUpLabel);
        optionTransferSelect.setRightDownLabel(this.rightDownLabel);
        optionTransferSelect.setButtonCssClass(this.buttonCssClass);
        optionTransferSelect.setButtonCssStyle(this.buttonCssStyle);
        optionTransferSelect.setLeftTitle(this.leftTitle);
        optionTransferSelect.setRightTitle(this.rightTitle);
        optionTransferSelect.setAddToLeftOnclick(this.addToLeftOnclick);
        optionTransferSelect.setAddToRightOnclick(this.addToRightOnclick);
        optionTransferSelect.setAddAllToLeftOnclick(this.addAllToLeftOnclick);
        optionTransferSelect.setAddAllToRightOnclick(this.addAllToRightOnclick);
        optionTransferSelect.setSelectAllOnclick(this.selectAllOnclick);
        optionTransferSelect.setUpDownOnLeftOnclick(this.upDownOnLeftOnclick);
        optionTransferSelect.setUpDownOnRightOnclick(this.upDownOnRightOnclick);
    }

    public String getAddAllToLeftLabel() {
        return this.addAllToLeftLabel;
    }

    public void setAddAllToLeftLabel(String addAllToLeftLabel) {
        this.addAllToLeftLabel = addAllToLeftLabel;
    }

    public String getAddAllToRightLabel() {
        return this.addAllToRightLabel;
    }

    public void setAddAllToRightLabel(String addAllToRightLabel) {
        this.addAllToRightLabel = addAllToRightLabel;
    }

    public String getAddToLeftLabel() {
        return this.addToLeftLabel;
    }

    public void setAddToLeftLabel(String addToLeftLabel) {
        this.addToLeftLabel = addToLeftLabel;
    }

    public String getAddToRightLabel() {
        return this.addToRightLabel;
    }

    public void setAddToRightLabel(String addToRightLabel) {
        this.addToRightLabel = addToRightLabel;
    }

    public String getAllowAddAllToLeft() {
        return this.allowAddAllToLeft;
    }

    public void setAllowAddAllToLeft(String allowAddAllToLeft) {
        this.allowAddAllToLeft = allowAddAllToLeft;
    }

    public String getAllowAddAllToRight() {
        return this.allowAddAllToRight;
    }

    public void setAllowAddAllToRight(String allowAddAllToRight) {
        this.allowAddAllToRight = allowAddAllToRight;
    }

    public String getAllowAddToLeft() {
        return this.allowAddToLeft;
    }

    public void setAllowAddToLeft(String allowAddToLeft) {
        this.allowAddToLeft = allowAddToLeft;
    }

    public String getAllowAddToRight() {
        return this.allowAddToRight;
    }

    public void setAllowAddToRight(String allowAddToRight) {
        this.allowAddToRight = allowAddToRight;
    }

    public String getAllowUpDownOnLeft() {
        return this.allowUpDownOnLeft;
    }

    public void setAllowUpDownOnLeft(String allowUpDownOnLeft) {
        this.allowUpDownOnLeft = allowUpDownOnLeft;
    }

    public String getAllowUpDownOnRight() {
        return this.allowUpDownOnRight;
    }

    public void setAllowUpDownOnRight(String allowUpDownOnRight) {
        this.allowUpDownOnRight = allowUpDownOnRight;
    }

    public String getLeftUpLabel() {
        return this.leftUpLabel;
    }

    public void setLeftUpLabel(String leftUpLabel) {
        this.leftUpLabel = leftUpLabel;
    }

    public String getLeftDownLabel() {
        return this.leftDownLabel;
    }

    public void setLeftDownLabel(String leftDownLabel) {
        this.leftDownLabel = leftDownLabel;
    }

    public String getRightUpLabel() {
        return this.rightUpLabel;
    }

    public void setRightUpLabel(String rightUpLabel) {
        this.rightUpLabel = rightUpLabel;
    }

    public String getRightDownLabel() {
        return this.rightDownLabel;
    }

    public void setRightDownLabel(String rightDownLabel) {
        this.rightDownLabel = rightDownLabel;
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

    public void setAllowSelectAll(String allowSelectAll) {
        this.allowSelectAll = allowSelectAll;
    }

    public String getAllowSelectAll() {
        return this.allowSelectAll;
    }

    public void setSelectAllLabel(String selectAllLabel) {
        this.selectAllLabel = selectAllLabel;
    }

    public String getSelectAllLabel() {
        return this.selectAllLabel;
    }

    public void setButtonCssClass(String buttonCssId) {
        this.buttonCssClass = buttonCssId;
    }

    public String getButtonCssClass() {
        return this.buttonCssClass;
    }

    public void setButtonCssStyle(String buttonCssStyle) {
        this.buttonCssStyle = buttonCssStyle;
    }

    public String getButtonCssStyle() {
        return this.buttonCssStyle;
    }

    public String getAddAllToLeftOnclick() {
        return this.addAllToLeftOnclick;
    }

    public void setAddAllToLeftOnclick(String addAllToLeftOnclick) {
        this.addAllToLeftOnclick = addAllToLeftOnclick;
    }

    public String getAddAllToRightOnclick() {
        return this.addAllToRightOnclick;
    }

    public void setAddAllToRightOnclick(String addAllToRightOnclick) {
        this.addAllToRightOnclick = addAllToRightOnclick;
    }

    public String getAddToLeftOnclick() {
        return this.addToLeftOnclick;
    }

    public void setAddToLeftOnclick(String addToLeftOnclick) {
        this.addToLeftOnclick = addToLeftOnclick;
    }

    public String getAddToRightOnclick() {
        return this.addToRightOnclick;
    }

    public void setAddToRightOnclick(String addToRightOnclick) {
        this.addToRightOnclick = addToRightOnclick;
    }

    public String getUpDownOnLeftOnclick() {
        return this.upDownOnLeftOnclick;
    }

    public void setUpDownOnLeftOnclick(String upDownOnLeftOnclick) {
        this.upDownOnLeftOnclick = upDownOnLeftOnclick;
    }

    public String getUpDownOnRightOnclick() {
        return this.upDownOnRightOnclick;
    }

    public void setUpDownOnRightOnclick(String upDownOnRightOnclick) {
        this.upDownOnRightOnclick = upDownOnRightOnclick;
    }

    public void setSelectAllOnclick(String selectAllOnclick) {
        this.selectAllOnclick = selectAllOnclick;
    }

    public String getSelectAllOnclick() {
        return this.selectAllOnclick;
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
        this.allowAddToLeft = null;
        this.allowAddToRight = null;
        this.allowAddAllToLeft = null;
        this.allowAddAllToRight = null;
        this.allowSelectAll = null;
        this.allowUpDownOnLeft = null;
        this.allowUpDownOnRight = null;
        this.leftTitle = null;
        this.rightTitle = null;
        this.buttonCssClass = null;
        this.buttonCssStyle = null;
        this.addToLeftLabel = null;
        this.addToRightLabel = null;
        this.addAllToLeftLabel = null;
        this.addAllToRightLabel = null;
        this.selectAllLabel = null;
        this.leftUpLabel = null;
        this.leftDownLabel = null;
        this.rightUpLabel = null;
        this.rightDownLabel = null;
        this.addToLeftOnclick = null;
        this.addToRightOnclick = null;
        this.addAllToLeftOnclick = null;
        this.addAllToRightOnclick = null;
        this.selectAllOnclick = null;
        this.upDownOnLeftOnclick = null;
        this.upDownOnRightOnclick = null;
    }
}

