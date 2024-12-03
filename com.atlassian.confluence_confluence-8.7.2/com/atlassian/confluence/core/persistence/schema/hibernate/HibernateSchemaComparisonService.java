/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  org.hibernate.Session
 *  org.hibernate.engine.spi.Mapping
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.persistence.schema.hibernate;

import com.atlassian.confluence.core.persistence.schema.api.SchemaComparison;
import com.atlassian.confluence.core.persistence.schema.api.SchemaComparisonService;
import com.atlassian.confluence.core.persistence.schema.api.SchemaInformationService;
import com.atlassian.confluence.core.persistence.schema.hibernate.HibernateSchemaComparator;
import com.atlassian.confluence.impl.core.persistence.hibernate.HibernateMetadataSource;
import com.google.common.base.Stopwatch;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.hibernate.Session;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateSchemaComparisonService
implements SchemaComparisonService {
    private static final Logger log = LoggerFactory.getLogger(HibernateSchemaComparisonService.class);
    private final SessionFactoryImplementor sessionFactory;
    private final HibernateMetadataSource metadataSource;
    private final SchemaInformationService dbSchemaInformationService;

    public HibernateSchemaComparisonService(SessionFactoryImplementor sessionFactory, HibernateMetadataSource metadataSource, SchemaInformationService dbSchemaInformationService) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
        this.metadataSource = Objects.requireNonNull(metadataSource);
        this.dbSchemaInformationService = Objects.requireNonNull(dbSchemaInformationService);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SchemaComparison compareExpectedWithActualSchema() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Session session = this.sessionFactory.openSession();
        log.debug("Opening Hibernate session for comparison took {}ms", (Object)stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset();
        stopwatch.start();
        try {
            HibernateSchemaComparator comparator = new HibernateSchemaComparator((Mapping)this.sessionFactory, this.metadataSource, this.dbSchemaInformationService);
            SchemaComparison schemaComparison = comparator.compareSchema();
            return schemaComparison;
        }
        finally {
            session.close();
            log.debug("Generating schema comparison took {}ms", (Object)stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }
}

