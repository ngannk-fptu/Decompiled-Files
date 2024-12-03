/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.marketplace.client.model;

import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.HostingType;
import com.google.common.base.Predicate;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import java.util.Iterator;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class VersionCompatibility {
    private final ApplicationKey application;
    private final CompatibilityHosting hosting;

    VersionCompatibility(ApplicationKey application, CompatibilityHosting hosting) {
        this.application = application;
        this.hosting = hosting;
    }

    CompatibilityHosting getHosting() {
        return this.hosting;
    }

    public ApplicationKey getApplication() {
        return this.application;
    }

    public boolean isCloudCompatible() {
        return this.hosting.isCloudCompatible();
    }

    public boolean isServerCompatible() {
        return this.hosting.isServerCompatible();
    }

    public boolean isDataCenterCompatible() {
        return this.hosting.isDataCenterCompatible();
    }

    public Option<Pair<Integer, Integer>> getServerBuildRange() {
        Iterator iterator = this.hosting.server.iterator();
        if (iterator.hasNext()) {
            CompatibilityHostingBounds b = (CompatibilityHostingBounds)iterator.next();
            return Option.some((Object)Pair.pair((Object)b.min.build, (Object)b.max.build));
        }
        return Option.none();
    }

    public boolean isInServerBuildRange(int build) {
        Iterator iterator = this.hosting.server.iterator();
        if (iterator.hasNext()) {
            CompatibilityHostingBounds b = (CompatibilityHostingBounds)iterator.next();
            return build >= b.min.build && build <= b.max.build;
        }
        return false;
    }

    public boolean isInDataCenterBuildRange(int build) {
        Iterator iterator = this.hosting.dataCenter.iterator();
        if (iterator.hasNext()) {
            CompatibilityHostingBounds b = (CompatibilityHostingBounds)iterator.next();
            return build >= b.min.build && build <= b.max.build;
        }
        return false;
    }

    public Option<Integer> getServerMinBuild() {
        Iterator iterator = this.hosting.server.iterator();
        if (iterator.hasNext()) {
            CompatibilityHostingBounds b = (CompatibilityHostingBounds)iterator.next();
            return Option.some((Object)b.min.build);
        }
        return Option.none();
    }

    public Option<Integer> getServerMaxBuild() {
        Iterator iterator = this.hosting.server.iterator();
        if (iterator.hasNext()) {
            CompatibilityHostingBounds b = (CompatibilityHostingBounds)iterator.next();
            return Option.some((Object)b.max.build);
        }
        return Option.none();
    }

    public Option<Integer> getDataCenterMinBuild() {
        return this.hosting.dataCenter.map(b -> b.min.build);
    }

    public Option<Integer> getDataCenterMaxBuild() {
        return this.hosting.dataCenter.map(b -> b.max.build);
    }

    public boolean isCompatibleWith(Predicate<ApplicationKey> applicationCriteria, HostingType hostingType, int build) {
        return applicationCriteria.apply((Object)this.getApplication()) && (hostingType == HostingType.CLOUD && this.hosting.isCloudCompatible() || hostingType == HostingType.SERVER && this.hosting.isServerCompatible() && this.isInServerBuildRange(build) || hostingType == HostingType.DATA_CENTER && this.hosting.isDataCenterCompatible() && this.isInDataCenterBuildRange(build));
    }

    public static Predicate<VersionCompatibility> compatibleWith(Predicate<ApplicationKey> applicationCriteria, HostingType hostingType, int build) {
        return c -> c.isCompatibleWith(applicationCriteria, hostingType, build);
    }

    static final class VersionPoint {
        final int build;
        final Option<String> version;

        VersionPoint(int build, Option<String> version) {
            this.build = build;
            this.version = version;
        }
    }

    static final class CompatibilityHostingBounds {
        @Nonnull
        final VersionPoint min;
        @Nonnull
        final VersionPoint max;

        CompatibilityHostingBounds(VersionPoint min, VersionPoint max) {
            this.min = min;
            this.max = max;
        }
    }

    static final class CompatibilityHosting {
        @Nonnull
        final Option<CompatibilityHostingBounds> server;
        @Nonnull
        final Option<CompatibilityHostingBounds> dataCenter;
        final Option<Boolean> cloud;

        CompatibilityHosting(Option<CompatibilityHostingBounds> server, Option<CompatibilityHostingBounds> dataCenter, Option<Boolean> cloud) {
            this.server = Objects.requireNonNull(server);
            this.dataCenter = Objects.requireNonNull(dataCenter);
            this.cloud = Objects.requireNonNull(cloud);
        }

        boolean isServerCompatible() {
            return this.server.isDefined();
        }

        boolean isDataCenterCompatible() {
            return this.dataCenter.isDefined();
        }

        boolean isCloudCompatible() {
            return (Boolean)this.cloud.getOrElse((Object)false);
        }

        @Nonnull
        Option<CompatibilityHostingBounds> getServer() {
            return this.server;
        }

        @Nonnull
        Option<CompatibilityHostingBounds> getDataCenter() {
            return this.dataCenter;
        }

        Option<Boolean> getCloud() {
            return this.cloud;
        }
    }
}

