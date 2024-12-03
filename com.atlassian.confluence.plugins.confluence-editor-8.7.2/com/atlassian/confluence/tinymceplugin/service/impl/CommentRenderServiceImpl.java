/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.JsonContentProperty
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.descriptors.ConfluenceWebItemModuleDescriptor
 *  com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebLabel
 *  com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebLink
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.base.Preconditions
 *  javax.servlet.http.HttpServletRequest
 *  org.jsoup.Jsoup
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.tinymceplugin.service.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.descriptors.ConfluenceWebItemModuleDescriptor;
import com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebLabel;
import com.atlassian.confluence.plugin.descriptor.web.model.ConfluenceWebLink;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.tinymceplugin.events.CommentAsyncRenderSafeEvent;
import com.atlassian.confluence.tinymceplugin.rest.entities.CommentResult;
import com.atlassian.confluence.tinymceplugin.rest.entities.CommentResultWithActions;
import com.atlassian.confluence.tinymceplugin.rest.entities.UserAction;
import com.atlassian.confluence.tinymceplugin.service.CommentRenderService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.renderer.RenderContext;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLStreamException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ExportAsService
@Component
public class CommentRenderServiceImpl
implements CommentRenderService {
    private static final Logger log = LoggerFactory.getLogger(CommentRenderServiceImpl.class);
    public static final String SERIALIZED_HIGHLIGHTS_JSON_PROP = "inline-serialized-highlights";
    private final XhtmlContent xhtmlContent;
    private final WebInterfaceManager webInterfaceManager;
    private final EventPublisher eventPublisher;
    private final ContentPropertyService contentPropertyService;

    public CommentRenderServiceImpl(XhtmlContent xhtmlContent, WebInterfaceManager webInterfaceManager, EventPublisher eventPublisher, ContentPropertyService contentPropertyService) {
        this.xhtmlContent = xhtmlContent;
        this.webInterfaceManager = webInterfaceManager;
        this.eventPublisher = eventPublisher;
        this.contentPropertyService = contentPropertyService;
    }

    @Override
    public CommentResult render(Comment comment, boolean hasActions, HttpServletRequest httpRequest) throws XMLStreamException, XhtmlException {
        return this.render(comment, hasActions, httpRequest, 0, false);
    }

    @Override
    public CommentResult render(Comment comment, boolean hasActions, HttpServletRequest httpRequest, int maxLength, boolean plainTextOnly) throws XMLStreamException, XhtmlException {
        RenderResult renderResult = this.renderComment(comment);
        log.debug("Comment {} is marked as {}", (Object)comment.getIdAsString(), (Object)(renderResult.asyncRenderSafe ? "async-render-safe" : "non-async-render-safe"));
        this.eventPublisher.publish((Object)new CommentAsyncRenderSafeEvent(renderResult.asyncRenderSafe));
        return this.createResult(comment, renderResult, hasActions, httpRequest, maxLength, plainTextOnly);
    }

    private RenderResult renderComment(Comment comment) throws XMLStreamException, XhtmlException {
        log.debug("Rendering comment {}", (Object)comment.getId());
        DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)comment.toPageContext());
        String renderedContent = this.xhtmlContent.convertStorageToView(comment.getBodyAsString(), (ConversionContext)conversionContext);
        return new RenderResult(renderedContent, conversionContext.isAsyncRenderSafe());
    }

    private CommentResult createResult(Comment comment, RenderResult renderResult, boolean hasActions, HttpServletRequest req, int maxLength, boolean plainTextOnly) {
        String renderedContent;
        Comment parent = comment.getParent();
        long parentId = parent == null ? 0L : parent.getId();
        ContentEntityObject container = comment.getContainer();
        Preconditions.checkState((container != null ? 1 : 0) != 0, (Object)"comment container must not be null");
        long contentId = container.getId();
        String string = renderedContent = plainTextOnly ? Jsoup.parse((String)renderResult.renderedContent).text() : renderResult.renderedContent;
        if (maxLength > 0) {
            renderedContent = GeneralUtil.shortenString((String)renderedContent, (int)maxLength);
        }
        CommentResultWithActions.CommentResultWithActionsBuilder builder = new CommentResultWithActions.CommentResultWithActionsBuilder(comment.getId(), renderedContent, contentId, parentId, renderResult.asyncRenderSafe);
        if (comment.isInlineComment()) {
            JsonContentProperty serializedHighlightsJsonProperty = (JsonContentProperty)this.contentPropertyService.find(new Expansion[0]).withContentId(comment.getContentId()).withKey(SERIALIZED_HIGHLIGHTS_JSON_PROP).fetchOrNull();
            String serializedHighlights = serializedHighlightsJsonProperty != null ? serializedHighlightsJsonProperty.getValue().getValue() : "";
            String markerRef = comment.getProperties().getStringProperty("inline-marker-ref");
            builder.setInlineComment(comment.isInlineComment()).setSerializedHighlights(serializedHighlights).setDataRef(markerRef);
        }
        if (hasActions) {
            ArrayList<UserAction> primary = new ArrayList<UserAction>();
            ArrayList<UserAction> secondary = new ArrayList<UserAction>();
            this.populateActions(comment, primary, secondary, req);
            builder.setPrimaryActions(primary).setSecondaryActions(secondary);
        }
        return builder.build();
    }

    private void populateActions(Comment comment, List<UserAction> primary, List<UserAction> secondary, HttpServletRequest req) {
        WebInterfaceContext context = this.getWebInterfaceContext(comment);
        Map contextMap = context.toMap();
        List primaryItems = this.webInterfaceManager.getDisplayableItems("system.comment.action/primary", contextMap);
        for (WebItemModuleDescriptor item : primaryItems) {
            if (!(item instanceof ConfluenceWebItemModuleDescriptor)) continue;
            primary.add(this.convertWebItemToUserAction((ConfluenceWebItemModuleDescriptor)item, contextMap, req));
        }
        List secondaryItems = this.webInterfaceManager.getDisplayableItems("system.comment.action/secondary", contextMap);
        for (WebItemModuleDescriptor item : secondaryItems) {
            if (!(item instanceof ConfluenceWebItemModuleDescriptor)) continue;
            secondary.add(this.convertWebItemToUserAction((ConfluenceWebItemModuleDescriptor)item, contextMap, req));
        }
    }

    private WebInterfaceContext getWebInterfaceContext(Comment comment) {
        DefaultWebInterfaceContext context = new DefaultWebInterfaceContext();
        context.setCurrentUser(AuthenticatedUserThreadLocal.get());
        context.setTargetedUser(context.getCurrentUser());
        ContentEntityObject commentContainer = comment.getContainer();
        Preconditions.checkState((boolean)(commentContainer instanceof AbstractPage), (Object)("commentContainer needs to be AbstractPage, but was " + commentContainer));
        AbstractPage container = (AbstractPage)commentContainer;
        context.setPage(container);
        Space space = container.getSpace();
        context.setSpace(space);
        if (space.isPersonal()) {
            context.setTargetedUser(space.getCreator());
        }
        context.setParameter("tinyUrl", (Object)Boolean.TRUE);
        context.setParameter("viewMode", (Object)Boolean.TRUE);
        context.setComment(comment);
        return context;
    }

    private UserAction convertWebItemToUserAction(ConfluenceWebItemModuleDescriptor item, Map<String, Object> context, HttpServletRequest req) {
        ConfluenceWebLink link = item.getLink();
        ConfluenceWebLabel tooltipWebLabel = item.getTooltip();
        String tooltip = tooltipWebLabel == null ? null : tooltipWebLabel.getDisplayableLabel(req, context);
        ConfluenceWebLabel labelWebLabel = item.getLabel();
        String label = labelWebLabel == null ? null : labelWebLabel.getDisplayableLabel(req, context);
        return new UserAction(link.getId(), label, tooltip, link.getDisplayableUrl(req, context), item.getStyleClass());
    }

    private static class RenderResult {
        final String renderedContent;
        final boolean asyncRenderSafe;

        private RenderResult(String renderedContent, boolean asyncRenderSafe) {
            this.renderedContent = renderedContent;
            this.asyncRenderSafe = asyncRenderSafe;
        }
    }
}

