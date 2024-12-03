/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelPermissionEnforcer;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;

public class RemoveLabelFromSpaceAction
extends AbstractSpaceAction {
    private long labelId;
    private LabelPermissionEnforcer labelPermissionEnforcer;

    public String execute() throws Exception {
        Label label = this.getLabelManager().getLabel(this.getLabelId());
        if (label != null) {
            if (!this.labelPermissionEnforcer.userCanEditLabel(label, (Labelable)this.getSpace().getDescription())) {
                this.addActionError(this.getText("you.cannot.remove.this.label"));
                return "success";
            }
            this.getLabelManager().removeLabel(this.getSpace().getDescription(), label);
        }
        return "success";
    }

    public long getLabelId() {
        return this.labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }

    public void setLabelPermissionEnforcer(LabelPermissionEnforcer labelPermissionEnforcer) {
        this.labelPermissionEnforcer = labelPermissionEnforcer;
    }
}

