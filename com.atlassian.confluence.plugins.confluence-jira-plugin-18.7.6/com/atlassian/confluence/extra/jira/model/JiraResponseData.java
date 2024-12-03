/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.extra.jira.model;

import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JiraResponseData
implements Serializable {
    private static final long serialVersionUID = 68497944707542153L;
    private final transient AtomicInteger stackCount;
    private Status status;
    private final String serverId;
    private final int numOfIssues;
    private final AtomicInteger numOfReceivedIssues;
    private Map<String, List<String>> htmlMacro;

    public JiraResponseData(String serverId, int numOfIssues) {
        this.serverId = serverId;
        this.numOfIssues = numOfIssues;
        this.htmlMacro = Maps.newConcurrentMap();
        this.status = Status.WORKING;
        this.numOfReceivedIssues = new AtomicInteger();
        this.stackCount = new AtomicInteger(1);
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getServerId() {
        return this.serverId;
    }

    public Map<String, List<String>> getHtmlMacro() {
        return this.htmlMacro;
    }

    public void add(Map<String, List<String>> htmlMacro) {
        this.htmlMacro.putAll(htmlMacro);
        if (this.numOfReceivedIssues.addAndGet(htmlMacro.size()) == this.numOfIssues) {
            this.status = Status.COMPLETED;
        }
    }

    public int increaseStackCount() {
        return this.stackCount.incrementAndGet();
    }

    public int decreaseStackCount() {
        return this.stackCount.decrementAndGet();
    }

    public static enum Status {
        WORKING,
        COMPLETED;

    }
}

