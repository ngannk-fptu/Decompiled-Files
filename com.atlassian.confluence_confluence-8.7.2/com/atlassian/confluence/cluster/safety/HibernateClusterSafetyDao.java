/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 */
package com.atlassian.confluence.cluster.safety;

import com.atlassian.confluence.cluster.safety.ClusterSafetyDao;
import com.atlassian.confluence.cluster.safety.ClusterSafetyNumber;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateClusterSafetyDao
implements ClusterSafetyDao {
    private SessionFactory sessionFactory;

    @Override
    public Integer getSafetyNumber() {
        return this.getEntity().map(ClusterSafetyNumber::getSafetyNumber).orElse(null);
    }

    @Override
    public void setSafetyNumber(int safetyNumber) {
        ClusterSafetyNumber entity = this.getEntity().orElse(new ClusterSafetyNumber());
        entity.setSafetyNumber(safetyNumber);
        this.currentSession().save((Object)entity);
    }

    private Optional<ClusterSafetyNumber> getEntity() {
        return this.currentSession().createNamedQuery("confluence.csn_getClusterSafetyNumber", ClusterSafetyNumber.class).uniqueResultOptional();
    }

    private Session currentSession() {
        return this.sessionFactory.getCurrentSession();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

