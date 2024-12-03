/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import java.util.HashMap;
import java.util.Map;

public class ScoreConfig {
    private static final float BASE_SCORE = 1.0f;
    private float followeeEdge = 1.0f;
    static final float DEFAULT_TIME_DECAY_BASE = 2.0f;
    private float timeDecayBase = 2.0f;
    private Map<EdgeType, Float> scoreByEdgeType = new HashMap<EdgeType, Float>();

    public float getTimeDecayBase() {
        return this.timeDecayBase;
    }

    public void setTimeDecayBase(float timeDecayBase) {
        this.timeDecayBase = timeDecayBase;
    }

    public void setFolloweeEdge(float followeeEdge) {
        this.followeeEdge = followeeEdge;
    }

    public float getFolloweeEdge() {
        return this.followeeEdge;
    }

    public float setScore(EdgeType edgeType, float boost) {
        return this.scoreByEdgeType.put(edgeType, Float.valueOf(boost)).floatValue();
    }

    public float getScore(EdgeType edgeType) {
        Float score = this.scoreByEdgeType.get(edgeType);
        if (score != null) {
            return score.floatValue();
        }
        return edgeType.getScore();
    }
}

