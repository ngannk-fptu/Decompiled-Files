/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.webdav.job;

public interface ContentJob {
    public long getMinimumAgeForExecution();

    public long getCreationTime();

    public void execute() throws Exception;
}

