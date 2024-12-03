/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.plugins.highlight.model.TextCollection;
import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTracker;
import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTrackerV2;
import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTrackerV2Adaptor;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class TextCollector {
    public TextCollection collect(Document document, ModificationStateTracker stateTracker) {
        return this.collectTextRecursively(document.getDocumentElement(), stateTracker, new TextCollection());
    }

    private TextCollection collectTextRecursively(Node node, ModificationStateTracker stateTracker, TextCollection textCollection) {
        ModificationStateTrackerV2 stateTrackerV2;
        ModificationStateTrackerV2 modificationStateTrackerV2 = stateTrackerV2 = stateTracker instanceof ModificationStateTrackerV2 ? (ModificationStateTrackerV2)stateTracker : new ModificationStateTrackerV2Adaptor(stateTracker);
        if (node.getNodeType() == 1 && node.hasChildNodes()) {
            String tagName = ((Element)node).getTagName();
            stateTrackerV2.forward(node, tagName);
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                this.collectTextRecursively(childNodes.item(i), stateTrackerV2, textCollection);
            }
            stateTrackerV2.back(node, tagName);
        }
        if (stateTrackerV2.shouldProcessText(node)) {
            textCollection.add(node, stateTrackerV2.allowInsertion());
        }
        return textCollection;
    }
}

