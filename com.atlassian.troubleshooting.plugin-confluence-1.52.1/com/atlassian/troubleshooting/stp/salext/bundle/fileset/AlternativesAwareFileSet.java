/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.bundle.fileset;

import com.atlassian.troubleshooting.stp.salext.bundle.fileset.FileSet;
import com.atlassian.troubleshooting.stp.salext.bundle.fileset.FileSetUtil;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlternativesAwareFileSet
implements FileSet {
    private static final Logger LOG = LoggerFactory.getLogger(AlternativesAwareFileSet.class);
    private static final Function<File, String> FILE_TO_ABSOLUTE_PATH = new Function<File, String>(){

        public String apply(@Nullable File file) {
            return file == null ? "" : file.getAbsolutePath();
        }
    };
    private final List<List<File>> filesWithAlternatives;

    private AlternativesAwareFileSet(Builder builder) {
        this.filesWithAlternatives = builder.filesWithAlternatives;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    @Nonnull
    public Set<File> getFiles() {
        HashSet<File> resultSet = new HashSet<File>();
        for (List<File> fileAlternatives : this.filesWithAlternatives) {
            Optional<File> maybeFile = this.getFileFromAlternatives(fileAlternatives);
            if (maybeFile.isPresent()) {
                resultSet.add(maybeFile.get());
                continue;
            }
            LOG.info("None of file alternative locations {} exists, hence the record was skipped", (Object)ImmutableList.copyOf((Iterable)Iterables.transform(fileAlternatives, FILE_TO_ABSOLUTE_PATH)));
        }
        return Collections.unmodifiableSet(resultSet);
    }

    private Optional<File> getFileFromAlternatives(List<File> fileAlternatives) {
        for (File file : fileAlternatives) {
            if (!file.isFile()) continue;
            return Optional.of(file);
        }
        return Optional.empty();
    }

    public static final class Builder {
        private final List<List<File>> filesWithAlternatives = new ArrayList<List<File>>();

        private Builder() {
        }

        @Nonnull
        public Builder file(@Nonnull File file) {
            this.filesWithAlternatives.add(Collections.singletonList(file));
            return this;
        }

        @Nonnull
        public Builder fileWithAlternatives(@Nonnull Collection<File> fileWithAlternatives) {
            this.filesWithAlternatives.add(new ArrayList<File>(fileWithAlternatives));
            return this;
        }

        @Nonnull
        public Builder path(@Nonnull String path) {
            this.file(FileSetUtil.fileWithPath(path));
            return this;
        }

        @Nonnull
        public Builder pathWithAlternatives(@Nonnull Collection<String> pathWithAlternatives) {
            this.fileWithAlternatives(FileSetUtil.filesWithPaths(pathWithAlternatives));
            return this;
        }

        @Nonnull
        public AlternativesAwareFileSet build() {
            return new AlternativesAwareFileSet(this);
        }
    }
}

