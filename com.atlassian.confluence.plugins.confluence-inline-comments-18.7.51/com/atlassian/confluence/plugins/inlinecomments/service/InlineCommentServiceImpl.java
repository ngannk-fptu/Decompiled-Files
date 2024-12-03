/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.content.ContentProperties
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarkerConstants
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.OperationContext
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.highlight.SelectionModificationException
 *  com.atlassian.confluence.plugins.highlight.SelectionModificationException$Type
 *  com.atlassian.confluence.plugins.highlight.SelectionStorageFormatModifier
 *  com.atlassian.confluence.plugins.highlight.model.TextSearch
 *  com.atlassian.confluence.plugins.highlight.model.XMLModification
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.confluence.xhtml.api.XhtmlVisitor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.inlinecomments.service;

import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inlinecommentmarker.InlineCommentMarkerConstants;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.highlight.SelectionModificationException;
import com.atlassian.confluence.plugins.highlight.SelectionStorageFormatModifier;
import com.atlassian.confluence.plugins.highlight.model.TextSearch;
import com.atlassian.confluence.plugins.highlight.model.XMLModification;
import com.atlassian.confluence.plugins.inlinecomments.entities.HistoryPageInlineComment;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentBuilder;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentCreationBean;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentResult;
import com.atlassian.confluence.plugins.inlinecomments.entities.InlineCommentUpdateBean;
import com.atlassian.confluence.plugins.inlinecomments.entities.ResolveProperties;
import com.atlassian.confluence.plugins.inlinecomments.entities.TopLevelInlineComment;
import com.atlassian.confluence.plugins.inlinecomments.events.InlineCommentCreateEvent;
import com.atlassian.confluence.plugins.inlinecomments.events.InlineCommentResolveEvent;
import com.atlassian.confluence.plugins.inlinecomments.helper.InlineCommentPermissionHelper;
import com.atlassian.confluence.plugins.inlinecomments.service.InlineCommentAutoWatchManager;
import com.atlassian.confluence.plugins.inlinecomments.service.InlineCommentMarkerHelper;
import com.atlassian.confluence.plugins.inlinecomments.service.InlineCommentPropertyManager;
import com.atlassian.confluence.plugins.inlinecomments.service.InlineCommentService;
import com.atlassian.confluence.plugins.inlinecomments.utils.InlineCommentUtils;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.confluence.xhtml.api.XhtmlVisitor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class InlineCommentServiceImpl
implements InlineCommentService {
    private static final Logger log = LoggerFactory.getLogger(InlineCommentServiceImpl.class);
    private static final String BLOCK_QUOTE_START_TAG = "<blockquote>";
    private static final String BLOCK_QUOTE_END_TAG = "</blockquote>";
    private final ContentService contentService;
    private final InlineCommentPropertyManager propertyManager;
    private final InlineCommentAutoWatchManager autoWatchManager;
    private final InlineCommentBuilder entityBuilder;
    private final SelectionStorageFormatModifier selectionStorageFormatModifier;
    private final InlineCommentMarkerHelper inlineCommentMarkerHelper;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;
    private final CommentManager commentManager;
    private final InlineCommentPermissionHelper permissionHelper;
    private final FormatConverter formatConverter;
    private final XhtmlContent xhtmlContent;
    private final PageManager pageManager;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;

    public InlineCommentServiceImpl(ContentService contentService, InlineCommentPropertyManager propertyManager, InlineCommentAutoWatchManager autoWatchManager, InlineCommentBuilder inlineCommentBuilder, SelectionStorageFormatModifier selectionStorageFormatModifier, InlineCommentMarkerHelper inlineCommentMarkerHelper, TransactionTemplate transactionTemplate, EventPublisher eventPublisher, CommentManager commentManager, InlineCommentPermissionHelper permissionHelper, FormatConverter formatConverter, PageManager pageManager, XhtmlContent xhtmlContent, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory) {
        this.contentService = contentService;
        this.propertyManager = propertyManager;
        this.autoWatchManager = autoWatchManager;
        this.entityBuilder = inlineCommentBuilder;
        this.selectionStorageFormatModifier = selectionStorageFormatModifier;
        this.inlineCommentMarkerHelper = inlineCommentMarkerHelper;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
        this.commentManager = commentManager;
        this.permissionHelper = permissionHelper;
        this.formatConverter = formatConverter;
        this.xhtmlContent = xhtmlContent;
        this.pageManager = pageManager;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public InlineCommentService.Result create(InlineCommentCreationBean creationBean) {
        if (!this.permissionHelper.hasCreateCommentPermission(creationBean.getContainerId())) {
            return new InlineCommentService.Result(InlineCommentService.Result.Status.NOT_PERMITTED);
        }
        return this.attemptInlineCommentCreateForPage(creationBean);
    }

    @Override
    public InlineCommentService.Result createAsPageLevelComment(InlineCommentCreationBean creationBean) {
        return this.attemptPageLevelCommentCreate(creationBean);
    }

    @Override
    public InlineCommentResult<TopLevelInlineComment> getComment(long commentId) {
        Comment comment = this.commentManager.getComment(commentId);
        if (comment == null) {
            return new InlineCommentResult<TopLevelInlineComment>(InlineCommentResult.Status.NOT_FOUND);
        }
        if (!this.permissionHelper.hasViewCommentPermission((ContentEntityObject)comment)) {
            return new InlineCommentResult<TopLevelInlineComment>(InlineCommentResult.Status.NOT_PERMITTED);
        }
        return new InlineCommentResult<TopLevelInlineComment>(InlineCommentResult.Status.SUCCESS, this.entityBuilder.build(comment));
    }

    @Override
    public InlineCommentResult updateResolveProperty(final Comment comment, final boolean resolved, final Date resolvedTime, final ConfluenceUser user, final boolean isDangling, final boolean publishEvent) {
        boolean hasResolveCommentPermission;
        if (comment == null) {
            return new InlineCommentResult(InlineCommentResult.Status.REQUEST_DATA_INCORRECT);
        }
        final long pageId = comment.getContainer().getId();
        boolean bl = hasResolveCommentPermission = this.permissionHelper.hasCreateCommentPermission(pageId) || isDangling && this.permissionHelper.hasEditPagePermission(pageId);
        if (!hasResolveCommentPermission) {
            return new InlineCommentResult(InlineCommentResult.Status.NOT_PERMITTED);
        }
        try {
            return (InlineCommentResult)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<InlineCommentResult>(){

                public InlineCommentResult doInTransaction() {
                    InlineCommentServiceImpl.this.autoWatchManager.watchContentRespectingUserAutoWatchPreference(pageId);
                    InlineCommentServiceImpl.this.propertyManager.setResolveProperties(comment, resolved, resolvedTime, user, isDangling);
                    ResolveProperties resolveProperties = InlineCommentServiceImpl.this.entityBuilder.buildResolveData(comment.getProperties());
                    if (publishEvent) {
                        InlineCommentServiceImpl.this.eventPublisher.publish((Object)new InlineCommentResolveEvent(this, comment, (OperationContext<?>)DefaultSaveContext.DEFAULT));
                    }
                    return new InlineCommentResult<String>(InlineCommentResult.Status.SUCCESS, resolveProperties.getJsonObjectSerialize());
                }
            });
        }
        catch (Exception e) {
            return InlineCommentResult.getResultFromServiceException(e);
        }
    }

    @Override
    public InlineCommentResult updateResolveProperty(long commentId, boolean resolved, Date resolvedTime, ConfluenceUser user, boolean isDangling, boolean publishEvent) {
        Comment comment = this.commentManager.getComment(commentId);
        return this.updateResolveProperty(comment, resolved, resolvedTime, user, isDangling, publishEvent);
    }

    @Override
    public InlineCommentResult<Long> getInlineCommentId(Long replyId) {
        try {
            Comment comment = this.commentManager.getComment(replyId.longValue());
            if (comment == null || comment.getParent() == null || !comment.isInlineComment()) {
                return new InlineCommentResult<Long>(InlineCommentResult.Status.NOT_FOUND);
            }
            if (!this.permissionHelper.hasViewCommentPermission((ContentEntityObject)comment)) {
                return new InlineCommentResult<Long>(InlineCommentResult.Status.NOT_PERMITTED);
            }
            return new InlineCommentResult<Long>(InlineCommentResult.Status.SUCCESS, comment.getParent().getId());
        }
        catch (Exception e) {
            return InlineCommentResult.getResultFromServiceException(e);
        }
    }

    @Override
    public InlineCommentResult<Collection<TopLevelInlineComment>> getCommentThreads(long containerId) {
        ContentEntityObject container = this.pageManager.getById(containerId);
        if (container == null) {
            return new InlineCommentResult<Collection<TopLevelInlineComment>>(InlineCommentResult.Status.NOT_FOUND);
        }
        if (!this.permissionHelper.hasViewCommentPermission(container)) {
            return new InlineCommentResult<Collection<TopLevelInlineComment>>(InlineCommentResult.Status.NOT_PERMITTED);
        }
        List comments = this.commentManager.getPageComments(containerId, new Date(0L));
        ArrayList<Comment> inlineComments = new ArrayList<Comment>();
        for (Comment comment : comments) {
            ContentProperties properties = comment.getProperties();
            if (!comment.isInlineComment() || !StringUtils.isNotBlank((CharSequence)properties.getStringProperty("inline-marker-ref")) || !StringUtils.isNotBlank((CharSequence)properties.getStringProperty("inline-original-selection"))) continue;
            inlineComments.add(comment);
        }
        return new InlineCommentResult<Collection<TopLevelInlineComment>>(InlineCommentResult.Status.SUCCESS, this.entityBuilder.build(inlineComments));
    }

    @Override
    public InlineCommentResult deleteInlineComment(long commentId) {
        Comment comment = this.commentManager.getComment(commentId);
        if (comment == null) {
            return new InlineCommentResult(InlineCommentResult.Status.REQUEST_DATA_INCORRECT);
        }
        if (!this.permissionHelper.hasDeleteCommentPermission(comment)) {
            return new InlineCommentResult(InlineCommentResult.Status.NOT_PERMITTED);
        }
        try {
            return (InlineCommentResult)this.transactionTemplate.execute(() -> {
                List children = comment.getChildren();
                if (children != null && !children.isEmpty()) {
                    I18NBean i18nBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
                    return new InlineCommentResult<String>(InlineCommentResult.Status.DELETE_FAILED, i18nBean.getText("inline.comments.delete.with.reply.need.reload"));
                }
                this.commentManager.removeCommentFromObject(commentId);
                this.attemptDeleteMarker(comment);
                return new InlineCommentResult<Long>(InlineCommentResult.Status.SUCCESS, commentId);
            });
        }
        catch (ServiceException e) {
            return InlineCommentResult.getResultFromServiceException((Exception)((Object)e));
        }
    }

    protected boolean attemptDeleteMarker(Comment comment) {
        String markerRef = comment.getProperties().getStringProperty("inline-marker-ref");
        try {
            ContentEntityObject container = comment.getContainer();
            return this.selectionStorageFormatModifier.stripTags(container.getId(), container.getLastModificationDate().getTime(), n -> {
                if (n.getNodeType() != 1 || !n.hasAttributes() || n.getAttributes().getNamedItem("ac:ref") == null) {
                    return 2;
                }
                String ref = n.getAttributes().getNamedItem("ac:ref").getNodeValue();
                if (ref.equals(markerRef)) {
                    return 1;
                }
                return 2;
            });
        }
        catch (SAXException e) {
            log.warn("Inline comment marker could not be deleted, something went wrong when parsing the page storage format to DOM");
            log.debug("", (Throwable)e);
        }
        catch (SelectionModificationException e) {
            log.warn("Inline comment marker could not be deleted since the current user doesn't have permission");
            log.debug("user {}", (Object)AuthenticatedUserThreadLocal.get().getName(), (Object)e);
        }
        return false;
    }

    @Override
    public InlineCommentResult<TopLevelInlineComment> updateComment(InlineCommentUpdateBean updateBean) {
        Comment comment = this.commentManager.getComment(updateBean.getId());
        if (comment == null) {
            return new InlineCommentResult<TopLevelInlineComment>(InlineCommentResult.Status.REQUEST_DATA_INCORRECT);
        }
        if (!this.permissionHelper.hasEditCommentPermission(comment)) {
            return new InlineCommentResult<TopLevelInlineComment>(InlineCommentResult.Status.NOT_PERMITTED);
        }
        try {
            this.commentManager.updateCommentContent(comment, this.formatConverter.convertToStorageFormat(updateBean.getBody(), (RenderContext)comment.toPageContext()));
            return new InlineCommentResult<TopLevelInlineComment>(InlineCommentResult.Status.SUCCESS, this.entityBuilder.build(comment));
        }
        catch (Exception e) {
            return InlineCommentResult.getResultFromServiceException(e);
        }
    }

    @Override
    public HistoryPageInlineComment getHistoryPageComment(Long commentId) {
        AbstractPage abstractPage;
        Comment comment = this.commentManager.getComment(commentId.longValue());
        if (comment == null || comment.getParent() != null || !comment.isInlineComment()) {
            throw new IllegalArgumentException(commentId + " must be existing and be a TopLevelInlineComment");
        }
        String markerRef = comment.getProperties().getStringProperty("inline-marker-ref");
        AbstractPage modifiedPage = abstractPage = (AbstractPage)comment.getContainer();
        int diffVersion = 0;
        while (abstractPage != null && !this.hasInlineCommentMarker(abstractPage, markerRef).booleanValue()) {
            modifiedPage = abstractPage;
            abstractPage = (AbstractPage)this.pageManager.getPreviousVersion((ContentEntityObject)modifiedPage);
            ++diffVersion;
        }
        return new HistoryPageInlineComment(comment, modifiedPage, diffVersion);
    }

    private Boolean hasInlineCommentMarker(AbstractPage pageContent, String markerRef) {
        AtomicReference<Boolean> ref = new AtomicReference<Boolean>(false);
        XhtmlVisitor inlineCommentVisitor = (xmlEvent, context) -> {
            StartElement startElement;
            if (xmlEvent.isStartElement() && InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_TAG.equals((startElement = xmlEvent.asStartElement()).getName()) && Objects.equals(markerRef, StaxUtils.getAttributeValue((StartElement)startElement, (QName)InlineCommentMarkerConstants.INLINE_COMMENT_MARKER_STORAGE_REF_ATTR))) {
                ref.set(true);
                return false;
            }
            return true;
        };
        try {
            this.xhtmlContent.handleXhtmlElements(pageContent.getBodyAsString(), (ConversionContext)new DefaultConversionContext((RenderContext)pageContent.toPageContext()), Collections.singletonList(inlineCommentVisitor));
        }
        catch (XhtmlException e) {
            log.warn("Inline comment marker cannot be detected in page, something went wrong when traversing the page storage format");
            log.debug("", (Throwable)e);
        }
        return ref.get();
    }

    private boolean versionsMatch(InlineCommentCreationBean creationBean) {
        AbstractPage pageContainer = this.pageManager.getAbstractPage(creationBean.getContainerId());
        if (creationBean.getContainerVersion() <= 0) {
            log.error("Missing containerVersion field in the request. Skip version validation");
            return true;
        }
        return pageContainer != null && pageContainer.getVersion() == creationBean.getContainerVersion();
    }

    private InlineCommentService.Result attemptInlineCommentCreateForPage(InlineCommentCreationBean creationBean) {
        String generatedMarkerRef = this.inlineCommentMarkerHelper.generateMarkerRef();
        try {
            if (this.isTopLevelComment(creationBean) && !this.insertStorageFormatMarkers(creationBean, generatedMarkerRef)) {
                if (!this.versionsMatch(creationBean)) {
                    return new InlineCommentService.Result(InlineCommentService.Result.Status.STALE_STORAGE_FORMAT);
                }
                return new InlineCommentService.Result(InlineCommentService.Result.Status.CANNOT_MODIFY_STORAGE_FORMAT);
            }
            Comment comment = this.buildCommentEntity(creationBean);
            return (InlineCommentService.Result)this.transactionTemplate.execute(() -> {
                this.commentManager.saveContentEntity((ContentEntityObject)comment, DefaultSaveContext.SUPPRESS_NOTIFICATIONS);
                this.propertyManager.setProperties(comment.getId(), generatedMarkerRef, creationBean.getOriginalSelection(), creationBean.getSerializedHighlights());
                this.autoWatchManager.watchContentRespectingUserAutoWatchPreference(creationBean.getContainerId());
                this.eventPublisher.publish((Object)new InlineCommentCreateEvent(this, comment, (OperationContext<?>)DefaultSaveContext.DEFAULT));
                return new InlineCommentService.Result(InlineCommentService.Result.Status.SUCCESS, comment.getId());
            });
        }
        catch (SAXException e) {
            log.warn(e.getMessage(), (Throwable)e);
            return new InlineCommentService.Result(InlineCommentService.Result.Status.OTHER_FAILURE);
        }
        catch (SelectionModificationException e) {
            if (e.getType() == SelectionModificationException.Type.STALE_OBJECT_TO_MODIFY) {
                return new InlineCommentService.Result(InlineCommentService.Result.Status.STALE_STORAGE_FORMAT);
            }
            log.warn(e.getMessage(), (Throwable)e);
            return new InlineCommentService.Result(InlineCommentService.Result.Status.OTHER_FAILURE);
        }
        catch (PermissionException e) {
            return new InlineCommentService.Result(InlineCommentService.Result.Status.NOT_PERMITTED);
        }
        catch (BadRequestException e) {
            return new InlineCommentService.Result(InlineCommentService.Result.Status.BAD_REQUEST_UTF8_MYSQL_ERROR, e.getMessage());
        }
        catch (ServiceException | XhtmlException | XMLStreamException e) {
            log.error(e.getMessage(), e);
            return new InlineCommentService.Result(InlineCommentService.Result.Status.OTHER_FAILURE);
        }
    }

    private InlineCommentService.Result attemptPageLevelCommentCreate(InlineCommentCreationBean creationBean) {
        Content container = InlineCommentUtils.buildContentProxy(creationBean.getContainerId());
        Content parentComment = InlineCommentUtils.buildContentProxy(ContentType.COMMENT, creationBean.getParentCommentId());
        Content comment = this.buildComment(container, parentComment, this.buildCommentBodyWithQuote(creationBean));
        try {
            Content savedComment = this.contentService.create(comment);
            return new InlineCommentService.Result(InlineCommentService.Result.Status.SUCCESS, savedComment.getId().asLong());
        }
        catch (ServiceException e) {
            if (e instanceof PermissionException) {
                return new InlineCommentService.Result(InlineCommentService.Result.Status.NOT_PERMITTED);
            }
            log.error(e.getMessage(), (Throwable)e);
            return new InlineCommentService.Result(InlineCommentService.Result.Status.OTHER_FAILURE);
        }
    }

    private Content buildComment(Content container, Content parentComment, String editorFormat) {
        return Content.builder((ContentType)ContentType.COMMENT).container((Container)container).body(editorFormat, ContentRepresentation.EDITOR).ancestors(Collections.singletonList(parentComment)).build();
    }

    private Comment buildCommentEntity(InlineCommentCreationBean creationBean) throws XhtmlException {
        Comment comment = new Comment();
        comment.setInlineComment(true);
        AbstractPage pageContainer = (AbstractPage)this.pageManager.getById(creationBean.getContainerId());
        comment.setBodyAsString(this.formatConverter.convertToStorageFormat(creationBean.getBody(), (RenderContext)pageContainer.toPageContext()));
        pageContainer.addComment(comment);
        if (creationBean.getParentCommentId() != 0L) {
            this.commentManager.getComment(creationBean.getParentCommentId()).addChild(comment);
        }
        return comment;
    }

    private boolean isTopLevelComment(InlineCommentCreationBean creationBean) {
        return creationBean.getParentCommentId() <= 0L;
    }

    private boolean insertStorageFormatMarkers(InlineCommentCreationBean creationBean, String generatedMarkerRef) throws XMLStreamException, SAXException, SelectionModificationException {
        return this.selectionStorageFormatModifier.markSelection(creationBean.getContainerId(), creationBean.getLastFetchTime(), new TextSearch(creationBean.getOriginalSelection(), creationBean.getNumMatches(), creationBean.getMatchIndex()), new XMLModification(this.inlineCommentMarkerHelper.toStorageFormat(generatedMarkerRef)));
    }

    private String buildCommentBodyWithQuote(InlineCommentCreationBean creationBean) {
        return BLOCK_QUOTE_START_TAG + creationBean.getOriginalSelection() + BLOCK_QUOTE_END_TAG + creationBean.getBody();
    }
}

