/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import java.io.Serializable;

public class UsageInfoDTO
implements Serializable {
    private final int totalSpaces;
    private final int globalSpaces;
    private final int personalSpaces;
    private final int allContent;
    private final int currentContent;
    private final int localUsers;
    private final int localGroups;

    public UsageInfoDTO(int totalSpaces, int globalSpaces, int personalSpaces, int allContent, int currentContent, int localUsers, int localGroups) {
        this.totalSpaces = totalSpaces;
        this.globalSpaces = globalSpaces;
        this.personalSpaces = personalSpaces;
        this.allContent = allContent;
        this.currentContent = currentContent;
        this.localUsers = localUsers;
        this.localGroups = localGroups;
    }

    public int getTotalSpaces() {
        return this.totalSpaces;
    }

    public int getGlobalSpaces() {
        return this.globalSpaces;
    }

    public int getPersonalSpaces() {
        return this.personalSpaces;
    }

    public int getAllContent() {
        return this.allContent;
    }

    public int getCurrentContent() {
        return this.currentContent;
    }

    public int getLocalUsers() {
        return this.localUsers;
    }

    public int getLocalGroups() {
        return this.localGroups;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        UsageInfoDTO that = (UsageInfoDTO)o;
        if (this.totalSpaces != that.totalSpaces) {
            return false;
        }
        if (this.globalSpaces != that.globalSpaces) {
            return false;
        }
        if (this.personalSpaces != that.personalSpaces) {
            return false;
        }
        if (this.allContent != that.allContent) {
            return false;
        }
        if (this.currentContent != that.currentContent) {
            return false;
        }
        if (this.localUsers != that.localUsers) {
            return false;
        }
        return this.localGroups == that.localGroups;
    }

    public int hashCode() {
        int result = this.totalSpaces;
        result = 31 * result + this.globalSpaces;
        result = 31 * result + this.personalSpaces;
        result = 31 * result + this.allContent;
        result = 31 * result + this.currentContent;
        result = 31 * result + this.localUsers;
        result = 31 * result + this.localGroups;
        return result;
    }
}

