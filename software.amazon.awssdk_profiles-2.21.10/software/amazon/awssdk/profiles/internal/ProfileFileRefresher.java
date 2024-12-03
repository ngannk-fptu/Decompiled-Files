/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.utils.cache.CachedSupplier
 *  software.amazon.awssdk.utils.cache.RefreshResult
 */
package software.amazon.awssdk.profiles.internal;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.utils.cache.CachedSupplier;
import software.amazon.awssdk.utils.cache.RefreshResult;

@SdkInternalApi
public final class ProfileFileRefresher {
    private static final ProfileFileRefreshRecord EMPTY_REFRESH_RECORD = ProfileFileRefreshRecord.builder().refreshTime(Instant.MIN).build();
    private final CachedSupplier<ProfileFileRefreshRecord> profileFileCache;
    private volatile ProfileFileRefreshRecord currentRefreshRecord;
    private final Supplier<ProfileFile> profileFile;
    private final Path profileFilePath;
    private final Consumer<ProfileFile> onProfileFileReload;
    private final Clock clock;

    private ProfileFileRefresher(Builder builder) {
        this.clock = builder.clock;
        this.profileFile = builder.profileFile;
        this.profileFilePath = builder.profileFilePath;
        this.onProfileFileReload = builder.onProfileFileReload;
        this.profileFileCache = CachedSupplier.builder(this::refreshResult).cachedValueName("ProfileFileSupplier()").clock(this.clock).build();
        this.currentRefreshRecord = EMPTY_REFRESH_RECORD;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ProfileFile refreshIfStale() {
        ProfileFileRefreshRecord cachedOrRefreshedRecord = (ProfileFileRefreshRecord)this.profileFileCache.get();
        ProfileFile cachedOrRefreshedProfileFile = cachedOrRefreshedRecord.profileFile;
        if (this.isNewProfileFile(cachedOrRefreshedProfileFile)) {
            this.currentRefreshRecord = cachedOrRefreshedRecord;
        }
        return cachedOrRefreshedProfileFile;
    }

    private RefreshResult<ProfileFileRefreshRecord> refreshResult() {
        return this.reloadAsRefreshResultIfStale();
    }

    private RefreshResult<ProfileFileRefreshRecord> reloadAsRefreshResultIfStale() {
        ProfileFileRefreshRecord refreshRecord;
        Instant now = this.clock.instant();
        if (this.canReloadProfileFile() || this.hasNotBeenPreviouslyLoaded()) {
            ProfileFile reloadedProfileFile = ProfileFileRefresher.reload(this.profileFile, this.onProfileFileReload);
            refreshRecord = ProfileFileRefreshRecord.builder().profileFile(reloadedProfileFile).refreshTime(now).build();
        } else {
            refreshRecord = this.currentRefreshRecord;
        }
        return this.wrapIntoRefreshResult(refreshRecord, now);
    }

    private <T> RefreshResult<T> wrapIntoRefreshResult(T value, Instant staleTime) {
        return RefreshResult.builder(value).staleTime(staleTime).build();
    }

    private static ProfileFile reload(Supplier<ProfileFile> supplier) {
        return supplier.get();
    }

    private static ProfileFile reload(Supplier<ProfileFile> supplier, Consumer<ProfileFile> consumer) {
        ProfileFile reloadedProfileFile = ProfileFileRefresher.reload(supplier);
        consumer.accept(reloadedProfileFile);
        return reloadedProfileFile;
    }

    private boolean isNewProfileFile(ProfileFile profileFile) {
        return !Objects.equals(this.currentRefreshRecord.profileFile, profileFile);
    }

    private boolean canReloadProfileFile() {
        if (Objects.isNull(this.profileFilePath)) {
            return false;
        }
        try {
            Instant lastModifiedInstant = Files.getLastModifiedTime(this.profileFilePath, new LinkOption[0]).toInstant();
            return this.currentRefreshRecord.refreshTime.isBefore(lastModifiedInstant);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean hasNotBeenPreviouslyLoaded() {
        return this.currentRefreshRecord == EMPTY_REFRESH_RECORD;
    }

    public static final class ProfileFileRefreshRecord {
        private final Instant refreshTime;
        private final ProfileFile profileFile;

        private ProfileFileRefreshRecord(Builder builder) {
            this.profileFile = builder.profileFile;
            this.refreshTime = builder.refreshTime;
        }

        public ProfileFile profileFile() {
            return this.profileFile;
        }

        public Instant refreshTime() {
            return this.refreshTime;
        }

        static Builder builder() {
            return new Builder();
        }

        private static final class Builder {
            private Instant refreshTime;
            private ProfileFile profileFile;

            private Builder() {
            }

            Builder refreshTime(Instant refreshTime) {
                this.refreshTime = refreshTime;
                return this;
            }

            Builder profileFile(ProfileFile profileFile) {
                this.profileFile = profileFile;
                return this;
            }

            ProfileFileRefreshRecord build() {
                return new ProfileFileRefreshRecord(this);
            }
        }
    }

    public static final class Builder {
        private Supplier<ProfileFile> profileFile;
        private Path profileFilePath;
        private Consumer<ProfileFile> onProfileFileReload = p -> {};
        private Clock clock = Clock.systemUTC();

        private Builder() {
        }

        public Builder profileFile(Supplier<ProfileFile> profileFile) {
            this.profileFile = profileFile;
            return this;
        }

        public Builder profileFilePath(Path profileFilePath) {
            this.profileFilePath = profileFilePath;
            return this;
        }

        @SdkTestInternalApi
        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder onProfileFileReload(Consumer<ProfileFile> consumer) {
            this.onProfileFileReload = consumer;
            return this;
        }

        public ProfileFileRefresher build() {
            return new ProfileFileRefresher(this);
        }
    }
}

