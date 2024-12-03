/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.bedework.caldav.server.sysinterface;

import ietf.params.xml.ns.icalendar_2.IcalendarType;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import net.fortuna.ical4j.model.Calendar;
import org.bedework.access.AccessPrincipal;
import org.bedework.access.Acl;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.CalDAVResource;
import org.bedework.caldav.server.PropertyHandler;
import org.bedework.caldav.server.SysiIcalendar;
import org.bedework.caldav.server.sysinterface.CalDAVAuthProperties;
import org.bedework.caldav.server.sysinterface.CalDAVSystemProperties;
import org.bedework.caldav.server.sysinterface.CalPrincipalInfo;
import org.bedework.caldav.server.sysinterface.RetrievalMode;
import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.FilterBase;
import org.bedework.caldav.util.notifications.NotificationType;
import org.bedework.caldav.util.sharing.InviteReplyType;
import org.bedework.caldav.util.sharing.InviteType;
import org.bedework.caldav.util.sharing.ShareResultType;
import org.bedework.caldav.util.sharing.ShareType;
import org.bedework.util.calendar.ScheduleStates;
import org.bedework.util.xml.XmlEmit;
import org.bedework.webdav.servlet.shared.PrincipalPropertySearch;
import org.bedework.webdav.servlet.shared.UrlHandler;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WdSysIntf;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentSelectionType;

