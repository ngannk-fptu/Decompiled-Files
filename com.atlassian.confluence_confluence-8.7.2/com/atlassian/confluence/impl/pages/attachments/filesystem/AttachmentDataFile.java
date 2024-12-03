/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.core.io.InputStreamSource
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.confluence.impl.pages.attachments.AttachmentV4Helper;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.AttachmentRef;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.pages.persistence.dao.filesystem.AttachmentDataFileSystemException;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamSource;

public class AttachmentDataFile<T extends FileStore.Path>
implements InputStreamSource {
    private static final Pattern FILE_PATTERN_V003 = Pattern.compile("(\\d+)(\\.\\S+)?");
    private static final Pattern FILE_PATTERN_V004 = Pattern.compile("(\\d+).(\\d+)(\\.\\S+)?");
    private final T file;
    private final String suffix;
    private final String attachmentVersion;

    static Stream<AttachmentDataFile<FilesystemPath>> fromContainer(FilesystemPath containerPath) throws IOException {
        return containerPath.getFileDescendents().flatMap(filePath -> AttachmentDataFile.getAttachmentDataFile(filePath).map(Stream::of).orElseGet(Stream::empty));
    }

    private static Optional<AttachmentDataFile<FilesystemPath>> getAttachmentDataFile(FilesystemPath file) {
        return file.getLeafName().flatMap(name -> {
            Matcher matcherV003 = FILE_PATTERN_V003.matcher((CharSequence)name);
            Matcher matcherV004 = FILE_PATTERN_V004.matcher((CharSequence)name);
            if (matcherV004.matches()) {
                return Optional.of(new AttachmentDataFile<FilesystemPath>(file, matcherV004.group(2), StringUtils.trimToEmpty((String)matcherV004.group(3))));
            }
            if (matcherV003.matches()) {
                return Optional.of(new AttachmentDataFile<FilesystemPath>(file, matcherV003.group(1), StringUtils.trimToEmpty((String)matcherV003.group(2))));
            }
            return Optional.empty();
        });
    }

    @Deprecated
    static AttachmentDataFile<FilesystemPath> getAttachmentDataFile(FilesystemPath dir, AttachmentRef attachmentVersion, AttachmentDataStreamType dataStreamType) {
        return AttachmentDataFile.getAttachmentDataFile(dir, attachmentVersion.getVersion(), dataStreamType);
    }

    @Deprecated
    public static AttachmentDataFile<FilesystemPath> getAttachmentDataFile(FilesystemPath dir, int attachmentVersion, AttachmentDataStreamType dataStreamType) {
        String version = String.valueOf(attachmentVersion);
        String suffix = AttachmentDataFile.getDataStreamFilenameSuffix(dataStreamType);
        String filename = String.format("%s%s", version, suffix);
        FilesystemPath file = dir.path(new String[]{filename});
        return new AttachmentDataFile<FilesystemPath>(file, version, suffix);
    }

    public static <T extends FileStore.Path> AttachmentDataFile<T> getAttachmentDataFileV004(T basePath, AttachmentRef attachment, AttachmentDataStreamType dataStreamType) {
        return AttachmentDataFile.getAttachmentDataFileV004(basePath, attachment.getId(), attachment.getVersion(), dataStreamType);
    }

    public static <T extends FileStore.Path> AttachmentDataFile<T> getAttachmentDataFileV004(T basePath, long attachmentId, Integer attachmentVersion, AttachmentDataStreamType dataStreamType) {
        String id = String.valueOf(attachmentId);
        String version = String.valueOf(attachmentVersion);
        String suffix = AttachmentDataFile.getDataStreamFilenameSuffix(dataStreamType);
        String filename = String.format("%s.%s%s", id, version, suffix);
        T attachmentContainer = AttachmentV4Helper.getContainerPathForAttachmentVersions(basePath, attachmentId);
        FileStore.Path file = attachmentContainer.path(new String[]{filename});
        return new AttachmentDataFile<FileStore.Path>(file, version, suffix);
    }

    private static String getDataStreamFilenameSuffix(AttachmentDataStreamType dataStreamType) {
        switch (dataStreamType) {
            case RAW_BINARY: {
                return "";
            }
            case EXTRACTED_TEXT: {
                return ".extracted_text";
            }
        }
        throw new IllegalArgumentException("Unrecognised data stream type " + dataStreamType);
    }

    AttachmentDataFile(T file, String attachmentVersion, String suffix) {
        this.attachmentVersion = attachmentVersion;
        this.file = (FileStore.Path)Preconditions.checkNotNull(file);
        this.suffix = (String)Preconditions.checkNotNull((Object)suffix);
    }

    boolean matchesVersion(int version) {
        return String.valueOf(version).equals(this.attachmentVersion);
    }

    public T getFilePath() {
        return this.file;
    }

    void delete() throws IOException {
        this.file.deleteFile();
    }

    @Deprecated
    void moveTo(AttachmentDataFile<FilesystemPath> targetFile) throws IOException {
        this.file.moveFile(targetFile.file);
    }

    @Deprecated
    AttachmentDataFile<FilesystemPath> withVersion(int newVersion) {
        if (this.file instanceof FilesystemPath) {
            FilesystemPath filesystemPath = (FilesystemPath)this.file;
            String newFilename = newVersion + this.suffix;
            FilesystemPath newFile = filesystemPath.getParent().map(parent -> parent.path(new String[]{newFilename})).orElseThrow(() -> new AttachmentDataFileSystemException("File has no parent: " + this.file));
            return new AttachmentDataFile<FilesystemPath>(newFile, String.valueOf(newVersion), this.suffix);
        }
        throw new UnsupportedOperationException("This method is only supported when using FilesystemPath to access attachments");
    }

    public boolean exists() {
        return this.file.tryFileExists();
    }

    public String toString() {
        return this.file.toString();
    }

    public int hashCode() {
        return this.file.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AttachmentDataFile that = (AttachmentDataFile)o;
        return this.file.equals(that.file);
    }

    public InputStream getInputStream() throws IOException {
        return this.file.fileReader().openInputStream();
    }
}

