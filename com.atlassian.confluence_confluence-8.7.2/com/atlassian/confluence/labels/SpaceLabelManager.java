/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;
import java.util.List;

public interface SpaceLabelManager {
    public Label addLabel(Space var1, String var2);

    public List getTeamLabelsOnSpace(String var1);

    public List getAvailableTeamLabels(String var1);

    public List getLabelsOnSpace(Space var1);

    public List getSuggestedLabelsForSpace(Space var1, User var2);
}

