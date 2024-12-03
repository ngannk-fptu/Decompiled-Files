/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.spi.DcLicenseChecker
 *  com.atlassian.crowd.event.user.UserAuthenticatedByEmailAddressEvent
 *  com.atlassian.crowd.event.user.UserAuthenticationFailedInvalidAuthenticationEvent
 *  com.atlassian.crowd.event.user.UserEmailAuthenticationDuplicatedEmailEvent
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.validator.EmailAddressValidator
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.spi.DcLicenseChecker;
import com.atlassian.crowd.event.user.UserAuthenticatedByEmailAddressEvent;
import com.atlassian.crowd.event.user.UserAuthenticationFailedInvalidAuthenticationEvent;
import com.atlassian.crowd.event.user.UserEmailAuthenticationDuplicatedEmailEvent;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.application.AbstractDelegatingApplicationService;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.manager.application.CanonicalUsersByEmailFinder;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.validator.EmailAddressValidator;
import com.atlassian.event.api.EventPublisher;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllowingAuthenticateByEmailApplicationService
extends AbstractDelegatingApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(AllowingAuthenticateByEmailApplicationService.class);
    private final EmailAddressValidator emailValidator;
    private final EventPublisher eventPublisher;
    private final CanonicalUsersByEmailFinder canonicalUsersByEmailFinder;
    private final DcLicenseChecker dcLicenseChecker;

    public AllowingAuthenticateByEmailApplicationService(ApplicationService delegate, EmailAddressValidator emailValidator, EventPublisher eventPublisher, CanonicalUsersByEmailFinder canonicalUsersByEmailFinder, DcLicenseChecker dcLicenseChecker) {
        super(delegate);
        this.emailValidator = emailValidator;
        this.eventPublisher = eventPublisher;
        this.canonicalUsersByEmailFinder = canonicalUsersByEmailFinder;
        this.dcLicenseChecker = dcLicenseChecker;
    }

    @Override
    public User authenticateUser(Application application, String usernameOrEmail, PasswordCredential passwordCredential) throws OperationFailedException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, UserNotFoundException {
        try {
            logger.debug("Trying to authenticate user in application '{}' by treating '{}' as username", (Object)application.getName(), (Object)usernameOrEmail);
            return super.authenticateUser(application, usernameOrEmail, passwordCredential);
        }
        catch (UserNotFoundException e) {
            if (!application.isAuthenticationViaEmailEnabled() || !this.dcLicenseChecker.isDcLicense()) {
                throw e;
            }
            logger.debug("User with username '{}' not found in application '{}'. Trying to authenticate by treating '{}' as email", new Object[]{usernameOrEmail, application.getName(), usernameOrEmail});
            return this.tryToLoginByEmail(application, passwordCredential, usernameOrEmail, e);
        }
    }

    private User tryToLoginByEmail(Application application, PasswordCredential passwordCredential, String email, UserNotFoundException originalException) throws InvalidAuthenticationException, OperationFailedException, InactiveAccountException, ExpiredCredentialException, UserNotFoundException {
        User authenticatedUser;
        if (!this.emailValidator.isValidSyntax(email)) {
            logger.debug("'{}' is not a valid email. We will not try to authenticate user in app '{}' by email", (Object)email, (Object)application.getName());
            throw originalException;
        }
        List<String> canonicalOwners = this.canonicalUsersByEmailFinder.findCanonicalUsersByEmail(application, email);
        if (canonicalOwners.isEmpty()) {
            logger.debug("There are no users owning '{}' email in app '{}'. Rejecting authentication", (Object)email, (Object)application.getName());
            throw originalException;
        }
        if (canonicalOwners.size() > 1) {
            logger.debug("There is more than one user in app '{}' who owns '{}' email. Rejecting authentication.", (Object)application.getName(), (Object)email);
            this.eventPublisher.publish((Object)new UserEmailAuthenticationDuplicatedEmailEvent());
            throw originalException;
        }
        logger.debug("Matched email '{}' to username '{}'. Trying to authenticate user", (Object)email, (Object)canonicalOwners.get(0));
        try {
            authenticatedUser = super.authenticateUser(application, canonicalOwners.get(0), passwordCredential);
        }
        catch (InvalidAuthenticationException e) {
            this.maybePublishEvent(e);
            throw e;
        }
        this.eventPublisher.publish((Object)new UserAuthenticatedByEmailAddressEvent());
        return authenticatedUser;
    }

    private void maybePublishEvent(InvalidAuthenticationException exception) {
        if (exception.getDirectory() != null && exception.getUsername() != null) {
            logger.info("Invalid credentials for user '{}' in directory '{}', aborting", (Object)exception.getUsername(), (Object)exception.getDirectory().getName());
            this.eventPublisher.publish((Object)new UserAuthenticationFailedInvalidAuthenticationEvent((Object)this, exception.getDirectory(), exception.getUsername()));
        }
    }
}

