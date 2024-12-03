/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.observation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.jackrabbit.webdav.observation.EventBundle;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EventDiscovery
implements ObservationConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(EventDiscovery.class);
    private final List<EventBundle> bundles = new ArrayList<EventBundle>();

    public void addEventBundle(EventBundle eventBundle) {
        if (eventBundle != null) {
            this.bundles.add(eventBundle);
        }
    }

    public Iterator<EventBundle> getEventBundles() {
        return this.bundles.iterator();
    }

    public boolean isEmpty() {
        return this.bundles.isEmpty();
    }

    @Override
    public Element toXml(Document document) {
        Element ed = DomUtil.createElement(document, "eventdiscovery", NAMESPACE);
        for (EventBundle bundle : this.bundles) {
            ed.appendChild(bundle.toXml(document));
        }
        return ed;
    }

    public static EventDiscovery createFromXml(Element eventDiscoveryElement) {
        if (!DomUtil.matches(eventDiscoveryElement, "eventdiscovery", ObservationConstants.NAMESPACE)) {
            throw new IllegalArgumentException("{" + ObservationConstants.NAMESPACE + "}" + "eventdiscovery" + " element expected, but got: {" + eventDiscoveryElement.getNamespaceURI() + "}" + eventDiscoveryElement.getLocalName());
        }
        EventDiscovery eventDiscovery = new EventDiscovery();
        ElementIterator it = DomUtil.getChildren(eventDiscoveryElement, "eventbundle", ObservationConstants.NAMESPACE);
        while (it.hasNext()) {
            final Element ebElement = it.nextElement();
            EventBundle eb = new EventBundle(){

                @Override
                public Element toXml(Document document) {
                    return (Element)document.importNode(ebElement, true);
                }
            };
            eventDiscovery.addEventBundle(eb);
        }
        return eventDiscovery;
    }
}

