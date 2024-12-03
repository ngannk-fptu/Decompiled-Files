/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.core.LifecycleAwareSchedulerService
 *  com.google.common.base.Predicate
 *  org.hibernate.SessionFactory
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.content.render.xhtml.migration.DefaultSiteMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.LatestVersionXhtmlContentWorkSource;
import com.atlassian.confluence.content.render.xhtml.migration.PageTemplateSiteMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.SiteMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.WikiMarkupContentEntityObjectMigrationWorkSource;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.impl.cache.CacheFlusher;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.pages.templates.persistence.dao.PageTemplateDao;
import com.atlassian.scheduler.core.LifecycleAwareSchedulerService;
import java.util.function.Predicate;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;

public class SiteMigratorFactory {
    private final SessionFactory sessionFactory;
    private final PlatformTransactionManager transactionManager;
    private final ContentDao contentDao;
    private final CacheFlusher cacheFlusher;
    private final PageTemplateDao pageTemplateDao;
    private final PageTemplateManager pageTemplateManager;
    private final LifecycleAwareSchedulerService lifecycleAwareSchedulerService;

    public SiteMigratorFactory(SessionFactory sessionFactory, PlatformTransactionManager transactionManager, ContentDao contentDao, CacheFlusher cacheFlusher, PageTemplateDao pageTemplateDao, PageTemplateManager pageTemplateManager, LifecycleAwareSchedulerService lifecycleAwareSchedulerService) {
        this.sessionFactory = sessionFactory;
        this.transactionManager = transactionManager;
        this.contentDao = contentDao;
        this.cacheFlusher = cacheFlusher;
        this.pageTemplateDao = pageTemplateDao;
        this.pageTemplateManager = pageTemplateManager;
        this.lifecycleAwareSchedulerService = lifecycleAwareSchedulerService;
    }

    public DefaultSiteMigrator createWikiToXhtmlSiteMigrator(ExceptionTolerantMigrator migrator) {
        return new DefaultSiteMigrator(DefaultSiteMigrator.getNumberOfThreads(), this.sessionFactory, this.transactionManager, this.contentDao, migrator, this.cacheFlusher, new WikiMarkupContentEntityObjectMigrationWorkSource(this.contentDao, DefaultSiteMigrator.getBatchSize()), "confluence.wiki.migration.versioncomment", "Migrated to Confluence 4.0", this.lifecycleAwareSchedulerService);
    }

    public SiteMigrator createWikiToXhtmlPageTemplateSiteMigrator(ExceptionTolerantMigrator migrator) {
        return this.createPageTemplateSiteMigrator(migrator, SiteMigratorFactory.onlyPageTemplatesWithBodyType(BodyType.WIKI));
    }

    public SiteMigrator createXhtmlRoundTripPageTemplateSiteMigrator(ExceptionTolerantMigrator migrator) {
        return this.createPageTemplateSiteMigrator(migrator, SiteMigratorFactory.onlyPageTemplatesWithBodyType(BodyType.XHTML));
    }

    private static com.google.common.base.Predicate<PageTemplate> onlyPageTemplatesWithBodyType(BodyType bodyType) {
        return template -> template.getBodyType() == bodyType;
    }

    private SiteMigrator createPageTemplateSiteMigrator(ExceptionTolerantMigrator migrator, com.google.common.base.Predicate<PageTemplate> pageTemplateMigrationSelector) {
        return new PageTemplateSiteMigrator(4, this.transactionManager, migrator, this.pageTemplateDao, this.pageTemplateManager, this.lifecycleAwareSchedulerService, (Predicate<PageTemplate>)pageTemplateMigrationSelector);
    }

    public SiteMigrator createXhtmlRoundTripSiteMigrator(ExceptionTolerantMigrator migrator) {
        return new DefaultSiteMigrator(DefaultSiteMigrator.getNumberOfThreads(), this.sessionFactory, this.transactionManager, this.contentDao, migrator, this.cacheFlusher, new LatestVersionXhtmlContentWorkSource(this.contentDao, DefaultSiteMigrator.getBatchSize()), "confluence.xhtml.migration.versioncomment", "Migrated to Confluence 5.3", this.lifecycleAwareSchedulerService);
    }
}

