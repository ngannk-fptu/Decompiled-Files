/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  weblogic.management.security.authentication.GroupMemberListerMBean
 *  weblogic.management.security.authentication.MemberGroupListerMBean
 *  weblogic.management.security.authentication.UserReaderMBean
 */
package com.opensymphony.user.provider.weblogic;

import com.opensymphony.user.provider.AccessProvider;
import com.opensymphony.user.provider.weblogic.WeblogicProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weblogic.management.security.authentication.GroupMemberListerMBean;
import weblogic.management.security.authentication.MemberGroupListerMBean;
import weblogic.management.security.authentication.UserReaderMBean;

public class WeblogicAccessProvider
extends WeblogicProvider
implements AccessProvider {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$user$provider$weblogic$WeblogicAccessProvider == null ? (class$com$opensymphony$user$provider$weblogic$WeblogicAccessProvider = WeblogicAccessProvider.class$("com.opensymphony.user.provider.weblogic.WeblogicAccessProvider")) : class$com$opensymphony$user$provider$weblogic$WeblogicAccessProvider));
    static /* synthetic */ Class class$com$opensymphony$user$provider$weblogic$WeblogicAccessProvider;

    public boolean addToGroup(String username, String groupname) {
        return false;
    }

    public boolean create(String name) {
        return false;
    }

    public boolean handles(String name) {
        try {
            Iterator i = this.userReaders.iterator();
            while (i.hasNext()) {
                if (!((UserReaderMBean)i.next()).userExists(name)) continue;
                return true;
            }
            i = this.groupMemberListers.iterator();
            while (i.hasNext()) {
                GroupMemberListerMBean groupMemberLister = (GroupMemberListerMBean)i.next();
                if (!groupMemberLister.groupExists(name)) continue;
                return true;
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean inGroup(String username, String groupname) {
        try {
            Iterator i = this.memberGroupListers.iterator();
            while (i.hasNext()) {
                if (!((MemberGroupListerMBean)i.next()).isMember(groupname, username, true)) continue;
                return true;
            }
            return false;
        }
        catch (Exception ex) {
            log.error((Object)("Error checking inGroup(" + username + ", " + groupname + ")"), (Throwable)ex);
            return false;
        }
    }

    public List list() {
        try {
            ArrayList<String> groups = new ArrayList<String>();
            Iterator i = this.memberGroupListers.iterator();
            while (i.hasNext()) {
                MemberGroupListerMBean memberGroupLister = (MemberGroupListerMBean)i.next();
                String cursor = memberGroupLister.listGroups("*", this.maxRecords);
                while (memberGroupLister.haveCurrent(cursor)) {
                    String groupName = memberGroupLister.getCurrentName(cursor);
                    if (!groups.contains(groupName)) {
                        groups.add(groupName);
                    }
                    memberGroupLister.advance(cursor);
                }
                memberGroupLister.close(cursor);
            }
            return Collections.unmodifiableList(groups);
        }
        catch (Exception ex) {
            log.error((Object)"Error getting list of groups", (Throwable)ex);
            return null;
        }
    }

    public List listGroupsContainingUser(String username) {
        try {
            ArrayList<String> groups = new ArrayList<String>();
            Iterator i = this.memberGroupListers.iterator();
            while (i.hasNext()) {
                MemberGroupListerMBean memberGroupLister = (MemberGroupListerMBean)i.next();
                String cursor = memberGroupLister.listMemberGroups(username);
                while (memberGroupLister.haveCurrent(cursor)) {
                    String userName = memberGroupLister.getCurrentName(cursor);
                    groups.add(userName);
                    memberGroupLister.advance(cursor);
                }
                memberGroupLister.close(cursor);
            }
            return groups;
        }
        catch (Exception ex) {
            log.error((Object)("Error listing groups of user " + username), (Throwable)ex);
            return null;
        }
    }

    public List listUsersInGroup(String groupname) {
        try {
            ArrayList<String> users = new ArrayList<String>();
            Iterator i = this.groupMemberListers.iterator();
            while (i.hasNext()) {
                GroupMemberListerMBean groupMemberLister = (GroupMemberListerMBean)i.next();
                String cursor = groupMemberLister.listGroupMembers(groupname, "*", this.maxRecords);
                while (groupMemberLister.haveCurrent(cursor)) {
                    String userName = groupMemberLister.getCurrentName(cursor);
                    users.add(userName);
                    groupMemberLister.advance(cursor);
                }
                groupMemberLister.close(cursor);
            }
            return users;
        }
        catch (Exception ex) {
            log.error((Object)("Error listing members of group " + groupname), (Throwable)ex);
            return null;
        }
    }

    public boolean remove(String name) {
        return false;
    }

    public boolean removeFromGroup(String username, String groupname) {
        return false;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

