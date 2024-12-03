/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user;

import com.atlassian.user.EntityException;
import com.atlassian.user.EntityManager;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.search.page.Pager;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface GroupManager
extends EntityManager {
    public Pager<Group> getGroups() throws EntityException;

    public Pager<Group> getGroups(User var1) throws EntityException;

    public List<Group> getWritableGroups();

    public Pager<String> getMemberNames(Group var1) throws EntityException;

    public Pager<String> getLocalMemberNames(Group var1) throws EntityException;

    public Pager<String> getExternalMemberNames(Group var1) throws EntityException;

    public Group getGroup(String var1) throws EntityException;

    public Group createGroup(String var1) throws EntityException;

    public void removeGroup(Group var1) throws EntityException;

    public void addMembership(Group var1, User var2) throws EntityException, IllegalArgumentException;

    public boolean hasMembership(Group var1, User var2) throws EntityException;

    public void removeMembership(Group var1, User var2) throws EntityException, IllegalArgumentException;

    public boolean supportsExternalMembership() throws EntityException;

    public boolean isReadOnly(Group var1) throws EntityException;
}

