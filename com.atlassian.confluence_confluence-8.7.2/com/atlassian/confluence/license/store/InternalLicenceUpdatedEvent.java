/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.license.store;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;
import com.google.common.base.Preconditions;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
@Internal
class InternalLicenceUpdatedEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -8040893847141294067L;
    private final String licenseString;

    InternalLicenceUpdatedEvent(Object src, String licenseString) {
        super(src);
        this.licenseString = (String)Preconditions.checkNotNull((Object)licenseString);
    }

    @NonNull String getLicenseString() {
        return this.licenseString;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalLicenceUpdatedEvent)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        InternalLicenceUpdatedEvent that = (InternalLicenceUpdatedEvent)o;
        return Objects.equals(this.licenseString, that.licenseString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.licenseString);
    }
}

