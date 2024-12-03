/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil
 *  org.apache.lucene.index.DirectoryReader
 *  org.apache.lucene.store.Directory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneIndexHelper {
    private static final long BYTES_PER_MB = 0x100000L;
    private static final Logger log = LoggerFactory.getLogger(LuceneIndexHelper.class);

    public static long sizeOfIndex(Path indexDirectory) throws IOException {
        long totalSize = 0L;
        for (Path file : LuceneIndexHelper.listRegularFilesNonRecursive(indexDirectory)) {
            totalSize += Files.size(file);
        }
        return totalSize;
    }

    public static long sizeOfIndexMB(Path indexDirectory) {
        try {
            return LuceneIndexHelper.sizeOfIndex(indexDirectory) / 0x100000L;
        }
        catch (IOException e) {
            log.warn("Cannot determine size of index located at {}", (Object)indexDirectory.toString(), (Object)e);
            return 0L;
        }
    }

    public static Optional<Long> getIndexVersion(Path indexDirectory) {
        Optional<Long> optional;
        block8: {
            DirectoryReader directoryReader = DirectoryReader.open((Directory)DirectoryUtil.getDirectory((File)indexDirectory.toFile()));
            try {
                optional = Optional.of(directoryReader.getVersion());
                if (directoryReader == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (directoryReader != null) {
                        try {
                            directoryReader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    log.warn("Index could not be read", (Throwable)e);
                    return Optional.empty();
                }
            }
            directoryReader.close();
        }
        return optional;
    }

    public static void replaceIndexDirectory(Path destination, Path source) throws IOException {
        for (Path indexFile : LuceneIndexHelper.listRegularFilesNonRecursive(destination)) {
            Files.delete(indexFile);
        }
        for (Path indexFile : LuceneIndexHelper.listRegularFilesNonRecursive(source)) {
            Files.move(indexFile, destination.resolve(indexFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static List<Path> listRegularFilesNonRecursive(Path directory) throws IOException {
        try (Stream<Path> files = Files.list(directory);){
            List<Path> list = files.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).collect(Collectors.toList());
            return list;
        }
    }
}

