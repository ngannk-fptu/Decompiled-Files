/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 */
package com.atlassian.confluence.plugins.gatekeeper.model.global;

import com.atlassian.confluence.plugins.gatekeeper.model.Copiable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;

public class GlobalPermissions
implements Copiable<GlobalPermissions> {
    private boolean anonymousCanUse;
    private Set<String> groupCanUseSet = new ObjectOpenHashSet();
    private Set<String> userCanUseSet = new ObjectOpenHashSet();

    public boolean getAnonymousCanUse() {
        return this.anonymousCanUse;
    }

    public void setAnonymousCanUse() {
        this.anonymousCanUse = true;
    }

    public void unsetAnonymousCanUse() {
        this.anonymousCanUse = false;
    }

    public Set<String> getGroupsCanUse() {
        return this.groupCanUseSet;
    }

    public void setGroupCanUse(String groupName) {
        groupName = groupName.intern();
        this.groupCanUseSet.add(groupName);
    }

    public void unsetGroupCanUse(String groupName) {
        this.groupCanUseSet.remove(groupName);
    }

    public Set<String> getUsersCanUse() {
        return this.userCanUseSet;
    }

    public void setUserCanUse(String username) {
        username = username.intern();
        this.userCanUseSet.add(username);
    }

    public void unsetUserCanUse(String groupname) {
        this.userCanUseSet.remove(groupname);
    }

    public void renameUser(String oldUsername, String newUsername) {
        if (this.userCanUseSet.remove(oldUsername)) {
            newUsername = newUsername.intern();
            this.userCanUseSet.add(newUsername);
        }
    }

    @Override
    public GlobalPermissions copy() {
        GlobalPermissions result = new GlobalPermissions();
        result.anonymousCanUse = this.anonymousCanUse;
        result.groupCanUseSet = new ObjectOpenHashSet(this.groupCanUseSet);
        result.userCanUseSet = new ObjectOpenHashSet(this.userCanUseSet);
        return result;
    }
}

