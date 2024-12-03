/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.duplicatetags.internal;

import com.atlassian.confluence.impl.content.duplicatetags.internal.Node;
import com.atlassian.confluence.impl.content.duplicatetags.internal.SingleRootTreeData;
import com.atlassian.confluence.impl.content.duplicatetags.internal.XmlEventToNodeConverter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

class SingleXmlBranchReader {
    private final XmlEventToNodeConverter xmlEventToNodeConverter;

    SingleXmlBranchReader(XmlEventToNodeConverter xmlEventToNodeConverter) {
        this.xmlEventToNodeConverter = xmlEventToNodeConverter;
    }

    SingleRootTreeData readCurrentTopLevelBranchFromTheInputStream(XMLEventReader xmlEventReader) throws XMLStreamException {
        if (!xmlEventReader.hasNext()) {
            throw new IllegalStateException("xmlEventReader is empty");
        }
        int index = 0;
        int level = 0;
        SingleRootTreeData treeData = new SingleRootTreeData();
        Node currentStartNode = null;
        while (xmlEventReader.hasNext()) {
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            treeData.addXmlEvent(xmlEvent);
            Node node = this.xmlEventToNodeConverter.createNode(xmlEvent, index++);
            switch (node.tagType) {
                case START_TAG: {
                    currentStartNode = treeData.addStartTag(node, currentStartNode);
                    ++level;
                    break;
                }
                case END_TAG: {
                    currentStartNode = treeData.addEndTag(node, currentStartNode);
                    --level;
                    break;
                }
                case ANY_OTHER_TAG: {
                    if (currentStartNode == null) {
                        treeData.addTopLevelInnerOtherTag(node);
                        return treeData;
                    }
                    currentStartNode = treeData.addInnerOtherTag(node, currentStartNode);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unexpected type: " + node.tagType);
                }
            }
            if (level != 0) continue;
            return treeData;
        }
        return treeData;
    }
}

