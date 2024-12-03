/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.observation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.observation.EventType;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultEventType
implements EventType {
    private static final Map<String, EventType> eventTypes = new HashMap<String, EventType>();
    private final String localName;
    private final Namespace namespace;

    private DefaultEventType(String localName, Namespace namespace) {
        this.localName = localName;
        this.namespace = namespace;
    }

    public static EventType create(String localName, Namespace namespace) {
        if (localName == null || "".equals(localName)) {
            throw new IllegalArgumentException("null and '' are not valid local names of an event type.");
        }
        String key = DomUtil.getExpandedName(localName, namespace);
        if (eventTypes.containsKey(key)) {
            return eventTypes.get(key);
        }
        DefaultEventType type = new DefaultEventType(localName, namespace);
        eventTypes.put(key, type);
        return type;
    }

    public static EventType[] create(String[] localNames, Namespace namespace) {
        EventType[] types = new EventType[localNames.length];
        for (int i = 0; i < localNames.length; ++i) {
            types[i] = DefaultEventType.create(localNames[i], namespace);
        }
        return types;
    }

    public static EventType[] createFromXml(Element eventType) {
        if (!DomUtil.matches(eventType, "eventtype", ObservationConstants.NAMESPACE)) {
            throw new IllegalArgumentException("'eventtype' element expected which contains a least a single child element.");
        }
        ArrayList<EventType> etypes = new ArrayList<EventType>();
        ElementIterator it = DomUtil.getChildren(eventType);
        while (it.hasNext()) {
            Element el = it.nextElement();
            etypes.add(DefaultEventType.create(el.getLocalName(), DomUtil.getNamespace(el)));
        }
        return etypes.toArray(new EventType[etypes.size()]);
    }

    @Override
    public String getName() {
        return this.localName;
    }

    @Override
    public Namespace getNamespace() {
        return this.namespace;
    }

    @Override
    public Element toXml(Document document) {
        return DomUtil.createElement(document, this.localName, this.namespace);
    }
}

