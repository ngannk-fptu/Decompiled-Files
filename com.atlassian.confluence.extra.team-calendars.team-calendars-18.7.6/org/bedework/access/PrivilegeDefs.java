/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.access;

import java.io.Serializable;

public interface PrivilegeDefs
extends Serializable {
    public static final char oldAllowed = '3';
    public static final char oldDenied = '2';
    public static final char allowed = 'y';
    public static final char denied = 'n';
    public static final char allowedInherited = 'Y';
    public static final char deniedInherited = 'N';
    public static final char unspecified = '?';
    public static final char inheritedFlag = 'I';
    public static final int privAll = 0;
    public static final int privRead = 1;
    public static final int privReadAcl = 2;
    public static final int privReadCurrentUserPrivilegeSet = 3;
    public static final int privReadFreeBusy = 4;
    public static final int privWrite = 5;
    public static final int privWriteAcl = 6;
    public static final int privWriteProperties = 7;
    public static final int privWriteContent = 8;
    public static final int privBind = 9;
    public static final int privSchedule = 10;
    public static final int privScheduleRequest = 11;
    public static final int privScheduleReply = 12;
    public static final int privScheduleFreeBusy = 13;
    public static final int privUnbind = 14;
    public static final int privUnlock = 15;
    public static final int privScheduleDeliver = 16;
    public static final int privScheduleDeliverInvite = 17;
    public static final int privScheduleDeliverReply = 18;
    public static final int privScheduleQueryFreebusy = 19;
    public static final int privScheduleSend = 20;
    public static final int privScheduleSendInvite = 21;
    public static final int privScheduleSendReply = 22;
    public static final int privScheduleSendFreebusy = 23;
    public static final int privNone = 24;
    public static final int privMaxType = 24;
    public static final int privAny = 25;
    public static final char[] privEncoding = new char[]{'A', 'R', 'r', 'P', 'F', 'W', 'a', 'p', 'c', 'b', 'S', 't', 'y', 's', 'u', 'U', 'D', 'i', 'e', 'q', 'T', 'I', 'E', 'Q', 'N'};
}

