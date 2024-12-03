/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.webdav.util;

import java.util.Arrays;
import java.util.List;
import org.apache.jackrabbit.webdav.DavConstants;

public interface WebdavConstants
extends DavConstants {
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_CONNECTION = "Connection";
    public static final List<String> SPECIAL_DIRECTORY_NAMES = Arrays.asList("@news", "@exports", "@versions");
}

