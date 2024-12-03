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
import org.apache.struts2.components.UpDownSelect;
import org.apache.struts2.views.jsp.ui.SelectTag;

public class UpDownSelectTag
extends SelectTag {
    private static final long serialVersionUID = -8136573053799541353L;
    protected String allowMoveUp;
    protected String allowMoveDown;
    protected String allowSelectAll;
    protected String moveUpLabel;
    protected String moveDownLabel;
    protected String selectAllLabel;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new UpDownSelect(stack, req, res);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        UpDownSelect c = (UpDownSelect)this.component;
        c.setAllowMoveUp(this.allowMoveUp);
        c.setAllowMoveDown(this.allowMoveDown);
        c.setAllowSelectAll(this.allowSelectAll);
        c.setMoveUpLabel(this.moveUpLabel);
        c.setMoveDownLabel(this.moveDownLabel);
        c.setSelectAllLabel(this.selectAllLabel);
    }

    public String getAllowMoveUp() {
        return this.allowMoveUp;
    }

    public void setAllowMoveUp(String allowMoveUp) {
        this.allowMoveUp = allowMoveUp;
    }

    public String getAllowMoveDown() {
        return this.allowMoveDown;
    }

    public void setAllowMoveDown(String allowMoveDown) {
        this.allowMoveDown = allowMoveDown;
    }

    public String getAllowSelectAll() {
        return this.allowSelectAll;
    }

    public void setAllowSelectAll(String allowSelectAll) {
        this.allowSelectAll = allowSelectAll;
    }

    public String getMoveUpLabel() {
        return this.moveUpLabel;
    }

    public void setMoveUpLabel(String moveUpLabel) {
        this.moveUpLabel = moveUpLabel;
    }

    public String getMoveDownLabel() {
        return this.moveDownLabel;
    }

    public void setMoveDownLabel(String moveDownLabel) {
        this.moveDownLabel = moveDownLabel;
    }

    public String getSelectAllLabel() {
        return this.selectAllLabel;
    }

    public void setSelectAllLabel(String selectAllLabel) {
        this.selectAllLabel = selectAllLabel;
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
        this.allowMoveUp = null;
        this.allowMoveDown = null;
        this.allowSelectAll = null;
        this.moveUpLabel = null;
        this.moveDownLabel = null;
        this.selectAllLabel = null;
    }
}

