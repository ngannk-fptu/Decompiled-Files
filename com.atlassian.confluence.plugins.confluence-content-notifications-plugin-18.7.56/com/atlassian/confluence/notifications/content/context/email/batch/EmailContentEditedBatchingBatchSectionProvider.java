/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.batch.service.AbstractBatchSectionProvider
 *  com.atlassian.confluence.notifications.batch.service.BatchSectionProvider$BatchOutput
 *  com.atlassian.confluence.notifications.batch.service.BatchTarget
 *  com.atlassian.confluence.notifications.batch.service.BatchingRoleRecipient
 *  com.atlassian.confluence.notifications.batch.template.BatchSection
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateActions
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateCommentPattern$Builder
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateElement
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateGroup
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateGroup$Builder
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateHtml
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateLink
 *  com.atlassian.confluence.notifications.batch.template.BatchTemplateMessage$Builder
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.content.context.email.batch;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.batch.service.AbstractBatchSectionProvider;
import com.atlassian.confluence.notifications.batch.service.BatchSectionProvider;
import com.atlassian.confluence.notifications.batch.service.BatchTarget;
import com.atlassian.confluence.notifications.batch.service.BatchingRoleRecipient;
import com.atlassian.confluence.notifications.batch.template.BatchSection;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateActions;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateCommentPattern;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateGroup;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateHtml;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateLink;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateMessage;
import com.atlassian.confluence.notifications.content.ContentEditedPayload;
import com.atlassian.confluence.notifications.content.DiffContextProvider;
import com.atlassian.confluence.notifications.content.batching.ContentBatchContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;

