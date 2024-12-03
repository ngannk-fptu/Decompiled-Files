/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.spaces.actions.EditSpaceLabelAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.LabelUtil;
import java.util.List;

public class AddLabelToSpaceAction
extends EditSpaceLabelAction {
    private static final String TYPE_SPACE = "space";
    private static final String TYPE_TEAM = "team";
    private String newSpaceLabel;
    private String newTeamLabel;
    private String labelType;

    @Override
    public void validate() {
        int totalLabelCountWithoutFavourites;
        int labelCountWithoutFavourites = LabelUtil.countLabelsWithoutFavourites(this.getNewLabel());
        if (labelCountWithoutFavourites > 20) {
            this.addActionError("labels.too.many.entries", labelCountWithoutFavourites, 20);
        }
        if ((totalLabelCountWithoutFavourites = LabelUtil.countLabelsWithoutFavourites(this.getSpaceLabelManager().getLabelsOnSpace(this.getSpace()))) > 500) {
            this.addActionError("labels.over.max", totalLabelCountWithoutFavourites, 500);
        }
    }

    public String execute() throws Exception {
        this.spaceManager.ensureSpaceDescriptionExists(this.getSpace());
        this.performSpaceLabelTransformations();
        List<String> labelNames = LabelUtil.split(this.getNewLabel());
        for (String labelName : labelNames) {
            if (LabelParser.parse(labelName) != null) continue;
            this.addActionError("label.invalid.name", HtmlUtil.htmlEncode(labelName));
        }
        if (this.hasActionErrors()) {
            return "error";
        }
        for (String labelName : labelNames) {
            Label label = this.addLabel(this.getSpace(), labelName);
            if (label != null) continue;
            this.addActionError("label.creation.failed", labelName);
        }
        return "success";
    }

    public String getNewLabel() {
        if (TYPE_SPACE.equals(this.labelType)) {
            return this.newSpaceLabel;
        }
        if (TYPE_TEAM.equals(this.labelType)) {
            return this.newTeamLabel;
        }
        this.addActionError("labels.invalid.type", this.labelType);
        return null;
    }

    public String getNewTeamLabel() {
        return this.newTeamLabel;
    }

    public void setNewTeamLabel(String newTeamLabel) {
        this.newTeamLabel = newTeamLabel;
    }

    public String getNewSpaceLabel() {
        return this.newSpaceLabel;
    }

    public void setNewSpaceLabel(String newSpaceLabel) {
        this.newSpaceLabel = newSpaceLabel;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public void performSpaceLabelTransformations() {
    }

    @Override
    protected List<String> getPermissionTypes() {
        List<String> permissionTypes = super.getPermissionTypes();
        if (this.getSpace() != null) {
            this.addPermissionTypeTo("VIEWSPACE", permissionTypes);
            this.addPermissionTypeTo("SETSPACEPERMISSIONS", permissionTypes);
        }
        return permissionTypes;
    }
}

