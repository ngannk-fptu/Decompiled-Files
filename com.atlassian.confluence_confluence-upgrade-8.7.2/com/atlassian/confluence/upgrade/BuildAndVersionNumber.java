/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.BuildNumber;
import java.util.Objects;

public class BuildAndVersionNumber
extends BuildNumber {
    private final String version;

    public BuildAndVersionNumber(Integer buildNumber, String version) {
        super(buildNumber);
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    @Override
    public String toString() {
        return this.buildNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        BuildAndVersionNumber that = (BuildAndVersionNumber)o;
        return Objects.equals(this.version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.version);
    }
}

