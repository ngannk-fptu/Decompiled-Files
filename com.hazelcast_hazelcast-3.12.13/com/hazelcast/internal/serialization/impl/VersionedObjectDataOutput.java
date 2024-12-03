/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.VersionAware;
import com.hazelcast.version.Version;
import java.io.OutputStream;

abstract class VersionedObjectDataOutput
extends OutputStream
implements ObjectDataOutput,
VersionAware {
    protected Version version = Version.UNKNOWN;

    VersionedObjectDataOutput() {
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public Version getVersion() {
        return this.version;
    }
}

