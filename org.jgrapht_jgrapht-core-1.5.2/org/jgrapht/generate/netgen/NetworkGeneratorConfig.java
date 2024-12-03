/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate.netgen;

public class NetworkGeneratorConfig {
    private final int nodeNum;
    private final int arcNum;
    private final int sourceNum;
    private final int sinkNum;
    private final int transshipSourceNum;
    private final int transshipSinkNum;
    private final int totalSupply;
    private final int minCap;
    private final int maxCap;
    private final int minCost;
    private final int maxCost;
    private final int percentCapacitated;
    private final int percentWithInfCost;

    NetworkGeneratorConfig(int nodeNum, int arcNum, int sourceNum, int sinkNum, int transshipSourceNum, int transshipSinkNum, int totalSupply, int minCap, int maxCap, int minCost, int maxCost, int percentCapacitated, int percentWithInfCost) {
        this.nodeNum = nodeNum;
        this.arcNum = arcNum;
        this.sourceNum = sourceNum;
        this.sinkNum = sinkNum;
        this.transshipSourceNum = transshipSourceNum;
        this.transshipSinkNum = transshipSinkNum;
        this.totalSupply = totalSupply;
        this.minCap = minCap;
        this.maxCap = maxCap;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.percentCapacitated = percentCapacitated;
        this.percentWithInfCost = percentWithInfCost;
    }

    public long getMaxSource2TSourceArcNum() {
        return (long)this.getPureSourceNum() * (long)this.transshipSourceNum + (long)this.transshipSourceNum * (long)(this.transshipSourceNum - 1);
    }

    public long getMaxSource2TNodeArcNum() {
        return (long)this.sourceNum * (long)this.getTransshipNodeNum();
    }

    public long getMaxSource2SinkArcNum() {
        return (long)this.sourceNum * (long)this.sinkNum;
    }

    public long getMaxTNode2TSourceArcNum() {
        return (long)this.getTransshipNodeNum() * (long)this.transshipSourceNum;
    }

    public long getMaxTNode2TNodeArcNum() {
        return (long)this.getTransshipNodeNum() * (long)(this.getTransshipNodeNum() - 1);
    }

    public long getMaxTNode2SinkArcNum() {
        return (long)this.getTransshipNodeNum() * (long)this.sinkNum;
    }

    public long getMaxTSink2TSourceArcNum() {
        return (long)this.transshipSinkNum * (long)this.transshipSourceNum;
    }

    public long getMaxTSink2TNodeArcNum() {
        return (long)this.transshipSinkNum * (long)this.getTransshipNodeNum();
    }

    public long getMaxTSink2SinkArcNum() {
        return (long)this.transshipSinkNum * (long)(this.transshipSinkNum - 1) + (long)(this.getPureSinkNum() * this.transshipSinkNum);
    }

    public long getMaxSource2AllArcNum() {
        return this.getMaxSource2TSourceArcNum() + this.getMaxSource2TNodeArcNum() + this.getMaxSource2SinkArcNum();
    }

    public long getMaxTransshipNode2AllArcNum() {
        return this.getMaxTNode2TSourceArcNum() + this.getMaxTNode2TNodeArcNum() + this.getMaxTNode2SinkArcNum();
    }

    public long getMaxSink2ALlArcNum() {
        return this.getMaxTSink2TSourceArcNum() + this.getMaxTSink2TNodeArcNum() + this.getMaxTSink2SinkArcNum();
    }

    public long getMinimumArcNum() {
        return this.getTransshipNodeNum() + Math.max(this.getSourceNum(), this.getSinkNum());
    }

    public long getMaximumArcNum() {
        return this.getMaxSource2AllArcNum() + this.getMaxTransshipNode2AllArcNum() + this.getMaxSink2ALlArcNum();
    }

    public static long getMinimumArcNum(long sourceNum, long tNodeNum, long sinkNum) {
        return tNodeNum + Math.max(sourceNum, sinkNum);
    }

    public static long getMaximumArcNum(long sourceNum, long tNodeNum, long sinkNum) {
        return NetworkGeneratorConfig.getMaximumArcNum(sourceNum, 0L, tNodeNum, 0L, sinkNum);
    }

    public static long getMaximumArcNum(long sourceNum, long tSourceNum, long tNodeNum, long tSinkNum, long sinkNum) {
        long pureSourceNum = sourceNum - tSourceNum;
        long sourceArcs = pureSourceNum * tSourceNum + tSourceNum * (tSourceNum - 1L) + sourceNum * (tNodeNum + sinkNum);
        long tNodeArcs = tNodeNum * (tSourceNum + (tNodeNum - 1L) + sinkNum);
        long sinkArcs = tSinkNum * (tSourceNum + tNodeNum + (sinkNum - 1L));
        return sourceArcs + tNodeArcs + sinkArcs;
    }

    public int getPureSourceNum() {
        return this.sourceNum - this.transshipSourceNum;
    }

    public int getPureSinkNum() {
        return this.sinkNum - this.transshipSinkNum;
    }

    public boolean isCostWeighted() {
        return this.minCost != this.maxCost;
    }

    public int getTransshipNodeNum() {
        return this.nodeNum - this.sourceNum - this.sinkNum;
    }

    private boolean transportationProblemCondition() {
        return this.sourceNum + this.sinkNum == this.nodeNum && this.transshipSourceNum == 0 && this.transshipSinkNum == 0;
    }

    private boolean assignmentProblemCondition() {
        return this.sourceNum == this.sinkNum && this.totalSupply == this.sourceNum && this.minCap == 1 && this.maxCap == 1;
    }

    public boolean isMaxFlowProblem() {
        return !this.isCostWeighted();
    }

    public boolean isAssignmentProblem() {
        return this.transportationProblemCondition() && this.assignmentProblemCondition();
    }

    public int getNodeNum() {
        return this.nodeNum;
    }

    public int getArcNum() {
        return this.arcNum;
    }

    public int getSourceNum() {
        return this.sourceNum;
    }

    public int getSinkNum() {
        return this.sinkNum;
    }

    public int getTransshipSourceNum() {
        return this.transshipSourceNum;
    }

    public int getTransshipSinkNum() {
        return this.transshipSinkNum;
    }

    public int getTotalSupply() {
        return this.totalSupply;
    }

    public int getMinCap() {
        return this.minCap;
    }

    public int getMaxCap() {
        return this.maxCap;
    }

    public int getMinCost() {
        return this.minCost;
    }

    public int getMaxCost() {
        return this.maxCost;
    }

    public int getPercentCapacitated() {
        return this.percentCapacitated;
    }

    public int getPercentWithInfCost() {
        return this.percentWithInfCost;
    }
}

