/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.HeartbeatManager
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 *  org.springframework.util.CollectionUtils
 */
package com.atlassian.confluence.plugins.highlight.service;

import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.HeartbeatManager;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import com.atlassian.confluence.plugins.highlight.service.DefaultSelectionModifier;
import com.atlassian.confluence.plugins.highlight.service.MarkModificationValidator;
import com.atlassian.confluence.plugins.highlight.xml.MarkSelectionTransformer;
import com.atlassian.confluence.plugins.highlight.xml.MarkStateTracker;
import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTracker;
import com.atlassian.confluence.plugins.highlight.xml.TextCollector;
import com.atlassian.confluence.plugins.highlight.xml.TextMatcher;
import com.atlassian.confluence.plugins.highlight.xml.XMLParserHelper;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class MarkSelectionModifier
extends DefaultSelectionModifier<XMLModification> {
    private final HeartbeatManager heartbeatManager;

    @Autowired
    protected MarkSelectionModifier(XMLParserHelper xmlParserHelper, TextCollector textCollector, TextMatcher textMatcher, MarkSelectionTransformer transformer, @Qualifier(value="markModificationValidator") MarkModificationValidator markModificationValidator, @ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport DarkFeaturesManager darkFeaturesManager, @ComponentImport HeartbeatManager heartbeatManager) {
        super(xmlParserHelper, textCollector, textMatcher, transformer, markModificationValidator, pageManager, eventPublisher, darkFeaturesManager);
        this.heartbeatManager = heartbeatManager;
    }

    @Override
    ModificationStateTracker createModificationStateTracker() {
        return new MarkStateTracker();
    }

    @Override
    protected SaveContext createSaveContext(AbstractPage page) {
        List userActivities = this.heartbeatManager.getUsersForActivity(page.getId() + page.getType());
        if (CollectionUtils.isEmpty((Collection)userActivities)) {
            return new DefaultSaveContext(true, false, true);
        }
        return new DefaultSaveContext(true, true, true);
    }

    @Override
    protected String getModifier() {
        return "mark";
    }
}

