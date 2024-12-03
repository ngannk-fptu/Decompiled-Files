/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.emailchange.InvalidChangeEmailTokenException
 *  com.atlassian.crowd.emailchange.SameEmailAddressException
 *  com.atlassian.crowd.event.EventTokenExpiredException
 *  com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException
 *  com.atlassian.crowd.exception.ApplicationAlreadyExistsException
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidEmailAddressException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidMembershipException
 *  com.atlassian.crowd.exception.InvalidTokenException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.NestedGroupsNotSupportedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 *  com.atlassian.crowd.manager.application.ApplicationAccessDeniedException
 *  com.atlassian.crowd.manager.application.ApplicationManagerException
 *  com.atlassian.crowd.manager.permission.AnonymousUserPermissionException
 *  com.atlassian.crowd.manager.permission.UserPermissionDowngradeException
 *  com.atlassian.crowd.manager.permission.UserPermissionException
 *  com.atlassian.plugins.rest.common.security.AuthenticationRequiredException
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlEnum
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.emailchange.InvalidChangeEmailTokenException;
import com.atlassian.crowd.emailchange.SameEmailAddressException;
import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException;
import com.atlassian.crowd.exception.ApplicationAlreadyExistsException;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidEmailAddressException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.InvalidTokenException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.NestedGroupsNotSupportedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.exception.WebhookNotFoundException;
import com.atlassian.crowd.manager.application.ApplicationAccessDeniedException;
import com.atlassian.crowd.manager.application.ApplicationManagerException;
import com.atlassian.crowd.manager.permission.AnonymousUserPermissionException;
import com.atlassian.crowd.manager.permission.UserPermissionDowngradeException;
import com.atlassian.crowd.manager.permission.UserPermissionException;
import com.atlassian.crowd.plugin.rest.exception.mapper.ExceptionMapperUtil;
import com.atlassian.plugins.rest.common.security.AuthenticationRequiredException;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="error")
@XmlAccessorType(value=XmlAccessType.FIELD)
public final class ErrorEntity
implements Serializable {
    @XmlElement(name="reason")
    private final ErrorReason reason;
    @XmlElement(name="message")
    private final String message;

    private ErrorEntity() {
        this.reason = null;
        this.message = null;
    }

    public ErrorEntity(ErrorReason reason, String message) {
        this.reason = reason;
        this.message = message;
    }

    public ErrorReason getReason() {
        return this.reason;
    }

    public String getMessage() {
        return this.message;
    }

    public static ErrorEntity of(Exception exception) {
        return new ErrorEntity(ErrorReason.of(exception), ExceptionMapperUtil.stripNonValidXMLCharacters(exception.getMessage()));
    }

    @XmlEnum
    public static enum ErrorReason {
        APPLICATION_ACCESS_DENIED,
        APPLICATION_PERMISSION_DENIED,
        EXPIRED_CREDENTIAL,
        GROUP_NOT_FOUND,
        ILLEGAL_ARGUMENT,
        INACTIVE_ACCOUNT,
        INVALID_USER_AUTHENTICATION,
        INVALID_CREDENTIAL,
        INVALID_EMAIL,
        INVALID_GROUP,
        INVALID_SSO_TOKEN,
        INVALID_USER,
        MEMBERSHIP_NOT_FOUND,
        MEMBERSHIP_ALREADY_EXISTS,
        NESTED_GROUPS_NOT_SUPPORTED,
        APPLICATION_NOT_FOUND,
        UNSUPPORTED_OPERATION,
        USER_NOT_FOUND,
        OPERATION_FAILED,
        EVENT_TOKEN_EXPIRED,
        INCREMENTAL_SYNC_NOT_AVAILABLE,
        WEBHOOK_NOT_FOUND,
        APPLICATION_MODIFICATION_FAILED,
        DIRECTORY_NOT_FOUND,
        PERMISSION_DENIED,
        INVALID_MEMBERSHIP,
        APPLICATION_ALREADY_EXISTS,
        INVALID_CHANGE_EMAIL_TOKEN,
        SAME_EMAIL_ADDRESS;


        public static ErrorReason of(Throwable e) {
            if (e instanceof ApplicationAccessDeniedException) {
                return APPLICATION_ACCESS_DENIED;
            }
            if (e instanceof ApplicationNotFoundException) {
                return APPLICATION_NOT_FOUND;
            }
            if (e instanceof ApplicationPermissionException) {
                return APPLICATION_PERMISSION_DENIED;
            }
            if (e instanceof ExpiredCredentialException) {
                return EXPIRED_CREDENTIAL;
            }
            if (e instanceof GroupNotFoundException) {
                return GROUP_NOT_FOUND;
            }
            if (e instanceof IllegalArgumentException) {
                return ILLEGAL_ARGUMENT;
            }
            if (e instanceof InactiveAccountException) {
                return INACTIVE_ACCOUNT;
            }
            if (e instanceof InvalidAuthenticationException) {
                return INVALID_USER_AUTHENTICATION;
            }
            if (e instanceof InvalidCredentialException) {
                return INVALID_CREDENTIAL;
            }
            if (e instanceof InvalidEmailAddressException) {
                return INVALID_EMAIL;
            }
            if (e instanceof InvalidGroupException) {
                return INVALID_GROUP;
            }
            if (e instanceof InvalidTokenException) {
                return INVALID_SSO_TOKEN;
            }
            if (e instanceof InvalidUserException) {
                return INVALID_USER;
            }
            if (e instanceof MembershipNotFoundException) {
                return MEMBERSHIP_NOT_FOUND;
            }
            if (e instanceof MembershipAlreadyExistsException) {
                return MEMBERSHIP_ALREADY_EXISTS;
            }
            if (e instanceof NestedGroupsNotSupportedException) {
                return NESTED_GROUPS_NOT_SUPPORTED;
            }
            if (e instanceof UnsupportedOperationException) {
                return UNSUPPORTED_OPERATION;
            }
            if (e instanceof UserNotFoundException) {
                return USER_NOT_FOUND;
            }
            if (e instanceof EventTokenExpiredException) {
                return EVENT_TOKEN_EXPIRED;
            }
            if (e instanceof IncrementalSynchronisationNotAvailableException) {
                return INCREMENTAL_SYNC_NOT_AVAILABLE;
            }
            if (e instanceof WebhookNotFoundException) {
                return WEBHOOK_NOT_FOUND;
            }
            if (e instanceof ApplicationManagerException) {
                return APPLICATION_MODIFICATION_FAILED;
            }
            if (e instanceof DirectoryNotFoundException) {
                return DIRECTORY_NOT_FOUND;
            }
            if (e instanceof UserPermissionDowngradeException) {
                return PERMISSION_DENIED;
            }
            if (e instanceof AnonymousUserPermissionException) {
                return PERMISSION_DENIED;
            }
            if (e instanceof AuthenticationRequiredException) {
                return PERMISSION_DENIED;
            }
            if (e instanceof UserPermissionException) {
                return PERMISSION_DENIED;
            }
            if (e instanceof InvalidMembershipException) {
                return INVALID_MEMBERSHIP;
            }
            if (e instanceof ApplicationAlreadyExistsException) {
                return APPLICATION_ALREADY_EXISTS;
            }
            if (e instanceof InvalidChangeEmailTokenException) {
                return INVALID_CHANGE_EMAIL_TOKEN;
            }
            if (e instanceof SameEmailAddressException) {
                return SAME_EMAIL_ADDRESS;
            }
            return OPERATION_FAILED;
        }
    }
}

