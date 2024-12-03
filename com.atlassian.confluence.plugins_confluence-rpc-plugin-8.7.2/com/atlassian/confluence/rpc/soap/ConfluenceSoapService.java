/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.rpc.SecureRpc
 */
package com.atlassian.confluence.rpc.soap;

import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.SecureRpc;
import com.atlassian.confluence.rpc.soap.beans.RemoteAttachment;
import com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntry;
import com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntrySummary;
import com.atlassian.confluence.rpc.soap.beans.RemoteClusterInformation;
import com.atlassian.confluence.rpc.soap.beans.RemoteComment;
import com.atlassian.confluence.rpc.soap.beans.RemoteConfluenceUser;
import com.atlassian.confluence.rpc.soap.beans.RemoteContentPermission;
import com.atlassian.confluence.rpc.soap.beans.RemoteContentPermissionSet;
import com.atlassian.confluence.rpc.soap.beans.RemoteContentSummaries;
import com.atlassian.confluence.rpc.soap.beans.RemoteLabel;
import com.atlassian.confluence.rpc.soap.beans.RemoteNodeStatus;
import com.atlassian.confluence.rpc.soap.beans.RemotePage;
import com.atlassian.confluence.rpc.soap.beans.RemotePageHistory;
import com.atlassian.confluence.rpc.soap.beans.RemotePageSummary;
import com.atlassian.confluence.rpc.soap.beans.RemotePageUpdateOptions;
import com.atlassian.confluence.rpc.soap.beans.RemotePermission;
import com.atlassian.confluence.rpc.soap.beans.RemoteSearchResult;
import com.atlassian.confluence.rpc.soap.beans.RemoteServerInfo;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpace;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpaceGroup;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpacePermissionSet;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpaceSummary;
import com.atlassian.confluence.rpc.soap.beans.RemoteUser;
import com.atlassian.confluence.rpc.soap.beans.RemoteUserInformation;
import java.util.Map;

