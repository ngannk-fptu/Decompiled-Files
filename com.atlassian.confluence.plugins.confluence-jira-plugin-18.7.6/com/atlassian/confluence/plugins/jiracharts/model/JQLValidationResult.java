/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.jiracharts.model;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class JQLValidationResult {
    private List<String> errorMgs;
    private String authUrl;
    private String filterUrl;
    private int issueCount;
    private String displayUrl;
    private String rpcUrl;

    public List<String> getErrorMgs() {
        return this.errorMgs;
    }

    public JQLValidationResult() {
        this.setAuthUrl("");
        this.setErrorMgs(Collections.EMPTY_LIST);
    }

    public void setErrorMgs(List<String> errorMgs) {
        if (errorMgs == null) {
            errorMgs = Collections.emptyList();
        }
        this.errorMgs = errorMgs;
    }

    public String getAuthUrl() {
        return this.authUrl;
    }

    public void setAuthUrl(String oAuthUrl) {
        this.authUrl = oAuthUrl;
    }

    public boolean isValidJQL() {
        return this.getErrorMgs().size() == 0;
    }

    public boolean isOAuthNeeded() {
        return !StringUtils.isBlank((CharSequence)this.getAuthUrl());
    }

    public int getIssueCount() {
        return this.issueCount;
    }

    public void setIssueCount(int issueCount) {
        this.issueCount = issueCount;
    }

    public String getFilterUrl() {
        return this.filterUrl;
    }

    public void setFilterUrl(String filterUrl) {
        this.filterUrl = filterUrl;
    }

    public String getDisplayUrl() {
        return this.displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getRpcUrl() {
        return this.rpcUrl;
    }

    public void setRpcUrl(String rpcUrl) {
        this.rpcUrl = rpcUrl;
    }
}

