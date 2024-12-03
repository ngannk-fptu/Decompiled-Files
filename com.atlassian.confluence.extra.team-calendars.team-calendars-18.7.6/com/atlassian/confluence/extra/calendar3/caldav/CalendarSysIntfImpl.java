/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.caldav;

import com.atlassian.confluence.extra.calendar3.caldav.CalDavCalendarManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavCollectionManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavEventManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavMisc;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavNotificationsManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalDavPermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import ietf.params.xml.ns.icalendar_2.IcalendarType;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
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
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.FilterBase;
import org.bedework.caldav.util.notifications.NotificationType;
import org.bedework.caldav.util.sharing.InviteReplyType;
import org.bedework.caldav.util.sharing.InviteType;
import org.bedework.caldav.util.sharing.ShareResultType;
import org.bedework.caldav.util.sharing.ShareType;
import org.bedework.util.xml.XmlEmit;
import org.bedework.webdav.servlet.shared.PrincipalPropertySearch;
import org.bedework.webdav.servlet.shared.UrlHandler;
import org.bedework.webdav.servlet.shared.WdCollection;
import org.bedework.webdav.servlet.shared.WdEntity;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentSelectionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="calendarSysIntfImpl")
public final class CalendarSysIntfImpl
implements SysIntf {
    private final CalDavPermissionManager calDavPermissionManager;
    private final CalDavCollectionManager calDavCollectionManager;
    private final CalDavMisc calDavMisc;
    private final CalDavEventManager calDavEventManager;
    private final CalDavCalendarManager calDavCalendarManager;
    private final CalDavNotificationsManager calDavNotificationsManager;
    private UrlHandler urlHandler;
    private boolean calWs;
    private boolean synchWs;

    public CalendarSysIntfImpl() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public CalendarSysIntfImpl(CalDavPermissionManager calDavPermissionManager, CalDavCollectionManager calDavCollectionManager, CalDavMisc calDavMisc, CalDavEventManager calDavEventManager, CalDavCalendarManager calDavCalendarManager, CalDavNotificationsManager calDavNotificationsManager) {
        this.calDavPermissionManager = calDavPermissionManager;
        this.calDavCollectionManager = calDavCollectionManager;
        this.calDavMisc = calDavMisc;
        this.calDavEventManager = calDavEventManager;
        this.calDavCalendarManager = calDavCalendarManager;
        this.calDavNotificationsManager = calDavNotificationsManager;
    }

    @Override
    public String init(HttpServletRequest request, String account, boolean service, boolean calWs, boolean synchWs, boolean notifyWs, boolean socketWs, String opaqueData) throws WebdavException {
        this.calWs = calWs;
        this.synchWs = synchWs;
        this.urlHandler = new UrlHandler(request, true);
        return AuthenticatedUserThreadLocal.getUsername();
    }

    public CalDavCalendarManager getCalDavCalendarManager() {
        return this.calDavCalendarManager;
    }

    @Override
    public UrlHandler getUrlHandler() {
        return this.urlHandler;
    }

    @Override
    public String getDefaultContentType() {
        if (this.allowCalWs()) {
            return "application/calendar+xml";
        }
        return "text/calendar";
    }

    private boolean allowCalWs() {
        return this.calWs;
    }

    @Override
    public boolean testMode() {
        return this.calDavPermissionManager.testMode();
    }

    @Override
    public boolean bedeworkExtensionsEnabled() {
        return this.calDavPermissionManager.bedeworkExtensionsEnabled();
    }

    @Override
    public CalDAVAuthProperties getAuthProperties() {
        return this.calDavPermissionManager.getAuthProperties();
    }

    @Override
    public CalDAVSystemProperties getSystemProperties() {
        return this.calDavPermissionManager.getSystemProperties();
    }

    @Override
    public AccessPrincipal getPrincipal() {
        return this.calDavPermissionManager.getPrincipal();
    }

    @Override
    public AccessPrincipal getPrincipal(String href) throws WebdavException {
        return this.calDavPermissionManager.getPrincipal(href);
    }

    @Override
    public boolean isPrincipal(String value) throws WebdavException {
        return this.calDavPermissionManager.isPrincipal(value);
    }

    @Override
    public AccessPrincipal getPrincipalForUser(String account) throws WebdavException {
        return this.calDavPermissionManager.getPrincipal(account);
    }

    @Override
    public PropertyHandler getPropertyHandler(PropertyHandler.PropertyType propertyType) throws WebdavException {
        return this.calDavPermissionManager.getPropertyHandler(propertyType);
    }

    @Override
    public byte[] getPublicKey(String domain, String service) throws WebdavException {
        return this.calDavPermissionManager.getPublicKey(domain, service);
    }

    @Override
    public String makeHref(String id, int whoType) throws WebdavException {
        return this.calDavPermissionManager.makeHref(this.urlHandler, id, whoType);
    }

    @Override
    public Collection<String> getGroups(String rootUrl, String principalUrl) throws WebdavException {
        return this.calDavPermissionManager.getGroups(rootUrl, principalUrl);
    }

    @Override
    public AccessPrincipal caladdrToPrincipal(String caladdr) throws WebdavException {
        return this.calDavPermissionManager.caladdrToPrincipal(caladdr);
    }

    @Override
    public String principalToCaladdr(AccessPrincipal principal) throws WebdavException {
        return this.calDavPermissionManager.principalToCaladdr(principal);
    }

    @Override
    public CalPrincipalInfo getCalPrincipalInfo(AccessPrincipal principal) throws WebdavException {
        return this.calDavPermissionManager.getCalPrincipalInfo(principal);
    }

    @Override
    public Collection<String> getPrincipalCollectionSet(String resourceUri) throws WebdavException {
        return this.calDavPermissionManager.getPrincipalCollectionSet(resourceUri);
    }

    @Override
    public Collection<CalPrincipalInfo> getPrincipals(String resourceUri, PrincipalPropertySearch pps) throws WebdavException {
        return this.calDavPermissionManager.getPrincipals(resourceUri, pps);
    }

    @Override
    public boolean validPrincipal(String href) throws WebdavException {
        return this.calDavPermissionManager.validPrincipal(href);
    }

    @Override
    public String getNotificationURL() throws WebdavException {
        return this.calDavNotificationsManager.getNotificationURL();
    }

    @Override
    public boolean subscribeNotification(String principalHref, String action, List<String> emails) throws WebdavException {
        return this.calDavNotificationsManager.subscribeNotification(principalHref, action, emails);
    }

    @Override
    public boolean sendNotification(String href, NotificationType value) throws WebdavException {
        return this.calDavNotificationsManager.sendNotification(href, value);
    }

    @Override
    public void removeNotification(String href, NotificationType value) throws WebdavException {
        this.calDavNotificationsManager.removeNotification(href, value);
    }

    @Override
    public List<NotificationType> getNotifications() throws WebdavException {
        return this.calDavNotificationsManager.getNotifications();
    }

    @Override
    public List<NotificationType> getNotifications(String href, QName type) throws WebdavException {
        return this.calDavNotificationsManager.getNotifications(href, type);
    }

    @Override
    public ShareResultType share(CalDAVCollection collection, ShareType share) throws WebdavException {
        return this.calDavNotificationsManager.share(collection, share);
    }

    @Override
    public String sharingReply(CalDAVCollection collection, InviteReplyType reply) throws WebdavException {
        return this.calDavNotificationsManager.sharingReply(collection, reply);
    }

    @Override
    public InviteType getInviteStatus(CalDAVCollection collection) throws WebdavException {
        return this.calDavNotificationsManager.getInviteStatus(collection);
    }

    @Override
    public Collection<String> getFreebusySet() throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<SysIntf.SchedRecipientResult> schedule(CalDAVEvent ev) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<CalDAVEvent> addEvent(CalDAVEvent event, boolean noInvites, boolean rollbackOnError) throws WebdavException {
        return this.calDavEventManager.addEvent(event, noInvites, rollbackOnError);
    }

    @Override
    public void reindexEvent(CalDAVEvent calDAVEvent) {
    }

    @Override
    public void updateEvent(CalDAVEvent event) throws WebdavException {
        this.calDavEventManager.updateEvent(event);
    }

    @Override
    public SysIntf.UpdateResult updateEvent(CalDAVEvent event, List<ComponentSelectionType> updates) throws WebdavException {
        return this.calDavEventManager.updateEvent(event, updates);
    }

    @Override
    public Collection<CalDAVEvent> getEvents(CalDAVCollection collection, FilterBase filter, List<String> retrieveList, RetrievalMode recurRetrieval) throws WebdavException {
        return this.calDavEventManager.getEvents(collection, filter, retrieveList, recurRetrieval);
    }

    @Override
    public CalDAVEvent getEvent(CalDAVCollection collection, String val) throws WebdavException {
        return this.calDavEventManager.getEvent(collection, val);
    }

    @Override
    public void deleteEvent(CalDAVEvent event, boolean scheduleReply) throws WebdavException {
        this.calDavEventManager.deleteEvent(event, scheduleReply);
    }

    @Override
    public Collection<SysIntf.SchedRecipientResult> requestFreeBusy(CalDAVEvent event, boolean iSchedule) throws WebdavException {
        return this.calDavEventManager.requestFreeBusy(event, iSchedule);
    }

    @Override
    public void getSpecialFreeBusy(String cua, Set<String> recipients, String originator, TimeRange timeRange, Writer writer) throws WebdavException {
        this.calDavEventManager.getSpecialFreeBusy(cua, recipients, originator, timeRange, writer);
    }

    @Override
    public CalDAVEvent getFreeBusy(CalDAVCollection collection, int depth, TimeRange timeRange) throws WebdavException {
        return this.calDavEventManager.getFreeBusy(collection, depth, timeRange);
    }

    @Override
    public Acl.CurrentAccess checkAccess(WdEntity entity, int desiredAccess, boolean returnResult) throws WebdavException {
        return this.calDavEventManager.checkAccess(entity, desiredAccess, returnResult);
    }

    @Override
    public void updateAccess(CalDAVEvent event, Acl acl) throws WebdavException {
        this.calDavEventManager.updateAccess(event, acl);
    }

    @Override
    public boolean copyMove(CalDAVEvent from, CalDAVCollection to, String name, boolean copy, boolean overwrite) throws WebdavException {
        return this.calDavEventManager.copyMove(from, to, name, copy, overwrite);
    }

    @Override
    public CalDAVCollection newCollectionObject(boolean isCalendarCollection, String parentPath) throws WebdavException {
        return this.calDavCollectionManager.newCollectionObject(isCalendarCollection, parentPath);
    }

    @Override
    public void updateAccess(CalDAVCollection collection, Acl acl) throws WebdavException {
        this.calDavCollectionManager.updateAccess(collection, acl);
    }

    @Override
    public int makeCollection(CalDAVCollection collection) throws WebdavException {
        return this.calDavCollectionManager.makeCollection(collection);
    }

    @Override
    public void copyMove(CalDAVCollection from, CalDAVCollection to, boolean copy, boolean overwrite) throws WebdavException {
        this.calDavCollectionManager.copyMove(from, to, copy, overwrite);
    }

    @Override
    public CalDAVCollection getCollection(String path) throws WebdavException {
        return this.calDavCollectionManager.getCollection(path);
    }

    @Override
    public void updateCollection(CalDAVCollection collection) throws WebdavException {
        this.calDavCollectionManager.updateCollection(collection);
    }

    @Override
    public void deleteCollection(CalDAVCollection collection, boolean sendSchedulingMessage) throws WebdavException {
        this.calDavCollectionManager.deleteCollection(collection, sendSchedulingMessage);
    }

    @Override
    public Collection<CalDAVCollection> getCollections(CalDAVCollection collection) throws WebdavException {
        return this.calDavCollectionManager.getCollections(collection);
    }

    @Override
    public CalDAVResource newResourceObject(String parentPath) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putFile(CalDAVCollection coll, CalDAVResource val) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CalDAVResource getFile(CalDAVCollection coll, String name) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getFileContent(CalDAVResource val) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<CalDAVResource> getFiles(CalDAVCollection collection) {
        return Collections.emptyList();
    }

    @Override
    public void updateFile(CalDAVResource val, boolean updateContent) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteFile(CalDAVResource val) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean copyMoveFile(CalDAVResource from, String toPath, String name, boolean copy, boolean overwrite) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean allowsSyncReport(WdCollection collection) throws WebdavException {
        return this.synchWs;
    }

    @Override
    public String getSyncToken(CalDAVCollection col) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SysIntf.SynchReportData getSyncReport(String path, String token, int limit, boolean recurse) throws WebdavException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Calendar toCalendar(CalDAVEvent event, boolean incSchedMethod) throws WebdavException {
        return this.calDavMisc.toCalendar(event, incSchedMethod);
    }

    @Override
    public IcalendarType toIcalendar(CalDAVEvent event, boolean incSchedMethod, IcalendarType pattern) throws WebdavException {
        return this.calDavMisc.toIcalendar(event, incSchedMethod, pattern);
    }

    @Override
    public String toJcal(CalDAVEvent event, boolean incSchedMethod, IcalendarType pattern) throws WebdavException {
        return this.calDavMisc.toJcal(event, incSchedMethod, pattern);
    }

    @Override
    public String toIcalString(Calendar calendar, String contentType) throws WebdavException {
        return this.calDavMisc.toIcalString(calendar, contentType);
    }

    @Override
    public String writeCalendar(Collection<CalDAVEvent> events, SysIntf.MethodEmitted method, XmlEmit xml, Writer writer, String contentType) throws WebdavException {
        return this.calDavMisc.writeCalendar(events, method, xml, writer, contentType);
    }

    @Override
    public SysiIcalendar fromIcal(CalDAVCollection collection, Reader reader, String contentType, SysIntf.IcalResultType rtype, boolean mergeAttendees) throws WebdavException {
        return this.calDavMisc.fromIcal(collection, reader, contentType, rtype, mergeAttendees);
    }

    @Override
    public SysiIcalendar fromIcal(CalDAVCollection collection, IcalendarType ical, SysIntf.IcalResultType rtype) throws WebdavException {
        return this.calDavMisc.fromIcal(collection, ical, rtype);
    }

    @Override
    public String toStringTzCalendar(String tzid) throws WebdavException {
        return this.calDavMisc.toStringTzCalendar(tzid);
    }

    @Override
    public String tzidFromTzdef(String value) throws WebdavException {
        return this.calDavMisc.tzidFromTzdef(value);
    }

    @Override
    public boolean validateAlarm(String value) throws WebdavException {
        return this.calDavMisc.validateAlarm(value);
    }

    @Override
    public void rollback() {
        this.calDavMisc.rollback();
    }

    @Override
    public void close() throws WebdavException {
        this.calDavMisc.close();
    }
}

