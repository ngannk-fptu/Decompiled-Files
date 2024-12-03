/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.google.common.collect.ImmutableSortedSet
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrowdDirectorySupportData
extends RootLevelSupportDataAppender {
    protected static final String STP_BASE = "stp.properties.crowd.embedded.directories";
    protected static final String STP_DIRECTORY_BASE = "stp.properties.crowd.embedded.directories.directory";
    private static final Logger LOG = LoggerFactory.getLogger(CrowdDirectorySupportData.class);
    private final CrowdDirectoryService crowdDirectoryService;

    public CrowdDirectorySupportData(CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = crowdDirectoryService;
    }

    @Override
    protected void addSupportData(SupportDataBuilder supportInfoBuilder) {
        List directories = this.crowdDirectoryService.findAllDirectories();
        if (directories != null) {
            SupportDataBuilder directoriesAppender = supportInfoBuilder.addCategory(STP_BASE);
            try {
                directoriesAppender.addValue("stp.properties.crowd.embedded.directories.membership.aggregation", Boolean.toString(this.crowdDirectoryService.isMembershipAggregationEnabled()));
            }
            catch (NoSuchMethodError e) {
                LOG.warn("Failed to invoke isMembershipAggregationEnabled()", (Throwable)e);
                directoriesAppender.addValue("stp.properties.crowd.embedded.directories.membership.aggregation", "Unknown");
            }
            for (Directory directory : directories) {
                SupportDataBuilder directoryAppender = directoriesAppender.addCategory(STP_DIRECTORY_BASE);
                directoryAppender.addValue("stp.properties.crowd.embedded.directories.directory.id", Long.toString(directory.getId()));
                directoryAppender.addValue("stp.properties.crowd.embedded.directories.directory.name", directory.getName());
                directoryAppender.addValue("stp.properties.crowd.embedded.directories.directory.active", Boolean.toString(directory.isActive()));
                directoryAppender.addValue("stp.properties.crowd.embedded.directories.directory.type", this.toString(directory.getType()));
                directoryAppender.addValue("stp.properties.crowd.embedded.directories.directory.created", this.toString(directory.getCreatedDate()));
                directoryAppender.addValue("stp.properties.crowd.embedded.directories.directory.updated", this.toString(directory.getUpdatedDate()));
                directoryAppender.addValue("stp.properties.crowd.embedded.directories.directory.operations", this.toString(directory.getAllowedOperations()));
                directoryAppender.addValue("stp.properties.crowd.embedded.directories.directory.class", directory.getImplementationClass());
                directoryAppender.addValue("stp.properties.crowd.embedded.directories.directory.encryption", directory.getEncryptionType());
                HashMap mutableAttributes = Maps.newHashMap((Map)directory.getAttributes());
                for (String key : directory.getAttributes().keySet()) {
                    if (!key.contains("pwd") && !key.contains("password") && !key.contains("credential")) continue;
                    mutableAttributes.put(key, "(not shown)");
                }
                directoryAppender.addValue("stp.properties.crowd.embedded.directories.directory.attributes", ((Object)mutableAttributes).toString());
                directoryAppender.addContext(directory);
            }
        }
    }

    private String toString(DirectoryType directoryType) {
        if (directoryType == null) {
            return null;
        }
        return directoryType.name();
    }

    private String toString(Set<OperationType> operations) {
        if (operations == null) {
            return null;
        }
        return StringUtils.join((Iterable)ImmutableSortedSet.copyOf(operations), (char)',');
    }

    private String toString(Date date) {
        if (date == null) {
            return null;
        }
        return date.toString();
    }
}

