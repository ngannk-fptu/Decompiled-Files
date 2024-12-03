/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.persistence.ContentEntityObjectDao
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.renderer.RenderContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc.soap;

import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
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
import com.atlassian.confluence.rpc.soap.services.AdminSoapService;
import com.atlassian.confluence.rpc.soap.services.AttachmentsSoapService;
import com.atlassian.confluence.rpc.soap.services.BlogsSoapService;
import com.atlassian.confluence.rpc.soap.services.LabelsSoapService;
import com.atlassian.confluence.rpc.soap.services.NotificationsSoapService;
import com.atlassian.confluence.rpc.soap.services.PagesSoapService;
import com.atlassian.confluence.rpc.soap.services.SpacesSoapService;
import com.atlassian.confluence.rpc.soap.services.UsersSoapService;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.renderer.RenderContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XhtmlSoapService
implements ConfluenceSoapService {
    private static final Logger log = LoggerFactory.getLogger(XhtmlSoapService.class);
    public static final List VIEW_SPACE_PERMISSION_TYPES = Arrays.asList("VIEWSPACE");
    public static final List MODIFY_SPACE_PERMISSION_TYPES = Arrays.asList("EDITSPACE");
    public static final List ADMINISTER_PERMISSION_TYPES = Arrays.asList("ADMINISTRATECONFLUENCE", "SYSTEMADMINISTRATOR");
    public static final List SPACE_ADMIN_PERMISSION_TYPES = Arrays.asList("SETSPACEPERMISSIONS");
    public static final List REMOVE_PAGE_PERMISSION_TYPES = Arrays.asList("REMOVEPAGE");
    public static final List EXPORT_SPACE_PERMISSION_TYPES = Arrays.asList("EXPORTSPACE");
    private SpacesSoapService spacesService;
    private PagesSoapService pagesService;
    private UsersSoapService usersService;
    private BlogsSoapService blogsService;
    private AdminSoapService adminSoapService;
    private LabelsSoapService labelsSoapService;
    private AttachmentsSoapService attachmentsService;
    private NotificationsSoapService notificationsService;
    private ExceptionTolerantMigrator wikiToXhtmlMigrator;
    private ExceptionTolerantMigrator xhtmlRoundTripMigrator;
    private ContentEntityObjectDao contentEntityObjectDao;
    private MailServerManager mailServerManager;
    public static final String __PARANAMER_DATA = "addAnonymousPermissionToSpace java.lang.String,java.lang.String,java.lang.String token,permission,spaceKey \naddAnonymousPermissionsToSpace java.lang.String,java.lang.String,java.lang.String token,permissions,spaceKey \naddAnonymousUsePermission java.lang.String token \naddAnonymousViewUserProfilePermission java.lang.String token \naddAttachment java.lang.String,long,com.atlassian.confluence.rpc.soap.beans.RemoteAttachment,byte token,contentId,attachment,attachmentData \naddComment java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteComment token,comment \naddGlobalPermission java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName \naddGlobalPermissions java.lang.String,java.lang.String,java.lang.String token,permissions,remoteEntityName \naddGroup java.lang.String,java.lang.String token,groupname \naddLabelById java.lang.String,long,long token,labelId,objectId \naddLabelByName java.lang.String,java.lang.String,long token,labelName,objectId \naddLabelByNameToSpace java.lang.String,java.lang.String,java.lang.String token,labelName,spaceKey \naddLabelByObject java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteLabel,long token,labelObject,objectId \naddPermissionToSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName,spaceKey \naddPermissionsToSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permissions,remoteEntityName,spaceKey \naddPersonalSpace java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace,java.lang.String token,space,username \naddPersonalSpaceWithDefaultPermissions java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace,java.lang.String token,space,username \naddProfilePicture java.lang.String,java.lang.String,java.lang.String,java.lang.String,byte token,userName,fileName,mimeType,pictureData \naddSpace java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace token,space \naddSpaceGroup java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpaceGroup token,spaceGroup \naddSpaceWithDefaultPermissions java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace token,space \naddUser java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUser,java.lang.String token,remoteUser,password \naddUser java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUser,java.lang.String,boolean token,remoteUser,password,notifyUser \naddUserToGroup java.lang.String,java.lang.String,java.lang.String token,username,groupname \nchangeMyPassword java.lang.String,java.lang.String,java.lang.String token,oldPass,newPass \nchangeUserPassword java.lang.String,java.lang.String,java.lang.String token,username,newPass \nclearIndexQueue java.lang.String token \nconvertWikiToStorageFormat java.lang.String,java.lang.String token,markup \ndeactivateUser java.lang.String,java.lang.String token,username \neditComment java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteComment token,comment \neditUser java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUser token,remoteUser \nemptyTrash java.lang.String,java.lang.String token,spaceKey \nexportSite java.lang.String,boolean token,exportAttachments \nexportSpace java.lang.String,java.lang.String,java.lang.String token,spaceKey,exportType \nexportSpace java.lang.String,java.lang.String,java.lang.String,boolean token,spaceKey,exportType,exportAll \nflushIndexQueue java.lang.String token \ngetActiveUsers java.lang.String,boolean token,viewAll \ngetAncestors java.lang.String,long token,pageId \ngetAttachment java.lang.String,long,java.lang.String,int token,contentId,fileName,version \ngetAttachmentData java.lang.String,long,java.lang.String,int token,contentId,fileName,version \ngetAttachments java.lang.String,long token,pageId \ngetBlogEntries java.lang.String,java.lang.String token,spaceKey \ngetBlogEntry java.lang.String,long token,entryId \ngetBlogEntryByDateAndTitle java.lang.String,java.lang.String,int,int,int,java.lang.String token,spaceKey,year,month,dayOfMonth,postTitle \ngetBlogEntryByDayAndTitle java.lang.String,java.lang.String,int,java.lang.String token,spaceKey,dayOfMonth,postTitle \ngetChildren java.lang.String,long token,pageId \ngetClusterInformation java.lang.String token \ngetClusterNodeStatuses java.lang.String token \ngetComment java.lang.String,long token,commentId \ngetComments java.lang.String,long token,pageId \ngetContentPermissionSet java.lang.String,long,java.lang.String token,contentId,permissionType \ngetContentPermissionSets java.lang.String,long token,contentId \ngetDescendents java.lang.String,long token,pageId \ngetGroups java.lang.String token \ngetLabelContentById java.lang.String,long token,labelId \ngetLabelContentByName java.lang.String,java.lang.String token,labelName \ngetLabelContentByObject java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteLabel token,labelObject \ngetLabelsByDetail java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String token,labelName,namespace,spaceKey,owner \ngetLabelsById java.lang.String,long token,objectId \ngetMostPopularLabels java.lang.String,int token,maxCount \ngetMostPopularLabelsInSpace java.lang.String,java.lang.String,int token,spaceKey,maxCount \ngetPage java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageTitle \ngetPage java.lang.String,long token,pageId \ngetPageHistory java.lang.String,long token,pageId \ngetPagePermissions java.lang.String,long token,pageId \ngetPageSummary java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageTitle \ngetPageSummary java.lang.String,long token,pageId \ngetPages java.lang.String,java.lang.String token,spaceKey \ngetPermissions java.lang.String,java.lang.String token,spaceKey \ngetPermissionsForUser java.lang.String,java.lang.String,java.lang.String token,spaceKey,userName \ngetRecentlyUsedLabels java.lang.String,int token,maxResults \ngetRecentlyUsedLabelsInSpace java.lang.String,java.lang.String,int token,spaceKey,maxResults \ngetRelatedLabels java.lang.String,java.lang.String,int token,labelName,maxResults \ngetRelatedLabelsInSpace java.lang.String,java.lang.String,java.lang.String,int token,labelName,spaceKey,maxResults \ngetServerInfo java.lang.String token \ngetSpace java.lang.String,java.lang.String token,spaceKey \ngetSpaceGroup java.lang.String,java.lang.String token,spaceGroupKey \ngetSpaceGroups java.lang.String token \ngetSpaceLevelPermissions java.lang.String token \ngetSpacePermissionSet java.lang.String,java.lang.String,java.lang.String token,spaceKey,permissionType \ngetSpacePermissionSets java.lang.String,java.lang.String token,spaceKey \ngetSpaceStatus java.lang.String,java.lang.String token,spaceKey \ngetSpaces java.lang.String token \ngetSpacesContainingContentWithLabel java.lang.String,java.lang.String token,labelName \ngetSpacesInGroup java.lang.String,java.lang.String token,spaceGroupKey \ngetSpacesWithLabel java.lang.String,java.lang.String token,labelName \ngetTopLevelPages java.lang.String,java.lang.String token,spaceKey \ngetTrashContents java.lang.String,java.lang.String,int,int token,spaceKey,offset,count \ngetUserByKey java.lang.String,java.lang.String token,key \ngetUserByName java.lang.String,java.lang.String token,username \ngetUserGroups java.lang.String,java.lang.String token,username \ngetUserInformation java.lang.String,java.lang.String token,username \ngetUserPreferenceBoolean java.lang.String,java.lang.String,java.lang.String token,username,key \ngetUserPreferenceLong java.lang.String,java.lang.String,java.lang.String token,username,key \ngetUserPreferenceString java.lang.String,java.lang.String,java.lang.String token,username,key \ngetWatchersForPage java.lang.String,long token,pageId \ngetWatchersForSpace java.lang.String,java.lang.String token,spaceKey \nhasGroup java.lang.String,java.lang.String token,groupname \nhasUser java.lang.String,java.lang.String token,username \nimportSpace java.lang.String,byte token,importData \ninstallPlugin java.lang.String,java.lang.String,byte token,pluginFileName,pluginData \nisActiveUser java.lang.String,java.lang.String token,username \nisDarkFeatureEnabled java.lang.String,java.lang.String token,key \nisPluginEnabled java.lang.String,java.lang.String token,pluginKey \nisPluginInstalled java.lang.String,java.lang.String token,pluginKey \nisWatchingPage java.lang.String,long,java.lang.String token,pageId,username \nisWatchingSpace java.lang.String,java.lang.String,java.lang.String token,spaceKey,username \nisWatchingSpaceForType java.lang.String,java.lang.String,java.lang.String,java.lang.String token,spaceKey,contentType,username \nlogin java.lang.String,java.lang.String username,password \nlogout java.lang.String token \nmoveAttachment java.lang.String,long,java.lang.String,long,java.lang.String token,originalContentId,originalFileName,newContentId,newFileName \nmovePage java.lang.String,long,long,java.lang.String token,sourcePageId,targetPageId,position \nmovePageToTopLevel java.lang.String,long,java.lang.String token,pageId,targetSpaceKey \nperformBackup java.lang.String,boolean token,exportAttachments \npurgeFromTrash java.lang.String,java.lang.String,long token,spaceKey,pageId \nreactivateUser java.lang.String,java.lang.String token,username \nrecoverMainIndex java.lang.String token \nremoveAllPermissionsForGroup java.lang.String,java.lang.String token,groupname \nremoveAnonymousPermissionFromSpace java.lang.String,java.lang.String,java.lang.String token,permission,spaceKey \nremoveAnonymousUsePermission java.lang.String token \nremoveAnonymousViewUserProfilePermission java.lang.String token \nremoveAttachment java.lang.String,long,java.lang.String token,contentId,fileName \nremoveComment java.lang.String,long token,commentId \nremoveGlobalPermission java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName \nremoveGroup java.lang.String,java.lang.String,java.lang.String token,groupname,defaultGroupName \nremoveLabelById java.lang.String,long,long token,labelId,objectId \nremoveLabelByName java.lang.String,java.lang.String,long token,labelName,objectId \nremoveLabelByNameFromSpace java.lang.String,java.lang.String,java.lang.String token,labelName,spaceKey \nremoveLabelByObject java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteLabel,long token,labelObject,objectId \nremovePage java.lang.String,long token,pageId \nremovePageVersionById java.lang.String,long token,historicalPageId \nremovePageVersionByVersion java.lang.String,long,int token,pageId,version \nremovePageWatch java.lang.String,long token,pageId \nremovePageWatchForUser java.lang.String,long,java.lang.String token,pageId,username \nremovePermissionFromSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName,spaceKey \nremoveSpace java.lang.String,java.lang.String token,spaceKey \nremoveSpaceGroup java.lang.String,java.lang.String token,spaceGroupKey \nremoveSpaceWatch java.lang.String,java.lang.String token,spaceKey \nremoveUser java.lang.String,java.lang.String token,username \nremoveUserFromGroup java.lang.String,java.lang.String,java.lang.String token,username,groupname \nrenameUser java.lang.String,java.lang.String,java.lang.String token,oldUsername,newUsername \nrenameUsers java.lang.String,java.util.Map token,oldUsernamesToNewUsernames \nrenderContent java.lang.String,java.lang.String,long,java.lang.String token,spaceKey,pageId,newContent \nrenderContent java.lang.String,java.lang.String,long,java.lang.String,java.util.Map token,spaceKey,pageId,newContent,renderParameters \nsearch java.lang.String,java.lang.String,int token,query,maxResults \nsearch java.lang.String,java.lang.String,java.util.Map,int token,query,params,maxResults \nsetAdminSoapService com.atlassian.confluence.rpc.soap.services.AdminSoapService adminSoapService \nsetAttachmentsSoapService com.atlassian.confluence.rpc.soap.services.AttachmentsSoapService attachmentsService \nsetBlogsSoapService com.atlassian.confluence.rpc.soap.services.BlogsSoapService blogsService \nsetContentEntityObjectDao com.atlassian.confluence.core.persistence.ContentEntityObjectDao contentEntityObjectDao \nsetContentPermissions java.lang.String,long,java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteContentPermission token,contentId,permissionType,remoteContentPermissions \nsetEnableAnonymousAccess java.lang.String,boolean token,value \nsetGlobalSoapService com.atlassian.confluence.rpc.soap.services.AdminSoapService adminSoapService \nsetLabelsSoapService com.atlassian.confluence.rpc.soap.services.LabelsSoapService labelsSoapService \nsetMailServerManager com.atlassian.mail.server.MailServerManager mailServerManager \nsetNotificationsSoapService com.atlassian.confluence.rpc.soap.services.NotificationsSoapService notificationsSoapService \nsetPagesSoapService com.atlassian.confluence.rpc.soap.services.PagesSoapService pagesService \nsetSpaceStatus java.lang.String,java.lang.String,java.lang.String token,spaceKey,status \nsetSpacesSoapService com.atlassian.confluence.rpc.soap.services.SpacesSoapService spacesService \nsetUserInformation java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteUserInformation token,userInfo \nsetUserPreferenceBoolean java.lang.String,java.lang.String,java.lang.String,boolean token,username,key,value \nsetUserPreferenceLong java.lang.String,java.lang.String,java.lang.String,long token,username,key,value \nsetUserPreferenceString java.lang.String,java.lang.String,java.lang.String,java.lang.String token,username,key,value \nsetUsersSoapService com.atlassian.confluence.rpc.soap.services.UsersSoapService usersService \nsetWikiToXhtmlMigrator com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator wikiToXhtmlMigrator \nsetXhtmlRoundTripMigrator com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator xhtmlRoundTripMigrator \nstartActivity java.lang.String,java.lang.String,java.lang.String token,key,user \nstopActivity java.lang.String,java.lang.String,java.lang.String token,key,user \nstoreBlogEntry java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntry token,blogEntry \nstorePage java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemotePage token,page \nstoreSpace java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteSpace token,remoteSpace \nupdatePage java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemotePage,com.atlassian.confluence.rpc.soap.beans.RemotePageUpdateOptions token,page,options \nwatchPage java.lang.String,long token,pageId \nwatchPageForUser java.lang.String,long,java.lang.String token,pageId,username \nwatchSpace java.lang.String,java.lang.String token,spaceKey \n";

    public void setSpacesSoapService(SpacesSoapService spacesService) {
        this.spacesService = spacesService;
    }

    public void setPagesSoapService(PagesSoapService pagesService) {
        this.pagesService = pagesService;
    }

    public void setUsersSoapService(UsersSoapService usersService) {
        this.usersService = usersService;
    }

    public void setBlogsSoapService(BlogsSoapService blogsService) {
        this.blogsService = blogsService;
    }

    public void setGlobalSoapService(AdminSoapService adminSoapService) {
        this.adminSoapService = adminSoapService;
    }

    public void setAdminSoapService(AdminSoapService adminSoapService) {
        this.adminSoapService = adminSoapService;
    }

    public void setLabelsSoapService(LabelsSoapService labelsSoapService) {
        this.labelsSoapService = labelsSoapService;
    }

    public void setAttachmentsSoapService(AttachmentsSoapService attachmentsService) {
        this.attachmentsService = attachmentsService;
    }

    public void setNotificationsSoapService(NotificationsSoapService notificationsSoapService) {
        this.notificationsService = notificationsSoapService;
    }

    @Override
    public String login(String username, String password) {
        throw new UnsupportedOperationException("Should be handled in an interceptor.");
    }

    @Override
    public boolean logout(String token) {
        throw new UnsupportedOperationException("Should be handled in an interceptor.");
    }

    @Override
    public RemoteSpaceSummary[] getSpaces(String token) {
        return this.spacesService.getSpaces();
    }

    @Override
    public RemoteSpaceSummary[] getSpacesInGroup(String token, String spaceGroupKey) {
        return this.spacesService.getSpacesInGroup(spaceGroupKey);
    }

    @Override
    public RemoteSpace addSpaceWithDefaultPermissions(String token, RemoteSpace space) throws RemoteException {
        return this.spacesService.addSpaceWithDefaultPermissions(space);
    }

    @Override
    public RemoteSpace addSpace(String token, RemoteSpace space) throws RemoteException {
        return this.spacesService.addSpace(space);
    }

    @Override
    public RemoteSpace storeSpace(String token, RemoteSpace remoteSpace) throws RemoteException {
        return this.spacesService.storeSpace(remoteSpace);
    }

    @Override
    public RemoteSpaceGroup addSpaceGroup(String token, RemoteSpaceGroup spaceGroup) throws RemoteException {
        return this.spacesService.addSpaceGroup(spaceGroup);
    }

    @Override
    public RemoteSpaceGroup getSpaceGroup(String token, String spaceGroupKey) throws RemoteException {
        return this.spacesService.getSpaceGroup(spaceGroupKey);
    }

    @Override
    public RemoteSpaceGroup[] getSpaceGroups(String token) throws RemoteException {
        return this.spacesService.getSpaceGroups();
    }

    @Override
    public boolean removeSpaceGroup(String token, String spaceGroupKey) throws RemoteException {
        return this.spacesService.removeSpaceGroup(spaceGroupKey);
    }

    @Override
    public RemoteSpace addPersonalSpace(String token, RemoteSpace space, String username) throws RemoteException {
        return this.spacesService.addPersonalSpace(space, username);
    }

    @Override
    public RemoteSpace addPersonalSpaceWithDefaultPermissions(String token, RemoteSpace space, String username) throws RemoteException {
        return this.spacesService.addPersonalSpaceWithDefaultPermissions(space, username);
    }

    @Override
    public Boolean removeSpace(String token, String spaceKey) throws RemoteException {
        return this.spacesService.removeSpace(spaceKey);
    }

    @Override
    public RemoteSpace getSpace(String token, String spaceKey) throws RemoteException {
        return this.spacesService.getSpace(spaceKey);
    }

    @Override
    public String getSpaceStatus(String token, String spaceKey) throws RemoteException {
        return this.spacesService.getSpaceStatus(spaceKey);
    }

    @Override
    public Boolean setSpaceStatus(String token, String spaceKey, String status) throws RemoteException {
        this.spacesService.setSpaceStatus(spaceKey, status);
        return true;
    }

    @Override
    public String[] getPermissions(String token, String spaceKey) throws RemoteException {
        return this.spacesService.getPermissions(spaceKey);
    }

    @Override
    public String[] getPermissionsForUser(String token, String spaceKey, String userName) throws RemoteException {
        return this.spacesService.getPermissions(spaceKey, userName);
    }

    @Override
    public RemoteSpacePermissionSet[] getSpacePermissionSets(String token, String spaceKey) throws RemoteException {
        return this.spacesService.getSpacePermissionSets(spaceKey);
    }

    @Override
    public RemoteSpacePermissionSet getSpacePermissionSet(String token, String spaceKey, String permissionType) throws RemoteException {
        return this.spacesService.getSpacePermissionSet(spaceKey, permissionType);
    }

    @Override
    public boolean addPermissionToSpace(String token, String permission, String remoteEntityName, String spaceKey) throws RemoteException {
        return this.spacesService.addPermissionToSpace(permission, remoteEntityName, spaceKey);
    }

    @Override
    public boolean addPermissionsToSpace(String token, String[] permissions, String remoteEntityName, String spaceKey) throws RemoteException {
        return this.spacesService.addPermissionsToSpace(permissions, remoteEntityName, spaceKey);
    }

    @Override
    public boolean removePermissionFromSpace(String token, String permission, String remoteEntityName, String spaceKey) throws RemoteException {
        return this.spacesService.removePermissionFromSpace(permission, remoteEntityName, spaceKey);
    }

    @Override
    public boolean addGlobalPermission(String token, String permission, String remoteEntityName) throws RemoteException {
        return this.spacesService.addGlobalPermission(permission, remoteEntityName);
    }

    @Override
    public boolean addGlobalPermissions(String token, String[] permissions, String remoteEntityName) throws RemoteException {
        return this.spacesService.addGlobalPermissions(permissions, remoteEntityName);
    }

    @Override
    public boolean removeGlobalPermission(String token, String permission, String remoteEntityName) throws RemoteException {
        return this.spacesService.removeGlobalPermission(permission, remoteEntityName);
    }

    @Override
    public boolean addAnonymousUsePermission(String token) throws RemoteException {
        return this.spacesService.addAnonymousUsePermission();
    }

    @Override
    public boolean removeAnonymousUsePermission(String token) throws RemoteException {
        return this.spacesService.removeAnonymousUserPermission();
    }

    @Override
    public boolean addAnonymousViewUserProfilePermission(String token) throws RemoteException {
        return this.spacesService.addAnonymousViewUserProfilePermission();
    }

    @Override
    public boolean removeAnonymousViewUserProfilePermission(String token) throws RemoteException {
        return this.spacesService.removeAnonymousViewUserProfilePermission();
    }

    @Override
    public boolean addAnonymousPermissionToSpace(String token, String permission, String spaceKey) throws RemoteException {
        return this.spacesService.addPermissionToSpace(permission, null, spaceKey);
    }

    @Override
    public boolean addAnonymousPermissionsToSpace(String token, String[] permissions, String spaceKey) throws RemoteException {
        return this.spacesService.addPermissionsToSpace(permissions, null, spaceKey);
    }

    @Override
    public boolean removeAnonymousPermissionFromSpace(String token, String permission, String spaceKey) throws RemoteException {
        return this.spacesService.removePermissionFromSpace(permission, null, spaceKey);
    }

    @Override
    public String[] getSpaceLevelPermissions(String token) {
        return this.spacesService.getSpaceLevelPermissions();
    }

    @Override
    public RemotePageSummary[] getPages(String token, String spaceKey) throws RemoteException {
        return this.pagesService.getPages(spaceKey);
    }

    @Override
    public RemotePage getPage(String token, String spaceKey, String pageTitle) throws RemoteException {
        return this.pagesService.getPage(spaceKey, pageTitle);
    }

    @Override
    public RemotePage getPage(String token, long pageId) throws RemoteException {
        return this.pagesService.getPage(pageId);
    }

    @Override
    public RemotePageSummary getPageSummary(String token, String spaceKey, String pageTitle) throws RemoteException {
        return this.pagesService.getPageSummary(spaceKey, pageTitle);
    }

    @Override
    public RemotePageSummary getPageSummary(String token, long pageId) throws RemoteException {
        return this.pagesService.getPageSummary(pageId);
    }

    @Override
    public RemoteComment[] getComments(String token, long pageId) throws RemoteException {
        return this.pagesService.getComments(pageId);
    }

    @Override
    public RemoteComment getComment(String token, long commentId) throws RemoteException {
        return this.pagesService.getComment(commentId);
    }

    @Override
    public RemoteComment addComment(String token, RemoteComment comment) throws RemoteException {
        ContentEntityObject owningContent = this.contentEntityObjectDao.getById(comment.getPageId());
        String storageFormattedContent = this.migrateStorageFormat(new PageContext(owningContent), comment.getContent());
        comment.setContent(storageFormattedContent);
        return this.pagesService.addComment(comment);
    }

    @Override
    public RemoteComment editComment(String token, RemoteComment comment) throws RemoteException {
        ContentEntityObject owningContent = this.contentEntityObjectDao.getById(comment.getPageId());
        String storageFormattedContent = this.migrateStorageFormat(new PageContext(owningContent), comment.getContent());
        comment.setContent(storageFormattedContent);
        return this.pagesService.editComment(comment);
    }

    @Override
    public boolean removeComment(String token, long commentId) throws RemoteException {
        return this.pagesService.removeComment(commentId);
    }

    @Override
    public RemotePageSummary[] getTopLevelPages(String token, String spaceKey) throws RemoteException {
        return this.pagesService.getTopLevelPages(spaceKey);
    }

    @Override
    public RemotePageSummary[] getAncestors(String token, long pageId) throws RemoteException {
        return this.pagesService.getAncestors(pageId);
    }

    @Override
    public RemotePageSummary[] getChildren(String token, long pageId) throws RemoteException {
        return this.pagesService.getChildren(pageId);
    }

    @Override
    public RemotePageSummary[] getDescendents(String token, long pageId) throws RemoteException {
        return this.pagesService.getDescendents(pageId);
    }

    @Override
    public RemoteAttachment[] getAttachments(String token, long pageId) throws RemoteException {
        return this.pagesService.getAttachments(pageId);
    }

    @Override
    public RemotePageHistory[] getPageHistory(String token, long pageId) throws RemoteException {
        return this.pagesService.getPageHistory(pageId);
    }

    @Override
    public boolean watchPage(String token, long pageId) throws RemoteException {
        return this.notificationsService.watchPage(pageId);
    }

    @Override
    public boolean watchSpace(String token, String spaceKey) throws RemoteException {
        return this.notificationsService.watchSpace(spaceKey);
    }

    @Override
    public boolean watchPageForUser(String token, long pageId, String username) throws RemoteException {
        return this.notificationsService.watchPageForUser(pageId, username);
    }

    @Override
    public boolean removePageWatch(String token, long pageId) throws RemoteException {
        return this.notificationsService.removePageWatch(pageId);
    }

    @Override
    public boolean removeSpaceWatch(String token, String spaceKey) throws RemoteException {
        return this.notificationsService.removeSpaceWatch(spaceKey);
    }

    @Override
    public boolean removePageWatchForUser(String token, long pageId, String username) throws RemoteException {
        return this.notificationsService.removePageWatchForUser(pageId, username);
    }

    @Override
    public boolean isWatchingPage(String token, long pageId, String username) throws RemoteException {
        return this.notificationsService.isWatchingPage(pageId, username);
    }

    @Override
    public boolean isWatchingSpace(String token, String spaceKey, String username) throws RemoteException {
        return this.notificationsService.isWatchingSpace(spaceKey, username);
    }

    @Override
    public boolean isWatchingSpaceForType(String token, String spaceKey, String contentType, String username) throws RemoteException {
        return this.notificationsService.isWatchingSpaceForType(spaceKey, contentType, username);
    }

    @Override
    public RemoteUser[] getWatchersForPage(String token, long pageId) throws RemoteException {
        return this.notificationsService.getWatchersForPage(pageId);
    }

    @Override
    public RemoteUser[] getWatchersForSpace(String token, String spaceKey) throws RemoteException {
        return this.notificationsService.getWatchersForSpace(spaceKey);
    }

    @Override
    public RemoteSearchResult[] search(String token, String query, int maxResults) throws RemoteException {
        return this.pagesService.search(query, maxResults);
    }

    @Override
    public RemoteSearchResult[] search(String token, String query, Map params, int maxResults) throws RemoteException {
        return this.pagesService.search(query, params, maxResults);
    }

    @Override
    public RemoteBlogEntry getBlogEntryByDayAndTitle(String token, String spaceKey, int dayOfMonth, String postTitle) throws RemoteException {
        return this.blogsService.getBlogEntryByDayAndTitle(spaceKey, dayOfMonth, postTitle);
    }

    @Override
    public RemoteBlogEntry getBlogEntryByDateAndTitle(String token, String spaceKey, int year, int month, int dayOfMonth, String postTitle) throws RemoteException {
        return this.blogsService.getBlogEntryByDateAndTitle(spaceKey, year, month, dayOfMonth, postTitle);
    }

    @Override
    public String renderContent(String token, String spaceKey, long pageId, String newContent) throws RemoteException {
        return this.pagesService.renderContent(spaceKey, pageId, newContent);
    }

    @Override
    public String renderContent(String token, String spaceKey, long pageId, String newContent, Map renderParameters) throws RemoteException {
        return this.pagesService.renderContent(spaceKey, pageId, newContent, renderParameters);
    }

    @Override
    public String convertWikiToStorageFormat(String token, String markup) throws RemoteException {
        ArrayList exceptions = new ArrayList();
        String storageFormattedContent = this.wikiToXhtmlMigrator.migrate(markup, (RenderContext)new PageContext(), exceptions);
        if (exceptions.size() == 1) {
            throw new RemoteException((Throwable)exceptions.get(0));
        }
        if (exceptions.size() > 1) {
            throw new RemoteException("Multiple exceptions occurred, only the first is returned: " + ((RuntimeException)exceptions.get(0)).getMessage(), (Throwable)exceptions.get(0));
        }
        return storageFormattedContent;
    }

    @Override
    public RemoteAttachment addAttachment(String token, long contentId, RemoteAttachment attachment, byte[] attachmentData) throws RemoteException {
        return this.attachmentsService.addAttachment(contentId, attachment, attachmentData);
    }

    @Override
    public RemoteAttachment getAttachment(String token, long contentId, String fileName, int version) throws RemoteException {
        return this.attachmentsService.getAttachment(contentId, fileName, version);
    }

    @Override
    public byte[] getAttachmentData(String token, long contentId, String fileName, int version) throws RemoteException {
        return this.attachmentsService.getAttachmentData(contentId, fileName, version);
    }

    @Override
    public boolean removeAttachment(String token, long contentId, String fileName) throws RemoteException {
        return this.attachmentsService.removeAttachment(contentId, fileName);
    }

    @Override
    public boolean moveAttachment(String token, long originalContentId, String originalFileName, long newContentId, String newFileName) throws RemoteException {
        return this.attachmentsService.moveAttachment(originalContentId, originalFileName, newContentId, newFileName);
    }

    @Override
    public Boolean movePageToTopLevel(String token, long pageId, String targetSpaceKey) throws RemoteException {
        return this.pagesService.movePageToTopLevel(pageId, targetSpaceKey);
    }

    @Override
    public Boolean movePage(String token, long sourcePageId, long targetPageId, String position) throws RemoteException {
        return this.pagesService.movePage(sourcePageId, targetPageId, position);
    }

    @Override
    public Boolean removePage(String token, long pageId) throws RemoteException {
        return this.pagesService.removePage(pageId);
    }

    @Override
    public Boolean removePageVersionById(String token, long historicalPageId) throws RemoteException {
        return this.pagesService.removePageVersion(token, historicalPageId);
    }

    @Override
    public Boolean removePageVersionByVersion(String token, long pageId, int version) throws RemoteException {
        return this.pagesService.removePageVersion(token, pageId, version);
    }

    @Override
    public RemotePage storePage(String token, RemotePage page) throws RemoteException {
        ContentEntityObject pageCeo = this.contentEntityObjectDao.getById(page.getId());
        PageContext pageContext = pageCeo != null ? new PageContext(pageCeo) : new PageContext(page.getSpace());
        String storageFormattedContent = this.migrateStorageFormat(pageContext, page.getContent());
        page.setContent(storageFormattedContent);
        return this.pagesService.storePage(page);
    }

    private String migrateStorageFormat(PageContext pageContext, String content) throws RemoteException {
        ArrayList exceptions = new ArrayList();
        String storageFormattedContent = this.xhtmlRoundTripMigrator.migrate(content, (RenderContext)pageContext, exceptions);
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

    @Override
    public RemotePage updatePage(String token, RemotePage page, RemotePageUpdateOptions options) throws RemoteException {
        ContentEntityObject pageCeo = this.contentEntityObjectDao.getById(page.getId());
        String storageFormattedContent = this.migrateStorageFormat(new PageContext(pageCeo), page.getContent());
        page.setContent(storageFormattedContent);
        return this.pagesService.updatePage(page, options);
    }

    @Override
    public RemotePermission[] getPagePermissions(String token, long pageId) throws RemoteException {
        return this.pagesService.getPermissions(pageId);
    }

    @Override
    public RemoteContentSummaries getTrashContents(String token, String spaceKey, int offset, int count) throws RemoteException {
        return this.pagesService.getTrashContents(spaceKey, offset, count);
    }

    @Override
    public Boolean purgeFromTrash(String token, String spaceKey, long pageId) throws RemoteException {
        return this.pagesService.purgeFromTrash(spaceKey, pageId);
    }

    @Override
    public Boolean emptyTrash(String token, String spaceKey) throws RemoteException {
        return this.pagesService.emptyTrash(spaceKey);
    }

    @Override
    public RemoteBlogEntry getBlogEntry(String token, long entryId) throws RemoteException {
        return this.blogsService.getBlogEntry(entryId);
    }

    @Override
    public RemoteBlogEntrySummary[] getBlogEntries(String token, String spaceKey) throws RemoteException {
        return this.blogsService.getBlogEntries(spaceKey);
    }

    @Override
    public RemoteBlogEntry storeBlogEntry(String token, RemoteBlogEntry blogEntry) throws RemoteException {
        ContentEntityObject pageCeo = this.contentEntityObjectDao.getById(blogEntry.getId());
        PageContext pageContext = pageCeo != null ? new PageContext(pageCeo) : new PageContext(blogEntry.getSpace());
        String storageFormattedContent = this.migrateStorageFormat(pageContext, blogEntry.getContent());
        blogEntry.setContent(storageFormattedContent);
        return this.blogsService.storeBlogEntry(blogEntry);
    }

    @Override
    public String[] getGroups(String token) throws RemoteException {
        return this.usersService.getGroups();
    }

    @Override
    public boolean hasGroup(String token, String groupname) throws NotPermittedException {
        return this.usersService.hasGroup(groupname);
    }

    @Override
    public boolean addGroup(String token, String groupname) throws RemoteException {
        return this.usersService.addGroup(groupname);
    }

    @Override
    public boolean removeGroup(String token, String groupname, String defaultGroupName) throws RemoteException {
        return this.usersService.removeGroup(groupname, defaultGroupName);
    }

    @Override
    public boolean removeAllPermissionsForGroup(String token, String groupname) throws RemoteException {
        return this.usersService.removeAllPermissionsForGroup(groupname);
    }

    @Override
    public String[] getUserGroups(String token, String username) throws RemoteException {
        return this.usersService.getUserGroups(username);
    }

    @Override
    public boolean addUserToGroup(String token, String username, String groupname) throws RemoteException {
        return this.usersService.addUserToGroup(username, groupname);
    }

    @Override
    public boolean removeUserFromGroup(String token, String username, String groupname) throws RemoteException {
        return this.usersService.removeUserFromGroup(username, groupname);
    }

    @Override
    public RemoteConfluenceUser getUserByName(String token, String username) throws RemoteException {
        return this.usersService.getUserByName(username);
    }

    @Override
    public RemoteConfluenceUser getUserByKey(String token, String key) throws RemoteException {
        return this.usersService.getUserByKey(key);
    }

    @Override
    public boolean setUserPreferenceBoolean(String token, String username, String key, boolean value) throws RemoteException {
        return this.usersService.setUserPreferenceBoolean(username, key, value);
    }

    @Override
    public boolean getUserPreferenceBoolean(String token, String username, String key) throws RemoteException {
        return this.usersService.getUserPreferenceBoolean(username, key);
    }

    @Override
    public boolean setUserPreferenceLong(String token, String username, String key, long value) throws RemoteException {
        return this.usersService.setUserPreferenceLong(username, key, value);
    }

    @Override
    public long getUserPreferenceLong(String token, String username, String key) throws RemoteException {
        return this.usersService.getUserPreferenceLong(username, key);
    }

    @Override
    public boolean setUserPreferenceString(String token, String username, String key, String value) throws RemoteException {
        return this.usersService.setUserPreferenceString(username, key, value);
    }

    @Override
    public String getUserPreferenceString(String token, String username, String key) throws RemoteException {
        return this.usersService.getUserPreferenceString(username, key);
    }

    @Override
    public boolean hasUser(String token, String username) {
        return this.usersService.hasUser(username);
    }

    @Override
    public void addUser(String token, RemoteUser remoteUser, String password) throws RemoteException {
        this.usersService.addUser(remoteUser, password, this.mailServerManager.isDefaultSMTPMailServerDefined());
    }

    @Override
    public void addUser(String token, RemoteUser remoteUser, String password, boolean notifyUser) throws RemoteException {
        this.usersService.addUser(remoteUser, password, notifyUser);
    }

    @Override
    public boolean removeUser(String token, String username) throws RemoteException {
        return this.usersService.removeUser(username);
    }

    @Override
    public boolean editUser(String token, RemoteUser remoteUser) throws RemoteException {
        return this.usersService.editUser(remoteUser);
    }

    @Override
    public boolean deactivateUser(String token, String username) throws RemoteException {
        return this.usersService.deactivateUser(username);
    }

    @Override
    public boolean reactivateUser(String token, String username) throws RemoteException {
        return this.usersService.reactivateUser(username);
    }

    @Override
    public boolean isActiveUser(String token, String username) throws RemoteException {
        return this.usersService.isActiveUser(username);
    }

    @Override
    public String[] getActiveUsers(String token, boolean viewAll) throws RemoteException {
        return this.usersService.getActiveUsers(viewAll);
    }

    @Override
    public boolean setUserInformation(String token, RemoteUserInformation userInfo) throws RemoteException {
        return this.usersService.setUserInformation(userInfo);
    }

    @Override
    public RemoteUserInformation getUserInformation(String token, String username) throws RemoteException {
        return this.usersService.getUserInformation(username);
    }

    @Override
    public boolean changeMyPassword(String token, String oldPass, String newPass) throws RemoteException {
        return this.usersService.changeMyPassword(token, oldPass, newPass);
    }

    @Override
    public boolean changeUserPassword(String token, String username, String newPass) throws RemoteException {
        return this.usersService.changeUserPassword(username, newPass);
    }

    @Override
    public boolean addProfilePicture(String token, String userName, String fileName, String mimeType, byte[] pictureData) throws RemoteException {
        return this.usersService.addProfilePicture(userName, fileName, mimeType, pictureData);
    }

    @Override
    public boolean renameUser(String token, String oldUsername, String newUsername) throws RemoteException {
        return this.usersService.renameUser(oldUsername, newUsername);
    }

    @Override
    public String[] renameUsers(String token, Map<String, String> oldUsernamesToNewUsernames) {
        return this.usersService.renameUsers(oldUsernamesToNewUsernames);
    }

    @Override
    public RemoteServerInfo getServerInfo(String token) throws RemoteException {
        return this.adminSoapService.getServerInfo();
    }

    @Override
    public String exportSpace(String token, String spaceKey, String exportType) throws RemoteException {
        return this.spacesService.exportSpace(spaceKey, exportType);
    }

    @Override
    public String exportSpace(String token, String spaceKey, String exportType, boolean exportAll) throws RemoteException {
        return this.spacesService.exportSpace(spaceKey, exportType, exportAll);
    }

    @Override
    public String exportSite(String token, boolean exportAttachments) throws RemoteException {
        return this.adminSoapService.exportSite(exportAttachments);
    }

    @Override
    public String performBackup(String token, boolean exportAttachments) throws RemoteException {
        return this.adminSoapService.performBackup(exportAttachments);
    }

    @Override
    public boolean importSpace(String token, byte[] importData) throws RemoteException {
        return this.adminSoapService.importSpace(importData);
    }

    @Override
    public boolean flushIndexQueue(String token) throws RemoteException {
        return this.adminSoapService.flushIndexQueue();
    }

    @Override
    public boolean clearIndexQueue(String token) throws RemoteException {
        return this.adminSoapService.clearIndexQueue();
    }

    @Override
    public boolean recoverMainIndex(String token) throws RemoteException {
        return this.adminSoapService.recoverIndex();
    }

    @Override
    public RemoteClusterInformation getClusterInformation(String token) {
        return this.adminSoapService.getClusterInformation();
    }

    @Override
    public RemoteNodeStatus[] getClusterNodeStatuses(String token) throws RemoteException {
        return this.adminSoapService.getClusterNodeStatuses();
    }

    @Override
    public RemoteLabel[] getLabelsById(String token, long objectId) throws RemoteException {
        return this.labelsSoapService.getLabelsById(objectId);
    }

    @Override
    public RemoteLabel[] getMostPopularLabels(String token, int maxCount) throws RemoteException {
        return this.labelsSoapService.getMostPopularLabels(maxCount);
    }

    @Override
    public RemoteLabel[] getMostPopularLabelsInSpace(String token, String spaceKey, int maxCount) throws RemoteException {
        return this.labelsSoapService.getMostPopularLabelsInSpace(spaceKey, maxCount);
    }

    @Override
    public RemoteLabel[] getRecentlyUsedLabels(String token, int maxResults) {
        return this.labelsSoapService.getRecentlyUsedLabels(maxResults);
    }

    @Override
    public RemoteLabel[] getRecentlyUsedLabelsInSpace(String token, String spaceKey, int maxResults) throws RemoteException {
        return this.labelsSoapService.getRecentlyUsedLabelsInSpace(spaceKey, maxResults);
    }

    @Override
    public RemoteSpace[] getSpacesWithLabel(String token, String labelName) throws RemoteException {
        return this.labelsSoapService.getSpacesWithLabel(labelName);
    }

    @Override
    public RemoteLabel[] getRelatedLabels(String token, String labelName, int maxResults) throws RemoteException {
        return this.labelsSoapService.getRelatedLabels(labelName, maxResults);
    }

    @Override
    public RemoteLabel[] getRelatedLabelsInSpace(String token, String labelName, String spaceKey, int maxResults) throws RemoteException {
        return this.labelsSoapService.getRelatedLabelsInSpace(labelName, spaceKey, maxResults);
    }

    @Override
    public RemoteLabel[] getLabelsByDetail(String token, String labelName, String namespace, String spaceKey, String owner) throws RemoteException {
        return this.labelsSoapService.getLabelsByDetail(labelName, namespace, spaceKey, owner);
    }

    @Override
    public RemoteSearchResult[] getLabelContentById(String token, long labelId) throws RemoteException {
        return this.labelsSoapService.getLabelContentById(labelId);
    }

    @Override
    public RemoteSearchResult[] getLabelContentByName(String token, String labelName) throws RemoteException {
        return this.labelsSoapService.getLabelContentByName(labelName);
    }

    @Override
    public RemoteSearchResult[] getLabelContentByObject(String token, RemoteLabel labelObject) throws RemoteException {
        return this.labelsSoapService.getLabelContentByObject(labelObject);
    }

    @Override
    public RemoteSpace[] getSpacesContainingContentWithLabel(String token, String labelName) throws RemoteException {
        return this.labelsSoapService.getSpacesContainingContentWithLabel(labelName);
    }

    @Override
    public boolean addLabelByName(String token, String labelName, long objectId) throws RemoteException {
        return this.labelsSoapService.addLabelByName(labelName, objectId);
    }

    @Override
    public boolean addLabelById(String token, long labelId, long objectId) throws RemoteException {
        return this.labelsSoapService.addLabelById(labelId, objectId);
    }

    @Override
    public boolean addLabelByObject(String token, RemoteLabel labelObject, long objectId) throws RemoteException {
        return this.labelsSoapService.addLabelByObject(labelObject, objectId);
    }

    @Override
    public boolean addLabelByNameToSpace(String token, String labelName, String spaceKey) throws RemoteException {
        return this.labelsSoapService.addLabelByNameToSpace(labelName, spaceKey);
    }

    @Override
    public boolean removeLabelByName(String token, String labelName, long objectId) throws RemoteException {
        return this.labelsSoapService.removeLabelByName(labelName, objectId);
    }

    @Override
    public boolean removeLabelById(String token, long labelId, long objectId) throws RemoteException {
        return this.labelsSoapService.removeLabelById(labelId, objectId);
    }

    @Override
    public boolean removeLabelByObject(String token, RemoteLabel labelObject, long objectId) throws RemoteException {
        return this.labelsSoapService.removeLabelByObject(labelObject, objectId);
    }

    @Override
    public boolean removeLabelByNameFromSpace(String token, String labelName, String spaceKey) throws RemoteException {
        return this.labelsSoapService.removeLabelByNameFromSpace(labelName, spaceKey);
    }

    @Override
    public RemoteContentPermissionSet[] getContentPermissionSets(String token, long contentId) throws RemoteException {
        return this.pagesService.getContentPermissionSets(contentId);
    }

    @Override
    public RemoteContentPermissionSet getContentPermissionSet(String token, long contentId, String permissionType) throws RemoteException {
        return this.pagesService.getContentPermissionSet(contentId, permissionType);
    }

    @Override
    public boolean setContentPermissions(String token, long contentId, String permissionType, RemoteContentPermission[] remoteContentPermissions) throws RemoteException {
        return this.pagesService.setContentPermissions(contentId, permissionType, remoteContentPermissions);
    }

    @Override
    public boolean setEnableAnonymousAccess(String token, boolean value) throws RemoteException {
        return this.adminSoapService.setEnableAnonymousAccess(value);
    }

    @Override
    public boolean isPluginEnabled(String token, String pluginKey) throws RemoteException {
        return this.adminSoapService.isPluginEnabled(pluginKey);
    }

    @Override
    public boolean isPluginInstalled(String token, String pluginKey) throws RemoteException {
        return this.adminSoapService.isPluginInstalled(pluginKey);
    }

    @Override
    public boolean installPlugin(String token, String pluginFileName, byte[] pluginData) throws RemoteException {
        return this.adminSoapService.installPlugin(pluginFileName, pluginData);
    }

    @Override
    public boolean isDarkFeatureEnabled(String token, String key) {
        return this.adminSoapService.isDarkFeatureEnabled(key);
    }

    @Override
    public boolean startActivity(String token, String key, String user) throws RemoteException {
        return this.adminSoapService.startActivity(key, user);
    }

    @Override
    public boolean stopActivity(String token, String key, String user) throws RemoteException {
        return this.adminSoapService.stopActivity(key, user);
    }

    public void setWikiToXhtmlMigrator(ExceptionTolerantMigrator wikiToXhtmlMigrator) {
        this.wikiToXhtmlMigrator = wikiToXhtmlMigrator;
    }

    public void setXhtmlRoundTripMigrator(ExceptionTolerantMigrator xhtmlRoundTripMigrator) {
        this.xhtmlRoundTripMigrator = xhtmlRoundTripMigrator;
    }

    public void setContentEntityObjectDao(ContentEntityObjectDao contentEntityObjectDao) {
        this.contentEntityObjectDao = contentEntityObjectDao;
    }

    public void setMailServerManager(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }
}

