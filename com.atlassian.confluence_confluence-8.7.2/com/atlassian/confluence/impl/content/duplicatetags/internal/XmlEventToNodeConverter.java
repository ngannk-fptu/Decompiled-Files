/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.duplicatetags.internal;

import com.atlassian.confluence.impl.content.duplicatetags.internal.Node;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class XmlEventToNodeConverter {
    Node createNode(XMLEvent xmlEvent, int index) {
        return new Node(this.getTagName(xmlEvent), index, this.convertToHash(xmlEvent), this.getElementType(xmlEvent), this.getElementText(xmlEvent));
    }

    private String getElementText(XMLEvent xmlEvent) {
        return xmlEvent.isCharacters() ? xmlEvent.asCharacters().getData() : null;
    }

    private Node.Type getElementType(XMLEvent xmlEvent) {
        if (xmlEvent instanceof StartElement) {
            return Node.Type.START_TAG;
        }
        if (xmlEvent instanceof EndElement) {
            return Node.Type.END_TAG;
        }
        return Node.Type.ANY_OTHER_TAG;
    }

    private String getTagName(XMLEvent xmlEvent) {
        if (xmlEvent instanceof StartElement) {
            return ((StartElement)xmlEvent).getName().getLocalPart();
        }
        if (xmlEvent instanceof EndElement) {
            return ((EndElement)xmlEvent).getName().getLocalPart();
        }
        return null;
    }

    long convertToHash(XMLEvent xmlEvent) {
        return xmlEvent.hashCode();
    }
}

