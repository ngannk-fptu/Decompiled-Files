/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.plugins.edgeindex.EdgeIndexSchema;
import com.atlassian.confluence.plugins.edgeindex.EdgeTypeRepository;
import com.atlassian.confluence.plugins.edgeindex.ScoreConfig;
import com.atlassian.confluence.plugins.edgeindex.model.ContentEntityEdgeTargetInfo;
import com.atlassian.confluence.plugins.edgeindex.model.ContentEntityObjectId;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetInfo;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeType;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TopEdgeTargetCollector
implements Consumer<Map<String, String[]>> {
    static final int DECAY_THRESHOLD_SECONDS = 86400;
    private final long currentTimeSeconds;
    private final EdgeTypeRepository edgeTypeRepository;
    private final Set<UserKey> followeeKeys;
    private final ScoreConfig scoreConfig;
    private final Predicate<EdgeTargetInfo> acceptFilter;
    private final int maxTarget;
    private final PriorityQueue<EdgeTargetInfo> queue;
    private ContentEntityEdgeTargetInfo lastTarget;

    public TopEdgeTargetCollector(EdgeTypeRepository edgeTypeRepository, Set<UserKey> followeeKeys, ScoreConfig scoreConfig, Predicate<EdgeTargetInfo> acceptFilter, int maxTarget, Date now) {
        Preconditions.checkArgument((maxTarget > 0 ? 1 : 0) != 0, (String)"non positive value: %s", (int)maxTarget);
        this.edgeTypeRepository = edgeTypeRepository;
        this.followeeKeys = followeeKeys;
        this.currentTimeSeconds = now.getTime() / 1000L;
        this.scoreConfig = scoreConfig;
        this.acceptFilter = acceptFilter;
        this.maxTarget = maxTarget;
        this.queue = new PriorityQueue(Math.min(maxTarget, 1024), (a, b) -> Float.compare(a.getScore(), b.getScore()));
        this.lastTarget = null;
    }

    public List<EdgeTargetInfo> getTopTargets() {
        if (this.lastTarget != null) {
            this.updateQueue(this.lastTarget);
        }
        LinkedList<EdgeTargetInfo> targets = new LinkedList<EdgeTargetInfo>();
        while (!this.queue.isEmpty()) {
            targets.addFirst(this.queue.poll());
        }
        return targets;
    }

    private float timeDecayFactor(long edgeAgeDays) {
        return (float)(1.0 / Math.pow(this.scoreConfig.getTimeDecayBase(), edgeAgeDays));
    }

    private int getRoundedDays(long timeSeconds) {
        return (int)Math.floor((float)timeSeconds / 86400.0f + 1.0f);
    }

    private void updateQueue(EdgeTargetInfo targetInfo) {
        this.queue.offer(targetInfo);
        if (this.queue.size() > this.maxTarget) {
            this.queue.poll();
        }
    }

    @Override
    public void accept(Map<String, String[]> fieldMap) {
        long edgeAgeSeconds;
        ContentEntityObjectId targetId = new ContentEntityObjectId(Long.parseLong(fieldMap.get(EdgeIndexSchema.EDGE_TARGET_ID)[0]));
        String targetType = fieldMap.get("edge.targetType")[0];
        long edgeTimeSeconds = Long.parseLong(fieldMap.get("edge.date")[0]);
        String edgeTypeKey = fieldMap.get("edge.type")[0];
        String edgeUserKey = fieldMap.get(EdgeIndexSchema.EDGE_USERKEY)[0];
        if (!this.acceptFilter.test(new ContentEntityEdgeTargetInfo(targetType, targetId, 0.0f))) {
            return;
        }
        if (this.lastTarget == null) {
            this.lastTarget = new ContentEntityEdgeTargetInfo(targetType, targetId, 0.0f);
        }
        if (!this.lastTarget.getTargetId().equals(targetId)) {
            this.updateQueue(this.lastTarget);
            this.lastTarget = new ContentEntityEdgeTargetInfo(targetType, targetId, 0.0f);
        }
        float edgeScore = this.scoreConfig.getScore((EdgeType)this.edgeTypeRepository.getEdgeIndexTypeByKey(edgeTypeKey).get());
        if (this.followeeKeys.contains(new UserKey(edgeUserKey))) {
            edgeScore += this.scoreConfig.getFolloweeEdge();
        }
        if ((edgeAgeSeconds = this.currentTimeSeconds - edgeTimeSeconds) > 86400L) {
            int edgeAgeDays = this.getRoundedDays(edgeAgeSeconds);
            edgeScore *= this.timeDecayFactor(edgeAgeDays);
        }
        this.lastTarget.incrementScore(edgeScore);
    }
}

