/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.embedded.admin.upgrade;

import com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpgradeTask002_AddExternalIdToDirectories
implements PluginUpgradeTask {
    private static final Logger logger = LoggerFactory.getLogger(UpgradeTask002_AddExternalIdToDirectories.class);
    private final DirectoryManager directoryManager;
    private final LDAPPropertiesHelper ldapPropertiesHelper;

    public UpgradeTask002_AddExternalIdToDirectories(DirectoryManager directoryManager, LDAPPropertiesHelper ldapPropertiesHelper) {
        this.directoryManager = directoryManager;
        this.ldapPropertiesHelper = ldapPropertiesHelper;
    }

    public int getBuildNumber() {
        return 2;
    }

    public String getShortDescription() {
        return "Upgrades directories configuration with User Unique Id Attribute";
    }

    public Collection<Message> doUpgrade() throws Exception {
        List allDirectories = this.directoryManager.findAllDirectories();
        for (Directory directory : allDirectories) {
            Properties properties;
            Map attributes = directory.getAttributes();
            String currentExternalIdName = (String)attributes.get("ldap.external.id");
            if (!StringUtils.isEmpty((CharSequence)currentExternalIdName) || (properties = this.getPropertiesForLdap(this.getClassNameForDirectory(directory))) == null || !properties.containsKey("ldap.external.id")) continue;
            String newExternalIdName = properties.getProperty("ldap.external.id");
            DirectoryImpl directoryToUpdate = new DirectoryImpl(directory);
            directoryToUpdate.setAttribute("ldap.external.id", newExternalIdName);
            this.directoryManager.updateDirectory((Directory)directoryToUpdate);
            logger.info(String.format("Directory %s configuration was updated by adding User Unique Id Attribute with value %s", directory.getName(), newExternalIdName));
        }
        return ImmutableList.of();
    }

    public String getPluginKey() {
        return "com.atlassian.crowd.embedded.admin";
    }

    @Nullable
    private String getClassNameForDirectory(Directory directory) {
        if (DirectoryType.DELEGATING.equals((Object)directory.getType())) {
            return directory.getValue("crowd.delegated.directory.type");
        }
        if (DirectoryType.CONNECTOR.equals((Object)directory.getType())) {
            return directory.getImplementationClass();
        }
        return null;
    }

    @Nullable
    private Properties getPropertiesForLdap(String ldapClass) {
        return ldapClass != null ? (Properties)this.ldapPropertiesHelper.getConfigurationDetails().get(ldapClass) : null;
    }
}

