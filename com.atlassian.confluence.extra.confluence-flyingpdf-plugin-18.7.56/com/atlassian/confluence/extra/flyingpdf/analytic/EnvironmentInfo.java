/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.analytic;

import com.atlassian.confluence.extra.flyingpdf.analytic.ExportScope;
import com.atlassian.confluence.extra.flyingpdf.analytic.SandboxStatus;

public class EnvironmentInfo {
    private int dcNodeId = -1;
    private SandboxStatus sandboxStatus;
    private ExportScope exportScope;
    private String spaceKey;
    private String spaceName;
    private String pageType;
    private String pageName;

    int getDcNodeId() {
        return this.dcNodeId;
    }

    public void setDcNodeId(int dcNodeId) {
        this.dcNodeId = dcNodeId;
    }

    public SandboxStatus getSandboxStatus() {
        return this.sandboxStatus;
    }

    public void setSandboxStatus(SandboxStatus sandboxStatus) {
        this.sandboxStatus = sandboxStatus;
    }

    public ExportScope getExportScope() {
        return this.exportScope;
    }

    public void setExportScope(ExportScope exportScope) {
        this.exportScope = exportScope;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getPageType() {
        return this.pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getPageName() {
        return this.pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }
}

