/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.content.render.xhtml.view.embed.InlineStyleHelper
 *  com.atlassian.confluence.content.service.CommentService
 *  com.atlassian.confluence.content.service.PageService
 *  com.atlassian.confluence.content.service.SpaceService
 *  com.atlassian.confluence.content.service.comment.CreateCommentCommand
 *  com.atlassian.confluence.content.service.comment.EditCommentCommand
 *  com.atlassian.confluence.content.service.page.MovePageCommand
 *  com.atlassian.confluence.content.service.page.MovePageCommandHelper
 *  com.atlassian.confluence.content.service.page.MovePageCommandHelper$MovePageMode
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPermissionManager
 *  com.atlassian.confluence.core.DefaultDeleteContext
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.DefaultSaveContext$Builder
 *  com.atlassian.confluence.core.OperationContext
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.confluence.core.service.NotValidException
 *  com.atlassian.confluence.core.service.ServiceCommand
 *  com.atlassian.confluence.core.service.ValidationError
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.event.events.search.SearchPerformedEvent
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.DuplicateDataRuntimeException
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.TrashManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.service.DateRangeEnum
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.service.SpaceCategoryEnum
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.security.ContentPermission
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.user.User
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.rpc.soap.services;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.view.embed.InlineStyleHelper;
import com.atlassian.confluence.content.service.CommentService;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.content.service.SpaceService;
import com.atlassian.confluence.content.service.comment.CreateCommentCommand;
import com.atlassian.confluence.content.service.comment.EditCommentCommand;
import com.atlassian.confluence.content.service.page.MovePageCommand;
import com.atlassian.confluence.content.service.page.MovePageCommandHelper;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.DefaultDeleteContext;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.search.SearchPerformedEvent;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DuplicateDataRuntimeException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.TrashManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.VersionMismatchException;
import com.atlassian.confluence.rpc.soap.SoapUtils;
import com.atlassian.confluence.rpc.soap.beans.RemoteAttachment;
import com.atlassian.confluence.rpc.soap.beans.RemoteComment;
import com.atlassian.confluence.rpc.soap.beans.RemoteContentPermission;
import com.atlassian.confluence.rpc.soap.beans.RemoteContentPermissionSet;
import com.atlassian.confluence.rpc.soap.beans.RemoteContentSummaries;
import com.atlassian.confluence.rpc.soap.beans.RemoteContentSummary;
import com.atlassian.confluence.rpc.soap.beans.RemotePage;
import com.atlassian.confluence.rpc.soap.beans.RemotePageHistory;
import com.atlassian.confluence.rpc.soap.beans.RemotePageSummary;
import com.atlassian.confluence.rpc.soap.beans.RemotePageUpdateOptions;
import com.atlassian.confluence.rpc.soap.beans.RemotePermission;
import com.atlassian.confluence.rpc.soap.beans.RemoteSearchResult;
import com.atlassian.confluence.rpc.soap.services.SoapServiceHelper;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.DateRangeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.event.Event;
import com.atlassian.event.EventManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.User;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class PagesSoapService {
    private AttachmentManager attachmentManager;
    private EventManager eventManager;
    private PageManager pageManager;
    private ContentEntityManager contentEntityManager;
    private PermissionManager permissionManager;
    private WikiStyleRenderer wikiStyleRenderer;
    private Renderer viewBodyTypeAwareRenderer;
    private TemplateRenderer templateRenderer;
    private TrashManager trashManager;
    private SoapServiceHelper soapServiceHelper;
    private LocaleManager localeManager;
    private ContentPermissionManager contentPermissionManager;
    private PageService pageService;
    private CommentService commentService;
    private SpaceService spaceService;
    private PredefinedSearchBuilder predefinedSearchBuilder;
    private SearchManager searchManager;
    private I18NBeanFactory i18NBeanFactory;
    private UserAccessor userAccessor;
    private MovePageCommandHelper movePageCommandHelper;
    public static final String __PARANAMER_DATA = "addComment com.atlassian.confluence.rpc.soap.beans.RemoteComment comment \neditComment com.atlassian.confluence.rpc.soap.beans.RemoteComment rComment \nemptyTrash java.lang.String spaceKey \ngetAncestors long pageId \ngetAttachments long pageId \ngetChildren long pageId \ngetComment long commentId \ngetComments long pageId \ngetContentPermissionSet long,java.lang.String contentId,permissionType \ngetContentPermissionSets long contentId \ngetDescendents long pageId \ngetPage long pageId \ngetPage java.lang.String,java.lang.String spaceKey,pageTitle \ngetPageHistory long pageId \ngetPageSummary long pageId \ngetPageSummary java.lang.String,java.lang.String spaceKey,pageTitle \ngetPages java.lang.String spaceKey \ngetPermissions long pageId \ngetTopLevelPages java.lang.String spaceKey \ngetTrashContents java.lang.String,int,int spaceKey,offset,count \nmovePage long,long,java.lang.String sourcePageId,targetPageId,position \nmovePageToTopLevel long,java.lang.String pageId,targetSpaceKey \npurgeFromTrash java.lang.String,long spaceKey,contentId \nremoveComment long commentId \nremovePage long pageId \nremovePageVersion java.lang.String,long token,historicalPageId \nremovePageVersion java.lang.String,long,int token,pageId,version \nrenderContent java.lang.String,long,java.lang.String spaceKey,pageId,newContent \nrenderContent java.lang.String,long,java.lang.String,java.util.Map spaceKey,pageId,newContent,parameters \nsearch java.lang.String,java.util.Map,int query,params,maxResults \nsearch java.lang.String,int query,maxResults \nsetAttachmentManager com.atlassian.confluence.pages.AttachmentManager attachmentManager \nsetCommentServiceTarget com.atlassian.confluence.content.service.CommentService commentService \nsetContentEntityManager com.atlassian.confluence.core.ContentEntityManager contentEntityManager \nsetContentPermissionManager com.atlassian.confluence.core.ContentPermissionManager contentPermissionManager \nsetContentPermissions long,java.lang.String,com.atlassian.confluence.rpc.soap.beans.RemoteContentPermission contentId,permissionType,remoteContentPermissions \nsetEventManager com.atlassian.event.EventManager eventManager \nsetI18NBeanFactory com.atlassian.confluence.util.i18n.I18NBeanFactory i18NBeanFactory \nsetLocaleManager com.atlassian.confluence.languages.LocaleManager localeManager \nsetMovePageCommandHelper com.atlassian.confluence.content.service.page.MovePageCommandHelper movePageCommandHelper \nsetPageManager com.atlassian.confluence.pages.PageManager pageManager \nsetPageServiceTarget com.atlassian.confluence.content.service.PageService pageService \nsetPermissionManager com.atlassian.confluence.security.PermissionManager permissionManager \nsetPredefinedSearchBuilder com.atlassian.confluence.search.service.PredefinedSearchBuilder predefinedSearchBuilder \nsetSearchManager com.atlassian.confluence.search.v2.SearchManager searchManager \nsetSoapServiceHelper com.atlassian.confluence.rpc.soap.services.SoapServiceHelper soapServiceHelper \nsetSpaceService com.atlassian.confluence.content.service.SpaceService spaceService \nsetTemplateRenderer com.atlassian.confluence.renderer.template.TemplateRenderer templateRenderer \nsetTrashManager com.atlassian.confluence.pages.TrashManager trashManager \nsetUserAccessor com.atlassian.confluence.user.UserAccessor userAccessor \nsetViewBodyTypeAwareRenderer com.atlassian.confluence.content.render.xhtml.Renderer viewBodyTypeAwareRenderer \nsetWikiStyleRenderer com.atlassian.renderer.WikiStyleRenderer wikiStyleRenderer \nstorePage com.atlassian.confluence.rpc.soap.beans.RemotePage rpage \nupdatePage com.atlassian.confluence.rpc.soap.beans.RemotePage,com.atlassian.confluence.rpc.soap.beans.RemotePageUpdateOptions rpage,options \n";

    public RemotePageSummary[] getPages(String spaceKey) throws RemoteException {
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        return SoapUtils.getPageSummaries(this.getPermittedEntities(this.pageManager.getPages(space, true)));
    }

    public RemotePage getPage(long pageId) throws RemoteException {
        AbstractPage abstractPage = this.soapServiceHelper.retrieveAbstractPage(pageId);
        Page page = this.castToPage(abstractPage);
        return new RemotePage(page);
    }

    public RemotePageSummary getPageSummary(long pageId) throws RemoteException {
        return new RemotePageSummary((Page)this.soapServiceHelper.retrieveAbstractPage(pageId));
    }

    public RemotePage getPage(String spaceKey, String pageTitle) throws RemoteException {
        return new RemotePage(this.soapServiceHelper.retrievePage(spaceKey, pageTitle));
    }

    public RemotePageSummary getPageSummary(String spaceKey, String pageTitle) throws RemoteException {
        return new RemotePageSummary(this.soapServiceHelper.retrievePage(spaceKey, pageTitle));
    }

    public RemoteComment[] getComments(long pageId) throws RemoteException {
        AbstractPage p = this.soapServiceHelper.retrieveAbstractPage(pageId);
        return SoapUtils.getComments(p.getComments());
    }

    public RemoteComment getComment(long commentId) throws RemoteException {
        ContentEntityObject object = this.contentEntityManager.getById(commentId);
        if (object == null || !this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)object)) {
            throw new RemoteException("You do not have permission to view the comment, or it does not exist.");
        }
        if (!(object instanceof Comment)) {
            throw new RemoteException("Object for given comment ID is not a comment.");
        }
        Comment comment = (Comment)object;
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)comment.getContainer())) {
            throw new RemoteException("You do not have permission to view the comment, or it does not exist.");
        }
        return new RemoteComment(comment);
    }

    public RemoteComment addComment(RemoteComment comment) throws RemoteException {
        CreateCommentCommand command = comment.getParentId() == 0L ? this.commentService.newCreateCommentCommand(comment.getPageId(), comment.getContent(), UUID.randomUUID()) : this.commentService.newCreateCommentCommand(comment.getPageId(), comment.getParentId(), comment.getContent(), UUID.randomUUID());
        this.executeCommand((ServiceCommand)command);
        this.eventManager.publishEvent((Event)new CommentCreateEvent((Object)this, command.getComment(), (OperationContext)DefaultSaveContext.DEFAULT));
        return new RemoteComment(command.getComment());
    }

    public RemoteComment editComment(RemoteComment rComment) throws RemoteException {
        EditCommentCommand command = this.commentService.newEditCommentCommand(rComment.getId(), rComment.getContent());
        this.executeCommand((ServiceCommand)command);
        return new RemoteComment(command.getComment());
    }

    public boolean removeComment(long commentId) throws RemoteException {
        this.executeCommand((ServiceCommand)this.commentService.newDeleteCommentCommand(commentId));
        return true;
    }

    private void executeCommand(ServiceCommand command) throws RemoteException {
        try {
            command.execute();
        }
        catch (NotAuthorizedException e) {
            throw new NotPermittedException("You do not have the permissions to perform this action");
        }
        catch (NotValidException e) {
            throw new RemoteException(this.validationErrorsToString(command));
        }
    }

    private String validationErrorsToString(ServiceCommand command) {
        StringBuffer validationErrors = new StringBuffer();
        boolean first = true;
        for (ValidationError error : command.getValidationErrors()) {
            if (first) {
                first = false;
            } else {
                validationErrors.append(", ");
            }
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            validationErrors.append(this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)user)).getText(error.getMessageKey(), error.getArgs()));
        }
        return validationErrors.toString();
    }

    public RemotePageSummary[] getDescendents(long pageId) throws RemoteException {
        AbstractPage abstractPage = this.soapServiceHelper.retrieveAbstractPage(pageId);
        Page page = this.castToPage(abstractPage);
        return SoapUtils.getPageSummaries(this.getPermittedEntities(this.pageManager.getDescendents(page)));
    }

    public RemotePageSummary[] getTopLevelPages(String spaceKey) throws RemoteException {
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        if (space == null) {
            throw new RemoteException(spaceKey + " is not a space?");
        }
        List permittedPages = this.getPermittedEntities(this.pageManager.getTopLevelPages(space));
        return SoapUtils.getPageSummaries(permittedPages);
    }

    public RemotePageSummary[] getAncestors(long pageId) throws RemoteException {
        AbstractPage abstractPage = this.soapServiceHelper.retrieveAbstractPage(pageId);
        Page page = this.castToPage(abstractPage);
        return SoapUtils.getPageSummaries(this.getPermittedEntities(page.getAncestors()));
    }

    public RemotePageSummary[] getChildren(long pageId) throws RemoteException {
        AbstractPage abstractPage = this.soapServiceHelper.retrieveAbstractPage(pageId);
        Page page = this.castToPage(abstractPage);
        return SoapUtils.getPageSummaries(this.getPermittedEntities(page.getSortedChildren()));
    }

    public RemoteAttachment[] getAttachments(long pageId) throws RemoteException {
        AbstractPage p = this.soapServiceHelper.retrieveAbstractPage(pageId);
        return SoapUtils.getAttachments(this.attachmentManager.getLatestVersionsOfAttachments((ContentEntityObject)p));
    }

    public RemotePageHistory[] getPageHistory(long pageId) throws RemoteException {
        AbstractPage p = this.soapServiceHelper.retrieveAbstractPage(pageId);
        if (!p.isLatestVersion()) {
            throw new VersionMismatchException("This is not the most recent version of this page");
        }
        return SoapUtils.getPageHistory(p, this.pageManager);
    }

    public Boolean movePageToTopLevel(long pageId, String targetSpaceKey) throws RemoteException {
        MovePageCommand command = this.movePageCommandHelper.newMovePageCommand(this.pageService.getIdPageLocator(pageId), this.spaceService.getKeySpaceLocator(targetSpaceKey), MovePageCommandHelper.MovePageMode.LEGACY);
        this.executeCommand((ServiceCommand)command);
        return Boolean.TRUE;
    }

    public Boolean movePage(long sourcePageId, long targetPageId, String position) throws RemoteException {
        MovePageCommand command = this.movePageCommandHelper.newMovePageCommand(this.pageService.getIdPageLocator(sourcePageId), this.pageService.getIdPageLocator(targetPageId), position, MovePageCommandHelper.MovePageMode.LEGACY);
        this.executeCommand((ServiceCommand)command);
        return Boolean.TRUE;
    }

    public Boolean removePage(long pageId) throws RemoteException {
        AbstractPage page = this.soapServiceHelper.retrieveAbstractPage(pageId);
        this.soapServiceHelper.assertCanRemove(page);
        if (!page.isLatestVersion()) {
            throw new RemoteException("You can't remove an old version of the page - remove the current version.");
        }
        if (page.isDeleted()) {
            return Boolean.TRUE;
        }
        this.pageManager.trashPage(page, DefaultDeleteContext.DEFAULT);
        return Boolean.TRUE;
    }

    public Boolean removePageVersion(String token, long historicalPageId) throws RemoteException {
        ServiceCommand removePageVersionCommand = this.pageService.newRemovePageVersionCommand(this.pageService.getIdPageLocator(historicalPageId));
        this.executeCommand(removePageVersionCommand);
        return Boolean.TRUE;
    }

    public Boolean removePageVersion(String token, long pageId, int version) throws RemoteException {
        ServiceCommand removePageVersionCommand = this.pageService.newRemovePageVersionCommand(this.pageService.getPageVersionLocator(pageId, version));
        this.executeCommand(removePageVersionCommand);
        return Boolean.TRUE;
    }

    public RemoteSearchResult[] search(String query, Map params, int maxResults) throws RemoteException {
        SearchResults searchResults;
        String typeValue;
        if (StringUtils.isBlank((CharSequence)query)) {
            throw new RemoteException((Throwable)new IllegalArgumentException("A query string must be supplied."));
        }
        SearchQueryParameters searchParams = new SearchQueryParameters(query);
        if (params.containsKey("spaceKey")) {
            String spaceKey = (String)params.get("spaceKey");
            SpaceCategoryEnum spaceCategory = SpaceCategoryEnum.get((String)spaceKey);
            if (spaceCategory != null) {
                searchParams.setCategory(spaceCategory);
            } else {
                searchParams.setSpaceKey(spaceKey);
            }
        }
        if (params.containsKey("type") && !"all".equalsIgnoreCase(typeValue = (String)params.get("type"))) {
            ContentTypeEnum contentType = ContentTypeEnum.getByRepresentation((String)typeValue);
            if (contentType == null) {
                throw new RemoteException("The supplied type parameter value of " + typeValue + " is an unknown content type.");
            }
            searchParams.setContentType(contentType);
        }
        if (params.containsKey("modified")) {
            try {
                DateRangeEnum lastModifiedEnum = Enum.valueOf(DateRangeEnum.class, (String)params.get("modified"));
                searchParams.setLastModified(lastModifiedEnum.dateRange());
            }
            catch (IllegalArgumentException ex) {
                throw new RemoteException("The supplied date range parameter was not recognised.", (Throwable)ex);
            }
        }
        if (params.containsKey("contributor")) {
            ConfluenceUser confluenceUser = this.userAccessor.getUserByName((String)params.get("contributor"));
            searchParams.setContributor(confluenceUser);
        }
        ISearch search = this.predefinedSearchBuilder.buildSiteSearch(searchParams, 0, maxResults);
        try {
            searchResults = this.searchManager.search(search);
        }
        catch (IllegalArgumentException e) {
            throw new RemoteException("Invalid query params specified: [" + searchParams + "] produced an invalid search query.", (Throwable)e);
        }
        catch (InvalidSearchException e) {
            throw new RemoteException("Failure executing search for term " + searchParams.getQuery(), (Throwable)e);
        }
        this.eventManager.publishEvent((Event)new SearchPerformedEvent((Object)this, search.getQuery(), (User)AuthenticatedUserThreadLocal.get(), searchResults.size()));
        ArrayList<RemoteSearchResult> remoteSearchResults = new ArrayList<RemoteSearchResult>(searchResults.size());
        for (SearchResult searchResult : searchResults.getAll()) {
            remoteSearchResults.add(new RemoteSearchResult(searchResult, query));
        }
        return remoteSearchResults.toArray(new RemoteSearchResult[remoteSearchResults.size()]);
    }

    public RemoteSearchResult[] search(String query, int maxResults) throws RemoteException {
        return this.search(query, Collections.EMPTY_MAP, maxResults);
    }

    public String renderContent(String spaceKey, long pageId, String newContent) throws RemoteException {
        return this.renderContent(spaceKey, pageId, newContent, null);
    }

    public String renderContent(String spaceKey, long pageId, String newContent, Map parameters) throws RemoteException {
        PageContext pageContext;
        AbstractPage page = null;
        if (pageId > 0L) {
            page = this.soapServiceHelper.retrieveAbstractPage(pageId);
            pageContext = page.toPageContext();
        } else {
            if (!StringUtils.isNotEmpty((CharSequence)spaceKey)) {
                throw new RemoteException("You must specify a space key to render non existent content.");
            }
            pageContext = new PageContext(spaceKey);
        }
        String renderedContent = "";
        if (StringUtils.isNotBlank((CharSequence)newContent)) {
            renderedContent = this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)pageContext, newContent);
        } else if (page != null) {
            renderedContent = this.viewBodyTypeAwareRenderer.render((ContentEntityObject)page, (ConversionContext)new DefaultConversionContext((RenderContext)pageContext));
        }
        if (parameters != null && parameters.containsKey("style") && "clean".equalsIgnoreCase((String)parameters.get("style"))) {
            return "<div id=\"ConfluenceContent\">" + renderedContent + "</div>";
        }
        return new InlineStyleHelper(this.templateRenderer, this.i18NBeanFactory).render(renderedContent, pageContext);
    }

    public RemotePage storePage(RemotePage rpage) throws RemoteException {
        if (rpage.getId() == 0L) {
            return this.createPage(rpage);
        }
        return this.updatePage(rpage, new RemotePageUpdateOptions());
    }

    private RemotePage createPage(RemotePage rpage) throws RemoteException {
        rpage.setTitle(rpage.getTitle().trim());
        Space space = this.soapServiceHelper.retrieveSpace(rpage.getSpace());
        this.soapServiceHelper.assertCanView(space);
        this.soapServiceHelper.assertCanCreatePage(space);
        Page page = new Page();
        page.setSpace(space);
        page.setTitle(rpage.getTitle());
        page.setBodyAsString(rpage.getContent());
        if (rpage.getParentId() > 0L) {
            Page potentialParent = this.pageManager.getPage(rpage.getParentId());
            if (potentialParent == null) {
                throw new RemoteException("The parent ID specified does not exist?");
            }
            potentialParent.addChild(page);
        }
        try {
            this.pageManager.saveContentEntity((ContentEntityObject)page, null);
        }
        catch (DuplicateDataRuntimeException ex) {
            throw new RemoteException(ex.getMessage(), ex.getCause());
        }
        return new RemotePage(page);
    }

    public RemotePage updatePage(RemotePage rpage, RemotePageUpdateOptions options) throws RemoteException {
        rpage.setTitle(rpage.getTitle().trim());
        Page page = (Page)this.soapServiceHelper.retrieveAbstractPage(rpage.getId());
        this.soapServiceHelper.assertCanModify((AbstractPage)page);
        if (!page.getSpace().getKey().equals(rpage.getSpace())) {
            throw new RemoteException("You can't change an existing page's space.");
        }
        if (page.getVersion() != rpage.getVersion()) {
            throw new VersionMismatchException("You're trying to edit an outdated version of that page.");
        }
        Page originalPage = (Page)page.clone();
        boolean storeRequired = false;
        if (!page.getTitle().equals(rpage.getTitle())) {
            page.setTitle(rpage.getTitle());
            storeRequired = true;
        }
        if (!rpage.getContent().equals(page.getBodyAsString())) {
            page.setBodyAsString(rpage.getContent());
            storeRequired = true;
        }
        Page potentialParent = rpage.getParentId() == 0L ? null : this.pageManager.getPage(rpage.getParentId());
        Page existingParent = page.getParent();
        if (potentialParent == null) {
            if (existingParent != null) {
                existingParent.removeChild(page);
                page.setParentPage(null);
                storeRequired = true;
            }
        } else if (existingParent == null) {
            potentialParent.addChild(page);
            storeRequired = true;
        } else if (existingParent.getId() != potentialParent.getId()) {
            existingParent.removeChild(page);
            potentialParent.addChild(page);
            storeRequired = true;
        }
        if (storeRequired) {
            DefaultSaveContext saveContext = ((DefaultSaveContext.Builder)DefaultSaveContext.builder().updateLastModifier(true).suppressNotifications(options.isMinorEdit())).build();
            page.setVersionComment(options.getVersionComment());
            try {
                this.pageManager.saveContentEntity((ContentEntityObject)page, (ContentEntityObject)originalPage, (SaveContext)saveContext);
            }
            catch (IllegalArgumentException ex) {
                throw new RemoteException(ex.getMessage(), ex.getCause());
            }
            this.renderContent(page.getSpaceKey(), page.getId(), page.getBodyAsString());
        }
        return new RemotePage(page);
    }

    public void setSoapServiceHelper(SoapServiceHelper soapServiceHelper) {
        this.soapServiceHelper = soapServiceHelper;
    }

    public boolean setContentPermissions(long contentId, String permissionType, RemoteContentPermission[] remoteContentPermissions) throws RemoteException {
        ContentEntityObject page = this.soapServiceHelper.retrieveContent(contentId);
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, (Object)page)) {
            throw new NotPermittedException("You do not have permissions to set page level restrictions on this page.");
        }
        ArrayList<ContentPermission> contentPermissionList = new ArrayList<ContentPermission>();
        for (int i = 0; i < remoteContentPermissions.length; ++i) {
            ContentPermission newPermission;
            RemoteContentPermission remoteContentPermission = remoteContentPermissions[i];
            if (remoteContentPermission.getType() != null && !permissionType.equals(remoteContentPermission.getType())) {
                throw new RemoteException("Content permission type does not match supplied permission type");
            }
            if (remoteContentPermission.getUserName() != null) {
                newPermission = ContentPermission.createUserPermission((String)permissionType, (String)remoteContentPermission.getUserName());
            } else if (remoteContentPermission.getGroupName() != null) {
                newPermission = ContentPermission.createGroupPermission((String)permissionType, (String)remoteContentPermission.getGroupName());
            } else {
                throw new RemoteException("Content permissions must include either a user or group name");
            }
            if (contentPermissionList.contains(newPermission)) {
                throw new RemoteException("The specified list of permissions contains duplicate permissions.");
            }
            contentPermissionList.add(newPermission);
        }
        this.contentPermissionManager.setContentPermissions(contentPermissionList, page, permissionType);
        return true;
    }

    public RemoteContentPermissionSet[] getContentPermissionSets(long contentId) throws RemoteException {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            throw new NotPermittedException("You do not have permissions to get content permissions restrictions on this page.");
        }
        ContentEntityObject content = this.soapServiceHelper.retrieveContent(contentId);
        if (!(content instanceof AbstractPage) && !(content instanceof Draft)) {
            throw new RemoteException("A matching page cannot be found for contentId: " + contentId + ". Content type found was: " + content.getType());
        }
        ArrayList<RemoteContentPermissionSet> remoteContentPermissionSets = new ArrayList<RemoteContentPermissionSet>();
        if (content.hasPermissions("View")) {
            remoteContentPermissionSets.add(new RemoteContentPermissionSet(content.getContentPermissionSet("View")));
        }
        if (content.hasPermissions("Edit")) {
            remoteContentPermissionSets.add(new RemoteContentPermissionSet(content.getContentPermissionSet("Edit")));
        }
        return remoteContentPermissionSets.toArray(new RemoteContentPermissionSet[remoteContentPermissionSets.size()]);
    }

    public RemoteContentPermissionSet getContentPermissionSet(long contentId, String permissionType) throws RemoteException {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            throw new NotPermittedException("You do not have permissions to get content permissions restrictions on this page.");
        }
        ContentEntityObject content = this.soapServiceHelper.retrieveContent(contentId);
        if (!(content instanceof AbstractPage) && !(content instanceof Draft)) {
            throw new RemoteException("A matching page cannot be found for contentId: " + contentId + ". Content type found was: " + content.getType());
        }
        if (content.hasPermissions(permissionType)) {
            return new RemoteContentPermissionSet(content.getContentPermissionSet(permissionType));
        }
        return new RemoteContentPermissionSet(permissionType);
    }

    public RemotePermission[] getPermissions(long pageId) throws RemoteException {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            throw new NotPermittedException("You do not have permissions to get permissions restrictions on this page.");
        }
        AbstractPage page = this.soapServiceHelper.retrieveAbstractPage(pageId);
        ArrayList permissions = Lists.newArrayList((Iterable)Iterables.concat(this.getContentPermissions(page, "View"), this.getContentPermissions(page, "Edit")));
        return permissions.toArray(new RemotePermission[permissions.size()]);
    }

    public RemoteContentSummaries getTrashContents(String spaceKey, int offset, int count) throws RemoteException {
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        this.soapServiceHelper.assertCanAdminister(space);
        int availableTrash = this.trashManager.getNumberOfItemsInTrash(space);
        List trashItems = this.trashManager.getTrashContents(space, offset, count);
        RemoteContentSummary[] summaries = new RemoteContentSummary[trashItems.size()];
        int i = 0;
        for (ContentEntityObject trashItem : trashItems) {
            summaries[i++] = new RemoteContentSummary(trashItem);
        }
        return new RemoteContentSummaries(availableTrash, offset, summaries);
    }

    public boolean purgeFromTrash(String spaceKey, long contentId) throws RemoteException {
        SpaceContentEntityObject spaceContent;
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        this.soapServiceHelper.assertCanAdminister(space);
        ContentEntityObject content = this.soapServiceHelper.retrieveContent(contentId);
        SpaceContentEntityObject spaceContentEntityObject = spaceContent = content instanceof SpaceContentEntityObject ? (SpaceContentEntityObject)content : null;
        if (spaceContent == null || !spaceContent.getSpace().equals((Object)space)) {
            throw new RemoteException("Content with id " + contentId + " is not in space " + spaceKey);
        }
        if (!content.isDeleted()) {
            throw new RemoteException("Content with id " + contentId + " in space " + spaceKey + " is not in the trash.");
        }
        return this.trashManager.purge(spaceKey, contentId);
    }

    public boolean emptyTrash(String spaceKey) throws RemoteException {
        Space space = this.soapServiceHelper.retrieveSpace(spaceKey);
        this.soapServiceHelper.assertCanAdminister(space);
        this.trashManager.emptyTrash(space);
        return true;
    }

    private List<RemotePermission> getContentPermissions(AbstractPage page, String permissionType) {
        ArrayList<RemotePermission> list = new ArrayList<RemotePermission>();
        if (page.hasPermissions(permissionType)) {
            for (ContentPermission permission : page.getContentPermissionSet(permissionType)) {
                list.add(new RemotePermission(permission));
            }
        }
        return list;
    }

    private List getPermittedEntities(List entities) {
        return this.permissionManager.getPermittedEntities((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, entities);
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setContentPermissionManager(ContentPermissionManager contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }

    public void setPageServiceTarget(PageService pageService) {
        this.pageService = pageService;
    }

    public void setCommentServiceTarget(CommentService commentService) {
        this.commentService = commentService;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public void setPredefinedSearchBuilder(PredefinedSearchBuilder predefinedSearchBuilder) {
        this.predefinedSearchBuilder = predefinedSearchBuilder;
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public void setViewBodyTypeAwareRenderer(Renderer viewBodyTypeAwareRenderer) {
        this.viewBodyTypeAwareRenderer = viewBodyTypeAwareRenderer;
    }

    public void setTemplateRenderer(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public void setTrashManager(TrashManager trashManager) {
        this.trashManager = trashManager;
    }

    private Page castToPage(AbstractPage abstractPage) throws RemoteException {
        if (abstractPage instanceof Page) {
            return (Page)abstractPage;
        }
        throw new RemoteException(abstractPage.getId() + " is not a page?");
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setMovePageCommandHelper(MovePageCommandHelper movePageCommandHelper) {
        this.movePageCommandHelper = movePageCommandHelper;
    }
}

