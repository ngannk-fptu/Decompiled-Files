/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.salext.bundle;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import com.atlassian.troubleshooting.api.supportzip.FileSupportZipArtifact;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.AbstractSupportZipBundle;
import com.atlassian.troubleshooting.stp.salext.bundle.BundleManifest;
import com.atlassian.troubleshooting.stp.salext.bundle.fileset.FileSet;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class FileSetsBundle
extends AbstractSupportZipBundle {
    private final Map<FileSet, String> fileSets;
    private final BundleCategory category;

    private FileSetsBundle(Builder builder, I18nResolver i18nResolver) {
        super(i18nResolver, builder.bundle, builder.title, builder.description, builder.applicable, builder.applicabilityReason);
        this.category = builder.category;
        this.fileSets = builder.fileSetsBuilder.build();
    }

    public static Builder builder(BundleManifest bundle, String title, String description, BundleCategory category, I18nResolver i18nResolver) {
        return new Builder(bundle, title, description, category, i18nResolver);
    }

    @Override
    public BundleCategory getCategory() {
        return this.category;
    }

    @Override
    public Collection<SupportZipBundle.Artifact> getArtifacts() {
        return this.fileSets.entrySet().stream().flatMap(s -> ((FileSet)s.getKey()).getFiles().stream().map(f -> new FileSupportZipArtifact((File)f, (String)s.getValue()))).collect(Collectors.toList());
    }

    public static final class Builder {
        private final BundleManifest bundle;
        private final String title;
        private final String description;
        private final BundleCategory category;
        private final ImmutableMap.Builder<FileSet, String> fileSetsBuilder = ImmutableMap.builder();
        private final I18nResolver i18nResolver;
        private boolean applicable = true;
        private String applicabilityReason = "";

        private Builder(BundleManifest bundle, String title, String description, BundleCategory category, I18nResolver i18nResolver) {
            this.bundle = bundle;
            this.title = title;
            this.description = description;
            this.category = category;
            this.i18nResolver = i18nResolver;
        }

        @Nonnull
        public Builder fileSet(@Nonnull FileSet fileSet) {
            return this.fileSet(fileSet, "");
        }

        @Nonnull
        public Builder fileSet(@Nonnull FileSet fileSet, @Nonnull String targetPath) {
            this.fileSetsBuilder.put((Object)fileSet, (Object)targetPath);
            return this;
        }

        @Nonnull
        public Builder fileSets(@Nonnull List<FileSet> fileSets) {
            fileSets.forEach(this::fileSet);
            return this;
        }

        @Nonnull
        public Builder applicable(@Nonnull Boolean applicable) {
            this.applicable = applicable;
            return this;
        }

        @Nonnull
        public FileSetsBundle build() {
            return new FileSetsBundle(this, this.i18nResolver);
        }

        @Nonnull
        public Builder notApplicabilityReason(@Nonnull String text) {
            this.applicabilityReason = text;
            return this;
        }
    }
}

