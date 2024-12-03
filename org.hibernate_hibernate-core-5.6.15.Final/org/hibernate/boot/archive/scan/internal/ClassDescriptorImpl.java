/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.internal;

import org.hibernate.boot.archive.scan.spi.ClassDescriptor;
import org.hibernate.boot.archive.spi.InputStreamAccess;

public class ClassDescriptorImpl
implements ClassDescriptor {
    private final String name;
    private final ClassDescriptor.Categorization categorization;
    private final InputStreamAccess streamAccess;

    public ClassDescriptorImpl(String name, ClassDescriptor.Categorization categorization, InputStreamAccess streamAccess) {
        this.name = name;
        this.categorization = categorization;
        this.streamAccess = streamAccess;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ClassDescriptor.Categorization getCategorization() {
        return this.categorization;
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
        ClassDescriptorImpl that = (ClassDescriptorImpl)o;
        return this.name.equals(that.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}

