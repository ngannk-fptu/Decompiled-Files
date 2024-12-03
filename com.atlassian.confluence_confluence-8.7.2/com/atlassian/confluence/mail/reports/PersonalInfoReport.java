/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.reports;

import com.atlassian.confluence.mail.reports.AbstractContentEntityReport;
import com.atlassian.confluence.mail.reports.ChangeDigestReport;
import com.atlassian.confluence.user.PersonalInformation;

public class PersonalInfoReport
extends AbstractContentEntityReport {
    private PersonalInformation personalInformation;

    public PersonalInfoReport(PersonalInformation personalInformation, ChangeDigestReport changeDigestReport) {
        super(personalInformation, changeDigestReport);
        this.personalInformation = personalInformation;
    }

    public PersonalInformation getPersonalInformation() {
        return this.personalInformation;
    }
}

