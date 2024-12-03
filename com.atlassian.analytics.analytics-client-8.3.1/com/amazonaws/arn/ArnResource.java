/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.arn;

import com.amazonaws.util.ValidationUtils;

public class ArnResource {
    private final String resourceType;
    private final String resource;
    private final String qualifier;

    private ArnResource(Builder b) {
        this.resourceType = b.resourceType;
        this.resource = ValidationUtils.assertStringNotEmpty(b.resource, "resource");
        this.qualifier = b.qualifier;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public String getResource() {
        return this.resource;
    }

    public String getQualifier() {
        return this.qualifier;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ArnResource fromString(String resource) {
        char ch;
        int i;
        Integer resourceTypeBoundary = null;
        Integer resourceIdBoundary = null;
        for (i = 0; i < resource.length(); ++i) {
            ch = resource.charAt(i);
            if (ch != ':' && ch != '/') continue;
            resourceTypeBoundary = i;
            break;
        }
        if (resourceTypeBoundary != null) {
            for (i = resource.length() - 1; i > resourceTypeBoundary; --i) {
                ch = resource.charAt(i);
                if (ch != ':') continue;
                resourceIdBoundary = i;
                break;
            }
        }
        if (resourceTypeBoundary == null) {
            return ArnResource.builder().withResource(resource).build();
        }
        if (resourceIdBoundary == null) {
            String resourceType = resource.substring(0, resourceTypeBoundary);
            String resourceId = resource.substring(resourceTypeBoundary + 1);
            return ArnResource.builder().withResourceType(resourceType).withResource(resourceId).build();
        }
        String resourceType = resource.substring(0, resourceTypeBoundary);
        String resourceId = resource.substring(resourceTypeBoundary + 1, resourceIdBoundary);
        String qualifier = resource.substring(resourceIdBoundary + 1);
        return ArnResource.builder().withResourceType(resourceType).withResource(resourceId).withQualifier(qualifier).build();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.resourceType != null) {
            sb.append(this.resourceType);
            sb.append(":");
        }
        sb.append(this.resource);
        if (this.qualifier != null) {
            sb.append(":");
            sb.append(this.qualifier);
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ArnResource that = (ArnResource)o;
        if (this.resourceType != null ? !this.resourceType.equals(that.resourceType) : that.resourceType != null) {
            return false;
        }
        if (!this.resource.equals(that.resource)) {
            return false;
        }
        return this.qualifier != null ? this.qualifier.equals(that.qualifier) : that.qualifier == null;
    }

    public int hashCode() {
        int result = this.resourceType != null ? this.resourceType.hashCode() : 0;
        result = 31 * result + this.resource.hashCode();
        result = 31 * result + (this.qualifier != null ? this.qualifier.hashCode() : 0);
        return result;
    }

    public static final class Builder {
        private String resourceType;
        private String resource;
        private String qualifier;

        private Builder() {
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public Builder withResourceType(String resourceType) {
            this.setResourceType(resourceType);
            return this;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }

        public Builder withResource(String resource) {
            this.setResource(resource);
            return this;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        public Builder withQualifier(String qualifier) {
            this.setQualifier(qualifier);
            return this;
        }

        public ArnResource build() {
            return new ArnResource(this);
        }
    }
}

