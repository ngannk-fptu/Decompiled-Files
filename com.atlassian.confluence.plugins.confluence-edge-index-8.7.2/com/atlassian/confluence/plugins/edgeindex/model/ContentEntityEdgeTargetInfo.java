/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.edgeindex.model;

import com.atlassian.confluence.plugins.edgeindex.model.ContentEntityObjectId;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetInfo;

public class ContentEntityEdgeTargetInfo
implements EdgeTargetInfo,
Comparable<ContentEntityEdgeTargetInfo> {
    private final String edgeTargetType;
    private final ContentEntityObjectId targetId;
    private float score;

    public ContentEntityEdgeTargetInfo(String edgeTargetType, ContentEntityObjectId targetId, float score) {
        this.edgeTargetType = edgeTargetType;
        this.targetId = targetId;
        this.score = score;
    }

    @Override
    public String getTargetType() {
        return this.edgeTargetType;
    }

    @Override
    public ContentEntityObjectId getTargetId() {
        return this.targetId;
    }

    @Override
    public float getScore() {
        return this.score;
    }

    public void incrementScore(float score) {
        this.score += score;
    }

    @Override
    public int compareTo(ContentEntityEdgeTargetInfo o) {
        return Float.compare(this.score, o.getScore());
    }

    public String toString() {
        return "ContentEntityEdgeTargetInfo{edgeTargetType='" + this.edgeTargetType + "', targetId=" + this.targetId + ", score=" + this.score + "}";
    }
}

