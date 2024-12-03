/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.jackrabbit.authorization;

import java.security.Principal;
import java.util.HashSet;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;

public class AccessControlUtils {
    public static Privilege[] privilegesFromNames(Session session, String ... privilegeNames) throws RepositoryException {
        return AccessControlUtils.privilegesFromNames(session.getAccessControlManager(), privilegeNames);
    }

    public static Privilege[] privilegesFromNames(AccessControlManager accessControlManager, String ... privilegeNames) throws RepositoryException {
        HashSet<Privilege> privileges = new HashSet<Privilege>(privilegeNames.length);
        for (String privName : privilegeNames) {
            privileges.add(accessControlManager.privilegeFromName(privName));
        }
        return privileges.toArray(new Privilege[privileges.size()]);
    }

    public static String[] namesFromPrivileges(Privilege ... privileges) {
        if (privileges == null || privileges.length == 0) {
            return new String[0];
        }
        String[] names = new String[privileges.length];
        for (int i = 0; i < privileges.length; ++i) {
            names[i] = privileges[i].getName();
        }
        return names;
    }

    public static JackrabbitAccessControlList getAccessControlList(Session session, String absPath) throws RepositoryException {
        AccessControlManager acMgr = session.getAccessControlManager();
        return AccessControlUtils.getAccessControlList(acMgr, absPath);
    }

    public static JackrabbitAccessControlList getAccessControlList(AccessControlManager accessControlManager, String absPath) throws RepositoryException {
        AccessControlPolicy[] pcls;
        AccessControlPolicyIterator itr = accessControlManager.getApplicablePolicies(absPath);
        while (itr.hasNext()) {
            AccessControlPolicy policy = itr.nextAccessControlPolicy();
            if (!(policy instanceof JackrabbitAccessControlList)) continue;
            return (JackrabbitAccessControlList)policy;
        }
        for (AccessControlPolicy policy : pcls = accessControlManager.getPolicies(absPath)) {
            if (!(policy instanceof JackrabbitAccessControlList)) continue;
            return (JackrabbitAccessControlList)policy;
        }
        return null;
    }

    public static boolean addAccessControlEntry(Session session, String absPath, Principal principal, String[] privilegeNames, boolean isAllow) throws RepositoryException {
        return AccessControlUtils.addAccessControlEntry(session, absPath, principal, AccessControlUtils.privilegesFromNames(session, privilegeNames), isAllow);
    }

    public static boolean addAccessControlEntry(Session session, String absPath, Principal principal, Privilege[] privileges, boolean isAllow) throws RepositoryException {
        JackrabbitAccessControlList acl = AccessControlUtils.getAccessControlList(session, absPath);
        if (acl != null && acl.addEntry(principal, privileges, isAllow)) {
            session.getAccessControlManager().setPolicy(absPath, acl);
            return true;
        }
        return false;
    }

    public static boolean grantAllToEveryone(Session session, String absPath) throws RepositoryException {
        Principal everyone = AccessControlUtils.getEveryonePrincipal(session);
        Privilege[] privileges = AccessControlUtils.privilegesFromNames(session, "{http://www.jcp.org/jcr/1.0}all");
        return AccessControlUtils.addAccessControlEntry(session, absPath, everyone, privileges, true);
    }

    public static boolean denyAllToEveryone(Session session, String absPath) throws RepositoryException {
        Principal everyone = AccessControlUtils.getEveryonePrincipal(session);
        Privilege[] privileges = AccessControlUtils.privilegesFromNames(session, "{http://www.jcp.org/jcr/1.0}all");
        return AccessControlUtils.addAccessControlEntry(session, absPath, everyone, privileges, false);
    }

    public static boolean allow(Node node, String principalName, String ... privileges) throws RepositoryException {
        return AccessControlUtils.addAccessControlEntry(node.getSession(), node.getPath(), AccessControlUtils.getPrincipal(node.getSession(), principalName), privileges, true);
    }

    public static boolean deny(Node node, String principalName, String ... privileges) throws RepositoryException {
        return AccessControlUtils.addAccessControlEntry(node.getSession(), node.getPath(), AccessControlUtils.getPrincipal(node.getSession(), principalName), privileges, false);
    }

    public static boolean clear(Session session, String absPath, String principalName) throws RepositoryException {
        AccessControlPolicy[] pcls;
        AccessControlManager acm = session.getAccessControlManager();
        JackrabbitAccessControlList acl = null;
        for (AccessControlPolicy policy : pcls = acm.getPolicies(absPath)) {
            if (!(policy instanceof JackrabbitAccessControlList)) continue;
            acl = (JackrabbitAccessControlList)policy;
        }
        if (acl != null) {
            if (principalName == null) {
                acm.removePolicy(absPath, acl);
                return true;
            }
            Principal principal = AccessControlUtils.getPrincipal(session, principalName);
            if (principal == null) {
                return false;
            }
            boolean removedEntries = false;
            for (AccessControlEntry ace : acl.getAccessControlEntries()) {
                if (!ace.getPrincipal().equals(principal)) continue;
                acl.removeAccessControlEntry(ace);
                removedEntries = true;
            }
            if (removedEntries) {
                acm.setPolicy(absPath, acl);
                return true;
            }
        }
        return false;
    }

    public static boolean clear(Node node, String principalName) throws RepositoryException {
        return AccessControlUtils.clear(node.getSession(), node.getPath(), principalName);
    }

    public static boolean clear(Node node) throws RepositoryException {
        return AccessControlUtils.clear(node, null);
    }

    public static boolean clear(Session session, String absPath) throws RepositoryException {
        return AccessControlUtils.clear(session, absPath, null);
    }

    public static Principal getPrincipal(Session session, String principalName) throws RepositoryException {
        if (session instanceof JackrabbitSession) {
            return ((JackrabbitSession)session).getPrincipalManager().getPrincipal(principalName);
        }
        throw new UnsupportedOperationException("Failed to retrieve principal: JackrabbitSession expected.");
    }

    public static Principal getEveryonePrincipal(Session session) throws RepositoryException {
        if (session instanceof JackrabbitSession) {
            return ((JackrabbitSession)session).getPrincipalManager().getEveryone();
        }
        throw new UnsupportedOperationException("Failed to retrieve everyone principal: JackrabbitSession expected.");
    }
}