public interface SysIntf
extends WdSysIntf {
    public String init(HttpServletRequest var1, String var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7, String var8) throws WebdavException;

    public boolean testMode();

    public boolean bedeworkExtensionsEnabled();

    public CalDAVAuthProperties getAuthProperties();

    public CalDAVSystemProperties getSystemProperties();

    public AccessPrincipal getPrincipal();

    public PropertyHandler getPropertyHandler(PropertyHandler.PropertyType var1) throws WebdavException;

    @Override
    public UrlHandler getUrlHandler();

    public boolean isPrincipal(String var1) throws WebdavException;

    public AccessPrincipal getPrincipalForUser(String var1) throws WebdavException;

    public AccessPrincipal getPrincipal(String var1) throws WebdavException;

    public byte[] getPublicKey(String var1, String var2) throws WebdavException;

    public String makeHref(String var1, int var2) throws WebdavException;

    public Collection<String> getGroups(String var1, String var2) throws WebdavException;

    public AccessPrincipal caladdrToPrincipal(String var1) throws WebdavException;

    public String principalToCaladdr(AccessPrincipal var1) throws WebdavException;

    public CalPrincipalInfo getCalPrincipalInfo(AccessPrincipal var1) throws WebdavException;

    public Collection<String> getPrincipalCollectionSet(String var1) throws WebdavException;

    public Collection<CalPrincipalInfo> getPrincipals(String var1, PrincipalPropertySearch var2) throws WebdavException;

    public boolean validPrincipal(String var1) throws WebdavException;

    public boolean subscribeNotification(String var1, String var2, List<String> var3) throws WebdavException;

    public boolean sendNotification(String var1, NotificationType var2) throws WebdavException;

    public void removeNotification(String var1, NotificationType var2) throws WebdavException;

    public List<NotificationType> getNotifications() throws WebdavException;

    public List<NotificationType> getNotifications(String var1, QName var2) throws WebdavException;

    public ShareResultType share(CalDAVCollection var1, ShareType var2) throws WebdavException;

    public String sharingReply(CalDAVCollection var1, InviteReplyType var2) throws WebdavException;

    public InviteType getInviteStatus(CalDAVCollection var1) throws WebdavException;

    public Collection<String> getFreebusySet() throws WebdavException;

    public Collection<SchedRecipientResult> schedule(CalDAVEvent var1) throws WebdavException;

    public Collection<CalDAVEvent> addEvent(CalDAVEvent var1, boolean var2, boolean var3) throws WebdavException;

    public void reindexEvent(CalDAVEvent var1);

    public void updateEvent(CalDAVEvent var1) throws WebdavException;

    public UpdateResult updateEvent(CalDAVEvent var1, List<ComponentSelectionType> var2) throws WebdavException;

    public Collection<CalDAVEvent> getEvents(CalDAVCollection var1, FilterBase var2, List<String> var3, RetrievalMode var4) throws WebdavException;

    public CalDAVEvent getEvent(CalDAVCollection var1, String var2) throws WebdavException;

    public void deleteEvent(CalDAVEvent var1, boolean var2) throws WebdavException;

    public Collection<SchedRecipientResult> requestFreeBusy(CalDAVEvent var1, boolean var2) throws WebdavException;

    public void getSpecialFreeBusy(String var1, Set<String> var2, String var3, TimeRange var4, Writer var5) throws WebdavException;

    public CalDAVEvent getFreeBusy(CalDAVCollection var1, int var2, TimeRange var3) throws WebdavException;

    public Acl.CurrentAccess checkAccess(WdEntity var1, int var2, boolean var3) throws WebdavException;

    public void updateAccess(CalDAVEvent var1, Acl var2) throws WebdavException;

    public boolean copyMove(CalDAVEvent var1, CalDAVCollection var2, String var3, boolean var4, boolean var5) throws WebdavException;

    public CalDAVCollection newCollectionObject(boolean var1, String var2) throws WebdavException;

    public void updateAccess(CalDAVCollection var1, Acl var2) throws WebdavException;

    public int makeCollection(CalDAVCollection var1) throws WebdavException;

    public void copyMove(CalDAVCollection var1, CalDAVCollection var2, boolean var3, boolean var4) throws WebdavException;

    public CalDAVCollection getCollection(String var1) throws WebdavException;

    public void updateCollection(CalDAVCollection var1) throws WebdavException;

    public void deleteCollection(CalDAVCollection var1, boolean var2) throws WebdavException;

    public Collection<CalDAVCollection> getCollections(CalDAVCollection var1) throws WebdavException;

    public CalDAVResource newResourceObject(String var1) throws WebdavException;

    public void putFile(CalDAVCollection var1, CalDAVResource var2) throws WebdavException;

    public CalDAVResource getFile(CalDAVCollection var1, String var2) throws WebdavException;

    public void getFileContent(CalDAVResource var1) throws WebdavException;

    public Collection<CalDAVResource> getFiles(CalDAVCollection var1) throws WebdavException;

    public void updateFile(CalDAVResource var1, boolean var2) throws WebdavException;

    public void deleteFile(CalDAVResource var1) throws WebdavException;

    public boolean copyMoveFile(CalDAVResource var1, String var2, String var3, boolean var4, boolean var5) throws WebdavException;

    public String getSyncToken(CalDAVCollection var1) throws WebdavException;

    public SynchReportData getSyncReport(String var1, String var2, int var3, boolean var4) throws WebdavException;

    public Calendar toCalendar(CalDAVEvent var1, boolean var2) throws WebdavException;

    public IcalendarType toIcalendar(CalDAVEvent var1, boolean var2, IcalendarType var3) throws WebdavException;

    public String toJcal(CalDAVEvent var1, boolean var2, IcalendarType var3) throws WebdavException;

    public String toIcalString(Calendar var1, String var2) throws WebdavException;

    public String writeCalendar(Collection<CalDAVEvent> var1, MethodEmitted var2, XmlEmit var3, Writer var4, String var5) throws WebdavException;

    public SysiIcalendar fromIcal(CalDAVCollection var1, Reader var2, String var3, IcalResultType var4, boolean var5) throws WebdavException;

    public SysiIcalendar fromIcal(CalDAVCollection var1, IcalendarType var2, IcalResultType var3) throws WebdavException;

    public String toStringTzCalendar(String var1) throws WebdavException;

    public String tzidFromTzdef(String var1) throws WebdavException;

    public boolean validateAlarm(String var1) throws WebdavException;

    public void rollback();

    public void close() throws WebdavException;

    public static enum IcalResultType {
        OneComponent,
        TimeZone;

    }

    public static enum MethodEmitted {
        noMethod,
        eventMethod,
        publish;

    }

    public static class SynchReportData {
        public List<SynchReportDataItem> items;
        public boolean truncated;
        public String token;

        public static class SynchReportDataItem
        implements Comparable<SynchReportDataItem> {
            private String token;
            private CalDAVEvent entity;
            private CalDAVResource resource;
            private CalDAVCollection col;
            private String vpath;
            private boolean canSync;

            public SynchReportDataItem(String vpath, CalDAVEvent entity, String token) throws WebdavException {
                this.vpath = vpath;
                this.entity = entity;
                this.token = token;
            }

            public SynchReportDataItem(String vpath, CalDAVResource resource, String token) throws WebdavException {
                this.vpath = vpath;
                this.resource = resource;
                this.token = token;
            }

            public SynchReportDataItem(String vpath, CalDAVCollection col, String token, boolean canSync) throws WebdavException {
                this.vpath = vpath;
                this.col = col;
                this.canSync = canSync;
                this.token = token;
            }

            public String getToken() {
                return this.token;
            }

            public CalDAVEvent getEntity() {
                return this.entity;
            }

            public CalDAVResource getResource() {
                return this.resource;
            }

            public CalDAVCollection getCol() {
                return this.col;
            }

            public String getVpath() {
                return this.vpath;
            }

            public boolean getCanSync() {
                return this.canSync;
            }

            @Override
            public int compareTo(SynchReportDataItem that) {
                return this.token.compareTo(that.token);
            }

            public int hashCode() {
                return this.token.hashCode();
            }

            public boolean equals(Object o) {
                return this.compareTo((SynchReportDataItem)o) == 0;
            }
        }
    }

    public static class UpdateResult {
        private boolean ok;
        private String reason;
        private static UpdateResult okResult = new UpdateResult();

        public static UpdateResult getOkResult() {
            return okResult;
        }

        private UpdateResult() {
            this.ok = true;
        }

        public UpdateResult(String reason) {
            this.reason = reason;
        }

        public boolean getOk() {
            return this.ok;
        }

        public String getReason() {
            return this.reason;
        }
    }

    public static class SchedRecipientResult
    implements ScheduleStates {
        public String recipient;
        public int status = -1;
        public CalDAVEvent freeBusy;

        public String toString() {
            StringBuilder sb = new StringBuilder("ScheduleRecipientResult{");
            SchedRecipientResult.tsseg(sb, "", "recipient", this.recipient);
            SchedRecipientResult.tsseg(sb, ", ", "status", String.valueOf(this.status));
            sb.append("}");
            return sb.toString();
        }

        private static void tsseg(StringBuilder sb, String delim, String name, String val) {
            sb.append(delim);
            sb.append(name);
            sb.append("=");
            sb.append(val);
        }
    }
}

