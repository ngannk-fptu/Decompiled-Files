/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate.netgen;

import org.jgrapht.generate.netgen.NetworkGeneratorConfig;

public class NetworkGeneratorConfigBuilder {
    int nodeNum = 0;
    int arcNum = 0;
    int sourceNum = 0;
    int sinkNum = 0;
    int tSourceNum = 0;
    int tSinkNum = 0;
    int totalSupply = 0;
    int minCap = 0;
    int maxCap = 0;
    int minCost = 0;
    int maxCost = 0;
    int percentCapacitated = 100;
    int percentWithInfCost = 0;

    public NetworkGeneratorConfig build() {
        if (this.nodeNum <= 0) {
            this.invalidParam("Number of nodes must be positive");
        } else if (this.arcNum <= 0) {
            this.invalidParam("Number of arcs must be positive");
        } else if (this.sourceNum <= 0) {
            this.invalidParam("Number of sources must be positive");
        } else if (this.sinkNum <= 0) {
            this.invalidParam("Number of sinks must be positive");
        } else if (this.sourceNum + this.sinkNum > this.nodeNum) {
            this.invalidParam("Number of sources and sinks must not exceed the number of nodes");
        } else if (this.tSourceNum > this.sourceNum) {
            this.invalidParam("Number of transhipment sources must not exceed the overall number of sources");
        } else if (this.tSinkNum > this.sinkNum) {
            this.invalidParam("Number of transhipment sinks must not exceed the overall number of sinks");
        } else if (this.totalSupply < Math.max(this.sourceNum, this.sinkNum)) {
            this.invalidParam("Total supply must not be less than the number of sources and the number of sinks");
        } else if (this.minCap > this.maxCap) {
            this.invalidParam("Minimum capacity must not exceed the maximum capacity");
        } else if (this.minCap <= 0) {
            this.invalidParam("Minimum capacity must be positive");
        } else if (this.minCost > this.maxCost) {
            this.invalidParam("Minimum cost must not exceed the maximum cost");
        }
        int tNodeNum = this.nodeNum - this.sourceNum - this.sinkNum;
        long minArcNum = NetworkGeneratorConfig.getMinimumArcNum(this.sourceNum, tNodeNum, this.sinkNum);
        long maxArcNum = NetworkGeneratorConfig.getMaximumArcNum(this.sourceNum, this.tSourceNum, tNodeNum, this.tSinkNum, this.sinkNum);
        if ((long)this.arcNum < minArcNum) {
            this.invalidParam("Too few arcs to generate a valid problem");
        } else if ((long)this.arcNum > maxArcNum) {
            this.invalidParam("Too many arcs to generate a valid problem");
        }
        return new NetworkGeneratorConfig(this.nodeNum, this.arcNum, this.sourceNum, this.sinkNum, this.tSourceNum, this.tSinkNum, this.totalSupply, this.minCap, this.maxCap, this.minCost, this.maxCost, this.percentCapacitated, this.percentWithInfCost);
    }

    private void invalidParam(String message) {
        throw new IllegalArgumentException(message);
    }

    private int checkNodeConstraint(int value) {
        if (value > 100000000) {
            this.invalidParam(String.format("Number of nodes must not exceed %d", 100000000));
        }
        return value;
    }

    private int checkCapacityCostConstraint(int value) {
        if (Math.abs(value) > 2000000000) {
            this.invalidParam(String.format("Arcs capacities and cost must be between -%d and %d", 2000000000, 2000000000));
        }
        return value;
    }

    public NetworkGeneratorConfigBuilder setParams(int nodeNum, int arcNum, int sourceNum, int sinkNum, int transshipSourceNum, int transshipSinkNum, int totalSupply, int minCap, int maxCap, int minCost, int maxCost, int percentCapacitated, int percentWithInfCost) {
        this.setNodeNum(nodeNum);
        this.setArcNum(arcNum);
        this.setSourceNum(sourceNum);
        this.setSinkNum(sinkNum);
        this.setTSourceNum(transshipSourceNum);
        this.setTSinkNum(transshipSinkNum);
        this.setTotalSupply(totalSupply);
        this.setMinCap(minCap);
        this.setMaxCap(maxCap);
        this.setMinCost(minCost);
        this.setMaxCost(maxCost);
        this.setPercentCapacitated(percentCapacitated);
        this.setPercentWithInfCost(percentWithInfCost);
        return this;
    }

    public NetworkGeneratorConfigBuilder setMaximumFlowProblemParams(int nodeNum, int arcNum, int supply) {
        this.setMaximumFlowProblemParams(nodeNum, arcNum, supply, 1, 1);
        return this;
    }

    public NetworkGeneratorConfigBuilder setMaximumFlowProblemParams(int nodeNum, int arcNum, int supply, int minCap, int maxCap) {
        this.setMaximumFlowProblemParams(nodeNum, arcNum, supply, minCap, maxCap, 1, 1);
        return this;
    }

    public NetworkGeneratorConfigBuilder setMaximumFlowProblemParams(int nodeNum, int arcNum, int supply, int minCap, int maxCap, int sourceNum, int sinkNum) {
        this.setMaximumFlowProblemParams(nodeNum, arcNum, supply, minCap, maxCap, sourceNum, sinkNum, 100);
        return this;
    }

