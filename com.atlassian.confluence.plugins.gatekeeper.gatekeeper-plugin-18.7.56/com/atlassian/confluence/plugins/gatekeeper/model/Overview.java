/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.model;

import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Overview {
    @JsonProperty(value="owners")
    private List<TinyOwner> ownerList;
    @JsonProperty(value="spaces")
    private List<TinySpace> spaceList;
    private TinyPage page;
    private int[][] permissions;
    private int totalOwnerCount;
    private long evaluationTime;

    public Overview(List<TinyOwner> ownerList, int totalOwnerCount, long evaluationTime) {
        this.ownerList = ownerList;
        this.totalOwnerCount = totalOwnerCount;
        this.evaluationTime = evaluationTime;
    }

    public Overview(List<TinyOwner> ownerList, List<TinySpace> spaceList, TinyPage page, int[][] permissions, int totalOwnerCount, long evaluationTime) {
        this.ownerList = ownerList;
        this.spaceList = spaceList;
        this.page = page;
        this.permissions = permissions;
        this.totalOwnerCount = totalOwnerCount;
        this.evaluationTime = evaluationTime;
    }

    public int[][] getPermissions() {
        return this.permissions;
    }

    public int getTotalOwnerCount() {
        return this.totalOwnerCount;
    }

    public List<TinyOwner> getOwnerList() {
        return this.ownerList;
    }

    public List<TinySpace> getSpaceList() {
        return this.spaceList;
    }

    public TinyPage getPage() {
        return this.page;
    }

    public long getEvaluationTime() {
        return this.evaluationTime;
    }
}

