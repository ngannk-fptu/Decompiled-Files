/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.persistence.dao.filesystem.HierarchicalContentFileSystemHelper
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.conversion.impl;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.persistence.dao.filesystem.HierarchicalContentFileSystemHelper;
import com.atlassian.confluence.plugins.conversion.api.ConversionStatus;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemConversionState {
    private static final Logger log = LoggerFactory.getLogger(FileSystemConversionState.class);
    public static final String MIME_SUFFIX = "mime";
    private static final HierarchicalContentFileSystemHelper fileSystemHelper = new HierarchicalContentFileSystemHelper();
    private static final Map<ConversionType, String> PREFIXES = Stream.of(ConversionType.values()).collect(Collectors.toMap(Function.identity(), type -> File.separator + "dcl-" + type.name().toLowerCase() + File.separator));
    private static BootstrapManager bootstrapManager = (BootstrapManager)ContainerManager.getComponent((String)"bootstrapManager");
    private static ClusterManager clusterManager = (ClusterManager)ContainerManager.getComponent((String)"clusterManager");
    private final File tempFile;
    private final File convertedFile;
    private final File errorFile;
    private boolean isBusy = false;

    public FileSystemConversionState(Attachment attachment, ConversionType conversionType) {
        this.tempFile = FileSystemConversionState.getStatusFileWithExtension(attachment, conversionType, ConversionStatus.IN_PROGRESS);
        this.convertedFile = FileSystemConversionState.getStatusFileWithExtension(attachment, conversionType, ConversionStatus.CONVERTED);
        this.errorFile = FileSystemConversionState.getStatusFileWithExtension(attachment, conversionType, ConversionStatus.ERROR);
    }

    public File getTempFile() {
        return this.tempFile;
    }

    public File getConvertedFile() {
        return this.convertedFile;
    }

    @VisibleForTesting
    public File getErrorFile() {
        return this.errorFile;
    }

    public boolean isConverted() {
        return this.convertedFile.exists() && this.convertedFile.length() != 0L;
    }

    public boolean isError() {
        return this.errorFile.exists() || this.convertedFile.exists() && this.convertedFile.length() == 0L;
    }

    public ConversionStatus getStatus() {
        if (this.isConverted()) {
            return ConversionStatus.CONVERTED;
        }
        if (this.isError()) {
            return ConversionStatus.ERROR;
        }
        if (this.isBusy) {
            return ConversionStatus.BUSY;
        }
        return ConversionStatus.IN_PROGRESS;
    }

    public void markAsError() {
        try {
            log.warn("Creating error file: " + this.errorFile.getAbsolutePath());
            this.errorFile.createNewFile();
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot create error file", e);
        }
    }

    public static Collection<File> getConversionDirectories() {
        ImmutableSet.Builder directories = ImmutableSet.builder();
        for (String conversionType : PREFIXES.values()) {
            directories.add((Object)FileSystemConversionState.getRootStorageDirectory(conversionType));
        }
        return directories.build();
    }

    public void markAsBusy() {
        this.isBusy = true;
    }

    private static File getRootStorageDirectory(String pathSuffix) {
        return new File(bootstrapManager.getSharedHome() + pathSuffix);
    }

    public static File getStorageFolder(Attachment attachment, ConversionType conversionType) {
        return fileSystemHelper.createDirectoryHierarchy(FileSystemConversionState.getRootStorageDirectory(PREFIXES.get((Object)conversionType)), attachment.getContainer().getId());
    }

    public static FileFilter getStatusFileFilter(Attachment attachment) {
        return file -> {
            Pattern filePattern = Pattern.compile(Long.toString(((Attachment)attachment.getLatestVersion()).getId()) + "_" + Integer.toString(attachment.getVersion()) + ".*");
            Matcher fileMatcher = filePattern.matcher(file.getName());
            return fileMatcher.matches();
        };
    }

    public static File getStatusFileWithExtension(Attachment attachment, ConversionType conversionType, ConversionStatus conversionStatus) {
        return new File(FileSystemConversionState.getStorageFolder(attachment, conversionType), Long.toString(((Attachment)attachment.getLatestVersion()).getId()) + "_" + Integer.toString(attachment.getVersion()) + FileSystemConversionState.getFilenameSuffix(conversionStatus));
    }

    private static String getFilenameSuffix(ConversionStatus conversionStatus) {
        switch (conversionStatus) {
            case CONVERTED: {
                return "";
            }
            case ERROR: {
                return ".err";
            }
            case IN_PROGRESS: {
                if (clusterManager.getThisNodeInformation() != null) {
                    return "-" + clusterManager.getThisNodeInformation().getAnonymizedNodeIdentifier() + "_" + Thread.currentThread().getId() + ".tmp";
                }
                return "-" + Thread.currentThread().getId() + ".tmp";
            }
            case BUSY: {
                return null;
            }
        }
        return null;
    }

    @VisibleForTesting
    static void setBootstrapManager(BootstrapManager bootstrapManager) {
        FileSystemConversionState.bootstrapManager = bootstrapManager;
    }

    @VisibleForTesting
    static void setClusterManager(ClusterManager clusterManager) {
        FileSystemConversionState.clusterManager = clusterManager;
    }
}

