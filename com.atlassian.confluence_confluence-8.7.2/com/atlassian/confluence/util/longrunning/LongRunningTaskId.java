/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.confluence.api.model.longtasks.LongTaskId
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.util.longrunning;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import java.io.Serializable;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public final class LongRunningTaskId
implements Serializable {
    private final @NonNull String uuid;

    public static LongRunningTaskId valueOf(@NonNull String stringRepresentation) {
        return new LongRunningTaskId(stringRepresentation);
    }

    public static LongRunningTaskId newInstance() {
        return new LongRunningTaskId(UUID.randomUUID().toString());
    }

    public static LongRunningTaskId from(LongTaskId id) {
        return new LongRunningTaskId(id.serialise());
    }

    public LongTaskId asLongTaskId() {
        return LongTaskId.deserialise((String)this.uuid);
    }

    private LongRunningTaskId(@NonNull String uuid) {
        this.uuid = uuid;
    }

    public String toString() {
        return this.uuid;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return this.uuid.equals(((LongRunningTaskId)o).uuid);
    }

    public int hashCode() {
        return this.uuid.hashCode();
    }
}

