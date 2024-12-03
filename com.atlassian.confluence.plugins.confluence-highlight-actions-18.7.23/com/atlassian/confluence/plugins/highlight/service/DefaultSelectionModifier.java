/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.highlight.service;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.highlight.SelectionModificationException;
import com.atlassian.confluence.plugins.highlight.events.MarkSelectionDetailEvent;
import com.atlassian.confluence.plugins.highlight.model.TextCollection;
import com.atlassian.confluence.plugins.highlight.model.TextMatch;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import com.atlassian.confluence.plugins.highlight.service.AbstractSelectionModifier;
import com.atlassian.confluence.plugins.highlight.service.SelectionValidator;
import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTracker;
import com.atlassian.confluence.plugins.highlight.xml.SelectionTransformer;
import com.atlassian.confluence.plugins.highlight.xml.TextCollector;
import com.atlassian.confluence.plugins.highlight.xml.TextMatcher;
import com.atlassian.confluence.plugins.highlight.xml.XMLParserHelper;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public abstract class DefaultSelectionModifier<T>
extends AbstractSelectionModifier {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSelectionModifier.class);
    private final SelectionValidator<T> selectionValidator;
    private final TextCollector textCollector;
    private final TextMatcher textMatcher;
    private final SelectionTransformer<T> selectionTransformer;

    protected DefaultSelectionModifier(XMLParserHelper xmlParserHelper, TextCollector textCollector, TextMatcher textMatcher, SelectionTransformer<T> selectionTransformer, SelectionValidator<T> selectionValidator, @ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport DarkFeaturesManager darkFeaturesManager) {
        super(xmlParserHelper, pageManager, eventPublisher, darkFeaturesManager);
        this.textCollector = textCollector;
        this.textMatcher = textMatcher;
        this.selectionTransformer = selectionTransformer;
        this.selectionValidator = selectionValidator;
    }

    public final boolean modify(long pageId, long lastFetchTime, TextSearch selection, T modification) throws SAXException, SelectionModificationException {
        AbstractPage abstractPage = this.pageManager.getAbstractPage(pageId);
        this.selectionValidator.validate(pageId, abstractPage, lastFetchTime, selection, modification);
        Document document = this.xmlParserHelper.parseDocument(abstractPage.getBodyAsString());
        boolean matched = this.modify(document, selection, modification);
        this.publishAnalyticsEvent(matched, abstractPage.getId(), selection);
        if (matched) {
            this.updatePage(abstractPage, document);
        }
        return matched;
    }

    @VisibleForTesting
    boolean modify(Document document, TextSearch textSearch, T modification) throws SAXException {
        TextCollection textCollection = this.textCollector.collect(document, this.createModificationStateTracker());
        TextMatch textMatch = this.textMatcher.match(textSearch, textCollection);
        try {
            MarkSelectionDetailEvent markSelectionDetailEvent = new MarkSelectionDetailEvent(this, textSearch, textCollection, textMatch);
            this.eventPublisher.publish((Object)markSelectionDetailEvent);
        }
        catch (Exception e) {
            logger.error("Could not publish MarkSelectionDetailEvent event", (Throwable)e);
        }
        if (textMatch == null) {
            logger.error("Could not locate highlight text in storage format.");
            return false;
        }
        boolean result = this.selectionTransformer.transform(document, textMatch, modification);
        if (!result) {
            logger.warn("Could not modify storage format to decorate with a marker");
        }
        return result;
    }

    abstract ModificationStateTracker createModificationStateTracker();
}

