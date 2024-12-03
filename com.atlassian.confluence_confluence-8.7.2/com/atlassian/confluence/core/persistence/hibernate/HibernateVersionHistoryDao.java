/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  javax.persistence.PersistenceException
 *  org.hibernate.LockMode
 *  org.hibernate.Session
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.query.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.confluence.core.VersionHistory;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.internal.persistence.VersionHistoryDaoInternal;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateVersionHistoryDao
extends HibernateObjectDao<VersionHistory>
implements VersionHistoryDaoInternal {
    private static final Logger log = LoggerFactory.getLogger(HibernateVersionHistoryDao.class);

    @Override
    public Class<VersionHistory> getPersistentClass() {
        return VersionHistory.class;
    }

    @Override
    public int getLatestBuildNumber() {
        return this.getLatestBuildNumber(null);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private int getLatestBuildNumber(String whereClause) {
        Session session = this.getSessionFactory().getCurrentSession();
        try (Statement st = ((SessionImplementor)session).connection().createStatement();){
            int n;
            block19: {
                ResultSet rs;
                block17: {
                    int n2;
                    block18: {
                        StringBuilder sqlBuilder = new StringBuilder("select max(BUILDNUMBER) from CONFVERSION");
                        if (!Strings.isNullOrEmpty((String)whereClause)) {
                            sqlBuilder.append(" where ").append(whereClause);
                        }
                        rs = st.executeQuery(sqlBuilder.toString());
                        try {
                            if (!rs.next()) break block17;
                            n2 = rs.getInt(1);
                            if (rs == null) break block18;
                        }
                        catch (Throwable throwable) {
                            if (rs != null) {
                                try {
                                    rs.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        rs.close();
                    }
                    return n2;
                }
                n = 0;
                if (rs == null) break block19;
                rs.close();
            }
            return n;
        }
        catch (Exception e) {
            log.warn("Unable to determine build number from database. If you are upgrading from a Confluence version prior to 7.14.0, this is expected: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public VersionHistory getVersionHistory(int buildNumber) {
        List results = this.getHibernateTemplate().findByNamedQueryAndNamedParam("confluence.confversion_findByBuildNumber", "buildNumber", (Object)buildNumber);
        if (results.size() > 0) {
            return (VersionHistory)results.iterator().next();
        }
        return null;
    }

    @Override
    public boolean tagBuild(int buildNumber, String tag) {
        boolean buildTagged;
        Session session = this.getSessionFactory().getCurrentSession();
        VersionHistory buildNumberRow = this.getVersionHistory(buildNumber);
        if (buildNumberRow == null) {
            try {
                log.debug("No existing record found for build number {}; creating one", (Object)buildNumber);
                this.addBuildToHistory(buildNumber);
                buildNumberRow = this.getVersionHistory(buildNumber);
            }
            catch (Throwable e) {
                throw new RuntimeException("Attempted to add a duplicate row. This may be caused by two nodes trying to create this row at the same time. This is a symptom of a communication problem between one or more nodes in your cluster.", e);
            }
        }
        try {
            session.lock((Object)buildNumberRow, LockMode.UPGRADE);
            if (tag.equals(buildNumberRow.getVersionTag())) {
                log.debug("Build number {} is already tagged with '{}'", (Object)buildNumber, (Object)tag);
                buildTagged = false;
            } else {
                buildNumberRow.setVersionTag(tag);
                this.getHibernateTemplate().update((Object)buildNumberRow);
                buildTagged = true;
            }
            session.lock((Object)buildNumberRow, LockMode.NONE);
        }
        catch (PersistenceException e) {
            throw new RuntimeException("Error acquiring lock on CONFVERSION table.", e);
        }
        return buildTagged;
    }

    @Override
    public int getFinalizedBuildNumber() {
        return this.getLatestBuildNumber("FINALIZED = 'Y'");
    }

    @Override
    public void finalizeBuild(int buildNumber) {
        VersionHistory versionHistory = this.getVersionHistory(buildNumber);
        if (versionHistory == null) {
            versionHistory = new VersionHistory(buildNumber, new Date());
        }
        versionHistory.setFinalized(true);
        this.getHibernateTemplate().saveOrUpdate((Object)versionHistory);
    }

    @Override
    public List<VersionHistory> getFullUpgradeHistory() {
        return (List)this.getHibernateTemplate().executeWithNativeSession(session -> {
            Query queryObject = session.createNamedQuery("confluence.confversion_findUpgradeHistory", VersionHistory.class);
            HibernateVersionHistoryDao.applyTransactionTimeout(queryObject, this.getSessionFactory());
            return ImmutableList.copyOf((Collection)queryObject.list());
        });
    }

    @Override
    public void addBuildToHistory(int buildNumber) {
        VersionHistory versionHistory = this.getVersionHistory(buildNumber);
        if (versionHistory == null) {
            this.getHibernateTemplate().save((Object)new VersionHistory(buildNumber, new Date()));
        } else {
            versionHistory.setInstallationDate(new Date());
            this.getHibernateTemplate().update((Object)versionHistory);
        }
    }
}

