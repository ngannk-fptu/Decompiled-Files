/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.stp.persistence;

import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.request.SupportZipCreationRequest;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ZipConfiguration {
    private static final Integer DEFAULT_FILE_SIZE_CONSTRAINT = 100;
    private static final Integer DEFAULT_FILE_AGE_CONSTRAINT = 5;
    @JsonProperty
    private final List<String> bundleKeys;
    @JsonProperty
    private final Integer fileConstraintSize;
    @JsonProperty
    private final Integer fileConstraintLastModified;

    @JsonCreator
    public ZipConfiguration(@Nonnull @JsonProperty(value="bundleKeys") Collection<String> bundleKeys, @Nullable @JsonProperty(value="fileConstraintSize") Integer fileConstraintSize, @Nullable @JsonProperty(value="fileConstraintLastModified") Integer fileConstraintLastModified) {
        this.bundleKeys = ImmutableList.copyOf(bundleKeys);
        this.fileConstraintSize = fileConstraintSize;
        this.fileConstraintLastModified = fileConstraintLastModified;
    }

    @Nonnull
    public List<String> getBundleKeys() {
        return this.bundleKeys;
    }

    @Nullable
    public Integer getFileConstraintSize() {
        return this.fileConstraintSize;
    }

    @Nullable
    public Integer getFileConstraintLastModified() {
        return this.fileConstraintLastModified;
    }

    public boolean isBundleSelected(String key) {
        return this.bundleKeys.contains(key);
    }

    public static ZipConfiguration from(SupportZipCreationRequest request) {
        Collection bundleKeys = request.getBundles().stream().map(SupportZipBundle::getKey).collect(Collectors.toList());
        return new ZipConfiguration(bundleKeys, request.getMaxMegaBytesPerFile().orElse(-1), request.getFileConstraintLastModified().orElse(-1));
    }

    public static ZipConfiguration getDefaultConfiguration(SupportApplicationInfo info) {
        return new ZipConfiguration(info.getDefaultBundleKeys(), DEFAULT_FILE_SIZE_CONSTRAINT, DEFAULT_FILE_AGE_CONSTRAINT);
    }
}

