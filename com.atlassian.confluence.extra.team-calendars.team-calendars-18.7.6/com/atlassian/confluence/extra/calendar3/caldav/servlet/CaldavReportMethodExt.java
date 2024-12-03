/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.caldav.servlet;

import com.atlassian.confluence.extra.calendar3.caldav.CalDavCalendarManager;
import com.atlassian.confluence.extra.calendar3.caldav.CalendarSysIntfImpl;
import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVCollection;
import com.atlassian.confluence.extra.calendar3.caldav.node.CalendarCalDAVEvent;
import com.atlassian.confluence.extra.calendar3.caldav.servlet.SecureXmlMethod;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.caldav.server.CalDAVCollection;
import org.bedework.caldav.server.CalDAVEvent;
import org.bedework.caldav.server.CaldavComponentNode;
import org.bedework.caldav.server.CaldavReportMethod;
import org.bedework.caldav.server.CaldavURI;
import org.bedework.caldav.server.CalendarCalDavBWIntf;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.util.misc.Util;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavNsNode;
import org.bedework.webdav.servlet.shared.WebdavUnauthorized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class CaldavReportMethodExt
extends CaldavReportMethod
implements SecureXmlMethod {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaldavReportMethodExt.class);

    @Override
    protected final Document parseContent(int contentLength, Reader reader) throws WebdavException {
        return this.parseContentSafe(contentLength, reader);
    }

    @Override
    public final void doMethod(HttpServletRequest request, HttpServletResponse response) throws WebdavException {
        if (request.getRemoteUser() == null) {
            throw new WebdavUnauthorized();
        }
        super.doMethod(request, response);
    }

    @Override
    public final Collection<WebdavNsNode> getMgetNodes(Collection<String> hrefs, Collection<String> badHrefs) throws WebdavException {
        try {
            ArrayList<WebdavNsNode> returnList = new ArrayList<WebdavNsNode>();
            CalendarCalDavBWIntf calendarCalDavBWIntf = (CalendarCalDavBWIntf)this.getNsIntf();
            SysIntf sysIntf = calendarCalDavBWIntf.getSysi();
            CalendarSysIntfImpl calendarSysIntf = (CalendarSysIntfImpl)sysIntf;
            CalDavCalendarManager calDavCalendarManager = calendarSysIntf.getCalDavCalendarManager();
            HashSet<String> fallbackHrefs = new HashSet<String>();
            Set splitResults = hrefs.stream().map(href -> {
                SplitResult splitResult = this.splitUri((String)href);
                if (splitResult == null) {
                    fallbackHrefs.add((String)href);
                }
                return splitResult;
            }).collect(Collectors.toSet());
            Map vEventIdGroupByCollection = splitResults.stream().collect(Collectors.groupingBy(SplitResult::getPath, Collectors.toSet()));
            HashMap<String, CalDAVCollection> cachedCollectionStore = new HashMap<String, CalDAVCollection>();
            for (Map.Entry entry : vEventIdGroupByCollection.entrySet()) {
                String collectionUri = entry.getKey();
                CalDAVCollection collection = this.getCalDAVCollection(sysIntf, cachedCollectionStore, collectionUri);
                CalendarCalDAVCollection calendarCalDAVCollection = (CalendarCalDAVCollection)collection;
                String[] vEventIdStrings = entry.getValue().stream().map(SplitResult::getName).collect(Collectors.toSet()).toArray(new String[0]);
                Collection<SubCalendarEvent> events = calDavCalendarManager.getEvents(calendarCalDAVCollection.getPersistedSubCalendar(), vEvent -> true, vEventIdStrings);
                Map<String, List<SubCalendarEvent>> mapByUid = events.stream().collect(Collectors.groupingBy(SubCalendarEvent::getUid));
                if (events.size() < vEventIdStrings.length) {
                    List<String> unableToLoadIds = events.size() == 0 ? Arrays.asList(vEventIdStrings) : (Collection)events.stream().filter(event -> Arrays.stream(vEventIdStrings).noneMatch(vEventId -> vEventId.equals(event.getUid()))).map(SubCalendarEvent::getUid).collect(Collectors.toSet());
                    fallbackHrefs.addAll(unableToLoadIds);
                }
                returnList.addAll(mapByUid.entrySet().stream().map(mapEntry -> {
                    try {
                        CalendarCalDAVEvent calendarCalDAVEvent = new CalendarCalDAVEvent(calendarCalDAVCollection.getPath(), calendarCalDAVCollection.getOwner(), true, (Collection)mapEntry.getValue());
                        CaldavURI caldavURI = this.createCaldavURI(calendarCalDAVCollection, calendarCalDAVEvent, (String)mapEntry.getKey());
                        return new CaldavComponentNode(caldavURI, sysIntf);
                    }
                    catch (WebdavException e) {
                        LOGGER.error("Could not create CalendarCalDAVEvent from SubCalendarEvent");
                        return null;
                    }
                }).collect(Collectors.toSet()));
            }
            if (fallbackHrefs != null && fallbackHrefs.size() > 0) {
                returnList.addAll(super.getMgetNodes(hrefs, badHrefs));
            }
            return returnList;
        }
        catch (Exception e) {
            LOGGER.error("Exception while batching multiget request", (Throwable)e);
            throw new WebdavException(e);
        }
    }

    private CalDAVCollection getCalDAVCollection(SysIntf sysIntf, Map<String, CalDAVCollection> cachedCollectionStore, String collectionUri) throws WebdavException {
        CalDAVCollection tempCol = cachedCollectionStore.get(collectionUri);
        if (tempCol == null) {
            tempCol = sysIntf.getCollection(collectionUri);
            cachedCollectionStore.put(collectionUri, tempCol);
        }
        return tempCol;
    }

    private CaldavURI createCaldavURI(CalDAVCollection calDAVCollection, CalDAVEvent event, String name) {
        CaldavURI calDAVURI;
        try {
            Constructor matchedConstructor = CaldavURI.class.getDeclaredConstructor(CalDAVCollection.class, CalDAVEvent.class, String.class, Boolean.TYPE, Boolean.TYPE);
            matchedConstructor.setAccessible(true);
            calDAVURI = (CaldavURI)matchedConstructor.newInstance(calDAVCollection, event, name, true, false);
        }
        catch (Exception e) {
            LOGGER.error("Could not create CaldavURI instance via reflection");
            return null;
        }
        return calDAVURI;
    }

    private SplitResult splitUri(@Nullable String uri) {
        String noslUri = Util.buildPath(false, uri);
        int pos = noslUri.lastIndexOf("/");
        if (pos <= 0) {
            return null;
        }
        String path = noslUri.substring(0, pos);
        String name = noslUri.substring(pos + 1);
        return new SplitResult(uri, Util.buildPath(true, path), name);
    }

    private static class SplitResult {
        private String uri;
        private String path;
        private String name;

        SplitResult(String uri, String path, String name) {
            this.uri = uri;
            this.path = path;
            this.name = name;
        }

        public String getUri() {
            return this.uri;
        }

        public String getPath() {
            return this.path;
        }

        public String getName() {
            return this.name;
        }
    }
}

