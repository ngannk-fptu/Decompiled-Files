/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.comparator.LastModifiedFileComparator
 *  org.apache.commons.io.filefilter.FalseFileFilter
 *  org.apache.commons.io.filefilter.IOFileFilter
 *  org.apache.commons.io.filefilter.RegexFileFilter
 *  org.apache.commons.io.filefilter.TrueFileFilter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.bundle.fileset;

import com.atlassian.troubleshooting.stp.salext.bundle.fileset.FileSet;
import com.atlassian.troubleshooting.stp.salext.bundle.fileset.FileSetUtil;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexFileSet
implements FileSet {
    private static final Logger LOG = LoggerFactory.getLogger(RegexFileSet.class);
    private final File directory;
    private final RegexFileFilter fileFilter;
    private final boolean recursive;
    private final int limitedFiles;

    private RegexFileSet(File directory, Pattern filenamePattern, boolean recursive, int limitedFiles) {
        this.directory = directory;
        this.fileFilter = new RegexFileFilter(filenamePattern);
        this.recursive = recursive;
        this.limitedFiles = limitedFiles;
    }

    @Deprecated
    public static RegexFileSet fromDirectoryPath(String directoryPath, Pattern filenamePattern) {
        return new RegexFileSet(FileSetUtil.fileWithPath(directoryPath), filenamePattern, false, 0);
    }

    @Deprecated
    public static RegexFileSet fromDirectoryPathRecursive(String directoryPath, Pattern filenamePattern) {
        return new RegexFileSet(FileSetUtil.fileWithPath(directoryPath), filenamePattern, true, 0);
    }

    @Override
    @Nonnull
    public Set<File> getFiles() {
        if (this.directory.isDirectory()) {
            IOFileFilter dirFilter = this.recursive ? TrueFileFilter.INSTANCE : FalseFileFilter.INSTANCE;
            Collection files = FileUtils.listFiles((File)this.directory, (IOFileFilter)this.fileFilter, (IOFileFilter)dirFilter);
            for (File file : files) {
                if (file.length() != 0L) continue;
                LOG.debug("The file {} does not contain any data", (Object)file.getAbsolutePath());
            }
            if (this.limitedFiles > 0) {
                files = files.stream().sorted(LastModifiedFileComparator.LASTMODIFIED_REVERSE).limit(this.limitedFiles).collect(Collectors.toSet());
            }
            return Collections.unmodifiableSet(new HashSet(files));
        }
        if (this.directory.isFile()) {
            LOG.warn("The requested base directory {} is a file rather than a directory, so the entire file set was skipped", (Object)this.directory.getAbsolutePath());
        } else {
            LOG.warn("The requested base directory {} does not exist, so the entire file set was skipped", (Object)this.directory.getAbsolutePath());
        }
        return Collections.emptySet();
    }

    public static class Builder {
        private File directory;
        private Pattern pattern;
        private boolean recursive = false;
        private int limitedFiles = 0;

        public Builder withDirectory(File directory) {
            this.directory = directory;
            return this;
        }

        public Builder withDirectory(String directory) {
            this.directory = FileSetUtil.fileWithPath(directory);
            return this;
        }

        public Builder withPattern(Pattern pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder findRecursively() {
            this.recursive = true;
            return this;
        }

        public Builder limitToMostRecentFiles(int numberToReturn) {
            this.limitedFiles = numberToReturn;
            return this;
        }

        public RegexFileSet build() {
            return new RegexFileSet(this.directory, this.pattern, this.recursive, this.limitedFiles);
        }
    }
}

