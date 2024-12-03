/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.tombstone.TombstoneDao
 *  com.atlassian.crowd.model.tombstone.AbstractTombstone
 *  com.atlassian.crowd.model.tombstone.AliasTombstone
 *  com.atlassian.crowd.model.tombstone.ApplicationTombstone
 *  com.atlassian.crowd.model.tombstone.ApplicationUpdatedTombstone
 *  com.atlassian.crowd.model.tombstone.EventStreamTombstone
 *  com.atlassian.crowd.model.tombstone.GroupMembershipTombstone
 *  com.atlassian.crowd.model.tombstone.GroupTombstone
 *  com.atlassian.crowd.model.tombstone.UserMembershipTombstone
 *  com.atlassian.crowd.model.tombstone.UserTombstone
 *  javax.persistence.criteria.CriteriaBuilder
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Order
 *  javax.persistence.criteria.Root
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.impl.user.crowd.hibernate;

import com.atlassian.crowd.dao.tombstone.TombstoneDao;
import com.atlassian.crowd.model.tombstone.AbstractTombstone;
import com.atlassian.crowd.model.tombstone.AliasTombstone;
import com.atlassian.crowd.model.tombstone.ApplicationTombstone;
import com.atlassian.crowd.model.tombstone.ApplicationUpdatedTombstone;
import com.atlassian.crowd.model.tombstone.EventStreamTombstone;
import com.atlassian.crowd.model.tombstone.GroupMembershipTombstone;
import com.atlassian.crowd.model.tombstone.GroupTombstone;
import com.atlassian.crowd.model.tombstone.UserMembershipTombstone;
import com.atlassian.crowd.model.tombstone.UserTombstone;
import java.time.Clock;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

public final class HibernateTombstoneDao
implements TombstoneDao {
    private final Clock clock;
    private final HibernateTemplate hibernateTemplate;

    public HibernateTombstoneDao(Clock clock, SessionFactory sessionFactory) {
        this.clock = clock;
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public void storeUserTombstones(long directoryId, Collection<String> names) {
        long timestamp = this.clock.millis();
        this.storeTombstones(names.stream().map(name -> new UserTombstone(timestamp, name, directoryId))::iterator);
    }

    public void storeGroupTombstones(long directoryId, Collection<String> names) {
        long timestamp = this.clock.millis();
        this.storeTombstones(names.stream().map(name -> new GroupTombstone(timestamp, name, directoryId))::iterator);
    }

    public void storeUserMembershipTombstone(long directoryId, String username, String parentGroupName) {
        long timestamp = this.clock.millis();
        this.storeTombstones(Collections.singleton(new UserMembershipTombstone(timestamp, username, parentGroupName, directoryId)));
    }

    public void storeGroupMembershipTombstone(long directoryId, String childGroupName, String parentGroupName) {
        long timestamp = this.clock.millis();
        this.storeTombstones(Collections.singleton(new GroupMembershipTombstone(timestamp, childGroupName, parentGroupName, directoryId)));
    }

    public void storeEventsTombstoneForDirectory(String reason, long directoryId) {
        long timestamp = this.clock.millis();
        this.storeTombstones(Collections.singleton(EventStreamTombstone.createForDirectory((long)timestamp, (long)directoryId, (String)reason)));
    }

    public void storeEventsTombstoneForApplication(long applicationId) {
        long timestamp = this.clock.millis();
        this.storeTombstones(Collections.singleton(new ApplicationUpdatedTombstone(timestamp, applicationId)));
    }

    public void storeEventsTombstone(String reason) {
        long timestamp = this.clock.millis();
        this.storeTombstones(Collections.singleton(EventStreamTombstone.createGlobal((long)timestamp, (String)reason)));
    }

    public void storeAliasTombstone(long applicationId, String username) {
        long timestamp = this.clock.millis();
        this.storeTombstones(Collections.singleton(new AliasTombstone(timestamp, applicationId, username)));
    }

    private <T extends AbstractTombstone> void storeTombstones(Iterable<T> tombstones) {
        this.hibernateTemplate.executeWithNativeSession(session -> {
            tombstones.forEach(arg_0 -> ((Session)session).save(arg_0));
            return null;
        });
    }

    public <T extends AbstractTombstone> List<T> getTombstonesAfter(long after, Collection<Long> directoryIds, Class<T> type) {
        return (List)this.hibernateTemplate.executeWithNativeSession(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(type);
            Root root = query.from(type);
            if (directoryIds.isEmpty()) {
                query.where((Expression)builder.gt((Expression)root.get("timestamp"), (Number)after));
            } else {
                query.where((Expression)builder.and((Expression)builder.gt((Expression)root.get("timestamp"), (Number)after), (Expression)builder.or((Expression)root.get("directoryId").in(directoryIds), (Expression)root.get("directoryId").isNull())));
            }
            query.orderBy(new Order[]{builder.asc((Expression)root.get("timestamp"))});
            return session.createQuery(query).getResultList();
        });
    }

    public <T extends ApplicationTombstone> List<T> getTombstonesAfter(long after, Long applicationId, Class<T> type) {
        return (List)this.hibernateTemplate.executeWithNativeSession(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(type);
            Root root = query.from(type);
            query.where((Expression)builder.and((Expression)builder.gt((Expression)root.get("timestamp"), (Number)after), (Expression)builder.equal((Expression)root.get("applicationId"), (Object)applicationId))).orderBy(new Order[]{builder.asc((Expression)root.get("timestamp"))});
            return session.createQuery(query).getResultList();
        });
    }

    public int removeAllUpTo(long timestamp) {
        return (Integer)this.hibernateTemplate.executeWithNativeSession(session -> session.createQuery("delete from AbstractTombstone where timestamp <= :timestamp").setParameter("timestamp", (Object)timestamp).executeUpdate());
    }
}

