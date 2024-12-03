/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.Version
 */
package com.atlassian.hazelcast.serialization;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

class BundleKey
implements Serializable,
Comparable<BundleKey> {
    private final String symbolicName;
    private final int majorVersion;
    private final int minorVersion;
    private final int microVersion;

    public BundleKey(Bundle bundle) {
        this(bundle.getSymbolicName(), bundle.getVersion());
    }

    public BundleKey(String symbolicName, Version version) {
        this.symbolicName = symbolicName;
        this.majorVersion = version.getMajor();
        this.minorVersion = version.getMinor();
        this.microVersion = version.getMicro();
    }

    @Override
    public int compareTo(@Nonnull BundleKey other) {
        int result = this.symbolicName.compareTo(((BundleKey)Preconditions.checkNotNull((Object)other, (Object)"other")).symbolicName);
        if (result != 0) {
            return result;
        }
        result = this.majorVersion - other.majorVersion;
        if (result != 0) {
            return result;
        }
        result = this.minorVersion - other.minorVersion;
        if (result != 0) {
            return result;
        }
        return this.microVersion - other.microVersion;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BundleKey that = (BundleKey)o;
        return this.symbolicName.equals(that.symbolicName) && this.majorVersion == that.majorVersion && this.minorVersion == that.minorVersion && this.microVersion == that.microVersion;
    }

    public String getSymbolicName() {
        return this.symbolicName;
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public int getMicroVersion() {
        return this.microVersion;
    }

    public int hashCode() {
        int result = this.symbolicName.hashCode();
        result = 31 * result + this.majorVersion;
        result = 31 * result + this.minorVersion;
        result = 31 * result + this.microVersion;
        return result;
    }

    public String toString() {
        return this.symbolicName + ":" + this.majorVersion + "." + this.minorVersion + "." + this.microVersion;
    }
}

