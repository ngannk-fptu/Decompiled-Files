/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.OperationNotSupportedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.password.factory.PasswordEncoderFactory
 *  com.atlassian.crowd.util.InstanceFactory
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.LdapContextSourceProvider;
import com.atlassian.crowd.directory.RFC2307Directory;
import com.atlassian.crowd.directory.ldap.credential.EncryptingCredentialEncoder;
import com.atlassian.crowd.directory.ldap.credential.LDAPCredentialEncoder;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotSupportedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.user.LDAPUserWithAttributes;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.password.factory.PasswordEncoderFactory;
import com.atlassian.crowd.search.ldap.LDAPQueryTranslater;
import com.atlassian.crowd.util.InstanceFactory;
import com.atlassian.event.api.EventPublisher;
import javax.naming.directory.Attributes;

public class Rfc2307
extends RFC2307Directory {
    private final PasswordEncoderFactory passwordEncoderFactory;

    public Rfc2307(LDAPQueryTranslater ldapQueryTranslater, EventPublisher eventPublisher, InstanceFactory instanceFactory, PasswordEncoderFactory passwordEncoderFactory, LdapContextSourceProvider ldapContextSourceProvider) {
        super(ldapQueryTranslater, eventPublisher, instanceFactory, ldapContextSourceProvider);
        this.passwordEncoderFactory = passwordEncoderFactory;
    }

    public static String getStaticDirectoryType() {
        return "Generic Posix/RFC2307 Directory (Read-Only)";
    }

    @Override
    protected LDAPCredentialEncoder getCredentialEncoder() {
        return new EncryptingCredentialEncoder(this.passwordEncoderFactory, this.ldapPropertiesMapper.getUserEncryptionMethod());
    }

    @Override
    protected void getNewUserDirectorySpecificAttributes(User user, Attributes attributes) {
        this.addDefaultSnToUserAttributes(attributes, user.getName());
    }

    public String getDescriptiveName() {
        return Rfc2307.getStaticDirectoryType();
    }

    public void addUserToGroup(String username, String groupName) throws UserNotFoundException, GroupNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws GroupNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    public void removeUserFromGroup(String username, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws GroupNotFoundException, MembershipNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    @Override
    public LDAPUserWithAttributes addUser(UserTemplate user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    @Override
    public LDAPUserWithAttributes addUser(UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    @Override
    public Group addGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    @Override
    public Group renameGroup(String oldName, String newName) throws GroupNotFoundException, InvalidGroupException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    @Override
    public User renameUser(String oldName, String newName) throws UserNotFoundException, InvalidUserException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    @Override
    public Group updateGroup(GroupTemplate group) throws GroupNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    @Override
    public User updateUser(UserTemplate user) throws UserNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    @Override
    public void removeUser(String name) throws UserNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }

    @Override
    public void removeGroup(String name) throws GroupNotFoundException, OperationFailedException {
        throw new OperationNotSupportedException("POSIX support is read-only");
    }
}

