/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.filecache;

import com.mchange.v1.io.InputStreamUtils;
import com.mchange.v1.io.OutputStreamUtils;
import com.mchange.v2.io.DirectoryDescentUtils;
import com.mchange.v2.io.FileIterator;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v3.filecache.FileCacheKey;
import com.mchange.v3.filecache.FileNotCachedException;
import com.mchange.v3.filecache.URLFetcher;
import com.mchange.v3.filecache.URLFetchers;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class FileCache {
    static final MLogger logger = MLog.getLogger(FileCache.class);
    final File cacheDir;
    final int buffer_size;
    final boolean read_only;
    final List<URLFetcher> fetchers;
    static final FileFilter NOT_DIR_FF = new FileFilter(){

        @Override
        public boolean accept(File file) {
            return !file.isDirectory();
        }
    };

    private InputStream fetchURL(URL uRL) throws IOException {
        LinkedList<IOException> linkedList = null;
        for (URLFetcher uRLFetcher : this.fetchers) {
            try {
                return uRLFetcher.openStream(uRL, logger);
            }
            catch (FileNotFoundException fileNotFoundException) {
                throw fileNotFoundException;
            }
            catch (IOException iOException) {
                if (logger.isLoggable(MLevel.FINE)) {
                    logger.log(MLevel.FINE, "URLFetcher " + uRLFetcher + " failed on Exception. Will try next fetcher, if any.", iOException);
                }
                if (linkedList == null) {
                    linkedList = new LinkedList<IOException>();
                }
                linkedList.add(iOException);
            }
        }
        if (logger.isLoggable(MLevel.WARNING)) {
            logger.log(MLevel.WARNING, "All URLFetchers failed on URL " + uRL);
            int n = linkedList.size();
            for (int i = 0; i < n; ++i) {
                logger.log(MLevel.WARNING, "URLFetcher Exception #" + (i + 1), (Throwable)linkedList.get(i));
            }
        }
        throw new IOException("Failed to fetch URL '" + uRL + "'.");
    }

    public FileCache(File file, int n, boolean bl) throws IOException {
        this(file, n, bl, Collections.singletonList(URLFetchers.DEFAULT));
    }

    public FileCache(File file, int n, boolean bl, URLFetcher ... uRLFetcherArray) throws IOException {
        this(file, n, bl, Arrays.asList(uRLFetcherArray));
    }

    public FileCache(File file, int n, boolean bl, List<URLFetcher> list) throws IOException {
        this.cacheDir = file;
        this.buffer_size = n;
        this.read_only = bl;
        this.fetchers = Collections.unmodifiableList(list);
        if (file.exists()) {
            if (!file.isDirectory()) {
                this.loggedIOException(MLevel.SEVERE, file + "exists and is not a directory. Can't use as cacheDir.");
            } else if (!file.canRead()) {
                this.loggedIOException(MLevel.SEVERE, file + "must be readable.");
            } else if (!file.canWrite() && !bl) {
                this.loggedIOException(MLevel.SEVERE, file + "not writable, and not read only.");
            }
        } else if (!file.mkdir()) {
            this.loggedIOException(MLevel.SEVERE, file + "does not exist and could not be created.");
        }
    }

    public void ensureCached(FileCacheKey fileCacheKey, boolean bl) throws IOException {
        block13: {
            File file;
            block11: {
                block12: {
                    BufferedOutputStream bufferedOutputStream;
                    BufferedInputStream bufferedInputStream;
                    block10: {
                        file = this.file(fileCacheKey);
                        if (this.read_only) break block11;
                        if (!bl && file.exists()) break block12;
                        bufferedInputStream = null;
                        bufferedOutputStream = null;
                        try {
                            File file2;
                            if (logger.isLoggable(MLevel.FINE)) {
                                logger.log(MLevel.FINE, "Caching file for " + fileCacheKey + " to " + file.getAbsolutePath() + "...");
                            }
                            if (!(file2 = file.getParentFile()).exists()) {
                                file2.mkdirs();
                            }
                            bufferedInputStream = new BufferedInputStream(this.fetchURL(fileCacheKey.getURL()), this.buffer_size);
                            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file), this.buffer_size);
                            int n = ((InputStream)bufferedInputStream).read();
                            while (n >= 0) {
                                ((OutputStream)bufferedOutputStream).write(n);
                                n = ((InputStream)bufferedInputStream).read();
                            }
                            if (!logger.isLoggable(MLevel.INFO)) break block10;
                            logger.log(MLevel.INFO, "Cached file for " + fileCacheKey + ".");
                        }
                        catch (IOException iOException) {
                            try {
                                logger.log(MLevel.WARNING, "An exception occurred while caching file for " + fileCacheKey + ". Deleting questionable cached file.", iOException);
                                file.delete();
                                throw iOException;
                            }
                            catch (Throwable throwable) {
                                InputStreamUtils.attemptClose(bufferedInputStream);
                                OutputStreamUtils.attemptClose(bufferedOutputStream);
                                throw throwable;
                            }
                        }
                    }
                    InputStreamUtils.attemptClose(bufferedInputStream);
                    OutputStreamUtils.attemptClose(bufferedOutputStream);
                    break block13;
                }
                if (logger.isLoggable(MLevel.FINE)) {
                    logger.log(MLevel.FINE, "File for " + fileCacheKey + " already exists and force_reacquire is not set.");
                }
                break block13;
            }
            if (bl) {
                String string = "force_reacquire canot be set on a read_only FileCache.";
                IllegalArgumentException illegalArgumentException = new IllegalArgumentException(string);
                logger.log(MLevel.WARNING, string, illegalArgumentException);
                throw illegalArgumentException;
            }
            if (!file.exists()) {
                String string = "Cache is read only, and file for key '" + fileCacheKey + "' does not exist.";
                FileNotCachedException fileNotCachedException = new FileNotCachedException(string);
                logger.log(MLevel.FINE, string, fileNotCachedException);
                throw fileNotCachedException;
            }
        }
    }

    public InputStream fetch(FileCacheKey fileCacheKey, boolean bl) throws IOException {
        this.ensureCached(fileCacheKey, bl);
        return new FileInputStream(this.file(fileCacheKey));
    }

    public boolean isCached(FileCacheKey fileCacheKey) throws IOException {
        return this.file(fileCacheKey).exists();
    }

    public int countCached() throws IOException {
        int n = 0;
        FileIterator fileIterator = DirectoryDescentUtils.depthFirstEagerDescent(this.cacheDir, NOT_DIR_FF, false);
        while (fileIterator.hasNext()) {
            fileIterator.next();
            ++n;
        }
        return n;
    }

    public int countCached(FileFilter fileFilter) throws IOException {
        int n = 0;
        FileIterator fileIterator = DirectoryDescentUtils.depthFirstEagerDescent(this.cacheDir, new NotDirAndFileFilter(fileFilter), false);
        while (fileIterator.hasNext()) {
            fileIterator.next();
            ++n;
        }
        return n;
    }

    public File fileForKey(FileCacheKey fileCacheKey) {
        return this.file(fileCacheKey);
    }

    private File file(FileCacheKey fileCacheKey) {
        return new File(this.cacheDir, fileCacheKey.getCacheFilePath());
    }

    private void loggedIOException(MLevel mLevel, String string) throws IOException {
        IOException iOException = new IOException(string);
        logger.log(mLevel, string, iOException);
        throw iOException;
    }

    static class NotDirAndFileFilter
    implements FileFilter {
        FileFilter ff;

        NotDirAndFileFilter(FileFilter fileFilter) {
            this.ff = fileFilter;
        }

        @Override
        public boolean accept(File file) {
            return !file.isDirectory() && this.ff.accept(file);
        }
    }
}

