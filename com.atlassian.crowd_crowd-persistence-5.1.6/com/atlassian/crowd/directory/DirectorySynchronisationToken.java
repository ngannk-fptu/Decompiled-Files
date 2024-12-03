/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.Directory;
import java.util.Objects;

public class DirectorySynchronisationToken {
    private Long directoryId;
    private Directory directory;
    private String synchronisationToken;

    public DirectorySynchronisationToken() {
    }

    public DirectorySynchronisationToken(Directory directory, String synchronisationToken) {
        this.directory = directory;
        this.synchronisationToken = synchronisationToken;
    }

    public Long getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public Directory getDirectory() {
        return this.directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public String getSynchronisationToken() {
        return this.synchronisationToken;
    }

    public void setSynchronisationToken(String synchronisationToken) {
        this.synchronisationToken = synchronisationToken;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectorySynchronisationToken that = (DirectorySynchronisationToken)o;
        return Objects.equals(this.directoryId, that.directoryId) && Objects.equals(this.directory, that.directory) && Objects.equals(this.synchronisationToken, that.synchronisationToken);
    }

    public int hashCode() {
        return Objects.hash(this.directoryId, this.directory, this.synchronisationToken);
    }
}

