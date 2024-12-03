/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services;

import com.atlassian.fugue.Option;
import javax.annotation.Nonnull;

public class SpacePermissionUpdateResult {
    private final boolean isSuccessful;
    private final Option<String> i18ErrorOpt;

    private SpacePermissionUpdateResult(boolean isSuccessful, Option<String> i18ErrorOpt) {
        this.isSuccessful = isSuccessful;
        this.i18ErrorOpt = i18ErrorOpt;
    }

    public static SpacePermissionUpdateResult error(@Nonnull String translatedErrorMessage) {
        return new SpacePermissionUpdateResult(false, (Option<String>)Option.some((Object)translatedErrorMessage));
    }

    public static SpacePermissionUpdateResult success() {
        return new SpacePermissionUpdateResult(true, (Option<String>)Option.none());
    }

    public boolean isSuccessful() {
        return this.isSuccessful;
    }

    public Option<String> getI18ErrorOpt() {
        return this.i18ErrorOpt;
    }
}

