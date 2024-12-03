/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.cluster;

import java.util.Objects;

public class ClusterInfoEntity {
    private String nodeId;
    private String ipAddress;
    private String hostname;
    private Long currentHeap;
    private Long maxHeap;
    private Double loadAverage;
    private Long uptime;
    private long infoTimestamp;

    protected ClusterInfoEntity() {
    }

    public ClusterInfoEntity(String nodeId, String ipAddress, String hostname, Long currentHeap, Long maxHeap, Double loadAverage, Long uptime, long infoTimestamp) {
        this.nodeId = nodeId;
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.currentHeap = currentHeap;
        this.maxHeap = maxHeap;
        this.loadAverage = loadAverage;
        this.uptime = uptime;
        this.infoTimestamp = infoTimestamp;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    protected void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    protected void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getHostname() {
        return this.hostname;
    }

    protected void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Long getCurrentHeap() {
        return this.currentHeap;
    }

    protected void setCurrentHeap(Long currentHeap) {
        this.currentHeap = currentHeap;
    }

    public Long getMaxHeap() {
        return this.maxHeap;
    }

    protected void setMaxHeap(Long maxHeap) {
        this.maxHeap = maxHeap;
    }

    public Double getLoadAverage() {
        return this.loadAverage;
    }

    protected void setLoadAverage(Double loadAverage) {
        this.loadAverage = loadAverage;
    }

    public Long getUptime() {
        return this.uptime;
    }

    protected void setUptime(Long uptime) {
        this.uptime = uptime;
    }

    public long getInfoTimestamp() {
        return this.infoTimestamp;
    }

    protected void setInfoTimestamp(long infoTimestamp) {
        this.infoTimestamp = infoTimestamp;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClusterInfoEntity that = (ClusterInfoEntity)o;
        return this.infoTimestamp == that.infoTimestamp && Objects.equals(this.nodeId, that.nodeId) && Objects.equals(this.ipAddress, that.ipAddress) && Objects.equals(this.hostname, that.hostname) && Objects.equals(this.currentHeap, that.currentHeap) && Objects.equals(this.maxHeap, that.maxHeap) && Objects.equals(this.loadAverage, that.loadAverage) && Objects.equals(this.uptime, that.uptime);
    }

    public int hashCode() {
        return Objects.hash(this.nodeId, this.ipAddress, this.hostname, this.currentHeap, this.maxHeap, this.loadAverage, this.uptime, this.infoTimestamp);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ClusterInfoEntity{");
        sb.append("nodeId='").append(this.nodeId).append('\'');
        sb.append(", ipAddress='").append(this.ipAddress).append('\'');
        sb.append(", hostname='").append(this.hostname).append('\'');
        sb.append(", currentHeap=").append(this.currentHeap);
        sb.append(", maxHeap=").append(this.maxHeap);
        sb.append(", loadAverage=").append(this.loadAverage);
        sb.append(", uptime=").append(this.uptime);
        sb.append(", infoTimestamp=").append(this.infoTimestamp);
        sb.append('}');
        return sb.toString();
    }
}

