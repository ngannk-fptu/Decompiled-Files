/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.impl.hibernate.HibernateSessionManager5
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.SessionFactory
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.impl.hibernate.HibernateSessionManager5;
import com.atlassian.confluence.internal.pages.persistence.PageDaoInternal;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.internal.search.tasks.AddChangeDocumentIndexTask;
import com.atlassian.confluence.internal.search.tasks.AddDocumentIndexTask;
import com.atlassian.confluence.internal.search.tasks.ContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.DeleteChangeDocumentsIndexTask;
import com.atlassian.confluence.internal.search.tasks.DeleteDocumentIndexTask;
import com.atlassian.confluence.internal.search.tasks.NoOpIndexTask;
import com.atlassian.confluence.internal.search.tasks.RebuildChangeDocumentsIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllBlogsChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllBlogsContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllSpacesChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllSpacesContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllUsersChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexAllUsersContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexUsersInGroupChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.ReindexUsersInGroupContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.UnIndexSpaceChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.UnIndexSpaceContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.UnindexContentTypeChangeIndexTask;
import com.atlassian.confluence.internal.search.tasks.UnindexContentTypeContentIndexTask;
import com.atlassian.confluence.internal.search.tasks.UpdateDocumentIndexTask;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.AtlassianDocumentBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.persistence.dao.SpaceDao;
import com.atlassian.confluence.user.GroupMembershipAccessor;
import com.atlassian.confluence.user.GroupResolver;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.persistence.dao.PersonalInformationDao;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;