@ExperimentalSpi
public class EmailContentEditedBatchingBatchSectionProvider
extends AbstractBatchSectionProvider<ContentBatchContext> {
    private static final String CONTENT_EDITED_SECTION_HEADER = "notifications.batch.content.edited.section.header";
    private static final String CONTENT_EDITED_SECTION_NAME = "notifications.batch.content.edited.section.name";
    private static final String CONTENT_EDITED_USERS_MSG = "notifications.batch.content.edited.users.msg";
    private static final String CONTENT_EDITED_VERSION_COMMENT_MSG = "notifications.batch.content.edited.version.comment.msg";
    private static final String CONTENT_EDITED_DIFF_MSG = "notifications.batch.content.edited.diff.msg";
    private static final String CONTENT_WEBITEMS_SECTION_ID = "email.batch.content.history.links";
    private final DiffContextProvider diffContextProvider;
    private final ContentEntityManager contentEntityManager;
    private final FormatSettingsManager formatSettingsManager;
    private final UserAccessor userAccessor;
    private final LocaleManager localeManager;
    private final I18nResolver i18nResolver;

    public EmailContentEditedBatchingBatchSectionProvider(DiffContextProvider diffContextProvider, @Qualifier(value="pageManager") ContentEntityManager contentEntityManager, FormatSettingsManager formatSettingsManager, UserAccessor userAccessor, LocaleManager localeManager, I18nResolver i18nResolver, UserNotificationPreferencesManager preferencesManager) {
        super(preferencesManager);
        this.diffContextProvider = diffContextProvider;
        this.contentEntityManager = contentEntityManager;
        this.formatSettingsManager = formatSettingsManager;
        this.userAccessor = userAccessor;
        this.localeManager = localeManager;
        this.i18nResolver = i18nResolver;
    }

    public BatchSectionProvider.BatchOutput processBatch(BatchingRoleRecipient recipient, List<ContentBatchContext> context, Set<UserKey> contributors) {
        int minVersion = Integer.MAX_VALUE;
        int maxVersion = 0;
        ContentEntityObject ceoMin = null;
        ContentEntityObject ceoMax = null;
        TreeSet<ContentEntityObject> ceos = new TreeSet<ContentEntityObject>((o1, o2) -> o2.getVersion() - o1.getVersion());
        for (ContentBatchContext notification : context) {
            long contentId = notification.getContentID();
            ContentEntityObject ceo = this.contentEntityManager.getById(contentId);
            if (ceo == null || ((ContentEntityObject)ceo.getLatestVersion()).isDeleted()) continue;
            int version = ceo.getVersion();
            if (version < minVersion) {
                minVersion = version;
                ceoMin = ceo;
            }
            if (version > maxVersion) {
                maxVersion = version;
                ceoMax = ceo;
            }
            ceos.add(ceo);
        }
        if (ceoMin == null || ceoMax == null) {
            return new BatchSectionProvider.BatchOutput();
        }
        ceoMax = this.contentEntityManager.getNextVersion(ceoMax);
        ceos.add(ceoMax);
        ceos.remove(ceoMin);
        Map<String, Object> diffMap = this.diffContextProvider.generateDiffContext(ceoMax.getContentId(), ceoMin.getContentId(), (Option<UserKey>)Option.some((Object)recipient.getUserKey()));
        ConfluenceUser user = this.userAccessor.getUserByKey(recipient.getUserKey());
        ConfluenceUserPreferences preferences = this.userAccessor.getConfluenceUserPreferences((User)user);
        DateFormatter dateFormatter = preferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        ArrayList<BatchTemplateGroup> groups = new ArrayList<BatchTemplateGroup>();
        groups.add(this.editsSummary(ceos.stream().filter(x -> x.isVersionCommentAvailable()).collect(Collectors.toList()), dateFormatter, contributors, ceoMax.getType()));
        Object showDiffs = diffMap.get("showDiffs");
        if (showDiffs != null && showDiffs.equals(true)) {
            groups.add(new BatchTemplateGroup.Builder().line().element((BatchTemplateElement)new BatchTemplateHtml((String)diffMap.get("diffHtml"), true, new BatchTemplateMessage.Builder(CONTENT_EDITED_DIFF_MSG).build())).end().build());
        }
        ContentId objContentId = ((ContentEntityObject)ceoMax.getLatestVersion()).getContentId();
        BatchTemplateActions contentActions = new BatchTemplateActions(objContentId, CONTENT_WEBITEMS_SECTION_ID);
        contentActions.getContext().put("originalContent", ceoMin);
        groups.add(new BatchTemplateGroup.Builder().line().element((BatchTemplateElement)contentActions).end().build());
        int count = context.size();
        return new BatchSectionProvider.BatchOutput(new BatchSection(count, this.i18nResolver.getText(CONTENT_EDITED_SECTION_HEADER, new Serializable[]{Integer.valueOf(count)}), this.i18nResolver.getText(CONTENT_EDITED_SECTION_NAME, new Serializable[]{Integer.valueOf(count)}), groups), new BatchTarget(objContentId.serialise(), 0));
    }

    private BatchTemplateGroup editsSummary(Collection<ContentEntityObject> versions, DateFormatter dateFormatter, Set<UserKey> contributors, String contentType) {
        BatchTemplateGroup.Builder summary = new BatchTemplateGroup.Builder();
        summary.line().element((BatchTemplateElement)new BatchTemplateCommentPattern.Builder().authors(contributors).message(String.format("%s.%s", CONTENT_EDITED_USERS_MSG, contentType)).split(true).build()).end();
        if (!versions.isEmpty()) {
            summary.line().element((BatchTemplateElement)new BatchTemplateHtml("", false, new BatchTemplateMessage.Builder(this.i18nResolver.getText(CONTENT_EDITED_VERSION_COMMENT_MSG, new Serializable[]{Integer.valueOf(versions.size())})).build())).end();
            versions.forEach(version -> {
                ConfluenceUser creator = version.getLastModifier();
                String versionDate = dateFormatter.formatTime(version.getLastModificationDate());
                summary.line().element((BatchTemplateElement)new BatchTemplateCommentPattern.Builder().author(creator != null ? creator.getKey() : null).message(this.i18nResolver.getText(CONTENT_EDITED_USERS_MSG)).messsageElementArg("date", (BatchTemplateElement)new BatchTemplateLink(versionDate, "/pages/viewpage.action?pageId=" + version.getContentId().serialise(), true)).split(true).commentBody(new BatchTemplateHtml(version.getRenderedVersionComment(), false)).build()).end();
            });
        }
        return summary.build();
    }

    public Class getPayloadType() {
        return ContentEditedPayload.class;
    }
}

