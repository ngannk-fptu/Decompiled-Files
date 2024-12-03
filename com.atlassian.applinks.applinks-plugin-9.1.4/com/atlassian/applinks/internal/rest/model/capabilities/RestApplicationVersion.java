/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.rest.model.capabilities;

import com.atlassian.applinks.internal.common.capabilities.ApplicationVersion;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.applinks.internal.rest.model.ReadOnlyRestRepresentation;
import java.util.Objects;
import javax.annotation.Nonnull;

public class RestApplicationVersion
extends BaseRestEntity
implements ReadOnlyRestRepresentation<ApplicationVersion> {
    public static final String VERSION_STRING = "versionString";
    public static final String MAJOR = "major";
    public static final String MINOR = "minor";
    public static final String BUGFIX = "bugfix";
    public static final String SUFFIX = "suffix";

    public RestApplicationVersion() {
    }

    public RestApplicationVersion(@Nonnull ApplicationVersion applicationVersion) {
        Objects.requireNonNull(applicationVersion, "applicationVersion");
        this.put(VERSION_STRING, (Object)applicationVersion.getVersionString());
        this.put(MAJOR, (Object)applicationVersion.getMajor());
        this.put(MINOR, (Object)applicationVersion.getMinor());
        this.put(BUGFIX, (Object)applicationVersion.getBugfix());
        this.put(SUFFIX, (Object)applicationVersion.getSuffix());
    }
}

