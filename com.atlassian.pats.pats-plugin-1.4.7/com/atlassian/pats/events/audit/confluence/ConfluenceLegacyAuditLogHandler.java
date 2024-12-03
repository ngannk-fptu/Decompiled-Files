/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.audit.AffectedObject
 *  com.atlassian.confluence.api.model.audit.AuditRecord
 *  com.atlassian.confluence.api.model.audit.ChangedValue
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.service.audit.AuditService
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.RequestCacheThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Joiner
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.pats.events.audit.confluence;

import com.atlassian.confluence.api.model.audit.AffectedObject;
import com.atlassian.confluence.api.model.audit.AuditRecord;
import com.atlassian.confluence.api.model.audit.ChangedValue;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.service.audit.AuditService;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.pats.events.audit.AuditLogHandler;
import com.atlassian.pats.events.token.TokenEvent;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;

public class ConfluenceLegacyAuditLogHandler
implements AuditLogHandler {
    private final AuditService auditService;
    private final I18nResolver i18nResolver;
    private final UserAccessor userAccessor;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    public ConfluenceLegacyAuditLogHandler(AuditService auditService, I18nResolver i18nResolver, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        this.auditService = auditService;
        this.i18nResolver = i18nResolver;
        this.userAccessor = userAccessor;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    @Override
    public void logTokenCreated(TokenEvent tokenEvent) {
        this.auditService.storeRecord(this.auditRecord(this.i18nResolver.getText("personal.access.tokens.audit.log.summary.token.created"), tokenEvent));
    }

    @Override
    public void logTokenDeleted(TokenEvent tokenEvent) {
        this.auditService.storeRecord(this.auditRecord(this.i18nResolver.getText("personal.access.tokens.audit.log.summary.token.deleted"), tokenEvent));
    }

    private AuditRecord auditRecord(String summary, TokenEvent tokenEvent) {
        I18NBean confluenceI18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
        return AuditRecord.builder().summary(summary).author(this.getAuthor(confluenceI18NBean)).affectedObject(AffectedObject.builder().name(this.userAccessor.getUserByKey(new UserKey(tokenEvent.getTokenOwnerId())).getName()).objectType(confluenceI18NBean.getText("audit.logging.affected.object.user")).build()).changedValue(ChangedValue.builder().name(this.i18nResolver.getText("personal.access.tokens.audit.log.extra.attribute.name")).newValue(tokenEvent.getTokenName()).build()).category(confluenceI18NBean.getText("audit.logging.category.user.management")).remoteAddress(StringUtils.defaultString((String)this.getRemoteAddress(), (String)"")).build();
    }

    private User getAuthor(I18NBean confluenceI18nBean) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        User author = user != null ? new User(null, user.getName(), user.getFullName(), user.getKey()) : this.systemUser(confluenceI18nBean);
        return author;
    }

    private User systemUser(I18NBean confluenceI18nBean) {
        return new User(null, "", confluenceI18nBean.getText("system.name"), "");
    }

    private String getRemoteAddress() {
        String proxyAddresses = RequestCacheThreadLocal.getXForwardedFor();
        String remoteAddress = RequestCacheThreadLocal.getRemoteAddress();
        if (proxyAddresses == null) {
            return remoteAddress;
        }
        return Joiner.on((char)',').join((Object)proxyAddresses, (Object)remoteAddress, new Object[0]);
    }
}

