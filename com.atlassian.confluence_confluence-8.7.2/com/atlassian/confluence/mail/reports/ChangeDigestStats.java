/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.reports;

import com.atlassian.confluence.mail.reports.ChangeDigestReport;
import com.atlassian.confluence.mail.reports.PageReport;
import com.atlassian.confluence.mail.reports.SpaceReport;

public class ChangeDigestStats {
    private int totalPagesAdded = 0;
    private int totalPagesAddedByUser = 0;
    private int totalPagesEdited = 0;
    private int totalPagesEditedByUser = 0;
    private int totalPagesEditedButCreatedByUser = 0;
    private int totalSpacesAdded = 0;
    private int totalSpacesAddedByUser = 0;
    private int totalSpacesEdited = 0;
    private int totalSpacesEditedByUser = 0;
    private int totalSpacesEditedButCreatedByUser = 0;

    public ChangeDigestStats(ChangeDigestReport report) {
        if (report == null) {
            throw new IllegalArgumentException("The report for digester stats cannot be null");
        }
        if (report.getUser() == null) {
            throw new IllegalArgumentException("The User on the digest report cannot be null");
        }
        for (SpaceReport space : report.getSpaceReports()) {
            if (space.isNewForPeriod()) {
                ++this.totalSpacesAdded;
                if (report.getUser().getName().equals(space.getCreatorName())) {
                    ++this.totalSpacesAddedByUser;
                }
                if (space.getLastModificationDate().getTime() > space.getCreationDate().getTime() + 1000L) {
                    ++this.totalSpacesEdited;
                    if (report.getUser().getName().equals(space.getLastModifierName())) {
                        ++this.totalSpacesEditedByUser;
                    }
                    if (report.getUser().getName().equals(space.getCreatorName())) {
                        ++this.totalSpacesEditedButCreatedByUser;
                    }
                }
            } else {
                ++this.totalSpacesEdited;
                if (report.getUser().getName().equals(space.getLastModifierName())) {
                    ++this.totalSpacesEditedByUser;
                }
                if (report.getUser().getName().equals(space.getCreatorName())) {
                    ++this.totalSpacesEditedButCreatedByUser;
                }
            }
            for (PageReport page : space.getPages()) {
                if (page.isNewForPeriod()) {
                    ++this.totalPagesAdded;
                    if (report.getUser().getName().equals(page.getCreatorName())) {
                        ++this.totalPagesAddedByUser;
                    }
                    if (page.getLastModificationDate().getTime() <= page.getCreationDate().getTime() + 1000L) continue;
                    ++this.totalPagesEdited;
                    if (report.getUser().getName().equals(page.getLastModifierName())) {
                        ++this.totalPagesEditedByUser;
                    }
                    if (!report.getUser().getName().equals(page.getCreatorName())) continue;
                    ++this.totalPagesEditedButCreatedByUser;
                    continue;
                }
                ++this.totalPagesEdited;
                if (report.getUser().getName().equals(page.getLastModifierName())) {
                    ++this.totalPagesEditedByUser;
                }
                if (!report.getUser().getName().equals(page.getCreatorName())) continue;
                ++this.totalPagesEditedButCreatedByUser;
            }
        }
    }

    public int getTotalPagesAdded() {
        return this.totalPagesAdded;
    }

    public int getTotalPagesAddedByUser() {
        return this.totalPagesAddedByUser;
    }

    public int getTotalPagesEdited() {
        return this.totalPagesEdited;
    }

    public int getTotalPagesEditedByUser() {
        return this.totalPagesEditedByUser;
    }

    public int getTotalSpacesAdded() {
        return this.totalSpacesAdded;
    }

    public int getTotalSpacesAddedByUser() {
        return this.totalSpacesAddedByUser;
    }

    public int getTotalSpacesEdited() {
        return this.totalSpacesEdited;
    }

    public int getTotalSpacesEditedByUser() {
        return this.totalSpacesEditedByUser;
    }

    public int getTotalPagesEditedButCreatedByUser() {
        return this.totalPagesEditedButCreatedByUser;
    }

    public int getTotalSpacesEditedButCreatedByUser() {
        return this.totalSpacesEditedButCreatedByUser;
    }
}

