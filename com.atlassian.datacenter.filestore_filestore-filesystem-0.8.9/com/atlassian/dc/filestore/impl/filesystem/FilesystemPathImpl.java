/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.DataSize
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.dc.filestore.api.FileStore$Reader
 *  com.atlassian.dc.filestore.api.FileStore$Writer
 *  com.atlassian.dc.filestore.api.Snapshot
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath$Pruner
 *  com.atlassian.dc.filestore.common.snapshot.DirectorySnapshot
 *  com.atlassian.dc.filestore.common.snapshot.EmptySnapshot
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.dc.filestore.impl.filesystem;

import com.atlassian.dc.filestore.api.DataSize;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.dc.filestore.api.Snapshot;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.dc.filestore.common.snapshot.DirectorySnapshot;
import com.atlassian.dc.filestore.common.snapshot.EmptySnapshot;
import com.atlassian.dc.filestore.impl.filesystem.FilesystemPathWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FilesystemPathImpl
implements FilesystemPath {
    private static final Logger log = LoggerFactory.getLogger(FilesystemPathImpl.class);
    private final UnaryOperator<Path> pathSanitiser;
    private final Path rootPath;
    private final Consumer<Path> legacyFilesystemApiAccessCheck;
    private final Path filePath;

    FilesystemPathImpl(Path filePath, Path rootPath, UnaryOperator<Path> pathSanitiser, Consumer<Path> legacyFilesystemApiAccessCheck) {
        this.pathSanitiser = Objects.requireNonNull(pathSanitiser);
        this.legacyFilesystemApiAccessCheck = Objects.requireNonNull(legacyFilesystemApiAccessCheck);
        this.filePath = Objects.requireNonNull(filePath);
        this.rootPath = Objects.requireNonNull(rootPath);
    }

    Path getFilePath() {
        return this.filePath;
    }

    public Path asJavaPath() {
        this.legacyFilesystemApiAccessCheck.accept(this.filePath);
        return this.filePath;
    }

    public FilesystemPath.Pruner deleteFileAndPrune() throws IOException {
        Files.delete(this.filePath);
        return new EmptyDirectoryPruner();
    }

    private FilesystemPathImpl subPath(Path subpath) {
        return new FilesystemPathImpl((Path)this.pathSanitiser.apply(subpath), this.rootPath, this.pathSanitiser, this.legacyFilesystemApiAccessCheck);
    }

    public FilesystemPath path(String ... pathComponents) {
        if (pathComponents.length == 0) {
            return this;
        }
        return this.subPath(this.resolve(pathComponents));
    }

    private Path resolve(String ... pathComponents) {
        if (pathComponents.length == 1) {
            return this.filePath.resolve(Paths.get(pathComponents[0], new String[0]));
        }
        return this.filePath.resolve(Paths.get(pathComponents[0], Arrays.copyOfRange(pathComponents, 1, pathComponents.length)));
    }

    public FileStore.Reader fileReader() {
        return this::openInputStream;
    }

    private InputStream openInputStream() throws IOException {
        return Files.newInputStream(this.filePath, new OpenOption[0]);
    }

    public FileStore.Writer fileWriter() {
        return new FilesystemPathWriter(this.filePath);
    }

    public void deleteFile() throws IOException {
        Files.delete(this.filePath);
    }

    public boolean tryDeleteFile() {
        try {
            return Files.deleteIfExists(this.filePath);
        }
        catch (IOException ex) {
            log.warn("Failed to delete file {}", (Object)this, (Object)ex);
            return false;
        }
    }

    public void moveFile(FileStore.Path toFile) throws IOException {
        if (toFile instanceof FilesystemPath) {
            try {
                this.moveFile((FilesystemPath)toFile);
            }
            catch (AtomicMoveNotSupportedException ex) {
                log.debug("Atomic move failed moving {} to {}, falling back to copy-and-delete", new Object[]{this, toFile, ex});
                this.copyAndDelete(toFile);
            }
        } else {
            this.copyAndDelete(toFile);
        }
    }

    public void copyFile(FileStore.Path toFile) throws IOException {
        if (toFile instanceof FilesystemPath) {
            this.copyFile((FilesystemPathImpl)toFile);
        } else {
            toFile.fileWriter().write(outputStream -> Files.copy(this.filePath, outputStream));
        }
    }

    private void moveFile(FilesystemPath toFile) throws IOException {
        FilesystemPathImpl.ensureParentExists(toFile);
        Files.move(this.filePath, toFile.asJavaPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    private void copyFile(FilesystemPath toFile) throws IOException {
        FilesystemPathImpl.ensureParentExists(toFile);
        Files.copy(this.filePath, toFile.asJavaPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void ensureParentExists(FilesystemPath file) throws IOException {
        Path parentDir = file.asJavaPath().getParent();
        if (parentDir != null) {
            Files.createDirectories(parentDir, new FileAttribute[0]);
        }
    }

    private void copyAndDelete(FileStore.Path toFile) throws IOException {
        this.copyFile(toFile);
        this.deleteFile();
    }

    public boolean fileExists() {
        return Files.isReadable(this.filePath) && !Files.isDirectory(this.filePath, new LinkOption[0]);
    }

    public boolean exists() {
        return Files.exists(this.filePath, new LinkOption[0]);
    }

    public DataSize getFileSize() throws IOException {
        return DataSize.ofBytes((long)Files.size(this.filePath));
    }

    public String getPathName() {
        return this.rootPath.relativize(this.filePath).toString();
    }

    public Optional<String> getLeafName() {
        return Optional.ofNullable(this.filePath.getFileName()).map(Path::toString);
    }

    public Stream<FilesystemPath> getFileDescendents() throws IOException {
        if (Files.isDirectory(this.filePath, new LinkOption[0])) {
            return Files.walk(this.filePath, new FileVisitOption[0]).filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).map(this::subPath);
        }
        return Stream.empty();
    }

    public Optional<FilesystemPath> getParent() {
        return Optional.ofNullable(this.filePath.getParent()).map(this::subPath);
    }

    public Snapshot snapshot() throws IOException {
        if (Files.isDirectory(this.filePath, new LinkOption[0])) {
            return DirectorySnapshot.copyOf((Path)this.filePath);
        }
        return EmptySnapshot.INSTANCE;
    }

    public void unpack(Snapshot snapshot) throws IOException {
        snapshot.unpack(this.filePath);
    }

    public String toString() {
        return this.filePath.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FilesystemPathImpl that = (FilesystemPathImpl)o;
        return this.filePath.equals(that.filePath);
    }

    public int hashCode() {
        return Objects.hash(this.filePath);
    }

    private final class EmptyDirectoryPruner
    implements FilesystemPath.Pruner {
        private EmptyDirectoryPruner() {
        }

        public void untilReach(FilesystemPath parentDir) {
            Predicate<Path> until = dir -> dir.equals(parentDir.asJavaPath()) || dir.equals(FilesystemPathImpl.this.rootPath);
            try {
                this.prune(FilesystemPathImpl.this.filePath.getParent(), until);
            }
            catch (IOException e) {
                log.warn("Failed to prune empty directories above {}", (Object)FilesystemPathImpl.this.filePath, (Object)e);
            }
        }

        private void prune(@Nullable Path directory, Predicate<Path> until) throws IOException {
            if (directory != null && !until.test(directory) && Files.isDirectory(directory, new LinkOption[0]) && this.isEmpty(directory)) {
                log.debug("Removing empty directory {}", (Object)directory);
                Files.delete(directory);
                this.prune(directory.getParent(), until);
            }
        }

        private boolean isEmpty(Path directory) throws IOException {
            return Files.list(directory).count() == 0L;
        }
    }
}

