/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.follow.FollowManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.CachedContentFinder
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.like.notifications;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.follow.FollowManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.CachedContentFinder;
import com.atlassian.confluence.plugins.like.notifications.AbstractLikeEventRenderContextProvider;
import com.atlassian.confluence.plugins.like.notifications.LikePayload;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import java.util.HashMap;
import java.util.Map;

public class LikeEventEmailRenderContextProvider
extends AbstractLikeEventRenderContextProvider {
    private final Excerpter excerpter;

    public LikeEventEmailRenderContextProvider(CachedContentFinder cachedContentFinder, UserAccessor userAccessor, FollowManager followManager, Excerpter excerpter, LocaleManager localeManager) {
        super(cachedContentFinder, userAccessor, followManager, localeManager);
        this.excerpter = excerpter;
    }

    @Override
    protected Expansion[] getMediumSpecificExpansions(CachedContentFinder cachedContentFinder) {
        return new Expansion[]{new Expansion("space"), new Expansion("history"), new Expansion("container"), cachedContentFinder.exportBody()};
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected Maybe<Map<String, Object>> getMediumSpecificContext(Content content, LikePayload payload, boolean recipientIsAuthor) {
        String excerpt;
        Content subjectContent;
        if (ContentType.COMMENT.equals((Object)payload.getContentType())) {
            if (!(content.getContainer() instanceof Content)) return MaybeNot.becauseOf((String)"Container should exist for comment and should be of type content.", (Object[])new Object[0]);
            subjectContent = (Content)content.getContainer();
        } else {
            subjectContent = content;
        }
        try {
            excerpt = this.excerpter.createExcerpt(content);
        }
        catch (Exception e) {
            return MaybeNot.becauseOfException((Exception)e);
        }
        String emailSummaryI18nKey = "likes.notification.adg.body.user.likes." + (recipientIsAuthor ? "your." : "") + payload.getContentType();
        long messageId = payload.getContentId();
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("subjectContent", subjectContent);
        context.put("contentBody", excerpt);
        context.put("emailSummaryI18nKey", emailSummaryI18nKey);
        context.put("messageId", String.valueOf(messageId));
        return Option.some(context);
    }
}

