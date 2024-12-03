/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.Modification
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.plugins.highlight.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.Modification;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.highlight.events.InternalNonPrivacyPolicySafeMatchEvent;
import com.atlassian.confluence.plugins.highlight.events.MatchEvent;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import com.atlassian.confluence.plugins.highlight.xml.XMLParserHelper;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.w3c.dom.Document;

public abstract class AbstractSelectionModifier {
    protected final XMLParserHelper xmlParserHelper;
    protected final PageManager pageManager;
    protected final EventPublisher eventPublisher;
    protected final DarkFeaturesManager darkFeaturesManager;

    protected AbstractSelectionModifier(XMLParserHelper xmlParserHelper, @ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport DarkFeaturesManager darkFeaturesManager) {
        this.xmlParserHelper = xmlParserHelper;
        this.eventPublisher = eventPublisher;
        this.pageManager = pageManager;
        this.darkFeaturesManager = darkFeaturesManager;
    }

    protected void updatePage(AbstractPage page, Document document) {
        final String modifiedXml = XMLParserHelper.documentToString(document);
        Modification<AbstractPage> updateContent = new Modification<AbstractPage>(){

            public void modify(AbstractPage content) {
                content.setBodyAsString(modifiedXml);
            }
        };
        this.pageManager.saveNewVersion((ContentEntityObject)page, (Modification)updateContent, this.createSaveContext(page));
    }

    protected void publishAnalyticsEvent(boolean matched, long id, TextSearch selection) {
        if (this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled("confluence-highlight-internal-non-privacy-policy-safe-events")) {
            this.eventPublisher.publish((Object)new InternalNonPrivacyPolicySafeMatchEvent(this, this.getModifier(), matched, selection.getText(), id));
        } else {
            this.eventPublisher.publish((Object)new MatchEvent(this, this.getModifier(), matched, id));
        }
    }

    protected SaveContext createSaveContext(AbstractPage page) {
        return DefaultSaveContext.DEFAULT;
    }

    protected abstract String getModifier();
}

