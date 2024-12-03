/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 */
package com.atlassian.confluence.rest.api.model.pagination;

import com.atlassian.confluence.api.model.Expansions;

public class PaginationLimits {
    public static int content(Expansions contentExpansion) {
        if (contentExpansion.canExpand("body")) {
            return 100;
        }
        if (contentExpansion.isEmpty()) {
            return 500;
        }
        return 200;
    }

    public static int version(Expansions expansions) {
        if (expansions.canExpand("content")) {
            return 100;
        }
        return 200;
    }

    public static int labels() {
        return 200;
    }

    public static int networkFollowers() {
        return 500;
    }

    public static int networkFollowees() {
        return 500;
    }

    public static int spaces(Expansions expansions) {
        if (expansions.canExpand("homepage") || expansions.canExpand("description")) {
            return 100;
        }
        if (expansions.isEmpty()) {
            return 500;
        }
        return 200;
    }

    public static int longTasks() {
        return 100;
    }

    public static int childMap(Expansions expansions) {
        return 25;
    }

    public static int draftChildren(Expansions expansions) {
        return 100;
    }

    public static int restrictionSubjects() {
        return 200;
    }

    public static int groups() {
        return 1000;
    }

    public static int users() {
        return 200;
    }

    public static int auditRecords() {
        return 1000;
    }
}

