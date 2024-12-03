/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.profiles;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileLocation;
import software.amazon.awssdk.profiles.internal.ProfileFileRefresher;

@FunctionalInterface
@SdkPublicApi
public interface ProfileFileSupplier
extends Supplier<ProfileFile> {
    public static ProfileFileSupplier defaultSupplier() {
        Optional<ProfileFileSupplier> credentialsSupplierOptional = ProfileFileLocation.credentialsFileLocation().map(path -> ProfileFileSupplier.reloadWhenModified(path, ProfileFile.Type.CREDENTIALS));
        Optional<ProfileFileSupplier> configurationSupplierOptional = ProfileFileLocation.configurationFileLocation().map(path -> ProfileFileSupplier.reloadWhenModified(path, ProfileFile.Type.CONFIGURATION));
        ProfileFileSupplier supplier = () -> ProfileFile.builder().build();
        if (credentialsSupplierOptional.isPresent() && configurationSupplierOptional.isPresent()) {
            supplier = ProfileFileSupplier.aggregate(credentialsSupplierOptional.get(), configurationSupplierOptional.get());
        } else if (credentialsSupplierOptional.isPresent()) {
            supplier = credentialsSupplierOptional.get();
        } else if (configurationSupplierOptional.isPresent()) {
            supplier = configurationSupplierOptional.get();
        }
        return supplier;
    }

    public static ProfileFileSupplier reloadWhenModified(final Path path, final ProfileFile.Type type) {
        return new ProfileFileSupplier(){
            final ProfileFile.Builder builder;
            final ProfileFileRefresher refresher;
            {
                this.builder = ProfileFile.builder().content(path).type(type);
                this.refresher = ProfileFileRefresher.builder().profileFile(this.builder::build).profileFilePath(path).build();
            }

            @Override
            public ProfileFile get() {
                return this.refresher.refreshIfStale();
            }
        };
    }

    public static ProfileFileSupplier fixedProfileFile(ProfileFile profileFile) {
        return () -> profileFile;
    }

    public static ProfileFileSupplier aggregate(final ProfileFileSupplier ... suppliers) {
        return new ProfileFileSupplier(){
            final AtomicReference<ProfileFile> currentAggregateProfileFile = new AtomicReference();
            final Map<Supplier<ProfileFile>, ProfileFile> currentValuesBySupplier = Collections.synchronizedMap(new LinkedHashMap());

            @Override
            public ProfileFile get() {
                boolean refreshAggregate = false;
                for (ProfileFileSupplier supplier : suppliers) {
                    if (!this.didSuppliedValueChange(supplier)) continue;
                    refreshAggregate = true;
                }
                if (refreshAggregate) {
                    this.refreshCurrentAggregate();
                }
                return this.currentAggregateProfileFile.get();
            }

            private boolean didSuppliedValueChange(Supplier<ProfileFile> supplier) {
                ProfileFile current;
                ProfileFile next = supplier.get();
                return !Objects.equals(next, current = this.currentValuesBySupplier.put(supplier, next));
            }

            private void refreshCurrentAggregate() {
                ProfileFile.Aggregator aggregator = ProfileFile.aggregator();
                this.currentValuesBySupplier.values().forEach(aggregator::addFile);
                ProfileFile current = this.currentAggregateProfileFile.get();
                ProfileFile next = aggregator.build();
                if (!Objects.equals(current, next)) {
                    this.currentAggregateProfileFile.compareAndSet(current, next);
                }
            }
        };
    }
}

