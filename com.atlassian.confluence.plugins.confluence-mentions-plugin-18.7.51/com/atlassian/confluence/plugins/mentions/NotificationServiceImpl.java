/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.activation.MimetypesFileTypeMap
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.mentions;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.mentions.MentionsExcerptor;
import com.atlassian.confluence.plugins.mentions.NotificationService;
import com.atlassian.confluence.plugins.mentions.api.ConfluenceMentionEvent;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import javax.activation.MimetypesFileTypeMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationServiceImpl
implements NotificationService {
    private static final MimetypesFileTypeMap fileTypeMap;
    private static final Logger log;
    private final EventPublisher eventPublisher;
    private final UserManager userManager;
    private final Renderer renderer;
    private final MentionsExcerptor mentionsExcerptor;

    public NotificationServiceImpl(EventPublisher eventPublisher, UserManager userManager, Renderer renderer, MentionsExcerptor mentionsExcerptor) {
        this.userManager = userManager;
        this.eventPublisher = eventPublisher;
        this.renderer = renderer;
        this.mentionsExcerptor = mentionsExcerptor;
    }

    @Override
    public void sendMentions(Set<ConfluenceUser> mentionedUsers, ConfluenceUser author, ContentEntityObject content) {
        if (mentionedUsers.isEmpty()) {
            return;
        }
        PageContext renderContext = content.toPageContext();
        renderContext.setOutputType("email");
        DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)renderContext);
        ContentTypeEnum contentType = content.getTypeEnum();
        switch (contentType) {
            case COMMENT: {
                for (ConfluenceUser recipient : mentionedUsers) {
                    try {
                        AutoCloseable asUserCloseable = AuthenticatedUserThreadLocal.asUser((ConfluenceUser)recipient);
                        try {
                            String commentMention = content.getBodyContent().getBodyType() == BodyType.XHTML ? this.renderer.render(content, (ConversionContext)conversionContext) : null;
                            this.sendUserMention(recipient, author, content, commentMention);
                        }
                        finally {
                            if (asUserCloseable == null) continue;
                            asUserCloseable.close();
                        }
                    }
                    catch (Exception ex) {
                        log.error("Error sending comment mention. recipient: {}, author:{}, content id: {}", new Object[]{recipient.getKey(), author.getKey(), content.getId(), ex});
                    }
                }
                break;
            }
            case PAGE: 
            case BLOG: 
            case CUSTOM: {
                for (ConfluenceUser recipient : mentionedUsers) {
                    String excerpt = this.mentionsExcerptor.getExcerpt(content, recipient);
                    try {
                        AutoCloseable asUserCloseable = AuthenticatedUserThreadLocal.asUser((ConfluenceUser)recipient);
                        try {
                            String mentionHtml = StringUtils.isNotBlank((CharSequence)excerpt) ? this.renderer.render(excerpt, (ConversionContext)conversionContext) : null;
                            this.sendUserMention(recipient, author, content, mentionHtml);
                        }
                        finally {
                            if (asUserCloseable == null) continue;
                            asUserCloseable.close();
                        }
                    }
                    catch (Exception ex) {
                        log.error("Error sending custom mention. recipient: {}, author:{}, content id: {}", new Object[]{recipient.getKey(), author.getKey(), content.getId(), ex});
                    }
                }
                break;
            }
        }
    }

    private void sendUserMention(ConfluenceUser recipient, ConfluenceUser author, ContentEntityObject content, String mentionHtml) {
        UserProfile recipientProfile = this.userManager.getUserProfile(recipient.getKey());
        ConfluenceMentionEvent event = new ConfluenceMentionEvent(this, content, recipientProfile, author, mentionHtml);
        this.eventPublisher.publish((Object)event);
    }

    static {
        log = LoggerFactory.getLogger(NotificationServiceImpl.class);
        fileTypeMap = new MimetypesFileTypeMap();
        try {
            fileTypeMap.addMimeTypes(IOUtils.toString((InputStream)BootstrapManager.class.getResourceAsStream("/mime.types")));
        }
        catch (IOException exception) {
            throw new RuntimeException("Unable to load mime types", exception);
        }
    }
}

