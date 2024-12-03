/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.embedded.admin.upgrade;

import com.atlassian.crowd.embedded.admin.util.SimpleMessage;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpgradeTask003_EnableLocalUserStatusForCachedDirectories
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(UpgradeTask003_EnableLocalUserStatusForCachedDirectories.class);
    private final DirectoryManager directoryManager;
    private ImmutableList.Builder<Message> errorsAndWarnings = ImmutableList.builder();

    public UpgradeTask003_EnableLocalUserStatusForCachedDirectories(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public int getBuildNumber() {
        return 3;
    }

    public String getShortDescription() {
        return "Upgrading cached LDAP directories to include the attribute localUserStatusEnabled set to true.";
    }

    public String getPluginKey() {
        return "com.atlassian.crowd.embedded.admin";
    }

    public Collection<Message> doUpgrade() throws Exception {
        for (Directory directory : this.directoryManager.findAllDirectories()) {
            if (directory.getType() != DirectoryType.CONNECTOR || !Boolean.valueOf(directory.getValue("com.atlassian.crowd.directory.sync.cache.enabled")).booleanValue() || !directory.getAllowedOperations().contains(OperationType.UPDATE_USER) || directory.getValue("localUserStatusEnabled") != null) continue;
            log.debug("Upgrading directory {}", (Object)directory);
            this.updateDirectory(directory);
        }
        return this.errorsAndWarnings.build();
    }

    private void updateDirectory(Directory directory) {
        DirectoryImpl directoryToUpdate = new DirectoryImpl(directory);
        directoryToUpdate.setAttribute("localUserStatusEnabled", Boolean.TRUE.toString());
        try {
            this.directoryManager.updateDirectory((Directory)directoryToUpdate);
        }
        catch (DirectoryNotFoundException e) {
            log.error("Could not update directory " + directory, (Throwable)e);
            this.errorsAndWarnings.add((Object)SimpleMessage.instance("embedded.crowd.update.directory.error", new Serializable[]{directory, e.getMessage()}));
        }
    }
}

