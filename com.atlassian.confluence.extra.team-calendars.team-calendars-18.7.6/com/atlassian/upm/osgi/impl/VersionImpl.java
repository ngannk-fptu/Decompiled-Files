/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package com.atlassian.upm.osgi.impl;

import com.atlassian.upm.osgi.Version;
import org.apache.commons.lang.StringUtils;

public final class VersionImpl
implements Version {
    private final int major;
    private final int minor;
    private final int micro;
    private final String qualifier;

    VersionImpl(org.osgi.framework.Version version) {
        this.major = version.getMajor();
        this.minor = version.getMinor();
        this.micro = version.getMicro();
        this.qualifier = version.getQualifier();
    }

    @Override
    public int getMajor() {
        return this.major;
    }

    @Override
    public int getMinor() {
        return this.minor;
    }

    @Override
    public int getMicro() {
        return this.micro;
    }

    @Override
    public String getQualifier() {
        return this.qualifier;
    }

    @Override
    public int compareTo(Version version) {
        if (this == version) {
            return 0;
        }
        int result = this.getMajor() - version.getMajor();
        if (result != 0) {
            return result;
        }
        result = this.getMinor() - version.getMinor();
        if (result != 0) {
            return result;
        }
        result = this.getMicro() - version.getMicro();
        if (result != 0) {
            return result;
        }
        String otherQualifier = version.getQualifier();
        if (StringUtils.isEmpty(this.getQualifier()) && StringUtils.isNotEmpty(otherQualifier)) {
            return VersionImpl.compareQualifierWithBlankQualifier(otherQualifier, false);
        }
        if (StringUtils.isNotEmpty(this.getQualifier()) && StringUtils.isEmpty(otherQualifier)) {
            return VersionImpl.compareQualifierWithBlankQualifier(this.getQualifier(), true);
        }
        return this.getQualifier().compareTo(version.getQualifier());
    }

    private static int compareQualifierWithBlankQualifier(String qualifier, boolean thisQualifier) {
        if (StringUtils.isNumeric(qualifier)) {
            return "".compareTo(qualifier) * (thisQualifier ? -1 : 1);
        }
        return qualifier.compareTo("") * (thisQualifier ? -1 : 1);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof Version)) {
            return false;
        }
        return this.compareTo((Version)other) == 0;
    }

    public int hashCode() {
        return 31 * (31 * (31 * this.major + this.minor) + this.micro) + this.qualifier.hashCode();
    }

    public String toString() {
        String result = String.format("%d.%d.%d", this.major, this.minor, this.micro);
        if (this.qualifier.length() != 0) {
            result = result + "." + this.qualifier;
        }
        return result;
    }
}

