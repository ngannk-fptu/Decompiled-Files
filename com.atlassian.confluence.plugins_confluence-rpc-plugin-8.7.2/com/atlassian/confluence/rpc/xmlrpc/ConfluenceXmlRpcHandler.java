/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.InvalidSessionException
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.rpc.SecureRpc
 */
package com.atlassian.confluence.rpc.xmlrpc;

import com.atlassian.confluence.rpc.InvalidSessionException;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.SecureRpc;
import java.util.Hashtable;
import java.util.Vector;

public interface ConfluenceXmlRpcHandler
extends SecureRpc {
    public static final String __PARANAMER_DATA = "addAnonymousPermissionToSpace java.lang.String,java.lang.String,java.lang.String token,permission,spaceKey \naddAnonymousPermissionsToSpace java.lang.String,java.util.Vector,java.lang.String token,permissions,spaceKey \naddAnonymousUsePermission java.lang.String token \naddAnonymousViewUserProfilePermission java.lang.String token \naddAttachment java.lang.String,java.lang.String,java.util.Hashtable,byte token,contentId,attachment,attachmentData \naddComment java.lang.String,java.util.Hashtable token,comment \naddGlobalPermission java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName \naddGlobalPermissions java.lang.String,java.util.Vector,java.lang.String token,permissions,remoteEntityName \naddGroup java.lang.String,java.lang.String token,groupname \naddLabelById java.lang.String,java.lang.String,java.lang.String token,labelId,objectId \naddLabelByName java.lang.String,java.lang.String,java.lang.String token,labelName,objectId \naddLabelByNameToSpace java.lang.String,java.lang.String,java.lang.String token,labelName,spaceKey \naddLabelByObject java.lang.String,java.util.Hashtable,java.lang.String token,labelObject,objectId \naddPermissionToSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName,spaceKey \naddPermissionsToSpace java.lang.String,java.util.Vector,java.lang.String,java.lang.String token,permissions,remoteEntityName,spaceKey \naddPersonalSpace java.lang.String,java.util.Hashtable,java.lang.String token,space,username \naddProfilePicture java.lang.String,java.lang.String,java.lang.String,java.lang.String,byte token,userName,fileName,mimeType,pictureData \naddSpace java.lang.String,java.util.Hashtable token,space \naddSpaceGroup java.lang.String,java.util.Hashtable token,spaceGroup \naddUser java.lang.String,java.util.Hashtable,java.lang.String token,remoteUser,password \naddUser java.lang.String,java.util.Hashtable,java.lang.String,boolean token,remoteUser,password,NotifyUser \naddUserToGroup java.lang.String,java.lang.String,java.lang.String token,username,groupname \nchangeMyPassword java.lang.String,java.lang.String,java.lang.String token,oldPass,newPass \nchangeUserPassword java.lang.String,java.lang.String,java.lang.String token,username,newPass \nclearIndexQueue java.lang.String token \nconvertWikiToStorageFormat java.lang.String,java.lang.String token,wiki \ndeactivateUser java.lang.String,java.lang.String token,username \neditComment java.lang.String,java.util.Hashtable token,comment \neditUser java.lang.String,java.util.Hashtable token,remoteUser \nemptyTrash java.lang.String,java.lang.String token,spaceKey \nexportSite java.lang.String,boolean token,exportAttachments \nexportSpace java.lang.String,java.lang.String,java.lang.String token,spaceKey,exportType \nflushIndexQueue java.lang.String token \ngetActiveUsers java.lang.String,boolean token,viewAll \ngetAncestors java.lang.String,java.lang.String token,pageId \ngetAttachment java.lang.String,java.lang.String,java.lang.String,java.lang.String token,contentId,fileName,version \ngetAttachmentData java.lang.String,java.lang.String,java.lang.String,java.lang.String token,contentId,fileName,version \ngetAttachments java.lang.String,java.lang.String token,pageId \ngetBlogEntries java.lang.String,java.lang.String token,spaceKey \ngetBlogEntry java.lang.String,java.lang.String token,blogEntryId \ngetBlogEntryByDateAndTitle java.lang.String,java.lang.String,int,int,int,java.lang.String token,spaceKey,year,month,dayOfMonth,postTitle \ngetBlogEntryByDayAndTitle java.lang.String,java.lang.String,int,java.lang.String token,spaceKey,dayOfMonth,postTitle \ngetChildren java.lang.String,java.lang.String token,pageId \ngetClusterInformation java.lang.String token \ngetClusterNodeStatuses java.lang.String token \ngetComment java.lang.String,java.lang.String token,commentId \ngetComments java.lang.String,java.lang.String token,pageId \ngetContentPermissionSet java.lang.String,java.lang.String,java.lang.String token,contentId,permissionType \ngetContentPermissionSets java.lang.String,java.lang.String token,contentId \ngetContentPermissions java.lang.String,java.lang.String token,contentId \ngetDescendents java.lang.String,java.lang.String token,pageId \ngetGroups java.lang.String token \ngetLabelContentById java.lang.String,java.lang.String token,labelId \ngetLabelContentByName java.lang.String,java.lang.String token,labelName \ngetLabelContentByObject java.lang.String,java.util.Hashtable token,labelObject \ngetLabelsByDetail java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String token,labelName,namespace,spaceKey,owner \ngetLabelsById java.lang.String,java.lang.String token,objectId \ngetMostPopularLabels java.lang.String,int token,maxCount \ngetMostPopularLabelsInSpace java.lang.String,java.lang.String,int token,spaceKey,maxCount \ngetPage java.lang.String,java.lang.String token,pageId \ngetPage java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageTitle \ngetPageHistory java.lang.String,java.lang.String token,pageId \ngetPagePermissions java.lang.String,java.lang.String token,pageId \ngetPageSummary java.lang.String,java.lang.String token,pageId \ngetPageSummary java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageTitle \ngetPages java.lang.String,java.lang.String token,spaceKey \ngetPermissions java.lang.String,java.lang.String token,spaceKey \ngetPermissionsForUser java.lang.String,java.lang.String,java.lang.String token,spaceKey,userName \ngetRecentlyUsedLabels java.lang.String,int token,maxResults \ngetRecentlyUsedLabelsInSpace java.lang.String,java.lang.String,int token,spaceKey,maxResults \ngetRelatedLabels java.lang.String,java.lang.String,int token,labelName,maxResults \ngetRelatedLabelsInSpace java.lang.String,java.lang.String,java.lang.String,int token,labelName,spaceKey,maxResults \ngetServerInfo java.lang.String token \ngetSpace java.lang.String,java.lang.String token,spaceKey \ngetSpaceGroup java.lang.String,java.lang.String token,spaceGroupKey \ngetSpaceGroups java.lang.String token \ngetSpaceLevelPermissions java.lang.String token \ngetSpacePermissionSet java.lang.String,java.lang.String,java.lang.String token,spaceKey,permissionType \ngetSpacePermissionSets java.lang.String,java.lang.String token,spaceKey \ngetSpaceStatus java.lang.String,java.lang.String token,spaceKey \ngetSpaces java.lang.String token \ngetSpacesContainingContentWithLabel java.lang.String,java.lang.String token,labelName \ngetSpacesInGroup java.lang.String,java.lang.String token,spaceGroupKey \ngetSpacesWithLabel java.lang.String,java.lang.String token,labelName \ngetTopLevelPages java.lang.String,java.lang.String token,spaceKey \ngetTrashContents java.lang.String,java.lang.String,int,int token,spaceKey,offset,maxResults \ngetUser java.lang.String,java.lang.String token,username \ngetUserByKey java.lang.String,java.lang.String token,userKey \ngetUserGroups java.lang.String,java.lang.String token,username \ngetUserInformation java.lang.String,java.lang.String token,username \ngetUserPreferenceBoolean java.lang.String,java.lang.String,java.lang.String token,username,key \ngetUserPreferenceLong java.lang.String,java.lang.String,java.lang.String token,username,key \ngetUserPreferenceString java.lang.String,java.lang.String,java.lang.String token,username,key \ngetWatchersForPage java.lang.String,java.lang.String token,pageId \ngetWatchersForSpace java.lang.String,java.lang.String token,spaceKey \nhasGroup java.lang.String,java.lang.String token,groupname \nhasUser java.lang.String,java.lang.String token,username \nimportSpace java.lang.String,byte token,importData \ninstallPlugin java.lang.String,java.lang.String,byte token,pluginFileName,pluginData \nisActiveUser java.lang.String,java.lang.String token,username \nisDarkFeatureEnabled java.lang.String,java.lang.String token,key \nisPluginEnabled java.lang.String,java.lang.String token,pluginKey \nisPluginInstalled java.lang.String,java.lang.String token,pluginKey \nisWatchingPage java.lang.String,java.lang.String,java.lang.String token,pageId,username \nisWatchingSpace java.lang.String,java.lang.String,java.lang.String token,spaceKey,username \nisWatchingSpaceForType java.lang.String,java.lang.String,java.lang.String,java.lang.String token,spaceKey,contentType,username \nlogin java.lang.String,java.lang.String username,password \nlogout java.lang.String token \nmoveAttachment java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String token,originalContentId,originalFileName,newContentId,newFileName \nmovePage java.lang.String,java.lang.String,java.lang.String,java.lang.String token,sourcePageId,targetPageId,position \nmovePageToTopLevel java.lang.String,java.lang.String,java.lang.String token,pageId,targetSpaceKey \nperformBackup java.lang.String,boolean token,exportAttachments \npurgeFromTrash java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageId \nreactivateUser java.lang.String,java.lang.String token,username \nrecoverMainIndex java.lang.String token \nremoveAllPermissionsForGroup java.lang.String,java.lang.String token,groupname \nremoveAnonymousPermissionFromSpace java.lang.String,java.lang.String,java.lang.String token,permission,spaceKey \nremoveAnonymousUsePermission java.lang.String token \nremoveAnonymousViewUserProfilePermission java.lang.String token \nremoveAttachment java.lang.String,java.lang.String,java.lang.String token,contentId,fileName \nremoveComment java.lang.String,java.lang.String token,commentId \nremoveGlobalPermission java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName \nremoveGroup java.lang.String,java.lang.String,java.lang.String token,groupname,defaultGroupName \nremoveLabelById java.lang.String,java.lang.String,java.lang.String token,labelId,objectId \nremoveLabelByName java.lang.String,java.lang.String,java.lang.String token,labelName,objectId \nremoveLabelByNameFromSpace java.lang.String,java.lang.String,java.lang.String token,labelName,spaceKey \nremoveLabelByObject java.lang.String,java.util.Hashtable,java.lang.String token,labelObject,objectId \nremovePage java.lang.String,java.lang.String token,pageId \nremovePageVersionById java.lang.String,java.lang.String token,historicalPageId \nremovePageVersionByVersion java.lang.String,java.lang.String,int token,pageId,version \nremovePageWatch java.lang.String,java.lang.String token,pageId \nremovePageWatchForUser java.lang.String,java.lang.String,java.lang.String token,pageId,username \nremovePermissionFromSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName,spaceKey \nremoveSpace java.lang.String,java.lang.String token,spaceKey \nremoveSpaceGroup java.lang.String,java.lang.String token,spaceGroupKey \nremoveSpaceWatch java.lang.String,java.lang.String token,spaceKey \nremoveUser java.lang.String,java.lang.String token,username \nremoveUserFromGroup java.lang.String,java.lang.String,java.lang.String token,username,groupname \nrenameUser java.lang.String,java.lang.String,java.lang.String token,oldUsername,newUsername \nrenameUsers java.lang.String,java.util.Hashtable token,oldUsernamesToNewUsernames \nrenderContent java.lang.String,java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageId,newContent \nrenderContent java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.util.Hashtable token,spaceKey,pageId,newContent,renderParameters \nsearch java.lang.String,java.lang.String,int token,query,maxResults \nsearch java.lang.String,java.lang.String,java.util.Hashtable,int token,query,params,maxResults \nsetContentPermissions java.lang.String,java.lang.String,java.lang.String,java.util.Vector token,contentId,permissionType,permissions \nsetEnableAnonymousAccess java.lang.String,java.lang.String token,value \nsetSpaceStatus java.lang.String,java.lang.String,java.lang.String token,spaceKey,statusString \nsetUserInformation java.lang.String,java.util.Hashtable token,userInfo \nsetUserPreferenceBoolean java.lang.String,java.lang.String,java.lang.String,boolean token,username,key,value \nsetUserPreferenceLong java.lang.String,java.lang.String,java.lang.String,java.lang.String token,username,key,value \nsetUserPreferenceString java.lang.String,java.lang.String,java.lang.String,java.lang.String token,username,key,value \nstartActivity java.lang.String,java.lang.String,java.lang.String token,key,user \nstopActivity java.lang.String,java.lang.String,java.lang.String token,key,user \nstoreBlogEntry java.lang.String,java.util.Hashtable token,blogEntryStruct \nstorePage java.lang.String,java.util.Hashtable token,pageStruct \nstoreSpace java.lang.String,java.util.Hashtable token,remoteSpace \nupdatePage java.lang.String,java.util.Hashtable,java.util.Hashtable token,pageStruct,editOptionsStruct \nwatchPage java.lang.String,java.lang.String token,pageId \nwatchPageForUser java.lang.String,java.lang.String,java.lang.String token,pageId,username \nwatchSpace java.lang.String,java.lang.String token,spaceKey \n";

    public String login(String var1, String var2) throws RemoteException;

    public boolean logout(String var1) throws RemoteException;

    public Vector getPages(String var1, String var2) throws RemoteException;

    public Hashtable getPage(String var1, String var2) throws RemoteException;

    public Hashtable getPageSummary(String var1, String var2) throws RemoteException;

    public Hashtable getPage(String var1, String var2, String var3) throws RemoteException;

    public Hashtable getPageSummary(String var1, String var2, String var3) throws RemoteException;

    public Vector getContentPermissionSets(String var1, String var2) throws RemoteException;

    public Hashtable getContentPermissionSet(String var1, String var2, String var3) throws RemoteException;

    public Vector getContentPermissions(String var1, String var2) throws RemoteException;

    public Boolean setContentPermissions(String var1, String var2, String var3, Vector var4) throws RemoteException;

    public Vector getPermissions(String var1, String var2) throws RemoteException;

    public Vector getPermissionsForUser(String var1, String var2, String var3) throws RemoteException;

    public Vector getSpacePermissionSets(String var1, String var2) throws RemoteException;

    public Hashtable getSpacePermissionSet(String var1, String var2, String var3) throws RemoteException;

    public String renderContent(String var1, String var2, String var3, String var4) throws RemoteException;

    public String renderContent(String var1, String var2, String var3, String var4, Hashtable var5) throws RemoteException;

    public Hashtable storePage(String var1, Hashtable var2) throws RemoteException;

    public Hashtable updatePage(String var1, Hashtable var2, Hashtable var3) throws RemoteException;

    public Boolean movePageToTopLevel(String var1, String var2, String var3) throws RemoteException;

    public Boolean movePage(String var1, String var2, String var3, String var4) throws RemoteException;

    public Boolean removePage(String var1, String var2) throws RemoteException;

    public Boolean removePageVersionById(String var1, String var2) throws RemoteException;

    public Boolean removePageVersionByVersion(String var1, String var2, int var3) throws RemoteException;

    public Vector search(String var1, String var2, int var3) throws RemoteException;

    public Vector search(String var1, String var2, Hashtable var3, int var4) throws RemoteException;

    public Vector getPageHistory(String var1, String var2) throws RemoteException;

    public Vector getPagePermissions(String var1, String var2) throws RemoteException;

    public Hashtable getTrashContents(String var1, String var2, int var3, int var4) throws RemoteException;

    public Boolean purgeFromTrash(String var1, String var2, String var3) throws RemoteException;

    public Boolean emptyTrash(String var1, String var2) throws RemoteException;

    public Vector getBlogEntries(String var1, String var2) throws RemoteException;

    public Hashtable getBlogEntryByDayAndTitle(String var1, String var2, int var3, String var4) throws RemoteException;

    public Hashtable getBlogEntryByDateAndTitle(String var1, String var2, int var3, int var4, int var5, String var6) throws RemoteException;

    public Hashtable getBlogEntry(String var1, String var2) throws RemoteException;

    public Hashtable storeBlogEntry(String var1, Hashtable var2) throws RemoteException;

    public Vector getComments(String var1, String var2) throws RemoteException;

    public Hashtable getComment(String var1, String var2) throws InvalidSessionException, RemoteException;

    public Hashtable addComment(String var1, Hashtable var2) throws InvalidSessionException, NotPermittedException, RemoteException;

    public Hashtable editComment(String var1, Hashtable var2) throws InvalidSessionException, NotPermittedException, RemoteException;

    public boolean removeComment(String var1, String var2) throws InvalidSessionException, NotPermittedException, RemoteException;

    public Vector getTopLevelPages(String var1, String var2) throws RemoteException;

    public Vector getAncestors(String var1, String var2) throws RemoteException;

    public Vector getChildren(String var1, String var2) throws RemoteException;

    public Vector getDescendents(String var1, String var2) throws RemoteException;

    public Vector getAttachments(String var1, String var2) throws RemoteException;

    public String convertWikiToStorageFormat(String var1, String var2) throws RemoteException;

    public boolean watchPage(String var1, String var2) throws RemoteException;

    public boolean watchSpace(String var1, String var2) throws RemoteException;

    public boolean watchPageForUser(String var1, String var2, String var3) throws RemoteException;

    public boolean removePageWatch(String var1, String var2) throws RemoteException;

    public boolean removeSpaceWatch(String var1, String var2) throws RemoteException;

    public boolean removePageWatchForUser(String var1, String var2, String var3) throws RemoteException;

    public boolean isWatchingPage(String var1, String var2, String var3) throws RemoteException;

    public boolean isWatchingSpace(String var1, String var2, String var3) throws RemoteException;

    public boolean isWatchingSpaceForType(String var1, String var2, String var3, String var4) throws RemoteException;

    public Vector getWatchersForPage(String var1, String var2) throws RemoteException;

    public Vector getWatchersForSpace(String var1, String var2) throws RemoteException;

    public Hashtable addAttachment(String var1, String var2, Hashtable var3, byte[] var4) throws RemoteException, NotPermittedException;

    public Hashtable getAttachment(String var1, String var2, String var3, String var4) throws RemoteException, NotPermittedException;

    public byte[] getAttachmentData(String var1, String var2, String var3, String var4) throws RemoteException, NotPermittedException;

    public boolean removeAttachment(String var1, String var2, String var3) throws RemoteException, NotPermittedException;

    public boolean moveAttachment(String var1, String var2, String var3, String var4, String var5) throws RemoteException, NotPermittedException;

    public Vector getSpaces(String var1) throws RemoteException;

    public Vector getSpacesInGroup(String var1, String var2) throws RemoteException;

    public Hashtable getSpace(String var1, String var2) throws RemoteException;

    public Hashtable addSpace(String var1, Hashtable var2) throws RemoteException;

    public Hashtable storeSpace(String var1, Hashtable var2) throws RemoteException;

    public Hashtable addPersonalSpace(String var1, Hashtable var2, String var3) throws RemoteException;

    public Boolean removeSpace(String var1, String var2) throws RemoteException;

    public String getSpaceStatus(String var1, String var2) throws RemoteException;

    public Boolean setSpaceStatus(String var1, String var2, String var3) throws RemoteException;

    public String exportSpace(String var1, String var2, String var3) throws RemoteException;

    public boolean importSpace(String var1, byte[] var2) throws RemoteException;

    public boolean addPermissionToSpace(String var1, String var2, String var3, String var4) throws RemoteException;

    public boolean addPermissionsToSpace(String var1, Vector var2, String var3, String var4) throws RemoteException;

    public boolean removePermissionFromSpace(String var1, String var2, String var3, String var4) throws NotPermittedException, RemoteException;

    public boolean addAnonymousPermissionToSpace(String var1, String var2, String var3) throws RemoteException;

    public boolean addAnonymousPermissionsToSpace(String var1, Vector var2, String var3) throws RemoteException;

    public boolean removeAnonymousPermissionFromSpace(String var1, String var2, String var3) throws NotPermittedException, RemoteException;

    public String[] getSpaceLevelPermissions(String var1) throws RemoteException;

    public boolean addGlobalPermissions(String var1, Vector var2, String var3) throws RemoteException;

    public boolean addGlobalPermission(String var1, String var2, String var3) throws RemoteException;

    public boolean removeGlobalPermission(String var1, String var2, String var3) throws RemoteException;

    public boolean addAnonymousUsePermission(String var1) throws RemoteException;

    public boolean removeAnonymousUsePermission(String var1) throws RemoteException;

    public boolean addAnonymousViewUserProfilePermission(String var1) throws RemoteException;

    public boolean removeAnonymousViewUserProfilePermission(String var1) throws RemoteException;

    @Deprecated
    public Hashtable addSpaceGroup(String var1, Hashtable var2) throws RemoteException;

    @Deprecated
    public Hashtable getSpaceGroup(String var1, String var2) throws RemoteException;

    @Deprecated
    public Vector getSpaceGroups(String var1) throws RemoteException;

    @Deprecated
    public boolean removeSpaceGroup(String var1, String var2) throws RemoteException;

    public String exportSite(String var1, boolean var2) throws RemoteException;

    public String performBackup(String var1, boolean var2) throws RemoteException;

    public Hashtable getServerInfo(String var1) throws RemoteException;

    public boolean flushIndexQueue(String var1) throws RemoteException;

    public boolean clearIndexQueue(String var1) throws RemoteException;

    public boolean recoverMainIndex(String var1) throws RemoteException;

    public Hashtable getClusterInformation(String var1) throws RemoteException;

    public Vector getClusterNodeStatuses(String var1) throws RemoteException;

    public Vector getGroups(String var1) throws RemoteException;

    public boolean hasGroup(String var1, String var2) throws InvalidSessionException, RemoteException;

    public boolean addGroup(String var1, String var2) throws RemoteException;

    public boolean removeGroup(String var1, String var2, String var3) throws RemoteException;

    public boolean removeAllPermissionsForGroup(String var1, String var2) throws RemoteException;

    public Vector getUserGroups(String var1, String var2) throws RemoteException;

    public boolean addUserToGroup(String var1, String var2, String var3) throws RemoteException;

    public boolean removeUserFromGroup(String var1, String var2, String var3) throws RemoteException;

    public Hashtable getUser(String var1, String var2) throws RemoteException;

    public Hashtable getUserByKey(String var1, String var2) throws RemoteException;

    public boolean hasUser(String var1, String var2) throws InvalidSessionException, RemoteException;

    public boolean addUser(String var1, Hashtable var2, String var3) throws RemoteException;

    public boolean addUser(String var1, Hashtable var2, String var3, boolean var4) throws RemoteException;

    public boolean removeUser(String var1, String var2) throws RemoteException;

    public boolean editUser(String var1, Hashtable var2) throws NotPermittedException, InvalidSessionException, RemoteException;

    public boolean deactivateUser(String var1, String var2) throws NotPermittedException, InvalidSessionException, RemoteException;

    public boolean reactivateUser(String var1, String var2) throws NotPermittedException, InvalidSessionException, RemoteException;

    public boolean isActiveUser(String var1, String var2) throws NotPermittedException, RemoteException;

    public Vector getActiveUsers(String var1, boolean var2) throws InvalidSessionException, RemoteException;

    public boolean setUserInformation(String var1, Hashtable var2) throws NotPermittedException, InvalidSessionException, RemoteException;

    public Hashtable getUserInformation(String var1, String var2) throws InvalidSessionException, RemoteException;

    public boolean changeMyPassword(String var1, String var2, String var3) throws InvalidSessionException, RemoteException;

    public boolean changeUserPassword(String var1, String var2, String var3) throws NotPermittedException, InvalidSessionException, RemoteException;

    public boolean addProfilePicture(String var1, String var2, String var3, String var4, byte[] var5) throws RemoteException;

    public boolean renameUser(String var1, String var2, String var3) throws RemoteException;

    public Vector renameUsers(String var1, Hashtable var2) throws RemoteException;

    public boolean setUserPreferenceBoolean(String var1, String var2, String var3, boolean var4) throws InvalidSessionException, RemoteException;

    public boolean getUserPreferenceBoolean(String var1, String var2, String var3) throws InvalidSessionException, RemoteException;

    public boolean setUserPreferenceLong(String var1, String var2, String var3, String var4) throws InvalidSessionException, RemoteException;

    public String getUserPreferenceLong(String var1, String var2, String var3) throws InvalidSessionException, RemoteException;

    public boolean setUserPreferenceString(String var1, String var2, String var3, String var4) throws InvalidSessionException, RemoteException;

    public String getUserPreferenceString(String var1, String var2, String var3) throws InvalidSessionException, RemoteException;

    public Vector getLabelsById(String var1, String var2) throws RemoteException;

    public Vector getMostPopularLabels(String var1, int var2) throws RemoteException;

    public Vector getMostPopularLabelsInSpace(String var1, String var2, int var3) throws RemoteException;

    public Vector getLabelContentById(String var1, String var2) throws RemoteException;

    public Vector getLabelContentByName(String var1, String var2) throws RemoteException;

    public Vector getLabelContentByObject(String var1, Hashtable var2) throws RemoteException;

    public Vector getRecentlyUsedLabels(String var1, int var2) throws InvalidSessionException, RemoteException;

    public Vector getRecentlyUsedLabelsInSpace(String var1, String var2, int var3) throws InvalidSessionException, RemoteException;

    public Vector getSpacesWithLabel(String var1, String var2) throws InvalidSessionException, RemoteException;

    public Vector getRelatedLabels(String var1, String var2, int var3) throws InvalidSessionException, RemoteException;

    public Vector getRelatedLabelsInSpace(String var1, String var2, String var3, int var4) throws InvalidSessionException, RemoteException;

    public Vector getLabelsByDetail(String var1, String var2, String var3, String var4, String var5) throws InvalidSessionException, RemoteException, NotPermittedException;

    public Vector getSpacesContainingContentWithLabel(String var1, String var2) throws InvalidSessionException, RemoteException;

    public boolean addLabelByName(String var1, String var2, String var3) throws NotPermittedException, RemoteException;

    public boolean addLabelById(String var1, String var2, String var3) throws NotPermittedException, RemoteException;

    public boolean addLabelByObject(String var1, Hashtable var2, String var3) throws NotPermittedException, RemoteException;

    public boolean addLabelByNameToSpace(String var1, String var2, String var3) throws NotPermittedException, InvalidSessionException, RemoteException;

    public boolean removeLabelByName(String var1, String var2, String var3) throws NotPermittedException, RemoteException;

    public boolean removeLabelById(String var1, String var2, String var3) throws NotPermittedException, RemoteException;

    public boolean removeLabelByObject(String var1, Hashtable var2, String var3) throws NotPermittedException, RemoteException;

    public boolean removeLabelByNameFromSpace(String var1, String var2, String var3) throws NotPermittedException, InvalidSessionException, RemoteException;

    public boolean startActivity(String var1, String var2, String var3) throws RemoteException;

    public boolean stopActivity(String var1, String var2, String var3) throws RemoteException;

    public boolean setEnableAnonymousAccess(String var1, String var2) throws RemoteException;

    public boolean isPluginEnabled(String var1, String var2) throws RemoteException;

    public boolean isPluginInstalled(String var1, String var2) throws RemoteException;

    public boolean installPlugin(String var1, String var2, byte[] var3) throws RemoteException;

    public boolean isDarkFeatureEnabled(String var1, String var2) throws RemoteException;
}

