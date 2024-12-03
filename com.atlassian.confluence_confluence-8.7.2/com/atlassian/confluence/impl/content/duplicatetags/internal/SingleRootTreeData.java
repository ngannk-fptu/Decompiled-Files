/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.duplicatetags.internal;

import com.atlassian.confluence.impl.content.duplicatetags.internal.Node;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

class SingleRootTreeData {
    private Node topLevelNode = null;
    private final List<XMLEvent> allXmlEvents = new ArrayList<XMLEvent>();

    SingleRootTreeData() {
    }

    Node addStartTag(Node node, Node currentStartNode) {
        if (this.topLevelNode == null) {
            this.topLevelNode = node;
            return node;
        }
        return currentStartNode.addChild(node);
    }

    Node addEndTag(Node node, Node currentStartNode) throws XMLStreamException {
        if (currentStartNode == null) {
            throw new XMLStreamException("Received closing tag without the corresponding opening tag: " + node.tagName);
        }
        currentStartNode.addClosingTag(node);
        return currentStartNode.parentNode;
    }

    Node addInnerOtherTag(Node node, Node currentStartNode) throws XMLStreamException {
        if (currentStartNode == null) {
            throw new XMLStreamException("Unexpected tag. Missed the first opening tag?");
        }
        currentStartNode.addChild(node);
        return currentStartNode;
    }

    void addTopLevelInnerOtherTag(Node node) throws XMLStreamException {
        if (this.topLevelNode != null) {
            throw new XMLStreamException("Can't add the other tag as a root level tag because it is not the first tag.");
        }
        this.topLevelNode = node;
    }

    void addXmlEvent(XMLEvent xmlEvent) {
        this.allXmlEvents.add(xmlEvent);
    }

    public List<XMLEvent> getAllXmlEvents() {
        return this.allXmlEvents;
    }

    public Node getTopLevelNode() {
        return this.topLevelNode;
    }
}