    public NetworkGeneratorConfigBuilder setMaximumFlowProblemParams(int nodeNum, int arcNum, int supply, int minCap, int maxCap, int sourceNum, int sinkNum, int percentCapacitated) {
        this.setParams(nodeNum, arcNum, sourceNum, sinkNum, 0, 0, supply, minCap, maxCap, 1, 1, percentCapacitated, 0);
        return this;
    }

    public NetworkGeneratorConfigBuilder setBipartiteMatchingProblemParams(int nodeNum, int arcNum) {
        this.setBipartiteMatchingProblemParams(nodeNum, arcNum, 1, 1);
        return this;
    }

    public NetworkGeneratorConfigBuilder setBipartiteMatchingProblemParams(int nodeNum, int arcNum, int minCost, int maxCost) {
        this.setBipartiteMatchingProblemParams(nodeNum, arcNum, minCost, maxCost, 0);
        return this;
    }

    public NetworkGeneratorConfigBuilder setBipartiteMatchingProblemParams(int nodeNum, int arcNum, int minCost, int maxCost, int percentWithInfCost) {
        if ((nodeNum & 1) != 0) {
            this.invalidParam("Assignment problem must have even number of nodes");
        }
        this.setParams(nodeNum, arcNum, nodeNum / 2, nodeNum / 2, 0, 0, nodeNum / 2, 1, 1, minCost, maxCost, 100, percentWithInfCost);
        return this;
    }

    public NetworkGeneratorConfigBuilder setNodeNum(int nodeNum) {
        if (nodeNum <= 0) {
            this.invalidParam("Number of nodes must be positive");
        }
        this.nodeNum = this.checkNodeConstraint(nodeNum);
        return this;
    }

    public NetworkGeneratorConfigBuilder setArcNum(int arcNum) {
        if (arcNum > 2000000000) {
            this.invalidParam(String.format("Number of arcs must not exceed %d", arcNum));
        }
        this.arcNum = arcNum;
        return this;
    }

    public NetworkGeneratorConfigBuilder setSourceNum(int sourceNum) {
        if (sourceNum <= 0) {
            this.invalidParam("Number of sources must be positive");
        }
        this.sourceNum = this.checkNodeConstraint(sourceNum);
        return this;
    }

    public NetworkGeneratorConfigBuilder setSinkNum(int sinkNum) {
        if (sinkNum <= 0) {
            this.invalidParam("Number of sinks must be positive");
        }
        this.sinkNum = this.checkNodeConstraint(sinkNum);
        return this;
    }

    public NetworkGeneratorConfigBuilder setTSourceNum(int tSourceNum) {
        if (tSourceNum < 0) {
            this.invalidParam("Number of transshipment sources must be non-negative");
        }
        this.tSourceNum = this.checkNodeConstraint(tSourceNum);
        return this;
    }

    public NetworkGeneratorConfigBuilder setTSinkNum(int tSinkNum) {
        if (tSinkNum < 0) {
            this.invalidParam("Number of transshipment sinks must be non-negative");
        }
        this.tSinkNum = this.checkNodeConstraint(tSinkNum);
        return this;
    }

    public NetworkGeneratorConfigBuilder setTotalSupply(int totalSupply) {
        if (totalSupply > 200000000) {
            this.invalidParam(String.format("Total supply must not exceed %d", 100000000));
        }
        this.totalSupply = totalSupply;
        return this;
    }

    public NetworkGeneratorConfigBuilder setMinCap(int minCap) {
        if (minCap < 0) {
            this.invalidParam("Minimum arc capacity must be non-negative");
        }
        this.minCap = this.checkCapacityCostConstraint(minCap);
        return this;
    }

    public NetworkGeneratorConfigBuilder setMaxCap(int maxCap) {
        if (maxCap < 0) {
            this.invalidParam("Maximum arc capacity must be non-negative");
        }
        this.maxCap = this.checkCapacityCostConstraint(maxCap);
        return this;
    }

    public NetworkGeneratorConfigBuilder setMinCost(int minCost) {
        this.minCost = this.checkCapacityCostConstraint(minCost);
        return this;
    }

    public NetworkGeneratorConfigBuilder setMaxCost(int maxCost) {
        this.maxCost = this.checkCapacityCostConstraint(maxCost);
        return this;
    }

    public NetworkGeneratorConfigBuilder setPercentCapacitated(int percentCapacitated) {
        if (percentCapacitated < 0 || percentCapacitated > 100) {
            this.invalidParam("Percent of capacitated arcs must be between 0 and 100 inclusive");
        }
        this.percentCapacitated = percentCapacitated;
        return this;
    }

    public NetworkGeneratorConfigBuilder setPercentWithInfCost(int percentWithInfCost) {
        if (percentWithInfCost < 0 || percentWithInfCost > 100) {
            this.invalidParam("Percent of arcs with infinite cost must be between 0 and 100 inclusive");
        }
        this.percentWithInfCost = percentWithInfCost;
        return this;
    }
}

