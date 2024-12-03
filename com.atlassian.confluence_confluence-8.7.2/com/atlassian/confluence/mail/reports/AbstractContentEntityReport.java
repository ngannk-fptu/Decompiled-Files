/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.reports;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.reports.ChangeDigestReport;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Date;

public abstract class AbstractContentEntityReport {
    private Date refDate;
    private ContentEntityObject content;

    public AbstractContentEntityReport(ContentEntityObject content, ChangeDigestReport parentReport) {
        this.content = content;
        this.refDate = new Date(System.currentTimeMillis() - parentReport.getCoverPeriod());
    }

    public boolean isNewForPeriod() {
        return this.refDate.before(this.content.getCreationDate());
    }

    public Date getLastModificationDate() {
        return this.content.getLastModificationDate();
    }

    public String getCreatorName() {
        return this.content.getCreatorName();
    }

    public Date getCreationDate() {
        return this.content.getCreationDate();
    }

    public String getLastModifierName() {
        ConfluenceUser lastModifier = this.content.getLastModifier();
        return lastModifier != null ? lastModifier.getName() : null;
    }

    public ContentEntityObject getContentEntityObject() {
        return this.content;
    }
}

