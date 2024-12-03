/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.user.AdminAddedUserEvent
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.rpc.InvalidSessionException
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.rpc.auth.TokenAuthenticationManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.security.login.LoginManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.user.impl.EntityValidationException
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.security.password.Credential
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.interceptor.TransactionInterceptor
 */
package com.atlassian.confluence.rpc.soap.services;

import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.user.AdminAddedUserEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.rpc.InvalidSessionException;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.auth.TokenAuthenticationManager;
import com.atlassian.confluence.rpc.soap.beans.RemoteConfluenceUser;
import com.atlassian.confluence.rpc.soap.beans.RemoteUser;
import com.atlassian.confluence.rpc.soap.beans.RemoteUserInformation;
import com.atlassian.confluence.rpc.soap.services.SoapServiceHelper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.impl.EntityValidationException;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.security.password.Credential;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.interceptor.TransactionInterceptor;

public class UsersSoapService {
    private static final Logger log = LoggerFactory.getLogger(UsersSoapService.class);
    private UserAccessor userAccessor;
    private SpacePermissionManager spacePermissionManager;
    private PermissionManager permissionManager;
    private SoapServiceHelper soapServiceHelper;
    private SpaceManager spaceManager;
    private PersonalInformationManager personalInformationManager;
    private AttachmentManager attachmentManager;
    private EventPublisher eventPublisher;
    private LoginManager loginManager;
    private TokenAuthenticationManager tokenAuthenticationManager;
    public static final String __PARANAMER_DATA = "addGroup java.lang.String groupname \naddProfilePicture java.lang.String,java.lang.String,java.lang.String,byte userName,fileName,mimeType,pictureData \naddUser com.atlassian.confluence.rpc.soap.beans.RemoteUser,java.lang.String,boolean user,password,notifyUser \naddUserToGroup java.lang.String,java.lang.String username,groupname \nchangeMyPassword java.lang.String,java.lang.String,java.lang.String token,oldPass,newPass \nchangeUserPassword java.lang.String,java.lang.String username,newPass \ndeactivateUser java.lang.String username \neditUser com.atlassian.confluence.rpc.soap.beans.RemoteUser remoteUser \ngetActiveUsers boolean viewAll \ngetUserByKey java.lang.String userKey \ngetUserByName java.lang.String username \ngetUserGroups java.lang.String username \ngetUserInformation java.lang.String username \ngetUserPreferenceBoolean java.lang.String,java.lang.String username,key \ngetUserPreferenceLong java.lang.String,java.lang.String username,key \ngetUserPreferenceString java.lang.String,java.lang.String username,key \nhasGroup java.lang.String groupname \nhasUser java.lang.String username \nisActiveUser java.lang.String username \nreactivateUser java.lang.String username \nremoveAllPermissionsForGroup java.lang.String groupname \nremoveGroup java.lang.String,java.lang.String groupName,moveToGroupName \nremoveUser java.lang.String username \nremoveUserFromGroup java.lang.String,java.lang.String username,groupname \nrenameUser java.lang.String,java.lang.String oldUsername,newUsername \nrenameUsers java.util.Map oldUsernamesToNewUsernames \nsetAttachmentManager com.atlassian.confluence.pages.AttachmentManager attachmentManager \nsetEventPublisher com.atlassian.event.api.EventPublisher eventPublisher \nsetLoginManager com.atlassian.confluence.security.login.LoginManager loginManager \nsetPermissionManager com.atlassian.confluence.security.PermissionManager permissionManager \nsetPersonalInformationManager com.atlassian.confluence.user.PersonalInformationManager personalInformationManager \nsetSoapServiceHelper com.atlassian.confluence.rpc.soap.services.SoapServiceHelper soapServiceHelper \nsetSpaceManager com.atlassian.confluence.spaces.SpaceManager spaceManager \nsetSpacePermissionManager com.atlassian.confluence.security.SpacePermissionManager spacePermissionManager \nsetTokenAuthenticationManager com.atlassian.confluence.rpc.auth.TokenAuthenticationManager tokenAuthenticationManager \nsetUserAccessor com.atlassian.confluence.user.UserAccessor userAccessor \nsetUserInformation com.atlassian.confluence.rpc.soap.beans.RemoteUserInformation userInfo \nsetUserPreferenceBoolean java.lang.String,java.lang.String,boolean username,key,value \nsetUserPreferenceLong java.lang.String,java.lang.String,long username,key,value \nsetUserPreferenceString java.lang.String,java.lang.String,java.lang.String username,key,value \n";

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public void setSoapServiceHelper(SoapServiceHelper soapServiceHelper) {
        this.soapServiceHelper = soapServiceHelper;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    public void setTokenAuthenticationManager(TokenAuthenticationManager tokenAuthenticationManager) {
        this.tokenAuthenticationManager = tokenAuthenticationManager;
    }

    public RemoteConfluenceUser getUserByName(String username) throws RemoteException {
        ConfluenceUser givenUser = this.retrieveUser(username);
        this.checkUserCanViewObject(givenUser);
        return new RemoteConfluenceUser(givenUser);
    }

    public RemoteConfluenceUser getUserByKey(String userKey) throws RemoteException {
        ConfluenceUser givenUser = this.retrieveUser(new UserKey(userKey));
        this.checkUserCanViewObject(givenUser);
        return new RemoteConfluenceUser(givenUser);
    }

    private ConfluenceUser retrieveUser(String username) throws RemoteException {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            throw new RemoteException("No user with username " + username + " found.");
        }
        return user;
    }

