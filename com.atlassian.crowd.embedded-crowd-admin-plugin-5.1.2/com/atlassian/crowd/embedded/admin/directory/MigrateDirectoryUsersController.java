/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory
 *  com.atlassian.crowd.embedded.impl.ImmutableDirectory$Builder
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.UserQuery
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Controller
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.FieldError
 *  org.springframework.validation.ObjectError
 *  org.springframework.web.bind.annotation.ModelAttribute
 *  org.springframework.web.bind.annotation.RequestMapping
 *  org.springframework.web.bind.annotation.RequestMethod
 *  org.springframework.web.servlet.ModelAndView
 */
package com.atlassian.crowd.embedded.admin.directory;

import com.atlassian.crowd.embedded.admin.directory.MigrateDirectoryUsersCommand;
import com.atlassian.crowd.embedded.admin.util.HtmlEncoder;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.ImmutableDirectory;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.UserQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value={"/directories/migrate/**"})
public final class MigrateDirectoryUsersController {
    private static final Logger log = LoggerFactory.getLogger(MigrateDirectoryUsersController.class);
    private static final String FORM_VIEW = "migrate-directory-users-form";
    private static final String MIGRATION = "migration";
    @Autowired
    private CrowdDirectoryService crowdDirectoryService;
    @Autowired
    private UserManager userManager;
    @Autowired
    private I18nResolver i18nResolver;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private HtmlEncoder htmlEncoder;
    @Autowired
    private DirectoryManager directoryManager;
    @Autowired
    private ApplicationProperties applicationProperties;

    @ModelAttribute(value="directories")
    public Map<String, String> getDirectories() {
        LinkedHashMap<String, String> directories = new LinkedHashMap<String, String>();
        for (Directory directory : this.crowdDirectoryService.findAllDirectories()) {
            if (directory.getType() != DirectoryType.INTERNAL && directory.getType() != DirectoryType.DELEGATING) continue;
            directories.put(directory.getId().toString(), directory.getName());
        }
        return directories;
    }

    @RequestMapping(method={RequestMethod.GET})
    public String initializeForm(@ModelAttribute(value="migration") MigrateDirectoryUsersCommand command) throws Exception {
        return FORM_VIEW;
    }

    @RequestMapping(method={RequestMethod.POST})
    public ModelAndView migrateUsers(HttpServletRequest request, @ModelAttribute(value="migration") MigrateDirectoryUsersCommand migrateUsersCommand, BindingResult errors) throws Exception {
        String remoteUser = Optional.ofNullable(this.userManager.getRemoteUser(request)).map(UserProfile::getUsername).orElse(null);
        if (!errors.hasErrors()) {
            this.migrateUsers(migrateUsersCommand.getFromDirectoryId(), migrateUsersCommand.getToDirectoryId(), remoteUser, migrateUsersCommand, errors);
        }
        return new ModelAndView(FORM_VIEW, errors.getModel());
    }

