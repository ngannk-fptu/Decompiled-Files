/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.embedded.admin.upgrade;

import com.atlassian.crowd.embedded.admin.DelegatedDirectoryFinder;
import com.atlassian.crowd.embedded.admin.util.SimpleMessage;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpgradeTask004_AddPermissionForUpdateUserToDelegatedDirectories
implements PluginUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(UpgradeTask004_AddPermissionForUpdateUserToDelegatedDirectories.class);
    private final DelegatedDirectoryFinder delegatedDirectoryFinder;
    private final DirectoryManager directoryManager;
    private final ImmutableList.Builder<Message> errorsAndWarnings = ImmutableList.builder();

    public UpgradeTask004_AddPermissionForUpdateUserToDelegatedDirectories(DelegatedDirectoryFinder delegatedDirectoryFinder, DirectoryManager directoryManager) {
        this.delegatedDirectoryFinder = delegatedDirectoryFinder;
        this.directoryManager = directoryManager;
    }

    public int getBuildNumber() {
        return 4;
    }

    @Nonnull
    public String getShortDescription() {
        return "Add " + OperationType.UPDATE_USER + " to Delegating directories that have updateUserOnAuth=true.";
    }

    @Nonnull
    public String getPluginKey() {
        return "com.atlassian.crowd.embedded.admin";
    }

    @Nullable
    public Collection<Message> doUpgrade() {
        this.delegatedDirectoryFinder.findDirectories().forEach(directory -> {
            if (Boolean.parseBoolean((String)directory.getAttributes().get("crowd.delegated.directory.auto.update.user"))) {
                this.updateDirectory((Directory)directory);
            }
        });
        return this.errorsAndWarnings.build();
    }

    private void updateDirectory(Directory directory) {
        ImmutableSet allowedOperations = ImmutableSet.builder().addAll((Iterable)directory.getAllowedOperations()).add((Object)OperationType.UPDATE_USER).build();
        ImmutableDirectory updatedDirectory = ImmutableDirectory.builder((Directory)directory).setAllowedOperations((Set)allowedOperations).build();
        try {
            this.directoryManager.updateDirectory((Directory)updatedDirectory);
        }
        catch (DirectoryNotFoundException e) {
            log.error("Could not update directory {}", (Object)directory, (Object)e);
            this.errorsAndWarnings.add((Object)SimpleMessage.instance("embedded.crowd.update.directory.error", new Serializable[]{directory, e.getMessage()}));
        }
    }
}

