/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 */
package com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result;

import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

public class PreEvaluationResult {
    private List<TinySpace> spaces;
    private int totalSpaces;
    private int totalOwners;
    @JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
    private long pageId;
    @JsonInclude(value=JsonInclude.Include.NON_DEFAULT)
    private String pageTitle;

    protected PreEvaluationResult() {
    }

    public PreEvaluationResult(List<TinySpace> spaces, int totalOwners, int totalSpaces, TinyPage page) {
        this.spaces = spaces;
        this.totalOwners = totalOwners;
        this.totalSpaces = totalSpaces;
        if (page != null) {
            this.pageId = page.getId();
            this.pageTitle = page.getTitle();
        }
    }

    public List<TinySpace> getSpaces() {
        return this.spaces;
    }

    public int getTotalOwners() {
        return this.totalOwners;
    }

    public long getPageId() {
        return this.pageId;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public int getTotalSpaces() {
        return this.totalSpaces;
    }
}

