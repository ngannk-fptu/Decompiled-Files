/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.tasks;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.LuceneIndependent;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.HandleAware;
import com.atlassian.confluence.search.queue.JournalEntryFactory;
import com.atlassian.confluence.search.queue.JournalEntryType;
import com.atlassian.confluence.search.v2.SearchIndexWriter;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.user.GroupMembershipAccessor;
import com.atlassian.confluence.user.GroupResolver;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@LuceneIndependent
@Internal
public class ReindexUsersInGroupContentIndexTask
implements ConfluenceIndexTask,
HandleAware {
    private static final Logger log = LoggerFactory.getLogger(ReindexUsersInGroupContentIndexTask.class);
    private static final JournalEntryType journalEntryType = JournalEntryType.REINDEX_USERS_IN_GROUP;
    private final IndexTaskFactoryInternal indexTaskFactory;
    private final BatchOperationManager batchOperationManager;
    private final PersonalInformationManager personalInformationManager;
    private final GroupResolver groupResolver;
    private final GroupMembershipAccessor groupMembershipAccessor;
    private final String groupName;

    public ReindexUsersInGroupContentIndexTask(BatchOperationManager batchOperationManager, PersonalInformationManager personalInformationManager, GroupResolver groupResolver, GroupMembershipAccessor groupMembershipAccessor, IndexTaskFactoryInternal indexTaskFactory, String groupName) {
        this.indexTaskFactory = indexTaskFactory;
        this.batchOperationManager = batchOperationManager;
        this.personalInformationManager = personalInformationManager;
        this.groupResolver = groupResolver;
        this.groupMembershipAccessor = groupMembershipAccessor;
        this.groupName = groupName;
    }

    @Override
    public String getDescription() {
        return "index.task.reindex.users.in.group.content";
    }

    @Override
    public void perform(final SearchIndexWriter writer) throws IOException {
        List<String> personalInfoNamesToReindex = this.getUsersInGroup(this.groupName);
        log.info("Found {} usernames in group {} that need PersonalInformation reindexed.", (Object)personalInfoNamesToReindex.size(), (Object)this.groupName);
        try {
            this.batchOperationManager.applyInBatches(personalInfoNamesToReindex, personalInfoNamesToReindex.size(), new Function<String, Void>(){

                @Override
                public Void apply(String username) {
                    PersonalInformation personalInformation = ReindexUsersInGroupContentIndexTask.this.personalInformationManager.getOrCreatePersonalInformation((User)new DefaultUser(username));
                    try {
                        ReindexUsersInGroupContentIndexTask.this.indexTaskFactory.createUpdateDocumentTask(personalInformation).perform(writer);
                    }
                    catch (IOException e) {
                        throw new UncheckedExecutionException((Throwable)e);
                    }
                    return null;
                }

                public String toString() {
                    return "PersonalInfo reindexing for group '" + ReindexUsersInGroupContentIndexTask.this.groupName + "'";
                }
            });
        }
        catch (UncheckedExecutionException e) {
            Throwable cause = e.getCause();
            Throwables.propagateIfInstanceOf((Throwable)cause, IOException.class);
            throw Throwables.propagate((Throwable)cause);
        }
    }

    @Override
    public Optional<JournalEntry> convertToJournalEntry(JournalIdentifier journalId) {
        return JournalEntryFactory.createJournalEntry(journalId, journalEntryType, this.getHandle().toString());
    }

    private List<String> getUsersInGroup(String groupName) {
        Group group = this.groupResolver.getGroup(groupName);
        if (group == null) {
            return Collections.emptyList();
        }
        return this.groupMembershipAccessor.getMemberNamesAsList(group);
    }

    @Override
    public Handle getHandle() {
        return new StringHandle(this.groupName);
    }

    @Override
    public SearchIndex getSearchIndex() {
        return SearchIndex.CONTENT;
    }

    private static class StringHandle
    implements Handle {
        private final String string;

        private StringHandle(String string) {
            this.string = (String)Preconditions.checkNotNull((Object)string);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof StringHandle)) {
                return false;
            }
            StringHandle that = (StringHandle)o;
            return this.string.equals(that.string);
        }

        public int hashCode() {
            return this.string.hashCode();
        }

        public String toString() {
            return this.string;
        }
    }
}

