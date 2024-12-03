/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  org.apache.commons.io.FileUtils
 *  org.apache.lucene.index.DirectoryReader
 *  org.apache.lucene.index.IndexDeletionPolicy
 *  org.apache.lucene.index.IndexFormatTooOldException
 *  org.apache.lucene.index.IndexNotFoundException
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.index.IndexWriterConfig
 *  org.apache.lucene.index.IndexWriterConfig$OpenMode
 *  org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy
 *  org.apache.lucene.index.MergePolicy
 *  org.apache.lucene.index.MergeScheduler
 *  org.apache.lucene.index.SerialMergeScheduler
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.TieredMergePolicy
 *  org.apache.lucene.search.Collector
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 *  org.apache.lucene.search.TotalHitCountCollector
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.store.FSDirectory
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask.splitindex;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneConstants;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.FileInfo;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.IndexInfo;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.SplitIndexException;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.SplitIndexPhase;
import com.atlassian.confluence.util.zip.FileUnzipper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.ZipEntry;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.index.SerialMergeScheduler;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TieredMergePolicy;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SplitIndexFileHelper {
    private static final Logger log = LoggerFactory.getLogger(SplitIndexFileHelper.class);
    private static final String PHASE_FILE = "index-split.pid";
    private static final String[] SIZES = new String[]{"bytes", "KB", "MB", "GB", "TB"};
    private static final String CONFLUENCE_DOCUMENT_TYPE = "confluence-document-type";
    private static final String CONTENT = "CONTENT";
    private static final String CHANGE = "CHANGE";
    private static final Query CHANGES_QUERY = new TermQuery(new Term("confluence-document-type", "CHANGE"));
    private static final Query CONTENT_QUERY = new TermQuery(new Term("confluence-document-type", "CONTENT"));
    private final String indexLocation;
    private final File index;
    private final File phaseFile;
    private final String name;

    public SplitIndexFileHelper(@NonNull String location, @NonNull String name) {
        this(new File(location), name);
    }

    public SplitIndexFileHelper(@NonNull File index, @NonNull String name) {
        this.index = Objects.requireNonNull(index);
        this.name = Objects.requireNonNull(name);
        this.indexLocation = index.getAbsolutePath();
        if (!index.exists() || !index.isDirectory()) {
            throw new IllegalArgumentException("Provided index location is not a directory");
        }
        this.phaseFile = new File(index, PHASE_FILE);
    }

    public File getIndex() {
        return this.index;
    }

    public Map<String, FileInfo> collectIndexFileInfos() {
        return SplitIndexFileHelper.collectIndexFileInfos(this.index);
    }

    public SplitIndexPhase getPhase() {
        if (this.phaseFile.exists()) {
            try {
                List<String> lines = Files.readAllLines(this.phaseFile.toPath(), StandardCharsets.UTF_8);
                if (lines.isEmpty()) {
                    return null;
                }
                return SplitIndexPhase.fromId(lines.get(0));
            }
            catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    public void setPhase(@NonNull SplitIndexPhase phase) {
        try {
            Files.write(this.phaseFile.toPath(), phase.getId().getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (IOException e) {
            throw new SplitIndexException(String.format("Unable to write state file '%s', upgrade can't continue", this.phaseFile.getAbsolutePath()), e);
        }
    }

    public void removePhase() {
        if (this.phaseFile.exists()) {
            try {
                Files.delete(this.phaseFile.toPath());
            }
            catch (IOException e) {
                throw new SplitIndexException(String.format("Unable to remove state file '%s', upgrade can't continue", this.phaseFile.getAbsolutePath()), e);
            }
        }
    }

    public static Map<String, FileInfo> collectIndexFileInfos(@NonNull File folder) {
        HashMap<String, FileInfo> result = new HashMap<String, FileInfo>();
        try (@NonNull FSDirectory directory = FSDirectory.open((File)folder);){
            for (String name : directory.listAll()) {
                File file = new File(folder.getAbsolutePath(), name);
                long size = directory.fileLength(name);
                result.put(name, new FileInfo(name, file, size));
            }
        }
        catch (IOException e) {
            throw new SplitIndexException(String.format("Unable to collect index file information for path %s", folder.getAbsolutePath()), e);
        }
        return result;
    }

    public static void copyFilesToFolder(@NonNull Collection<FileInfo> source, @NonNull File target) {
        if (!target.exists() && !target.mkdirs()) {
            throw new SplitIndexException(String.format("Unable to create target folder %s", target.getAbsolutePath()));
        }
        source.forEach(fi -> {
            File copy = new File(target, fi.getName());
            try {
                Files.copy(fi.getFile().toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }
            catch (IOException e) {
                throw new SplitIndexException(String.format("Unable to copy file '%s` to '%s'", fi.getFile().getAbsolutePath(), copy.getAbsolutePath()), e);
            }
        });
    }

    public static long getTotalSize(@NonNull Collection<FileInfo> files) {
        return files.stream().mapToLong(FileInfo::getSize).sum();
    }

    public static String bytesToHumanReadable(long bytes) {
        double size;
        int index = 0;
        for (size = (double)bytes; size >= 1024.0 && index < SIZES.length - 1; size /= 1024.0, ++index) {
        }
        if ((size = Math.ceil(10.0 * size) / 10.0) >= 100.0 && (size = Math.ceil(size)) >= 1024.0 && index < SIZES.length - 2) {
            size /= 1024.0;
            ++index;
        }
        return String.format("%s %s", new DecimalFormat("#.#").format(size), SIZES[index]);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public @NonNull IndexInfo refreshIndexInfo() {
        try (@NonNull FSDirectory dir = FSDirectory.open((File)this.index);){
            IndexInfo indexInfo;
            block16: {
                @NonNull DirectoryReader reader = DirectoryReader.open((Directory)dir);
                try {
                    IndexSearcher searcher = new IndexSearcher((IndexReader)reader);
                    int content = this.countDocuments(searcher, CONTENT_QUERY);
                    int changes = this.countDocuments(searcher, CHANGES_QUERY);
                    IndexInfo indexInfo2 = IndexInfo.builder().withValid(true).withDocuments(reader.numDocs()).withMax(reader.maxDoc()).withDeleted(reader.numDeletedDocs()).withContent(content).withChanges(changes).build();
                    log.info("index at {}: {}", (Object)this.indexLocation, (Object)indexInfo2);
                    indexInfo = indexInfo2;
                    if (reader == null) break block16;
                }
                catch (Throwable throwable) {
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                reader.close();
            }
            return indexInfo;
        }
        catch (IndexFormatTooOldException e) {
            return IndexInfo.builder().withValid(false).withOldVersion(true).build();
        }
        catch (IndexNotFoundException e) {
            return IndexInfo.builder().withValid(false).withEmptyFolder(true).build();
        }
        catch (IOException e) {
            throw new SplitIndexException(String.format("Unexpected IOException while opening index at %s", this.indexLocation), e);
        }
    }

    private int countDocuments(@NonNull IndexSearcher searcher, @NonNull Query query) throws IOException {
        TotalHitCountCollector collector = new TotalHitCountCollector();
        searcher.search(query, (Collector)collector);
        return collector.getTotalHits();
    }

    public static boolean isLuceneIndex(@NonNull File location) {
        boolean bl;
        block8: {
            @NonNull FSDirectory dir = FSDirectory.open((File)location);
            try {
                bl = DirectoryReader.indexExists((Directory)dir);
                if (dir == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if (dir != null) {
                        try {
                            dir.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    return false;
                }
            }
            dir.close();
        }
        return bl;
    }

    public static boolean hasFiles(File location) {
        return Optional.ofNullable(location).map(file -> file.listFiles(File::isFile)).map(l -> ((File[])l).length > 0).orElse(false);
    }

    public void purgeContent() {
        this.deleteDocuments(CONTENT_QUERY);
    }

    public void purgeChanges() {
        this.deleteDocuments(CHANGES_QUERY);
    }

    public void createEmptyIndexIfNone() {
        IndexWriterConfig config = SplitIndexFileHelper.createDefaultConfig(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        try (@NonNull FSDirectory directory = FSDirectory.open((File)this.index);
             IndexWriter writer = new IndexWriter((Directory)directory, config);){
            writer.commit();
        }
        catch (IOException e) {
            throw new SplitIndexException(e.getMessage(), e);
        }
    }

    public void overrideWithEmptyIndex() {
        IndexWriterConfig config = SplitIndexFileHelper.createDefaultConfig(IndexWriterConfig.OpenMode.CREATE);
        try (@NonNull FSDirectory directory = FSDirectory.open((File)this.index);
             IndexWriter writer = new IndexWriter((Directory)directory, config);){
            writer.commit();
        }
        catch (IOException e) {
            throw new SplitIndexException(e.getMessage(), e);
        }
    }

    private void deleteDocuments(Query query) {
        IndexWriterConfig config = SplitIndexFileHelper.createDefaultConfig(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        DirectoryReader reader = null;
        try (@NonNull FSDirectory directory = FSDirectory.open((File)this.index);
             IndexWriter writer = new IndexWriter((Directory)directory, config);){
            reader = DirectoryReader.open((Directory)directory);
            log.info("before purge from {} -> num docs: {}, max: {}, deleted: {}", new Object[]{this.name, reader.numDocs(), reader.maxDoc(), reader.numDeletedDocs()});
            writer.deleteDocuments(query);
            writer.deleteUnusedFiles();
            writer.commit();
            reader = SplitIndexFileHelper.refresh(reader);
            log.info("after purge from {} -> num docs: {}, max: {}, deleted: {}", new Object[]{this.name, reader.numDocs(), reader.maxDoc(), reader.numDeletedDocs()});
        }
        catch (IOException e) {
            throw new SplitIndexException(e.getMessage(), e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    log.warn("couldn't close reader, ignoring");
                }
            }
        }
    }

    private static IndexWriterConfig createDefaultConfig(IndexWriterConfig.OpenMode openMode) {
        return new IndexWriterConfig(LuceneConstants.LUCENE_VERSION, null).setRAMBufferSizeMB(48.0).setMergePolicy((MergePolicy)new TieredMergePolicy()).setMergeScheduler((MergeScheduler)new SerialMergeScheduler()).setIndexDeletionPolicy((IndexDeletionPolicy)new KeepOnlyLastCommitDeletionPolicy()).setMaxBufferedDocs(-1).setOpenMode(openMode);
    }

    public static long getTotalZipEntriesSize(File zipFile) throws IOException {
        FileUnzipper unzipper = new FileUnzipper(zipFile, Files.createTempDirectory("split-index-unzip", new FileAttribute[0]).toFile());
        return SplitIndexFileHelper.getTotalZipEntriesSize(unzipper.entries());
    }

    public static long getTotalZipEntriesSize(ZipEntry[] entries) {
        return Arrays.stream(entries).map(ZipEntry::getSize).mapToLong(Long::longValue).sum();
    }

    public static DirectoryReader refresh(DirectoryReader reader) throws IOException {
        DirectoryReader old = reader;
        if ((reader = DirectoryReader.openIfChanged((DirectoryReader)reader)) == null) {
            return old;
        }
        if (reader != old) {
            old.close();
        }
        return reader;
    }

    public void purgeAllFiles() throws UpgradeException {
        try {
            FileUtils.cleanDirectory((File)this.getIndex());
        }
        catch (IOException e) {
            throw new UpgradeException((Throwable)e);
        }
    }
}

