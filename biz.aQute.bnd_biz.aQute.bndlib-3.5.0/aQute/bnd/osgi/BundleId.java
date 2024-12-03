/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.osgi.Verifier;

public class BundleId
implements Comparable<BundleId> {
    final String bsn;
    final String version;

    public BundleId(String bsn, String version) {
        this.bsn = bsn.trim();
        this.version = version.trim();
    }

    public String getVersion() {
        return this.version;
    }

    public String getBsn() {
        return this.bsn;
    }

    public boolean isValid() {
        return Verifier.isVersion(this.version) && Verifier.isBsn(this.bsn);
    }

    public boolean equals(Object o) {
        return this == o || o instanceof BundleId && this.compareTo((BundleId)o) == 0;
    }

    public int hashCode() {
        return this.bsn.hashCode() ^ this.version.hashCode();
    }

    @Override
    public int compareTo(BundleId other) {
        int result = this.bsn.compareTo(other.bsn);
        if (result != 0) {
            return result;
        }
        return this.version.compareTo(other.version);
    }
}

