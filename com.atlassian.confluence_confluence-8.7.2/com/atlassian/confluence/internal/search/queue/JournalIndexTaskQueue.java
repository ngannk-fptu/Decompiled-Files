/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.api.model.journal.JournalEntry
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.journal.JournalService
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.Hibernate
 *  org.hibernate.HibernateException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.queue;

import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.api.model.journal.JournalEntry;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.journal.JournalService;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.internal.search.IndexTaskFactoryInternal;
import com.atlassian.confluence.internal.search.queue.AbstractJournalIndexTaskQueue;
import com.atlassian.confluence.search.ConfluenceIndexTask;
import com.atlassian.confluence.search.IndexFlushRequester;
import com.atlassian.confluence.spaces.Spaced;
import com.google.common.base.Preconditions;
import java.text.ParseException;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JournalIndexTaskQueue
extends AbstractJournalIndexTaskQueue<ConfluenceIndexTask> {
    private static final Logger log = LoggerFactory.getLogger(JournalIndexTaskQueue.class);
    protected final IndexTaskFactoryInternal indexTaskFactory;

    public JournalIndexTaskQueue(JournalService journalService, IndexTaskFactoryInternal indexTaskFactory, AnyTypeDao anyTypeDao, IndexFlushRequester indexFlushRequester, JournalIdentifier journalIdentifier) {
        super(journalService, anyTypeDao, indexFlushRequester, journalIdentifier);
        this.indexTaskFactory = (IndexTaskFactoryInternal)Preconditions.checkNotNull((Object)indexTaskFactory);
    }

    @Override
    protected Optional<JournalEntry> toEntry(ConfluenceIndexTask task) {
        return task.convertToJournalEntry(this.journalIdentifier);
    }

    protected @Nullable Searchable getSearchableFromEntry(JournalEntry entry) {
        HibernateHandle handle;
        if (entry.getMessage() == null) {
            throw new IllegalArgumentException("Index queue entry found with null handle: " + entry);
        }
        try {
            handle = new HibernateHandle(entry.getMessage());
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Index queue entry found with invalid handle: " + entry.getMessage());
        }
        Searchable searchable = (Searchable)this.anyTypeDao.findByHandle((Handle)handle);
        if (searchable instanceof Spaced) {
            try {
                Hibernate.initialize((Object)((Spaced)searchable).getSpace());
            }
            catch (HibernateException e) {
                log.debug("Could not find space for handle: " + handle, (Throwable)e);
                return null;
            }
        }
        if (searchable == null) {
            log.debug("Could not find searchable for handle: {}", (Object)handle);
        }
        return searchable;
    }
}

