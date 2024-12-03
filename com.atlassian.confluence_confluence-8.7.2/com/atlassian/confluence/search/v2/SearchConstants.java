/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.confluence.search.v2;

import org.apache.commons.lang3.math.NumberUtils;

public final class SearchConstants {
    public static final int DEFAULT_LIMIT = 10;
    public static final int MAX_LIMIT = Integer.getInteger("confluence.search.max.limit", 500);
    public static final int MAX_START_OFFSET = Integer.getInteger("confluence.search.max.start.offset", Integer.MAX_VALUE);
    public static final String INDEXED_TITLE_FIELD_NAME = "title";
    public static final String BODY_FIELD_NAME = "contentBody";
    public static final String UNSTEMMED_TITLE_FIELD_NAME = "content-name-unstemmed";
    public static final String HIGHLIGHT_START = "@@@hl@@@";
    public static final String HIGHLIGHT_END = "@@@endhl@@@";
    private static final int DEFAULT_MAX_NUM_RESULTS = 1000;
    private static final String MAX_NUM_RESULTS_PROP = "confluence.search.max.results";
    public static final int MAX_NUM_RESULTS = NumberUtils.toInt((String)System.getProperty("confluence.search.max.results"), (int)1000);
}

