/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.hash;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailCleaner {
    private static final Logger LOG = LoggerFactory.getLogger(EmailCleaner.class);

    public static String cleanEmail(String email) {
        if (StringUtils.isBlank((CharSequence)email)) {
            return null;
        }
        String[] emailParts = email.toLowerCase().split("@");
        if (emailParts.length != 2) {
            LOG.warn("Email is invalid '{}'; returning empty string", (Object)email);
            return null;
        }
        String firstPartWithoutPlus = StringUtils.substringBefore((String)emailParts[0], (String)"+");
        String firstPartWithoutDot = StringUtils.remove((String)firstPartWithoutPlus, (String)".");
        return firstPartWithoutDot + "@" + emailParts[1];
    }
}

