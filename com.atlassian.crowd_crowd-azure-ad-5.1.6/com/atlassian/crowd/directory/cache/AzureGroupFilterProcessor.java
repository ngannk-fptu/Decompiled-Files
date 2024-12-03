/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.map.type.CollectionType
 *  org.codehaus.jackson.type.JavaType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.cache;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.config.AzureGroupFiltersConfiguration;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzureGroupFilterProcessor {
    private static final Logger log = LoggerFactory.getLogger(AzureGroupFilterProcessor.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final DirectoryManager directoryManager;

    public AzureGroupFilterProcessor(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public static Set<String> getGroupNames(String attributeValue) {
        if (StringUtils.isBlank((CharSequence)attributeValue)) {
            return Collections.emptySet();
        }
        CollectionType setOfStrings = OBJECT_MAPPER.getTypeFactory().constructCollectionType(Set.class, String.class);
        try {
            return ImmutableSet.copyOf((Collection)((Set)OBJECT_MAPPER.readValue(attributeValue, (JavaType)setOfStrings)));
        }
        catch (Exception e) {
            log.error("Unable to parse attribute JSON value containing list of groups to be filtered for Azure AD: {}", (Object)attributeValue);
            throw new RuntimeException("Invalid value for list of groups to be filtered");
        }
    }

    public static String prepareAttribute(Set<String> groupNames) {
        try {
            return OBJECT_MAPPER.writeValueAsString(groupNames);
        }
        catch (IOException e) {
            log.error("Unable to serialize list of group external Ids to filter", (Throwable)e);
            throw new RuntimeException("Invalid value for list of groups to be filtered");
        }
    }

    public AzureGroupFiltersConfiguration getConfiguration(long directoryId) throws DirectoryNotFoundException {
        Directory directory = this.loadAzureDirectoryOrElseThrow(directoryId);
        Set<String> groupsToFilter = AzureGroupFilterProcessor.getGroupNames(directory.getValue("AZURE_AD_FILTERED_GROUPS"));
        boolean enabled = Boolean.parseBoolean(directory.getAttributes().getOrDefault("GROUP_FILTERING_ENABLED", Boolean.FALSE.toString()));
        return new AzureGroupFiltersConfiguration(enabled, groupsToFilter);
    }

    public void configureGroupFilter(long directoryId, AzureGroupFiltersConfiguration configuration) throws DirectoryNotFoundException {
        Directory directory = this.loadAzureDirectoryOrElseThrow(directoryId);
        this.directoryManager.updateDirectory((Directory)ImmutableDirectory.builder((Directory)directory).setAttribute("GROUP_FILTERING_ENABLED", Boolean.toString(configuration.isEnabled())).setAttribute("AZURE_AD_FILTERED_GROUPS", AzureGroupFilterProcessor.prepareAttribute(configuration.getGroupsNames())).build());
    }

    private Directory loadAzureDirectoryOrElseThrow(long directoryId) throws DirectoryNotFoundException {
        Directory directory = this.directoryManager.findDirectoryById(directoryId);
        if (directory.getType() != DirectoryType.AZURE_AD) {
            throw new IllegalArgumentException("Cannot configure filterable groups for non-azure active directory");
        }
        return directory;
    }
}

