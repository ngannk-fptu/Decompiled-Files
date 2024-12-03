/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration.exceptions;

import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ContentMigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.PageTemplateMigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.SettingsMigrationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExceptionReport {
    private List<PageTemplateMigrationException> pageTemplateExceptions = Collections.synchronizedList(new ArrayList());
    private List<ContentMigrationException> contentExceptions = Collections.synchronizedList(new ArrayList());
    private List<SettingsMigrationException> settingsExceptions = Collections.synchronizedList(new ArrayList());
    private List<MigrationException> uncategorizedMigrationExceptions = Collections.synchronizedList(new ArrayList());

    public void addException(PageTemplateMigrationException ex) {
        this.pageTemplateExceptions.add(ex);
    }

    public void addException(ContentMigrationException ex) {
        this.contentExceptions.add(ex);
    }

    public void addException(SettingsMigrationException ex) {
        this.settingsExceptions.add(ex);
    }

    public void addUncategorizedMigrationException(MigrationException ex) {
        this.uncategorizedMigrationExceptions.add(ex);
    }

    public boolean isErrored() {
        return !this.pageTemplateExceptions.isEmpty() || !this.contentExceptions.isEmpty() || !this.settingsExceptions.isEmpty() || !this.uncategorizedMigrationExceptions.isEmpty();
    }

    public List<PageTemplateMigrationException> getPageTemplateExceptions() {
        return new ArrayList<PageTemplateMigrationException>(this.pageTemplateExceptions);
    }

    public List<ContentMigrationException> getContentMigrationExceptions() {
        return new ArrayList<ContentMigrationException>(this.contentExceptions);
    }

    public List<SettingsMigrationException> getSettingsMigrationException() {
        return new ArrayList<SettingsMigrationException>(this.settingsExceptions);
    }

    public List<MigrationException> getUncategorizedMigrationExceptions() {
        return new ArrayList<MigrationException>(this.uncategorizedMigrationExceptions);
    }

    public String toString() {
        return "ExceptionReport{pageTemplateExceptions=" + this.pageTemplateExceptions + ", contentExceptions=" + this.contentExceptions + ", settingsExceptions=" + this.settingsExceptions + ", uncategorizedMigrationExceptions=" + this.uncategorizedMigrationExceptions + "}";
    }
}

