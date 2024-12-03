/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.internal;

import org.hibernate.boot.archive.scan.spi.MappingFileDescriptor;
import org.hibernate.boot.archive.spi.InputStreamAccess;

public class MappingFileDescriptorImpl
implements MappingFileDescriptor {
    private final String name;
    private final InputStreamAccess streamAccess;

    public MappingFileDescriptorImpl(String name, InputStreamAccess streamAccess) {
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
}

