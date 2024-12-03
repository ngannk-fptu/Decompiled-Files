/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.observation;

import javax.xml.namespace.QName;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;

public interface ObservationConstants {
    public static final Namespace NAMESPACE = Namespace.getNamespace("dcr", "http://www.day.com/jcr/webdav/1.0");
    public static final String HEADER_SUBSCRIPTIONID = "SubscriptionId";
    public static final String HEADER_POLL_TIMEOUT = "PollTimeout";
    public static final String XML_SUBSCRIPTION = "subscription";
    public static final String XML_SUBSCRIPTIONINFO = "subscriptioninfo";
    public static final String XML_EVENTTYPE = "eventtype";
    public static final String XML_NOLOCAL = "nolocal";
    public static final String XML_FILTER = "filter";
    public static final String XML_SUBSCRIPTIONID = "subscriptionid";
    public static final String XML_EVENTSWITHTYPES = "eventswithnodetypes";
    public static final String XML_EVENTSWITHLOCALFLAG = "eventswithlocalflag";
    public static final String XML_UUID = "uuid";
    public static final String XML_NODETYPE_NAME = "nodetype-name";
    public static final String XML_EVENTDISCOVERY = "eventdiscovery";
    public static final String XML_EVENTBUNDLE = "eventbundle";
    public static final String XML_EVENT_TRANSACTION_ID = "transactionid";
    public static final String XML_EVENT_LOCAL = "local";
    public static final String XML_EVENT = "event";
    public static final String XML_EVENTUSERID = "eventuserid";
    public static final String XML_EVENTUSERDATA = "eventuserdata";
    public static final String XML_EVENTDATE = "eventdate";
    public static final String XML_EVENTIDENTIFIER = "eventidentifier";
    public static final String XML_EVENTINFO = "eventinfo";
    public static final String XML_EVENTPRIMARNODETYPE = "eventprimarynodetype";
    public static final String XML_EVENTMIXINNODETYPE = "eventmixinnodetype";
    public static final QName N_EVENT = new QName(NAMESPACE.getURI(), "event");
    public static final QName N_EVENTBUNDLE = new QName(NAMESPACE.getURI(), "eventbundle");
    public static final QName N_EVENTDATE = new QName(NAMESPACE.getURI(), "eventdate");
    public static final QName N_EVENTDISCOVERY = new QName(NAMESPACE.getURI(), "eventdiscovery");
    public static final QName N_EVENTINFO = new QName(NAMESPACE.getURI(), "eventinfo");
    public static final QName N_EVENTMIXINNODETYPE = new QName(NAMESPACE.getURI(), "eventmixinnodetype");
    public static final QName N_EVENTPRIMARYNODETYPE = new QName(NAMESPACE.getURI(), "eventprimarynodetype");
    public static final QName N_EVENTTYPE = new QName(NAMESPACE.getURI(), "eventtype");
    public static final QName N_EVENTUSERDATA = new QName(NAMESPACE.getURI(), "eventuserdata");
    public static final QName N_EVENTUSERID = new QName(NAMESPACE.getURI(), "eventuserid");
    public static final DavPropertyName SUBSCRIPTIONDISCOVERY = DavPropertyName.create("subscriptiondiscovery", NAMESPACE);
}

