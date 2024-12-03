/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.version.Version;
import java.io.InputStream;

abstract class VersionedObjectDataInput
extends InputStream
implements ObjectDataInput {
    protected Version version = Version.UNKNOWN;

    VersionedObjectDataInput() {
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public Version getVersion() {
        return this.version;
    }
}

