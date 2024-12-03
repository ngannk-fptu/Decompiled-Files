/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildNumber
implements Comparable<BuildNumber> {
    private static final Logger log = LoggerFactory.getLogger(BuildNumber.class);
    protected final String buildNumber;

    public BuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public BuildNumber(Integer buildNumber) {
        this.buildNumber = buildNumber != null ? buildNumber.toString() : null;
    }

    public boolean isLowerThan(BuildNumber other) {
        return this.toInteger() < other.toInteger();
    }

    @Override
    public int compareTo(BuildNumber that) {
        return this.toInteger() - that.toInteger();
    }

    public int hashCode() {
        return this.buildNumber.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !this.getClass().equals(obj.getClass())) {
            return false;
        }
        BuildNumber that = (BuildNumber)obj;
        return this.compareTo(that) == 0;
    }

    public String toString() {
        return this.buildNumber;
    }

    private int toInteger() throws NumberFormatException {
        if (this.buildNumber == null) {
            return 0;
        }
        return Integer.parseInt(this.buildNumber);
    }
}

