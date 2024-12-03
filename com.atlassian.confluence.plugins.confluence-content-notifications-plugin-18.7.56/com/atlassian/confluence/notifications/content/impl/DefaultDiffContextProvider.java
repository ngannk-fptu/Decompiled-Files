/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.diff.DiffException
 *  com.atlassian.confluence.diff.Differ
 *  com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.content.impl;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.diff.DiffException;
import com.atlassian.confluence.diff.Differ;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.content.DiffContextProvider;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultDiffContextProvider
implements DiffContextProvider {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forClass(DefaultDiffContextProvider.class);
    private final ContentEntityManager contentEntityManager;
    private final UserAccessor userAccessor;
    private final Differ differ;
    private TransactionTemplate transactionTemplate;

    public DefaultDiffContextProvider(TransactionTemplate transactionTemplate, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, UserAccessor userAccessor, Differ differ) {
        this.transactionTemplate = transactionTemplate;
        this.contentEntityManager = contentEntityManager;
        this.userAccessor = userAccessor;
        this.differ = differ;
    }

    @Override
    public Map<String, Object> generateDiffContext(ContentId current, ContentId original, Option<UserKey> recipient) {
        ConfluenceUser recipientConfluenceUser;
        ConfluenceUserPreferences preferences;
        ImmutableMap.Builder context = ImmutableMap.builder();
        if (recipient.isDefined() && !(preferences = this.userAccessor.getConfluenceUserPreferences((User)(recipientConfluenceUser = this.userAccessor.getUserByKey((UserKey)recipient.get())))).isShowDifferencesInNotificationEmails()) {
            context.put((Object)"showDiffs", (Object)false);
            return context.build();
        }
        context.put((Object)"diffHtml", (Object)this.calculateDiff(current, original));
        context.put((Object)"showDiffs", (Object)true);
        return context.build();
    }

    private String calculateDiff(final ContentId contentId, final ContentId originalId) {
        return (String)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<String>(){

            public String doInTransaction() {
                ContentEntityObject current = DefaultDiffContextProvider.this.contentEntityManager.getById(contentId.asLong());
                ContentEntityObject previous = DefaultDiffContextProvider.this.contentEntityManager.getById(originalId.asLong());
                return this.tryMakeDiff(current, previous);
            }

            private String tryMakeDiff(ContentEntityObject current, ContentEntityObject previous) {
                try {
                    return DefaultDiffContextProvider.this.differ.diff(previous, current);
                }
                catch (DiffException e) {
                    log.errorOrDebug((Throwable)e, "Error rendering diff in " + this.getClass().getName(), new Object[0]);
                    return "Error rendering diff: " + e.getMessage();
                }
            }
        });
    }
}

