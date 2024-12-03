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

public final class ApplicationVersionSpecifier {
    private final Optional<Either<String, Integer>> nameOrBuild;

    private ApplicationVersionSpecifier(Optional<Either<String, Integer>> nameOrBuild) {
        this.nameOrBuild = nameOrBuild;
    }

    public static ApplicationVersionSpecifier buildNumber(int buildNumber) {
        return new ApplicationVersionSpecifier(Optional.of(Either.right((Object)buildNumber)));
    }

    public static ApplicationVersionSpecifier versionName(String name) {
        return new ApplicationVersionSpecifier(Optional.of(Either.left((Object)name)));
    }

    public static ApplicationVersionSpecifier latest() {
        return new ApplicationVersionSpecifier(Optional.empty());
    }

    public Optional<Either<String, Integer>> safeGetSpecifiedVersion() {
        return this.nameOrBuild;
    }

    public String toString() {
        for (Either<String, Integer> vob : Convert.iterableOf(this.nameOrBuild)) {
            Iterator iterator = vob.right().iterator();
            if (iterator.hasNext()) {
                Integer b = (Integer)iterator.next();
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
        return other instanceof ApplicationVersionSpecifier && ((ApplicationVersionSpecifier)other).nameOrBuild.equals(this.nameOrBuild);
    }

    public int hashCode() {
        return this.nameOrBuild.hashCode();
    }
}

