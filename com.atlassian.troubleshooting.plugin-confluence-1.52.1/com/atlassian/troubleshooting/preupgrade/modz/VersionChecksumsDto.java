/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.preupgrade.modz;

import com.atlassian.troubleshooting.stp.spi.Version;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonProperty;

public class VersionChecksumsDto
implements Comparable<VersionChecksumsDto> {
    @JsonProperty
    private final String version;
    @JsonProperty
    private final List<String> checksums;

    public VersionChecksumsDto(@JsonProperty(value="version") String version, @JsonProperty(value="checksum") List<String> checksums) {
        this.version = Objects.requireNonNull(version);
        this.checksums = Objects.requireNonNull(checksums);
    }

    public String getVersion() {
        return this.version;
    }

    public List<String> getChecksums() {
        return this.checksums;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VersionChecksumsDto that = (VersionChecksumsDto)o;
        return Objects.equals(this.getVersion(), that.getVersion()) && Objects.equals(this.getChecksums(), that.getChecksums());
    }

    public int hashCode() {
        return Objects.hash(this.getVersion(), this.getChecksums());
    }

    @Override
    public int compareTo(VersionChecksumsDto that) {
        return Version.of(this.getVersion()).compareTo(Version.of(that.getVersion()));
    }

    public String toString() {
        return String.format("VersionChecksumsDto{version='%s', checksums=%s}", this.version, this.checksums);
    }
}

