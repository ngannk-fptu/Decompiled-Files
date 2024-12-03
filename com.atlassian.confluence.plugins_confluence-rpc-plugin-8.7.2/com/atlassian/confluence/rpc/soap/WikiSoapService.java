/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.persistence.ContentEntityObjectDao
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.rpc.AuthenticationFailedException
 *  com.atlassian.confluence.rpc.InvalidSessionException
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.concurrent.Supplier
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc.soap;

import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.rpc.AlreadyExistsException;
import com.atlassian.confluence.rpc.AuthenticationFailedException;
import com.atlassian.confluence.rpc.InvalidSessionException;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.VersionMismatchException;
import com.atlassian.confluence.rpc.soap.ConfluenceSoapService;
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
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.Supplier;
import java.util.ArrayList;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiSoapService
implements ConfluenceSoapService {
    private static final Logger log = LoggerFactory.getLogger(WikiSoapService.class);
    private ConfluenceSoapService soapService;
    private ContentEntityObjectDao contentEntityObjectDao;
    private ExceptionTolerantMigrator wikiToXhtmlMigrator;
    private Supplier<String> unsupportedOperationMessage;
    public static final String __PARANAMER_DATA = "addAnonymousPermissionToSpace java.lang.String,java.lang.String,java.lang.String token,permission,spaceKey \naddAnonymousPermissionsToSpace java.lang.String,java.lang.String,java.lang.String token,permissions,spaceKey \naddAnonymousUsePermission java.lang.String token \naddAnonymousViewUserProfilePermission java.lang.String token \naddAttachment java.lang.String,long,com.atlassian.confluence.rpc.soap.beans.RemoteAttachment,byte token,contentId,attachment,attachmentData \naddComment java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteComment token,comment \naddGlobalPermission java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName \naddGlobalPermissions java.lang.String,java.lang.String,java.lang.String token,permissions,remoteEntityName \naddGroup java.lang.String,java.lang.String token,groupname \naddLabelById java.lang.String,long,long token,labelId,objectId \naddLabelByName java.lang.String,java.lang.String,long token,labelName,objectId \naddLabelByNameToSpace java.lang.String,java.lang.String,java.lang.String token,labelName,spaceKey \naddLabelByObject java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteLabel,long token,labelObject,objectId \naddPermissionToSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName,spaceKey \naddPermissionsToSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permissions,remoteEntityName,spaceKey \naddPersonalSpace java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace,java.lang.String token,space,username \naddPersonalSpaceWithDefaultPermissions java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace,java.lang.String token,space,username \naddProfilePicture java.lang.String,java.lang.String,java.lang.String,java.lang.String,byte token,userName,fileName,mimeType,pictureData \naddSpace java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace token,space \naddSpaceGroup java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpaceGroup token,spaceGroup \naddSpaceWithDefaultPermissions java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace token,space \naddUser java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUser,java.lang.String token,remoteUser,password \naddUser java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUser,java.lang.String,boolean token,remoteUser,password,notifyUser \naddUserToGroup java.lang.String,java.lang.String,java.lang.String token,username,groupname \nchangeMyPassword java.lang.String,java.lang.String,java.lang.String token,oldPass,newPass \nchangeUserPassword java.lang.String,java.lang.String,java.lang.String token,username,newPass \nclearIndexQueue java.lang.String token \nconvertWikiToStorageFormat java.lang.String,java.lang.String token,markup \ndeactivateUser java.lang.String,java.lang.String token,username \neditComment java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteComment token,comment \neditUser java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUser token,remoteUser \nemptyTrash java.lang.String,java.lang.String token,spaceKey \nexportSite java.lang.String,boolean token,exportAttachments \nexportSpace java.lang.String,java.lang.String,java.lang.String token,spaceKey,exportType \nexportSpace java.lang.String,java.lang.String,java.lang.String,boolean token,spaceKey,exportType,exportAll \nflushIndexQueue java.lang.String token \ngetActiveUsers java.lang.String,boolean token,viewAll \ngetAncestors java.lang.String,long token,pageId \ngetAttachment java.lang.String,long,java.lang.String,int token,entityId,fileName,version \ngetAttachmentData java.lang.String,long,java.lang.String,int token,entityId,fileName,version \ngetAttachments java.lang.String,long token,pageId \ngetBlogEntries java.lang.String,java.lang.String token,spaceKey \ngetBlogEntry java.lang.String,long token,entryId \ngetBlogEntryByDateAndTitle java.lang.String,java.lang.String,int,int,int,java.lang.String token,spaceKey,year,month,dayOfMonth,postTitle \ngetBlogEntryByDayAndTitle java.lang.String,java.lang.String,int,java.lang.String token,spaceKey,dayOfMonth,postTitle \ngetChildren java.lang.String,long token,pageId \ngetClusterInformation java.lang.String token \ngetClusterNodeStatuses java.lang.String token \ngetComment java.lang.String,long token,commentId \ngetComments java.lang.String,long token,pageId \ngetContentPermissionSet java.lang.String,long,java.lang.String token,contentId,permissionType \ngetContentPermissionSets java.lang.String,long token,contentId \ngetDescendents java.lang.String,long token,pageId \ngetGroups java.lang.String token \ngetLabelContentById java.lang.String,long token,labelId \ngetLabelContentByName java.lang.String,java.lang.String token,labelName \ngetLabelContentByObject java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteLabel token,labelObject \ngetLabelsByDetail java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String token,labelName,namespace,spaceKey,owner \ngetLabelsById java.lang.String,long token,objectId \ngetMostPopularLabels java.lang.String,int token,maxCount \ngetMostPopularLabelsInSpace java.lang.String,java.lang.String,int token,spaceKey,maxCount \ngetPage java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageTitle \ngetPage java.lang.String,long token,pageId \ngetPageHistory java.lang.String,long token,pageId \ngetPagePermissions java.lang.String,long token,pageId \ngetPageSummary java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageTitle \ngetPageSummary java.lang.String,long token,pageId \ngetPages java.lang.String,java.lang.String token,spaceKey \ngetPermissions java.lang.String,java.lang.String token,spaceKey \ngetPermissionsForUser java.lang.String,java.lang.String,java.lang.String token,spaceKey,userName \ngetRecentlyUsedLabels java.lang.String,int token,maxResults \ngetRecentlyUsedLabelsInSpace java.lang.String,java.lang.String,int token,spaceKey,maxResults \ngetRelatedLabels java.lang.String,java.lang.String,int token,labelName,maxResults \ngetRelatedLabelsInSpace java.lang.String,java.lang.String,java.lang.String,int token,labelName,spaceKey,maxResults \ngetServerInfo java.lang.String token \ngetSpace java.lang.String,java.lang.String token,spaceKey \ngetSpaceGroup java.lang.String,java.lang.String token,spaceGroup \ngetSpaceGroups java.lang.String token \ngetSpaceLevelPermissions java.lang.String token \ngetSpacePermissionSet java.lang.String,java.lang.String,java.lang.String token,spaceKey,permissionType \ngetSpacePermissionSets java.lang.String,java.lang.String token,spaceKey \ngetSpaceStatus java.lang.String,java.lang.String token,spaceKey \ngetSpaces java.lang.String token \ngetSpacesContainingContentWithLabel java.lang.String,java.lang.String token,labelName \ngetSpacesInGroup java.lang.String,java.lang.String token,spaceGroupKey \ngetSpacesWithLabel java.lang.String,java.lang.String token,labelName \ngetTopLevelPages java.lang.String,java.lang.String token,spaceKey \ngetTrashContents java.lang.String,java.lang.String,int,int token,spaceKey,offset,count \ngetUserByKey java.lang.String,java.lang.String token,key \ngetUserByName java.lang.String,java.lang.String token,username \ngetUserGroups java.lang.String,java.lang.String token,username \ngetUserInformation java.lang.String,java.lang.String token,username \ngetUserPreferenceBoolean java.lang.String,java.lang.String,java.lang.String token,username,key \ngetUserPreferenceLong java.lang.String,java.lang.String,java.lang.String token,username,key \ngetUserPreferenceString java.lang.String,java.lang.String,java.lang.String token,username,key \ngetWatchersForPage java.lang.String,long token,pageId \ngetWatchersForSpace java.lang.String,java.lang.String token,spaceKey \nhasGroup java.lang.String,java.lang.String token,groupname \nhasUser java.lang.String,java.lang.String token,username \nimportSpace java.lang.String,byte token,importData \ninstallPlugin java.lang.String,java.lang.String,byte token,pluginFileName,pluginData \nisActiveUser java.lang.String,java.lang.String token,username \nisDarkFeatureEnabled java.lang.String,java.lang.String token,key \nisPluginEnabled java.lang.String,java.lang.String token,pluginKey \nisPluginInstalled java.lang.String,java.lang.String token,pluginKey \nisWatchingPage java.lang.String,long,java.lang.String token,pageId,username \nisWatchingSpace java.lang.String,java.lang.String,java.lang.String token,spaceKey,username \nisWatchingSpaceForType java.lang.String,java.lang.String,java.lang.String,java.lang.String token,spaceKey,contentType,username \nlogin java.lang.String,java.lang.String username,password \nlogout java.lang.String token \nmoveAttachment java.lang.String,long,java.lang.String,long,java.lang.String token,originalContentId,originalFileName,newContentId,newFileName \nmovePage java.lang.String,long,long,java.lang.String token,sourcePageId,targetPageId,position \nmovePageToTopLevel java.lang.String,long,java.lang.String token,pageId,targetSpaceKey \nperformBackup java.lang.String,boolean token,exportAttachments \npurgeFromTrash java.lang.String,java.lang.String,long token,spaceKey,pageId \nreactivateUser java.lang.String,java.lang.String token,username \nrecoverMainIndex java.lang.String token \nremoveAllPermissionsForGroup java.lang.String,java.lang.String token,groupname \nremoveAnonymousPermissionFromSpace java.lang.String,java.lang.String,java.lang.String token,permission,spaceKey \nremoveAnonymousUsePermission java.lang.String token \nremoveAnonymousViewUserProfilePermission java.lang.String token \nremoveAttachment java.lang.String,long,java.lang.String token,contentId,fileName \nremoveComment java.lang.String,long token,commentId \nremoveGlobalPermission java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName \nremoveGroup java.lang.String,java.lang.String,java.lang.String token,groupname,defaultGroupName \nremoveLabelById java.lang.String,long,long token,labelId,objectId \nremoveLabelByName java.lang.String,java.lang.String,long token,labelName,objectId \nremoveLabelByNameFromSpace java.lang.String,java.lang.String,java.lang.String token,labelName,spaceKey \nremoveLabelByObject java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteLabel,long token,labelObject,objectId \nremovePage java.lang.String,long token,pageId \nremovePageVersionById java.lang.String,long token,historicalPageId \nremovePageVersionByVersion java.lang.String,long,int token,pageId,version \nremovePageWatch java.lang.String,long token,pageId \nremovePageWatchForUser java.lang.String,long,java.lang.String token,pageId,username \nremovePermissionFromSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName,spaceKey \nremoveSpace java.lang.String,java.lang.String token,spaceKey \nremoveSpaceGroup java.lang.String,java.lang.String token,spaceGroupKey \nremoveSpaceWatch java.lang.String,java.lang.String token,spaceKey \nremoveUser java.lang.String,java.lang.String token,username \nremoveUserFromGroup java.lang.String,java.lang.String,java.lang.String token,username,groupname \nrenameUser java.lang.String,java.lang.String,java.lang.String token,oldUsername,newUsername \nrenameUsers java.lang.String,java.util.Map token,oldUsernamesToNewUsernames \nrenderContent java.lang.String,java.lang.String,long,java.lang.String token,spaceKey,pageId,newContent \nrenderContent java.lang.String,java.lang.String,long,java.lang.String,java.util.Map token,spaceKey,pageId,newContent,renderParameters \nsearch java.lang.String,java.lang.String,int token,query,maxResults \nsearch java.lang.String,java.lang.String,java.util.Map,int token,query,params,maxResults \nsetContentEntityObjectDao com.atlassian.confluence.core.persistence.ContentEntityObjectDao contentEntityObjectDao \nsetContentPermissions java.lang.String,long,java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteContentPermission token,contentId,permissionType,remoteContentPermissions \nsetEnableAnonymousAccess java.lang.String,boolean token,value \nsetSettingsManager com.atlassian.confluence.setup.settings.SettingsManager settingsManager \nsetSpaceStatus java.lang.String,java.lang.String,java.lang.String token,spaceKey,status \nsetUserInformation java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUserInformation token,userInfo \nsetUserPreferenceBoolean java.lang.String,java.lang.String,java.lang.String,boolean token,username,key,value \nsetUserPreferenceLong java.lang.String,java.lang.String,java.lang.String,long token,username,key,value \nsetUserPreferenceString java.lang.String,java.lang.String,java.lang.String,java.lang.String token,username,key,value \nsetWikiToXhtmlMigrator com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator wikiToXhtmlMigrator \nsetXhtmlSoapService com.atlassian.confluence.rpc.soap.ConfluenceSoapService soapService \nstartActivity java.lang.String,java.lang.String,java.lang.String token,key,user \nstopActivity java.lang.String,java.lang.String,java.lang.String token,key,user \nstoreBlogEntry java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntry token,blogEntry \nstorePage java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemotePage token,page \nstoreSpace java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace token,remoteSpace \nupdatePage java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemotePage,com.atlassian.confluence.rpc.soap.beans.RemotePageUpdateOptions token,page,options \nwatchPage java.lang.String,long token,pageId \nwatchPageForUser java.lang.String,long,java.lang.String token,pageId,username \nwatchSpace java.lang.String,java.lang.String token,spaceKey \n";

    @Override
    public RemoteBlogEntry getBlogEntryByDayAndTitle(String token, String spaceKey, int dayOfMonth, String postTitle) throws RemoteException {
        throw new RemoteException((String)this.unsupportedOperationMessage.get());
    }

    @Override
    public RemoteBlogEntry getBlogEntryByDateAndTitle(String token, String spaceKey, int year, int month, int dayOfMonth, String postTitle) throws RemoteException {
        throw new RemoteException((String)this.unsupportedOperationMessage.get());
    }

    @Override
    public RemoteBlogEntry getBlogEntry(String token, long entryId) throws RemoteException {
        throw new RemoteException((String)this.unsupportedOperationMessage.get());
    }

    @Override
    public RemotePage getPage(String token, String spaceKey, String pageTitle) throws InvalidSessionException, RemoteException {
        throw new RemoteException((String)this.unsupportedOperationMessage.get() + " Please use getPageSummary() to get page data without its content.");
    }

    @Override
    public RemotePageSummary getPageSummary(String token, String spaceKey, String pageTitle) throws InvalidSessionException, RemoteException {
        return this.soapService.getPageSummary(token, spaceKey, pageTitle);
    }

    @Override
    public RemotePage getPage(String token, long pageId) throws InvalidSessionException, RemoteException {
        throw new RemoteException((String)this.unsupportedOperationMessage.get());
    }

    @Override
    public RemotePageSummary getPageSummary(String token, long pageId) throws InvalidSessionException, RemoteException {
        return this.soapService.getPageSummary(token, pageId);
    }

    @Override
    public RemoteComment[] getComments(String token, long pageId) throws InvalidSessionException, RemoteException {
        throw new RemoteException((String)this.unsupportedOperationMessage.get());
    }

    @Override
    public RemoteComment getComment(String token, long commentId) throws InvalidSessionException, RemoteException {
        throw new RemoteException((String)this.unsupportedOperationMessage.get());
    }

    @Override
    public String login(String username, String password) throws AuthenticationFailedException, RemoteException {
        return this.soapService.login(username, password);
    }

    @Override
    public boolean logout(String token) throws RemoteException {
        return this.soapService.logout(token);
    }

    @Override
    public RemoteSpaceSummary[] getSpaces(String token) throws InvalidSessionException, RemoteException {
        return this.soapService.getSpaces(token);
    }

    @Override
    public RemoteSpaceSummary[] getSpacesInGroup(String token, String spaceGroupKey) throws RemoteException {
        return new RemoteSpaceSummary[0];
    }

    @Override
    public RemoteSpace addSpaceWithDefaultPermissions(String token, RemoteSpace space) throws NotPermittedException, InvalidSessionException, AlreadyExistsException, RemoteException {
        return this.soapService.addSpace(token, space);
    }

    @Override
    public RemoteSpace addSpace(String token, RemoteSpace space) throws NotPermittedException, InvalidSessionException, AlreadyExistsException, RemoteException {
        return this.soapService.addSpace(token, space);
    }

    @Override
    public RemoteSpace storeSpace(String token, RemoteSpace remoteSpace) throws RemoteException {
        return this.soapService.storeSpace(token, remoteSpace);
    }

    @Override
    public RemoteSpaceGroup addSpaceGroup(String token, RemoteSpaceGroup spaceGroup) throws NotPermittedException, InvalidSessionException, AlreadyExistsException, RemoteException {
        return this.soapService.addSpaceGroup(token, spaceGroup);
    }

    @Override
    public RemoteSpaceGroup getSpaceGroup(String token, String spaceGroup) throws NotPermittedException, InvalidSessionException, AlreadyExistsException, RemoteException {
        return this.soapService.getSpaceGroup(token, spaceGroup);
    }

    @Override
    public RemoteSpaceGroup[] getSpaceGroups(String token) throws InvalidSessionException, RemoteException {
        return this.soapService.getSpaceGroups(token);
    }

    @Override
    public boolean removeSpaceGroup(String token, String spaceGroupKey) throws RemoteException {
        return this.soapService.removeSpaceGroup(token, spaceGroupKey);
    }

    @Override
    public RemoteSpace addPersonalSpace(String token, RemoteSpace space, String username) throws NotPermittedException, InvalidSessionException, AlreadyExistsException, RemoteException {
        return this.soapService.addPersonalSpace(token, space, username);
    }

    @Override
    public RemoteSpace addPersonalSpaceWithDefaultPermissions(String token, RemoteSpace space, String username) throws NotPermittedException, InvalidSessionException, AlreadyExistsException, RemoteException {
        return this.soapService.addPersonalSpaceWithDefaultPermissions(token, space, username);
    }

    @Override
    public Boolean removeSpace(String token, String spaceKey) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removeSpace(token, spaceKey);
    }

    @Override
    public RemoteSpace getSpace(String token, String spaceKey) throws InvalidSessionException, RemoteException {
        return this.soapService.getSpace(token, spaceKey);
    }

    @Override
    public String[] getPermissions(String token, String spaceKey) throws InvalidSessionException, RemoteException {
        return this.soapService.getPermissions(token, spaceKey);
    }

    @Override
    public String[] getPermissionsForUser(String token, String spaceKey, String userName) throws InvalidSessionException, RemoteException {
        return this.soapService.getPermissionsForUser(token, spaceKey, userName);
    }

    @Override
    public RemoteSpacePermissionSet[] getSpacePermissionSets(String token, String spaceKey) throws InvalidSessionException, NotPermittedException, RemoteException {
        return this.soapService.getSpacePermissionSets(token, spaceKey);
    }

    @Override
    public RemoteSpacePermissionSet getSpacePermissionSet(String token, String spaceKey, String permissionType) throws InvalidSessionException, NotPermittedException, RemoteException {
        return this.soapService.getSpacePermissionSet(token, spaceKey, permissionType);
    }

    @Override
    public boolean addPermissionToSpace(String token, String permission, String remoteEntityName, String spaceKey) throws RemoteException {
        return this.soapService.addPermissionToSpace(token, permission, remoteEntityName, spaceKey);
    }

    @Override
    public boolean addPermissionsToSpace(String token, String[] permissions, String remoteEntityName, String spaceKey) throws RemoteException {
        return this.soapService.addPermissionsToSpace(token, permissions, remoteEntityName, spaceKey);
    }

    @Override
    public boolean removePermissionFromSpace(String token, String permission, String remoteEntityName, String spaceKey) throws NotPermittedException, RemoteException {
        return this.soapService.removePermissionFromSpace(token, permission, remoteEntityName, spaceKey);
    }

    @Override
    public boolean addAnonymousPermissionToSpace(String token, String permission, String spaceKey) throws RemoteException {
        return this.soapService.addAnonymousPermissionToSpace(token, permission, spaceKey);
    }

    @Override
    public boolean addAnonymousPermissionsToSpace(String token, String[] permissions, String spaceKey) throws RemoteException {
        return this.soapService.addAnonymousPermissionsToSpace(token, permissions, spaceKey);
    }

    @Override
    public boolean removeAnonymousPermissionFromSpace(String token, String permission, String spaceKey) throws NotPermittedException, RemoteException {
        return this.soapService.removeAnonymousPermissionFromSpace(token, permission, spaceKey);
    }

    @Override
    public String[] getSpaceLevelPermissions(String token) throws RemoteException {
        return this.soapService.getSpaceLevelPermissions(token);
    }

    @Override
    public RemotePageSummary[] getPages(String token, String spaceKey) throws InvalidSessionException, RemoteException {
        return this.soapService.getPages(token, spaceKey);
    }

    @Override
    public RemoteComment addComment(String token, RemoteComment comment) throws InvalidSessionException, NotPermittedException, RemoteException {
        ContentEntityObject owningContent = this.contentEntityObjectDao.getById(comment.getPageId());
        String storageFormattedContent = this.convertToStorage(new PageContext(owningContent), comment.getContent());
        comment.setContent(storageFormattedContent);
        return this.soapService.addComment(token, comment);
    }

    @Override
    public RemoteComment editComment(String token, RemoteComment comment) throws InvalidSessionException, NotPermittedException, RemoteException {
        ContentEntityObject owningContent = this.contentEntityObjectDao.getById(comment.getPageId());
        String storageFormattedContent = this.convertToStorage(new PageContext(owningContent), comment.getContent());
        comment.setContent(storageFormattedContent);
        return this.soapService.editComment(token, comment);
    }

    @Override
    public boolean removeComment(String token, long commentId) throws InvalidSessionException, NotPermittedException, RemoteException {
        return this.soapService.removeComment(token, commentId);
    }

    @Override
    public RemotePageSummary[] getTopLevelPages(String token, String spaceKey) throws RemoteException {
        return this.soapService.getTopLevelPages(token, spaceKey);
    }

    @Override
    public RemotePageSummary[] getAncestors(String token, long pageId) throws InvalidSessionException, RemoteException {
        return this.soapService.getAncestors(token, pageId);
    }

    @Override
    public RemotePageSummary[] getChildren(String token, long pageId) throws InvalidSessionException, RemoteException {
        return this.soapService.getChildren(token, pageId);
    }

    @Override
    public RemotePageSummary[] getDescendents(String token, long pageId) throws InvalidSessionException, RemoteException {
        return this.soapService.getDescendents(token, pageId);
    }

    @Override
    public RemoteAttachment[] getAttachments(String token, long pageId) throws InvalidSessionException, RemoteException {
        return this.soapService.getAttachments(token, pageId);
    }

    @Override
    public RemotePageHistory[] getPageHistory(String token, long pageId) throws InvalidSessionException, RemoteException {
        return this.soapService.getPageHistory(token, pageId);
    }

    @Override
    public boolean watchPage(String token, long pageId) throws RemoteException {
        return this.soapService.watchPage(token, pageId);
    }

    @Override
    public boolean watchSpace(String token, String spaceKey) throws RemoteException {
        return this.soapService.watchSpace(token, spaceKey);
    }

    @Override
    public boolean watchPageForUser(String token, long pageId, String username) throws RemoteException {
        return this.soapService.watchPageForUser(token, pageId, username);
    }

    @Override
    public boolean removePageWatch(String token, long pageId) throws RemoteException {
        return this.soapService.removePageWatch(token, pageId);
    }

    @Override
    public boolean removeSpaceWatch(String token, String spaceKey) throws RemoteException {
        return this.soapService.removeSpaceWatch(token, spaceKey);
    }

    @Override
    public boolean removePageWatchForUser(String token, long pageId, String username) throws RemoteException {
        return this.soapService.removePageWatchForUser(token, pageId, username);
    }

    @Override
    public boolean isWatchingPage(String token, long pageId, String username) throws RemoteException {
        return this.soapService.isWatchingPage(token, pageId, username);
    }

    @Override
    public boolean isWatchingSpace(String token, String spaceKey, String username) throws RemoteException {
        return this.soapService.isWatchingSpace(token, spaceKey, username);
    }

    @Override
    public boolean isWatchingSpaceForType(String token, String spaceKey, String contentType, String username) throws RemoteException {
        return this.soapService.isWatchingSpaceForType(token, spaceKey, contentType, username);
    }

    @Override
    public RemoteUser[] getWatchersForPage(String token, long pageId) throws RemoteException {
        return this.soapService.getWatchersForPage(token, pageId);
    }

    @Override
    public RemoteUser[] getWatchersForSpace(String token, String spaceKey) throws RemoteException {
        return this.soapService.getWatchersForSpace(token, spaceKey);
    }

    @Override
    public String renderContent(String token, String spaceKey, long pageId, String newContent) throws InvalidSessionException, RemoteException {
        return this.soapService.renderContent(token, spaceKey, pageId, newContent);
    }

    @Override
    public String renderContent(String token, String spaceKey, long pageId, String newContent, Map renderParameters) throws RemoteException {
        return this.soapService.renderContent(token, spaceKey, pageId, newContent, renderParameters);
    }

    @Override
    public String convertWikiToStorageFormat(String token, String markup) throws RemoteException {
        return this.soapService.convertWikiToStorageFormat(token, markup);
    }

    @Override
    public RemoteAttachment addAttachment(String token, long contentId, RemoteAttachment attachment, byte[] attachmentData) throws RemoteException {
        return this.soapService.addAttachment(token, contentId, attachment, attachmentData);
    }

    @Override
    public RemoteAttachment getAttachment(String token, long entityId, String fileName, int version) throws RemoteException {
        return this.soapService.getAttachment(token, entityId, fileName, version);
    }

    @Override
    public byte[] getAttachmentData(String token, long entityId, String fileName, int version) throws RemoteException {
        return this.soapService.getAttachmentData(token, entityId, fileName, version);
    }

    @Override
    public boolean removeAttachment(String token, long contentId, String fileName) throws RemoteException {
        return this.soapService.removeAttachment(token, contentId, fileName);
    }

    @Override
    public boolean moveAttachment(String token, long originalContentId, String originalFileName, long newContentId, String newFileName) throws RemoteException {
        return this.soapService.moveAttachment(token, originalContentId, originalFileName, newContentId, newFileName);
    }

    @Override
    public RemotePage storePage(String token, RemotePage page) throws VersionMismatchException, NotPermittedException, InvalidSessionException, RemoteException {
        ContentEntityObject pageCeo = this.contentEntityObjectDao.getById(page.getId());
        PageContext pageContext = pageCeo != null ? new PageContext(pageCeo) : new PageContext(page.getSpace());
        String storageFormattedContent = this.convertToStorage(pageContext, page.getContent());
        page.setContent(storageFormattedContent);
        return this.soapService.storePage(token, page);
    }

    @Override
    public RemotePage updatePage(String token, RemotePage page, RemotePageUpdateOptions options) throws VersionMismatchException, NotPermittedException, InvalidSessionException, RemoteException {
        ContentEntityObject pageCeo = this.contentEntityObjectDao.getById(page.getId());
        String storageFormattedContent = this.convertToStorage(new PageContext(pageCeo), page.getContent());
        page.setContent(storageFormattedContent);
        return this.soapService.updatePage(token, page, options);
    }

    @Override
    public Boolean movePageToTopLevel(String token, long pageId, String targetSpaceKey) throws RemoteException {
        return this.soapService.movePageToTopLevel(token, pageId, targetSpaceKey);
    }

    @Override
    public Boolean movePage(String token, long sourcePageId, long targetPageId, String position) throws RemoteException {
        return this.soapService.movePage(token, sourcePageId, targetPageId, position);
    }

    @Override
    public Boolean removePage(String token, long pageId) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removePage(token, pageId);
    }

    @Override
    public Boolean removePageVersionById(String token, long historicalPageId) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removePageVersionById(token, historicalPageId);
    }

    @Override
    public Boolean removePageVersionByVersion(String token, long pageId, int version) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removePageVersionByVersion(token, pageId, version);
    }

    @Override
    public RemoteSearchResult[] search(String token, String query, int maxResults) throws InvalidSessionException, RemoteException {
        return this.soapService.search(token, query, maxResults);
    }

    @Override
    public RemoteSearchResult[] search(String token, String query, Map params, int maxResults) throws RemoteException {
        return this.soapService.search(token, query, params, maxResults);
    }

    @Override
    public RemoteBlogEntrySummary[] getBlogEntries(String token, String spaceKey) throws InvalidSessionException, RemoteException {
        return this.soapService.getBlogEntries(token, spaceKey);
    }

    @Override
    public RemoteBlogEntry storeBlogEntry(String token, RemoteBlogEntry blogEntry) throws VersionMismatchException, NotPermittedException, InvalidSessionException, RemoteException {
        ContentEntityObject pageCeo = this.contentEntityObjectDao.getById(blogEntry.getId());
        PageContext pageContext = pageCeo != null ? new PageContext(pageCeo) : new PageContext(blogEntry.getSpace());
        String storageFormattedContent = this.convertToStorage(pageContext, blogEntry.getContent());
        blogEntry.setContent(storageFormattedContent);
        return this.soapService.storeBlogEntry(token, blogEntry);
    }

    @Override
    public RemoteServerInfo getServerInfo(String token) throws InvalidSessionException, RemoteException {
        return this.soapService.getServerInfo(token);
    }

    @Override
    public RemotePermission[] getPagePermissions(String token, long pageId) throws InvalidSessionException, RemoteException {
        return this.soapService.getPagePermissions(token, pageId);
    }

    @Override
    public String exportSpace(String token, String spaceKey, String exportType) throws RemoteException {
        return this.soapService.exportSpace(token, spaceKey, exportType);
    }

    @Override
    public String exportSpace(String token, String spaceKey, String exportType, boolean exportAll) throws RemoteException {
        return this.soapService.exportSpace(token, spaceKey, exportType, exportAll);
    }

    @Override
    public String exportSite(String token, boolean exportAttachments) throws RemoteException {
        return this.soapService.exportSite(token, exportAttachments);
    }

    @Override
    public String performBackup(String token, boolean exportAttachments) throws RemoteException {
        return this.soapService.performBackup(token, exportAttachments);
    }

    @Override
    public boolean importSpace(String token, byte[] importData) throws RemoteException {
        return this.soapService.importSpace(token, importData);
    }

    @Override
    public boolean flushIndexQueue(String token) throws RemoteException {
        return this.soapService.flushIndexQueue(token);
    }

    @Override
    public boolean clearIndexQueue(String token) throws RemoteException {
        return this.soapService.clearIndexQueue(token);
    }

    @Override
    public boolean recoverMainIndex(String token) throws RemoteException {
        return this.soapService.recoverMainIndex(token);
    }

    @Override
    public RemoteClusterInformation getClusterInformation(String token) throws RemoteException {
        return this.soapService.getClusterInformation(token);
    }

    @Override
    public RemoteNodeStatus[] getClusterNodeStatuses(String token) throws RemoteException {
        return this.soapService.getClusterNodeStatuses(token);
    }

    @Override
    public String[] getGroups(String token) throws RemoteException {
        return this.soapService.getGroups(token);
    }

    @Override
    public boolean hasGroup(String token, String groupname) throws InvalidSessionException, RemoteException {
        return this.soapService.hasGroup(token, groupname);
    }

    @Override
    public boolean addGroup(String token, String groupname) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.addGroup(token, groupname);
    }

    @Override
    public boolean removeGroup(String token, String groupname, String defaultGroupName) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removeGroup(token, groupname, defaultGroupName);
    }

    @Override
    public boolean removeAllPermissionsForGroup(String token, String groupname) throws RemoteException {
        return this.soapService.removeAllPermissionsForGroup(token, groupname);
    }

    @Override
    public String[] getUserGroups(String token, String username) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.getUserGroups(token, username);
    }

    @Override
    public boolean addUserToGroup(String token, String username, String groupname) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.addUserToGroup(token, username, groupname);
    }

    @Override
    public boolean removeUserFromGroup(String token, String username, String groupname) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removeUserFromGroup(token, username, groupname);
    }

    @Override
    public RemoteConfluenceUser getUserByName(String token, String username) throws InvalidSessionException, RemoteException {
        return this.soapService.getUserByName(token, username);
    }

    @Override
    public RemoteConfluenceUser getUserByKey(String token, String key) throws InvalidSessionException, RemoteException {
        return this.soapService.getUserByKey(token, key);
    }

    @Override
    public boolean setUserPreferenceBoolean(String token, String username, String key, boolean value) throws InvalidSessionException, RemoteException {
        return this.soapService.setUserPreferenceBoolean(token, username, key, value);
    }

    @Override
    public boolean getUserPreferenceBoolean(String token, String username, String key) throws InvalidSessionException, RemoteException {
        return this.soapService.getUserPreferenceBoolean(token, username, key);
    }

    @Override
    public boolean setUserPreferenceLong(String token, String username, String key, long value) throws InvalidSessionException, RemoteException {
        return this.soapService.setUserPreferenceLong(token, username, key, value);
    }

    @Override
    public long getUserPreferenceLong(String token, String username, String key) throws InvalidSessionException, RemoteException {
        return this.soapService.getUserPreferenceLong(token, username, key);
    }

    @Override
    public boolean setUserPreferenceString(String token, String username, String key, String value) throws InvalidSessionException, RemoteException {
        return this.soapService.setUserPreferenceString(token, username, key, value);
    }

    @Override
    public String getUserPreferenceString(String token, String username, String key) throws InvalidSessionException, RemoteException {
        return this.soapService.getUserPreferenceString(token, username, key);
    }

    @Override
    public boolean hasUser(String token, String username) throws InvalidSessionException, RemoteException {
        return this.soapService.hasUser(token, username);
    }

    @Override
    public void addUser(String token, RemoteUser remoteUser, String password) throws NotPermittedException, InvalidSessionException, RemoteException {
        this.soapService.addUser(token, remoteUser, password);
    }

    @Override
    public void addUser(String token, RemoteUser remoteUser, String password, boolean notifyUser) throws NotPermittedException, InvalidSessionException, RemoteException {
        this.soapService.addUser(token, remoteUser, password, notifyUser);
    }

    @Override
    public boolean removeUser(String token, String username) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removeUser(token, username);
    }

    @Override
    public boolean editUser(String token, RemoteUser remoteUser) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.editUser(token, remoteUser);
    }

    @Override
    public boolean deactivateUser(String token, String username) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.deactivateUser(token, username);
    }

    @Override
    public boolean reactivateUser(String token, String username) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.reactivateUser(token, username);
    }

    @Override
    public boolean isActiveUser(String token, String username) throws NotPermittedException, RemoteException {
        return this.soapService.isActiveUser(token, username);
    }

    @Override
    public String[] getActiveUsers(String token, boolean viewAll) throws InvalidSessionException, RemoteException {
        return this.soapService.getActiveUsers(token, viewAll);
    }

    @Override
    public boolean setUserInformation(String token, RemoteUserInformation userInfo) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.setUserInformation(token, userInfo);
    }

    @Override
    public RemoteUserInformation getUserInformation(String token, String username) throws InvalidSessionException, RemoteException {
        return this.soapService.getUserInformation(token, username);
    }

    @Override
    public boolean changeMyPassword(String token, String oldPass, String newPass) throws InvalidSessionException, RemoteException {
        return this.soapService.changeMyPassword(token, oldPass, newPass);
    }

    @Override
    public boolean changeUserPassword(String token, String username, String newPass) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.changeUserPassword(token, username, newPass);
    }

    @Override
    public boolean addProfilePicture(String token, String userName, String fileName, String mimeType, byte[] pictureData) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.addProfilePicture(token, userName, fileName, mimeType, pictureData);
    }

    @Override
    public boolean renameUser(String token, String oldUsername, String newUsername) throws RemoteException {
        return this.soapService.renameUser(token, oldUsername, newUsername);
    }

    @Override
    public String[] renameUsers(String token, Map<String, String> oldUsernamesToNewUsernames) throws RemoteException {
        return this.soapService.renameUsers(token, oldUsernamesToNewUsernames);
    }

    @Override
    public RemoteLabel[] getLabelsById(String token, long objectId) throws InvalidSessionException, RemoteException {
        return this.soapService.getLabelsById(token, objectId);
    }

    @Override
    public RemoteLabel[] getMostPopularLabels(String token, int maxCount) throws InvalidSessionException, RemoteException {
        return this.soapService.getMostPopularLabels(token, maxCount);
    }

    @Override
    public RemoteLabel[] getMostPopularLabelsInSpace(String token, String spaceKey, int maxCount) throws InvalidSessionException, RemoteException {
        return this.soapService.getMostPopularLabelsInSpace(token, spaceKey, maxCount);
    }

    @Override
    public RemoteLabel[] getRecentlyUsedLabels(String token, int maxResults) throws InvalidSessionException, RemoteException {
        return this.soapService.getRecentlyUsedLabels(token, maxResults);
    }

    @Override
    public RemoteLabel[] getRecentlyUsedLabelsInSpace(String token, String spaceKey, int maxResults) throws InvalidSessionException, RemoteException {
        return this.soapService.getRecentlyUsedLabelsInSpace(token, spaceKey, maxResults);
    }

    @Override
    public RemoteSpace[] getSpacesWithLabel(String token, String labelName) throws InvalidSessionException, RemoteException {
        return this.soapService.getSpacesWithLabel(token, labelName);
    }

    @Override
    public RemoteLabel[] getRelatedLabels(String token, String labelName, int maxResults) throws InvalidSessionException, RemoteException {
        return this.soapService.getRelatedLabels(token, labelName, maxResults);
    }

    @Override
    public RemoteLabel[] getRelatedLabelsInSpace(String token, String labelName, String spaceKey, int maxResults) throws InvalidSessionException, RemoteException {
        return this.soapService.getRelatedLabelsInSpace(token, labelName, spaceKey, maxResults);
    }

    @Override
    public RemoteLabel[] getLabelsByDetail(String token, String labelName, String namespace, String spaceKey, String owner) throws InvalidSessionException, RemoteException, NotPermittedException {
        return this.soapService.getLabelsByDetail(token, labelName, namespace, spaceKey, owner);
    }

    @Override
    public RemoteSearchResult[] getLabelContentById(String token, long labelId) throws InvalidSessionException, RemoteException {
        return this.soapService.getLabelContentById(token, labelId);
    }

    @Override
    public RemoteSearchResult[] getLabelContentByName(String token, String labelName) throws InvalidSessionException, RemoteException {
        return this.soapService.getLabelContentByName(token, labelName);
    }

    @Override
    public RemoteSearchResult[] getLabelContentByObject(String token, RemoteLabel labelObject) throws InvalidSessionException, RemoteException {
        return this.soapService.getLabelContentByObject(token, labelObject);
    }

    @Override
    public RemoteSpace[] getSpacesContainingContentWithLabel(String token, String labelName) throws InvalidSessionException, RemoteException {
        return this.soapService.getSpacesContainingContentWithLabel(token, labelName);
    }

    @Override
    public boolean addLabelByName(String token, String labelName, long objectId) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.addLabelByName(token, labelName, objectId);
    }

    @Override
    public boolean addLabelById(String token, long labelId, long objectId) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.addLabelById(token, labelId, objectId);
    }

    @Override
    public boolean addLabelByObject(String token, RemoteLabel labelObject, long objectId) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.addLabelByObject(token, labelObject, objectId);
    }

    @Override
    public boolean addLabelByNameToSpace(String token, String labelName, String spaceKey) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.addLabelByNameToSpace(token, labelName, spaceKey);
    }

    @Override
    public boolean removeLabelByName(String token, String labelName, long objectId) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removeLabelByName(token, labelName, objectId);
    }

    @Override
    public boolean removeLabelById(String token, long labelId, long objectId) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removeLabelById(token, labelId, objectId);
    }

    @Override
    public boolean removeLabelByObject(String token, RemoteLabel labelObject, long objectId) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removeLabelByObject(token, labelObject, objectId);
    }

    @Override
    public boolean removeLabelByNameFromSpace(String token, String labelName, String spaceKey) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.removeLabelByNameFromSpace(token, labelName, spaceKey);
    }

    @Override
    public RemoteContentPermissionSet[] getContentPermissionSets(String token, long contentId) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.getContentPermissionSets(token, contentId);
    }

    @Override
    public RemoteContentPermissionSet getContentPermissionSet(String token, long contentId, String permissionType) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.getContentPermissionSet(token, contentId, permissionType);
    }

    @Override
    public boolean setContentPermissions(String token, long contentId, String permissionType, RemoteContentPermission[] remoteContentPermissions) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.setContentPermissions(token, contentId, permissionType, remoteContentPermissions);
    }

    @Override
    public boolean addGlobalPermission(String token, String permission, String remoteEntityName) throws NotPermittedException, RemoteException {
        return this.soapService.addGlobalPermission(token, permission, remoteEntityName);
    }

    @Override
    public boolean addGlobalPermissions(String token, String[] permissions, String remoteEntityName) throws NotPermittedException, RemoteException {
        return this.soapService.addGlobalPermissions(token, permissions, remoteEntityName);
    }

    @Override
    public boolean removeGlobalPermission(String token, String permission, String remoteEntityName) throws RemoteException {
        return this.soapService.removeGlobalPermission(token, permission, remoteEntityName);
    }

    @Override
    public boolean addAnonymousUsePermission(String token) throws RemoteException {
        return this.soapService.addAnonymousUsePermission(token);
    }

    @Override
    public boolean removeAnonymousUsePermission(String token) throws RemoteException {
        return this.soapService.removeAnonymousUsePermission(token);
    }

    @Override
    public boolean addAnonymousViewUserProfilePermission(String token) throws RemoteException {
        return this.soapService.addAnonymousViewUserProfilePermission(token);
    }

    @Override
    public boolean removeAnonymousViewUserProfilePermission(String token) throws RemoteException {
        return this.soapService.removeAnonymousViewUserProfilePermission(token);
    }

    @Override
    public boolean startActivity(String token, String key, String user) throws RemoteException {
        return this.soapService.startActivity(token, key, user);
    }

    @Override
    public boolean stopActivity(String token, String key, String user) throws RemoteException {
        return this.soapService.stopActivity(token, key, user);
    }

    @Override
    public boolean setEnableAnonymousAccess(String token, boolean value) throws RemoteException {
        return this.soapService.setEnableAnonymousAccess(token, value);
    }

    @Override
    public boolean isPluginEnabled(String token, String pluginKey) throws RemoteException {
        return this.soapService.isPluginEnabled(token, pluginKey);
    }

    @Override
    public boolean isPluginInstalled(String token, String pluginKey) throws RemoteException {
        return this.soapService.isPluginInstalled(token, pluginKey);
    }

    @Override
    public boolean installPlugin(String token, String pluginFileName, byte[] pluginData) throws RemoteException {
        return this.soapService.installPlugin(token, pluginFileName, pluginData);
    }

    @Override
    public boolean isDarkFeatureEnabled(String token, String key) throws RemoteException {
        return this.soapService.isDarkFeatureEnabled(token, key);
    }

    @Override
    public RemoteContentSummaries getTrashContents(String token, String spaceKey, int offset, int count) throws RemoteException {
        return this.soapService.getTrashContents(token, spaceKey, offset, count);
    }

    @Override
    public Boolean purgeFromTrash(String token, String spaceKey, long pageId) throws RemoteException {
        return this.soapService.purgeFromTrash(token, spaceKey, pageId);
    }

    @Override
    public Boolean emptyTrash(String token, String spaceKey) throws RemoteException {
        return this.soapService.emptyTrash(token, spaceKey);
    }

    @Override
    public String getSpaceStatus(String token, String spaceKey) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.getSpaceStatus(token, spaceKey);
    }

    @Override
    public Boolean setSpaceStatus(String token, String spaceKey, String status) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapService.setSpaceStatus(token, spaceKey, status);
    }

    private String convertToStorage(PageContext pageContext, String content) throws RemoteException {
        ArrayList exceptions = new ArrayList();
        String storageFormattedContent = this.wikiToXhtmlMigrator.migrate(content, (RenderContext)pageContext, exceptions);
        if (exceptions.isEmpty()) {
            return storageFormattedContent;
        }
        RuntimeException firstException = (RuntimeException)exceptions.get(0);
        log.debug("Exception during migration: {}", (Object)firstException.getMessage(), (Object)firstException);
        if (exceptions.size() == 1) {
            throw new RemoteException((Throwable)firstException);
        }
        throw new RemoteException("Multiple exceptions occurred, only the first is returned: " + firstException.getMessage(), (Throwable)firstException);
    }

    public void setXhtmlSoapService(ConfluenceSoapService soapService) {
        this.soapService = soapService;
    }

    public void setWikiToXhtmlMigrator(ExceptionTolerantMigrator wikiToXhtmlMigrator) {
        this.wikiToXhtmlMigrator = wikiToXhtmlMigrator;
    }

    public void setContentEntityObjectDao(ContentEntityObjectDao contentEntityObjectDao) {
        this.contentEntityObjectDao = contentEntityObjectDao;
    }

    public void setSettingsManager(final SettingsManager settingsManager) {
        this.unsupportedOperationMessage = new LazyReference<String>(){

            protected String create() throws Exception {
                return "Unsupported operation: Wiki formatted content can no longer be retrieved from this API. Please use the version 2 API. The version 2 WSDL is available at: " + settingsManager.getGlobalSettings().getBaseUrl() + "/rpc/soap-axis/confluenceservice-v2?wsdl. XML-RPC requests should prefixed with \"confluence2.\".";
            }
        };
    }
}

