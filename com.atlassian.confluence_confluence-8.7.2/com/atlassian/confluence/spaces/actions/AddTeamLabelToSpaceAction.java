/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.spaces.actions.AddLabelToSpaceAction;
import com.atlassian.confluence.util.LabelUtil;
import java.util.Iterator;
import java.util.List;

public class AddTeamLabelToSpaceAction
extends AddLabelToSpaceAction {
    @Override
    public void performSpaceLabelTransformations() {
        this.setNewTeamLabel(this.addTeamPrefixToAllLabels(this.getNewLabel()));
    }

    private String addTeamPrefixToAllLabels(String labelText) {
        StringBuilder result = new StringBuilder();
        List<String> labelNames = LabelUtil.split(labelText);
        Iterator iterator = labelNames.iterator();
        while (iterator.hasNext()) {
            String label = (String)iterator.next();
            result.append(this.addTeamPrefix(label));
            if (!iterator.hasNext()) continue;
            result.append(" ");
        }
        return result.toString();
    }

    private String addTeamPrefix(String label) {
        if (!((String)label).startsWith(LabelParser.TEAM_LABEL_PREFIX)) {
            label = LabelParser.TEAM_LABEL_PREFIX + (String)label;
        }
        return label;
    }
}

