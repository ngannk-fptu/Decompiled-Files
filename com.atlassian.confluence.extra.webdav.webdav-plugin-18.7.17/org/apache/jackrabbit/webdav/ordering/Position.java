/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.ordering;

import java.util.HashSet;
import java.util.Set;
import org.apache.jackrabbit.webdav.ordering.OrderingConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Position
implements OrderingConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(Position.class);
    private static final Set<String> VALID_TYPES = new HashSet<String>();
    private final String type;
    private final String segment;

    public Position(String type) {
        if (!VALID_TYPES.contains(type)) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        if (!"first".equals(type) && !"last".equals(type)) {
            throw new IllegalArgumentException("If type is other than 'first' or 'last' a segment must be specified");
        }
        this.type = type;
        this.segment = null;
    }

    public Position(String type, String segment) {
        if (!VALID_TYPES.contains(type)) {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        if (("after".equals(type) || "before".equals(type)) && (segment == null || "".equals(segment))) {
            throw new IllegalArgumentException("If type is other than 'first' or 'last' a segment must be specified");
        }
        this.type = type;
        this.segment = segment;
    }

    public String getType() {
        return this.type;
    }

    public String getSegment() {
        return this.segment;
    }

    @Override
    public Element toXml(Document document) {
        Element positionElement = DomUtil.createElement(document, "position", NAMESPACE);
        Element typeElement = DomUtil.addChildElement(positionElement, this.type, NAMESPACE);
        if (this.segment != null) {
            DomUtil.addChildElement(typeElement, "segment", NAMESPACE, this.segment);
        }
        return positionElement;
    }

    public static Position createFromXml(Element positionElement) {
        if (!DomUtil.matches(positionElement, "position", NAMESPACE)) {
            throw new IllegalArgumentException("The 'DAV:position' element required.");
        }
        ElementIterator it = DomUtil.getChildren(positionElement);
        if (it.hasNext()) {
            Element el = it.nextElement();
            String type = el.getLocalName();
            String segmentText = DomUtil.getChildText(el, "segment", NAMESPACE);
            return new Position(type, segmentText);
        }
        throw new IllegalArgumentException("The 'DAV:position' element required with exact one child indicating the type.");
    }

    static {
        VALID_TYPES.add("first");
        VALID_TYPES.add("last");
        VALID_TYPES.add("after");
        VALID_TYPES.add("before");
    }
}

