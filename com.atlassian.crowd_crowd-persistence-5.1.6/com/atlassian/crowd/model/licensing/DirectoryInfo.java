/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.model.licensing;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.licensing.LicensingSummary;
import java.util.Objects;

public class DirectoryInfo {
    private Long id;
    private String name;
    private Directory localDirectory;
    private LicensingSummary licensingSummary;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Directory getLocalDirectory() {
        return this.localDirectory;
    }

    public void setLocalDirectory(Directory localDirectory) {
        this.localDirectory = localDirectory;
    }

    public LicensingSummary getLicensingSummary() {
        return this.licensingSummary;
    }

    public void setLicensingSummary(LicensingSummary licensingSummary) {
        this.licensingSummary = licensingSummary;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectoryInfo that = (DirectoryInfo)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) && Objects.equals(this.localDirectory, that.localDirectory) && Objects.equals(this.licensingSummary, that.licensingSummary);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.name, this.localDirectory, this.licensingSummary);
    }
}

