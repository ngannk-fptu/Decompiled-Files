/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.zdu.event;

import java.io.Serializable;
import java.util.Objects;
import javax.annotation.Nonnull;

public class BuildInfo
implements Serializable {
    private static final long serialVersionUID = -4770649382568962117L;
    private final String version;

    public BuildInfo(@Nonnull String version) {
        this.version = Objects.requireNonNull(version);
    }

    @Nonnull
    public String getVersion() {
        return this.version;
    }
}

