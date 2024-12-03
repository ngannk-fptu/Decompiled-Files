/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.profiles;

import java.nio.file.Path;
import java.time.Clock;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSupplier;
import software.amazon.awssdk.profiles.internal.ProfileFileRefresher;

@SdkInternalApi
final class ProfileFileSupplierBuilder {
    private boolean reloadingSupplier = false;
    private Supplier<ProfileFile> profileFile;
    private Path profileFilePath;
    private Clock clock;
    private Consumer<ProfileFile> onProfileFileLoad;

    ProfileFileSupplierBuilder() {
    }

    public ProfileFileSupplierBuilder reloadWhenModified(Path path, ProfileFile.Type type) {
        ProfileFile.Builder builder = ProfileFile.builder().content(path).type(type);
        this.profileFile = builder::build;
        this.profileFilePath = path;
        this.reloadingSupplier = true;
        return this;
    }

    public ProfileFileSupplierBuilder fixedProfileFile(Path path, ProfileFile.Type type) {
        return this.fixedProfileFile(ProfileFile.builder().content(path).type(type).build());
    }

    public ProfileFileSupplierBuilder fixedProfileFile(ProfileFile profileFile) {
        this.profileFile = () -> profileFile;
        this.profileFilePath = null;
        this.reloadingSupplier = false;
        return this;
    }

    public ProfileFileSupplierBuilder onProfileFileLoad(Consumer<ProfileFile> action) {
        this.onProfileFileLoad = action;
        return this;
    }

    public ProfileFileSupplierBuilder clock(Clock clock) {
        this.clock = clock;
        return this;
    }

    public ProfileFileSupplier build() {
        return ProfileFileSupplierBuilder.fromBuilder(this);
    }

    static ProfileFileSupplier fromBuilder(ProfileFileSupplierBuilder builder) {
        if (builder.reloadingSupplier) {
            ProfileFileRefresher.Builder refresherBuilder = ProfileFileRefresher.builder().profileFile(builder.profileFile).profileFilePath(builder.profileFilePath);
            if (Objects.nonNull(builder.clock)) {
                refresherBuilder.clock(builder.clock);
            }
            if (Objects.nonNull(builder.onProfileFileLoad)) {
                refresherBuilder.onProfileFileReload(builder.onProfileFileLoad);
            }
            final ProfileFileRefresher refresher = refresherBuilder.build();
            return new ProfileFileSupplier(){

                @Override
                public ProfileFile get() {
                    return refresher.refreshIfStale();
                }
            };
        }
        return builder.profileFile::get;
    }
}

