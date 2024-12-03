/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.actions;

import com.atlassian.confluence.security.actions.PermissionsAware;
import java.util.List;

public interface EditPermissionsAware
extends PermissionsAware {
    public void validate();

    public String execute() throws Exception;

    public List<String> getUsersToAddAsList();

    public void setUsersToAdd(String var1);

    public List<String> getGroupsToAddAsList();

    public void setGroupsToAdd(String var1);

    public void setGroupListToAdd(List<String> var1);

    public String getSelectedUsername();

    public void setSelectedUsername(String var1);

    public String getSelectedGroup();

    public void setSelectedGroup(String var1);
}

