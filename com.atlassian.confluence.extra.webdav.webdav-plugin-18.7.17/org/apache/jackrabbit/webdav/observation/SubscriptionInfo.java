/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.observation;

import java.util.ArrayList;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.observation.DefaultEventType;
import org.apache.jackrabbit.webdav.observation.EventType;
import org.apache.jackrabbit.webdav.observation.Filter;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SubscriptionInfo
implements ObservationConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(SubscriptionInfo.class);
    private final EventType[] eventTypes;
    private final Filter[] filters;
    private final boolean noLocal;
    private final boolean isDeep;
    private final long timeout;

    public SubscriptionInfo(EventType[] eventTypes, boolean isDeep, long timeout) {
        this(eventTypes, null, false, isDeep, timeout);
    }

    public SubscriptionInfo(EventType[] eventTypes, Filter[] filters, boolean noLocal, boolean isDeep, long timeout) {
        if (eventTypes == null || eventTypes.length == 0) {
            throw new IllegalArgumentException("'subscriptioninfo' must at least indicate a single event type.");
        }
        this.eventTypes = eventTypes;
        this.noLocal = noLocal;
        this.filters = filters != null ? filters : new Filter[0];
        this.isDeep = isDeep;
        this.timeout = timeout;
    }

    public SubscriptionInfo(Element reqInfo, long timeout, boolean isDeep) throws DavException {
        if (!DomUtil.matches(reqInfo, "subscriptioninfo", NAMESPACE)) {
            log.warn("Element with name 'subscriptioninfo' expected");
            throw new DavException(400);
        }
        Element el = DomUtil.getChildElement(reqInfo, "eventtype", NAMESPACE);
        if (el != null) {
            this.eventTypes = DefaultEventType.createFromXml(el);
            if (this.eventTypes.length == 0) {
                log.warn("'subscriptioninfo' must at least indicate a single, valid event type.");
                throw new DavException(400);
            }
        } else {
            log.warn("'subscriptioninfo' must contain an 'eventtype' child element.");
            throw new DavException(400);
        }
        ArrayList<Filter> filters = new ArrayList<Filter>();
        el = DomUtil.getChildElement(reqInfo, "filter", NAMESPACE);
        if (el != null) {
            ElementIterator it = DomUtil.getChildren(el);
            while (it.hasNext()) {
                Filter f = new Filter(it.nextElement());
                filters.add(f);
            }
        }
        this.filters = filters.toArray(new Filter[filters.size()]);
        this.noLocal = DomUtil.hasChildElement(reqInfo, "nolocal", NAMESPACE);
        this.isDeep = isDeep;
        this.timeout = timeout;
    }

    public EventType[] getEventTypes() {
        return this.eventTypes;
    }

    public Filter[] getFilters() {
        return this.filters;
    }

    public Filter[] getFilters(String localName, Namespace namespace) {
        ArrayList<Filter> l = new ArrayList<Filter>();
        for (Filter filter : this.filters) {
            if (!filter.isMatchingFilter(localName, namespace)) continue;
            l.add(filter);
        }
        return l.toArray(new Filter[l.size()]);
    }

    public boolean isNoLocal() {
        return this.noLocal;
    }

    public boolean isDeep() {
        return this.isDeep;
    }

    public long getTimeOut() {
        return this.timeout;
    }

    @Override
    public Element toXml(Document document) {
        Element subscrInfo = DomUtil.createElement(document, "subscriptioninfo", NAMESPACE);
        Element eventType = DomUtil.addChildElement(subscrInfo, "eventtype", NAMESPACE);
        for (EventType et : this.eventTypes) {
            eventType.appendChild(et.toXml(document));
        }
        if (this.filters.length > 0) {
            Element filter = DomUtil.addChildElement(subscrInfo, "filter", NAMESPACE);
            for (Filter f : this.filters) {
                filter.appendChild(f.toXml(document));
            }
        }
        if (this.noLocal) {
            DomUtil.addChildElement(subscrInfo, "nolocal", NAMESPACE);
        }
        return subscrInfo;
    }
}

