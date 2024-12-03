/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;
import com.google.gson.internal.$Gson$Preconditions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class VersionExclusionStrategy
implements ExclusionStrategy {
    private final double version;

    VersionExclusionStrategy(double version) {
        $Gson$Preconditions.checkArgument(version >= 0.0);
        this.version = version;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return !this.isValidVersion(f.getAnnotation(Since.class), f.getAnnotation(Until.class));
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return !this.isValidVersion(clazz.getAnnotation(Since.class), clazz.getAnnotation(Until.class));
    }

    private boolean isValidVersion(Since since, Until until) {
        return this.isValidSince(since) && this.isValidUntil(until);
    }

    private boolean isValidSince(Since annotation) {
        double annotationVersion;
        return annotation == null || !((annotationVersion = annotation.value()) > this.version);
    }

    private boolean isValidUntil(Until annotation) {
        double annotationVersion;
        return annotation == null || !((annotationVersion = annotation.value()) <= this.version);
    }
}

