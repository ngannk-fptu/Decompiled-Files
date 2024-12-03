/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.confluence.importexport.DefaultExportContext
 *  com.atlassian.confluence.importexport.ExportContext
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.importexport.ImportExportManager
 *  com.atlassian.confluence.importexport.impl.ExportScope
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.rpc.NotFoundException
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.security.GateKeeper
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SetSpacePermissionChecker
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.security.service.AnonymousUserPermissionsService
 *  com.atlassian.confluence.security.service.IllegalPermissionStateException
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceGroup
 *  com.atlassian.confluence.spaces.SpaceGroupManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 */
package com.atlassian.confluence.rpc.soap.services;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.importexport.DefaultExportContext;
import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.rpc.AlreadyExistsException;
import com.atlassian.confluence.rpc.NotFoundException;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.soap.SoapUtils;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpace;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpaceGroup;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpacePermissionSet;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpaceSummary;
import com.atlassian.confluence.rpc.soap.services.SoapServiceHelper;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SetSpacePermissionChecker;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.service.AnonymousUserPermissionsService;
import com.atlassian.confluence.security.service.IllegalPermissionStateException;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceGroup;
import com.atlassian.confluence.spaces.SpaceGroupManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class SpacesSoapService {
    private SpaceManager spaceManager;
    private PermissionManager permissionManager;
    private SpacePermissionManager spacePermissionManager;
    private PersonalInformationManager personalInformationManager;
    private WikiStyleRenderer wikiStyleRenderer;
    private SoapServiceHelper soapServiceHelper;
    private ImportExportManager importExportManager;
    private GlobalSettingsManager settingsManager;
    private GateKeeper gateKeeper;
    private UserAccessor userAccessor;
    private SpaceGroupManager spaceGroupManager;
    private PageManager pageManager;
    private SetSpacePermissionChecker setSpacePermissionChecker;
    private AnonymousUserPermissionsService anonymousUserPermissionsService;
    public static final String __PARANAMER_DATA = "addGlobalPermission java.lang.String,java.lang.String permission,remoteEntityName \naddGlobalPermissions java.lang.String,java.lang.String permissions,remoteEntityName \naddPermissionToSpace java.lang.String,java.lang.String,java.lang.String permission,remoteEntityName,spaceKey \naddPermissionsToSpace java.lang.String,java.lang.String,java.lang.String permissions,remoteEntityName,spaceKey \naddPersonalSpace com.atlassian.confluence.rpc.soap.beans.RemoteSpace,java.lang.String space,username \naddPersonalSpaceWithDefaultPermissions com.atlassian.confluence.rpc.soap.beans.RemoteSpace,java.lang.String space,username \naddSpace com.atlassian.confluence.rpc.soap.beans.RemoteSpace space \naddSpaceGroup com.atlassian.confluence.rpc.soap.beans.RemoteSpaceGroup spaceGroup \naddSpaceWithDefaultPermissions com.atlassian.confluence.rpc.soap.beans.RemoteSpace space \nexportSpace java.lang.String,java.lang.String spaceKey,exportType \nexportSpace java.lang.String,java.lang.String,boolean spaceKey,exportType,exportAll \ngetPermissions java.lang.String spaceKey \ngetPermissions java.lang.String,java.lang.String spaceKey,userName \ngetSpace java.lang.String spaceKey \ngetSpaceGroup java.lang.String spaceGroupKey \ngetSpacePermissionSet java.lang.String,java.lang.String spaceKey,type \ngetSpacePermissionSets java.lang.String spaceKey \ngetSpaceStatus java.lang.String spaceKey \ngetSpacesInGroup java.lang.String groupKey \nremoveGlobalPermission java.lang.String,java.lang.String permission,remoteEntityName \nremovePermissionFromSpace java.lang.String,java.lang.String,java.lang.String permission,remoteEntityName,spaceKey \nremoveSpace java.lang.String spaceKey \nremoveSpaceGroup java.lang.String spaceGroupKey \nsetAnonymousUserPermissionsService com.atlassian.confluence.security.service.AnonymousUserPermissionsService anonymousUserPermissionsService \nsetGateKeeper com.atlassian.confluence.security.GateKeeper gateKeeper \nsetImportExportManager com.atlassian.confluence.importexport.ImportExportManager importExportManager \nsetPageManager com.atlassian.confluence.pages.PageManager pageManager \nsetPermissionManager com.atlassian.confluence.security.PermissionManager permissionManager \nsetPersonalInformationManager com.atlassian.confluence.user.PersonalInformationManager personalInformationManager \nsetSetSpacePermissionChecker com.atlassian.confluence.security.SetSpacePermissionChecker setSpacePermissionChecker \nsetSettingsManager com.atlassian.confluence.setup.settings.GlobalSettingsManager settingsManager \nsetSoapServiceHelper com.atlassian.confluence.rpc.soap.services.SoapServiceHelper soapServiceHelper \nsetSpaceGroupManager com.atlassian.confluence.spaces.SpaceGroupManager spaceGroupManager \nsetSpaceManager com.atlassian.confluence.spaces.SpaceManager spaceManager \nsetSpacePermissionManager com.atlassian.confluence.security.SpacePermissionManager spacePermissionManager \nsetSpaceStatus java.lang.String,java.lang.String spaceKey,newStatus \nsetUserAccessor com.atlassian.confluence.user.UserAccessor userAccessor \nsetWikiStyleRenderer com.atlassian.renderer.WikiStyleRenderer wikiStyleRenderer \nstoreSpace com.atlassian.confluence.rpc.soap.beans.RemoteSpace remoteSpace \nverifyPersonalSpaceCreation com.atlassian.confluence.user.ConfluenceUser user \n";

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public void setSoapServiceHelper(SoapServiceHelper soapServiceHelper) {
        this.soapServiceHelper = soapServiceHelper;
    }

    public void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    public void setPersonalInformationManager(PersonalInformationManager personalInformationManager) {
        this.personalInformationManager = personalInformationManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public RemoteSpaceSummary[] getSpaces() {
        ListBuilder listBuilder = this.spaceManager.getSpaces(SpacesQuery.newQuery().forUser((User)AuthenticatedUserThreadLocal.get()).build());
        ArrayList<Space> permittedSpaces = new ArrayList<Space>(listBuilder.getAvailableSize());
        for (List spaces : listBuilder) {
            for (Space space : spaces) {
                permittedSpaces.add(space);
            }
        }
        return SoapUtils.getSpaceSummaries(permittedSpaces);
    }

    @Deprecated
    public RemoteSpaceSummary[] getSpacesInGroup(String groupKey) {
        SpaceGroup spaceGroup = this.spaceGroupManager.getSpaceGroup(groupKey);
        ListBuilder listBuilder = this.spaceManager.getSpaces(SpacesQuery.newQuery().forUser((User)AuthenticatedUserThreadLocal.get()).inSpaceGroup(spaceGroup).build());
        ArrayList spaces = new ArrayList(listBuilder.getAvailableSize());
        for (List subList : listBuilder) {
            spaces.addAll(subList);
        }
        return SoapUtils.getSpaceSummaries(spaces);
    }

    public RemoteSpace getSpace(String spaceKey) throws RemoteException {
        return new RemoteSpace(this.soapServiceHelper.retrieveSpace(spaceKey), this.wikiStyleRenderer);
    }

    public String[] getPermissions(String spaceKey) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        return this.getUserPermissions(space, (User)user);
    }

    public RemoteSpacePermissionSet[] getSpacePermissionSets(String spaceKey) throws RemoteException {
        Space space = this.validatePermissionsOperation(spaceKey);
        Multimap<String, SpacePermission> permissions = this.buildSpacePermissionSets(space);
        ArrayList<RemoteSpacePermissionSet> permissionSets = new ArrayList<RemoteSpacePermissionSet>();
        for (String type : permissions.keySet()) {
            permissionSets.add(new RemoteSpacePermissionSet(type, permissions.get((Object)type)));
        }
        return permissionSets.toArray(new RemoteSpacePermissionSet[permissionSets.size()]);
    }

    public RemoteSpacePermissionSet getSpacePermissionSet(String spaceKey, String type) throws RemoteException {
        Space space = this.validatePermissionsOperation(spaceKey);
        Multimap<String, SpacePermission> permissions = this.buildSpacePermissionSets(space);
        return new RemoteSpacePermissionSet(type, permissions.get((Object)type));
    }

    public String[] getPermissions(String spaceKey, String userName) throws RemoteException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        ConfluenceUser user = this.userAccessor.getUserByName(userName);
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.ADMINISTER, (Object)space) && !currentUser.equals(user)) {
            throw new NotPermittedException("Only space administrators can view permissions for other users in the space.");
        }
        return this.getUserPermissions(space, (User)user);
    }

    private String[] getUserPermissions(Space space, User user) {
        ArrayList<String> permissions = new ArrayList<String>(4);
        if (this.permissionManager.hasPermission(user, Permission.VIEW, (Object)space)) {
            permissions.add("view");
        }
        if (this.permissionManager.hasCreatePermission(user, (Object)space, Page.class)) {
            permissions.add("modify");
        }
        if (this.permissionManager.hasCreatePermission(user, (Object)space, Comment.class)) {
            permissions.add("comment");
        }
        if (this.permissionManager.hasPermission(user, Permission.ADMINISTER, (Object)space)) {
            permissions.add("admin");
        }
        return permissions.toArray(new String[permissions.size()]);
    }

    public RemoteSpace addSpaceWithDefaultPermissions(RemoteSpace space) throws RemoteException {
        return this.addSpace(space, false);
    }

    public RemoteSpace addSpace(RemoteSpace space) throws RemoteException {
        return this.addSpace(space, true);
    }

    private RemoteSpace addSpace(RemoteSpace space, boolean isPrivate) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        String key = space.getKey();
        if (!Space.isValidGlobalSpaceKey((String)key)) {
            throw new RemoteException("Invalid space key: " + key);
        }
        if (!this.permissionManager.hasCreatePermission((User)user, PermissionManager.TARGET_APPLICATION, Space.class)) {
            throw new NotPermittedException("No permission to create spaces.");
        }
        if (this.spaceManager.getSpace(key) != null) {
            throw new AlreadyExistsException("A space already exists with key " + key);
        }
        String name = space.getName();
        String description = space.getDescription();
        Space newSpace = isPrivate ? this.spaceManager.createPrivateSpace(key, name, description, (User)user) : this.spaceManager.createSpace(key, name, description, (User)user);
        String group = space.getSpaceGroup();
        if (group != null) {
            SpaceGroup spaceGroup = this.spaceGroupManager.getSpaceGroup(group);
            if (spaceGroup == null) {
                throw new RemoteException("Invalid space group key: " + group);
            }
            newSpace.setSpaceGroup(spaceGroup);
            this.spaceManager.saveSpace(newSpace);
        }
        return new RemoteSpace(newSpace, this.wikiStyleRenderer);
    }

    public RemoteSpace storeSpace(RemoteSpace remoteSpace) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Space space = this.spaceManager.getSpace(remoteSpace.getKey());
        if (space == null) {
            throw new RemoteException("A space with the key '" + remoteSpace.getKey() + "' doesn't exist. You can not change the space key from an existing space.");
        }
        if (!Space.isValidGlobalSpaceKey((String)space.getKey())) {
            throw new RemoteException("Invalid space key: " + space.getKey());
        }
        if (!this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, (Object)space)) {
            throw new NotPermittedException("No permission to change the space details.");
        }
        space.setName(remoteSpace.getName());
        space.setHomePage(this.pageManager.getPage(remoteSpace.getHomePage()));
        space.setSpaceGroup(this.spaceGroupManager.getSpaceGroup(remoteSpace.getSpaceGroup()));
        this.spaceManager.saveSpace(space);
        return new RemoteSpace(space, this.wikiStyleRenderer);
    }

    @Deprecated
    public RemoteSpaceGroup addSpaceGroup(RemoteSpaceGroup spaceGroup) throws RemoteException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!Space.isValidGlobalSpaceKey((String)spaceGroup.getKey())) {
            throw new RemoteException("Invalid spaceGroup key: " + spaceGroup.getKey());
        }
        if (!this.permissionManager.hasCreatePermission((User)user, PermissionManager.TARGET_APPLICATION, Space.class)) {
            throw new NotPermittedException("No permission to create spacesGroups.");
        }
        if (this.spaceGroupManager.getSpaceGroup(spaceGroup.getKey()) != null) {
            throw new AlreadyExistsException("A spaceGroup already exists with key " + spaceGroup.getKey());
        }
        return new RemoteSpaceGroup(this.spaceGroupManager.createSpaceGroup(spaceGroup.getKey(), spaceGroup.getName()));
    }

    @Deprecated
    public RemoteSpaceGroup getSpaceGroup(String spaceGroupKey) throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        SpaceGroup spaceGroup = this.spaceGroupManager.getSpaceGroup(spaceGroupKey);
        if (spaceGroup == null) {
            throw new RemoteException("SpaceGroup with key \"" + spaceGroupKey + "\" does not exist");
        }
        return new RemoteSpaceGroup(spaceGroup);
    }

    @Deprecated
    public RemoteSpaceGroup[] getSpaceGroups() throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        List spaceGroups = this.spaceGroupManager.getSpaceGroups();
        return SoapUtils.getSpaceGroups(spaceGroups);
    }

    @Deprecated
    public boolean removeSpaceGroup(String spaceGroupKey) throws RemoteException {
        SpaceGroup spaceGroup = this.spaceGroupManager.getSpaceGroup(spaceGroupKey);
        if (spaceGroup == null) {
            throw new RemoteException("No spaceGroup found for space key: " + spaceGroupKey);
        }
        this.soapServiceHelper.assertCanAdminister();
        this.spaceGroupManager.removeSpaceGroup(spaceGroup, false);
        return true;
    }

    @Deprecated
    protected void verifyPersonalSpaceCreation(ConfluenceUser user) throws NotPermittedException, AlreadyExistsException {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (currentUser != null && !user.getName().equals(currentUser.getName()) && !this.permissionManager.hasPermission((User)currentUser, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION)) {
            throw new NotPermittedException("No permission to create a personal space for user " + user.getName());
        }
        PersonalInformation pi = this.personalInformationManager.getOrCreatePersonalInformation((User)user);
        if (!this.permissionManager.hasCreatePermission((User)currentUser, (Object)pi, Space.class)) {
            throw new NotPermittedException("No permission to create spaces.");
        }
        Space personalSpace = this.spaceManager.getPersonalSpace(user);
        if (personalSpace != null) {
            throw new AlreadyExistsException("A personal space already exists for the user " + user + " with the key " + personalSpace.getKey());
        }
    }

    public RemoteSpace addPersonalSpaceWithDefaultPermissions(RemoteSpace space, String username) throws RemoteException {
        return this.addPersonalSpace(space, username, false);
    }

    public RemoteSpace addPersonalSpace(RemoteSpace space, String username) throws RemoteException {
        return this.addPersonalSpace(space, username, true);
    }

    private RemoteSpace addPersonalSpace(RemoteSpace space, String username, boolean isPrivate) throws RemoteException {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            throw new RemoteException("No user found with name " + username);
        }
        this.verifyPersonalSpaceCreation(user);
        Space personalSpace = isPrivate ? this.spaceManager.createPrivatePersonalSpace(space.getName(), space.getDescription(), (User)user) : this.spaceManager.createPersonalSpace(space.getName(), space.getDescription(), (User)user);
        return new RemoteSpace(personalSpace, this.wikiStyleRenderer);
    }

    public String[] getSpaceLevelPermissions() {
        Collection genericPermissions = SpacePermission.GENERIC_SPACE_PERMISSIONS;
        String[] permissions = new String[genericPermissions.size() + 1];
        Iterator it = genericPermissions.iterator();
        int i = 0;
        while (it.hasNext()) {
            permissions[i] = (String)it.next();
            ++i;
        }
        permissions[permissions.length - 1] = "VIEWSPACE";
        return permissions;
    }

    public boolean addPermissionToSpace(String permission, String remoteEntityName, String spaceKey) throws RemoteException {
        if (permission == null) {
            throw new RemoteException("Space Permission must be non-null");
        }
        String[] permissionsArray = new String[]{permission};
        return this.addPermissionsToSpace(permissionsArray, remoteEntityName, spaceKey);
    }

    public boolean addGlobalPermissions(String[] permissions, String remoteEntityName) throws RemoteException {
        if (permissions == null) {
            throw new RemoteException("Permissions must be non-null");
        }
        this.soapServiceHelper.assertCanAdminister();
        for (int i = 0; i < permissions.length; ++i) {
            String permission = permissions[i];
            this.addGlobalPermission(permission, remoteEntityName);
        }
        return true;
    }

    public boolean addGlobalPermission(String permission, String remoteEntityName) throws RemoteException {
        if (permission == null) {
            throw new RemoteException("Permission must be non-null");
        }
        this.soapServiceHelper.assertCanAdminister();
        UserOrGroupResolver resolver = new UserOrGroupResolver(remoteEntityName);
        List globalPermissions = this.spacePermissionManager.getGlobalPermissions();
        SpacePermission newPermission = new SpacePermission(permission, null, resolver.getGroupName(), resolver.getUser());
        if (!globalPermissions.contains(newPermission)) {
            globalPermissions.add(newPermission);
            this.spacePermissionManager.savePermission(newPermission);
        }
        return true;
    }

    public boolean addAnonymousUsePermission() throws RemoteException {
        try {
            this.anonymousUserPermissionsService.setUsePermission(true);
        }
        catch (NotAuthorizedException ex) {
            throw new RemoteException((Throwable)ex);
        }
        return true;
    }

    public boolean removeAnonymousUserPermission() throws RemoteException {
        try {
            this.anonymousUserPermissionsService.setUsePermission(false);
        }
        catch (NotAuthorizedException ex) {
            throw new RemoteException((Throwable)ex);
        }
        return true;
    }

    public boolean addAnonymousViewUserProfilePermission() throws RemoteException {
        try {
            this.anonymousUserPermissionsService.setViewUserProfilesPermission(true);
        }
        catch (NotAuthorizedException ex) {
            throw new RemoteException((Throwable)ex);
        }
        catch (IllegalPermissionStateException ex) {
            return false;
        }
        return true;
    }

    public boolean removeAnonymousViewUserProfilePermission() throws RemoteException {
        try {
            this.anonymousUserPermissionsService.setViewUserProfilesPermission(false);
        }
        catch (NotAuthorizedException ex) {
            throw new RemoteException((Throwable)ex);
        }
        return true;
    }

    public boolean removeGlobalPermission(String permission, String remoteEntityName) throws RemoteException {
        this.soapServiceHelper.assertCanAdminister();
        UserOrGroupResolver resolver = new UserOrGroupResolver(remoteEntityName);
        List globalPermissions = this.spacePermissionManager.getGlobalPermissions();
        SpacePermission permissionToBeRemoved = new SpacePermission(permission, null, resolver.getGroupName(), resolver.getUser());
        for (int i = 0; i < globalPermissions.size(); ++i) {
            SpacePermission globalPermission = (SpacePermission)globalPermissions.get(i);
            if (!permissionToBeRemoved.equals((Object)globalPermission)) continue;
            if (this.setSpacePermissionChecker.canSetPermission((User)AuthenticatedUserThreadLocal.get(), globalPermission)) {
                this.spacePermissionManager.removePermission(globalPermission);
                continue;
            }
            throw new NotPermittedException("You do not have permission to remove " + permission + " from " + remoteEntityName);
        }
        return true;
    }

    private Space validatePermissionsOperation(String spaceKey) throws RemoteException {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new NotFoundException("Cannot modify space permissions: space with key '" + spaceKey + "' not found");
        }
        this.soapServiceHelper.assertCanAdminister(space);
        return space;
    }

    public boolean addPermissionsToSpace(String[] permissions, String remoteEntityName, String spaceKey) throws RemoteException {
        if (permissions == null) {
            throw new RemoteException("Space Permissions must be non-null");
        }
        Space space = this.validatePermissionsOperation(spaceKey);
        UserOrGroupResolver resolver = new UserOrGroupResolver(remoteEntityName);
        List spacePermissions = space.getPermissions();
        for (int i = 0; i < permissions.length; ++i) {
            String permission = permissions[i];
            SpacePermission userPermission = new SpacePermission(permission, space, resolver.getGroupName(), resolver.getUser());
            if (spacePermissions.contains(userPermission)) continue;
            space.addPermission(userPermission);
            this.spacePermissionManager.savePermission(userPermission);
        }
        return true;
    }

    public boolean removePermissionFromSpace(String permission, String remoteEntityName, String spaceKey) throws RemoteException {
        Space space = this.validatePermissionsOperation(spaceKey);
        UserOrGroupResolver resolver = new UserOrGroupResolver(remoteEntityName);
        List spacePermissions = space.getPermissions();
        SpacePermission userPermission = new SpacePermission(permission, space, resolver.getGroupName(), resolver.getUser());
        for (int i = 0; i < spacePermissions.size(); ++i) {
            SpacePermission spacePermission = (SpacePermission)spacePermissions.get(i);
            if (!userPermission.equals((Object)spacePermission)) continue;
            this.spacePermissionManager.removePermission(spacePermission);
        }
        return true;
    }

    public Boolean removeSpace(String spaceKey) throws RemoteException {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new RemoteException("No space found for space key: " + spaceKey);
        }
        this.soapServiceHelper.assertCanView(space);
        this.soapServiceHelper.assertCanAdminister(space);
        this.spaceManager.removeSpace(space);
        return Boolean.TRUE;
    }

    public String getSpaceStatus(String spaceKey) throws RemoteException {
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        return space.getSpaceStatus().toString();
    }

    public void setSpaceStatus(String spaceKey, String newStatus) throws RemoteException {
        Space originalSpace;
        SpaceStatus status;
        Space updatedSpace = this.soapServiceHelper.retrieveSpace(spaceKey);
        this.soapServiceHelper.assertCanAdminister(updatedSpace);
        try {
            status = SpaceStatus.valueOf((String)newStatus);
        }
        catch (IllegalArgumentException e) {
            throw new RemoteException("Unknown space status: " + newStatus);
        }
        try {
            originalSpace = (Space)updatedSpace.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RemoteException((Throwable)e);
        }
        updatedSpace.setSpaceStatus(status);
        this.spaceManager.saveSpace(updatedSpace, originalSpace);
    }

    public String exportSpace(String spaceKey, String exportType) throws RemoteException {
        return this.exportSpace(spaceKey, exportType, false);
    }

    public String exportSpace(String spaceKey, String exportType, boolean exportAll) throws RemoteException {
        String downloadPath;
        if (exportType.equals("all")) {
            exportType = "TYPE_XML";
        }
        if (!this.importExportManager.getImportExportTypeSpecifications().contains(exportType)) {
            throw new RemoteException("Invalid export type: [" + exportType + "]");
        }
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new RemoteException("Invalid spaceKey: [" + spaceKey + "]");
        }
        this.soapServiceHelper.assertCanExport(space);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        try {
            DefaultExportContext context = new DefaultExportContext();
            context.setType(exportType);
            context.setExportScope(ExportScope.SPACE);
            context.setExportComments(true);
            context.setExportAttachments(true);
            context.setUser((User)user);
            context.setSpaceKey(space.getKey());
            context.addWorkingEntity((ConfluenceEntityObject)space);
            if ("TYPE_XML".equals(exportType)) {
                context.setContentTree(this.importExportManager.getPageBlogTree((User)user, space));
            } else {
                context.setContentTree(this.importExportManager.getContentTree((User)user, space));
            }
            if (exportAll && "TYPE_XML".equals(exportType)) {
                if (this.isSpaceAdminOrConfAdmin(space)) {
                    context.setExportAll(true);
                } else {
                    throw new NotPermittedException("Not permitted to export restricted pages in the space.");
                }
            }
            String archivePath = this.importExportManager.exportAs((ExportContext)context, new ProgressMeter());
            downloadPath = this.importExportManager.prepareDownloadPath(archivePath);
            Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission(u, Permission.EXPORT, (Object)space);
            this.gateKeeper.addKey(downloadPath, (User)user, permissionPredicate);
        }
        catch (ImportExportException e) {
            return "Could not export space: " + (Object)((Object)e);
        }
        catch (IOException e) {
            return "Could not export space: " + e;
        }
        return this.settingsManager.getGlobalSettings().getBaseUrl() + downloadPath;
    }

    private boolean isSpaceAdminOrConfAdmin(Space space) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, (Object)space) || this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    private Multimap<String, SpacePermission> buildSpacePermissionSets(Space space) {
        HashMultimap permissions = HashMultimap.create();
        for (SpacePermission spacePermission : space.getPermissions()) {
            permissions.put((Object)spacePermission.getType(), (Object)spacePermission);
        }
        return permissions;
    }

    @Deprecated
    public void setImportExportManager(ImportExportManager importExportManager) {
        this.importExportManager = importExportManager;
    }

    public void setGateKeeper(GateKeeper gateKeeper) {
        this.gateKeeper = gateKeeper;
    }

    public void setSettingsManager(GlobalSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Deprecated
    public void setSpaceGroupManager(SpaceGroupManager spaceGroupManager) {
        this.spaceGroupManager = spaceGroupManager;
    }

    public void setSetSpacePermissionChecker(SetSpacePermissionChecker setSpacePermissionChecker) {
        this.setSpacePermissionChecker = setSpacePermissionChecker;
    }

    public void setAnonymousUserPermissionsService(AnonymousUserPermissionsService anonymousUserPermissionsService) {
        this.anonymousUserPermissionsService = anonymousUserPermissionsService;
    }

    private class UserOrGroupResolver {
        private String userName = null;
        private String groupName = null;
        private ConfluenceUser user;
        public static final String __PARANAMER_DATA = "<init> java.lang.String remoteEntityName \n";

        public UserOrGroupResolver(String remoteEntityName) throws RemoteException {
            if (remoteEntityName != null) {
                ConfluenceUser targetUser = SpacesSoapService.this.userAccessor.getUserByName(remoteEntityName);
                if (targetUser == null) {
                    Group group = SpacesSoapService.this.userAccessor.getGroup(remoteEntityName);
                    if (group == null) {
                        throw new RemoteException("No user or group with the name '" + remoteEntityName + "' exists.");
                    }
                    this.groupName = group.getName();
                } else {
                    this.userName = targetUser.getName();
                    this.user = targetUser;
                }
            }
        }

        public ConfluenceUser getUser() {
            return this.user;
        }

        public String getUserName() {
            return this.userName;
        }

        public String getGroupName() {
            return this.groupName;
        }
    }
}

