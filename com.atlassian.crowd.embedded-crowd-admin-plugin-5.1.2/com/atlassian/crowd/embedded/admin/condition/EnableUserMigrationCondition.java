/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.crowd.embedded.admin.condition;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class EnableUserMigrationCondition
implements Condition {
    private final CrowdDirectoryService crowdDirectoryService;

    public EnableUserMigrationCondition(CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = crowdDirectoryService;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        int count = 0;
        for (Directory directory : this.crowdDirectoryService.findAllDirectories()) {
            if (directory.getType() != DirectoryType.INTERNAL && directory.getType() != DirectoryType.DELEGATING) continue;
            ++count;
        }
        return count > 1;
    }
}

