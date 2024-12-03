/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rpc.soap.beans;

import com.atlassian.confluence.rpc.soap.beans.RemoteContentSummary;

public class RemoteContentSummaries {
    private final int totalAvailable;
    private final int offset;
    private final RemoteContentSummary[] content;
    public static final String __PARANAMER_DATA = "<init> int,int,com.atlassian.confluence.rpc.soap.beans.RemoteContentSummary totalAvailable,offset,content \n";

    public RemoteContentSummaries(int totalAvailable, int offset, RemoteContentSummary[] content) {
        this.totalAvailable = totalAvailable;
        this.offset = offset;
        this.content = content;
    }

    public int getTotalAvailable() {
        return this.totalAvailable;
    }

    public int getOffset() {
        return this.offset;
    }

    public RemoteContentSummary[] getContent() {
        return this.content;
    }
}

