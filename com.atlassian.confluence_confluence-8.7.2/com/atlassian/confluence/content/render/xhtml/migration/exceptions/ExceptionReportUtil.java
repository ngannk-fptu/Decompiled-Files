/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Functions
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableSortedMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Ordering
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration.exceptions;

import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ContentMigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ExceptionReport;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.PageTemplateMigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.SettingsMigrationException;
import com.google.common.base.Functions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ExceptionReportUtil {
    public static String generateReportString(String reportTitle, ExceptionReport report, boolean includeStackTrace) {
        List<SettingsMigrationException> settingsExceptions = report.getSettingsMigrationException();
        List<PageTemplateMigrationException> templateExceptions = report.getPageTemplateExceptions();
        List<ContentMigrationException> contentExceptions = report.getContentMigrationExceptions();
        List<MigrationException> uncategorizedMigrationExceptions = report.getUncategorizedMigrationExceptions();
        StringBuilder logBuilder = new StringBuilder(reportTitle).append(":\n");
        logBuilder.append("Summary:\n").append("\t").append(settingsExceptions.size()).append(" settings values failed.\n").append("\t").append(templateExceptions.size()).append(" PageTemplates failed.\n").append("\t").append(contentExceptions.size()).append(" ContentEntityObjects failed.\n").append("\t").append(uncategorizedMigrationExceptions.size()).append(" uncategorized migration errors.\n");
        ExceptionReportUtil.appendSettingsExceptionLogDetails(logBuilder, settingsExceptions, includeStackTrace);
        ExceptionReportUtil.appendPageTemplateExceptionLogDetails(logBuilder, templateExceptions, includeStackTrace);
        ExceptionReportUtil.appendContentExceptionLogDetails(logBuilder, contentExceptions, includeStackTrace);
        ExceptionReportUtil.appendUncategorizedMigrationExceptionLogDetails(logBuilder, uncategorizedMigrationExceptions, includeStackTrace);
        return logBuilder.toString();
    }

    private static void appendUncategorizedMigrationExceptionLogDetails(StringBuilder logBuilder, List<MigrationException> uncategorizedMigrationExceptions, boolean includeStackTrace) {
        if (!uncategorizedMigrationExceptions.isEmpty()) {
            logBuilder.append("Uncategorized Exceptions:\n");
            for (int i = 0; i < uncategorizedMigrationExceptions.size(); ++i) {
                MigrationException migrationException = uncategorizedMigrationExceptions.get(i);
                logBuilder.append("\t").append(i + 1).append(") ").append(MigrationExceptionFormatter.format(migrationException, includeStackTrace)).append("\n");
            }
        }
    }

    private static void appendPageTemplateExceptionLogDetails(StringBuilder logBuilder, List<PageTemplateMigrationException> templateExceptions, boolean includeStackTrace) {
        if (!templateExceptions.isEmpty()) {
            logBuilder.append("PageTemplate Exceptions:\n");
            for (int i = 0; i < templateExceptions.size(); ++i) {
                logBuilder.append("\t").append(i + 1).append(") ").append(MigrationExceptionFormatter.format(templateExceptions.get(i), includeStackTrace)).append("\n");
            }
        }
    }

    private static void appendSettingsExceptionLogDetails(StringBuilder logBuilder, List<SettingsMigrationException> settingsExceptions, boolean includeStackTrace) {
        if (!settingsExceptions.isEmpty()) {
            logBuilder.append("Settings Exceptions:\n");
            for (int i = 0; i < settingsExceptions.size(); ++i) {
                logBuilder.append("\t").append(i + 1).append(") ").append(MigrationExceptionFormatter.format(settingsExceptions.get(i), includeStackTrace)).append("\n");
            }
        }
    }

    private static void appendContentExceptionLogDetails(StringBuilder logBuilder, List<ContentMigrationException> contentExceptions, boolean includeStackTrace) {
        if (!contentExceptions.isEmpty()) {
            logBuilder.append("Content Migration Exceptions:\n");
            HashMap exceptionStats = Maps.newHashMapWithExpectedSize((int)contentExceptions.size());
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
                Ordering comparator = Ordering.natural().onResultOf(Functions.forMap((Map)exceptionStats)).compound((Comparator)Ordering.natural());
                comparator = comparator.reverse();
                ImmutableSortedMap sortedStats = ImmutableSortedMap.copyOf((Map)exceptionStats, (Comparator)comparator);
                logBuilder.append("\nContent Migration Exception statistics:\n");
                for (Map.Entry entry : sortedStats.entrySet()) {
                    logBuilder.append(String.format("Count: %6d, %s\n", entry.getValue(), ((String)entry.getKey()).replaceAll("\n", "\\n")));
                }
            }
        }
    }

    private static class MigrationExceptionFormatter {
        private MigrationExceptionFormatter() {
        }

        private static String format(SettingsMigrationException ex, boolean stackTrace) {
            StringBuilder builder = new StringBuilder("Setting: ");
            builder.append(ex.getSettingName());
            return MigrationExceptionFormatter.withCauseAndStackTrace(ex, stackTrace, builder);
        }

        private static String format(MigrationException ex, boolean stackTrace) {
            return MigrationExceptionFormatter.withCauseAndStackTrace(ex, stackTrace, new StringBuilder());
        }

        private static String withCauseAndStackTrace(MigrationException migrationException, boolean stackTrace, StringBuilder builder) {
            for (Throwable cause : Throwables.getCausalChain((Throwable)migrationException)) {
                builder.append(". Cause: ").append(cause);
            }
            if (stackTrace) {
                builder.append('\n').append(MigrationExceptionFormatter.getStackTrace(migrationException));
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
            return MigrationExceptionFormatter.withCauseAndStackTrace(ex, stackTrace, builder);
        }

        private static String format(ContentMigrationException ex, boolean stackTrace) {
            StringBuilder builder = new StringBuilder("Type: ");
            builder.append(ex.getType()).append(", Id: ").append(ex.getId()).append(", Title: ").append(ex.getTitle());
            if (StringUtils.isNotBlank((CharSequence)ex.getSpaceName())) {
                builder.append(", Space: ").append(ex.getSpaceKey()).append(" - ").append(ex.getSpaceName());
            } else {
                builder.append(", Global Scope");
            }
            return MigrationExceptionFormatter.withCauseAndStackTrace(ex, stackTrace, builder);
        }

        private static String getStackTrace(Throwable throwable) {
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            return writer.toString();
        }
    }
}

