/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationError
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.user.ConfluenceAuthenticator
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.UserQuery
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.directory.internal;

import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.user.ConfluenceAuthenticator;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.UserQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.troubleshooting.api.healthcheck.Application;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.confluence.healthcheck.directory.internal.AuthenticatorProvider;
import com.atlassian.troubleshooting.healthcheck.DefaultSupportHealthStatus;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class InternalAdminCheckFallback {
    private static final String ADMIN_URL = "confluence.healthcheck.directory.internal.admin.url";
    private final CrowdDirectoryService crowdDirectoryService;
    private final DirectoryManager directoryManager;
    private final I18nResolver i18n;
    private final UserManager userManager;
    private final SimpleValidationResult.Builder errorBuilder;
    private final AuthenticatorProvider authenticatorProvider;

    @Autowired
    public InternalAdminCheckFallback(CrowdDirectoryService crowdDirectoryService, DirectoryManager directoryManager, HelpPathResolver helpPathResolver, I18nResolver i18n, UserManager userManager, AuthenticatorProvider authenticatorProvider) {
        this.authenticatorProvider = authenticatorProvider;
        this.crowdDirectoryService = crowdDirectoryService;
        this.directoryManager = directoryManager;
        this.i18n = i18n;
        this.userManager = userManager;
        this.errorBuilder = SimpleValidationResult.builder();
    }

    public SupportHealthStatus check() {
        boolean hasInternalDirectory = false;
        List directories = this.crowdDirectoryService.findAllDirectories();
        for (Directory directory : directories) {
            if (directory.getType() != DirectoryType.INTERNAL) continue;
            hasInternalDirectory = true;
            UserQuery userEntityQuery = new UserQuery(User.class, (SearchRestriction)NullRestrictionImpl.INSTANCE, 0, 1000);
            ArrayList internalUsers = new ArrayList();
            try {
                internalUsers.addAll(this.directoryManager.searchUsers(directory.getId().longValue(), (EntityQuery)userEntityQuery));
            }
            catch (DirectoryNotFoundException e) {
                this.errorBuilder.addError("no-access-to-directory", new Object[]{this.i18n.getText("confluence.healthcheck.directory.internal.access.directory.fail")});
            }
            catch (OperationFailedException e) {
                this.errorBuilder.addError("directory-not-found", new Object[]{this.i18n.getText("confluence.healthcheck.directory.internal.find.directory.fail")});
            }
            if (internalUsers.isEmpty()) {
                this.errorBuilder.addError("no-internal-user", new Object[]{this.i18n.getText("confluence.healthcheck.directory.internal.users.fail")});
                continue;
            }
            boolean hasInternalAdmin = false;
            for (User user : internalUsers) {
                if (!this.userManager.isSystemAdmin(user.getName())) continue;
                hasInternalAdmin = true;
                break;
            }
            if (hasInternalAdmin) continue;
            this.errorBuilder.addError("no-internal-admin", new Object[]{this.i18n.getText("confluence.healthcheck.directory.internal.admin.fail")});
        }
        if (!hasInternalDirectory) {
            this.errorBuilder.addError("no-internal-directory", new Object[]{this.i18n.getText("confluence.healthcheck.directory.internal.has.internal.dir.fail")});
        }
        if (!(this.authenticatorProvider.getAuthenticator() instanceof ConfluenceAuthenticator)) {
            this.errorBuilder.addError("internal-sso-present", new Object[]{this.i18n.getText("confluence.healthcheck.directory.internal.sso.present")});
        }
        ValidationResult result = this.errorBuilder.build();
        ArrayList<String> messages = new ArrayList<String>();
        if (result.isValid()) {
            messages.add(this.i18n.getText("confluence.healthcheck.directory.internal.has.internal.dir.ok"));
            messages.add(this.i18n.getText("confluence.healthcheck.directory.internal.admin.ok"));
            messages.add(this.i18n.getText("confluence.healthcheck.directory.internal.sso.notpresent"));
            return new DefaultSupportHealthStatus(true, this.buildCheckResponse(messages), System.currentTimeMillis(), Application.Confluence, null, SupportHealthStatus.Severity.UNDEFINED, ADMIN_URL);
        }
        Iterable errors = result.getErrors();
        ArrayList<String> keys = new ArrayList<String>();
        for (ValidationError error : errors) {
            Message message = error.getMessage();
            keys.add(message.getKey());
            messages.add(String.valueOf(message.getArgs()[0]));
        }
        if (keys.size() == 1 && ((String)keys.get(0)).equals("internal-sso-present")) {
            messages.add(this.i18n.getText("confluence.healthcheck.directory.internal.has.internal.dir.ok"));
            messages.add(this.i18n.getText("confluence.healthcheck.directory.internal.admin.ok"));
            return new DefaultSupportHealthStatus(false, this.buildCheckResponse(messages), System.currentTimeMillis(), Application.Confluence, null, SupportHealthStatus.Severity.WARNING, ADMIN_URL);
        }
        return new DefaultSupportHealthStatus(false, this.buildCheckResponse(messages), System.currentTimeMillis(), Application.Confluence, null, SupportHealthStatus.Severity.CRITICAL, ADMIN_URL);
    }

    private String buildCheckResponse(List<String> messages) {
        StringBuilder builder = new StringBuilder();
        for (String message : messages) {
            builder.append(message).append(", ");
        }
        String response = builder.toString();
        response = response.replaceAll(", $", ".");
        return response;
    }
}

