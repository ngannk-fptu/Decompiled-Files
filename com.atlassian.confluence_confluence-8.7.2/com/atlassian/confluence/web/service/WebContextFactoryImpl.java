/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.collections.CompositeMap
 *  com.atlassian.confluence.velocity.ContextUtils
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.web.service;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.setup.struts.ConfluenceVelocityManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.collections.CompositeMap;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.velocity.ContextUtils;
import com.atlassian.confluence.web.service.WebContextFactory;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.apache.velocity.context.Context;

public class WebContextFactoryImpl
implements WebContextFactory {
    private final ContentEntityManager contentEntityManager;
    private final I18NBeanFactory i18nBeanFactory;
    private final LocaleManager localeManager;
    private final FormatSettingsManager formatSettingsManager;
    private final SpaceManager spaceManager;
    private final TimeZoneManager timeZoneManager;

    public WebContextFactoryImpl(ContentEntityManager contentEntityManager, I18NBeanFactory i18nBeanFactory, LocaleManager localeManager, FormatSettingsManager formatSettingsManager, SpaceManager spaceManager, TimeZoneManager timeZoneManager) {
        this.contentEntityManager = contentEntityManager;
        this.i18nBeanFactory = i18nBeanFactory;
        this.localeManager = localeManager;
        this.formatSettingsManager = formatSettingsManager;
        this.spaceManager = spaceManager;
        this.timeZoneManager = timeZoneManager;
    }

    @Override
    public WebInterfaceContext createWebInterfaceContext(Long contentId) {
        return this.createWebInterfaceContext(contentId, Collections.emptyMap());
    }

    @Override
    public WebInterfaceContext createWebInterfaceContext(Long contentId, Map<String, Object> additionalContext) {
        ContentEntityObject ceo = contentId == null ? null : this.contentEntityManager.getById(contentId);
        return this.createWebInterfaceContext(ceo, additionalContext);
    }

    @Override
    public WebInterfaceContext createWebInterfaceContext(ContentEntityObject content) {
        return this.createWebInterfaceContext(content, Collections.emptyMap());
    }

    @Override
    public WebInterfaceContext createWebInterfaceContext(ContentEntityObject content, Map<String, Object> additionalContext) {
        DefaultWebInterfaceContext webInterfaceContext = this.createEnrichedCustomContext(additionalContext);
        webInterfaceContext.setCurrentUser(AuthenticatedUserThreadLocal.get());
        if (content instanceof AbstractPage) {
            AbstractPage page = (AbstractPage)content;
            webInterfaceContext.setPage(page);
            if (page.getSpace() == null) {
                webInterfaceContext.setSpace(page.getLatestVersion().getSpace());
            } else {
                webInterfaceContext.setSpace(page.getSpace());
            }
        } else if (content instanceof Draft) {
            Draft draft = (Draft)content;
            webInterfaceContext.setParameter("draft", draft);
            webInterfaceContext.setSpace(this.spaceManager.getSpace(draft.getDraftSpaceKey()));
            ContentEntityObject publishedContent = this.contentEntityManager.getById(draft.getPageIdAsLong());
            if (publishedContent instanceof AbstractPage) {
                webInterfaceContext.setPage((AbstractPage)publishedContent);
            }
        } else if (content instanceof Comment) {
            Comment comment = (Comment)content;
            webInterfaceContext.setComment(comment);
            ContentEntityObject owner = comment.getContainer();
            if (owner instanceof AbstractPage) {
                webInterfaceContext.setPage((AbstractPage)owner);
            }
        }
        if (content != null) {
            webInterfaceContext.setParameter("content", content);
            webInterfaceContext.setParameter("contentId", content.getIdAsString());
        }
        return webInterfaceContext;
    }

    private DefaultWebInterfaceContext createEnrichedCustomContext(Map<String, Object> customContext) {
        ImmutableMap.Builder enrichedContextBuilder = ImmutableMap.builder();
        if (customContext != null) {
            ContentEntityObject parentPage;
            Boolean editMode = (Boolean)customContext.get("editMode");
            enrichedContextBuilder.put((Object)"viewMode", (Object)(editMode == null || editMode == false ? 1 : 0));
            if (customContext.containsKey("parentPageId") && (parentPage = this.contentEntityManager.getById(Long.parseLong((String)customContext.get("parentPageId")))) != null) {
                enrichedContextBuilder.put((Object)"parentPage", (Object)parentPage);
            }
        } else {
            enrichedContextBuilder.put((Object)"viewMode", (Object)Boolean.TRUE);
        }
        return DefaultWebInterfaceContext.createFrom(enrichedContextBuilder.build());
    }

    @Override
    public WebInterfaceContext createWebInterfaceContextForSpace(Space space) {
        Objects.requireNonNull(space, "Unknown space");
        ImmutableMap customContext = ImmutableMap.builder().put((Object)"viewMode", (Object)Boolean.TRUE).build();
        DefaultWebInterfaceContext webInterfaceContext = DefaultWebInterfaceContext.createFrom(customContext);
        webInterfaceContext.setCurrentUser(AuthenticatedUserThreadLocal.get());
        webInterfaceContext.setSpace(space);
        return webInterfaceContext;
    }

    @Override
    public WebInterfaceContext createWebInterfaceContextForSpace(String spaceKey) {
        return this.createWebInterfaceContextForSpace(this.spaceManager.getSpace(spaceKey));
    }

    @Override
    public Map<String, Object> createWebPanelTemplateContext(WebInterfaceContext webInterfaceContext, Map<String, Object> additionalContext) {
        Map<String, Object> baseContext = this.createBaseTemplateContext(webInterfaceContext, additionalContext);
        Context confluenceContext = ConfluenceVelocityManager.getConfluenceVelocityContext();
        Map confluenceVelocityContextMap = ContextUtils.toMap((Context)confluenceContext);
        return CompositeMap.of((Map)confluenceVelocityContextMap, baseContext);
    }

    @Override
    public Map<String, Object> createWebItemTemplateContext(WebInterfaceContext webInterfaceContext, Map<String, Object> additionalContext) {
        return this.createBaseTemplateContext(webInterfaceContext, additionalContext);
    }

    @Override
    public Map<String, Object> createTemplateContext(WebInterfaceContext webInterfaceContext, Map<String, Object> additionalContext) {
        return this.createBaseTemplateContext(webInterfaceContext, additionalContext);
    }

    private Map<String, Object> createBaseTemplateContext(WebInterfaceContext webInterfaceContext, Map<String, Object> additionalContext) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        builder.putAll(Maps.filterValues(webInterfaceContext.toMap(), Objects::nonNull));
        GhettoHelper helper = new GhettoHelper(webInterfaceContext.getPage(), new DateFormatter(this.timeZoneManager.getDefaultTimeZone(), this.formatSettingsManager, this.localeManager), webInterfaceContext.getSpace());
        builder.put((Object)"helper", (Object)helper);
        builder.put((Object)"req", (Object)ServletContextThreadLocal.getRequest());
        builder.put((Object)"action", (Object)helper.getAction());
        builder.put((Object)"i18n", (Object)this.i18nBeanFactory.getI18NBean());
        builder.put((Object)"generalUtil", (Object)GeneralUtil.INSTANCE);
        builder.put((Object)"dateFormatter", (Object)helper.getAction().getDateFormatter());
        if (AuthenticatedUserThreadLocal.get() != null) {
            builder.put((Object)"remoteUser", (Object)AuthenticatedUserThreadLocal.get());
        }
        if (additionalContext != null) {
            builder.putAll(Maps.filterValues(additionalContext, Objects::nonNull));
        }
        return builder.build();
    }

    public static class GhettoHelper {
        private AbstractPage page;
        private Space space;
        private GhettoAction action;

        private GhettoHelper(AbstractPage page, DateFormatter dateFormatter, Space space) {
            this.page = page;
            this.space = space != null ? space : (page != null ? page.getSpace() : null);
            this.action = new GhettoAction(dateFormatter);
        }

        public AbstractPage getPage() {
            return this.page;
        }

        public String getSpaceKey() {
            return this.space.getKey();
        }

        public Space getSpace() {
            return this.space;
        }

        public final GhettoAction getAction() {
            return this.action;
        }

        public static class GhettoAction {
            private final DateFormatter dateFormatter;

            private GhettoAction(DateFormatter dateFormatter) {
                this.dateFormatter = dateFormatter;
            }

            public User getRemoteUser() {
                return AuthenticatedUserThreadLocal.get();
            }

            public DateFormatter getDateFormatter() {
                return this.dateFormatter;
            }
        }
    }
}

