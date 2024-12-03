/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.oracle.webservices.api.databinding;

import com.sun.xml.ws.api.databinding.MetadataReader;
import com.sun.xml.ws.model.ExternalMetadataReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

public class ExternalMetadataFeature
extends WebServiceFeature {
    private static final String ID = "com.oracle.webservices.api.databinding.ExternalMetadataFeature";
    private boolean enabled = true;
    private List<String> resourceNames;
    private List<File> files;
    private MetadataReader reader;

    private ExternalMetadataFeature() {
    }

    public void addResources(String ... resourceNames) {
        if (this.resourceNames == null) {
            this.resourceNames = new ArrayList<String>();
        }
        Collections.addAll(this.resourceNames, resourceNames);
    }

    public List<String> getResourceNames() {
        return this.resourceNames;
    }

    public void addFiles(File ... files) {
        if (this.files == null) {
            this.files = new ArrayList<File>();
        }
        Collections.addAll(this.files, files);
    }

    public List<File> getFiles() {
        return this.files;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    private void setEnabled(boolean x) {
        this.enabled = x;
    }

    public String getID() {
        return ID;
    }

    public MetadataReader getMetadataReader(ClassLoader classLoader, boolean disableXmlSecurity) {
        if (this.reader != null && this.enabled) {
            return this.reader;
        }
        return this.enabled ? new ExternalMetadataReader(this.files, this.resourceNames, classLoader, true, disableXmlSecurity) : null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        ExternalMetadataFeature that = (ExternalMetadataFeature)((Object)o);
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.files != null ? !this.files.equals(that.files) : that.files != null) {
            return false;
        }
        return !(this.resourceNames != null ? !this.resourceNames.equals(that.resourceNames) : that.resourceNames != null);
    }

    public int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.resourceNames != null ? this.resourceNames.hashCode() : 0);
        result = 31 * result + (this.files != null ? this.files.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "[" + this.getID() + ", enabled=" + this.enabled + ", resourceNames=" + this.resourceNames + ", files=" + this.files + ']';
    }

    public static Builder builder() {
        return new Builder(new ExternalMetadataFeature());
    }

    public static final class Builder {
        private final ExternalMetadataFeature o;

        Builder(ExternalMetadataFeature x) {
            this.o = x;
        }

        public ExternalMetadataFeature build() {
            return this.o;
        }

        public Builder addResources(String ... res) {
            this.o.addResources(res);
            return this;
        }

        public Builder addFiles(File ... files) {
            this.o.addFiles(files);
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.o.setEnabled(enabled);
            return this;
        }

        public Builder setReader(MetadataReader r) {
            this.o.reader = r;
            return this;
        }
    }
}

