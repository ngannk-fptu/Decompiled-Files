/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.bedework.access.Access;
import org.bedework.access.AccessException;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.Ace;
import org.bedework.access.AceWho;
import org.bedework.access.EncodedAcl;
import org.bedework.access.EvaluatedAccessCache;
import org.bedework.access.Privilege;
import org.bedework.access.PrivilegeDefs;
import org.bedework.access.PrivilegeSet;
import org.bedework.util.caching.ObjectPool;
import org.bedework.util.misc.Util;

public class Acl
extends EncodedAcl
implements PrivilegeDefs {
    static boolean debug;
    private TreeMap<AceWho, Ace> aces;
    private static ObjectPool<PrivilegeSet> privSets;
    private static boolean usePool;
    private static Access.AccessStatsEntry evaluations;
    public static CurrentAccess defaultNonOwnerAccess;

    public Acl(Collection<Ace> aces) {
        debug = Acl.getLog().isDebugEnabled();
        this.aces = new TreeMap();
        for (Ace ace : aces) {
            this.aces.put(ace.getWho(), ace);
        }
    }

    public static Collection<Access.AccessStatsEntry> getStatistics() {
        ArrayList<Access.AccessStatsEntry> stats = new ArrayList<Access.AccessStatsEntry>();
        stats.add(evaluations);
        stats.addAll(Ace.getStatistics());
        stats.addAll(EvaluatedAccessCache.getStatistics());
        return stats;
    }

    public static CurrentAccess forceAccessAllowed(CurrentAccess ca) {
        CurrentAccess newCa = new CurrentAccess(true);
        newCa.acl = ca.acl;
        CurrentAccess.access$102(newCa, ca.aclChars);
        newCa.privileges = ca.privileges;
        return newCa;
    }

    public static CurrentAccess evaluateAccess(Access.AccessCb cb, AccessPrincipal who, AccessPrincipal owner, Privilege[] how, char[] aclChars, PrivilegeSet filter) throws AccessException {
        String aclString = new String(aclChars);
        PrivilegeSet howPriv = PrivilegeSet.makePrivilegeSet(how);
        CurrentAccess ca = EvaluatedAccessCache.get(owner.getPrincipalRef(), who.getPrincipalRef(), howPriv, filter, aclString);
        if (ca != null) {
            return ca;
        }
        ca = Acl.evaluateAccessInt(cb, who, owner, how, aclChars, filter);
        if (ca == null) {
            return null;
        }
        EvaluatedAccessCache.put(owner.getPrincipalRef(), who.getPrincipalRef(), howPriv, filter, aclString, ca);
        return ca;
    }

    /*
     * Unable to fully structure code
     */
    private static CurrentAccess evaluateAccessInt(Access.AccessCb cb, AccessPrincipal who, AccessPrincipal owner, Privilege[] how, char[] aclChars, PrivilegeSet filter) throws AccessException {
        ++Acl.evaluations.count;
        authenticated = who.getUnauthenticated() == false;
        isOwner = false;
        ca = new CurrentAccess();
        acl = Acl.decode(aclChars);
        CurrentAccess.access$002(ca, acl);
        CurrentAccess.access$102(ca, aclChars);
        if (authenticated) {
            isOwner = who.equals(owner);
        }
        debugsb = null;
        if (Acl.debug) {
            debugsb = new StringBuilder("Check access for '");
            if (aclChars == null) {
                debugsb.append("NULL");
            } else {
                debugsb.append(new String(aclChars));
            }
            debugsb.append("'\n");
            if (authenticated) {
                debugsb.append("   with authenticated principal ");
                debugsb.append(who.getPrincipalRef());
            } else {
                debugsb.append("   unauthenticated ");
            }
            debugsb.append(" isOwner = ");
            debugsb.append(isOwner);
            debugsb.append("'\n");
        }
        if (aclChars == null) {
            return ca;
        }
        if (authenticated) ** GOTO lbl-1000
        CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, null, 7));
        if (CurrentAccess.access$200(ca) == null) {
            CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, null, 10));
        }
        if (CurrentAccess.access$200(ca) != null) {
            if (Acl.debug) {
                debugsb.append("... For unauthenticated got: " + CurrentAccess.access$200(ca));
                debugsb.append("'\n");
            }
        } else if (isOwner) {
            CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, null, 0));
            if (CurrentAccess.access$200(ca) == null) {
                CurrentAccess.access$202(ca, PrivilegeSet.makeDefaultOwnerPrivileges());
            }
            if (Acl.debug) {
                debugsb.append("... For owner got: " + CurrentAccess.access$200(ca));
                debugsb.append("'\n");
            }
        } else {
            CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, who.getPrincipalRef(), 1));
            if (CurrentAccess.access$200(ca) == null) {
                CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, who.getPrincipalRef(), 5));
            }
            if (CurrentAccess.access$200(ca) == null) {
                CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, who.getPrincipalRef(), 4));
            }
            if (CurrentAccess.access$200(ca) == null) {
                CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, who.getPrincipalRef(), 6));
            }
            if (CurrentAccess.access$200(ca) == null) {
                CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, who.getPrincipalRef(), 3));
            }
            if (CurrentAccess.access$200(ca) != null) {
                if (Acl.debug) {
                    debugsb.append("... For user got: " + CurrentAccess.access$200(ca));
                    debugsb.append("'\n");
                }
            } else {
                if (who.getGroupNames() != null) {
                    for (String group : who.getGroupNames()) {
                        if (Acl.debug) {
                            debugsb.append("...Try access for group " + group);
                            debugsb.append("'\n");
                        }
                        if ((privs = Ace.findMergedPrivilege(acl, cb, group, 2)) == null) continue;
                        CurrentAccess.access$202(ca, PrivilegeSet.mergePrivileges(CurrentAccess.access$200(ca), privs, false));
                    }
                }
                if (CurrentAccess.access$200(ca) != null) {
                    if (Acl.debug) {
                        debugsb.append("...For groups got: " + CurrentAccess.access$200(ca));
                        debugsb.append("'\n");
                    }
                } else {
                    if (authenticated) {
                        CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, null, 8));
                    }
                    if (CurrentAccess.access$200(ca) != null) {
                        if (Acl.debug) {
                            debugsb.append("...For authenticated got: " + CurrentAccess.access$200(ca));
                            debugsb.append("'\n");
                        }
                    } else {
                        CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, null, 9));
                        if (CurrentAccess.access$200(ca) == null) {
                            CurrentAccess.access$202(ca, Ace.findMergedPrivilege(acl, cb, null, 10));
                        }
                        if (CurrentAccess.access$200(ca) != null && Acl.debug) {
                            debugsb.append("...For other got: " + CurrentAccess.access$200(ca));
                            debugsb.append("'\n");
                        }
                    }
                }
            }
        }
        if (isOwner) {
            racl = CurrentAccess.access$200(ca).getPrivilege(2);
            wacl = CurrentAccess.access$200(ca).getPrivilege(6);
            if (racl != 'y' && racl != 'Y' || wacl != 'y' && wacl != 'Y') {
                CurrentAccess.access$202(ca, PrivilegeSet.mergePrivileges(CurrentAccess.access$200(ca), PrivilegeSet.ownerAclPrivileges, false));
            }
        }
        if (CurrentAccess.access$200(ca) == null) {
            if (Acl.debug) {
                Acl.debugMsg(debugsb.toString() + "...Check access denied (noprivs)");
            }
            return ca;
        }
        CurrentAccess.access$202(ca, PrivilegeSet.setUnspecified(CurrentAccess.access$200(ca), isOwner));
        if (filter != null) {
            CurrentAccess.access$202(ca, PrivilegeSet.filterPrivileges(CurrentAccess.access$200(ca), filter));
        }
        if (Acl.usePool) {
            CurrentAccess.access$202(ca, Acl.privSets.get(CurrentAccess.access$200(ca)));
        }
        if (how.length == 0) {
            CurrentAccess.access$302(ca, CurrentAccess.access$200(ca).getAnyAllowed());
            if (Acl.debug) {
                if (CurrentAccess.access$300(ca)) {
                    Acl.debugMsg(debugsb.toString() + "...Check access allowed (any requested)");
                } else {
                    Acl.debugMsg(debugsb.toString() + "...Check access denied (any requested)");
                }
            }
            return ca;
        }
        for (i = 0; i < how.length; ++i) {
            priv = CurrentAccess.access$200(ca).getPrivilege(how[i].getIndex());
            if (priv == 'y' || priv == 'Y') continue;
            if (Acl.debug) {
                debugsb.append("...Check access denied (!allowed) ");
                debugsb.append(CurrentAccess.access$200(ca));
                Acl.debugMsg(debugsb.toString());
            }
            return ca;
        }
        if (Acl.debug) {
            Acl.debugMsg(debugsb.toString() + "...Check access allowed");
        }
        CurrentAccess.access$302(ca, true);
        return ca;
    }

    public Collection<Ace> getAces() throws AccessException {
        if (this.aces == null) {
            return null;
        }
        return Collections.unmodifiableCollection(this.aces.values());
    }

    public Acl removeWho(AceWho who) throws AccessException {
        if (this.aces == null) {
            return null;
        }
        boolean contains = false;
        for (Ace a : this.getAces()) {
            if (!who.equals(a.getWho())) continue;
            contains = true;
            break;
        }
        if (!contains) {
            return null;
        }
        ArrayList<Ace> aces = new ArrayList<Ace>();
        for (Ace a : this.getAces()) {
            if (who.equals(a.getWho())) continue;
            aces.add(a);
        }
        return new Acl(aces);
    }

    public static Acl decode(String val) throws AccessException {
        return Acl.decode(val.toCharArray());
    }

    public static Acl decode(char[] val) throws AccessException {
        return Acl.decode(val, null);
    }

    public static Acl decode(char[] val, String path) throws AccessException {
        EncodedAcl eacl = new EncodedAcl();
        eacl.setEncoded(val);
        ArrayList<Ace> aces = new ArrayList<Ace>();
        while (eacl.hasMore()) {
            Ace ace = Ace.decode(eacl, path);
            aces.add(ace);
        }
        return new Acl(aces);
    }

    public Acl merge(char[] val, String path) throws AccessException {
        ArrayList<Ace> newAces = new ArrayList<Ace>();
        newAces.addAll(this.getAces());
        Acl encAcl = Acl.decode(val, path);
        block0: for (Ace a : encAcl.getAces()) {
            for (Ace ace : newAces) {
                if (!a.getWho().equals(ace.getWho())) continue;
                continue block0;
            }
            newAces.add(a);
        }
        return new Acl(newAces);
    }

    public char[] encode() throws AccessException {
        this.startEncoding();
        if (this.aces == null) {
            return null;
        }
        for (Ace ace : this.aces.values()) {
            if (ace.getInheritedFrom() != null) continue;
            ace.encode(this);
        }
        return this.getEncoding();
    }

    public String encodeStr() throws AccessException {
        char[] encoded = this.encode();
        if (encoded == null) {
            return null;
        }
        return new String(encoded);
    }

    public char[] encodeAll() throws AccessException {
        this.startEncoding();
        if (this.aces == null) {
            return null;
        }
        for (Ace ace : this.aces.values()) {
            ace.encode(this);
        }
        return this.getEncoding();
    }

    public String toUserString() {
        StringBuilder sb = new StringBuilder();
        try {
            Acl.decode(this.getEncoded());
            for (Ace ace : this.aces.values()) {
                sb.append(ace.toString());
                sb.append(" ");
            }
        }
        catch (Throwable t) {
            this.error(t);
            sb.append("Decode exception " + t.getMessage());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Acl{");
        if (!this.empty()) {
            sb.append("encoded=[");
            this.rewind();
            while (this.hasMore()) {
                sb.append(this.getChar());
            }
            sb.append("] ");
            this.rewind();
            try {
                if (this.aces == null) {
                    Acl.decode(this.getEncoded());
                }
                for (Ace ace : this.aces.values()) {
                    sb.append("\n");
                    sb.append(ace.toString());
                }
            }
            catch (Throwable t) {
                this.error(t);
                sb.append("Decode exception " + t.getMessage());
            }
        }
        sb.append("}");
        return sb.toString();
    }

    protected static Logger getLog(Class cl) {
        if (log == null) {
            log = Logger.getLogger(EncodedAcl.class);
        }
        return log;
    }

    public static void main(String[] args) {
        try {
            Acl acl = Acl.decode(args[0]);
            System.out.println(acl.toString());
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static {
        privSets = new ObjectPool();
        usePool = false;
        evaluations = new Access.AccessStatsEntry("evaluations");
        defaultNonOwnerAccess = new CurrentAccess(PrivilegeSet.makeDefaultNonOwnerPrivileges());
    }

    public static class CurrentAccess
    implements Serializable,
    Comparable<CurrentAccess> {
        private Acl acl;
        private char[] aclChars;
        private PrivilegeSet privileges = null;
        private boolean accessAllowed;

        public CurrentAccess() {
        }

        public CurrentAccess(PrivilegeSet privs) {
            this.privileges = privs;
        }

        public CurrentAccess(boolean accessAllowed) {
            this.accessAllowed = accessAllowed;
        }

        public Acl getAcl() {
            return this.acl;
        }

        public PrivilegeSet getPrivileges() {
            return this.privileges;
        }

        public boolean getAccessAllowed() {
            return this.accessAllowed;
        }

        @Override
        public int compareTo(CurrentAccess that) {
            if (this == that) {
                return 0;
            }
            int res = Util.compare(this.aclChars, that.aclChars);
            if (res != 0) {
                return res;
            }
            res = Util.cmpObjval(this.privileges, that.privileges);
            if (res != 0) {
                return res;
            }
            return Util.cmpBoolval(this.accessAllowed, that.accessAllowed);
        }

        public int hashCode() {
            int hc = 7;
            if (this.aclChars != null) {
                hc *= this.aclChars.hashCode();
            }
            if (this.privileges != null) {
                hc *= this.privileges.hashCode();
            }
            return hc;
        }

        public boolean equals(Object o) {
            return this.compareTo((CurrentAccess)o) == 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("CurrentAccess{");
            sb.append("acl=");
            sb.append(this.acl);
            sb.append("accessAllowed=");
            sb.append(this.accessAllowed);
            sb.append("}");
            return sb.toString();
        }

        static /* synthetic */ char[] access$102(CurrentAccess x0, char[] x1) {
            x0.aclChars = x1;
            return x1;
        }

        static /* synthetic */ boolean access$302(CurrentAccess x0, boolean x1) {
            x0.accessAllowed = x1;
            return x0.accessAllowed;
        }

        static /* synthetic */ boolean access$300(CurrentAccess x0) {
            return x0.accessAllowed;
        }
    }
}

