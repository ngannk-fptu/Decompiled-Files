/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.bedework.access.AccessException;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.Ace;
import org.bedework.access.AceWho;
import org.bedework.access.Acl;
import org.bedework.access.Privilege;
import org.bedework.access.PrivilegeSet;
import org.bedework.access.Privileges;

public class Access
implements Serializable {
    public static final Privilege none = Privileges.makePriv(24);
    public static final Privilege all = Privileges.makePriv(0);
    public static final Privilege read = Privileges.makePriv(1);
    public static final Privilege write = Privileges.makePriv(5);
    public static final Privilege writeContent = Privileges.makePriv(8);
    public static final Privilege[] privSetAny = new Privilege[0];
    public static final Privilege[] privSetRead = new Privilege[]{read};
    public static final Privilege[] privSetReadWrite = new Privilege[]{read, write};
    private static volatile String defaultPublicAccess;
    private static volatile String defaultPersonalAccess;

    public static Collection<AccessStatsEntry> getStatistics() {
        return Acl.getStatistics();
    }

    public static String getDefaultPublicAccess() {
        return defaultPublicAccess;
    }

    public static String getDefaultPersonalAccess() {
        return defaultPersonalAccess;
    }

    public Privilege makePriv(int priv) {
        return Privileges.makePriv(priv);
    }

    public Acl.CurrentAccess evaluateAccess(AccessCb cb, AccessPrincipal who, AccessPrincipal owner, Privilege[] how, String aclString, PrivilegeSet filter) throws AccessException {
        return Acl.evaluateAccess(cb, who, owner, how, aclString.toCharArray(), filter);
    }

    public Acl.CurrentAccess evaluateAccess(AccessCb cb, AccessPrincipal who, AccessPrincipal owner, Privilege[] how, char[] aclChars, PrivilegeSet filter) throws AccessException {
        return Acl.evaluateAccess(cb, who, owner, how, aclChars, filter);
    }

    public Acl.CurrentAccess checkRead(AccessCb cb, AccessPrincipal who, AccessPrincipal owner, char[] aclChars, PrivilegeSet filter) throws AccessException {
        return Acl.evaluateAccess(cb, who, owner, privSetRead, aclChars, filter);
    }

    public Acl.CurrentAccess checkReadWrite(AccessCb cb, AccessPrincipal who, AccessPrincipal owner, char[] aclChars, PrivilegeSet filter) throws AccessException {
        return Acl.evaluateAccess(cb, who, owner, privSetReadWrite, aclChars, filter);
    }

    public Acl.CurrentAccess checkAny(AccessCb cb, AccessPrincipal who, AccessPrincipal owner, char[] aclChars, PrivilegeSet filter) throws AccessException {
        return Acl.evaluateAccess(cb, who, owner, privSetAny, aclChars, filter);
    }

    public Acl.CurrentAccess evaluateAccess(AccessCb cb, AccessPrincipal who, AccessPrincipal owner, int priv, char[] aclChars, PrivilegeSet filter) throws AccessException {
        return Acl.evaluateAccess(cb, who, owner, new Privilege[]{Privileges.makePriv(priv)}, aclChars, filter);
    }

    static {
        try {
            ArrayList<Privilege> allPrivs = new ArrayList<Privilege>();
            allPrivs.add(all);
            ArrayList<Privilege> readPrivs = new ArrayList<Privilege>();
            readPrivs.add(read);
            ArrayList<Privilege> noPrivs = new ArrayList<Privilege>();
            noPrivs.add(none);
            ArrayList<Ace> aces = new ArrayList<Ace>();
            aces.add(Ace.makeAce(AceWho.owner, allPrivs, null));
            aces.add(Ace.makeAce(AceWho.other, readPrivs, null));
            aces.add(Ace.makeAce(AceWho.unauthenticated, readPrivs, null));
            defaultPublicAccess = new String(new Acl(aces).encode());
            aces.clear();
            aces.add(Ace.makeAce(AceWho.owner, allPrivs, null));
            aces.add(Ace.makeAce(AceWho.other, noPrivs, null));
            defaultPersonalAccess = new String(new Acl(aces).encode());
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static interface AccessCb {
        public String makeHref(String var1, int var2) throws AccessException;
    }

    public static class AccessStatsEntry {
        public String name;
        public long count;

        public AccessStatsEntry(String name) {
            this.name = name;
        }
    }
}

