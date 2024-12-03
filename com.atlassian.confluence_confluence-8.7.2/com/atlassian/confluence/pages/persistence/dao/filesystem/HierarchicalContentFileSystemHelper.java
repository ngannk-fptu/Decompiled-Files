/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.persistence.dao.filesystem;

import com.atlassian.confluence.pages.persistence.dao.filesystem.IdMultiPartHashGenerator;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.File;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HierarchicalContentFileSystemHelper {
    private static final Logger log = LoggerFactory.getLogger(HierarchicalContentFileSystemHelper.class);
    private final IdMultiPartHashGenerator hashGenerator = new IdMultiPartHashGenerator(3, 250, 2);

    @Deprecated
    public File createDirectoryHierarchy(File parentDir, long contentId) {
        File directory = this.getDirectory(parentDir, contentId);
        if (!directory.isDirectory() && !directory.mkdirs()) {
            log.warn("Failed to create directory at {}", (Object)directory.getAbsolutePath());
        }
        return directory;
    }

    public File getDirectory(File parentDir, long contentId) {
        return HierarchicalContentFileSystemHelper.convertDirectoryPartsToDirectory(parentDir, this.getIdParts(contentId).collect(Collectors.toList()));
    }

    public FilesystemPath getDirectory(FilesystemPath parentDir, long contentId) {
        return parentDir.path((String[])this.getIdParts(contentId).toArray(String[]::new));
    }

    private Stream<String> getIdParts(long contentId) {
        LinkedList<Integer> parts = new LinkedList<Integer>(this.hashGenerator.generate(contentId));
        parts.add((Integer)contentId);
        return parts.stream().map(String::valueOf);
    }

    private static File convertDirectoryPartsToDirectory(File topLevelDir, Iterable<String> parts) {
        File parentDir = topLevelDir;
        File currentDir = null;
        for (String part : parts) {
            parentDir = currentDir = new File(parentDir, part);
        }
        return currentDir;
    }
}

