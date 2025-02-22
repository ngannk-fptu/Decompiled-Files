/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii.memcache;

public class Stats {
    private int waitingRequests;
    private int threads;
    private int uptime;
    private long cmdGet;
    private long cmdSet;
    private long cmdTouch;
    private long getHits;
    private long getMisses;
    private long deleteHits;
    private long deleteMisses;
    private long incrHits;
    private long incrMisses;
    private long decrHits;
    private long decrMisses;
    private long bytes;
    private int currConnections;
    private int totalConnections;

    public void setDeleteHits(long deleteHits) {
        this.deleteHits = deleteHits;
    }

    public void setWaitingRequests(int waitingRequests) {
        this.waitingRequests = waitingRequests;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setUptime(int uptime) {
        this.uptime = uptime;
    }

    public void setCmdGet(long cmdGet) {
        this.cmdGet = cmdGet;
    }

    public void setCmdSet(long cmdSet) {
        this.cmdSet = cmdSet;
    }

    public void setCmdTouch(long cmdTouch) {
        this.cmdTouch = cmdTouch;
    }

    public void setGetHits(long getHits) {
        this.getHits = getHits;
    }

    public void setGetMisses(long getMisses) {
        this.getMisses = getMisses;
    }

    public void setDeleteMisses(long deleteMisses) {
        this.deleteMisses = deleteMisses;
    }

    public void setIncrHits(long incrHits) {
        this.incrHits = incrHits;
    }

    public void setIncrMisses(long incrMisses) {
        this.incrMisses = incrMisses;
    }

    public void setDecrHits(long decrHits) {
        this.decrHits = decrHits;
    }

    public void setDecrMisses(long decrMisses) {
        this.decrMisses = decrMisses;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public void setCurrConnections(int currConnections) {
        this.currConnections = currConnections;
    }

    public void setTotalConnections(int totalConnections) {
        this.totalConnections = totalConnections;
    }

    public int getWaitingRequests() {
        return this.waitingRequests;
    }

    public int getThreads() {
        return this.threads;
    }

    public int getUptime() {
        return this.uptime;
    }

    public long getCmdGet() {
        return this.cmdGet;
    }

    public long getCmdSet() {
        return this.cmdSet;
    }

    public long getCmdTouch() {
        return this.cmdTouch;
    }

    public long getGetHits() {
        return this.getHits;
    }

    public long getGetMisses() {
        return this.getMisses;
    }

    public long getDeleteHits() {
        return this.deleteHits;
    }

    public long getDeleteMisses() {
        return this.deleteMisses;
    }

    public long getIncrHits() {
        return this.incrHits;
    }

    public long getIncrMisses() {
        return this.incrMisses;
    }

    public long getDecrHits() {
        return this.decrHits;
    }

    public long getDecrMisses() {
        return this.decrMisses;
    }

    public long getBytes() {
        return this.bytes;
    }

    public int getCurrConnections() {
        return this.currConnections;
    }

    public int getTotalConnections() {
        return this.totalConnections;
    }
}

