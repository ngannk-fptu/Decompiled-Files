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

public final class ProductVersionSpecifier {
    private final Optional<Either<Integer, String>> nameOrBuild;

    private ProductVersionSpecifier(Optional<Either<Integer, String>> nameOrBuild) {
        this.nameOrBuild = nameOrBuild;
    }

    public static ProductVersionSpecifier buildNumber(int buildNumber) {
        return new ProductVersionSpecifier(Optional.of(Either.left((Object)buildNumber)));
    }

    public static ProductVersionSpecifier name(String name) {
        return new ProductVersionSpecifier(Optional.of(Either.right((Object)name)));
    }

    public static ProductVersionSpecifier latest() {
        return new ProductVersionSpecifier(Optional.empty());
    }

    public Optional<Integer> safeGetBuildNumber() {
        Iterator<Either<Integer, String>> iterator = Convert.iterableOf(this.nameOrBuild).iterator();
        if (iterator.hasNext()) {
            Either<Integer, String> nb = iterator.next();
            return Convert.toOptional(nb.left().toOption());
        }
        return Optional.empty();
    }

    public Optional<String> safeGetName() {
        Iterator<Either<Integer, String>> iterator = Convert.iterableOf(this.nameOrBuild).iterator();
        if (iterator.hasNext()) {
            Either<Integer, String> nb = iterator.next();
            return Convert.toOptional(nb.right().toOption());
        }
        return Optional.empty();
    }

    public String toString() {
        Iterator<Object> iterator = Convert.iterableOf(this.safeGetBuildNumber()).iterator();
        if (iterator.hasNext()) {
            Integer b = iterator.next();
            return "buildNumber(" + b + ")";
        }
        iterator = Convert.iterableOf(this.safeGetName()).iterator();
        if (iterator.hasNext()) {
            String n = (String)iterator.next();
            return "name(" + n + ")";
        }
        return "latest";
    }

    public boolean equals(Object other) {
        return other instanceof ProductVersionSpecifier && ((ProductVersionSpecifier)other).nameOrBuild.equals(this.nameOrBuild);
    }

    public int hashCode() {
        return this.nameOrBuild.hashCode();
    }
}

