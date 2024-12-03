/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.rest.service;

import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class RestSearchParameters {
    private static final char[] LUCENE_SPECIAL_CHARACTERS = new char[]{'+', '-', '&', '|', '!', '(', ')', '{', '}', '[', ']', '^', '~', '*', '?', ':', '\\'};
    private final String query;
    private final String type;
    private final String spaceKey;
    private final Set<String> attachmentType;
    private Set<String> label;
    private boolean searchParentName;
    private final String preferredSpaceKey;

    public RestSearchParameters(String query, String type, String spaceKey, Set<String> attachmentType, Set<String> label, boolean searchParentName, String preferredSpaceKey) {
        this.query = RestSearchParameters.escapeForLucene(query);
        this.type = type;
        this.spaceKey = spaceKey;
        this.attachmentType = attachmentType;
        this.label = label;
        this.searchParentName = searchParentName;
        this.preferredSpaceKey = preferredSpaceKey;
    }

    public String getQuery() {
        return this.query;
    }

    public String getType() {
        return this.type;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public Set<String> getAttachmentType() {
        return this.attachmentType;
    }

    public Set<String> getLabel() {
        return this.label;
    }

    public boolean isSearchParentName() {
        return this.searchParentName;
    }

    public String getPreferredSpaceKey() {
        return this.preferredSpaceKey;
    }

    private static String escapeForLucene(String query) {
        if (StringUtils.isEmpty((CharSequence)query)) {
            return "";
        }
        StringBuilder str = new StringBuilder(query.length());
        for (int j = 0; j < query.length(); ++j) {
            char c = query.charAt(j);
            if (c >= '\u0080') {
                str.append(c);
                continue;
            }
            boolean found = false;
            for (char specialCharacer : LUCENE_SPECIAL_CHARACTERS) {
                if (specialCharacer != c) continue;
                str.append("\\").append(c);
                found = true;
                break;
            }
            if (found) continue;
            str.append(c);
        }
        return str.toString();
    }
}

