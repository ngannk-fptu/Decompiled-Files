/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.troubleshooting.healthcheck.checks.vuln.model;

import com.atlassian.troubleshooting.healthcheck.checks.vuln.model.Advisory;
import com.atlassian.troubleshooting.healthcheck.checks.vuln.model.CpeMatch;
import com.atlassian.troubleshooting.stp.spi.Version;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CveRecord {
    private Advisory advisory;
    private String cveId;
    private String description;
    private double baseScore;
    private List<CpeMatch> cpeMatches = new ArrayList<CpeMatch>();

    public String getCveId() {
        return this.cveId;
    }

    @Nonnull
    public Advisory getAdvisory() {
        return this.advisory;
    }

    public void setAdvisory(Advisory advisory) {
        this.advisory = advisory;
    }

    public void setCveId(String cveId) {
        this.cveId = cveId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBaseScore() {
        return this.baseScore;
    }

    public void setBaseScore(double baseScore) {
        this.baseScore = baseScore;
    }

    public List<CpeMatch> getCpeMatches() {
        return this.cpeMatches;
    }

    public void addMatch(CpeMatch match) {
        this.cpeMatches.add(match);
    }

    public boolean matchesVersion(Version version) {
        return this.cpeMatches.stream().anyMatch(m -> m.matchesVersion(version));
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
        CveRecord cveRecord = (CveRecord)o;
        return new EqualsBuilder().append(this.baseScore, cveRecord.baseScore).append((Object)this.advisory, (Object)cveRecord.advisory).append((Object)this.cveId, (Object)cveRecord.cveId).append((Object)this.description, (Object)cveRecord.description).append(this.cpeMatches, cveRecord.cpeMatches).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append((Object)this.advisory).append((Object)this.cveId).append((Object)this.description).append(this.baseScore).append(this.cpeMatches).toHashCode();
    }
}

