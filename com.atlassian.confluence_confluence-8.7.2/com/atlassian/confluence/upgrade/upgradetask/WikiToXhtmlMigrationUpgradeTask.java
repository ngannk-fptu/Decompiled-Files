/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.google.common.base.Functions
 *  com.google.common.collect.ImmutableSortedMap
 *  com.google.common.collect.Ordering
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.content.render.xhtml.migration.SiteMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ContentMigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ExceptionReport;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.PageTemplateMigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.SettingsMigrationException;
import com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class WikiToXhtmlMigrationUpgradeTask
extends AbstractDeferredRunUpgradeTask
implements DatabaseUpgradeTask {
    private final SiteMigrator migrator;

    public WikiToXhtmlMigrationUpgradeTask(SiteMigrator migrator) {
        this.migrator = migrator;
    }

    public void doDeferredUpgrade() throws Exception {
        ExceptionReport report = this.migrator.migrateSite();
        if (report.isErrored()) {
            int i;
            List<SettingsMigrationException> settingsExceptions = report.getSettingsMigrationException();
            List<PageTemplateMigrationException> templateExceptions = report.getPageTemplateExceptions();
            List<ContentMigrationException> contentExceptions = report.getContentMigrationExceptions();
            StringBuilder logBuilder = new StringBuilder("Wiki to XHTML Exception Report:\n");
            logBuilder.append("Summary:\n").append("\t").append(settingsExceptions.size()).append(" settings values failed.\n").append("\t").append(templateExceptions.size()).append(" PageTemplates failed.\n").append("\t").append(contentExceptions.size()).append(" ContentEntityObjects failed.\n");
            boolean includeStackTrace = log.isDebugEnabled();
            if (!settingsExceptions.isEmpty()) {
                logBuilder.append("Settings Exceptions:\n");
                for (i = 0; i < settingsExceptions.size(); ++i) {
                    logBuilder.append("\t").append(i + 1).append(") ").append(MigrationExceptionFormatter.format(settingsExceptions.get(i), includeStackTrace)).append("\n");
                }
            }
            if (!templateExceptions.isEmpty()) {
                logBuilder.append("PageTemplate Exceptions:\n");
                for (i = 0; i < templateExceptions.size(); ++i) {
                    logBuilder.append("\t").append(i + 1).append(") ").append(MigrationExceptionFormatter.format(templateExceptions.get(i), includeStackTrace)).append("\n");
                }
            }
            this.appendContentExceptionLogDetails(logBuilder, contentExceptions, includeStackTrace);
            log.warn(logBuilder.toString());
        }
    }

    private void appendContentExceptionLogDetails(StringBuilder logBuilder, List<ContentMigrationException> contentExceptions, boolean includeStackTrace) {
        if (!contentExceptions.isEmpty()) {
            logBuilder.append("Content Migration Exceptions:\n");
            HashMap<String, Integer> exceptionStats = new HashMap<String, Integer>();
            for (int i = 0; i < contentExceptions.size(); ++i) {
                String causeStr;
                ContentMigrationException cme = contentExceptions.get(i);
                logBuilder.append("\t").append(i + 1).append(") ").append(MigrationExceptionFormatter.format(cme, includeStackTrace)).append("\n");
                String string = causeStr = cme.getCause() != null ? cme.getCause().toString() : "";
                if (causeStr.isEmpty()) continue;
                Integer count = (Integer)exceptionStats.get(causeStr);
                if (count == null) {
                    exceptionStats.put(causeStr, 1);
                    continue;
                }
                int countI = count;
                exceptionStats.put(causeStr, ++countI);
            }
            if (!exceptionStats.isEmpty()) {
                Ordering comparator = Ordering.natural().onResultOf(Functions.forMap(exceptionStats)).compound((Comparator)Ordering.natural());
                comparator = comparator.reverse();
                ImmutableSortedMap sortedStats = ImmutableSortedMap.copyOf(exceptionStats, (Comparator)comparator);
                logBuilder.append("\nContent Migration Exception statistics:\n");
                for (Map.Entry entry : sortedStats.entrySet()) {
                    logBuilder.append(String.format("Count: %6d, %s\n", entry.getValue(), ((String)entry.getKey()).replaceAll("\n", "\\n")));
                }
            }
        }
    }

    public String getBuildNumber() {
        return "3003";
    }

    public String getShortDescription() {
        return "Migrate all wiki formatted content in this Confluence instance to XHTML";
    }

    public boolean runOnSpaceImport() {
        return true;
    }

    public boolean breaksBackwardCompatibility() {
        return true;
    }

    private static class MigrationExceptionFormatter {
        private MigrationExceptionFormatter() {
        }

        private static String format(SettingsMigrationException ex, boolean stackTrace) {
            StringBuilder builder = new StringBuilder("Setting: ");
            builder.append(ex.getSettingName()).append(". Cause: ").append(ex.getCause()).append(". Message: ").append(ex.getCause().getMessage());
            if (stackTrace) {
                builder.append('\n').append(MigrationExceptionFormatter.getStackTrace(ex.getCause()));
            }
            return builder.toString();
        }

        private static String format(PageTemplateMigrationException ex, boolean stackTrace) {
            StringBuilder builder = new StringBuilder(ex.getName());
            builder.append(" (Version ").append(ex.getVersion()).append(", Id ").append(ex.getId()).append("), ");
            if (StringUtils.isNotBlank((CharSequence)ex.getSpaceName())) {
                builder.append("Space: ").append(ex.getSpaceKey()).append(" - ").append(ex.getSpaceName());
            } else {
                builder.append("Global Scope");
            }
            builder.append(". Cause: ").append(ex.getCause()).append(". Message: ").append(ex.getCause().getMessage());
            if (stackTrace) {
                builder.append('\n').append(MigrationExceptionFormatter.getStackTrace(ex.getCause()));
            }
            return builder.toString();
        }

        private static String format(ContentMigrationException ex, boolean stackTrace) {
            StringBuilder builder = new StringBuilder("Type: ");
            builder.append(ex.getType()).append(", Id: ").append(ex.getId()).append(", Title: ").append(ex.getTitle());
            if (StringUtils.isNotBlank((CharSequence)ex.getSpaceName())) {
                builder.append(", Space: ").append(ex.getSpaceKey()).append(" - ").append(ex.getSpaceName());
            } else {
                builder.append(", Global Scope");
            }
            builder.append(". Cause: ").append(ex.getCause()).append(". Message: ").append(ex.getCause().getMessage());
            if (stackTrace) {
                builder.append('\n').append(MigrationExceptionFormatter.getStackTrace(ex.getCause()));
            }
            return builder.toString();
        }

        private static String getStackTrace(Throwable t) {
            StringWriter writer = new StringWriter();
            PrintWriter pwriter = new PrintWriter(writer);
            t.printStackTrace(pwriter);
            return writer.toString();
        }
    }
}

