/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.core.util.Clock
 *  com.atlassian.fugue.Option
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.jdbc.core.namedparam.MapSqlParameterSource
 *  org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
 *  org.springframework.jdbc.core.namedparam.SqlParameterSource
 *  org.springframework.jdbc.datasource.SingleConnectionDataSource
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.journal;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.impl.journal.JournalDao;
import com.atlassian.confluence.impl.journal.JournalEntry;
import com.atlassian.confluence.util.DefaultClock;
import com.atlassian.core.util.Clock;
import com.atlassian.fugue.Option;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class HibernateJournalDao
implements JournalDao {
    private static final Logger log = LoggerFactory.getLogger(HibernateJournalDao.class);
    private final HibernateTemplate hibernateTemplate;
    private final Clock clock;

    public HibernateJournalDao(SessionFactory sessionFactory) {
        this(sessionFactory, new DefaultClock());
    }

    public HibernateJournalDao(SessionFactory sessionFactory, Clock clock) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
        this.clock = (Clock)Preconditions.checkNotNull((Object)clock);
    }

    @Override
    public long enqueue(@NonNull JournalEntry entry) {
        entry.setCreationDate(this.clock.getCurrentDate());
        return (Long)this.hibernateTemplate.save((Object)entry);
    }

    @Override
    public void enqueue(@NonNull Collection<JournalEntry> entries) {
        log.trace("Enqueuing of {} entries started", (Object)entries.size());
        entries.forEach(this::enqueue);
        log.trace("Enqueuing of {} entries has been finished", (Object)entries.size());
    }

    @Internal
    public void queueWithCustomCreationDate(JournalEntry entry, Date creationDate) {
        entry.setCreationDate(creationDate);
        this.hibernateTemplate.save((Object)entry);
    }

    @Override
    public List<JournalEntry> findEntries(@NonNull JournalIdentifier journalId, long afterId, long ignoreWithinMillis, int maxEntries) {
        Preconditions.checkArgument((maxEntries > 0 ? 1 : 0) != 0, (Object)"maxEntries must be a bigger than 0");
        this.hibernateTemplate.setMaxResults(maxEntries);
        List result = this.hibernateTemplate.findByNamedQueryAndNamedParam("confluence.journal_findEntriesLaterThan", new String[]{"entryId", "journalName", "creationDate"}, new Object[]{afterId, journalId.getJournalName(), this.since(ignoreWithinMillis)});
        return result;
    }

    @Override
    @VisibleForTesting
    public Option<JournalEntry> findMostRecentEntryByMessage(@NonNull JournalIdentifier journalId, String message) {
        Preconditions.checkNotNull((Object)message, (Object)"message must be a non-null");
        List entries = this.hibernateTemplate.findByNamedQueryAndNamedParam("confluence.journal_findEntriesByMessage", new String[]{"journalName", "message"}, new Object[]{journalId.getJournalName(), message});
        return !entries.isEmpty() ? Option.some((Object)((JournalEntry)entries.get(0))) : Option.none();
    }

    @Override
    public int removeEntriesOlderThan(@NonNull Date date) {
        List latestEntryIds = this.hibernateTemplate.findByNamedQuery("confluence.journal_findLatestEntryForJournals", new Object[0]);
        if (latestEntryIds.isEmpty()) {
            return 0;
        }
        return Objects.requireNonNull((Integer)this.hibernateTemplate.executeWithNativeSession(session -> {
            session.flush();
            NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate((DataSource)new SingleConnectionDataSource(((SessionImplementor)session).connection(), true));
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("date", (Object)date);
            parameters.addValue("ids", (Object)latestEntryIds);
            return template.update("DELETE FROM journalentry WHERE creationdate < :date and entry_id not in (:ids)", (SqlParameterSource)parameters);
        }));
    }

    @Override
    public Option<JournalEntry> findLatestEntry(@NonNull JournalIdentifier journalId, long ignoreWithinMillis) {
        this.hibernateTemplate.setMaxResults(1);
        List entries = this.hibernateTemplate.findByNamedQueryAndNamedParam("confluence.journal_findLatestEntries", new String[]{"journalName", "creationDate"}, new Object[]{journalId.getJournalName(), this.since(ignoreWithinMillis)});
        return !entries.isEmpty() ? Option.some((Object)((JournalEntry)entries.get(0))) : Option.none();
    }

    @Override
    public Option<JournalEntry> findEarliestEntry() {
        List entries = this.hibernateTemplate.findByNamedQuery("confluence.journal_findEarliestEntry", new Object[0]);
        return !entries.isEmpty() ? Option.some((Object)((JournalEntry)entries.get(0))) : Option.none();
    }

    @Override
    public JournalEntry findEntry(long entryId) {
        return (JournalEntry)this.hibernateTemplate.get(JournalEntry.class, (Serializable)Long.valueOf(entryId));
    }

    @Override
    public int countEntries(@NonNull JournalIdentifier journalId, long afterId, long ignoreWithinMillis) {
        List counts = Objects.requireNonNull(this.hibernateTemplate.findByNamedQueryAndNamedParam("confluence.journal_countEntriesLaterThan", new String[]{"entryId", "journalName", "creationDate"}, new Object[]{afterId, journalId.getJournalName(), this.since(ignoreWithinMillis)}));
        return (Integer)counts.get(0);
    }

    @Override
    public void updateEntry(JournalEntry journalEntry) {
        this.hibernateTemplate.update((Object)journalEntry);
    }

    private Date since(long ignoreWithinMillis) {
        return new Date(this.clock.getCurrentDate().getTime() - ignoreWithinMillis);
    }
}

