/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.index;

public final class ReIndexAuditAttributes {
    public static final String PREFIX = "reindex.";
    public static final String REINDEX_STATUS = "reindex.status";
    public static final String REINDEX_SITE = "reindex.site";
    public static final String REINDEX_SPACES = "reindex.spaces";
    public static final String REINDEX_DURATION = "reindex.duration";
    public static final String REINDEX_START_TIME = "reindex.start.time";
    public static final String REINDEX_COMPLETE_TIME = "reindex.complete.time";
    public static final String REINDEX_NODE_STATUS = "reindex.node.status";
    public static final String JOB_COMPLETED_SUFFIX = ".job.completed";
    public static final String REINDEX_SITE_FINISHED_SUMMARY = "reindex.site.job.completed";
    public static final String REINDEX_SPACES_FINISHED_SUMMARY = "reindex.spaces.job.completed";

    private ReIndexAuditAttributes() {
        throw new IllegalStateException("Constant class");
    }
}

