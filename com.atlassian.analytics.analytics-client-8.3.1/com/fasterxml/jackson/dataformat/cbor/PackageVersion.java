/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.dataformat.cbor;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

public final class PackageVersion
implements Versioned {
    public static final Version VERSION = VersionUtil.parseVersion("2.13.4", "com.fasterxml.jackson.dataformat", "jackson-dataformat-cbor");

    @Override
    public Version version() {
        return VERSION;
    }
}

