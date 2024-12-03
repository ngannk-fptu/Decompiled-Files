/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.log4j.Logger;
import org.bedework.access.Access;
import org.bedework.access.Acl;
import org.bedework.access.PrivilegeSet;

public class EvaluatedAccessCache
implements Serializable {
    private static transient Logger log;
    private static final Object synch;
    private static Map<String, AccessorsMap> ownerHrefs;
    private static LinkedList<String> accessorQueue;
    private static Access.AccessStatsEntry accessorQueueLen;
    private static Access.AccessStatsEntry numGets;
    private static Access.AccessStatsEntry numHits;
    private static Access.AccessStatsEntry numAclTables;
    private static Access.AccessStatsEntry numEntries;
    private static Collection<Access.AccessStatsEntry> stats;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Acl.CurrentAccess get(String ownerHref, String accessorHref, PrivilegeSet desiredPriv, PrivilegeSet maxAccess, String acl) {
        ++EvaluatedAccessCache.numGets.count;
        Object object = synch;
        synchronized (object) {
            AccessorsMap accessors = ownerHrefs.get(ownerHref);
            if (accessors == null) {
                return null;
            }
            PrivSetMap desiredPrivs = (PrivSetMap)accessors.get(accessorHref);
            if (desiredPrivs == null) {
                return null;
            }
            accessorQueue.remove(accessorHref);
            accessorQueue.add(accessorHref);
            PrivMap maxPrivs = (PrivMap)desiredPrivs.get(desiredPriv);
            if (maxPrivs == null) {
                return null;
            }
            AccessMap acls = (AccessMap)maxPrivs.get(maxAccess);
            if (acls == null) {
                return null;
            }
            Acl.CurrentAccess ca = (Acl.CurrentAccess)acls.get(acl);
            if (ca != null) {
                ++EvaluatedAccessCache.numHits.count;
            }
            return ca;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void put(String ownerHref, String accessorHref, PrivilegeSet desiredPriv, PrivilegeSet maxAccess, String acl, Acl.CurrentAccess ca) {
        boolean found = true;
        Object object = synch;
        synchronized (object) {
            Acl.CurrentAccess tca;
            AccessorsMap accessors = ownerHrefs.get(ownerHref);
            if (accessors == null) {
                accessors = new AccessorsMap();
                ownerHrefs.put(ownerHref, accessors);
                found = false;
            }
            accessorQueue.remove(accessorHref);
            accessorQueue.add(accessorHref);
            PrivSetMap desiredPrivs = null;
            if (found) {
                desiredPrivs = (PrivSetMap)accessors.get(accessorHref);
            }
            if (desiredPrivs == null) {
                desiredPrivs = new PrivSetMap();
                accessors.put(accessorHref, desiredPrivs);
                found = false;
            }
            PrivMap maxPrivs = null;
            if (found) {
                maxPrivs = (PrivMap)desiredPrivs.get(desiredPriv);
            }
            if (maxPrivs == null) {
                maxPrivs = new PrivMap();
                desiredPrivs.put(desiredPriv, maxPrivs);
                found = false;
            }
            AccessMap acls = null;
            if (found) {
                acls = (AccessMap)maxPrivs.get(maxAccess);
            }
            if (acls == null) {
                acls = new AccessMap();
                maxPrivs.put(maxAccess, acls);
                ++EvaluatedAccessCache.numAclTables.count;
                found = false;
            }
            if (found && (tca = (Acl.CurrentAccess)acls.get(acl)) != null && !tca.equals(ca)) {
                EvaluatedAccessCache.error("Current access in table does not match, table:" + tca + " new version " + ca);
            }
            ++EvaluatedAccessCache.numEntries.count;
            acls.put(acl, ca);
        }
    }

    public static Collection<Access.AccessStatsEntry> getStatistics() {
        EvaluatedAccessCache.accessorQueueLen.count = accessorQueue.size();
        return stats;
    }

    private static Logger getLog() {
        if (log == null) {
            log = Logger.getLogger(EvaluatedAccessCache.class.getName());
        }
        return log;
    }

    private static void error(String msg) {
        EvaluatedAccessCache.getLog().error(msg);
    }

    static {
        synch = new Object();
        ownerHrefs = new HashMap<String, AccessorsMap>();
        accessorQueue = new LinkedList();
        accessorQueueLen = new Access.AccessStatsEntry("Access cache accessor queue len");
        numGets = new Access.AccessStatsEntry("Access cache gets");
        numHits = new Access.AccessStatsEntry("Access cache hits");
        numAclTables = new Access.AccessStatsEntry("Access cache ACL tables");
        numEntries = new Access.AccessStatsEntry("Access cache entries");
        stats = new ArrayList<Access.AccessStatsEntry>();
        stats.add(accessorQueueLen);
        stats.add(numGets);
        stats.add(numHits);
        stats.add(numAclTables);
        stats.add(numEntries);
    }

    private static class AccessorsMap
    extends HashMap<String, PrivSetMap> {
        private AccessorsMap() {
        }
    }

    private static class PrivSetMap
    extends HashMap<PrivilegeSet, PrivMap> {
        private PrivSetMap() {
        }
    }

    private static class PrivMap
    extends HashMap<PrivilegeSet, AccessMap> {
        private PrivMap() {
        }
    }

    private static class AccessMap
    extends HashMap<String, Acl.CurrentAccess> {
        private AccessMap() {
        }
    }
}

