/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 */
package com.atlassian.confluence.content.render.xhtml.migration.tasks;

import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ExceptionReport;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.PageTemplateMigrationException;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.pages.templates.persistence.dao.PageTemplateDao;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.renderer.RenderContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

public final class PageTemplateMigratorTask
extends TransactionCallbackWithoutResult {
    private static final Logger log = LoggerFactory.getLogger(PageTemplateMigratorTask.class);
    private final ExceptionTolerantMigrator migrator;
    private final PageTemplateDao dao;
    private final ExceptionReport report;
    private final PageTemplateManager pageTemplateManager;
    private final Predicate<PageTemplate> migrationCandidateSelector;

    public PageTemplateMigratorTask(ExceptionTolerantMigrator migrator, PageTemplateDao dao, PageTemplateManager pageTemplateManager, ExceptionReport report, Predicate<PageTemplate> migrationCandidateSelector) {
        this.migrator = migrator;
        this.dao = dao;
        this.pageTemplateManager = pageTemplateManager;
        this.report = report;
        this.migrationCandidateSelector = migrationCandidateSelector;
    }

    protected void doInTransactionWithoutResult(TransactionStatus status) {
        List<PageTemplate> allPageTemplates = this.dao.findLatestVersions();
        Collection pageTemplatesToMigrate = allPageTemplates.stream().filter(this.migrationCandidateSelector).collect(Collectors.toSet());
        log.info("{} of {} PageTemplates are candidates for migration.", (Object)pageTemplatesToMigrate.size(), (Object)allPageTemplates.size());
        PageTemplateContext context = new PageTemplateContext();
        int migrationCount = 0;
        for (PageTemplate template : pageTemplatesToMigrate) {
            List<RuntimeException> exceptions = this.migratePageTemplate(template, context);
            if (!exceptions.isEmpty()) {
                for (RuntimeException ex : exceptions) {
                    this.report.addException(new PageTemplateMigrationException(template, (Throwable)ex));
                }
                exceptions.clear();
                continue;
            }
            ++migrationCount;
        }
        log.info("Migrated {} of {} PageTemplates.", (Object)migrationCount, (Object)allPageTemplates.size());
    }

    private List<RuntimeException> migratePageTemplate(PageTemplate template, RenderContext context) {
        ArrayList<RuntimeException> exceptions = new ArrayList<RuntimeException>();
        try {
            PageTemplate originalTemplate = (PageTemplate)template.clone();
            String originalContent = template.getContent();
            String migratedContent = this.migrator.migrate(originalContent, context, exceptions);
            template.setContent(migratedContent);
            template.setBodyType(BodyType.XHTML);
            Date originalLastModificationDate = originalTemplate.getLastModificationDate();
            if (originalLastModificationDate != null) {
                template.setLastModificationDate(new Date(originalLastModificationDate.getTime() + 1000L));
            }
            template.setLastModifier(originalTemplate.getLastModifier());
            this.pageTemplateManager.savePageTemplate(template, originalTemplate);
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e));
        }
        return exceptions;
    }
}

