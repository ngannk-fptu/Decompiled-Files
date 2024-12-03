/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;

public class MultipartDef
implements Serializable {
    private static final long serialVersionUID = 1L;
    private String location;
    private String maxFileSize;
    private String maxRequestSize;
    private String fileSizeThreshold;

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMaxFileSize() {
        return this.maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getMaxRequestSize() {
        return this.maxRequestSize;
    }

    public void setMaxRequestSize(String maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public String getFileSizeThreshold() {
        return this.fileSizeThreshold;
    }

    public void setFileSizeThreshold(String fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.fileSizeThreshold == null ? 0 : this.fileSizeThreshold.hashCode());
        result = 31 * result + (this.location == null ? 0 : this.location.hashCode());
        result = 31 * result + (this.maxFileSize == null ? 0 : this.maxFileSize.hashCode());
        result = 31 * result + (this.maxRequestSize == null ? 0 : this.maxRequestSize.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MultipartDef)) {
            return false;
        }
        MultipartDef other = (MultipartDef)obj;
        if (this.fileSizeThreshold == null ? other.fileSizeThreshold != null : !this.fileSizeThreshold.equals(other.fileSizeThreshold)) {
            return false;
        }
        if (this.location == null ? other.location != null : !this.location.equals(other.location)) {
            return false;
        }
        if (this.maxFileSize == null ? other.maxFileSize != null : !this.maxFileSize.equals(other.maxFileSize)) {
            return false;
        }
        return !(this.maxRequestSize == null ? other.maxRequestSize != null : !this.maxRequestSize.equals(other.maxRequestSize));
    }
}

