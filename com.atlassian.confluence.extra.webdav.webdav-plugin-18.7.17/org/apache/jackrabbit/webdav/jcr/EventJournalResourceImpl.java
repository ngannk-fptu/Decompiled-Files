/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventJournal;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.apache.jackrabbit.commons.webdav.EventUtil;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.AdditionalEventInfo;
import org.apache.jackrabbit.util.ISO8601;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.jcr.AbstractResource;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class EventJournalResourceImpl
extends AbstractResource {
    public static final String RELURIFROMWORKSPACE = "?type=journal";
    public static final String EVENTMEDIATYPE = "application/vnd.apache.jackrabbit.event+xml";
    private static Logger log = LoggerFactory.getLogger(EventJournalResourceImpl.class);
    private final HttpServletRequest request;
    private final EventJournal journal;
    private final DavResourceLocator locator;
    private static final String ATOMNS = "http://www.w3.org/2005/Atom";
    private static final String EVNS = ObservationConstants.NAMESPACE.getURI();
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String ENTRY = "entry";
    private static final String FEED = "feed";
    private static final String ID = "id";
    private static final String LINK = "link";
    private static final String NAME = "name";
    private static final String TITLE = "title";
    private static final String UPDATED = "updated";
    private static final String E_EVENT = "event";
    private static final String E_EVENTDATE = "eventdate";
    private static final String E_EVENTIDENTIFIER = "eventidentifier";
    private static final String E_EVENTINFO = "eventinfo";
    private static final String E_EVENTTYPE = "eventtype";
    private static final String E_EVENTMIXINNODETYPE = "eventmixinnodetype";
    private static final String E_EVENTPRIMARNODETYPE = "eventprimarynodetype";
    private static final String E_EVENTUSERDATA = "eventuserdata";
    private static final int MAXWAIT = 2000;
    private static final int MAXEV = 10000;
    private static final Attributes NOATTRS = new AttributesImpl();

    EventJournalResourceImpl(EventJournal journal, DavResourceLocator locator, JcrDavSession session, HttpServletRequest request, DavResourceFactory factory) {
        super(locator, session, factory);
        this.journal = journal;
        this.locator = locator;
        this.request = request;
    }

    @Override
    public String getSupportedMethods() {
        return "GET, HEAD";
    }

    @Override
    public boolean exists() {
        try {
            List<String> available = Arrays.asList(this.getRepositorySession().getWorkspace().getAccessibleWorkspaceNames());
            return available.contains(this.getLocator().getWorkspaceName());
        }
        catch (RepositoryException e) {
            log.warn(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public String getDisplayName() {
        return "event journal for " + this.getLocator().getWorkspaceName();
    }

    @Override
    public long getModificationTime() {
        return System.currentTimeMillis();
    }

    @Override
    public void spool(OutputContext outputContext) throws IOException {
        block31: {
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            try {
                outputContext.setContentType("application/atom+xml; charset=UTF-8");
                outputContext.setProperty("Vary", "If-None-Match");
                long prevts = -1L;
                String inm = this.request.getHeader("If-None-Match");
                if (inm != null && (inm = inm.trim()).startsWith("\"") && inm.endsWith("\"")) {
                    String tmp = inm.substring(1, inm.length() - 1);
                    try {
                        prevts = Long.parseLong(tmp, 16);
                        this.journal.skipTo(prevts);
                    }
                    catch (NumberFormatException numberFormatException) {
                        // empty catch block
                    }
                }
                boolean hasPersistEvents = false;
                if (!outputContext.hasStream()) break block31;
                long lastts = -1L;
                long now = System.currentTimeMillis();
                boolean done = false;
                ArrayList<Event> events = new ArrayList<Event>(10000);
                while (!done && this.journal.hasNext()) {
                    Event e = this.journal.nextEvent();
                    hasPersistEvents |= e.getType() == 64;
                    if (e.getDate() != lastts) {
                        if (events.size() > 10000) {
                            done = true;
                        }
                        if (e.getDate() > now + 2000L) {
                            done = true;
                        }
                    }
                    if (!(done || prevts != -1L && e.getDate() < prevts)) {
                        events.add(e);
                    }
                    lastts = e.getDate();
                }
                if (lastts >= 0L) {
                    outputContext.setETag("\"" + Long.toHexString(lastts) + "\"");
                }
                OutputStream os = outputContext.getOutputStream();
                StreamResult streamResult = new StreamResult(os);
                SAXTransformerFactory tf = (SAXTransformerFactory)TransformerFactory.newInstance();
                TransformerHandler th = tf.newTransformerHandler();
                Transformer s = th.getTransformer();
                s.setOutputProperty("encoding", "UTF-8");
                s.setOutputProperty("indent", "yes");
                s.setOutputProperty("omit-xml-declaration", "yes");
                th.setResult(streamResult);
                th.startDocument();
                th.startElement(ATOMNS, FEED, FEED, NOATTRS);
                this.writeAtomElement(th, TITLE, "EventJournal for " + this.getLocator().getWorkspaceName());
                th.startElement(ATOMNS, AUTHOR, AUTHOR, NOATTRS);
                this.writeAtomElement(th, NAME, "Jackrabbit Event Journal Feed Generator");
                th.endElement(ATOMNS, AUTHOR, AUTHOR);
                String id = this.getFullUri(this.request);
                this.writeAtomElement(th, ID, id);
                AttributesImpl linkattrs = new AttributesImpl();
                linkattrs.addAttribute(null, "self", "self", "CDATA", id);
                this.writeAtomElement(th, LINK, linkattrs, null);
                cal.setTimeInMillis(lastts >= 0L ? lastts : now);
                String upd = ISO8601.format(cal);
                this.writeAtomElement(th, UPDATED, upd);
                String lastDateString = "";
                long lastTimeStamp = 0L;
                long index = 0L;
                AttributesImpl contentatt = new AttributesImpl();
                contentatt.addAttribute(null, "type", "type", "CDATA", EVENTMEDIATYPE);
                while (!events.isEmpty()) {
                    String author;
                    String op;
                    Event e;
                    List<Object> bundle = null;
                    String path = null;
                    if (hasPersistEvents) {
                        bundle = new ArrayList();
                        e = null;
                        op = "operations";
                        do {
                            e = (Event)events.remove(0);
                            bundle.add(e);
                            if (path == null) {
                                path = e.getPath();
                                continue;
                            }
                            if (e.getPath() == null || e.getPath().length() >= path.length()) continue;
                            path = e.getPath();
                        } while (e.getType() != 64 && !events.isEmpty());
                    } else {
                        e = (Event)events.remove(0);
                        bundle = Collections.singletonList(e);
                        path = e.getPath();
                        op = EventUtil.getEventName(e.getType());
                    }
                    Event firstEvent = (Event)bundle.get(0);
                    String entryupd = lastDateString;
                    if (lastTimeStamp != firstEvent.getDate()) {
                        cal.setTimeInMillis(firstEvent.getDate());
                        entryupd = ISO8601.format(cal);
                        index = 0L;
                    } else {
                        ++index;
                    }
                    th.startElement(ATOMNS, ENTRY, ENTRY, NOATTRS);
                    String entrytitle = op + (path != null ? ": " + path : "");
                    this.writeAtomElement(th, TITLE, entrytitle);
                    String entryid = id + "?type=journal&ts=" + Long.toHexString(firstEvent.getDate()) + "-" + index;
                    this.writeAtomElement(th, ID, entryid);
                    String string = author = firstEvent.getUserID() == null || firstEvent.getUserID().length() == 0 ? null : firstEvent.getUserID();
                    if (author != null) {
                        th.startElement(ATOMNS, AUTHOR, AUTHOR, NOATTRS);
                        this.writeAtomElement(th, NAME, author);
                        th.endElement(ATOMNS, AUTHOR, AUTHOR);
                    }
                    this.writeAtomElement(th, UPDATED, entryupd);
                    th.startElement(ATOMNS, CONTENT, CONTENT, contentatt);
                    for (Event event : bundle) {
                        th.startElement(EVNS, E_EVENT, E_EVENT, NOATTRS);
                        if (event.getPath() != null) {
                            boolean isCollection = event.getType() == 1 || event.getType() == 2;
                            String href = this.locator.getFactory().createResourceLocator(this.locator.getPrefix(), this.locator.getWorkspacePath(), event.getPath(), false).getHref(isCollection);
                            th.startElement(DavConstants.NAMESPACE.getURI(), "href", "href", NOATTRS);
                            th.characters(href.toCharArray(), 0, href.length());
                            th.endElement(DavConstants.NAMESPACE.getURI(), "href", "href");
                        }
                        String evname = EventUtil.getEventName(event.getType());
                        th.startElement(EVNS, E_EVENTTYPE, E_EVENTTYPE, NOATTRS);
                        th.startElement(EVNS, evname, evname, NOATTRS);
                        th.endElement(EVNS, evname, evname);
                        th.endElement(EVNS, E_EVENTTYPE, E_EVENTTYPE);
                        this.writeObsElement(th, E_EVENTDATE, Long.toString(event.getDate()));
                        if (event.getUserData() != null && event.getUserData().length() > 0) {
                            this.writeObsElement(th, E_EVENTUSERDATA, firstEvent.getUserData());
                        }
                        if (event instanceof AdditionalEventInfo) {
                            try {
                                Set<Name> mixins;
                                Name pnt = ((AdditionalEventInfo)((Object)event)).getPrimaryNodeTypeName();
                                if (pnt != null) {
                                    this.writeObsElement(th, E_EVENTPRIMARNODETYPE, pnt.toString());
                                }
                                if ((mixins = ((AdditionalEventInfo)((Object)event)).getMixinTypeNames()) != null) {
                                    for (Name mixin : mixins) {
                                        this.writeObsElement(th, E_EVENTMIXINNODETYPE, mixin.toString());
                                    }
                                }
                            }
                            catch (UnsupportedRepositoryOperationException pnt) {
                                // empty catch block
                            }
                        }
                        if (event.getIdentifier() != null) {
                            this.writeObsElement(th, E_EVENTIDENTIFIER, event.getIdentifier());
                        }
                        if (!event.getInfo().isEmpty()) {
                            th.startElement(EVNS, E_EVENTINFO, E_EVENTINFO, NOATTRS);
                            Map m = event.getInfo();
                            for (Map.Entry entry : m.entrySet()) {
                                String key = entry.getKey().toString();
                                Object value = entry.getValue();
                                String t = value != null ? value.toString() : null;
                                this.writeElement(th, null, key, NOATTRS, t);
                            }
                            th.endElement(EVNS, E_EVENTINFO, E_EVENTINFO);
                        }
                        th.endElement(EVNS, E_EVENT, E_EVENT);
                        lastTimeStamp = event.getDate();
                        lastDateString = entryupd;
                    }
                    th.endElement(ATOMNS, CONTENT, CONTENT);
                    th.endElement(ATOMNS, ENTRY, ENTRY);
                }
                th.endElement(ATOMNS, FEED, FEED);
                th.endDocument();
                os.flush();
            }
            catch (Exception ex) {
                throw new IOException("error generating feed: " + ex.getMessage());
            }
        }
    }

    @Override
    public DavResource getCollection() {
        return null;
    }

    @Override
    public void addMember(DavResource resource, InputContext inputContext) throws DavException {
        throw new DavException(403);
    }

    @Override
    public DavResourceIterator getMembers() {
        return DavResourceIteratorImpl.EMPTY;
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        throw new DavException(403);
    }

    @Override
    protected void initLockSupport() {
    }

    @Override
    protected String getWorkspaceHref() {
        return this.getHref();
    }

    private void writeElement(TransformerHandler th, String ns, String name, Attributes attrs, String textContent) throws SAXException {
        th.startElement(ns, name, name, attrs);
        if (textContent != null) {
            th.characters(textContent.toCharArray(), 0, textContent.length());
        }
        th.endElement(ns, name, name);
    }

    private void writeAtomElement(TransformerHandler th, String name, Attributes attrs, String textContent) throws SAXException {
        this.writeElement(th, ATOMNS, name, attrs, textContent);
    }

    private void writeAtomElement(TransformerHandler th, String name, String textContent) throws SAXException {
        this.writeAtomElement(th, name, NOATTRS, textContent);
    }

    private void writeObsElement(TransformerHandler th, String name, String textContent) throws SAXException {
        this.writeElement(th, EVNS, name, NOATTRS, textContent);
    }

    private String getFullUri(HttpServletRequest req) {
        String scheme = req.getScheme();
        int port = req.getServerPort();
        boolean isDefaultPort = scheme.equals("http") && port == 80 || scheme.equals("http") && port == 443;
        String query = this.request.getQueryString() != null ? "?" + this.request.getQueryString() : "";
        return String.format("%s://%s%s%s%s%s", scheme, req.getServerName(), isDefaultPort ? ":" : "", isDefaultPort ? Integer.toString(port) : "", req.getRequestURI(), query);
    }
}

