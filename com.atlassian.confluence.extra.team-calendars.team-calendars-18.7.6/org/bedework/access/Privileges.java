/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.util.ArrayList;
import java.util.Collection;
import org.bedework.access.AccessException;
import org.bedework.access.EncodedAcl;
import org.bedework.access.Privilege;
import org.bedework.access.PrivilegeDefs;

public class Privileges
implements PrivilegeDefs {
    private static final Privilege[] privs = new Privilege[25];
    private static final Privilege[] deniedPrivs = new Privilege[25];

    private Privileges() {
    }

    public static Privilege getPrivAll() {
        return privs[0];
    }

    public static Privilege getPrivNone() {
        return privs[24];
    }

    public static Privilege makePriv(int privType) {
        return privs[privType];
    }

    public static Privilege makePriv(int privType, boolean denial) {
        if (!denial) {
            return privs[privType];
        }
        return deniedPrivs[privType];
    }

    public static void skip(EncodedAcl acl) throws AccessException {
        char c;
        while (acl.hasMore() && (c = acl.getChar()) != ' ' && c != 'I') {
        }
    }

    public static Collection<Privilege> getPrivs(EncodedAcl acl) throws AccessException {
        char c;
        ArrayList<Privilege> al = new ArrayList<Privilege>();
        while (acl.hasMore() && (c = acl.getChar()) != ' ' && c != 'I') {
            acl.back();
            Privilege p = Privilege.findPriv(privs[0], privs[24], acl);
            if (p == null) {
                throw AccessException.badACL("unknown priv");
            }
            al.add(p);
        }
        return al;
    }

    private static void makePrivileges(Privilege[] ps, boolean denial) {
        ps[2] = new Privilege("read-acl", "Read calendar accls", denial, 2);
        ps[3] = new Privilege("read-current-user-privilege-set", "Read current user privilege set property", denial, 3);
        ps[4] = new Privilege("view-free-busy", "View a users free busy information", denial, 4);
        Privilege[] containedRead = new Privilege[]{ps[2], ps[3], ps[4]};
        ps[1] = new Privilege("read", "Read any calendar object", denial, 1, containedRead);
        ps[11] = new Privilege("schedule-request", "Submit schedule request", denial, 11);
        ps[12] = new Privilege("schedule-reply", "Submit schedule reply", denial, 12);
        ps[13] = new Privilege("schedule-free-busy", "Freebusy for scheduling", denial, 13);
        Privilege[] containedSchedule = new Privilege[]{ps[11], ps[12], ps[13]};
        ps[10] = new Privilege("schedule", "Scheduling operations", denial, 10, containedSchedule);
        Privilege[] containedBind = new Privilege[]{ps[10]};
        ps[9] = new Privilege("create", "Create a calendar object", denial, 9, containedBind);
        ps[6] = new Privilege("write-acl", "Write ACL", denial, 6);
        ps[7] = new Privilege("write-properties", "Write calendar properties", denial, 7);
        ps[8] = new Privilege("write-content", "Write calendar content", denial, 8);
        ps[14] = new Privilege("delete", "Delete a calendar object", denial, 14);
        Privilege[] containedWrite = new Privilege[]{ps[6], ps[7], ps[8], ps[9], ps[14]};
        ps[5] = new Privilege("write", "Write any calendar object", denial, 5, containedWrite);
        ps[17] = new Privilege("schedule-deliver-invite", "Schedule: deliver invitations", denial, 17);
        ps[18] = new Privilege("schedule-deliver-reply", "Schedule: deliver replies", denial, 18);
        ps[19] = new Privilege("schedule-query-freebusy", "Schedule: query freebusy", denial, 19);
        Privilege[] containedScheduleDeliver = new Privilege[]{ps[17], ps[18], ps[19]};
        ps[16] = new Privilege("schedule-deliver", "Scheduling delivery", denial, 16, containedScheduleDeliver);
        ps[21] = new Privilege("schedule-send-invite", "Schedule: send invitations", denial, 21);
        ps[22] = new Privilege("schedule-send-reply", "Schedule: send replies", denial, 22);
        ps[23] = new Privilege("schedule-send-freebusy", "Schedule: send freebusy", denial, 23);
        Privilege[] containedScheduleSend = new Privilege[]{ps[21], ps[22], ps[23]};
        ps[20] = new Privilege("schedule-send", "Scheduling send", denial, 20, containedScheduleSend);
        ps[15] = new Privilege("unlock", "Remove a lock", denial, 15);
        Privilege[] containedAll = new Privilege[]{ps[1], ps[5], ps[15], ps[16], ps[20]};
        ps[0] = new Privilege("all", "All privileges", denial, 0, containedAll);
        ps[24] = Privilege.cloneDenied(ps[0]);
    }

    static {
        Privileges.makePrivileges(privs, false);
        Privileges.makePrivileges(deniedPrivs, true);
    }
}

