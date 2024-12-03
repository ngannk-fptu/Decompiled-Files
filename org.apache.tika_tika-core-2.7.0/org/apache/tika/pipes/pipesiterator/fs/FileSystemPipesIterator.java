/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.pipes.pipesiterator.fs;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.tika.config.Field;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.Param;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.async.AsyncProcessor;
import org.apache.tika.pipes.emitter.EmitKey;
import org.apache.tika.pipes.fetcher.FetchKey;
import org.apache.tika.pipes.pipesiterator.PipesIterator;
import org.apache.tika.pipes.pipesiterator.TotalCountResult;
import org.apache.tika.pipes.pipesiterator.TotalCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemPipesIterator
extends PipesIterator
implements TotalCounter,
Initializable,
Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncProcessor.class);
    private Path basePath;
    private boolean countTotal = false;
    private FileCountWorker fileCountWorker;

    public FileSystemPipesIterator() {
    }

    public FileSystemPipesIterator(Path basePath) {
        this.basePath = basePath;
    }

    @Field
    public void setBasePath(String basePath) {
        this.basePath = Paths.get(basePath, new String[0]);
    }

    @Override
    protected void enqueue() throws InterruptedException, IOException, TimeoutException {
        if (!Files.isDirectory(this.basePath, new LinkOption[0])) {
            throw new IllegalArgumentException("\"basePath\" directory does not exist: " + this.basePath.toAbsolutePath());
        }
        try {
            Files.walkFileTree(this.basePath, new FSFileVisitor(this.getFetcherName(), this.getEmitterName()));
        }
        catch (IOException e) {
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof TimeoutException) {
                throw (TimeoutException)cause;
            }
            throw e;
        }
    }

    @Override
    public void checkInitialization(InitializableProblemHandler problemHandler) throws TikaConfigException {
        TikaConfig.mustNotBeEmpty("basePath", this.basePath);
        TikaConfig.mustNotBeEmpty("fetcherName", this.getFetcherName());
        TikaConfig.mustNotBeEmpty("emitterName", this.getFetcherName());
    }

    @Override
    public void initialize(Map<String, Param> params) throws TikaConfigException {
        if (this.countTotal) {
            this.fileCountWorker = new FileCountWorker(this.basePath);
        }
    }

    @Field
    public void setCountTotal(boolean countTotal) {
        this.countTotal = countTotal;
    }

    @Override
    public void startTotalCount() {
        if (!this.countTotal) {
            return;
        }
        this.fileCountWorker.startTotalCount();
    }

    @Override
    public TotalCountResult getTotalCount() {
        if (!this.countTotal) {
            return TotalCountResult.UNSUPPORTED;
        }
        return this.fileCountWorker.getTotalCount();
    }

    @Override
    public void close() throws IOException {
        if (this.fileCountWorker != null) {
            this.fileCountWorker.close();
        }
    }

    private static class FileCountWorker
    implements TotalCounter,
    Closeable {
        private Thread totalCounterThread;
        private final AtomicLong totalCount = new AtomicLong(0L);
        private TotalCountResult.STATUS status;
        private TotalCountResult finalResult;
        private final Path basePath;

        public FileCountWorker(Path basePath) {
            this.basePath = basePath;
            this.status = TotalCountResult.STATUS.NOT_COMPLETED;
        }

        @Override
        public void startTotalCount() {
            this.totalCounterThread = new Thread(() -> {
                try {
                    Files.walkFileTree(this.basePath, new FSFileCounter(this.totalCount));
                    this.status = TotalCountResult.STATUS.COMPLETED;
                    this.finalResult = new TotalCountResult(this.totalCount.get(), this.status);
                }
                catch (IOException e) {
                    LOG.warn("problem counting files", (Throwable)e);
                    this.status = TotalCountResult.STATUS.EXCEPTION;
                    this.finalResult = new TotalCountResult(this.totalCount.get(), this.status);
                }
            });
            this.totalCounterThread.setDaemon(true);
            this.totalCounterThread.start();
        }

        @Override
        public TotalCountResult getTotalCount() {
            if (this.finalResult != null) {
                return this.finalResult;
            }
            return new TotalCountResult(this.totalCount.get(), this.status);
        }

        @Override
        public void close() throws IOException {
            this.totalCounterThread.interrupt();
        }

        private class FSFileCounter
        implements FileVisitor<Path> {
            private final AtomicLong count;

            private FSFileCounter(AtomicLong count) {
                this.count = count;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                this.count.incrementAndGet();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        }
    }

    private class FSFileVisitor
    implements FileVisitor<Path> {
        private final String fetcherName;
        private final String emitterName;

        private FSFileVisitor(String fetcherName, String emitterName) {
            this.fetcherName = fetcherName;
            this.emitterName = emitterName;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            String relPath = FileSystemPipesIterator.this.basePath.relativize(file).toString();
            try {
                FileSystemPipesIterator.this.tryToAdd(new FetchEmitTuple(relPath, new FetchKey(this.fetcherName, relPath), new EmitKey(this.emitterName, relPath), new Metadata(), FileSystemPipesIterator.this.getHandlerConfig(), FileSystemPipesIterator.this.getOnParseException()));
            }
            catch (TimeoutException e) {
                throw new IOException(e);
            }
            catch (InterruptedException e) {
                return FileVisitResult.TERMINATE;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}

