/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.observation;

import java.util.ArrayList;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.apache.jackrabbit.webdav.observation.Subscription;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SubscriptionDiscovery
extends AbstractDavProperty<Subscription[]> {
    private final Subscription[] subscriptions;

    public SubscriptionDiscovery(Subscription[] subscriptions) {
        super(ObservationConstants.SUBSCRIPTIONDISCOVERY, true);
        this.subscriptions = subscriptions != null ? subscriptions : new Subscription[0];
    }

    public SubscriptionDiscovery(Subscription subscription) {
        super(ObservationConstants.SUBSCRIPTIONDISCOVERY, true);
        this.subscriptions = subscription != null ? new Subscription[]{subscription} : new Subscription[0];
    }

    @Override
    public Subscription[] getValue() {
        return this.subscriptions;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        for (Subscription subscription : this.subscriptions) {
            elem.appendChild(subscription.toXml(document));
        }
        return elem;
    }

    public static SubscriptionDiscovery createFromXml(Element sDiscoveryElement) {
        if (!DomUtil.matches(sDiscoveryElement, ObservationConstants.SUBSCRIPTIONDISCOVERY.getName(), ObservationConstants.SUBSCRIPTIONDISCOVERY.getNamespace())) {
            throw new IllegalArgumentException("'subscriptiondiscovery' element expected.");
        }
        ArrayList<1> subscriptions = new ArrayList<1>();
        ElementIterator it = DomUtil.getChildren(sDiscoveryElement, "subscription", ObservationConstants.NAMESPACE);
        while (it.hasNext()) {
            final Element sb = it.nextElement();
            Subscription s = new Subscription(){

                @Override
                public String getSubscriptionId() {
                    Element ltEl = DomUtil.getChildElement(sb, "subscriptionid", ObservationConstants.NAMESPACE);
                    if (ltEl != null) {
                        return DomUtil.getChildText(sb, "href", DavConstants.NAMESPACE);
                    }
                    return null;
                }

                @Override
                public boolean eventsProvideNodeTypeInformation() {
                    String t = DomUtil.getChildText(sb, "eventswithnodetypes", ObservationConstants.NAMESPACE);
                    return t == null ? false : Boolean.parseBoolean(t);
                }

                @Override
                public boolean eventsProvideNoLocalFlag() {
                    String t = DomUtil.getChildText(sb, "eventswithlocalflag", ObservationConstants.NAMESPACE);
                    return t == null ? false : Boolean.parseBoolean(t);
                }

                @Override
                public Element toXml(Document document) {
                    return (Element)document.importNode(sb, true);
                }
            };
            subscriptions.add(s);
        }
        return new SubscriptionDiscovery(subscriptions.toArray(new Subscription[subscriptions.size()]));
    }
}

