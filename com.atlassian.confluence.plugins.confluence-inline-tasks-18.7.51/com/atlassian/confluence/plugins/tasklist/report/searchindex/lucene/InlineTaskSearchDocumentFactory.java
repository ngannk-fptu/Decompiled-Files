/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.plugins.index.api.LongFieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.StringFieldDescriptor
 *  com.atlassian.confluence.search.v2.AtlassianDocument
 *  com.atlassian.confluence.search.v2.ContentPermissionCalculator
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.lucene.LuceneUtils
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.report.searchindex.lucene;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.LongFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.plugins.tasklist.ao.AOInlineTask;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.ContentPermissionCalculator;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class InlineTaskSearchDocumentFactory {
    final ContentPermissionCalculator contentPermissionCalculator;
    final UserAccessor userAccessor;

    public InlineTaskSearchDocumentFactory(ContentPermissionCalculator contentPermissionCalculator, UserAccessor userAccessor) {
        this.contentPermissionCalculator = contentPermissionCalculator;
        this.userAccessor = userAccessor;
    }

    public AtlassianDocument buildDocument(ContentEntityObject content, AOInlineTask task) {
        ConfluenceUser assigneeUser;
        Preconditions.checkNotNull((Object)content, (Object)"Content entity object can't be null");
        Preconditions.checkNotNull((Object)task, (Object)"Task can't be null");
        AtlassianDocument document = new AtlassianDocument();
        document.addField((FieldDescriptor)new StringFieldDescriptor("globalId", Long.toString(task.getGlobalId()), FieldDescriptor.Store.YES));
        document.addField((FieldDescriptor)new StringFieldDescriptor("taskId", Long.toString(task.getId()), FieldDescriptor.Store.YES));
        if (StringUtils.isNotEmpty((CharSequence)content.getTitle())) {
            document.addField((FieldDescriptor)new StringFieldDescriptor("pageTitle", content.getTitle(), FieldDescriptor.Store.YES));
            document.addField((FieldDescriptor)new StringFieldDescriptor("pageTitleCI", content.getTitle().toLowerCase(), FieldDescriptor.Store.NO));
        }
        document.addField((FieldDescriptor)new StringFieldDescriptor("taskStatus", task.getTaskStatus().name(), FieldDescriptor.Store.YES));
        if (StringUtils.isNotEmpty((CharSequence)task.getBody())) {
            document.addField((FieldDescriptor)new StringFieldDescriptor("taskBody", task.getBody(), FieldDescriptor.Store.YES));
        }
        ConfluenceUser confluenceUser = assigneeUser = StringUtils.isNotEmpty((CharSequence)task.getAssigneeUserKey()) ? this.userAccessor.getUserByKey(new UserKey(task.getAssigneeUserKey())) : null;
        if (assigneeUser != null) {
            document.addField((FieldDescriptor)new StringFieldDescriptor("assigneeKey", task.getAssigneeUserKey(), FieldDescriptor.Store.YES));
            document.addField((FieldDescriptor)new StringFieldDescriptor("assigneeName", assigneeUser.getFullName(), FieldDescriptor.Store.YES));
            document.addField((FieldDescriptor)new StringFieldDescriptor("assignee", assigneeUser.getName(), FieldDescriptor.Store.YES));
        }
        document.addField((FieldDescriptor)new StringFieldDescriptor("assigneeNameEmptyValuesLast", this.getAssigneeNameEmptyStringsLast(assigneeUser != null ? assigneeUser.getFullName() : null), FieldDescriptor.Store.NO));
        if (StringUtils.isNotEmpty((CharSequence)task.getCreatorUserKey())) {
            document.addField((FieldDescriptor)new StringFieldDescriptor("creatorKey", task.getCreatorUserKey(), FieldDescriptor.Store.YES));
        }
        document.addField((FieldDescriptor)new StringFieldDescriptor("contentId", String.valueOf(content.getId()), FieldDescriptor.Store.YES));
        for (Label label : content.getLabels()) {
            document.addField((FieldDescriptor)new StringFieldDescriptor("label", label.getName(), FieldDescriptor.Store.YES));
        }
        this.addDateField(document, task.getCreateDate(), "createDate", "createDateMs", null);
        this.addDateField(document, task.getDueDate(), "dueDate", "dueDateMs", "dueDateEmptyValueLastMs");
        this.addAncestors(document, content);
        this.addSpaceKeyAndId(document, content);
        Collection permissions = this.contentPermissionCalculator.calculate(content);
        if (!permissions.isEmpty()) {
            document.addField((FieldDescriptor)new StringFieldDescriptor(SearchFieldNames.CONTENT_PERMISSION_SETS, this.contentPermissionCalculator.getEncodedContentPermissionSets(permissions), FieldDescriptor.Store.NO));
        }
        return document;
    }

    private String getAssigneeNameEmptyStringsLast(String userName) {
        return StringUtils.isEmpty((CharSequence)userName) ? "1" : "0." + userName;
    }

    private void addDateField(AtlassianDocument document, Date date, String humanReadableFieldName, String fieldNameForDateInMilliseconds, String fieldNameForDateEmptyValuesLastInMilliseconds) {
        if (fieldNameForDateEmptyValuesLastInMilliseconds != null) {
            document.addField((FieldDescriptor)new LongFieldDescriptor(fieldNameForDateEmptyValuesLastInMilliseconds, date != null ? date.getTime() : Long.MAX_VALUE, FieldDescriptor.Store.NO));
        }
        if (date == null) {
            return;
        }
        document.addField((FieldDescriptor)new StringFieldDescriptor(humanReadableFieldName, LuceneUtils.dateToString((Date)date), FieldDescriptor.Store.YES));
        document.addField((FieldDescriptor)new LongFieldDescriptor(fieldNameForDateInMilliseconds, date.getTime(), FieldDescriptor.Store.NO));
    }

    private void addAncestors(AtlassianDocument document, ContentEntityObject content) {
        if (content instanceof Page) {
            Collection<Long> ancestors = this.getPageAncestors((Page)content);
            ancestors.forEach(ancestorId -> document.addField((FieldDescriptor)new StringFieldDescriptor("ancestorIds", String.valueOf(ancestorId), FieldDescriptor.Store.NO)));
        } else {
            document.addField((FieldDescriptor)new StringFieldDescriptor("ancestorIds", String.valueOf(content.getId()), FieldDescriptor.Store.NO));
        }
    }

    private Collection<Long> getPageAncestors(Page content) {
        ArrayList<Long> ancestors = new ArrayList<Long>();
        while (content != null) {
            ancestors.add(content.getId());
            content = content.getParent();
        }
        return ancestors;
    }

    private void addSpaceKeyAndId(AtlassianDocument document, ContentEntityObject content) {
        Space space;
        if (content instanceof Spaced && (space = ((Spaced)content.getLatestVersion()).getSpace()) != null) {
            document.addField((FieldDescriptor)new StringFieldDescriptor("spaceId", Long.toString(space.getId()), FieldDescriptor.Store.YES));
            if (StringUtils.isNotBlank((CharSequence)space.getKey())) {
                document.addField((FieldDescriptor)new StringFieldDescriptor(SearchFieldNames.SPACE_KEY, space.getKey(), FieldDescriptor.Store.YES));
                return;
            }
        }
        document.addField((FieldDescriptor)new StringFieldDescriptor(SearchFieldNames.IN_SPACE, "false", FieldDescriptor.Store.NO));
    }
}

