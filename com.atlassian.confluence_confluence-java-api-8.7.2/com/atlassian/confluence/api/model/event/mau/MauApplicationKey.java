/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.model.event.mau;

import com.atlassian.annotations.ExperimentalApi;
import org.checkerframework.checker.nullness.qual.NonNull;

@ExperimentalApi
public class MauApplicationKey {
    public static final MauApplicationKey APP_CONFLUENCE = new MauApplicationKey("confluence");
    private final String application;

    public MauApplicationKey(@NonNull String application) {
        this.application = application;
    }

    public String getKey() {
        return this.application;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MauApplicationKey that = (MauApplicationKey)o;
        return !(this.application != null ? !this.application.equals(that.application) : that.application != null);
    }

    public int hashCode() {
        return this.application != null ? this.application.hashCode() : 0;
    }
}

