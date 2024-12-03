/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.bedework.access.AccessException;
import org.bedework.access.EncodedAcl;
import org.bedework.access.Privilege;
import org.bedework.access.PrivilegeDefs;
import org.bedework.access.Privileges;
import org.bedework.util.caching.ObjectPool;

public class PrivilegeSet
implements Serializable,
PrivilegeDefs,
Comparable<PrivilegeSet> {
    private char[] privileges;
    private static ObjectPool<PrivilegeSet> privSets = new ObjectPool();
    private static boolean usePool = true;
    public static PrivilegeSet defaultOwnerPrivileges = PrivilegeSet.pooled(new PrivilegeSet('y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y', 'y'));
    public static PrivilegeSet userHomeMaxPrivileges = PrivilegeSet.pooled(new PrivilegeSet('n', 'y', 'y', 'y', 'y', 'n', 'y', 'y', 'y', 'y', 'n', 'n', 'n', 'n', 'n', 'y', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'y'));
    public static PrivilegeSet readOnlyPrivileges = PrivilegeSet.pooled(new PrivilegeSet('n', 'y', 'n', 'y', 'y', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'y', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'y'));
    public static PrivilegeSet defaultNonOwnerPrivileges = PrivilegeSet.pooled(new PrivilegeSet('n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'));
    public static PrivilegeSet ownerAclPrivileges = PrivilegeSet.pooled(new PrivilegeSet('n', 'n', 'y', 'n', 'n', 'n', 'y', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n', 'n'));

    public PrivilegeSet(char privAllState, char privReadState, char privReadAclState, char privReadCurrentUserPrivilegeSetState, char privReadFreeBusyState, char privWriteState, char privWriteAclState, char privWritePropertiesState, char privWriteContentState, char privBindState, char privScheduleState, char privScheduleRequestState, char privScheduleReplyState, char privScheduleFreeBusyState, char privUnbindState, char privUnlockState, char privScheduleDeliverState, char privScheduleDeliverInviteState, char privScheduleDeliverReplyState, char privScheduleQueryFreebusyState, char privScheduleSendState, char privScheduleSendInviteState, char privScheduleSendReplyState, char privScheduleSendFreebusyState, char privNoneState) {
        this.privileges = new char[25];
        this.privileges[0] = privAllState;
        this.privileges[1] = privReadState;
        this.privileges[2] = privReadAclState;
        this.privileges[3] = privReadCurrentUserPrivilegeSetState;
        this.privileges[4] = privReadFreeBusyState;
        this.privileges[5] = privWriteState;
        this.privileges[6] = privWriteAclState;
        this.privileges[7] = privWritePropertiesState;
        this.privileges[8] = privWriteContentState;
        this.privileges[9] = privBindState;
        this.privileges[10] = privScheduleState;
        this.privileges[11] = privScheduleRequestState;
        this.privileges[12] = privScheduleReplyState;
        this.privileges[13] = privScheduleFreeBusyState;
        this.privileges[14] = privUnbindState;
        this.privileges[15] = privUnlockState;
        this.privileges[16] = privScheduleDeliverState;
        this.privileges[17] = privScheduleDeliverInviteState;
        this.privileges[18] = privScheduleDeliverReplyState;
        this.privileges[19] = privScheduleQueryFreebusyState;
        this.privileges[20] = privScheduleSendState;
        this.privileges[21] = privScheduleSendInviteState;
        this.privileges[22] = privScheduleSendReplyState;
        this.privileges[23] = privScheduleSendFreebusyState;
        this.privileges[24] = privNoneState;
    }

    public PrivilegeSet(char[] privileges) {
        this.privileges = privileges;
    }

    public PrivilegeSet() {
        this.privileges = defaultNonOwnerPrivileges.getPrivileges();
    }

    public static PrivilegeSet makeDefaultOwnerPrivileges() {
        return defaultOwnerPrivileges;
    }

    public static PrivilegeSet makeUserHomeMaxPrivileges() {
        return userHomeMaxPrivileges;
    }

    public static PrivilegeSet makeDefaultNonOwnerPrivileges() {
        return defaultNonOwnerPrivileges;
    }

    public static PrivilegeSet makePrivileges(Privilege priv) {
        PrivilegeSet pset = new PrivilegeSet();
        pset.privileges = new char[25];
        pset.privileges[priv.getIndex()] = priv.getDenial() ? 110 : 121;
        for (Privilege p : priv.getContainedPrivileges()) {
            pset.setPrivilege(p);
        }
        return PrivilegeSet.pooled(pset);
    }

    public static PrivilegeSet fromEncoding(EncodedAcl acl) throws AccessException {
        char c;
        char[] privStates = new char[]{'?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?', '?'};
        while (acl.hasMore() && (c = acl.getChar()) != ' ' && c != 'I') {
            acl.back();
            Privilege p = Privilege.findPriv(Privileges.getPrivAll(), Privileges.getPrivNone(), acl);
            if (p == null) {
                throw AccessException.badACL("unknown priv " + acl.getErrorInfo());
            }
            PrivilegeSet.setState(privStates, p, p.getDenial());
        }
        return PrivilegeSet.pooled(new PrivilegeSet(privStates));
    }

    public static PrivilegeSet addPrivilege(PrivilegeSet pset, Privilege priv) {
        PrivilegeSet newPset = (PrivilegeSet)pset.clone();
        if (newPset.privileges == null) {
            newPset.privileges = defaultNonOwnerPrivileges.getPrivileges();
        }
        newPset.privileges[priv.getIndex()] = priv.getDenial() ? 110 : 121;
        for (Privilege p : priv.getContainedPrivileges()) {
            newPset.setPrivilege(p);
        }
        return PrivilegeSet.pooled(newPset);
    }

    public static PrivilegeSet makePrivilegeSet(Privilege[] privs) {
        PrivilegeSet newPset = new PrivilegeSet();
        newPset.privileges = defaultNonOwnerPrivileges.getPrivileges();
        for (Privilege priv : privs) {
            newPset.privileges[priv.getIndex()] = priv.getDenial() ? 110 : 121;
            for (Privilege p : priv.getContainedPrivileges()) {
                newPset.setPrivilege(p);
            }
        }
        return PrivilegeSet.pooled(newPset);
    }

    public char getPrivilege(int index) {
        if (this.privileges == null) {
            return '?';
        }
        return this.privileges[index];
    }

    public static PrivilegeSet filterPrivileges(PrivilegeSet pset, PrivilegeSet filter) {
        PrivilegeSet newPset = (PrivilegeSet)pset.clone();
        if (newPset.privileges == null) {
            newPset.privileges = defaultNonOwnerPrivileges.getPrivileges();
        }
        char[] filterPrivs = filter.privileges;
        for (int pi = 0; pi < newPset.privileges.length; ++pi) {
            if (!PrivilegeSet.privAgtB(newPset.privileges[pi], filterPrivs[pi])) continue;
            newPset.privileges[pi] = filterPrivs[pi];
        }
        return PrivilegeSet.pooled(newPset);
    }

    public boolean getAnyAllowed() {
        if (this.privileges == null) {
            return false;
        }
        for (int pi = 0; pi < this.privileges.length; ++pi) {
            char pr = this.privileges[pi];
            if (pr == 'y') {
                return true;
            }
            if (pr != 'Y') continue;
            return true;
        }
        return false;
    }

    public static PrivilegeSet mergePrivileges(PrivilegeSet current, PrivilegeSet morePriv, boolean inherited) {
        int i;
        PrivilegeSet mp = (PrivilegeSet)morePriv.clone();
        if (inherited) {
            for (i = 0; i <= 24; ++i) {
                char p = mp.getPrivilege(i);
                if (p == 'y') {
                    mp.setPrivilege(i, 'Y');
                    continue;
                }
                if (p != 'n') continue;
                mp.setPrivilege(i, 'N');
            }
        }
        if (current == null) {
            return mp;
        }
        for (i = 0; i <= 24; ++i) {
            char priv = mp.getPrivilege(i);
            if (current.getPrivilege(i) >= priv) continue;
            current.setPrivilege(i, priv);
        }
        return PrivilegeSet.pooled(current);
    }

    public static PrivilegeSet setUnspecified(PrivilegeSet pset, boolean isOwner) {
        PrivilegeSet newPset = (PrivilegeSet)pset.clone();
        if (newPset.privileges == null) {
            newPset.privileges = defaultNonOwnerPrivileges.getPrivileges();
        }
        for (int pi = 0; pi < newPset.privileges.length; ++pi) {
            if (newPset.privileges[pi] != '?') continue;
            newPset.privileges[pi] = isOwner ? 121 : 110;
        }
        return PrivilegeSet.pooled(newPset);
    }

    public char[] getPrivileges() {
        if (this.privileges == null) {
            return null;
        }
        return (char[])this.privileges.clone();
    }

    public Collection<Privilege> getPrivs() {
        char[] ps = this.getPrivileges();
        for (int pi = 0; pi < ps.length; ++pi) {
            if (ps[pi] == '?') continue;
            Privilege priv = Privileges.makePriv(pi);
            for (Privilege pr : priv.getContainedPrivileges()) {
                this.setUnspec(ps, pr);
            }
        }
        ArrayList<Privilege> privs = new ArrayList<Privilege>();
        for (int pi = 0; pi < ps.length; ++pi) {
            if (ps[pi] == '?') continue;
            privs.add(Privileges.makePriv(pi));
        }
        return privs;
    }

    private void setUnspec(char[] ps, Privilege priv) {
        ps[priv.getIndex()] = 63;
        for (Privilege pr : priv.getContainedPrivileges()) {
            this.setUnspec(ps, pr);
        }
    }

    private static boolean privAgtB(char priva, char privb) {
        if (privb == '?') {
            return true;
        }
        if (privb == 'n' || privb == 'N') {
            return priva == 'y' || priva == 'Y';
        }
        return false;
    }

    private static PrivilegeSet pooled(PrivilegeSet val) {
        if (!usePool) {
            return val;
        }
        return privSets.get(val);
    }

    private void setPrivilege(int index, char val) {
        if (this.privileges == null) {
            this.privileges = defaultNonOwnerPrivileges.getPrivileges();
        }
        this.privileges[index] = val;
    }

    private void setPrivilege(Privilege priv) {
        if (this.privileges == null) {
            this.privileges = defaultNonOwnerPrivileges.getPrivileges();
        }
        this.privileges[priv.getIndex()] = priv.getDenial() ? 110 : 121;
        for (Privilege p : priv.getContainedPrivileges()) {
            this.setPrivilege(p);
        }
    }

    private static void setState(char[] states, Privilege p, boolean denial) {
        if (!denial) {
            states[p.getIndex()] = 121;
        } else if (states[p.getIndex()] == '?') {
            states[p.getIndex()] = 110;
        }
        for (Privilege pr : p.getContainedPrivileges()) {
            PrivilegeSet.setState(states, pr, denial);
        }
    }

    @Override
    public int compareTo(PrivilegeSet that) {
        if (this == that) {
            return 0;
        }
        if (this.privileges == null) {
            if (that.privileges != null) {
                return -1;
            }
            return 0;
        }
        if (that.privileges == null) {
            return 1;
        }
        for (int pi = 0; pi < this.privileges.length; ++pi) {
            char thisp = this.privileges[pi];
            char thatp = that.privileges[pi];
            if (thisp < thatp) {
                return -1;
            }
            if (thisp <= thatp) continue;
            return -1;
        }
        return 0;
    }

    public int hashCode() {
        int hc = 7;
        if (this.privileges == null) {
            return hc;
        }
        for (int pi = 0; pi < this.privileges.length; ++pi) {
            hc *= this.privileges[pi];
        }
        return hc;
    }

    public boolean equals(Object o) {
        return this.compareTo((PrivilegeSet)o) == 0;
    }

    public Object clone() {
        return new PrivilegeSet(this.getPrivileges());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PrivilegeSet[");
        sb.append(this.privileges);
        sb.append("]");
        return sb.toString();
    }
}

