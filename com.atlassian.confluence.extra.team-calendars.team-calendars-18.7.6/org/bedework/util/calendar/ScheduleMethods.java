/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar;

import java.io.Serializable;

public interface ScheduleMethods
extends Serializable {
    public static final int methodTypeNone = 0;
    public static final int methodTypePublish = 1;
    public static final int methodTypeRequest = 2;
    public static final int methodTypeReply = 3;
    public static final int methodTypeAdd = 4;
    public static final int methodTypeCancel = 5;
    public static final int methodTypeRefresh = 6;
    public static final int methodTypeCounter = 7;
    public static final int methodTypeDeclineCounter = 8;
    public static final int methodTypePollStatus = 9;
    public static final int methodTypeUnknown = 99;
    public static final String[] methods = new String[]{null, "PUBLISH", "REQUEST", "REPLY", "ADD", "CANCEL", "REFRESH", "COUNTER", "DECLINECOUNTER", "POLLSTATUS"};
}

