/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.user.administrators;

import com.atlassian.confluence.util.i18n.Message;
import java.util.List;

public interface EditUserGroupAdministrator {
    public List<String> getInitialMemberGroups();

    public List<String> getReadOnlyGroups();

    public boolean checkPermissions(List<String> var1);

    public boolean updateGroups(List<String> var1);

    public List<Message> getErrors();
}

