/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.util;

import org.apache.commons.codec.digest.DigestUtils;

public class BandanaKeyUtil {
    public static final String APPLICATION_ID = "applicationId";
    public static final String PROJECT_KEY = "projectKey";
    public static final String SEARCH_FILTER_ID = "searchFilterId";
    public static final String JQL = "jql";
    public static final String DATE_FIELD_NAMES = "dateFieldNames";
    public static final String DATE_FIELD_NAME = "dateFieldName";
    public static final String DURATIONS = "durations";
    public static final String DURATION = "duration";

    public static String toShaHex(String toDigest) {
        return DigestUtils.sha1Hex(toDigest);
    }
}

