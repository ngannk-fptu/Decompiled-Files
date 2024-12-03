/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Either
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.util.Convert;
import io.atlassian.fugue.Either;
import java.util.Iterator;
import java.util.Optional;

public final class AddonVersionSpecifier {
    private final Optional<Either<String, Long>> nameOrBuild;

    private AddonVersionSpecifier(Optional<Either<String, Long>> nameOrBuild) {
        this.nameOrBuild = nameOrBuild;
    }

    public static AddonVersionSpecifier buildNumber(long buildNumber) {
        return new AddonVersionSpecifier(Optional.of(Either.right((Object)buildNumber)));
    }

    public static AddonVersionSpecifier versionName(String name) {
        return new AddonVersionSpecifier(Optional.of(Either.left((Object)name)));
    }

    public static AddonVersionSpecifier latest() {
        return new AddonVersionSpecifier(Optional.empty());
    }

    public Optional<Either<String, Long>> getSpecifiedVersion() {
        return this.nameOrBuild;
    }

    public String toString() {
        for (Either<String, Long> vob : Convert.iterableOf(this.nameOrBuild)) {
            Iterator iterator = vob.right().iterator();
            if (iterator.hasNext()) {
                Long b = (Long)iterator.next();
                return "buildNumber(" + b + ")";
            }
            iterator = vob.left().iterator();
            if (!iterator.hasNext()) continue;
            String n = (String)iterator.next();
            return "name(" + n + ")";
        }
        return "latest";
    }

    public boolean equals(Object other) {
        return other instanceof AddonVersionSpecifier && ((AddonVersionSpecifier)other).nameOrBuild.equals(this.nameOrBuild);
    }

    public int hashCode() {
        return this.nameOrBuild.hashCode();
    }
}