public interface ConfluenceSoapService
extends SecureRpc {
    public static final String VIEW_PERMISSION = "view";
    public static final String MODIFY_PERMISSION = "modify";
    public static final String COMMENT_PERMISSION = "comment";
    public static final String ADMIN_SPACE_PERMISSION = "admin";
    public static final String __PARANAMER_DATA = "addAnonymousPermissionToSpace java.lang.String,java.lang.String,java.lang.String token,permission,spaceKey \naddAnonymousPermissionsToSpace java.lang.String,java.lang.String,java.lang.String token,permissions,spaceKey \naddAnonymousUsePermission java.lang.String token \naddAnonymousViewUserProfilePermission java.lang.String token \naddAttachment java.lang.String,long,com.atlassian.confluence.rpc.soap.beans.RemoteAttachment,byte token,contentId,attachment,attachmentData \naddComment java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteComment token,comment \naddGlobalPermission java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName \naddGlobalPermissions java.lang.String,java.lang.String,java.lang.String token,permissions,remoteEntityName \naddGroup java.lang.String,java.lang.String token,groupname \naddLabelById java.lang.String,long,long token,labelId,objectId \naddLabelByName java.lang.String,java.lang.String,long token,labelName,objectId \naddLabelByNameToSpace java.lang.String,java.lang.String,java.lang.String token,labelName,spaceKey \naddLabelByObject java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteLabel,long token,labelObject,objectId \naddPermissionToSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName,spaceKey \naddPermissionsToSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permissions,remoteEntityName,spaceKey \naddPersonalSpace java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace,java.lang.String token,space,username \naddPersonalSpaceWithDefaultPermissions java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace,java.lang.String token,space,username \naddProfilePicture java.lang.String,java.lang.String,java.lang.String,java.lang.String,byte token,userName,fileName,mimeType,pictureData \naddSpace java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace token,space \naddSpaceGroup java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpaceGroup token,spaceGroup \naddSpaceWithDefaultPermissions java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace token,space \naddUser java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUser,java.lang.String token,remoteUser,password \naddUser java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUser,java.lang.String,boolean token,remoteUser,password,notifyUser \naddUserToGroup java.lang.String,java.lang.String,java.lang.String token,username,groupname \nchangeMyPassword java.lang.String,java.lang.String,java.lang.String token,oldPass,newPass \nchangeUserPassword java.lang.String,java.lang.String,java.lang.String token,username,newPass \nclearIndexQueue java.lang.String token \nconvertWikiToStorageFormat java.lang.String,java.lang.String token,markup \ndeactivateUser java.lang.String,java.lang.String token,username \neditComment java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteComment token,comment \neditUser java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUser token,remoteUser \nemptyTrash java.lang.String,java.lang.String token,spaceKey \nexportSite java.lang.String,boolean token,exportAttachments \nexportSpace java.lang.String,java.lang.String,java.lang.String token,spaceKey,exportType \nexportSpace java.lang.String,java.lang.String,java.lang.String,boolean token,spaceKey,exportType,exportAll \nflushIndexQueue java.lang.String token \ngetActiveUsers java.lang.String,boolean token,viewAll \ngetAncestors java.lang.String,long token,pageId \ngetAttachment java.lang.String,long,java.lang.String,int token,contentId,fileName,version \ngetAttachmentData java.lang.String,long,java.lang.String,int token,contentId,fileName,version \ngetAttachments java.lang.String,long token,pageId \ngetBlogEntries java.lang.String,java.lang.String token,spaceKey \ngetBlogEntry java.lang.String,long token,entryId \ngetBlogEntryByDateAndTitle java.lang.String,java.lang.String,int,int,int,java.lang.String token,spaceKey,year,month,dayOfMonth,postTitle \ngetBlogEntryByDayAndTitle java.lang.String,java.lang.String,int,java.lang.String token,spaceKey,dayOfMonth,postTitle \ngetChildren java.lang.String,long token,pageId \ngetClusterInformation java.lang.String token \ngetClusterNodeStatuses java.lang.String token \ngetComment java.lang.String,long token,commentId \ngetComments java.lang.String,long token,pageId \ngetContentPermissionSet java.lang.String,long,java.lang.String token,contentId,permissionType \ngetContentPermissionSets java.lang.String,long token,contentId \ngetDescendents java.lang.String,long token,pageId \ngetGroups java.lang.String token \ngetLabelContentById java.lang.String,long token,labelId \ngetLabelContentByName java.lang.String,java.lang.String token,labelName \ngetLabelContentByObject java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteLabel token,labelObject \ngetLabelsByDetail java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String token,labelName,namespace,spaceKey,owner \ngetLabelsById java.lang.String,long token,objectId \ngetMostPopularLabels java.lang.String,int token,maxCount \ngetMostPopularLabelsInSpace java.lang.String,java.lang.String,int token,spaceKey,maxCount \ngetPage java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageTitle \ngetPage java.lang.String,long token,pageId \ngetPageHistory java.lang.String,long token,pageId \ngetPagePermissions java.lang.String,long token,pageId \ngetPageSummary java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageTitle \ngetPageSummary java.lang.String,long token,pageId \ngetPages java.lang.String,java.lang.String token,spaceKey \ngetPermissions java.lang.String,java.lang.String token,spaceKey \ngetPermissionsForUser java.lang.String,java.lang.String,java.lang.String token,spaceKey,userName \ngetRecentlyUsedLabels java.lang.String,int token,maxResults \ngetRecentlyUsedLabelsInSpace java.lang.String,java.lang.String,int token,spaceKey,maxResults \ngetRelatedLabels java.lang.String,java.lang.String,int token,labelName,maxResults \ngetRelatedLabelsInSpace java.lang.String,java.lang.String,java.lang.String,int token,labelName,spaceKey,maxResults \ngetServerInfo java.lang.String token \ngetSpace java.lang.String,java.lang.String token,spaceKey \ngetSpaceGroup java.lang.String,java.lang.String token,spaceGroup \ngetSpaceGroups java.lang.String token \ngetSpaceLevelPermissions java.lang.String token \ngetSpacePermissionSet java.lang.String,java.lang.String,java.lang.String token,spaceKey,permissionType \ngetSpacePermissionSets java.lang.String,java.lang.String token,spaceKey \ngetSpaceStatus java.lang.String,java.lang.String token,spaceKey \ngetSpaces java.lang.String token \ngetSpacesContainingContentWithLabel java.lang.String,java.lang.String token,labelName \ngetSpacesInGroup java.lang.String,java.lang.String token,spaceGroupKey \ngetSpacesWithLabel java.lang.String,java.lang.String token,labelName \ngetTopLevelPages java.lang.String,java.lang.String token,spaceKey \ngetTrashContents java.lang.String,java.lang.String,int,int token,spaceKey,offset,count \ngetUserByKey java.lang.String,java.lang.String token,userKey \ngetUserByName java.lang.String,java.lang.String token,username \ngetUserGroups java.lang.String,java.lang.String token,username \ngetUserInformation java.lang.String,java.lang.String token,username \ngetUserPreferenceBoolean java.lang.String,java.lang.String,java.lang.String token,username,key \ngetUserPreferenceLong java.lang.String,java.lang.String,java.lang.String token,username,key \ngetUserPreferenceString java.lang.String,java.lang.String,java.lang.String token,username,key \ngetWatchersForPage java.lang.String,long token,pageId \ngetWatchersForSpace java.lang.String,java.lang.String token,spaceKey \nhasGroup java.lang.String,java.lang.String token,groupname \nhasUser java.lang.String,java.lang.String token,username \nimportSpace java.lang.String,byte token,importData \ninstallPlugin java.lang.String,java.lang.String,byte token,pluginFileName,pluginData \nisActiveUser java.lang.String,java.lang.String token,username \nisDarkFeatureEnabled java.lang.String,java.lang.String token,key \nisPluginEnabled java.lang.String,java.lang.String token,pluginKey \nisPluginInstalled java.lang.String,java.lang.String token,pluginKey \nisWatchingPage java.lang.String,long,java.lang.String token,pageId,username \nisWatchingSpace java.lang.String,java.lang.String,java.lang.String token,spaceKey,username \nisWatchingSpaceForType java.lang.String,java.lang.String,java.lang.String,java.lang.String token,spaceKey,contentType,username \nlogin java.lang.String,java.lang.String username,password \nlogout java.lang.String token \nmoveAttachment java.lang.String,long,java.lang.String,long,java.lang.String token,originalContentId,originalFileName,newContentId,newFileName \nmovePage java.lang.String,long,long,java.lang.String token,sourcePageId,targetPageId,position \nmovePageToTopLevel java.lang.String,long,java.lang.String token,pageId,targetSpaceKey \nperformBackup java.lang.String,boolean token,exportAttachments \npurgeFromTrash java.lang.String,java.lang.String,long token,spaceKey,pageId \nreactivateUser java.lang.String,java.lang.String token,username \nrecoverMainIndex java.lang.String token \nremoveAllPermissionsForGroup java.lang.String,java.lang.String token,groupname \nremoveAnonymousPermissionFromSpace java.lang.String,java.lang.String,java.lang.String token,permission,spaceKey \nremoveAnonymousUsePermission java.lang.String token \nremoveAnonymousViewUserProfilePermission java.lang.String token \nremoveAttachment java.lang.String,long,java.lang.String token,contentId,fileName \nremoveComment java.lang.String,long token,commentId \nremoveGlobalPermission java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName \nremoveGroup java.lang.String,java.lang.String,java.lang.String token,groupname,defaultGroupName \nremoveLabelById java.lang.String,long,long token,labelId,objectId \nremoveLabelByName java.lang.String,java.lang.String,long token,labelName,objectId \nremoveLabelByNameFromSpace java.lang.String,java.lang.String,java.lang.String token,labelName,spaceKey \nremoveLabelByObject java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteLabel,long token,labelObject,objectId \nremovePage java.lang.String,long token,pageId \nremovePageVersionById java.lang.String,long token,historicalPageId \nremovePageVersionByVersion java.lang.String,long,int token,pageId,version \nremovePageWatch java.lang.String,long token,pageId \nremovePageWatchForUser java.lang.String,long,java.lang.String token,pageId,username \nremovePermissionFromSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName,spaceKey \nremoveSpace java.lang.String,java.lang.String token,spaceKey \nremoveSpaceGroup java.lang.String,java.lang.String token,spaceGroupKey \nremoveSpaceWatch java.lang.String,java.lang.String token,spaceKey \nremoveUser java.lang.String,java.lang.String token,username \nremoveUserFromGroup java.lang.String,java.lang.String,java.lang.String token,username,groupname \nrenameUser java.lang.String,java.lang.String,java.lang.String token,oldUsername,newUsername \nrenameUsers java.lang.String,java.util.Map token,oldUsernamesToNewUsernames \nrenderContent java.lang.String,java.lang.String,long,java.lang.String token,spaceKey,pageId,newContent \nrenderContent java.lang.String,java.lang.String,long,java.lang.String,java.util.Map token,spaceKey,pageId,newContent,renderParameters \nsearch java.lang.String,java.lang.String,int token,query,maxResults \nsearch java.lang.String,java.lang.String,java.util.Map,int token,query,params,maxResults \nsetContentPermissions java.lang.String,long,java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteContentPermission token,contentId,permissionType,remoteContentPermissions \nsetEnableAnonymousAccess java.lang.String,boolean token,value \nsetSpaceStatus java.lang.String,java.lang.String,java.lang.String token,spaceKey,status \nsetUserInformation java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUserInformation token,userInfo \nsetUserPreferenceBoolean java.lang.String,java.lang.String,java.lang.String,boolean token,username,key,value \nsetUserPreferenceLong java.lang.String,java.lang.String,java.lang.String,long token,username,key,value \nsetUserPreferenceString java.lang.String,java.lang.String,java.lang.String,java.lang.String token,username,key,value \nstartActivity java.lang.String,java.lang.String,java.lang.String token,key,user \nstopActivity java.lang.String,java.lang.String,java.lang.String token,key,user \nstoreBlogEntry java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntry token,blogEntry \nstorePage java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemotePage token,page \nstoreSpace java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace token,remoteSpace \nupdatePage java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemotePage,com.atlassian.confluence.rpc.soap.beans.RemotePageUpdateOptions token,page,options \nwatchPage java.lang.String,long token,pageId \nwatchPageForUser java.lang.String,long,java.lang.String token,pageId,username \nwatchSpace java.lang.String,java.lang.String token,spaceKey \n";

    public String login(String var1, String var2) throws RemoteException;

    public boolean logout(String var1) throws RemoteException;

    public RemoteSpaceSummary[] getSpaces(String var1) throws RemoteException;

    public RemoteSpaceSummary[] getSpacesInGroup(String var1, String var2) throws RemoteException;

    public RemoteSpace addSpaceWithDefaultPermissions(String var1, RemoteSpace var2) throws RemoteException;

    public RemoteSpace addSpace(String var1, RemoteSpace var2) throws RemoteException;

    public RemoteSpace storeSpace(String var1, RemoteSpace var2) throws RemoteException;

    @Deprecated
    public RemoteSpaceGroup addSpaceGroup(String var1, RemoteSpaceGroup var2) throws RemoteException;

    @Deprecated
    public RemoteSpaceGroup getSpaceGroup(String var1, String var2) throws RemoteException;

    @Deprecated
    public RemoteSpaceGroup[] getSpaceGroups(String var1) throws RemoteException;

    @Deprecated
    public boolean removeSpaceGroup(String var1, String var2) throws RemoteException;

    public RemoteSpace addPersonalSpace(String var1, RemoteSpace var2, String var3) throws RemoteException;

    public RemoteSpace addPersonalSpaceWithDefaultPermissions(String var1, RemoteSpace var2, String var3) throws RemoteException;

    public Boolean removeSpace(String var1, String var2) throws RemoteException;

    public RemoteSpace getSpace(String var1, String var2) throws RemoteException;

    public String getSpaceStatus(String var1, String var2) throws RemoteException;

    public Boolean setSpaceStatus(String var1, String var2, String var3) throws RemoteException;

    public String[] getPermissions(String var1, String var2) throws RemoteException;

    public String[] getPermissionsForUser(String var1, String var2, String var3) throws RemoteException;

    public RemoteSpacePermissionSet[] getSpacePermissionSets(String var1, String var2) throws RemoteException;

    public RemoteSpacePermissionSet getSpacePermissionSet(String var1, String var2, String var3) throws RemoteException;

    public boolean addPermissionToSpace(String var1, String var2, String var3, String var4) throws RemoteException;

    public boolean addPermissionsToSpace(String var1, String[] var2, String var3, String var4) throws RemoteException;

    public boolean removePermissionFromSpace(String var1, String var2, String var3, String var4) throws RemoteException;

    public boolean addAnonymousPermissionToSpace(String var1, String var2, String var3) throws RemoteException;

    public boolean addAnonymousPermissionsToSpace(String var1, String[] var2, String var3) throws RemoteException;

    public boolean removeAnonymousPermissionFromSpace(String var1, String var2, String var3) throws RemoteException;

    public RemotePermission[] getPagePermissions(String var1, long var2) throws RemoteException;

    public String[] getSpaceLevelPermissions(String var1) throws RemoteException;

    public RemotePageSummary[] getPages(String var1, String var2) throws RemoteException;

    public RemotePage getPage(String var1, String var2, String var3) throws RemoteException;

    public RemotePageSummary getPageSummary(String var1, String var2, String var3) throws RemoteException;

    public RemotePage getPage(String var1, long var2) throws RemoteException;

    public RemotePageSummary getPageSummary(String var1, long var2) throws RemoteException;

    public RemotePage storePage(String var1, RemotePage var2) throws RemoteException;

    public RemotePage updatePage(String var1, RemotePage var2, RemotePageUpdateOptions var3) throws RemoteException;

    public Boolean movePageToTopLevel(String var1, long var2, String var4) throws RemoteException;

    public Boolean movePage(String var1, long var2, long var4, String var6) throws RemoteException;

    public Boolean removePage(String var1, long var2) throws RemoteException;

    public Boolean removePageVersionById(String var1, long var2) throws RemoteException;

    public Boolean removePageVersionByVersion(String var1, long var2, int var4) throws RemoteException;

    public RemoteContentSummaries getTrashContents(String var1, String var2, int var3, int var4) throws RemoteException;

    public Boolean purgeFromTrash(String var1, String var2, long var3) throws RemoteException;

    public Boolean emptyTrash(String var1, String var2) throws RemoteException;

    public RemotePageSummary[] getTopLevelPages(String var1, String var2) throws RemoteException;

    public RemotePageSummary[] getAncestors(String var1, long var2) throws RemoteException;

    public RemotePageSummary[] getChildren(String var1, long var2) throws RemoteException;

    public RemotePageSummary[] getDescendents(String var1, long var2) throws RemoteException;

    public RemoteAttachment[] getAttachments(String var1, long var2) throws RemoteException;

    public RemotePageHistory[] getPageHistory(String var1, long var2) throws RemoteException;

    public boolean watchPage(String var1, long var2) throws RemoteException;

    public boolean watchSpace(String var1, String var2) throws RemoteException;

    public boolean watchPageForUser(String var1, long var2, String var4) throws RemoteException;

    public boolean removePageWatch(String var1, long var2) throws RemoteException;

    public boolean removeSpaceWatch(String var1, String var2) throws RemoteException;

    public boolean removePageWatchForUser(String var1, long var2, String var4) throws RemoteException;

    public boolean isWatchingPage(String var1, long var2, String var4) throws RemoteException;

    public boolean isWatchingSpace(String var1, String var2, String var3) throws RemoteException;

    public boolean isWatchingSpaceForType(String var1, String var2, String var3, String var4) throws RemoteException;

    public RemoteUser[] getWatchersForPage(String var1, long var2) throws RemoteException;

    public RemoteUser[] getWatchersForSpace(String var1, String var2) throws RemoteException;

    public String renderContent(String var1, String var2, long var3, String var5) throws RemoteException;

    public String renderContent(String var1, String var2, long var3, String var5, Map var6) throws RemoteException;

    public String convertWikiToStorageFormat(String var1, String var2) throws RemoteException;

    public RemoteAttachment addAttachment(String var1, long var2, RemoteAttachment var4, byte[] var5) throws RemoteException;

    public RemoteAttachment getAttachment(String var1, long var2, String var4, int var5) throws RemoteException;

    public byte[] getAttachmentData(String var1, long var2, String var4, int var5) throws RemoteException;

    public boolean removeAttachment(String var1, long var2, String var4) throws RemoteException;

    public boolean moveAttachment(String var1, long var2, String var4, long var5, String var7) throws RemoteException;

    public RemoteComment[] getComments(String var1, long var2) throws RemoteException;

    public RemoteComment getComment(String var1, long var2) throws RemoteException;

    public RemoteComment addComment(String var1, RemoteComment var2) throws RemoteException;

    public RemoteComment editComment(String var1, RemoteComment var2) throws RemoteException;

    public boolean removeComment(String var1, long var2) throws RemoteException;

    public RemoteSearchResult[] search(String var1, String var2, int var3) throws RemoteException;

    public RemoteSearchResult[] search(String var1, String var2, Map var3, int var4) throws RemoteException;

    public RemoteBlogEntry getBlogEntryByDayAndTitle(String var1, String var2, int var3, String var4) throws RemoteException;

    public RemoteBlogEntry getBlogEntryByDateAndTitle(String var1, String var2, int var3, int var4, int var5, String var6) throws RemoteException;

    public RemoteBlogEntry getBlogEntry(String var1, long var2) throws RemoteException;

    public RemoteBlogEntrySummary[] getBlogEntries(String var1, String var2) throws RemoteException;

    public RemoteBlogEntry storeBlogEntry(String var1, RemoteBlogEntry var2) throws RemoteException;

    public RemoteServerInfo getServerInfo(String var1) throws RemoteException;

    public String exportSpace(String var1, String var2, String var3) throws RemoteException;

    public String exportSpace(String var1, String var2, String var3, boolean var4) throws RemoteException;

    public String exportSite(String var1, boolean var2) throws RemoteException;

    public String performBackup(String var1, boolean var2) throws RemoteException;

    public boolean importSpace(String var1, byte[] var2) throws RemoteException;

    public boolean flushIndexQueue(String var1) throws RemoteException;

    public boolean clearIndexQueue(String var1) throws RemoteException;

    public boolean recoverMainIndex(String var1) throws RemoteException;

    public RemoteClusterInformation getClusterInformation(String var1) throws RemoteException;

    public RemoteNodeStatus[] getClusterNodeStatuses(String var1) throws RemoteException;

    public String[] getGroups(String var1) throws RemoteException;

    public boolean hasGroup(String var1, String var2) throws RemoteException;

    public boolean addGroup(String var1, String var2) throws RemoteException;

    public boolean removeGroup(String var1, String var2, String var3) throws RemoteException;

    public boolean removeAllPermissionsForGroup(String var1, String var2) throws RemoteException;

    public String[] getUserGroups(String var1, String var2) throws RemoteException;

    public boolean addUserToGroup(String var1, String var2, String var3) throws RemoteException;

    public boolean removeUserFromGroup(String var1, String var2, String var3) throws RemoteException;

    public RemoteConfluenceUser getUserByName(String var1, String var2) throws RemoteException;

    public RemoteConfluenceUser getUserByKey(String var1, String var2) throws RemoteException;

    public boolean setUserPreferenceBoolean(String var1, String var2, String var3, boolean var4) throws RemoteException;

    public boolean getUserPreferenceBoolean(String var1, String var2, String var3) throws RemoteException;

    public boolean setUserPreferenceLong(String var1, String var2, String var3, long var4) throws RemoteException;

    public long getUserPreferenceLong(String var1, String var2, String var3) throws RemoteException;

    public boolean setUserPreferenceString(String var1, String var2, String var3, String var4) throws RemoteException;

    public String getUserPreferenceString(String var1, String var2, String var3) throws RemoteException;

    public boolean hasUser(String var1, String var2) throws RemoteException;

    public void addUser(String var1, RemoteUser var2, String var3) throws RemoteException;

    public void addUser(String var1, RemoteUser var2, String var3, boolean var4) throws RemoteException;

    public boolean removeUser(String var1, String var2) throws RemoteException;

    public boolean editUser(String var1, RemoteUser var2) throws RemoteException;

    public boolean deactivateUser(String var1, String var2) throws RemoteException;

    public boolean reactivateUser(String var1, String var2) throws RemoteException;

    public boolean isActiveUser(String var1, String var2) throws RemoteException;

    public String[] getActiveUsers(String var1, boolean var2) throws RemoteException;

    public boolean setUserInformation(String var1, RemoteUserInformation var2) throws RemoteException;

    public RemoteUserInformation getUserInformation(String var1, String var2) throws RemoteException;

    public boolean changeMyPassword(String var1, String var2, String var3) throws RemoteException;

    public boolean changeUserPassword(String var1, String var2, String var3) throws RemoteException;

    public boolean addProfilePicture(String var1, String var2, String var3, String var4, byte[] var5) throws RemoteException;

    public boolean renameUser(String var1, String var2, String var3) throws RemoteException;

    public String[] renameUsers(String var1, Map<String, String> var2) throws RemoteException;

    public RemoteLabel[] getLabelsById(String var1, long var2) throws RemoteException;

    public RemoteLabel[] getMostPopularLabels(String var1, int var2) throws RemoteException;

    public RemoteLabel[] getMostPopularLabelsInSpace(String var1, String var2, int var3) throws RemoteException;

    public RemoteLabel[] getRecentlyUsedLabels(String var1, int var2) throws RemoteException;

    public RemoteLabel[] getRecentlyUsedLabelsInSpace(String var1, String var2, int var3) throws RemoteException;

    public RemoteSpace[] getSpacesWithLabel(String var1, String var2) throws RemoteException;

    public RemoteLabel[] getRelatedLabels(String var1, String var2, int var3) throws RemoteException;

    public RemoteLabel[] getRelatedLabelsInSpace(String var1, String var2, String var3, int var4) throws RemoteException;

    public RemoteLabel[] getLabelsByDetail(String var1, String var2, String var3, String var4, String var5) throws RemoteException;

    public RemoteSearchResult[] getLabelContentById(String var1, long var2) throws RemoteException;

    public RemoteSearchResult[] getLabelContentByName(String var1, String var2) throws RemoteException;

    public RemoteSearchResult[] getLabelContentByObject(String var1, RemoteLabel var2) throws RemoteException;

    public RemoteSpace[] getSpacesContainingContentWithLabel(String var1, String var2) throws RemoteException;

    public boolean addLabelByName(String var1, String var2, long var3) throws RemoteException;

    public boolean addLabelById(String var1, long var2, long var4) throws RemoteException;

    public boolean addLabelByObject(String var1, RemoteLabel var2, long var3) throws RemoteException;

    public boolean addLabelByNameToSpace(String var1, String var2, String var3) throws RemoteException;

    public boolean removeLabelByName(String var1, String var2, long var3) throws RemoteException;

    public boolean removeLabelById(String var1, long var2, long var4) throws RemoteException;

    public boolean removeLabelByObject(String var1, RemoteLabel var2, long var3) throws RemoteException;

    public boolean removeLabelByNameFromSpace(String var1, String var2, String var3) throws RemoteException;

    public RemoteContentPermissionSet[] getContentPermissionSets(String var1, long var2) throws RemoteException;

    public RemoteContentPermissionSet getContentPermissionSet(String var1, long var2, String var4) throws RemoteException;

    public boolean setContentPermissions(String var1, long var2, String var4, RemoteContentPermission[] var5) throws RemoteException;

    public boolean addGlobalPermission(String var1, String var2, String var3) throws RemoteException;

    public boolean addGlobalPermissions(String var1, String[] var2, String var3) throws RemoteException;

    public boolean removeGlobalPermission(String var1, String var2, String var3) throws RemoteException;

    public boolean addAnonymousUsePermission(String var1) throws RemoteException;

    public boolean removeAnonymousUsePermission(String var1) throws RemoteException;

    public boolean addAnonymousViewUserProfilePermission(String var1) throws RemoteException;

    public boolean removeAnonymousViewUserProfilePermission(String var1) throws RemoteException;

    public boolean setEnableAnonymousAccess(String var1, boolean var2) throws RemoteException;

    public boolean isPluginEnabled(String var1, String var2) throws RemoteException;

    public boolean isPluginInstalled(String var1, String var2) throws RemoteException;

    public boolean installPlugin(String var1, String var2, byte[] var3) throws RemoteException;

    public boolean isDarkFeatureEnabled(String var1, String var2) throws RemoteException;

    public boolean startActivity(String var1, String var2, String var3) throws RemoteException;

    public boolean stopActivity(String var1, String var2, String var3) throws RemoteException;
}

