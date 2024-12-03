/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.Message
 */
package com.atlassian.audit.plugin.upgrade.task;

import com.atlassian.audit.plugin.upgrade.AuditUpgradeTask;
import com.atlassian.sal.api.message.Message;
import java.util.Collection;
import java.util.Collections;

public class UpgradeTask3Noop
extends AuditUpgradeTask {
    private static final int BUILD_NUMBER = 3;

    public int getBuildNumber() {
        return 3;
    }

    public String getShortDescription() {
        return "Noop upgrade task 3. Bumps build number only.";
    }

    public Collection<Message> doUpgrade() {
        return Collections.emptyList();
    }
}

