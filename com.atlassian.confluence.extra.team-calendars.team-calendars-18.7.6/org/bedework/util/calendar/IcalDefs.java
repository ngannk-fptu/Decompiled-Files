/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.calendar;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class IcalDefs {
    public static final int entityTypeEvent = 0;
    public static final int entityTypeAlarm = 1;
    public static final int entityTypeTodo = 2;
    public static final int entityTypeJournal = 3;
    public static final int entityTypeFreeAndBusy = 4;
    public static final int entityTypeVavailability = 5;
    public static final int entityTypeAvailable = 6;
    public static final int entityTypeVpoll = 7;
    public static final String[] entityTypeNames = new String[]{"event", "alarm", "todo", "journal", "freeAndBusy", "vavailability", "available", "vpoll"};
    public static final Set<String> entityTypes;
    public static final String[] entityTypeIcalNames;
    public static final String statusTentative = "TENTATIVE";
    public static final String statusConfirmed = "CONFIRMED";
    public static final String statusCancelled = "CANCELLED";
    public static final String statusNeedsAction = "NEEDS-ACTION";
    public static final String statusCompleted = "COMPLETED";
    public static final String statusInProcess = "IN-PROCESS";
    public static final String statusDraft = "DRAFT";
    public static final String statusFinal = "FINAL";
    public static final String requestStatusDeferred = "1.0;Deferred";
    public static final String requestStatusOK = "2.0;Success";
    public static final String requestStatusInvalidProperty = "3.0;Invalid Property Name:";
    public static final String requestStatusInvalidUser = "3.7;Invalid User:";
    public static final String requestStatusUnsupportedCapability = "3.14;Unsupported capability";
    public static final String requestStatusNoAccess = "4.2;No Access";
    public static final String requestStatusUnavailable = "5.1;Unavailable";
    public static final String requestStatusNoSupport = "5.3;No Support";
    public static final String transparencyOpaque = "OPAQUE";
    public static final String transparencyTransparent = "TRANSPARENT";
    public static final String alarmTriggerRelatedStart = "START";
    public static final String alarmTriggerRelatedEnd = "END";
    public static final int partstatNone = -2;
    public static final int partstatOther = -1;
    public static final int partstatNeedsAction = 0;
    public static final int partstatAccepted = 1;
    public static final int partstatDeclined = 2;
    public static final int partstatTentative = 3;
    public static final int partstatDelegated = 4;
    public static final int partstatCompleted = 5;
    public static final int partstatInProcess = 6;
    public static final String partstatValNeedsAction = "NEEDS-ACTION";
    public static final String partstatValAccepted = "ACCEPTED";
    public static final String partstatValDeclined = "DECLINED";
    public static final String partstatValTentative = "TENTATIVE";
    public static final String partstatValDelegated = "DELEGATED";
    public static final String partstatValCompleted = "COMPLETED";
    public static final String partstatValInProcess = "IN-PROCESS";
    public static final String[] partstats;
    public static final int scheduleAgentOther = -1;
    public static final int scheduleAgentServer = 0;
    public static final int scheduleAgentClient = 1;
    public static final int scheduleAgentNone = 2;
    public static final String[] scheduleAgents;
    public static final String deliveryStatusPending = "1.0";
    public static final String deliveryStatusSent = "1.1";
    public static final String deliveryStatusDelivered = "1.2";
    public static final String deliveryStatusSuccess = "2.0";
    public static final String deliveryStatusInvalidCUA = "3.7";
    public static final String deliveryStatusNoAccess = "3.8";
    public static final String deliveryStatusTempFailure = "5.1";
    public static final String deliveryStatusFailed = "5.2";
    public static final String deliveryStatusRejected = "5.3";
    public static final RequestStatus requestStatusSuccess;

    public static String fromEntityType(int type) {
        return entityTypeNames[type];
    }

    public static int checkPartstat(String val) {
        if (val == null) {
            return -2;
        }
        for (int i = 0; i < partstats.length; ++i) {
            if (!partstats[i].equals(val)) continue;
            return i;
        }
        return -1;
    }

    static {
        TreeSet<String> ts = new TreeSet<String>();
        for (String s : entityTypeNames) {
            ts.add(s);
        }
        entityTypes = Collections.unmodifiableSet(ts);
        entityTypeIcalNames = new String[]{"VEVENT", "VALARM", "VTODO", "VJOURNAL", "VFREEBUSY", "VAVAILABILITY", "AVAILABLE", "VPOLL"};
        partstats = new String[]{"NEEDS-ACTION", partstatValAccepted, partstatValDeclined, "TENTATIVE", partstatValDelegated, "COMPLETED", "IN-PROCESS"};
        scheduleAgents = new String[]{"SERVER", "CLIENT", "NONE"};
        requestStatusSuccess = new RequestStatus(deliveryStatusSuccess, "Success");
    }

    public static class RequestStatus {
        private String code;
        private String description;

        RequestStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return this.code;
        }

        public String getDescription() {
            return this.description;
        }
    }

    public static enum IcalComponentType {
        none,
        event,
        todo,
        journal,
        freebusy,
        vavailability,
        available,
        vpoll,
        mixed;

    }
}

