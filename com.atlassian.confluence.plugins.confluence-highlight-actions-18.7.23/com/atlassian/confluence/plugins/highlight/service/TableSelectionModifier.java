/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.highlight.service;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.highlight.model.TableModification;
import com.atlassian.confluence.plugins.highlight.service.DefaultSelectionModifier;
import com.atlassian.confluence.plugins.highlight.service.TableModificationValidator;
import com.atlassian.confluence.plugins.highlight.xml.AppendStateTracker;
import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTracker;
import com.atlassian.confluence.plugins.highlight.xml.TableSelectionTransformer;
import com.atlassian.confluence.plugins.highlight.xml.TextCollector;
import com.atlassian.confluence.plugins.highlight.xml.TextMatcher;
import com.atlassian.confluence.plugins.highlight.xml.XMLParserHelper;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TableSelectionModifier
extends DefaultSelectionModifier<TableModification> {
    @Autowired
    protected TableSelectionModifier(XMLParserHelper xmlParserHelper, TextCollector textCollector, TextMatcher textMatcher, TableSelectionTransformer transformer, @Qualifier(value="tableModificationValidator") TableModificationValidator tableModificationValidator, @ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher, @ComponentImport DarkFeaturesManager darkFeaturesManager) {
        super(xmlParserHelper, textCollector, textMatcher, transformer, tableModificationValidator, pageManager, eventPublisher, darkFeaturesManager);
    }

    @Override
    protected String getModifier() {
        return "table";
    }

    @Override
    ModificationStateTracker createModificationStateTracker() {
        return new AppendStateTracker();
    }
}

