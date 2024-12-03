/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.descriptor.TaglibDescriptor
 */
package org.apache.tomcat.util.descriptor.web;

import javax.servlet.descriptor.TaglibDescriptor;

public class TaglibDescriptorImpl
implements TaglibDescriptor {
    private final String location;
    private final String uri;

    public TaglibDescriptorImpl(String location, String uri) {
        this.location = location;
        this.uri = uri;
    }

    public String getTaglibLocation() {
        return this.location;
    }

    public String getTaglibURI() {
        return this.uri;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.location == null ? 0 : this.location.hashCode());
        result = 31 * result + (this.uri == null ? 0 : this.uri.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TaglibDescriptorImpl)) {
            return false;
        }
        TaglibDescriptorImpl other = (TaglibDescriptorImpl)obj;
        if (this.location == null ? other.location != null : !this.location.equals(other.location)) {
            return false;
        }
        return !(this.uri == null ? other.uri != null : !this.uri.equals(other.uri));
    }
}

