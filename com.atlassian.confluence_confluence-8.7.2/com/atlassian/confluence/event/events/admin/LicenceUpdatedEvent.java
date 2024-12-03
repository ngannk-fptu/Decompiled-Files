/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.extras.api.AtlassianLicense
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.event.events.admin.ConfigurationEvent;
import com.atlassian.extras.api.AtlassianLicense;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
@SuppressFBWarnings(value={"SE_NO_SERIALVERSIONID"})
public class LicenceUpdatedEvent
extends ConfigurationEvent {
    private final @NonNull AtlassianLicense license;
    private final @Nullable AtlassianLicense previousLicense;

    public LicenceUpdatedEvent(Object src, @NonNull AtlassianLicense license) {
        this(src, license, null);
    }

    public LicenceUpdatedEvent(Object src, @NonNull AtlassianLicense license, @Nullable AtlassianLicense previousLicense) {
        super(src);
        this.license = Objects.requireNonNull(license);
        this.previousLicense = previousLicense;
    }

    public @NonNull AtlassianLicense getLicense() {
        return this.license;
    }

    public @Nullable AtlassianLicense getPreviousLicense() {
        return this.previousLicense;
    }

    @Deprecated
    public AtlassianLicense getOldLicense() {
        return this.getPreviousLicense();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        LicenceUpdatedEvent that = (LicenceUpdatedEvent)o;
        return this.license.equals(that.license) && Objects.equals(this.previousLicense, that.previousLicense);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.license, this.previousLicense);
    }
}

