/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.reports;

import com.atlassian.confluence.mail.reports.AbstractContentEntityReport;
import com.atlassian.confluence.mail.reports.ChangeDigestReport;
import com.atlassian.confluence.spaces.SpaceDescription;

public class SpaceDescriptionReport
extends AbstractContentEntityReport {
    public SpaceDescriptionReport(SpaceDescription spaceDescription, ChangeDigestReport parentReport) {
        super(spaceDescription, parentReport);
    }
}

