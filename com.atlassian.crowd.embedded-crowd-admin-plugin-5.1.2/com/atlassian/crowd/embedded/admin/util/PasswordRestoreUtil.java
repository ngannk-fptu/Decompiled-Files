/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory$Builder
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.embedded.admin.util;

import com.atlassian.crowd.embedded.admin.DirectoryContextHelper;
import com.atlassian.crowd.embedded.admin.util.ConfigurationWithPassword;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.impl.ImmutableDirectory;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class PasswordRestoreUtil {
    private final DirectoryContextHelper directoryContextHelper;

    public PasswordRestoreUtil(DirectoryContextHelper directoryContextHelper) {
        this.directoryContextHelper = directoryContextHelper;
    }

    public Directory restoreOldPasswordIfNewIsEmpty(ConfigurationWithPassword configuration, Directory newDirectory) {
        String passwordAttributeKey = configuration.getPasswordAttributeKey();
        String newPassword = (String)newDirectory.getAttributes().get(passwordAttributeKey);
        if (StringUtils.isEmpty((CharSequence)newPassword)) {
            return this.restoreOldPassword(newDirectory, configuration);
        }
        return newDirectory;
    }

    private Directory restoreOldPassword(Directory newDirectory, ConfigurationWithPassword configuration) {
        long directoryId = configuration.getDirectoryId();
        try {
            Directory oldDirectory = this.directoryContextHelper.getDirectory(directoryId);
            return this.restoreOldPasswordFromExistingDirectory(newDirectory, configuration, oldDirectory);
        }
        catch (DirectoryNotFoundException e) {
            return newDirectory;
        }
    }

    private Directory restoreOldPasswordFromExistingDirectory(Directory newDirectory, ConfigurationWithPassword configuration, Directory oldDirectory) {
        String passwordAttributeKey = configuration.getPasswordAttributeKey();
        String oldPassword = (String)oldDirectory.getAttributes().get(passwordAttributeKey);
        if (StringUtils.isNotEmpty((CharSequence)oldPassword)) {
            return this.restoreOldPassword(newDirectory, passwordAttributeKey, oldPassword);
        }
        return newDirectory;
    }

    private Directory restoreOldPassword(Directory newDirectory, String passwordAttributeKey, String oldPassword) {
        ImmutableDirectory.Builder builder = ImmutableDirectory.newBuilder((Directory)newDirectory);
        HashMap<String, String> updatedAttributes = new HashMap<String, String>(newDirectory.getAttributes());
        updatedAttributes.put(passwordAttributeKey, oldPassword);
        builder.setAttributes(updatedAttributes);
        return builder.toDirectory();
    }
}

