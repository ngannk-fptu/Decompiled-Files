/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import java.io.Serializable;

public class EditPageBean
implements Serializable {
    String title;
    String label;
    String content;
    String parentPageTitle;
    String spaceKey;
    String versionComment;
    boolean moveHierarchy;
    private String viewPermissionsGroups;
    private String viewPermissionsUsers;
    private String editPermissionsGroups;
    private String editPermissionsUsers;
    private String position;
    private long targetId;
    private long draftId;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentPageTitle() {
        return this.parentPageTitle;
    }

    public void setParentPageTitle(String parentPageTitle) {
        this.parentPageTitle = parentPageTitle;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public boolean isMoveHierarchy() {
        return this.moveHierarchy;
    }

    public void setMoveHierarchy(boolean moveHierarchy) {
        this.moveHierarchy = moveHierarchy;
    }

    public String getLabels() {
        return this.label;
    }

    public void setLabels(String label) {
        this.label = label;
    }

    public String getVersionComment() {
        return this.versionComment;
    }

    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }

    public String getViewPermissionsGroups() {
        return this.viewPermissionsGroups;
    }

    public void setViewPermissionsGroups(String viewPermissionsGroups) {
        this.viewPermissionsGroups = viewPermissionsGroups;
    }

    public String getViewPermissionsUsers() {
        return this.viewPermissionsUsers;
    }

    public void setViewPermissionsUsers(String viewPermissionsUsers) {
        this.viewPermissionsUsers = viewPermissionsUsers;
    }

    public String getEditPermissionsGroups() {
        return this.editPermissionsGroups;
    }

    public void setEditPermissionsGroups(String editPermissionsGroups) {
        this.editPermissionsGroups = editPermissionsGroups;
    }

    public String getEditPermissionsUsers() {
        return this.editPermissionsUsers;
    }

    public void setEditPermissionsUsers(String editPermissionsUsers) {
        this.editPermissionsUsers = editPermissionsUsers;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public String getPosition() {
        return this.position;
    }

    public long getTargetId() {
        return this.targetId;
    }

    public long getDraftId() {
        return this.draftId;
    }

    public void setDraftId(long draftId) {
        this.draftId = draftId;
    }
}

