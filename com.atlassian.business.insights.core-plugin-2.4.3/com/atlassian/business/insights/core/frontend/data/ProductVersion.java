/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.business.insights.core.frontend.data;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class ProductVersion
implements Comparable<ProductVersion> {
    public static final String NON_NUMERIC_CHARS = "\\D+";
    private int majorVersion = 0;
    private int minorVersion = 0;
    private int patchVersion = 0;

    public ProductVersion(@Nonnull String versionString) {
        Objects.requireNonNull(versionString);
        List versions = Arrays.stream(versionString.split(NON_NUMERIC_CHARS)).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        if (versions.size() >= 3) {
            this.majorVersion = Integer.parseInt((String)versions.get(0));
            this.minorVersion = Integer.parseInt((String)versions.get(1));
            this.patchVersion = Integer.parseInt((String)versions.get(2));
        } else if (versions.size() == 2) {
            this.majorVersion = Integer.parseInt((String)versions.get(0));
            this.minorVersion = Integer.parseInt((String)versions.get(1));
        } else if (versions.size() == 1) {
            this.majorVersion = Integer.parseInt((String)versions.get(0));
        }
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public int getPatchVersion() {
        return this.patchVersion;
    }

    @Override
    public int compareTo(ProductVersion that) {
        if (this.getMajorVersion() != that.getMajorVersion()) {
            return this.getMajorVersion() - that.getMajorVersion();
        }
        if (this.getMinorVersion() != that.getMinorVersion()) {
            return this.getMinorVersion() - that.getMinorVersion();
        }
        return this.getPatchVersion() - that.getPatchVersion();
    }

    public boolean equals(Object o) {
        if (o instanceof ProductVersion) {
            return ((ProductVersion)o).getMajorVersion() == this.getMajorVersion() && ((ProductVersion)o).getMinorVersion() == this.getMinorVersion() && ((ProductVersion)o).getPatchVersion() == this.getPatchVersion();
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.majorVersion, this.minorVersion, this.patchVersion);
    }
}

