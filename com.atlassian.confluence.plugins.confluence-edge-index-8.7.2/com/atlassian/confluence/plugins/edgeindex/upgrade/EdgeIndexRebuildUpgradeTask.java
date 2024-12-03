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
package com.atlassian.confluence.plugins.edgeindex.upgrade;

import com.atlassian.confluence.plugins.edgeindex.EdgeIndexBuilder;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={PluginUpgradeTask.class})
@Component
public class EdgeIndexRebuildUpgradeTask
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(EdgeIndexRebuildUpgradeTask.class);
    private final EdgeIndexBuilder edgeIndexBuilder;

    @Autowired
    public EdgeIndexRebuildUpgradeTask(EdgeIndexBuilder edgeIndexBuilder) {
        this.edgeIndexBuilder = edgeIndexBuilder;
    }

    public int getBuildNumber() {
        return 1;
    }

    public String getShortDescription() {
        return "Rebuild Edge Index";
    }

    public Collection<Message> doUpgrade() {
        try {
            log.info("Rebuilding Edge Index");
            this.edgeIndexBuilder.rebuild(EdgeIndexBuilder.EDGE_INDEX_REBUILD_DEFAULT_START_PERIOD, EdgeIndexBuilder.RebuildCondition.ONLY_IF_INDEX_PRESENT);
            log.info("Edge Index rebuild complete");
            return Collections.emptySet();
        }
        catch (Exception ex) {
            log.error("Edge index rebuild failed", (Throwable)ex);
            return Collections.singleton(EdgeIndexRebuildUpgradeTask.toMessage(ex));
        }
    }

    private static Message toMessage(final Exception ex) {
        return new Message(){

            public String getKey() {
                return "edge.index.rebuild.upgrade.task.failed";
            }

            public Serializable[] getArguments() {
                return new Serializable[]{ex.getLocalizedMessage()};
            }
        };
    }

    public String getPluginKey() {
        return "com.atlassian.confluence.plugins.confluence-edge-index";
    }
}

