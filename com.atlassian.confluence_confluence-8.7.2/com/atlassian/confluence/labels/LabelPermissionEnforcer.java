/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.user.User;
import java.util.List;

public interface LabelPermissionEnforcer {
    public boolean isLabelableByUser(Labelable var1);

    public boolean userCanEditLabel(ParsedLabelName var1, Labelable var2);

    public boolean userCanEditLabel(Label var1, Labelable var2);

    public boolean userCanEditLabelOrIsSpaceAdmin(Label var1, SpaceContentEntityObject var2);

    public boolean userCanViewObject(Labelable var1);

    public List filterVisibleLabels(List var1, User var2, boolean var3);

    public List filterLabelsByNamespace(List var1, User var2, Namespace var3);
}

