/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.provisioning;

import java.util.List;
import org.springframework.security.core.GrantedAuthority;

public interface GroupManager {
    public List<String> findAllGroups();

    public List<String> findUsersInGroup(String var1);

    public void createGroup(String var1, List<GrantedAuthority> var2);

    public void deleteGroup(String var1);

    public void renameGroup(String var1, String var2);

    public void addUserToGroup(String var1, String var2);

    public void removeUserFromGroup(String var1, String var2);

    public List<GrantedAuthority> findGroupAuthorities(String var1);

    public void addGroupAuthority(String var1, GrantedAuthority var2);

    public void removeGroupAuthority(String var1, GrantedAuthority var2);
}

