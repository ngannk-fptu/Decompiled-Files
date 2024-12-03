/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.configuration;

import java.util.Calendar;
import java.util.Date;

public class LastPrivacyPolicyUpdateDateProvider {
    public Date getLastPrivacyPolicyUpdateDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2014, 1, 12);
        return cal.getTime();
    }
}

