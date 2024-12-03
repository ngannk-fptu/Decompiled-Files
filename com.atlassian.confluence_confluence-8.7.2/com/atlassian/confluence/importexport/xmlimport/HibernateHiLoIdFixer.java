/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.impl.hibernate.ResettableTableHiLoGenerator
 *  com.atlassian.event.api.EventPublisher
 *  javax.persistence.PersistenceException
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.persister.entity.EntityPersister
 *  org.hibernate.persister.entity.SingleTableEntityPersister
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.event.events.admin.ResetHibernateIdRangeEvent;
import com.atlassian.confluence.impl.hibernate.ResettableTableHiLoGenerator;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain.DenormalisedContentViewPermission;
import com.atlassian.confluence.security.denormalisedpermissions.impl.space.domain.DenormalisedSpacePermission;
import com.atlassian.confluence.security.persistence.dao.hibernate.legacy.HibernateKey;
import com.atlassian.confluence.util.SQLUtils;
import com.atlassian.event.api.EventPublisher;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class HibernateHiLoIdFixer {
    private static final Logger log = LoggerFactory.getLogger(HibernateHiLoIdFixer.class);
    private static final String HIBERNATE_UNIQUE_KEY_TABLE = "hibernate_unique_key";
    private static final String HIBERNATE_UNIQUE_KEY_COLUMN = "next_hi";
    private static final long MAX_VALUE_TO_AVOID_TRUNCATION = 70366596661249L;
    private static final Set<Class> ENTITIES_WITHOUT_HI_LO_ID = new HashSet<Class>(Arrays.asList(DenormalisedContentViewPermission.class, DenormalisedSpacePermission.class));
    private final EventPublisher eventPublisher;
    private final SessionFactoryImplementor sessionFactory;

    public HibernateHiLoIdFixer(EventPublisher eventPublisher, SessionFactory sessionFactory) {
        this.eventPublisher = eventPublisher;
        this.sessionFactory = (SessionFactoryImplementor)sessionFactory;
    }

    public void fixHiLoTable() throws ImportExportException {
        Statement statement = null;
        ArrayList<String> errors = new ArrayList<String>();
        try {
            Session session = this.sessionFactory.getCurrentSession();
            session.flush();
            Connection connection = ((SessionImplementor)session).connection();
            statement = connection.createStatement();
            long maximumId = 0L;
            int maxLo = 0;
            for (String entityName : this.sessionFactory.getMetamodel().getAllEntityNames()) {
                Class c = this.sessionFactory.getMetamodel().entityPersister(entityName).getMappedClass();
                if (ENTITIES_WITHOUT_HI_LO_ID.contains(c) || c == HibernateKey.class) continue;
                EntityPersister persister = this.sessionFactory.getMetamodel().entityPersister(c);
                if (persister instanceof SingleTableEntityPersister && persister.getIdentifierGenerator() instanceof ResettableTableHiLoGenerator) {
                    long value;
                    ResultSet rs;
                    SingleTableEntityPersister entityPersister;
                    String[] idColumnNames;
                    ResettableTableHiLoGenerator generator = (ResettableTableHiLoGenerator)persister.getIdentifierGenerator();
                    if (maxLo == 0) {
                        maxLo = generator.getMaxLo();
                    }
                    if (maxLo != generator.getMaxLo()) {
                        errors.add("ID generator for " + c.getName() + " is using " + generator.getMaxLo() + " for its maximum low value, previous generators used: " + maxLo);
                    }
                    if ((idColumnNames = (entityPersister = (SingleTableEntityPersister)persister).getIdentifierColumnNames()).length != 1) {
                        errors.add("Expected a single id column for " + c + " found " + idColumnNames.length);
                    }
                    if (!(rs = statement.executeQuery("select max(" + idColumnNames[0] + ") from " + entityPersister.getTableName() + " where " + idColumnNames[0] + " < 70366596661249")).next()) {
                        errors.add("No maximum id returned for " + c);
                    }
                    if ((value = rs.getLong(1)) > maximumId) {
                        maximumId = value;
                    }
                    rs.close();
                }
                if (errors.isEmpty()) continue;
                this.logErrors(errors);
                throw new ImportExportException("Unable to update identifier table due to unexpected database configuration. Details in server logs.");
            }
            int nextHi = (int)(maximumId / (long)(maxLo + 1)) + 1;
            log.info("Updating hibernate HiLo identifier table. Setting next_hi to " + nextHi);
            if (statement.executeUpdate("update hibernate_unique_key set next_hi = " + nextHi) == 0 && statement.executeUpdate("insert into hibernate_unique_key values(" + nextHi + ")") == 0) {
                throw new ImportExportException("Unable to update identifier table. Failed to insert row.");
            }
            connection.commit();
            this.eventPublisher.publish((Object)new ResetHibernateIdRangeEvent(this));
        }
        catch (SQLException | PersistenceException e) {
            try {
                throw new ImportExportException("Database exception encountered while trying to update identifier table: " + (Exception)e, e);
            }
            catch (Throwable throwable) {
                SQLUtils.closeStatementQuietly(statement);
                throw throwable;
            }
        }
        SQLUtils.closeStatementQuietly(statement);
    }

    private void logErrors(List<String> errors) {
        log.error("Unable to update Hibernate identifier table due to unexpected database configuration! " + StringUtils.join(errors, (String)"\n    "));
    }
}

