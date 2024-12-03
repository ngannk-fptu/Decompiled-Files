/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.event.PluginContentCreatedEvent
 *  com.atlassian.confluence.content.event.PluginContentUpdatedEvent
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentCreateEvent
 *  com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageUpdateEvent
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.dashboard.macros;

import com.atlassian.confluence.content.event.PluginContentCreatedEvent;
import com.atlassian.confluence.content.event.PluginContentUpdatedEvent;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostUpdateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentCreateEvent;
import com.atlassian.confluence.event.events.content.comment.CommentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageUpdateEvent;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.dashboard.macros.ContentMacroNamesParser;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.List;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ContentSaveListener
implements InitializingBean,
DisposableBean {
    private static final String MACRO_NAMES_PROPERTY = "macroNames";
    private final EventPublisher eventPublisher;
    private final ContentMacroNamesParser contentMacroNamesParser;

    public ContentSaveListener(EventPublisher eventListenerRegistrar, ContentMacroNamesParser contentMacroNamesParser) {
        this.eventPublisher = eventListenerRegistrar;
        this.contentMacroNamesParser = contentMacroNamesParser;
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onPageCreateEvent(PageCreateEvent event) {
        this.updateContentMacroNamesProperty(event.getContent());
    }

    @EventListener
    public void onBlogPostCreateEvent(BlogPostCreateEvent event) {
        this.updateContentMacroNamesProperty(event.getContent());
    }

    @EventListener
    public void onPluginContentCreatedEvent(PluginContentCreatedEvent event) {
        this.updateContentMacroNamesProperty((ContentEntityObject)event.getContent());
    }

    @EventListener
    public void onCommentCreateEvent(CommentCreateEvent event) {
        this.updateCommentMacroNamesIfContainerHasMacroNamesProperty(event.getComment());
    }

    @EventListener
    public void onPageUpdateEvent(PageUpdateEvent event) {
        this.updateContentAndPreexistingCommentsMacroNameProperties(event.getContent());
    }

    @EventListener
    public void onBlogPostUpdateEvent(BlogPostUpdateEvent event) {
        this.updateContentAndPreexistingCommentsMacroNameProperties(event.getContent());
    }

    @EventListener
    public void onPluginContentUpdatedEvent(PluginContentUpdatedEvent event) {
        this.updateContentMacroNamesProperty(event.getContent());
    }

    @EventListener
    public void onCommentUpdateEvent(CommentUpdateEvent event) {
        this.updateCommentMacroNamesIfContainerHasMacroNamesProperty(event.getComment());
    }

    private void updateCommentMacroNamesIfContainerHasMacroNamesProperty(Comment comment) {
        if (comment.getContainer() == null || this.hasMacroNamesProperty(comment.getContainer())) {
            this.updateContentMacroNamesProperty((ContentEntityObject)comment);
        }
    }

    private void updateContentAndPreexistingCommentsMacroNameProperties(ContentEntityObject ceo) {
        if (!this.updateContentMacroNamesProperty(ceo)) {
            for (Comment comment : ceo.getComments()) {
                this.updateContentAndPreexistingCommentsMacroNameProperties((ContentEntityObject)comment);
            }
        }
    }

    private boolean updateContentMacroNamesProperty(ContentEntityObject ceo) {
        List<String> macroNames = this.contentMacroNamesParser.getMacroNames(ceo);
        boolean existed = this.hasMacroNamesProperty(ceo);
        ceo.getProperties().removeProperty(MACRO_NAMES_PROPERTY);
        try {
            ContentSaveListener.updateContentMacroNamesProperty(ceo, macroNames);
        }
        catch (IllegalArgumentException ex) {
            ceo.getProperties().removeProperty(MACRO_NAMES_PROPERTY);
            ContentSaveListener.updateContentMacroNamesProperty(ceo, macroNames);
        }
        return existed;
    }

    private static void updateContentMacroNamesProperty(ContentEntityObject ceo, List<String> macroNames) {
        ceo.getProperties().setStringProperty(MACRO_NAMES_PROPERTY, ContentSaveListener.commaJoinMacroNames(macroNames, 255));
    }

    private static String commaJoinMacroNames(Iterable<String> macroNames, int maxLength) {
        StringBuilder builder = new StringBuilder();
        for (String str : macroNames) {
            if (builder.length() >= maxLength) break;
            if (builder.length() > 0) {
                builder.append(",");
            }
            if (builder.length() + str.length() > maxLength) break;
            builder.append(str);
        }
        return builder.toString();
    }

    private boolean hasMacroNamesProperty(ContentEntityObject ceo) {
        String macroNames;
        try {
            macroNames = ceo.getProperties().getStringProperty(MACRO_NAMES_PROPERTY);
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
        return macroNames != null && !macroNames.equals(",");
    }
}

