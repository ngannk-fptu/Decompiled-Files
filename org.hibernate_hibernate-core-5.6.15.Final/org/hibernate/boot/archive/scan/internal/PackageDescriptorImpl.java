/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.internal;

import org.hibernate.boot.archive.scan.spi.PackageDescriptor;
import org.hibernate.boot.archive.spi.InputStreamAccess;

public class PackageDescriptorImpl
implements PackageDescriptor {
    private final String name;
    private final InputStreamAccess streamAccess;

    public PackageDescriptorImpl(String name, InputStreamAccess streamAccess) {
        this.name = name;
        this.streamAccess = streamAccess;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public InputStreamAccess getStreamAccess() {
        return this.streamAccess;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PackageDescriptorImpl that = (PackageDescriptorImpl)o;
        return this.name.equals(that.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

