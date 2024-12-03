/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.healthcheck.checks.vuln.model;

import com.atlassian.troubleshooting.stp.spi.Version;
import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CpeMatch {
    private String versionStartIncluding;
    private String versionEndExcluding;

    @JsonCreator
    public CpeMatch(@JsonProperty(value="versionStartIncluding") String versionStartIncluding, @JsonProperty(value="versionEndExcluding") String versionEndExcluding) {
        this.versionStartIncluding = versionStartIncluding;
        this.versionEndExcluding = versionEndExcluding;
    }

    public String getVersionStartIncluding() {
        return this.versionStartIncluding;
    }

    public String getVersionEndExcluding() {
        return this.versionEndExcluding;
    }

    public boolean matchesVersion(Version version) {
        Objects.requireNonNull(version);
        if (this.versionStartIncluding != null && version.compareTo(Version.of(this.versionStartIncluding)) < 0) {
            return false;
        }
        return this.versionEndExcluding == null || version.compareTo(Version.of(this.versionEndExcluding)) < 0;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.MULTI_LINE_STYLE);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CpeMatch cpeMatch = (CpeMatch)o;
        return new EqualsBuilder().append((Object)this.versionStartIncluding, (Object)cpeMatch.versionStartIncluding).append((Object)this.versionEndExcluding, (Object)cpeMatch.versionEndExcluding).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.versionStartIncluding).append((Object)this.versionEndExcluding).toHashCode();
    }
}

