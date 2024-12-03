/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.InvalidSessionException
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.rpc.xmlrpc.Translator
 *  com.atlassian.core.exception.InfrastructureException
 *  com.google.common.collect.Lists
 *  org.apache.commons.beanutils.BeanUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc.xmlrpc;

import com.atlassian.confluence.rpc.InvalidSessionException;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.soap.ConfluenceSoapService;
import com.atlassian.confluence.rpc.soap.beans.RemoteAttachment;
import com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntry;
import com.atlassian.confluence.rpc.soap.beans.RemoteClusterInformation;
import com.atlassian.confluence.rpc.soap.beans.RemoteComment;
import com.atlassian.confluence.rpc.soap.beans.RemoteContentPermission;
import com.atlassian.confluence.rpc.soap.beans.RemoteLabel;
import com.atlassian.confluence.rpc.soap.beans.RemotePage;
import com.atlassian.confluence.rpc.soap.beans.RemotePageUpdateOptions;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpace;
import com.atlassian.confluence.rpc.soap.beans.RemoteSpaceGroup;
import com.atlassian.confluence.rpc.soap.beans.RemoteUser;
import com.atlassian.confluence.rpc.soap.beans.RemoteUserInformation;
import com.atlassian.confluence.rpc.xmlrpc.ConfluenceXmlRpcHandler;
import com.atlassian.confluence.rpc.xmlrpc.Translator;
import com.atlassian.confluence.rpc.xmlrpc.XmlRpcUtils;
import com.atlassian.core.exception.InfrastructureException;
import com.google.common.collect.Lists;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceXmlRpcHandlerImpl
implements ConfluenceXmlRpcHandler {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceXmlRpcHandlerImpl.class);
    ConfluenceSoapService soapServiceDelegator;
    public static final String __PARANAMER_DATA = "addAnonymousPermissionToSpace java.lang.String,java.lang.String,java.lang.String token,permission,spaceKey \naddAnonymousPermissionsToSpace java.lang.String,java.util.Vector,java.lang.String token,permissions,spaceKey \naddAnonymousUsePermission java.lang.String token \naddAnonymousViewUserProfilePermission java.lang.String token \naddAttachment java.lang.String,java.lang.String,java.util.Hashtable,byte token,contentId,attachment,attachmentData \naddComment java.lang.String,java.util.Hashtable token,comment \naddGlobalPermission java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName \naddGlobalPermissions java.lang.String,java.util.Vector,java.lang.String token,permissions,remoteEntityName \naddGroup java.lang.String,java.lang.String token,groupname \naddLabelById java.lang.String,java.lang.String,java.lang.String token,labelId,objectId \naddLabelByName java.lang.String,java.lang.String,java.lang.String token,labelName,objectId \naddLabelByNameToSpace java.lang.String,java.lang.String,java.lang.String token,labelName,spaceKey \naddLabelByObject java.lang.String,java.util.Hashtable,java.lang.String token,labelObject,objectId \naddPermissionToSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName,spaceKey \naddPermissionsToSpace java.lang.String,java.util.Vector,java.lang.String,java.lang.String token,permissions,remoteEntityName,spaceKey \naddPersonalSpace java.lang.String,java.util.Hashtable,java.lang.String token,space,username \naddProfilePicture java.lang.String,java.lang.String,java.lang.String,java.lang.String,byte token,userName,fileName,mimeType,pictureData \naddSpace java.lang.String,java.util.Hashtable token,space \naddSpaceGroup java.lang.String,java.util.Hashtable token,spaceGroup \naddUser java.lang.String,java.util.Hashtable,java.lang.String token,remoteUser,password \naddUser java.lang.String,java.util.Hashtable,java.lang.String,boolean token,remoteUser,password,notifyUser \naddUserToGroup java.lang.String,java.lang.String,java.lang.String token,username,groupname \nchangeMyPassword java.lang.String,java.lang.String,java.lang.String token,oldPass,newPass \nchangeUserPassword java.lang.String,java.lang.String,java.lang.String token,username,newPass \nclearIndexQueue java.lang.String token \nconvertWikiToStorageFormat java.lang.String,java.lang.String token,wiki \ndeactivateUser java.lang.String,java.lang.String token,username \neditComment java.lang.String,java.util.Hashtable token,comment \neditUser java.lang.String,java.util.Hashtable token,remoteUser \nemptyTrash java.lang.String,java.lang.String token,spaceKey \nexportSite java.lang.String,boolean token,exportAttachments \nexportSpace java.lang.String,java.lang.String,java.lang.String token,spaceKey,exportType \nflushIndexQueue java.lang.String token \ngetActiveUsers java.lang.String,boolean token,viewAll \ngetAncestors java.lang.String,java.lang.String token,pageId \ngetAttachment java.lang.String,java.lang.String,java.lang.String,java.lang.String token,contentId,fileName,version \ngetAttachmentData java.lang.String,java.lang.String,java.lang.String,java.lang.String token,contentId,fileName,version \ngetAttachments java.lang.String,java.lang.String token,pageId \ngetBlogEntries java.lang.String,java.lang.String token,spaceKey \ngetBlogEntry java.lang.String,java.lang.String token,blogEntryId \ngetBlogEntryByDateAndTitle java.lang.String,java.lang.String,int,int,int,java.lang.String token,spaceKey,year,month,dayOfMonth,postTitle \ngetBlogEntryByDayAndTitle java.lang.String,java.lang.String,int,java.lang.String token,spaceKey,dayOfMonth,postTitle \ngetChildren java.lang.String,java.lang.String token,pageId \ngetClusterInformation java.lang.String token \ngetClusterNodeStatuses java.lang.String token \ngetComment java.lang.String,java.lang.String token,commentId \ngetComments java.lang.String,java.lang.String token,pageId \ngetContentPermissionSet java.lang.String,java.lang.String,java.lang.String token,contentId,permissionType \ngetContentPermissionSets java.lang.String,java.lang.String token,contentId \ngetContentPermissions java.lang.String,java.lang.String token,contentId \ngetDescendents java.lang.String,java.lang.String token,pageId \ngetGroups java.lang.String token \ngetLabelContentById java.lang.String,java.lang.String token,labelId \ngetLabelContentByName java.lang.String,java.lang.String token,labelName \ngetLabelContentByObject java.lang.String,java.util.Hashtable token,labelObject \ngetLabelsByDetail java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String token,labelName,namespace,spaceKey,owner \ngetLabelsById java.lang.String,java.lang.String token,objectId \ngetMostPopularLabels java.lang.String,int token,maxCount \ngetMostPopularLabelsInSpace java.lang.String,java.lang.String,int token,spaceKey,maxCount \ngetPage java.lang.String,java.lang.String token,pageId \ngetPage java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageTitle \ngetPageHistory java.lang.String,java.lang.String token,pageId \ngetPagePermissions java.lang.String,java.lang.String token,pageId \ngetPageSummary java.lang.String,java.lang.String token,pageId \ngetPageSummary java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageTitle \ngetPages java.lang.String,java.lang.String token,spaceKey \ngetPermissions java.lang.String,java.lang.String token,spaceKey \ngetPermissionsForUser java.lang.String,java.lang.String,java.lang.String token,spaceKey,userName \ngetRecentlyUsedLabels java.lang.String,int token,maxResults \ngetRecentlyUsedLabelsInSpace java.lang.String,java.lang.String,int token,spaceKey,maxResults \ngetRelatedLabels java.lang.String,java.lang.String,int token,labelName,maxResults \ngetRelatedLabelsInSpace java.lang.String,java.lang.String,java.lang.String,int token,labelName,spaceKey,maxResults \ngetServerInfo java.lang.String token \ngetSpace java.lang.String,java.lang.String token,spaceKey \ngetSpaceGroup java.lang.String,java.lang.String token,spaceGroupKey \ngetSpaceGroups java.lang.String token \ngetSpaceLevelPermissions java.lang.String token \ngetSpacePermissionSet java.lang.String,java.lang.String,java.lang.String token,spaceKey,permissionType \ngetSpacePermissionSets java.lang.String,java.lang.String token,spaceKey \ngetSpaceStatus java.lang.String,java.lang.String token,spaceKey \ngetSpaces java.lang.String token \ngetSpacesContainingContentWithLabel java.lang.String,java.lang.String token,labelName \ngetSpacesInGroup java.lang.String,java.lang.String token,spaceGroupKey \ngetSpacesWithLabel java.lang.String,java.lang.String token,labelName \ngetTopLevelPages java.lang.String,java.lang.String token,spaceKey \ngetTrashContents java.lang.String,java.lang.String,int,int token,spaceKey,offset,maxResults \ngetUser java.lang.String,java.lang.String token,username \ngetUserByKey java.lang.String,java.lang.String token,userKey \ngetUserGroups java.lang.String,java.lang.String token,username \ngetUserInformation java.lang.String,java.lang.String token,username \ngetUserPreferenceBoolean java.lang.String,java.lang.String,java.lang.String token,username,key \ngetUserPreferenceLong java.lang.String,java.lang.String,java.lang.String token,username,key \ngetUserPreferenceString java.lang.String,java.lang.String,java.lang.String token,username,key \ngetWatchersForPage java.lang.String,java.lang.String token,pageId \ngetWatchersForSpace java.lang.String,java.lang.String token,spaceKey \nhasGroup java.lang.String,java.lang.String token,groupname \nhasUser java.lang.String,java.lang.String token,username \nimportSpace java.lang.String,byte token,importData \ninstallPlugin java.lang.String,java.lang.String,byte token,pluginFileName,pluginData \nisActiveUser java.lang.String,java.lang.String token,username \nisDarkFeatureEnabled java.lang.String,java.lang.String token,key \nisPluginEnabled java.lang.String,java.lang.String token,pluginKey \nisPluginInstalled java.lang.String,java.lang.String token,pluginKey \nisWatchingPage java.lang.String,java.lang.String,java.lang.String token,pageId,username \nisWatchingSpace java.lang.String,java.lang.String,java.lang.String token,spaceKey,username \nisWatchingSpaceForType java.lang.String,java.lang.String,java.lang.String,java.lang.String token,spaceKey,contentType,username \nlogin java.lang.String,java.lang.String username,password \nlogout java.lang.String token \nmoveAttachment java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String token,originalContentId,originalFileName,newContentId,newFileName \nmovePage java.lang.String,java.lang.String,java.lang.String,java.lang.String token,sourcePageId,targetPageId,position \nmovePageToTopLevel java.lang.String,java.lang.String,java.lang.String token,pageId,targetSpaceKey \nperformBackup java.lang.String,boolean token,exportAttachments \npurgeFromTrash java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageId \nreactivateUser java.lang.String,java.lang.String token,username \nrecoverMainIndex java.lang.String token \nremoveAllPermissionsForGroup java.lang.String,java.lang.String token,groupname \nremoveAnonymousPermissionFromSpace java.lang.String,java.lang.String,java.lang.String token,permission,spaceKey \nremoveAnonymousUsePermission java.lang.String token \nremoveAnonymousViewUserProfilePermission java.lang.String token \nremoveAttachment java.lang.String,java.lang.String,java.lang.String token,contentId,fileName \nremoveComment java.lang.String,java.lang.String token,commentId \nremoveGlobalPermission java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName \nremoveGroup java.lang.String,java.lang.String,java.lang.String token,groupname,defaultGroupName \nremoveLabelById java.lang.String,java.lang.String,java.lang.String token,labelId,objectId \nremoveLabelByName java.lang.String,java.lang.String,java.lang.String token,labelName,objectId \nremoveLabelByNameFromSpace java.lang.String,java.lang.String,java.lang.String token,labelName,spaceKey \nremoveLabelByObject java.lang.String,java.util.Hashtable,java.lang.String token,labelObject,objectId \nremovePage java.lang.String,java.lang.String token,pageId \nremovePageVersionById java.lang.String,java.lang.String token,historicalPageId \nremovePageVersionByVersion java.lang.String,java.lang.String,int token,pageId,version \nremovePageWatch java.lang.String,java.lang.String token,pageId \nremovePageWatchForUser java.lang.String,java.lang.String,java.lang.String token,pageId,username \nremovePermissionFromSpace java.lang.String,java.lang.String,java.lang.String,java.lang.String token,permission,remoteEntityName,spaceKey \nremoveSpace java.lang.String,java.lang.String token,spaceKey \nremoveSpaceGroup java.lang.String,java.lang.String token,spaceGroupKey \nremoveSpaceWatch java.lang.String,java.lang.String token,spaceKey \nremoveUser java.lang.String,java.lang.String token,username \nremoveUserFromGroup java.lang.String,java.lang.String,java.lang.String token,username,groupname \nrenameUser java.lang.String,java.lang.String,java.lang.String token,oldUsername,newUsername \nrenameUsers java.lang.String,java.util.Hashtable token,oldUsernamesToNewUsernames \nrenderContent java.lang.String,java.lang.String,java.lang.String,java.lang.String token,spaceKey,pageId,newContent \nrenderContent java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.util.Hashtable token,spaceKey,pageId,newContent,renderParameters \nsearch java.lang.String,java.lang.String,int token,query,maxResults \nsearch java.lang.String,java.lang.String,java.util.Hashtable,int token,query,params,maxResults \nsetContentPermissions java.lang.String,java.lang.String,java.lang.String,java.util.Vector token,contentId,permissionType,permissions \nsetEnableAnonymousAccess java.lang.String,java.lang.String token,value \nsetSoapServiceDelegator com.atlassian.confluence.rpc.soap.ConfluenceSoapService soapServiceDelegator \nsetSpaceStatus java.lang.String,java.lang.String,java.lang.String token,spaceKey,statusString \nsetUserInformation java.lang.String,java.util.Hashtable token,userInfo \nsetUserPreferenceBoolean java.lang.String,java.lang.String,java.lang.String,boolean token,username,key,value \nsetUserPreferenceLong java.lang.String,java.lang.String,java.lang.String,java.lang.String token,username,key,value \nsetUserPreferenceString java.lang.String,java.lang.String,java.lang.String,java.lang.String token,username,key,value \nstartActivity java.lang.String,java.lang.String,java.lang.String token,key,user \nstopActivity java.lang.String,java.lang.String,java.lang.String token,key,user \nstoreBlogEntry java.lang.String,java.util.Hashtable token,blogEntryStruct \nstorePage java.lang.String,java.util.Hashtable token,pageStruct \nstoreSpace java.lang.String,java.util.Hashtable token,remoteSpace \nupdatePage java.lang.String,java.util.Hashtable,java.util.Hashtable token,pageStruct,editOptionsStruct \nwatchPage java.lang.String,java.lang.String token,pageId \nwatchPageForUser java.lang.String,java.lang.String,java.lang.String token,pageId,username \nwatchSpace java.lang.String,java.lang.String token,spaceKey \n";

    public void setSoapServiceDelegator(ConfluenceSoapService soapServiceDelegator) {
        this.soapServiceDelegator = soapServiceDelegator;
    }

    @Override
    public String login(String username, String password) throws RemoteException {
        return this.soapServiceDelegator.login(username, password);
    }

    @Override
    public boolean logout(String token) throws RemoteException {
        return this.soapServiceDelegator.logout(token);
    }

    @Override
    public Vector getSpaces(String token) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getSpaces(token));
    }

    @Override
    public Vector getSpacesInGroup(String token, String spaceGroupKey) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getSpacesInGroup(token, spaceGroupKey));
    }

    @Override
    public Vector getBlogEntries(String token, String spaceKey) throws RemoteException {
        try {
            return Translator.makeVector((Object[])this.soapServiceDelegator.getBlogEntries(token, spaceKey));
        }
        catch (RemoteException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RemoteException((Throwable)e);
        }
    }

    @Override
    public Hashtable getBlogEntryByDayAndTitle(String token, String spaceKey, int dayOfMonth, String postTitle) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getBlogEntryByDayAndTitle(token, spaceKey, dayOfMonth, postTitle));
    }

    @Override
    public Hashtable getBlogEntryByDateAndTitle(String token, String spaceKey, int year, int month, int dayOfMonth, String postTitle) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getBlogEntryByDateAndTitle(token, spaceKey, year, month, dayOfMonth, postTitle));
    }

    @Override
    public Vector getComments(String token, String pageId) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getComments(token, this.makePageId(pageId)));
    }

    @Override
    public Hashtable getComment(String token, String commentId) throws InvalidSessionException, RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getComment(token, Long.parseLong(commentId)));
    }

    @Override
    public Hashtable addComment(String token, Hashtable comment) throws InvalidSessionException, NotPermittedException, RemoteException {
        RemoteComment rComment = new RemoteComment();
        comment.remove("created");
        XmlRpcUtils.convertLong(comment, "id");
        XmlRpcUtils.convertLong(comment, "pageId");
        try {
            BeanUtils.populate((Object)rComment, (Map)comment);
        }
        catch (IllegalAccessException e) {
            log.warn("Unable to add comment via XML-RPC: " + e.getMessage(), (Throwable)e);
            throw new InfrastructureException("Unable to add comment: " + e.toString(), (Throwable)e);
        }
        catch (InvocationTargetException e) {
            log.error("Unable to add comment via XML-RPC: " + e.getMessage(), (Throwable)e);
        }
        return Translator.makeStruct((Object)this.soapServiceDelegator.addComment(token, rComment));
    }

    @Override
    public Hashtable editComment(String token, Hashtable comment) throws InvalidSessionException, NotPermittedException, RemoteException {
        RemoteComment rComment = new RemoteComment();
        comment.remove("created");
        comment.remove("modified");
        XmlRpcUtils.convertLong(comment, "id");
        XmlRpcUtils.convertLong(comment, "pageId");
        try {
            BeanUtils.populate((Object)rComment, (Map)comment);
        }
        catch (IllegalAccessException e) {
            log.warn("Unable to edit comment via XML-RPC: " + e.getMessage(), (Throwable)e);
            throw new InfrastructureException("Unable to edit comment: " + e.toString(), (Throwable)e);
        }
        catch (InvocationTargetException e) {
            log.error("Could not edit comment via XML-RPC", (Throwable)e);
        }
        return Translator.makeStruct((Object)this.soapServiceDelegator.editComment(token, rComment));
    }

    @Override
    public boolean removeComment(String token, String commentId) throws InvalidSessionException, NotPermittedException, RemoteException {
        return this.soapServiceDelegator.removeComment(token, Long.parseLong(commentId));
    }

    @Override
    public Vector getTopLevelPages(String token, String spaceKey) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getTopLevelPages(token, spaceKey));
    }

    @Override
    public Vector getAncestors(String token, String pageId) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getAncestors(token, this.makePageId(pageId)));
    }

    @Override
    public Vector getChildren(String token, String pageId) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getChildren(token, this.makePageId(pageId)));
    }

    @Override
    public Vector getDescendents(String token, String pageId) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getDescendents(token, this.makePageId(pageId)));
    }

    @Override
    public Vector getAttachments(String token, String pageId) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getAttachments(token, this.makePageId(pageId)));
    }

    @Override
    public String convertWikiToStorageFormat(String token, String wiki) throws RemoteException {
        return this.soapServiceDelegator.convertWikiToStorageFormat(token, wiki);
    }

    @Override
    public boolean watchPage(String token, String pageId) throws RemoteException {
        return this.soapServiceDelegator.watchPage(token, this.makePageId(pageId));
    }

    @Override
    public boolean watchSpace(String token, String spaceKey) throws RemoteException {
        return this.soapServiceDelegator.watchSpace(token, spaceKey);
    }

    @Override
    public boolean watchPageForUser(String token, String pageId, String username) throws RemoteException {
        return this.soapServiceDelegator.watchPageForUser(token, this.makePageId(pageId), username);
    }

    @Override
    public boolean removePageWatch(String token, String pageId) throws RemoteException {
        return this.soapServiceDelegator.removePageWatch(token, this.makePageId(pageId));
    }

    @Override
    public boolean removeSpaceWatch(String token, String spaceKey) throws RemoteException {
        return this.soapServiceDelegator.removeSpaceWatch(token, spaceKey);
    }

    @Override
    public boolean removePageWatchForUser(String token, String pageId, String username) throws RemoteException {
        return this.soapServiceDelegator.removePageWatchForUser(token, this.makePageId(pageId), username);
    }

    @Override
    public boolean isWatchingPage(String token, String pageId, String username) throws RemoteException {
        return this.soapServiceDelegator.isWatchingPage(token, this.makePageId(pageId), username);
    }

    @Override
    public boolean isWatchingSpace(String token, String spaceKey, String username) throws RemoteException {
        return this.soapServiceDelegator.isWatchingSpace(token, spaceKey, username);
    }

    @Override
    public boolean isWatchingSpaceForType(String token, String spaceKey, String contentType, String username) throws RemoteException {
        return this.soapServiceDelegator.isWatchingSpaceForType(token, spaceKey, contentType, username);
    }

    @Override
    public Vector getWatchersForPage(String token, String pageId) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getWatchersForPage(token, this.makePageId(pageId)));
    }

    @Override
    public Vector getWatchersForSpace(String token, String spaceKey) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getWatchersForSpace(token, spaceKey));
    }

    @Override
    public Hashtable getBlogEntry(String token, String blogEntryId) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getBlogEntry(token, this.makePageId(blogEntryId)));
    }

    @Override
    public Vector getPages(String token, String spaceKey) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getPages(token, spaceKey));
    }

    @Override
    public Hashtable getPage(String token, String pageId) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getPage(token, this.makePageId(pageId)));
    }

    @Override
    public Hashtable getPageSummary(String token, String pageId) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getPageSummary(token, this.makePageId(pageId)));
    }

    @Override
    public Hashtable getPage(String token, String spaceKey, String pageTitle) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getPage(token, spaceKey, pageTitle));
    }

    @Override
    public Hashtable getPageSummary(String token, String spaceKey, String pageTitle) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getPageSummary(token, spaceKey, pageTitle));
    }

    @Override
    public Vector getContentPermissionSets(String token, String contentId) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getContentPermissionSets(token, this.makePageId(contentId)));
    }

    @Override
    public Hashtable getContentPermissionSet(String token, String contentId, String permissionType) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getContentPermissionSet(token, this.makePageId(contentId), permissionType));
    }

    @Override
    public Vector getContentPermissions(String token, String contentId) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getPagePermissions(token, this.makePageId(contentId)));
    }

    @Override
    public Boolean setContentPermissions(String token, String contentId, String permissionType, Vector permissions) throws RemoteException {
        this.soapServiceDelegator.setContentPermissions(token, this.makePageId(contentId), permissionType, this.makePermissionArray(permissions));
        return Boolean.TRUE;
    }

    private RemoteContentPermission[] makePermissionArray(Vector permissions) {
        ArrayList permissionsList = Lists.newArrayListWithExpectedSize((int)permissions.size());
        for (Object permission1 : permissions) {
            Hashtable permissionHash = (Hashtable)permission1;
            try {
                RemoteContentPermission permission = new RemoteContentPermission();
                BeanUtils.populate((Object)permission, (Map)permissionHash);
                permissionsList.add(permission);
            }
            catch (Exception e) {
                log.warn("Unable to create content permission via XML-RPC: " + e.getMessage(), (Throwable)e);
                throw new InfrastructureException("Unable to create content permission: " + e.toString(), (Throwable)e);
            }
        }
        return permissionsList.toArray(new RemoteContentPermission[permissionsList.size()]);
    }

    @Override
    public Vector getPageHistory(String token, String pageId) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getPageHistory(token, this.makePageId(pageId)));
    }

    @Override
    public Hashtable getSpace(String token, String spaceKey) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getSpace(token, spaceKey));
    }

    @Override
    public Vector getPermissions(String token, String spaceKey) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getPermissions(token, spaceKey));
    }

    @Override
    public Vector getPermissionsForUser(String token, String spaceKey, String userName) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getPermissionsForUser(token, spaceKey, userName));
    }

    @Override
    public Vector getSpacePermissionSets(String token, String spaceKey) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getSpacePermissionSets(token, spaceKey));
    }

    @Override
    public Hashtable getSpacePermissionSet(String token, String spaceKey, String permissionType) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getSpacePermissionSet(token, spaceKey, permissionType));
    }

    @Override
    public Vector getPagePermissions(String token, String pageId) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getPagePermissions(token, this.makePageId(pageId)));
    }

    private long makePageId(String pageId) throws RemoteException {
        try {
            return Long.parseLong(pageId);
        }
        catch (NumberFormatException e) {
            throw new RemoteException("You must supply a valid number as the page ID.");
        }
    }

    @Override
    public String renderContent(String token, String spaceKey, String pageId, String newContent) throws RemoteException {
        return this.soapServiceDelegator.renderContent(token, spaceKey, this.makePageId(pageId), newContent);
    }

    @Override
    public String renderContent(String token, String spaceKey, String pageId, String newContent, Hashtable renderParameters) throws RemoteException {
        return this.soapServiceDelegator.renderContent(token, spaceKey, this.makePageId(pageId), newContent, renderParameters);
    }

    @Override
    public Boolean movePageToTopLevel(String token, String pageId, String targetSpaceKey) throws RemoteException {
        return this.soapServiceDelegator.movePageToTopLevel(token, this.makePageId(pageId), targetSpaceKey);
    }

    @Override
    public Boolean movePage(String token, String sourcePageId, String targetPageId, String position) throws RemoteException {
        return this.soapServiceDelegator.movePage(token, this.makePageId(sourcePageId), this.makePageId(targetPageId), position);
    }

    @Override
    public Boolean removePage(String token, String pageId) throws RemoteException {
        return this.soapServiceDelegator.removePage(token, this.makePageId(pageId));
    }

    @Override
    public Boolean removePageVersionById(String token, String historicalPageId) throws RemoteException {
        return this.soapServiceDelegator.removePageVersionById(token, this.makePageId(historicalPageId));
    }

    @Override
    public Boolean removePageVersionByVersion(String token, String pageId, int version) throws RemoteException {
        return this.soapServiceDelegator.removePageVersionByVersion(token, this.makePageId(pageId), version);
    }

    @Override
    public Hashtable getTrashContents(String token, String spaceKey, int offset, int maxResults) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getTrashContents(token, spaceKey, offset, maxResults));
    }

    @Override
    public Boolean purgeFromTrash(String token, String spaceKey, String pageId) throws RemoteException {
        return this.soapServiceDelegator.purgeFromTrash(token, spaceKey, this.makePageId(pageId));
    }

    @Override
    public Boolean emptyTrash(String token, String spaceKey) throws RemoteException {
        return this.soapServiceDelegator.emptyTrash(token, spaceKey);
    }

    @Override
    public Vector search(String token, String query, int maxResults) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.search(token, query, maxResults));
    }

    @Override
    public Vector search(String token, String query, Hashtable params, int maxResults) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.search(token, query, params, maxResults));
    }

    @Override
    public Hashtable storeBlogEntry(String token, Hashtable blogEntryStruct) throws RemoteException {
        RemoteBlogEntry rblog = new RemoteBlogEntry();
        XmlRpcUtils.convertLong(blogEntryStruct, "id");
        XmlRpcUtils.convertInteger(blogEntryStruct, "version");
        try {
            BeanUtils.populate((Object)rblog, (Map)blogEntryStruct);
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("Bad error :)", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return Translator.makeStruct((Object)this.soapServiceDelegator.storeBlogEntry(token, rblog));
    }

    @Override
    public Hashtable storePage(String token, Hashtable pageStruct) throws RemoteException {
        RemotePage rpage = XmlRpcUtils.createRemotePageFromPageStruct(pageStruct);
        return Translator.makeStruct((Object)this.soapServiceDelegator.storePage(token, rpage));
    }

    @Override
    public Hashtable updatePage(String token, Hashtable pageStruct, Hashtable editOptionsStruct) throws RemoteException {
        RemotePage rpage = XmlRpcUtils.createRemotePageFromPageStruct(pageStruct);
        RemotePageUpdateOptions options = new RemotePageUpdateOptions();
        try {
            BeanUtils.populate((Object)options, (Map)editOptionsStruct);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new InfrastructureException("Unable to create a RemotePageUpdateOptions object from given hashtable ", (Throwable)e);
        }
        return Translator.makeStruct((Object)this.soapServiceDelegator.updatePage(token, rpage, options));
    }

    private String getRequiredStringParameter(Hashtable map, String key) {
        if (!map.containsKey(key)) {
            throw new RuntimeException("No '" + key + "' specified.");
        }
        return (String)map.get(key);
    }

    @Override
    public Hashtable addAttachment(String token, String contentId, Hashtable attachment, byte[] attachmentData) throws RemoteException {
        RemoteAttachment attachmentStruct = new RemoteAttachment(Long.parseLong(contentId), this.getRequiredStringParameter(attachment, "fileName"), this.getRequiredStringParameter(attachment, "contentType"), (String)attachment.get("comment"));
        return Translator.makeStruct((Object)this.soapServiceDelegator.addAttachment(token, Long.parseLong(contentId), attachmentStruct, attachmentData));
    }

    @Override
    public Hashtable getAttachment(String token, String contentId, String fileName, String version) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getAttachment(token, Long.parseLong(contentId), fileName, Integer.parseInt(version)));
    }

    @Override
    public byte[] getAttachmentData(String token, String contentId, String fileName, String version) throws RemoteException {
        return this.soapServiceDelegator.getAttachmentData(token, Long.parseLong(contentId), fileName, Integer.parseInt(version));
    }

    @Override
    public boolean removeAttachment(String token, String contentId, String fileName) throws RemoteException, NotPermittedException {
        return this.soapServiceDelegator.removeAttachment(token, Long.parseLong(contentId), fileName);
    }

    @Override
    public boolean moveAttachment(String token, String originalContentId, String originalFileName, String newContentId, String newFileName) throws RemoteException, NotPermittedException {
        return this.soapServiceDelegator.moveAttachment(token, Long.parseLong(originalContentId), originalFileName, Long.parseLong(newContentId), newFileName);
    }

    @Override
    public Hashtable addSpaceGroup(String token, Hashtable spaceGroup) throws RemoteException {
        RemoteSpaceGroup rSpaceGroup = new RemoteSpaceGroup();
        try {
            BeanUtils.populate((Object)rSpaceGroup, (Map)spaceGroup);
        }
        catch (Exception e) {
            log.warn("Unable to create space group via XML-RPC: " + e.getMessage(), (Throwable)e);
            throw new InfrastructureException("Unable to create space group: " + e.toString(), (Throwable)e);
        }
        return Translator.makeStruct((Object)this.soapServiceDelegator.addSpaceGroup(token, rSpaceGroup));
    }

    @Override
    public Hashtable getSpaceGroup(String token, String spaceGroupKey) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getSpaceGroup(token, spaceGroupKey));
    }

    @Override
    public Vector getSpaceGroups(String token) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getSpaceGroups(token));
    }

    @Override
    public boolean removeSpaceGroup(String token, String spaceGroupKey) throws RemoteException {
        return this.soapServiceDelegator.removeSpaceGroup(token, spaceGroupKey);
    }

    @Override
    public Hashtable addSpace(String token, Hashtable space) throws RemoteException {
        RemoteSpace rSpace = new RemoteSpace();
        try {
            BeanUtils.populate((Object)rSpace, (Map)space);
        }
        catch (Exception e) {
            log.warn("Unable to create space via XML-RPC: " + e.getMessage(), (Throwable)e);
            throw new InfrastructureException("Unable to create space: " + e.toString(), (Throwable)e);
        }
        return Translator.makeStruct((Object)this.soapServiceDelegator.addSpace(token, rSpace));
    }

    @Override
    public Hashtable storeSpace(String token, Hashtable remoteSpace) throws RemoteException {
        RemoteSpace rSpace = new RemoteSpace();
        try {
            BeanUtils.populate((Object)rSpace, (Map)remoteSpace);
        }
        catch (Exception e) {
            log.warn("Unable to create space via XML-RPC: " + e.getMessage(), (Throwable)e);
            throw new InfrastructureException("Unable to create space: " + e.toString(), (Throwable)e);
        }
        return Translator.makeStruct((Object)this.soapServiceDelegator.storeSpace(token, rSpace));
    }

    @Override
    public Hashtable addPersonalSpace(String token, Hashtable space, String username) throws RemoteException {
        RemoteSpace rSpace = new RemoteSpace();
        try {
            BeanUtils.populate((Object)rSpace, (Map)space);
        }
        catch (Exception e) {
            log.warn("Unable to create space via XML-RPC: " + e.getMessage(), (Throwable)e);
            throw new InfrastructureException("Unable to create space: " + e.toString(), (Throwable)e);
        }
        return Translator.makeStruct((Object)this.soapServiceDelegator.addPersonalSpace(token, rSpace, username));
    }

    @Override
    public Boolean removeSpace(String token, String spaceKey) throws RemoteException {
        return this.soapServiceDelegator.removeSpace(token, spaceKey);
    }

    @Override
    public Boolean setSpaceStatus(String token, String spaceKey, String statusString) throws RemoteException {
        return this.soapServiceDelegator.setSpaceStatus(token, spaceKey, statusString);
    }

    @Override
    public String getSpaceStatus(String token, String spaceKey) throws RemoteException {
        return this.soapServiceDelegator.getSpaceStatus(token, spaceKey);
    }

    @Override
    public String exportSpace(String token, String spaceKey, String exportType) throws RemoteException {
        return this.soapServiceDelegator.exportSpace(token, spaceKey, exportType);
    }

    @Override
    public boolean importSpace(String token, byte[] importData) throws RemoteException {
        return this.soapServiceDelegator.importSpace(token, importData);
    }

    @Override
    public boolean addPermissionToSpace(String token, String permission, String remoteEntityName, String spaceKey) throws RemoteException {
        return this.soapServiceDelegator.addPermissionToSpace(token, permission, remoteEntityName, spaceKey);
    }

    @Override
    public boolean addPermissionsToSpace(String token, Vector permissions, String remoteEntityName, String spaceKey) throws RemoteException {
        return this.soapServiceDelegator.addPermissionsToSpace(token, this.convertPermissionsVectorToArray(permissions), remoteEntityName, spaceKey);
    }

    @Override
    public boolean addGlobalPermissions(String token, Vector permissions, String remoteEntityName) throws RemoteException {
        return this.soapServiceDelegator.addGlobalPermissions(token, this.convertPermissionsVectorToArray(permissions), remoteEntityName);
    }

    @Override
    public boolean addGlobalPermission(String token, String permission, String remoteEntityName) throws RemoteException {
        return this.soapServiceDelegator.addGlobalPermission(token, permission, remoteEntityName);
    }

    @Override
    public boolean removeGlobalPermission(String token, String permission, String remoteEntityName) throws RemoteException {
        return this.soapServiceDelegator.removeGlobalPermission(token, permission, remoteEntityName);
    }

    @Override
    public boolean addAnonymousUsePermission(String token) throws RemoteException {
        return this.soapServiceDelegator.addAnonymousUsePermission(token);
    }

    @Override
    public boolean removeAnonymousUsePermission(String token) throws RemoteException {
        return this.soapServiceDelegator.removeAnonymousUsePermission(token);
    }

    @Override
    public boolean addAnonymousViewUserProfilePermission(String token) throws RemoteException {
        return this.soapServiceDelegator.addAnonymousViewUserProfilePermission(token);
    }

    @Override
    public boolean removeAnonymousViewUserProfilePermission(String token) throws RemoteException {
        return this.soapServiceDelegator.removeAnonymousViewUserProfilePermission(token);
    }

    private String[] convertPermissionsVectorToArray(Vector permissions) throws RemoteException {
        String[] permissionsArray = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); ++i) {
            Object object = permissions.elementAt(i);
            if (!(object instanceof String)) {
                throw new RemoteException("Unable to add permissions to Space: Permissions must be Strings");
            }
            permissionsArray[i] = (String)object;
        }
        return permissionsArray;
    }

    @Override
    public boolean removePermissionFromSpace(String token, String permission, String remoteEntityName, String spaceKey) throws NotPermittedException, RemoteException {
        return this.soapServiceDelegator.removePermissionFromSpace(token, permission, remoteEntityName, spaceKey);
    }

    @Override
    public boolean addAnonymousPermissionToSpace(String token, String permission, String spaceKey) throws RemoteException {
        return this.soapServiceDelegator.addAnonymousPermissionToSpace(token, permission, spaceKey);
    }

    @Override
    public boolean addAnonymousPermissionsToSpace(String token, Vector permissions, String spaceKey) throws RemoteException {
        return this.soapServiceDelegator.addAnonymousPermissionsToSpace(token, this.convertPermissionsVectorToArray(permissions), spaceKey);
    }

    @Override
    public boolean removeAnonymousPermissionFromSpace(String token, String permission, String spaceKey) throws NotPermittedException, RemoteException {
        return this.soapServiceDelegator.removeAnonymousPermissionFromSpace(token, permission, spaceKey);
    }

    @Override
    public String[] getSpaceLevelPermissions(String token) throws RemoteException {
        return this.soapServiceDelegator.getSpaceLevelPermissions(token);
    }

    @Override
    public Hashtable getServerInfo(String token) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getServerInfo(token));
    }

    @Override
    public String exportSite(String token, boolean exportAttachments) throws RemoteException {
        return this.soapServiceDelegator.exportSite(token, exportAttachments);
    }

    @Override
    public String performBackup(String token, boolean exportAttachments) throws RemoteException {
        return this.soapServiceDelegator.performBackup(token, exportAttachments);
    }

    @Override
    public boolean flushIndexQueue(String token) throws RemoteException {
        return this.soapServiceDelegator.flushIndexQueue(token);
    }

    @Override
    public boolean clearIndexQueue(String token) throws RemoteException {
        return this.soapServiceDelegator.clearIndexQueue(token);
    }

    @Override
    public boolean recoverMainIndex(String token) throws RemoteException {
        return this.soapServiceDelegator.recoverMainIndex(token);
    }

    @Override
    public Hashtable getClusterInformation(String token) throws RemoteException {
        RemoteClusterInformation rci = this.soapServiceDelegator.getClusterInformation(token);
        Hashtable h = Translator.makeStruct((Object)rci);
        return h;
    }

    @Override
    public Vector getClusterNodeStatuses(String token) throws RemoteException {
        Object[] rcni = this.soapServiceDelegator.getClusterNodeStatuses(token);
        Vector v = Translator.makeVector((Object[])rcni);
        return v;
    }

    @Override
    public Vector getGroups(String token) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getGroups(token));
    }

    @Override
    public boolean hasGroup(String token, String groupname) throws InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.hasGroup(token, groupname);
    }

    @Override
    public boolean addGroup(String token, String groupname) throws RemoteException {
        return this.soapServiceDelegator.addGroup(token, groupname);
    }

    @Override
    public boolean removeGroup(String token, String groupname, String defaultGroupName) throws RemoteException {
        return this.soapServiceDelegator.removeGroup(token, groupname, defaultGroupName);
    }

    @Override
    public Vector getUserGroups(String token, String username) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getUserGroups(token, username));
    }

    @Override
    public boolean addUserToGroup(String token, String username, String groupname) throws RemoteException {
        return this.soapServiceDelegator.addUserToGroup(token, username, groupname);
    }

    @Override
    public boolean removeAllPermissionsForGroup(String token, String groupname) throws RemoteException {
        return this.soapServiceDelegator.removeAllPermissionsForGroup(token, groupname);
    }

    @Override
    public boolean removeUserFromGroup(String token, String username, String groupname) throws RemoteException {
        return this.soapServiceDelegator.removeUserFromGroup(token, username, groupname);
    }

    @Override
    public Hashtable getUser(String token, String username) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getUserByName(token, username));
    }

    @Override
    public Hashtable getUserByKey(String token, String userKey) throws RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getUserByKey(token, userKey));
    }

    @Override
    public boolean hasUser(String token, String username) throws InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.hasUser(token, username);
    }

    private RemoteUser buildRemoteUser(Hashtable remoteUser) {
        RemoteUser rUser = new RemoteUser();
        try {
            BeanUtils.populate((Object)rUser, (Map)remoteUser);
        }
        catch (Exception e) {
            log.warn("Unable to create user via XML-RPC: " + e.getMessage(), (Throwable)e);
            throw new InfrastructureException("Unable to create user: " + e.toString(), (Throwable)e);
        }
        return rUser;
    }

    @Override
    public boolean addUser(String token, Hashtable remoteUser, String password) throws RemoteException {
        RemoteUser rUser = this.buildRemoteUser(remoteUser);
        this.soapServiceDelegator.addUser(token, rUser, password);
        return true;
    }

    @Override
    public boolean addUser(String token, Hashtable remoteUser, String password, boolean notifyUser) throws RemoteException {
        RemoteUser rUser = this.buildRemoteUser(remoteUser);
        this.soapServiceDelegator.addUser(token, rUser, password, notifyUser);
        return true;
    }

    @Override
    public boolean removeUser(String token, String username) throws RemoteException {
        return this.soapServiceDelegator.removeUser(token, username);
    }

    @Override
    public boolean editUser(String token, Hashtable remoteUser) throws NotPermittedException, InvalidSessionException, RemoteException {
        RemoteUser rUser = this.buildRemoteUser(remoteUser);
        return this.soapServiceDelegator.editUser(token, rUser);
    }

    @Override
    public boolean deactivateUser(String token, String username) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.deactivateUser(token, username);
    }

    @Override
    public boolean reactivateUser(String token, String username) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.reactivateUser(token, username);
    }

    @Override
    public boolean isActiveUser(String token, String username) throws NotPermittedException, RemoteException {
        return this.soapServiceDelegator.isActiveUser(token, username);
    }

    @Override
    public Vector getActiveUsers(String token, boolean viewAll) throws InvalidSessionException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getActiveUsers(token, viewAll));
    }

    @Override
    public boolean setUserInformation(String token, Hashtable userInfo) throws NotPermittedException, InvalidSessionException, RemoteException {
        RemoteUserInformation rUser = new RemoteUserInformation();
        if (userInfo.containsKey("id")) {
            userInfo.put("id", Long.valueOf((String)userInfo.get("id")));
        }
        if (userInfo.containsKey("version")) {
            userInfo.put("version", Integer.valueOf((String)userInfo.get("version")));
        }
        userInfo.remove("creationDate");
        userInfo.remove("lastModificationDate");
        try {
            BeanUtils.populate((Object)rUser, (Map)userInfo);
        }
        catch (Exception e) {
            log.warn("Unable to edit user information via XML-RPC: " + e.getMessage(), (Throwable)e);
            throw new InfrastructureException("Unable to edit user information: " + e.toString(), (Throwable)e);
        }
        return this.soapServiceDelegator.setUserInformation(token, rUser);
    }

    @Override
    public Hashtable getUserInformation(String token, String username) throws InvalidSessionException, RemoteException {
        return Translator.makeStruct((Object)this.soapServiceDelegator.getUserInformation(token, username));
    }

    @Override
    public boolean changeMyPassword(String token, String oldPass, String newPass) throws InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.changeMyPassword(token, oldPass, newPass);
    }

    @Override
    public boolean changeUserPassword(String token, String username, String newPass) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.changeUserPassword(token, username, newPass);
    }

    @Override
    public boolean addProfilePicture(String token, String userName, String fileName, String mimeType, byte[] pictureData) throws RemoteException {
        return this.soapServiceDelegator.addProfilePicture(token, userName, fileName, mimeType, pictureData);
    }

    @Override
    public boolean renameUser(String token, String oldUsername, String newUsername) throws RemoteException {
        return this.soapServiceDelegator.renameUser(token, oldUsername, newUsername);
    }

    @Override
    public Vector renameUsers(String token, Hashtable oldUsernamesToNewUsernames) throws RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.renameUsers(token, oldUsernamesToNewUsernames));
    }

    @Override
    public boolean setUserPreferenceBoolean(String token, String username, String key, boolean value) throws InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.setUserPreferenceBoolean(token, username, key, value);
    }

    @Override
    public boolean getUserPreferenceBoolean(String token, String username, String key) throws InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.getUserPreferenceBoolean(token, username, key);
    }

    @Override
    public boolean setUserPreferenceLong(String token, String username, String key, String value) throws InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.setUserPreferenceLong(token, username, key, Long.parseLong(value));
    }

    @Override
    public String getUserPreferenceLong(String token, String username, String key) throws InvalidSessionException, RemoteException {
        return Long.toString(this.soapServiceDelegator.getUserPreferenceLong(token, username, key));
    }

    @Override
    public boolean setUserPreferenceString(String token, String username, String key, String value) throws InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.setUserPreferenceString(token, username, key, value);
    }

    @Override
    public String getUserPreferenceString(String token, String username, String key) throws InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.getUserPreferenceString(token, username, key);
    }

    @Override
    public Vector getLabelsById(String token, String objectId) throws NotPermittedException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getLabelsById(token, this.makePageId(objectId)));
    }

    @Override
    public Vector getMostPopularLabels(String token, int maxCount) throws NotPermittedException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getMostPopularLabels(token, maxCount));
    }

    @Override
    public Vector getMostPopularLabelsInSpace(String token, String spaceKey, int maxCount) throws NotPermittedException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getMostPopularLabelsInSpace(token, spaceKey, maxCount));
    }

    @Override
    public Vector getLabelContentById(String token, String labelId) throws NotPermittedException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getLabelContentById(token, Long.parseLong(labelId)));
    }

    @Override
    public Vector getLabelContentByName(String token, String labelName) throws NotPermittedException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getLabelContentByName(token, labelName));
    }

    @Override
    public Vector getLabelContentByObject(String token, Hashtable labelObject) throws NotPermittedException, RemoteException {
        RemoteLabel remoteLabel = new RemoteLabel();
        XmlRpcUtils.convertLong(labelObject, "id");
        try {
            BeanUtils.populate((Object)remoteLabel, (Map)labelObject);
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("Error in populating RemoteLabel bean", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return Translator.makeVector((Object[])this.soapServiceDelegator.getLabelContentByObject(token, remoteLabel));
    }

    @Override
    public Vector getRecentlyUsedLabels(String token, int maxResults) throws InvalidSessionException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getRecentlyUsedLabels(token, maxResults));
    }

    @Override
    public Vector getRecentlyUsedLabelsInSpace(String token, String spaceKey, int maxResults) throws InvalidSessionException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getRecentlyUsedLabelsInSpace(token, spaceKey, maxResults));
    }

    @Override
    public Vector getSpacesWithLabel(String token, String labelName) throws InvalidSessionException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getSpacesWithLabel(token, labelName));
    }

    @Override
    public Vector getRelatedLabels(String token, String labelName, int maxResults) throws InvalidSessionException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getRelatedLabels(token, labelName, maxResults));
    }

    @Override
    public Vector getRelatedLabelsInSpace(String token, String labelName, String spaceKey, int maxResults) throws InvalidSessionException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getRelatedLabelsInSpace(token, labelName, spaceKey, maxResults));
    }

    @Override
    public Vector getLabelsByDetail(String token, String labelName, String namespace, String spaceKey, String owner) throws InvalidSessionException, RemoteException, NotPermittedException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getLabelsByDetail(token, labelName, namespace, spaceKey, owner));
    }

    @Override
    public Vector getSpacesContainingContentWithLabel(String token, String labelName) throws InvalidSessionException, RemoteException {
        return Translator.makeVector((Object[])this.soapServiceDelegator.getSpacesContainingContentWithLabel(token, labelName));
    }

    @Override
    public boolean addLabelByName(String token, String labelName, String objectId) throws NotPermittedException, RemoteException {
        return this.soapServiceDelegator.addLabelByName(token, labelName, Long.parseLong(objectId));
    }

    @Override
    public boolean addLabelById(String token, String labelId, String objectId) throws NotPermittedException, RemoteException {
        return this.soapServiceDelegator.addLabelById(token, Long.parseLong(labelId), Long.parseLong(objectId));
    }

    @Override
    public boolean addLabelByObject(String token, Hashtable labelObject, String objectId) throws NotPermittedException, RemoteException {
        RemoteLabel remoteLabel = new RemoteLabel();
        XmlRpcUtils.convertLong(labelObject, "id");
        try {
            BeanUtils.populate((Object)remoteLabel, (Map)labelObject);
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("Error in populating RemoteLabel bean", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return this.soapServiceDelegator.addLabelByObject(token, remoteLabel, Long.parseLong(objectId));
    }

    @Override
    public boolean addLabelByNameToSpace(String token, String labelName, String spaceKey) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.addLabelByNameToSpace(token, labelName, spaceKey);
    }

    @Override
    public boolean removeLabelByName(String token, String labelName, String objectId) throws NotPermittedException, RemoteException {
        return this.soapServiceDelegator.removeLabelByName(token, labelName, Long.parseLong(objectId));
    }

    @Override
    public boolean removeLabelById(String token, String labelId, String objectId) throws NotPermittedException, RemoteException {
        return this.soapServiceDelegator.removeLabelById(token, Long.parseLong(labelId), Long.parseLong(objectId));
    }

    @Override
    public boolean removeLabelByObject(String token, Hashtable labelObject, String objectId) throws NotPermittedException, RemoteException {
        RemoteLabel remoteLabel = new RemoteLabel();
        XmlRpcUtils.convertLong(labelObject, "id");
        try {
            BeanUtils.populate((Object)remoteLabel, (Map)labelObject);
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("Error in populating RemoteLabel bean", (Throwable)e);
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return this.soapServiceDelegator.removeLabelByObject(token, remoteLabel, Long.parseLong(objectId));
    }

    @Override
    public boolean removeLabelByNameFromSpace(String token, String labelName, String spaceKey) throws NotPermittedException, InvalidSessionException, RemoteException {
        return this.soapServiceDelegator.removeLabelByNameFromSpace(token, labelName, spaceKey);
    }

    @Override
    public boolean setEnableAnonymousAccess(String token, String value) throws RemoteException {
        return this.soapServiceDelegator.setEnableAnonymousAccess(token, Boolean.parseBoolean(value));
    }

    @Override
    public boolean isPluginEnabled(String token, String pluginKey) throws RemoteException {
        return this.soapServiceDelegator.isPluginEnabled(token, pluginKey);
    }

    @Override
    public boolean isPluginInstalled(String token, String pluginKey) throws RemoteException {
        return this.soapServiceDelegator.isPluginInstalled(token, pluginKey);
    }

    @Override
    public boolean installPlugin(String token, String pluginFileName, byte[] pluginData) throws RemoteException {
        return this.soapServiceDelegator.installPlugin(token, pluginFileName, pluginData);
    }

    @Override
    public boolean isDarkFeatureEnabled(String token, String key) throws RemoteException {
        return this.soapServiceDelegator.isDarkFeatureEnabled(token, key);
    }

    @Override
    public boolean startActivity(String token, String key, String user) throws RemoteException {
        return this.soapServiceDelegator.startActivity(token, key, user);
    }

    @Override
    public boolean stopActivity(String token, String key, String user) throws RemoteException {
        return this.soapServiceDelegator.stopActivity(token, key, user);
    }
}

