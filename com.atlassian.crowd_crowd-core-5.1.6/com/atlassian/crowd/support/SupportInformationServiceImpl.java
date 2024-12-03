/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.application.ApplicationManager
 *  com.atlassian.crowd.manager.application.DefaultGroupMembershipService
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.atlassian.crowd.model.application.RemoteAddress
 *  com.atlassian.crowd.service.support.AdditionalSupportInformationService
 *  com.atlassian.crowd.support.SupportInformationBuilder
 *  com.atlassian.crowd.support.SupportInformationService
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.support;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.application.ApplicationManager;
import com.atlassian.crowd.manager.application.DefaultGroupMembershipService;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.application.RemoteAddress;
import com.atlassian.crowd.service.support.AdditionalSupportInformationService;
import com.atlassian.crowd.support.SupportInformationBuilder;
import com.atlassian.crowd.support.SupportInformationService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupportInformationServiceImpl
implements SupportInformationService {
    private final DirectoryManager directoryManager;
    private final DefaultGroupMembershipService defaultGroupMembershipService;
    @Nullable
    private ApplicationManager applicationManager;
    private List<AdditionalSupportInformationService> additionalSupportInformationServices = Collections.emptyList();
    private static final String ATTRIBUTES_KEY = "Attributes";
    private static final Logger log = LoggerFactory.getLogger(SupportInformationServiceImpl.class);

    public SupportInformationServiceImpl(DirectoryManager directoryManager, DefaultGroupMembershipService defaultGroupMembershipService) {
        this.directoryManager = (DirectoryManager)Preconditions.checkNotNull((Object)directoryManager);
        this.defaultGroupMembershipService = defaultGroupMembershipService;
    }

    public String getSupportInformation(@Nullable User currentUser) {
        Map<String, String> map = this.getSupportInformationMap(currentUser);
        StringBuilder builder = new StringBuilder(1000);
        List<Object> previousCategories = Collections.emptyList();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            List<String> parts = Arrays.asList(entry.getKey().split("\\."));
            int splitIndex = parts.indexOf(ATTRIBUTES_KEY) + 1;
            if (splitIndex > 0 && splitIndex < parts.size() - 1) {
                parts = Stream.concat(parts.subList(0, splitIndex).stream(), Stream.of(String.join((CharSequence)".", parts.subList(splitIndex, parts.size())))).collect(Collectors.toList());
            }
            List<String> categories = parts.subList(0, parts.size() - 1);
            String attribute = parts.get(parts.size() - 1);
            boolean parentWasDifferent = false;
            for (int i = 0; i < categories.size(); ++i) {
                if (!parentWasDifferent && i < previousCategories.size() && categories.get(i).equals(previousCategories.get(i))) continue;
                builder.append(StringUtils.repeat((String)"\t", (int)i)).append(categories.get(i)).append(":\n");
                parentWasDifferent = true;
            }
            builder.append(StringUtils.repeat((String)"\t", (int)categories.size())).append(attribute).append(": ").append(entry.getValue()).append("\n");
            previousCategories = categories;
        }
        return builder.toString();
    }

    public Map<String, String> getSupportInformationMap(@Nullable User currentUser) {
        SupportInformationBuilder builder = new SupportInformationBuilder();
        this.addCurrentUserInformation(builder, currentUser);
        this.addDirectoryConfiguration(builder);
        this.addApplicationConfiguration(builder);
        this.addAdditionalSupportInformation(builder);
        return builder.getMap();
    }

    private void addAdditionalSupportInformation(SupportInformationBuilder builder) {
        for (AdditionalSupportInformationService additionalSupportInformationService : this.additionalSupportInformationServices) {
            additionalSupportInformationService.extendSupportInformation(builder);
        }
    }

    private void addCurrentUserInformation(SupportInformationBuilder builder, @Nullable User currentUser) {
        if (currentUser != null) {
            builder.prefix("Current user").field("Directory ID", (Object)currentUser.getDirectoryId()).field("Username", (Object)currentUser.getName()).field("Display name", (Object)currentUser.getDisplayName()).field("Email address", (Object)currentUser.getEmailAddress());
        }
    }

    private void addDirectoryConfiguration(SupportInformationBuilder builder) {
        List directories = this.directoryManager.findAllDirectories();
        if (directories != null) {
            int index = 1;
            for (Directory directory : directories) {
                builder.prefix("Directory " + index).field("Directory ID", (Object)directory.getId()).field("Name", (Object)directory.getName()).field("Active", (Object)directory.isActive()).field("Type", (Object)directory.getType()).field("Created date", (Object)directory.getCreatedDate()).field("Updated date", (Object)directory.getUpdatedDate()).field("Allowed operations", (Object)directory.getAllowedOperations()).field("Implementation class", (Object)directory.getImplementationClass()).field("Encryption type", (Object)directory.getEncryptionType()).attributes(ATTRIBUTES_KEY, directory.getAttributes());
                ++index;
            }
        }
    }

    private void addApplicationConfiguration(SupportInformationBuilder builder) {
        if (this.applicationManager != null) {
            AtomicInteger applicationIndex = new AtomicInteger(1);
            for (Application application : this.applicationManager.findAll()) {
                builder.prefix("Application " + applicationIndex.get()).field("Application ID", (Object)application.getId()).field("Name", (Object)application.getName()).field("Active", (Object)application.isActive()).field("Type", (Object)application.getType()).field("Description", (Object)application.getDescription()).field("Is lowercase output", (Object)application.isLowerCaseOutput()).field("Is aliasing enabled", (Object)application.isAliasingEnabled()).field("Remote addresses", (Object)Iterables.transform((Iterable)application.getRemoteAddresses(), RemoteAddress::getAddress)).field("Created date", (Object)application.getCreatedDate()).field("Updated date", (Object)application.getUpdatedDate()).attributes(ATTRIBUTES_KEY, application.getAttributes());
                int directoryIndex = 1;
                for (ApplicationDirectoryMapping directoryMapping : application.getApplicationDirectoryMappings()) {
                    builder.prefix("Application " + applicationIndex.get() + ".Mapping " + directoryIndex).field("Mapped to directory ID", (Object)directoryMapping.getDirectory().getId()).field("Allow all to authenticate", (Object)directoryMapping.isAllowAllToAuthenticate()).field("Mapped groups", (Object)directoryMapping.getAuthorisedGroupNames()).field("Allowed operations", (Object)directoryMapping.getAllowedOperations());
                    List<String> defaultGroupMemberships = this.fetchDefaultGroupMemberships(application, directoryMapping);
                    if (!defaultGroupMemberships.isEmpty()) {
                        builder.field("Default group memberships", defaultGroupMemberships);
                    }
                    ++directoryIndex;
                }
                this.additionalSupportInformationServices.forEach(s -> s.extendSupportInformation(builder, application, applicationIndex.get()));
                applicationIndex.incrementAndGet();
            }
        }
    }

    private List<String> fetchDefaultGroupMemberships(Application application, ApplicationDirectoryMapping directoryMapping) {
        try {
            return this.defaultGroupMembershipService.listAll(application, directoryMapping);
        }
        catch (OperationFailedException e) {
            log.debug("Could not fetch default group memberships for application {} and directory {}", new Object[]{application.getId(), directoryMapping.getDirectory().getId(), e});
            return Collections.emptyList();
        }
    }

    public void setApplicationManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }

    public void setAdditionalSupportInformationServices(@Nullable List<AdditionalSupportInformationService> additionalSupportInformationServices) {
        this.additionalSupportInformationServices = additionalSupportInformationServices;
    }
}

