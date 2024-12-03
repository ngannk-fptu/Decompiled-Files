/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.SynchronisableDirectoryProperties$SyncGroupMembershipsAfterAuth
 *  com.atlassian.crowd.directory.ldap.LdapPoolType
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory$Builder
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.CrowdException
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.DirectoryPermissionException
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.ui.ModelMap
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.ObjectError
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.ModelAndView
 *  org.springframework.web.util.WebUtils
 */
package com.atlassian.crowd.embedded.admin;

import com.atlassian.crowd.directory.SynchronisableDirectoryProperties;
import com.atlassian.crowd.directory.ldap.LdapPoolType;
import com.atlassian.crowd.embedded.admin.DirectoryContextHelper;
import com.atlassian.crowd.embedded.admin.DirectoryMapper;
import com.atlassian.crowd.embedded.admin.plugin.DefaultGroupsModuleDescriptor;
import com.atlassian.crowd.embedded.admin.util.ConfigurationWithPassword;
import com.atlassian.crowd.embedded.admin.util.HtmlEncoder;
import com.atlassian.crowd.embedded.admin.util.PasswordRestoreUtil;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.ImmutableDirectory;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.CrowdException;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.DirectoryTermKeys;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

public abstract class ConfigurationController<C> {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationController.class);
    protected static final String CONFIGURATION = "configuration";
    @Autowired
    private CrowdDirectoryService crowdDirectoryService;
    @Autowired
    protected DirectoryMapper directoryMapper;
    @Autowired
    protected DirectoryContextHelper directoryContextHelper;
    @Autowired
    private I18nResolver i18nResolver;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    protected HtmlEncoder htmlEncoder;
    @Autowired
    private DirectoryManager directoryManager;
    @Autowired
    private PluginAccessor pluginAccessor;
    @Autowired
    private PasswordRestoreUtil passwordRestoreUtil;
    @Autowired
    private DarkFeatureManager darkFeatureManager;
    @Autowired
    private ApplicationProperties applicationProperties;

    @RequestMapping(method={RequestMethod.GET})
    public final String initializeForm(HttpServletRequest request, ModelMap model) throws Exception {
        model.addAttribute(CONFIGURATION, this.createConfigurationFromRequest(request));
        return this.getFormView();
    }

    protected final ModelAndView handleSubmit(HttpServletRequest request, C formObject, BindingResult errors) {
        Directory directory = this.createDirectoryFromConfiguration(formObject);
        HashMap<String, Boolean> model = new HashMap<String, Boolean>();
        if (!errors.hasErrors()) {
            boolean testPassed;
            block10: {
                testPassed = true;
                try {
                    if (formObject instanceof ConfigurationWithPassword && !StringUtils.isEmpty((CharSequence)((ConfigurationWithPassword)formObject).getPassword())) {
                        model.put("keepPasswordPlainText", true);
                    }
                    this.crowdDirectoryService.testConnection(directory);
                    log.info("Configuration test successful for user directory: [ {} ], type: [ {} ]", (Object)directory.getName(), (Object)directory.getType());
                }
                catch (OperationFailedException e) {
                    testPassed = false;
                    log.error("Configuration test failed for user directory: [ " + directory.getName() + "], type: [ " + directory.getType() + " ]", (Throwable)e);
                    String rawMessage = e.getMessage();
                    String error = this.htmlEncoder.encode(rawMessage);
                    this.addObjectError(errors, "embedded.crowd.connection.test.failed", new Serializable[]{error});
                    if (rawMessage == null || !rawMessage.toLowerCase().contains("error code")) break block10;
                    model.put("addErrorCodeLink", true);
                }
            }
            if (testPassed && WebUtils.hasSubmitParameter((ServletRequest)request, (String)"save")) {
                try {
                    Directory savedDirectory = this.saveDirectory(directory);
                    String successView = StringUtils.replace((String)this.getSuccessView(), (String)"{directoryId}", (String)String.valueOf(savedDirectory.getId()));
                    return new ModelAndView(successView, errors.getModel());
                }
                catch (DirectoryInstantiationException e) {
                    String error = this.htmlEncoder.encode(e.getMessage());
                    this.addObjectError(errors, "embedded.crowd.save.directory.failed", new Serializable[]{error});
                }
            } else if (WebUtils.hasSubmitParameter((ServletRequest)request, (String)"test")) {
                model.put("testSuccessful", testPassed);
            } else {
                this.addObjectError(errors, "embedded.crowd.validation.submission.mode.missing", new Serializable[0]);
            }
        }
        model.put(CONFIGURATION, (Boolean)formObject);
        model.put("userPreviewIsEnabled", this.darkFeatureManager.isEnabledForCurrentUser("crowd.admin.user.preview.enable").orElse(false));
        return new ModelAndView(this.getFormView(), model);
    }

    private Directory createDirectoryFromConfiguration(C configuration) {
        Directory newDirectory = this.createDirectory(configuration);
        if (configuration instanceof ConfigurationWithPassword) {
            ConfigurationWithPassword configurationWithPassword = (ConfigurationWithPassword)configuration;
            return this.passwordRestoreUtil.restoreOldPasswordIfNewIsEmpty(configurationWithPassword, newDirectory);
        }
        return newDirectory;
    }

    protected abstract C createConfigurationFromRequest(HttpServletRequest var1) throws Exception;

    protected abstract Directory createDirectory(C var1);

    protected abstract String getFormView();

    protected abstract String getSuccessView();

    protected Directory createUpdatedDirectory(Directory oldDirectory, Directory newDirectory) {
        ImmutableDirectory.Builder builder = ImmutableDirectory.newBuilder((Directory)newDirectory);
        builder.setCreatedDate(oldDirectory.getCreatedDate());
        HashMap updatedAttributes = new HashMap(oldDirectory.getAttributes());
        updatedAttributes.putAll(newDirectory.getAttributes());
        builder.setAttributes(updatedAttributes);
        return builder.toDirectory();
    }

    private boolean directoryNameInUse(String directoryName) {
        EntityQuery directoryQuery = QueryBuilder.queryFor(Directory.class, (EntityDescriptor)EntityDescriptor.directory()).with((SearchRestriction)Restriction.on((Property)DirectoryTermKeys.NAME).exactlyMatching((Object)directoryName)).returningAtMost(-1);
        return !this.directoryManager.searchDirectories(directoryQuery).isEmpty();
    }

    private Directory saveDirectory(Directory directory) throws DirectoryInstantiationException {
        if (directory.getId() <= 0L) {
            if (StringUtils.isEmpty((CharSequence)directory.getName())) {
                throw new DirectoryInstantiationException(this.i18nResolver.getText("embedded.crowd.validation.directory.name.required", new Serializable[]{directory.getName()}));
            }
            if (this.directoryNameInUse(directory.getName())) {
                throw new DirectoryInstantiationException(this.i18nResolver.getText("embedded.crowd.validation.directory.name.conflict", new Serializable[]{directory.getName()}));
            }
            return (Directory)this.transactionTemplate.execute(() -> {
                log.info("User directory created: [ {} ], type: [ {} ]", (Object)directory.getName(), (Object)directory.getType());
                Directory newDirectory = this.crowdDirectoryService.addDirectory(directory);
                this.postprocessDirectory(newDirectory);
                return newDirectory;
            });
        }
        return (Directory)this.transactionTemplate.execute(() -> {
            log.info("User directory updated: [ {} ], type: [ {} ]", (Object)directory.getName(), (Object)directory.getType());
            Directory oldDirectory = this.crowdDirectoryService.findDirectoryById(directory.getId().longValue());
            Directory updatedDirectory = this.createUpdatedDirectory(oldDirectory, directory);
            Directory newDirectory = this.crowdDirectoryService.updateDirectory(updatedDirectory);
            this.postprocessDirectory(newDirectory);
            return newDirectory;
        });
    }

    protected String getDefaultLdapAutoAddGroups() {
        StringBuilder defaultGroups = new StringBuilder();
        for (DefaultGroupsModuleDescriptor defaultGroupDescriptor : this.pluginAccessor.getEnabledModuleDescriptorsByClass(DefaultGroupsModuleDescriptor.class)) {
            Iterator iterator = defaultGroupDescriptor.getModule().iterator();
            while (iterator.hasNext()) {
                String groupName = (String)iterator.next();
                if (defaultGroups.length() > 0) {
                    defaultGroups.append(", ");
                }
                defaultGroups.append(groupName);
            }
        }
        if (defaultGroups.length() == 0) {
            log.warn("No default auto add group is defined.");
        }
        return defaultGroups.toString();
    }

    protected void postprocessDirectory(Directory directory) {
        String groups = (String)directory.getAttributes().get("autoAddGroups");
        if (StringUtils.isNotBlank((CharSequence)groups)) {
            for (String groupName : StringUtils.split((String)groups, (String)"|")) {
                try {
                    this.ensureGroupExistsInDirectory(directory.getId(), groupName);
                }
                catch (CrowdException e) {
                    log.warn("Failed to create group '" + groupName + "' for auto-add groups of '" + directory.getName() + "'", (Throwable)e);
                }
                catch (ApplicationPermissionException e) {
                    log.warn("Failed to create group '" + groupName + "' for auto-add groups of '" + directory.getName() + "'", (Throwable)e);
                }
            }
        }
    }

    private void ensureGroupExistsInDirectory(long directoryId, String groupName) throws GroupNotFoundException, ApplicationPermissionException, com.atlassian.crowd.exception.OperationFailedException, DirectoryNotFoundException {
        try {
            this.directoryManager.findGroupByName(directoryId, groupName);
        }
        catch (GroupNotFoundException ex) {
            try {
                this.directoryManager.addGroup(directoryId, new GroupTemplate(groupName, directoryId));
            }
            catch (DirectoryPermissionException ex2) {
                throw new ApplicationPermissionException("Group '" + groupName + "' does not exist in the directory of the user and cannot be added.");
            }
            catch (InvalidGroupException e) {
                throw new com.atlassian.crowd.exception.OperationFailedException(e.getMessage(), (Throwable)e);
            }
        }
    }

    private void addObjectError(BindingResult errors, String message, Serializable ... arguments) {
        errors.addError(new ObjectError(CONFIGURATION, this.i18nResolver.getText(message, arguments)));
    }

    protected final void setCrowdDirectoryService(CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = crowdDirectoryService;
    }

    protected final void setI18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    protected final void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    protected I18nResolver getI18nResolver() {
        return this.i18nResolver;
    }

    @ModelAttribute(value="htmlEncoder")
    public HtmlEncoder getHtmlEncoder() {
        return this.htmlEncoder;
    }

    protected void setHtmlEncoder(HtmlEncoder htmlEncoder) {
        this.htmlEncoder = htmlEncoder;
    }

    protected void setDirectoryManager(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    protected void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    protected void setPasswordRestoreUtil(PasswordRestoreUtil passwordRestoreUtil) {
        this.passwordRestoreUtil = passwordRestoreUtil;
    }

    protected void setDarkFeatureManager(DarkFeatureManager darkFeatureManager) {
        this.darkFeatureManager = darkFeatureManager;
    }

    @ModelAttribute(value="groupSyncOnAuthModes")
    public Map<SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth, String> getGroupSyncOnAuthModes() {
        return ImmutableMap.of((Object)SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.WHEN_AUTHENTICATION_CREATED_THE_USER, (Object)"embedded.crowd.directory.edit.ldap.field.groupSyncOnAuthMode.option.whencreated", (Object)SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.NEVER, (Object)"embedded.crowd.directory.edit.ldap.field.groupSyncOnAuthMode.option.never", (Object)SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.ALWAYS, (Object)"embedded.crowd.directory.edit.ldap.field.groupSyncOnAuthMode.option.always");
    }

    @ModelAttribute(value="ldapConnectionPoolingTypeOptions")
    public List<String> getLdapConnectionPoolingTypeOptions() {
        return ImmutableList.of((Object)LdapPoolType.JNDI.name(), (Object)LdapPoolType.COMMONS_POOL2.name());
    }
}