@LuceneIndependent
@Internal
public class DefaultIndexTaskFactory
implements IndexTaskFactoryInternal {
    private final AtlassianDocumentBuilder<Searchable> documentBuilder;
    private final AtlassianDocumentBuilder<Searchable> changeDocumentBuilder;
    private final ContentEntityObjectDao<?> contentEntityObjectDao;
    private final PersonalInformationDao personalInformationDao;
    private final SpaceDao spaceDao;
    private final BatchOperationManager batchOperationManager;
    private final PersonalInformationManager personalInformationManager;
    private final GroupResolver groupResolver;
    private final GroupMembershipAccessor groupMembershipAccessor;
    private final PageDaoInternal pageDao;
    private final HibernateSessionManager5 hibernateSessionManager;
    private final SessionFactory sessionFactory;

    public DefaultIndexTaskFactory(BatchOperationManager batchOperationManager, AtlassianDocumentBuilder<Searchable> documentBuilder, AtlassianDocumentBuilder<Searchable> changeDocumentBuilder, ContentEntityObjectDao<?> contentEntityObjectDao, PersonalInformationManager personalInformationManager, UserAccessor userAccessor, PersonalInformationDao personalInformationDao, SpaceDao spaceDao, PageDaoInternal pageDao, HibernateSessionManager5 hibernateSessionManager, SessionFactory sessionFactory) {
        this.batchOperationManager = batchOperationManager;
        this.documentBuilder = documentBuilder;
        this.changeDocumentBuilder = changeDocumentBuilder;
        this.contentEntityObjectDao = contentEntityObjectDao;
        this.personalInformationManager = personalInformationManager;
        this.groupResolver = userAccessor;
        this.groupMembershipAccessor = userAccessor;
        this.personalInformationDao = personalInformationDao;
        this.spaceDao = spaceDao;
        this.pageDao = pageDao;
        this.hibernateSessionManager = hibernateSessionManager;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public ContentIndexTask createContentIndexTask(List<ContentType> contentTypes, List<ContentStatus> contentStatuses, JournalEntryType journalEntryType) {
        return new ContentIndexTask(contentTypes, contentStatuses, journalEntryType, this.pageDao, this.batchOperationManager, this);
    }

    @Override
    public ContentIndexTask createIndexDraftsTask() {
        return new ContentIndexTask(Arrays.asList(ContentType.PAGE, ContentType.BLOG_POST), Collections.singletonList(ContentStatus.DRAFT), JournalEntryType.INDEX_DRAFTS, this.pageDao, this.batchOperationManager, this);
    }

    @Override
    public UnIndexSpaceContentIndexTask createUnIndexSpaceContentIndexTask(String handle) {
        return new UnIndexSpaceContentIndexTask(handle);
    }

    @Override
    public UnIndexSpaceContentIndexTask createUnIndexSpaceContentIndexTask(Space space) {
        return new UnIndexSpaceContentIndexTask(space.getKey());
    }

    @Override
    public UnIndexSpaceChangeIndexTask createUnIndexSpaceChangeIndexTask(String handle) {
        return new UnIndexSpaceChangeIndexTask(handle);
    }

    @Override
    public UnIndexSpaceChangeIndexTask createUnIndexSpaceChangeIndexTask(Space space) {
        return new UnIndexSpaceChangeIndexTask(space.getKey());
    }

    @Override
    public UnindexContentTypeContentIndexTask createUnindexContentTypeContentTask(String contentType) {
        return new UnindexContentTypeContentIndexTask(contentType);
    }

    @Override
    public UnindexContentTypeChangeIndexTask createUnindexContentTypeChangeTask(String contentType) {
        return new UnindexContentTypeChangeIndexTask(contentType);
    }

    @Override
    public ReindexAllUsersContentIndexTask createReindexAllUsersContentTask() {
        return new ReindexAllUsersContentIndexTask(this.batchOperationManager, this.personalInformationDao, this);
    }

    @Override
    public ReindexAllUsersChangeIndexTask createReindexAllUsersChangeTask() {
        return new ReindexAllUsersChangeIndexTask(this.batchOperationManager, this.personalInformationDao, this);
    }

    @Override
    public ReindexAllBlogsContentIndexTask createReindexAllBlogsContentTask() {
        return new ReindexAllBlogsContentIndexTask(this, this.batchOperationManager);
    }

    @Override
    public ReindexAllBlogsChangeIndexTask createReindexAllBlogsChangeTask() {
        return new ReindexAllBlogsChangeIndexTask(this, this.batchOperationManager);
    }

    @Override
    public ReindexUsersInGroupContentIndexTask createReindexUsersInGroupContentTask(String groupName) {
        return new ReindexUsersInGroupContentIndexTask(this.batchOperationManager, this.personalInformationManager, this.groupResolver, this.groupMembershipAccessor, this, groupName);
    }

    @Override
    public ReindexUsersInGroupChangeIndexTask createReindexUsersInGroupChangeTask(String groupName) {
        return new ReindexUsersInGroupChangeIndexTask(this.batchOperationManager, this.personalInformationManager, this.groupResolver, this.groupMembershipAccessor, this, groupName);
    }

    @Override
    public ConfluenceIndexTask createDeleteDocumentTask(Searchable searchable) {
        return this.createDeleteDocumentTask(new HibernateHandle(searchable).toString());
    }

    @Override
    public ConfluenceIndexTask createDeleteDocumentTask(String handle) {
        return new DeleteDocumentIndexTask(handle);
    }

    @Override
    public ConfluenceIndexTask createUpdateDocumentTask(Searchable searchable) {
        return this.createUpdateDocumentTask(searchable, true);
    }

    @Override
    public ConfluenceIndexTask createUpdateDocumentTask(Searchable searchable, boolean includeDependents) {
        if (searchable == null || !searchable.isIndexable()) {
            return NoOpIndexTask.getContentInstance();
        }
        return new UpdateDocumentIndexTask(searchable, this, includeDependents);
    }

    @Override
    public ConfluenceIndexTask createAddDocumentTask(Searchable searchable) {
        if (searchable == null || !searchable.isIndexable()) {
            return NoOpIndexTask.getContentInstance();
        }
        return new AddDocumentIndexTask(searchable, this.documentBuilder);
    }

    @Override
    public ConfluenceIndexTask createDeleteChangeDocumentsIndexTask(Searchable searchable) {
        if (searchable == null) {
            return NoOpIndexTask.getChangeInstance();
        }
        return new DeleteChangeDocumentsIndexTask(searchable);
    }

    @Override
    public ConfluenceIndexTask createDeleteChangeDocumentsIndexTask(String handle) {
        if (StringUtils.isBlank((CharSequence)handle)) {
            return NoOpIndexTask.getChangeInstance();
        }
        return new DeleteChangeDocumentsIndexTask(handle);
    }

    @Override
    public ConfluenceIndexTask createRebuildChangeDocumentsIndexTask(Searchable searchable) {
        if (searchable == null) {
            return NoOpIndexTask.getChangeInstance();
        }
        return new RebuildChangeDocumentsIndexTask(searchable, this.contentEntityObjectDao, this, this.hibernateSessionManager, this.sessionFactory);
    }

    @Override
    public ConfluenceIndexTask createAddChangeDocumentTask(Searchable searchable) {
        if (searchable == null) {
            return NoOpIndexTask.getChangeInstance();
        }
        return new AddChangeDocumentIndexTask(searchable, this.changeDocumentBuilder);
    }

    @Override
    public ReindexAllSpacesContentIndexTask createReindexAllSpacesContentTask() {
        return new ReindexAllSpacesContentIndexTask(this.spaceDao, this);
    }

    @Override
    public ReindexAllSpacesChangeIndexTask createReindexAllSpacesChangeTask() {
        return new ReindexAllSpacesChangeIndexTask(this.spaceDao, this);
    }
}

