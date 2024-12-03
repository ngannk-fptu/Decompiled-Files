/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.troubleshooting.api.supportzip;

import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import java.io.File;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FileSupportZipArtifact
implements SupportZipBundle.Artifact {
    private final String targetPath;
    private final File file;

    public FileSupportZipArtifact(File file) {
        this(file, "");
    }

    public FileSupportZipArtifact(File file, String targetPath) {
        this.targetPath = targetPath;
        this.file = file;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public String getTargetPath() {
        return this.targetPath;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("file", (Object)this.file).append("targetPath", (Object)this.targetPath).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        SupportZipBundle.Artifact that = (SupportZipBundle.Artifact)o;
        return new EqualsBuilder().append((Object)this.file, (Object)that.getFile()).append((Object)this.targetPath, (Object)that.getTargetPath()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.file).append((Object)this.targetPath).toHashCode();
    }
}