    private ConfluenceUser retrieveUser(UserKey userKey) throws RemoteException {
        ConfluenceUser user = this.userAccessor.getUserByKey(userKey);
        if (user == null) {
            throw new RemoteException("No user with key " + userKey + " found.");
        }
        return user;
    }

    private void checkUserCanEditObject(Object target) throws NotPermittedException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, target)) {
            throw new NotPermittedException("You are not permitted to access " + target);
        }
    }

    private void checkUserCanViewObject(Object target) throws NotPermittedException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, target)) {
            throw new NotPermittedException("You are not permitted to access " + target);
        }
    }

    public boolean addUser(RemoteUser user, String password, boolean notifyUser) throws RemoteException {
        if (user == null) {
            throw new RemoteException("Can't add null user.");
        }
        if (StringUtils.isBlank((CharSequence)user.getName())) {
            throw new RemoteException("Can't add user with null or blank username.");
        }
        if (StringUtils.isBlank((CharSequence)user.getFullname())) {
            throw new RemoteException("Can't add user with null or blank fullname.");
        }
        if (StringUtils.isBlank((CharSequence)user.getEmail())) {
            throw new RemoteException("Can't add user with null or blank email address.");
        }
        if (!user.getName().equals(user.getName().toLowerCase())) {
            throw new RemoteException("A user name must be in lower case.");
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasCreatePermission((User)currentUser, PermissionManager.TARGET_APPLICATION, User.class)) {
            throw new NotPermittedException("You do not have permissions to add the user " + user.getName() + ".");
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        try {
            ConfluenceUser newUser = this.userAccessor.createUser((User)new DefaultUser(user.getName(), user.getFullname(), user.getEmail()), Credential.unencrypted((String)password));
            Group confluenceUsersGroup = this.userAccessor.getGroupCreateIfNecessary(this.userAccessor.getNewUserDefaultGroupName());
            this.userAccessor.addMembership(confluenceUsersGroup, (User)newUser);
            if (notifyUser) {
                AdminAddedUserEvent addedUserEvent = new AdminAddedUserEvent((User)newUser);
                this.eventPublisher.publish((Object)addedUserEvent);
            }
        }
        catch (InfrastructureException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new RemoteException("Could not create new user '" + user.getName() + "': " + e.getMessage(), e.getCause());
        }
        return true;
    }

    public boolean removeUser(String username) throws RemoteException {
        ConfluenceUser user = this.retrieveUser(username);
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.REMOVE, (Object)user)) {
            throw new NotPermittedException("You do not have permissions to remove the user " + username);
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        Space personalSpace = this.spaceManager.getPersonalSpace(user);
        if (personalSpace != null) {
            this.spaceManager.removeSpace(personalSpace);
            user = this.retrieveUser(username);
        }
        try {
            this.userAccessor.removeUser((User)user);
        }
        catch (InfrastructureException e) {
            return false;
        }
        return true;
    }

    public boolean editUser(RemoteUser remoteUser) throws RemoteException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ConfluenceUser givenUser = this.retrieveUser(remoteUser.getName());
        boolean updateRequired = false;
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, (Object)givenUser)) {
            throw new NotPermittedException("You do not have permissions to edit user: " + remoteUser.getName());
        }
        if (!currentUser.equals(givenUser)) {
            this.soapServiceHelper.assertHasValidWebSudoSession();
        }
        DefaultUser userTemplate = new DefaultUser((User)givenUser);
        if (!StringUtils.equals((CharSequence)givenUser.getFullName(), (CharSequence)remoteUser.getFullname())) {
            updateRequired = true;
            userTemplate.setFullName(remoteUser.getFullname());
        }
        if (!StringUtils.equals((CharSequence)givenUser.getEmail(), (CharSequence)remoteUser.getEmail())) {
            updateRequired = true;
            userTemplate.setEmail(remoteUser.getEmail());
        }
        if (updateRequired) {
            this.userAccessor.saveUser((User)userTemplate);
        }
        return true;
    }

    public String[] getUserGroups(String username) throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        ConfluenceUser user = this.retrieveUser(username);
        Pager groups = this.userAccessor.getGroups((User)user);
        ArrayList<String> groupsOfUser = new ArrayList<String>();
        for (Group group : groups) {
            groupsOfUser.add(group.getName());
        }
        return groupsOfUser.toArray(new String[0]);
    }

    public boolean addUserToGroup(String username, String groupname) throws RemoteException {
        ConfluenceUser user = this.retrieveUser(username);
        Group group = this.userAccessor.getGroup(groupname);
        if (group == null) {
            throw new RemoteException("The group specified does not exist.");
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)group)) {
            throw new NotPermittedException("You do not have permissions to add the user " + username + " to the following group " + groupname);
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        this.userAccessor.addMembership(group, (User)user);
        return this.userAccessor.hasMembership(group, (User)user);
    }

    public boolean removeUserFromGroup(String username, String groupname) throws RemoteException {
        ConfluenceUser user = this.retrieveUser(username);
        Group group = this.userAccessor.getGroup(groupname);
        if (group == null) {
            throw new RemoteException("The group specified does not exist.");
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)group)) {
            throw new NotPermittedException("You do not have permissions to remove the user " + username + " from the following group " + groupname);
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        this.userAccessor.removeMembership(group, (User)user);
        return !this.userAccessor.hasMembership(group, (User)user);
    }

    public boolean addGroup(String groupname) throws RemoteException {
        if (StringUtils.isBlank((CharSequence)groupname)) {
            throw new RemoteException("Can't add a blank group.");
        }
        if (!groupname.equals(groupname.toLowerCase())) {
            throw new RemoteException("A group name must be in lower case.");
        }
        if (!this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), PermissionManager.TARGET_APPLICATION, Group.class)) {
            throw new NotPermittedException("You do not have permissions to create groups.");
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        if (this.userAccessor.getGroup(groupname) == null) {
            this.userAccessor.createGroup(groupname);
        }
        return true;
    }

    public boolean removeAllPermissionsForGroup(String groupname) throws RemoteException {
        Group group = this.userAccessor.getGroup(groupname);
        if (group == null) {
            throw new RemoteException("cannot delete permissions for non existing group");
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, (Object)group)) {
            throw new NotPermittedException("You do not have permissions to remove all permissions from the group " + groupname);
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        this.spacePermissionManager.removeAllPermissionsForGroup(groupname);
        return true;
    }

    public boolean removeGroup(String groupName, String moveToGroupName) throws RemoteException {
        Group group = this.userAccessor.getGroup(groupName);
        if (group == null) {
            throw new RemoteException("Group " + groupName + " does not exist.");
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, (Object)group)) {
            throw new NotPermittedException("You do not have permissions to remove this group: " + group.getName());
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        if (StringUtils.isNotBlank((CharSequence)moveToGroupName)) {
            Group moveToGroup = this.userAccessor.getGroup(moveToGroupName);
            if (moveToGroup == null) {
                throw new RemoteException("Cannot remove members of deleted group to " + moveToGroupName + " as it does not exist.");
            }
            if (this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, (Object)moveToGroup)) {
                for (ConfluenceUser user : this.userAccessor.getMembers(group)) {
                    this.userAccessor.addMembership(moveToGroup, (User)user);
                }
            } else {
                throw new NotPermittedException("Cannot move members of deleted group " + groupName + " to " + moveToGroupName + " as " + AuthenticatedUserThreadLocal.getUsername() + " does not have group edit permissions.");
            }
        }
        this.userAccessor.removeGroup(group);
        return true;
    }

    public String[] getGroups() throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        ArrayList<String> groups = new ArrayList<String>();
        for (Group group : this.userAccessor.getGroupsAsList()) {
            groups.add(group.getName());
        }
        return groups.toArray(new String[0]);
    }

    public boolean deactivateUser(String username) throws RemoteException {
        ConfluenceUser user = this.retrieveUser(username);
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, (Object)user)) {
            throw new NotPermittedException("You do not have permissions to deactivate this user: " + user.getName());
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        if (this.userAccessor.isDeactivated((User)user)) {
            throw new RemoteException("User has already been deactivated");
        }
        this.userAccessor.deactivateUser((User)user);
        return true;
    }

    public boolean reactivateUser(String username) throws RemoteException {
        ConfluenceUser user = this.retrieveUser(username);
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, (Object)user)) {
            throw new NotPermittedException("You do not have permissions to reactivate this user: " + user.getName());
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        if (!this.userAccessor.isDeactivated((User)user)) {
            throw new RemoteException("User is already active");
        }
        this.userAccessor.reactivateUser((User)user);
        return true;
    }

    public boolean isActiveUser(String username) throws RemoteException {
        ConfluenceUser user = this.retrieveUser(username);
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)user)) {
            throw new NotPermittedException("You do not have permissions to check if this user is active: " + username);
        }
        return !this.userAccessor.isDeactivated((User)user);
    }

    public String[] getActiveUsers(boolean viewAll) throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        if (!viewAll) {
            List users = this.userAccessor.getUserNamesWithConfluenceAccess();
            return users.toArray(new String[0]);
        }
        ArrayList<String> result = new ArrayList<String>();
        for (User user : this.userAccessor.getUsers()) {
            result.add(user.getName());
        }
        return result.toArray(new String[0]);
    }

    public boolean changeMyPassword(String token, String oldPass, String newPass) throws RemoteException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (this.loginManager.requiresElevatedSecurityCheck(currentUser.getName())) {
            this.tokenAuthenticationManager.logout(token);
            throw new InvalidSessionException("Session is invalid. Please login again.");
        }
        if (!this.userAccessor.authenticate(currentUser.getName(), oldPass)) {
            this.loginManager.onFailedLoginAttempt(currentUser.getName(), null);
            throw new NotPermittedException("The current password was incorrect. Please try again.");
        }
        if (StringUtils.isBlank((CharSequence)newPass)) {
            throw new RemoteException("New password cannot be null or empty");
        }
        try {
            this.userAccessor.alterPassword((User)currentUser, newPass);
        }
        catch (EntityException e) {
            throw new RemoteException((Throwable)e);
        }
        return true;
    }

    public boolean changeUserPassword(String username, String newPass) throws RemoteException {
        ConfluenceUser givenUser;
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, (Object)(givenUser = this.retrieveUser(username)))) {
            throw new NotPermittedException("You are not logged in as the correct user, or you do not have the correct permissions to perform this action.");
        }
        this.soapServiceHelper.assertHasValidWebSudoSession();
        if (StringUtils.isBlank((CharSequence)newPass)) {
            throw new RemoteException("New password cannot be null or empty.");
        }
        try {
            this.userAccessor.alterPassword((User)givenUser, newPass);
        }
        catch (EntityException ee) {
            throw new RemoteException("Error changing password for user " + username, (Throwable)ee);
        }
        return true;
    }

    public boolean setUserInformation(RemoteUserInformation userInfo) throws RemoteException {
        ConfluenceUser givenUser;
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, (Object)(givenUser = this.retrieveUser(userInfo.getUsername())))) {
            throw new NotPermittedException("You are not logged in as the correct user, or you do not have the correct permissions to perform this action.");
        }
        if (!currentUser.equals(givenUser)) {
            this.soapServiceHelper.assertHasValidWebSudoSession();
        }
        if (userInfo.getContent() != null) {
            PersonalInformation newInfo = this.personalInformationManager.getOrCreatePersonalInformation((User)givenUser);
            PersonalInformation oldInfo = null;
            oldInfo = (PersonalInformation)newInfo.clone();
            if (newInfo.getId() != userInfo.getId()) {
                throw new RemoteException("Error saving personal information: ID cannot be changed");
            }
            if (newInfo.getBodyContent().getBody().equals(userInfo.getContent())) {
                return true;
            }
            ExceptionTolerantMigrator migrator = (ExceptionTolerantMigrator)ContainerManager.getComponent((String)"wikiToXhtmlMigrator");
            String migratedBody = migrator.migrate(userInfo.getContent(), (RenderContext)newInfo.toPageContext(), null);
            newInfo.setBodyAsString(migratedBody);
            this.personalInformationManager.savePersonalInformation(newInfo, oldInfo);
        }
        return true;
    }

    public RemoteUserInformation getUserInformation(String username) throws RemoteException {
        ConfluenceUser givenUser = this.retrieveUser(username);
        this.checkUserCanViewObject(givenUser);
        PersonalInformation info = this.personalInformationManager.getOrCreatePersonalInformation((User)givenUser);
        return new RemoteUserInformation(info);
    }

    public boolean setUserPreferenceBoolean(String username, String key, boolean value) throws RemoteException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ConfluenceUser givenUser = this.retrieveUser(username);
        this.checkUserCanEditObject(givenUser);
        if (!currentUser.equals(givenUser)) {
            this.soapServiceHelper.assertHasValidWebSudoSession();
        }
        try {
            this.userAccessor.getUserPreferences((User)givenUser).setBoolean(key, value);
        }
        catch (AtlassianCoreException e) {
            throw new RemoteException("Failed to set user preference.", (Throwable)e);
        }
        return true;
    }

    public boolean getUserPreferenceBoolean(String username, String key) throws RemoteException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ConfluenceUser givenUser = this.retrieveUser(username);
        this.checkUserCanEditObject(givenUser);
        if (!currentUser.equals(givenUser)) {
            this.soapServiceHelper.assertHasValidWebSudoSession();
        }
        return this.userAccessor.getUserPreferences((User)givenUser).getBoolean(key);
    }

    public boolean setUserPreferenceLong(String username, String key, long value) throws RemoteException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ConfluenceUser givenUser = this.retrieveUser(username);
        this.checkUserCanEditObject(givenUser);
        if (!currentUser.equals(givenUser)) {
            this.soapServiceHelper.assertHasValidWebSudoSession();
        }
        try {
            this.userAccessor.getUserPreferences((User)givenUser).setLong(key, value);
        }
        catch (AtlassianCoreException e) {
            throw new RemoteException("Failed to set user preference.", (Throwable)e);
        }
        return true;
    }

    public long getUserPreferenceLong(String username, String key) throws RemoteException {
        ConfluenceUser givenUser = this.retrieveUser(username);
        this.checkUserCanEditObject(givenUser);
        return this.userAccessor.getUserPreferences((User)givenUser).getLong(key);
    }

    public boolean setUserPreferenceString(String username, String key, String value) throws RemoteException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ConfluenceUser givenUser = this.retrieveUser(username);
        this.checkUserCanEditObject(givenUser);
        if (!currentUser.equals(givenUser)) {
            this.soapServiceHelper.assertHasValidWebSudoSession();
        }
        try {
            this.userAccessor.getUserPreferences((User)givenUser).setString(key, value);
        }
        catch (AtlassianCoreException e) {
            throw new RemoteException("Failed to set user preference.", (Throwable)e);
        }
        return true;
    }

    public String getUserPreferenceString(String username, String key) throws RemoteException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ConfluenceUser givenUser = this.retrieveUser(username);
        this.checkUserCanEditObject(givenUser);
        if (!currentUser.equals(givenUser)) {
            this.soapServiceHelper.assertHasValidWebSudoSession();
        }
        return this.userAccessor.getUserPreferences((User)givenUser).getString(key);
    }

    public boolean hasUser(String username) {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        return user != null;
    }

    public boolean hasGroup(String groupname) throws NotPermittedException {
        Group group = this.userAccessor.getGroup(groupname);
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, PermissionManager.TARGET_APPLICATION)) {
            throw new NotPermittedException("Unauthorized user, not permitted to perform this operation");
        }
        return group != null;
    }

    public void setPersonalInformationManager(PersonalInformationManager personalInformationManager) {
        this.personalInformationManager = personalInformationManager;
    }

    public boolean addProfilePicture(String userName, String fileName, String mimeType, byte[] pictureData) throws RemoteException {
        if (!mimeType.toLowerCase().startsWith("image/")) {
            throw new RemoteException("Invalid MIME type. Only image/* types may be used for profile pictures");
        }
        ConfluenceUser user = this.userAccessor.getUserByName(userName);
        if (user == null) {
            throw new RemoteException("User does not exist");
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, (Object)user)) {
            throw new NotPermittedException("You are not permitted to add a profile picture for the specified user");
        }
        if (!currentUser.equals(user)) {
            this.soapServiceHelper.assertHasValidWebSudoSession();
        }
        if (fileName == null) {
            throw new RemoteException("Filename is required");
        }
        String actualFileName = new File(fileName).getName();
        if (!fileName.equals(actualFileName)) {
            throw new RemoteException("An invalid filename was provided");
        }
        PersonalInformation personalInfo = this.personalInformationManager.getOrCreatePersonalInformation((User)user);
        Attachment attachment = this.attachmentManager.getAttachment((ContentEntityObject)personalInfo, fileName);
        Attachment previousVersion = null;
        if (attachment == null) {
            attachment = new Attachment();
        } else {
            previousVersion = (Attachment)attachment.clone();
        }
        attachment.setMediaType(mimeType);
        attachment.setFileName(fileName);
        attachment.setVersionComment("Uploaded Profile Picture");
        attachment.setFileSize((long)pictureData.length);
        personalInfo.addAttachment(attachment);
        try {
            this.attachmentManager.saveAttachment(attachment, previousVersion, (InputStream)new ByteArrayInputStream(pictureData));
        }
        catch (IOException e) {
            throw new RemoteException("Error adding profile picture: Cound not save attachment");
        }
        UserPreferences userPreferences = new UserPreferences(this.userAccessor.getPropertySet(user));
        try {
            userPreferences.setString("confluence.user.profile.picture", fileName);
        }
        catch (AtlassianCoreException ex) {
            throw new RemoteException("Problem setting user preferences", (Throwable)ex);
        }
        return true;
    }

    public boolean renameUser(String oldUsername, String newUsername) throws RemoteException {
        ConfluenceUser user;
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, (Object)(user = this.retrieveUser(oldUsername)))) {
            throw new NotPermittedException("You do not have permissions to edit user: " + oldUsername);
        }
        if (!currentUser.equals(user)) {
            this.soapServiceHelper.assertHasValidWebSudoSession();
        }
        try {
            this.userAccessor.renameUser(user, newUsername);
            return true;
        }
        catch (EntityException e) {
            throw new RemoteException("Cannot rename user: " + e.getMessage(), (Throwable)e);
        }
    }

    public String[] renameUsers(Map<String, String> oldUsernamesToNewUsernames) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ArrayList<String> failedRenames = new ArrayList<String>();
        for (Map.Entry<String, String> entry : oldUsernamesToNewUsernames.entrySet()) {
            String oldUsername = entry.getKey();
            String newUsername = entry.getValue();
            try {
                ConfluenceUser user = this.retrieveUser(oldUsername);
                if (!this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, (Object)user)) {
                    throw new NotPermittedException("You do not have permissions to edit user: " + oldUsername);
                }
                if (!currentUser.equals(user)) {
                    this.soapServiceHelper.assertHasValidWebSudoSession();
                }
                this.userAccessor.renameUser(user, newUsername);
            }
            catch (EntityValidationException e) {
                failedRenames.add(oldUsername);
                log.debug("New username does not meet username requirements", (Throwable)e);
            }
            catch (RemoteException | EntityException e) {
                failedRenames.add(oldUsername);
                log.debug("Problem renaming user", e);
            }
        }
        return failedRenames.toArray(new String[0]);
    }
}

