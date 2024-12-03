/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.upgrade;

import com.atlassian.confluence.plugins.createcontent.ContentBlueprintCleaner;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PluginUpgradeTask.class})
public class ContentBlueprintsCleanUpUpgradeTask
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(ContentBlueprintsCleanUpUpgradeTask.class);
    private final ContentBlueprintCleaner contentBlueprintCleaner;

    @Autowired
    public ContentBlueprintsCleanUpUpgradeTask(ContentBlueprintCleaner contentBlueprintCleaner) {
        this.contentBlueprintCleaner = contentBlueprintCleaner;
    }

    public int getBuildNumber() {
        return 5;
    }

    public String getShortDescription() {
        return "Clean up space-level content blueprints for removed spaces.";
    }

    public Collection<Message> doUpgrade() {
        log.info("Deleting old content blueprints for removed spaces");
        this.contentBlueprintCleaner.cleanUp();
        return null;
    }

    public String getPluginKey() {
        return "com.atlassian.confluence.plugins.confluence-create-content-plugin";
    }
}

