/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallbackWithoutResult
 */
package com.atlassian.confluence.content.render.xhtml.migration.tasks;

import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ExceptionReport;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.SettingsMigrationException;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.listeners.CreatePersonalSpaceListener;
import com.atlassian.confluence.spaces.listeners.InitialSpaceContentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

public final class SettingsMigratorTask
extends TransactionCallbackWithoutResult {
    private static final Logger LOG = LoggerFactory.getLogger(SettingsMigratorTask.class);
    private final SettingsManager settingsManager;
    private final ExceptionTolerantMigrator migrator;
    private final ExceptionReport report;

    public SettingsMigratorTask(SettingsManager settingsManager, ExceptionTolerantMigrator migrator, ExceptionReport report) {
        this.settingsManager = settingsManager;
        this.migrator = migrator;
        this.report = report;
    }

    protected void doInTransactionWithoutResult(TransactionStatus status) {
        String contactMessage;
        String convertedContent;
        String xhtmlContent;
        String content;
        PageContext context = new PageContext();
        boolean dirty = false;
        ArrayList<RuntimeException> exceptions = new ArrayList<RuntimeException>(1);
        Settings settings = this.settingsManager.getGlobalSettings();
        if (StringUtils.isNotBlank((CharSequence)settings.getSiteWelcomeMessage())) {
            String xhtmlContent2 = this.migrator.migrate(settings.getSiteWelcomeMessage(), context, exceptions);
            if (!this.updateReportWithErrors("siteWelcomeMessage", exceptions, this.report)) {
                settings.setSiteWelcomeMessage(xhtmlContent2);
                dirty = true;
            }
        }
        if (!InitialSpaceContentListener.getDefaultHomePageContent().equals(settings.getDefaultSpaceHomepageContent())) {
            content = SpaceContentPlaceholderTranslator.replacePlaceholders(settings.getDefaultSpaceHomepageContent());
            xhtmlContent = this.migrator.migrate(content, context, exceptions);
            if (!this.updateReportWithErrors("defaultSpaceHomepageContent", exceptions, this.report)) {
                convertedContent = SpaceContentPlaceholderTranslator.replaceTokens(xhtmlContent);
                settings.setDefaultSpaceHomepageContent(convertedContent);
                dirty = true;
            }
        }
        if (!CreatePersonalSpaceListener.getDefaultHomePageContent().equals(settings.getDefaultPersonalSpaceHomepageContent())) {
            content = SpaceContentPlaceholderTranslator.replacePlaceholders(settings.getDefaultPersonalSpaceHomepageContent());
            xhtmlContent = this.migrator.migrate(content, context, exceptions);
            if (!this.updateReportWithErrors("defaultPersonalSpaceHomepageContent", exceptions, this.report)) {
                convertedContent = SpaceContentPlaceholderTranslator.replaceTokens(xhtmlContent);
                settings.setDefaultPersonalSpaceHomepageContent(convertedContent);
                dirty = true;
            }
        }
        if (StringUtils.isNotBlank((CharSequence)(contactMessage = settings.getCustomContactMessage()))) {
            xhtmlContent = this.migrator.migrate(SpaceContentPlaceholderTranslator.replacePlaceholders(contactMessage), context, exceptions);
            if (!this.updateReportWithErrors("customContactMessage", exceptions, this.report)) {
                settings.setCustomContactMessage(xhtmlContent);
                dirty = true;
            }
        }
        if (dirty) {
            this.settingsManager.updateGlobalSettings(settings);
        }
        LOG.info("Migrated all wiki formatted settings.");
    }

    private boolean updateReportWithErrors(String setting, List<RuntimeException> exceptions, ExceptionReport report) {
        if (!exceptions.isEmpty()) {
            for (RuntimeException ex : exceptions) {
                report.addException(new SettingsMigrationException(setting, ex));
            }
            exceptions.clear();
            return true;
        }
        return false;
    }

    private static final class SpaceContentPlaceholderTranslator {
        private static final String REPLACEMENT_TOKEN = "__ATL:DEFAULT_CONTENT_PLACEHOLDER:$1";
        private static final Pattern REPLACEMENT_TOKEN_REGEX = Pattern.compile("__ATL:DEFAULT_CONTENT_PLACEHOLDER:(\\d+)");
        private static final String PLACEHOLDER = "\\{$1\\}";
        private static final Pattern PLACEHOLDER_REGEX = Pattern.compile("\\{(\\d+)\\}");

        private SpaceContentPlaceholderTranslator() {
        }

        static String replacePlaceholders(String content) {
            Matcher matcher = PLACEHOLDER_REGEX.matcher(content);
            return matcher.replaceAll(REPLACEMENT_TOKEN);
        }

        static String replaceTokens(String content) {
            Matcher matcher = REPLACEMENT_TOKEN_REGEX.matcher(content);
            return matcher.replaceAll(PLACEHOLDER);
        }
    }
}