    private void migrateUsers(final long fromDirectoryId, final long toDirectoryId, final String remoteUser, MigrateDirectoryUsersCommand migrateUsersCommand, BindingResult errors) {
        Directory from = this.validateDirectory(fromDirectoryId, errors, "fromDirectoryId");
        Directory to = this.validateDirectory(toDirectoryId, errors, "toDirectoryId");
        if (to != null && to.equals(from)) {
            errors.addError((ObjectError)new FieldError(MIGRATION, "toDirectoryId", this.i18nResolver.getText("embedded.crowd.directory.migrate.users.field.directory.same")));
        }
        if (errors.hasErrors()) {
            return;
        }
        this.setDirectoryEnabled(from, false);
        this.setDirectoryEnabled(to, false);
        NullRestriction restriction = NullRestrictionImpl.INSTANCE;
        UserQuery query = new UserQuery(User.class, (SearchRestriction)restriction, 0, -1);
        try {
            List users = this.directoryManager.searchUsers(fromDirectoryId, (EntityQuery)query);
            final AtomicLong migratedCount = new AtomicLong(0L);
            for (final User user : users) {
                this.transactionTemplate.execute(new TransactionCallback(){

                    public Object doInTransaction() {
                        try {
                            MigrateDirectoryUsersController.this.migrateUser(fromDirectoryId, toDirectoryId, remoteUser, user, migratedCount);
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    }
                });
            }
            migrateUsersCommand.setTestSuccessful(true);
            migrateUsersCommand.setTotalCount(users.size());
            migrateUsersCommand.setMigratedCount(migratedCount.get());
        }
        catch (Exception e) {
            log.error("User migration failed", (Throwable)e);
            errors.addError(new ObjectError(MIGRATION, this.i18nResolver.getText("embedded.crowd.directory.migrate.users.error", new Serializable[]{this.htmlEncoder.encode(e.getMessage())})));
        }
        this.setDirectoryEnabled(from, true);
        this.setDirectoryEnabled(to, true);
    }

    private void migrateUser(long fromDirectoryId, long toDirectoryId, String remoteUser, User user, AtomicLong migratedCount) throws Exception {
        if (!user.getName().equalsIgnoreCase(remoteUser)) {
            UserWithAttributes userWithAttributes = this.directoryManager.findUserWithAttributesByName(fromDirectoryId, user.getName());
            try {
                UserTemplate newUser = new UserTemplate(user);
                newUser.setDirectoryId(toDirectoryId);
                this.directoryManager.addUser(toDirectoryId, newUser, new PasswordCredential(MigrateDirectoryUsersController.generatePassword()));
            }
            catch (InvalidUserException e) {
                return;
            }
            Set keys = userWithAttributes.getKeys();
            HashMap<String, Set> attributes = new HashMap<String, Set>();
            for (String key : keys) {
                Set values = userWithAttributes.getValues(key);
                attributes.put(key, values);
            }
            this.directoryManager.storeUserAttributes(toDirectoryId, user.getName(), attributes);
            MembershipQuery groupQuery = QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withName(user.getName()).returningAtMost(-1);
            List groups = this.directoryManager.searchDirectGroupRelationships(fromDirectoryId, groupQuery);
            for (Group group : groups) {
                try {
                    this.directoryManager.findGroupByName(toDirectoryId, group.getName());
                }
                catch (GroupNotFoundException ex) {
                    GroupTemplate newGroup = new GroupTemplate(group);
                    newGroup.setDirectoryId(toDirectoryId);
                    this.directoryManager.addGroup(toDirectoryId, newGroup);
                }
                this.directoryManager.addUserToGroup(toDirectoryId, user.getName(), group.getName());
                this.directoryManager.removeUserFromGroup(fromDirectoryId, user.getName(), group.getName());
            }
            this.directoryManager.removeUser(fromDirectoryId, user.getName());
            migratedCount.addAndGet(1L);
        }
    }

    private Directory validateDirectory(Long directoryId, BindingResult errors, String field) {
        if (directoryId == -1L) {
            errors.addError((ObjectError)new FieldError(MIGRATION, field, this.i18nResolver.getText("embedded.crowd.directory.migrate.users.field.directory.required")));
            return null;
        }
        Directory directory = this.crowdDirectoryService.findDirectoryById(directoryId.longValue());
        if (directory == null) {
            errors.addError((ObjectError)new FieldError(MIGRATION, field, this.i18nResolver.getText("embedded.crowd.directory.migrate.users.field.directory.not.found")));
        } else {
            Set allowedOperations;
            if (directory.getType() != DirectoryType.INTERNAL && directory.getType() != DirectoryType.DELEGATING) {
                errors.addError((ObjectError)new FieldError(MIGRATION, field, this.i18nResolver.getText("embedded.crowd.directory.migrate.users.field.directory.wrong.type")));
            }
            if (!((allowedOperations = directory.getAllowedOperations()).contains(OperationType.CREATE_USER) && allowedOperations.contains(OperationType.CREATE_GROUP) && allowedOperations.contains(OperationType.DELETE_USER) && allowedOperations.contains(OperationType.DELETE_GROUP))) {
                errors.addError((ObjectError)new FieldError(MIGRATION, field, this.i18nResolver.getText("embedded.crowd.directory.migrate.users.field.directory.read.only")));
            }
        }
        return directory;
    }

    private void setDirectoryEnabled(final Directory from, final boolean enabled) {
        this.transactionTemplate.execute(new TransactionCallback(){

            public Object doInTransaction() {
                ImmutableDirectory.Builder builder = ImmutableDirectory.newBuilder((Directory)from);
                builder.setActive(enabled);
                Directory updatedDirectory = builder.toDirectory();
                MigrateDirectoryUsersController.this.crowdDirectoryService.updateDirectory(updatedDirectory);
                log.info("User directory {}: [ {} ], type: [ {} ]", (Object[])new String[]{enabled ? "enabled" : "disabled", from.getName(), from.getType().toString()});
                return null;
            }
        });
    }

    public static String generatePassword() {
        Random random = new Random();
        return new BigInteger(130, random).toString(32) + "ABab23";
    }

    public CrowdDirectoryService getCrowdDirectoryService() {
        return this.crowdDirectoryService;
    }

    public I18nResolver getI18nResolver() {
        return this.i18nResolver;
    }

    public TransactionTemplate getTransactionTemplate() {
        return this.transactionTemplate;
    }

    @ModelAttribute(value="htmlEncoder")
    public HtmlEncoder getHtmlEncoder() {
        return this.htmlEncoder;
    }

    public DirectoryManager getDirectoryManager() {
        return this.directoryManager;
    }

    public ApplicationProperties getApplicationProperties() {
        return this.applicationProperties;
    }

    public UserManager getUserManager() {
        return this.userManager;
    }
}

