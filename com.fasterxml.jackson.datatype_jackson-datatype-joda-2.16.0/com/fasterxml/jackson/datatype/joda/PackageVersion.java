/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.Version
 *  com.fasterxml.jackson.core.Versioned
 *  com.fasterxml.jackson.core.util.VersionUtil
 */
package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

public final class PackageVersion
implements Versioned {
    public static final Version VERSION = VersionUtil.parseVersion((String)"2.16.0", (String)"com.fasterxml.jackson.datatype", (String)"jackson-datatype-joda");

    public Version version() {
        return VERSION;
    }
}

