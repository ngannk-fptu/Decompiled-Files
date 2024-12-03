/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight.service;

import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.highlight.SelectionModificationException;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import com.atlassian.confluence.plugins.highlight.service.AbstractSelectionModifier;
import com.atlassian.confluence.plugins.highlight.service.SelectionValidator;
import com.atlassian.confluence.plugins.highlight.xml.XMLParserHelper;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

@Component
public class StripTagModifier
extends AbstractSelectionModifier {
    private static final Logger log = LoggerFactory.getLogger(StripTagModifier.class);
    private final SelectionValidator<XMLModification> selectionValidator;

    @Autowired
    protected StripTagModifier(XMLParserHelper xmlParserHelper, @ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport DarkFeaturesManager darkFeaturesManager, @Qualifier(value="xmlModificationValidator") SelectionValidator<XMLModification> selectionValidator) {
        super(xmlParserHelper, pageManager, eventPublisher, darkFeaturesManager);
        this.selectionValidator = selectionValidator;
    }

    @Override
    protected String getModifier() {
        return "striptag";
    }

    public boolean modify(long pageId, long lastFetchTime, NodeFilter nodeFilter) throws SAXException, SelectionModificationException {
        AbstractPage abstractPage = this.pageManager.getAbstractPage(pageId);
        this.selectionValidator.validatePage(pageId, abstractPage, lastFetchTime).validatePermissions(abstractPage);
        Document document = this.xmlParserHelper.parseDocument(abstractPage.getBodyAsString());
        boolean matched = StripTagModifier.stripTags(document, nodeFilter);
        this.publishAnalyticsEvent(matched, abstractPage.getId(), new TextSearch(""));
        if (matched) {
            this.updatePage(abstractPage, document);
        }
        return matched;
    }

    @VisibleForTesting
    static boolean stripTags(Document doc, NodeFilter nodeFilter) {
        DOMImplementation domimpl = doc.getImplementation();
        if (!domimpl.hasFeature("Traversal", "2.0")) {
            log.warn("Traversal 2.0 is not available in the loaded version of DOMImplementation, implementation class: {}", (Object)domimpl.getClass().getName());
            return false;
        }
        DocumentTraversal traversal = (DocumentTraversal)((Object)doc);
        Element root = doc.getDocumentElement();
        boolean expandReferences = true;
        NodeIterator nodeIterator = traversal.createNodeIterator(root, -1, nodeFilter, true);
        Node thisNode = null;
        Node tempNode = null;
        boolean matched = false;
        while ((thisNode = nodeIterator.nextNode()) != null) {
            tempNode = thisNode;
            matched = true;
            NodeList nodeList = thisNode.getChildNodes();
            for (int i = nodeList.getLength() - 1; i > -1; --i) {
                Node node = nodeList.item(i);
                thisNode.getParentNode().insertBefore(node, tempNode);
                tempNode = node;
            }
            thisNode.getParentNode().removeChild(thisNode);
        }
        return matched;
    }

    @Override
    protected SaveContext createSaveContext(AbstractPage page) {
        return new DefaultSaveContext(true, false, true);
    }
}

