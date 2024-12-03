/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugin.descriptor.aboutpage;

import org.apache.commons.lang3.StringUtils;

public final class Material
implements Comparable<Material> {
    private final String libraryName;
    private final String mavenInfo;
    private final String license;
    private final String url;
    private final String artifactType;

    public Material(String libraryName, String mavenInfo, String license, String url, String artifactType) {
        this.libraryName = libraryName;
        this.mavenInfo = mavenInfo;
        this.license = license;
        this.url = url;
        this.artifactType = artifactType;
    }

    public String getLibraryName() {
        return this.libraryName == null ? "" : this.libraryName;
    }

    public String getMavenInfo() {
        return this.mavenInfo == null ? "" : this.mavenInfo;
    }

    public String getLicense() {
        return this.license == null ? "" : this.license;
    }

    public String getUrl() {
        return this.url == null ? "" : this.url;
    }

    public String getArtifactType() {
        return this.artifactType == null ? "" : this.artifactType;
    }

    public boolean isUrlAndGav() {
        return !StringUtils.isEmpty((CharSequence)this.mavenInfo) && !StringUtils.isEmpty((CharSequence)this.url);
    }

    public boolean isUrlNotGav() {
        return StringUtils.isEmpty((CharSequence)this.mavenInfo) && !StringUtils.isEmpty((CharSequence)this.url);
    }

    public boolean isGavNotUrl() {
        return !StringUtils.isEmpty((CharSequence)this.mavenInfo) && StringUtils.isEmpty((CharSequence)this.url);
    }

    public String toString() {
        return this.getLibraryName() + "," + this.getMavenInfo() + "," + this.getLicense() + "," + this.getUrl() + "," + this.getArtifactType();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        Material material = (Material)other;
        if (this.artifactType != null ? !this.artifactType.equals(material.artifactType) : material.artifactType != null) {
            return false;
        }
        if (this.libraryName != null ? !this.libraryName.equals(material.libraryName) : material.libraryName != null) {
            return false;
        }
        if (this.license != null ? !this.license.equals(material.license) : material.license != null) {
            return false;
        }
        if (this.mavenInfo != null ? !this.mavenInfo.equals(material.mavenInfo) : material.mavenInfo != null) {
            return false;
        }
        return !(this.url != null ? !this.url.equals(material.url) : material.url != null);
    }

    public int hashCode() {
        int result = this.libraryName != null ? this.libraryName.hashCode() : 0;
        result = 31 * result + (this.mavenInfo != null ? this.mavenInfo.hashCode() : 0);
        result = 31 * result + (this.license != null ? this.license.hashCode() : 0);
        result = 31 * result + (this.url != null ? this.url.hashCode() : 0);
        result = 31 * result + (this.artifactType != null ? this.artifactType.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Material material) {
        int result = this.getLibraryName().compareTo(material.getLibraryName());
        if (result != 0) {
            return result;
        }
        result = this.getMavenInfo().compareTo(material.getMavenInfo());
        if (result != 0) {
            return result;
        }
        result = this.getLicense().compareTo(material.getLicense());
        if (result != 0) {
            return result;
        }
        result = this.getUrl().compareTo(material.getUrl());
        if (result != 0) {
            return result;
        }
        return this.getArtifactType().compareTo(material.getArtifactType());
    }
}

