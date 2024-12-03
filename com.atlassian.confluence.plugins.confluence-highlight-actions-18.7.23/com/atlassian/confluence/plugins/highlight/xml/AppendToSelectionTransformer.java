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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

@Component
public class AppendToSelectionTransformer
extends SelectionTransformer<XMLModification> {
    @Autowired
    public AppendToSelectionTransformer(XMLParserHelper xmlParserHelper) {
        super(xmlParserHelper);
    }

    @Override
    public boolean transform(Document document, TextMatch textMatch, XMLModification xmlInsertion) throws SAXException {
        TextNode endPosition = textMatch.getLastMatchingItem();
        if (endPosition.isModifiable()) {
            this.insertContentAtIndexInTextNode(document, endPosition.getNode(), textMatch.getLastNodeEndIndex(), xmlInsertion.getXml());
        }
        return endPosition.isModifiable();
    }
}

