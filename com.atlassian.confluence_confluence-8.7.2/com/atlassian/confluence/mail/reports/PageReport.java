/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.reports;

import com.atlassian.confluence.mail.reports.AbstractContentEntityReport;
import com.atlassian.confluence.mail.reports.ChangeDigestReport;
import com.atlassian.confluence.pages.AbstractPage;

public class PageReport
extends AbstractContentEntityReport {
    private AbstractPage page;

    public PageReport(AbstractPage page, ChangeDigestReport report) {
        super(page, report);
        this.page = page;
    }

    public long getId() {
        return this.page.getId();
    }

    public String getIdAsString() {
        return this.page.getIdAsString();
    }

    public String getTitle() {
        return this.page.getTitle();
    }

    public String getNameForComparison() {
        return this.page.getNameForComparison();
    }

    public String getType() {
        return this.page.getType();
    }

    public boolean isNew() {
        return this.page.isNew();
    }

    public String getXHTMLContent() {
        return "";
    }

    public AbstractPage getPage() {
        return this.page;
    }

    public int getVersion() {
        return this.page.getVersion();
    }

    public int getPreviousVersion() {
        return this.page.getPreviousVersion();
    }
}

