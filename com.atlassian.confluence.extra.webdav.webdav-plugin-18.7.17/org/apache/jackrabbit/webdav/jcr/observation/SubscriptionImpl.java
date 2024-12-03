/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.observation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import org.apache.jackrabbit.commons.webdav.EventUtil;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.AdditionalEventInfo;
import org.apache.jackrabbit.spi.commons.SessionExtensions;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.transaction.TransactionListener;
import org.apache.jackrabbit.webdav.observation.DefaultEventType;
import org.apache.jackrabbit.webdav.observation.EventBundle;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.EventType;
import org.apache.jackrabbit.webdav.observation.Filter;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.apache.jackrabbit.webdav.observation.ObservationResource;
import org.apache.jackrabbit.webdav.observation.Subscription;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;
import org.apache.jackrabbit.webdav.transaction.TransactionResource;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SubscriptionImpl
implements Subscription,
ObservationConstants,
EventListener {
    private static Logger log = LoggerFactory.getLogger(SubscriptionImpl.class);
    private static final long DEFAULT_TIMEOUT = 300000L;
    private SubscriptionInfo info;
    private long expirationTime;
    private final DavResourceLocator locator;
    private final String subscriptionId = UUID.randomUUID().toString();
    private final List<EventBundle> eventBundles = new ArrayList<EventBundle>();
    private final ObservationManager obsMgr;
    private final Session session;

    public SubscriptionImpl(SubscriptionInfo info, ObservationResource resource) throws DavException {
        this.setInfo(info);
        this.locator = resource.getLocator();
        this.session = JcrDavSession.getRepositorySession(resource.getSession());
        try {
            this.obsMgr = this.session.getWorkspace().getObservationManager();
        }
        catch (RepositoryException e) {
            throw new DavException(500, (Throwable)e);
        }
    }

    @Override
    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    @Override
    public boolean eventsProvideNodeTypeInformation() {
        String t = this.session.getRepository().getDescriptor("org.apache.jackrabbit.spi.commons.AdditionalEventInfo");
        return t == null ? false : Boolean.parseBoolean(t);
    }

    @Override
    public boolean eventsProvideNoLocalFlag() {
        return this.session instanceof SessionExtensions;
    }

    @Override
    public Element toXml(Document document) {
        Element subscr = DomUtil.createElement(document, "subscription", NAMESPACE);
        subscr.appendChild(this.info.toXml(document));
        subscr.appendChild(DomUtil.depthToXml(this.info.isDeep(), document));
        subscr.appendChild(DomUtil.timeoutToXml(this.info.getTimeOut(), document));
        if (this.getSubscriptionId() != null) {
            Element id = DomUtil.addChildElement(subscr, "subscriptionid", NAMESPACE);
            id.appendChild(DomUtil.hrefToXml(this.getSubscriptionId(), document));
        }
        DomUtil.addChildElement(subscr, "eventswithnodetypes", NAMESPACE, Boolean.toString(this.eventsProvideNodeTypeInformation()));
        DomUtil.addChildElement(subscr, "eventswithlocalflag", NAMESPACE, Boolean.toString(this.eventsProvideNoLocalFlag()));
        return subscr;
    }

    void setInfo(SubscriptionInfo info) {
        this.info = info;
        long timeout = info.getTimeOut();
        if (timeout <= 0L) {
            timeout = 300000L;
        }
        this.expirationTime = System.currentTimeMillis() + timeout;
    }

    int getJcrEventTypes() throws DavException {
        EventType[] eventTypes = this.info.getEventTypes();
        int events = 0;
        for (EventType eventType : eventTypes) {
            events |= SubscriptionImpl.getJcrEventType(eventType);
        }
        return events;
    }

    String[] getUuidFilters() {
        return this.getFilterValues("uuid");
    }

    String[] getNodetypeNameFilters() {
        return this.getFilterValues("nodetype-name");
    }

    private String[] getFilterValues(String filterLocalName) {
        ArrayList<String> values = new ArrayList<String>();
        for (Filter filter : this.info.getFilters(filterLocalName, NAMESPACE)) {
            String val = filter.getValue();
            if (val == null) continue;
            values.add(val);
        }
        return values.size() > 0 ? values.toArray(new String[values.size()]) : null;
    }

    boolean isNoLocal() {
        return this.info.isNoLocal();
    }

    boolean isDeep() {
        return this.info.isDeep();
    }

    DavResourceLocator getLocator() {
        return this.locator;
    }

    boolean isSubscribedToResource(ObservationResource resource) {
        return this.locator.getResourcePath().equals(resource.getResourcePath());
    }

    boolean isExpired() {
        return System.currentTimeMillis() > this.expirationTime;
    }

    synchronized EventDiscovery discoverEvents(long timeout) {
        EventDiscovery ed = new EventDiscovery();
        if (this.eventBundles.isEmpty() && timeout > 0L) {
            try {
                this.wait(timeout);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        for (EventBundle eb : this.eventBundles) {
            ed.addEventBundle(eb);
        }
        this.eventBundles.clear();
        return ed;
    }

    TransactionListener createTransactionListener() {
        if (this.info.isNoLocal()) {
            return new TransactionEvent(){

                @Override
                public void onEvent(EventIterator events) {
                }

                @Override
                public void beforeCommit(TransactionResource resource, String lockToken) {
                }

                @Override
                public void afterCommit(TransactionResource resource, String lockToken, boolean success) {
                }
            };
        }
        return new TransactionEvent();
    }

    void suspend() throws DavException {
        try {
            this.obsMgr.removeEventListener(this);
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    void resume() throws DavException {
        try {
            this.obsMgr.addEventListener(this, this.getJcrEventTypes(), this.getLocator().getRepositoryPath(), this.isDeep(), this.getUuidFilters(), this.getNodetypeNameFilters(), this.isNoLocal());
        }
        catch (RepositoryException e) {
            throw new JcrDavException(e);
        }
    }

    @Override
    public synchronized void onEvent(EventIterator events) {
        if (!this.isExpired()) {
            this.eventBundles.add(new EventBundleImpl(events));
        } else {
            try {
                this.obsMgr.removeEventListener(this);
            }
            catch (RepositoryException e) {
                log.warn("Exception while unsubscribing: " + e);
            }
        }
        this.notifyAll();
    }

    public static EventType getEventType(int jcrEventType) {
        String localName = EventUtil.getEventName(jcrEventType);
        return DefaultEventType.create(localName, NAMESPACE);
    }

    public static EventType[] getAllEventTypes() {
        EventType[] types = DefaultEventType.create(EventUtil.EVENT_ALL, NAMESPACE);
        return types;
    }

    public static int getJcrEventType(EventType eventType) throws DavException {
        if (eventType == null || !NAMESPACE.equals(eventType.getNamespace())) {
            throw new DavException(422, "Invalid JCR event type: " + eventType + ": Namespace mismatch.");
        }
        String eventName = eventType.getName();
        if (!EventUtil.isValidEventName(eventName)) {
            throw new DavException(422, "Invalid event type: " + eventName);
        }
        return EventUtil.getJcrEventType(eventName);
    }

    protected static void serializeInfoMap(Element eventElem, Session session, Map<?, ?> map) {
        Element info = DomUtil.addChildElement(eventElem, "eventinfo", NAMESPACE);
        Map<?, ?> m = map;
        for (Map.Entry<?, ?> entry : m.entrySet()) {
            try {
                Object value;
                String key = entry.getKey().toString();
                Namespace ns = Namespace.EMPTY_NAMESPACE;
                int colon = key.indexOf(58);
                if (colon >= 0) {
                    String prefix = key.substring(0, colon);
                    String localname = key.substring(colon + 1);
                    ns = Namespace.getNamespace(prefix, session.getNamespaceURI(prefix));
                    key = localname;
                }
                if ((value = entry.getValue()) != null) {
                    DomUtil.addChildElement(info, key, ns, value.toString());
                    continue;
                }
                DomUtil.addChildElement(info, key, ns);
            }
            catch (RepositoryException nse) {
                log.error("Internal error while getting namespaceUri, info map field skipped for {}", entry.getKey());
            }
        }
    }

    private class TransactionEvent
    implements EventListener,
    TransactionListener {
        private String transactionId;

        private TransactionEvent() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void onEvent(EventIterator events) {
            String tId = this.transactionId;
            if (tId == null) {
                tId = UUID.randomUUID().toString();
            }
            SubscriptionImpl subscriptionImpl = SubscriptionImpl.this;
            synchronized (subscriptionImpl) {
                SubscriptionImpl.this.eventBundles.add(new EventBundleImpl(events, tId));
                SubscriptionImpl.this.notifyAll();
            }
        }

        @Override
        public void beforeCommit(TransactionResource resource, String lockToken) {
            try {
                this.transactionId = lockToken;
                SubscriptionImpl.this.obsMgr.addEventListener(this, SubscriptionImpl.this.getJcrEventTypes(), SubscriptionImpl.this.getLocator().getRepositoryPath(), SubscriptionImpl.this.isDeep(), SubscriptionImpl.this.getUuidFilters(), SubscriptionImpl.this.getNodetypeNameFilters(), SubscriptionImpl.this.isNoLocal());
                SubscriptionImpl.this.suspend();
            }
            catch (RepositoryException e) {
                log.warn("Unable to register TransactionListener: " + e);
            }
            catch (DavException e) {
                log.warn("Unable to register TransactionListener: " + e);
            }
        }

        @Override
        public void afterCommit(TransactionResource resource, String lockToken, boolean success) {
            try {
                SubscriptionImpl.this.resume();
                SubscriptionImpl.this.obsMgr.removeEventListener(this);
            }
            catch (RepositoryException e) {
                log.warn("Unable to remove listener: " + e);
            }
            catch (DavException e) {
                log.warn("Unable to resume Subscription: " + e);
            }
        }
    }

    private class EventBundleImpl
    implements EventBundle {
        private final EventIterator events;
        private final String transactionId;

        private EventBundleImpl(EventIterator events) {
            this(events, (String)null);
        }

        private EventBundleImpl(EventIterator events, String transactionId) {
            this.events = events;
            this.transactionId = transactionId;
        }

        @Override
        public Element toXml(Document document) {
            Element bundle = DomUtil.createElement(document, "eventbundle", ObservationConstants.NAMESPACE);
            if (this.transactionId != null) {
                DomUtil.setAttribute(bundle, "transactionid", ObservationConstants.NAMESPACE, this.transactionId);
            }
            boolean localFlagSet = false;
            while (this.events.hasNext()) {
                Event event = this.events.nextEvent();
                if (!localFlagSet) {
                    localFlagSet = true;
                    String name = "http://www.day.com/jcr/webdav/1.0/session-id";
                    Object forSessionId = SubscriptionImpl.this.session.getAttribute(name);
                    if (forSessionId != null && event instanceof AdditionalEventInfo) {
                        AdditionalEventInfo aei = (AdditionalEventInfo)((Object)event);
                        try {
                            boolean isLocal = forSessionId.equals(aei.getSessionAttribute(name));
                            DomUtil.setAttribute(bundle, "local", null, Boolean.toString(isLocal));
                        }
                        catch (UnsupportedRepositoryOperationException isLocal) {
                            // empty catch block
                        }
                    }
                }
                Element eventElem = DomUtil.addChildElement(bundle, "event", ObservationConstants.NAMESPACE);
                String eHref = "";
                try {
                    boolean isCollection = event.getType() == 1 || event.getType() == 2;
                    eHref = SubscriptionImpl.this.locator.getFactory().createResourceLocator(SubscriptionImpl.this.locator.getPrefix(), SubscriptionImpl.this.locator.getWorkspacePath(), event.getPath(), false).getHref(isCollection);
                }
                catch (RepositoryException e) {
                    log.error(e.getMessage());
                }
                eventElem.appendChild(DomUtil.hrefToXml(eHref, document));
                Element eType = DomUtil.addChildElement(eventElem, "eventtype", ObservationConstants.NAMESPACE);
                eType.appendChild(SubscriptionImpl.getEventType(event.getType()).toXml(document));
                DomUtil.addChildElement(eventElem, "eventuserid", ObservationConstants.NAMESPACE, event.getUserID());
                if (event instanceof AdditionalEventInfo) {
                    try {
                        DomUtil.addChildElement(eventElem, "eventprimarynodetype", ObservationConstants.NAMESPACE, ((AdditionalEventInfo)((Object)event)).getPrimaryNodeTypeName().toString());
                        for (Name mixin : ((AdditionalEventInfo)((Object)event)).getMixinTypeNames()) {
                            DomUtil.addChildElement(eventElem, "eventmixinnodetype", ObservationConstants.NAMESPACE, mixin.toString());
                        }
                    }
                    catch (UnsupportedRepositoryOperationException isLocal) {
                        // empty catch block
                    }
                }
                try {
                    DomUtil.addChildElement(eventElem, "eventuserdata", ObservationConstants.NAMESPACE, event.getUserData());
                }
                catch (RepositoryException e) {
                    log.error("Internal error while retrieving event user data. {}", (Object)e.getMessage());
                }
                try {
                    DomUtil.addChildElement(eventElem, "eventdate", ObservationConstants.NAMESPACE, String.valueOf(event.getDate()));
                }
                catch (RepositoryException e) {
                    log.error("Internal error while retrieving event date. {}", (Object)e.getMessage());
                }
                try {
                    DomUtil.addChildElement(eventElem, "eventidentifier", ObservationConstants.NAMESPACE, event.getIdentifier());
                }
                catch (RepositoryException e) {
                    log.error("Internal error while retrieving event identifier. {}", (Object)e.getMessage());
                }
                try {
                    SubscriptionImpl.serializeInfoMap(eventElem, SubscriptionImpl.this.session, event.getInfo());
                }
                catch (RepositoryException e) {
                    log.error("Internal error while retrieving event info. {}", (Object)e.getMessage());
                }
            }
            return bundle;
        }
    }
}

