/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugin.copyspace.entity;

import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class CopySpaceProgressBarData {
    public static final CopySpaceProgressBarData NO_OPERATION_IN_PROGRESS = new CopySpaceProgressBarData("", "", "", "", false, false);
    @JsonProperty(value="originalSpaceKey")
    private String originalSpaceKey;
    @JsonProperty(value="longRunningTaskId")
    private String longRunningTaskId;
    @JsonProperty(value="targetSpaceKey")
    private String targetSpaceKey;
    @JsonProperty(value="targetSpaceName")
    private String targetSpaceName;
    private boolean isCopyPages;
    private boolean isCopyBlogPosts;

    public CopySpaceProgressBarData() {
    }

    public CopySpaceProgressBarData(String originalSpaceKey, String longRunningTaskId, String targetSpaceKey, String targetSpaceName, boolean isCopyPages, boolean isCopyBlogPosts) {
        this.originalSpaceKey = originalSpaceKey;
        this.longRunningTaskId = longRunningTaskId;
        this.targetSpaceKey = targetSpaceKey;
        this.targetSpaceName = targetSpaceName;
        this.isCopyPages = isCopyPages;
        this.isCopyBlogPosts = isCopyBlogPosts;
    }

    public String getOriginalSpaceKey() {
        return this.originalSpaceKey;
    }

    public void setOriginalSpaceKey(String originalSpaceKey) {
        this.originalSpaceKey = originalSpaceKey;
    }

    public String getLongRunningTaskId() {
        return this.longRunningTaskId;
    }

    public void setLongRunningTaskId(String longRunningTaskId) {
        this.longRunningTaskId = longRunningTaskId;
    }

    public String getTargetSpaceKey() {
        return this.targetSpaceKey;
    }

    public void setTargetSpaceKey(String targetSpaceKey) {
        this.targetSpaceKey = targetSpaceKey;
    }

    public String getTargetSpaceName() {
        return this.targetSpaceName;
    }

    public void setTargetSpaceName(String targetSpaceName) {
        this.targetSpaceName = targetSpaceName;
    }

    public boolean isCopyPages() {
        return this.isCopyPages;
    }

    public void setCopyPages(boolean copyPages) {
        this.isCopyPages = copyPages;
    }

    public boolean isCopyBlogPosts() {
        return this.isCopyBlogPosts;
    }

    public void setCopyBlogPosts(boolean copyBlogPosts) {
        this.isCopyBlogPosts = copyBlogPosts;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CopySpaceProgressBarData that = (CopySpaceProgressBarData)o;
        return this.isCopyPages() == that.isCopyPages() && this.isCopyBlogPosts() == that.isCopyBlogPosts() && this.getOriginalSpaceKey().equals(that.getOriginalSpaceKey()) && this.getLongRunningTaskId().equals(that.getLongRunningTaskId()) && this.getTargetSpaceKey().equals(that.getTargetSpaceKey()) && this.getTargetSpaceName().equals(that.getTargetSpaceName());
    }

    public int hashCode() {
        return Objects.hash(this.getOriginalSpaceKey(), this.getLongRunningTaskId(), this.getTargetSpaceKey(), this.getTargetSpaceName(), this.isCopyPages(), this.isCopyBlogPosts());
    }
}

