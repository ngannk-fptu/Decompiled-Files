/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.plugins.highlight.model.TextMatch;
import com.atlassian.confluence.plugins.highlight.model.TextNode;
import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import com.atlassian.confluence.plugins.highlight.xml.SelectionTransformer;
import com.atlassian.confluence.plugins.highlight.xml.XMLParserHelper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@Component
public class MarkSelectionTransformer
extends SelectionTransformer<XMLModification> {
    @Autowired
    public MarkSelectionTransformer(XMLParserHelper xmlParserHelper) {
        super(xmlParserHelper);
    }

    @Override
    public boolean transform(Document document, TextMatch textMatch, XMLModification xmlModification) throws SAXException {
        int i = 1;
        boolean wrapped = false;
        List<TextNode> positions = textMatch.getMatchingNodes();
        DocumentFragment wrappingXml = this.xmlParserHelper.parseDocumentFragment(document, xmlModification.getXml());
        for (TextNode textNode : positions) {
            if (textNode.isModifiable()) {
                Node node = textNode.getNode();
                Object nodeText = node.getNodeValue();
                int inNodeStartIndex = i == 1 ? textMatch.getFirstNodeStartIndex() : 0;
                int inNodeEndIndex = i == positions.size() ? textMatch.getLastNodeEndIndex() : ((String)nodeText).length();
                String textToBeWrapped = ((String)nodeText).substring(inNodeStartIndex, inNodeEndIndex);
                Node fragment = document.importNode(wrappingXml, true);
                Node wrapper = fragment.getFirstChild();
                while (wrapper.hasChildNodes()) {
                    wrapper = wrapper.getFirstChild();
                }
                wrapper.setTextContent(textToBeWrapped);
                if (inNodeStartIndex == 0 && inNodeEndIndex == ((String)nodeText).length()) {
                    node.getParentNode().replaceChild(wrapper, node);
                } else {
                    nodeText = ((String)nodeText).substring(0, inNodeStartIndex) + ((String)nodeText).substring(inNodeEndIndex, ((String)nodeText).length());
                    node.setNodeValue((String)nodeText);
                    this.insertContentAtIndexInTextNode(document, node, inNodeStartIndex, fragment);
                }
                wrapped = true;
            }
            ++i;
        }
        return wrapped;
    }
}

