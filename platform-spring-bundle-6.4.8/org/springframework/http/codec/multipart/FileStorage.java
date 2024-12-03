/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Scheduler
 */
package org.springframework.http.codec.multipart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

abstract class FileStorage {
    private static final Log logger = LogFactory.getLog(FileStorage.class);

    protected FileStorage() {
    }

    public abstract Mono<Path> directory();

    public static FileStorage fromPath(Path path) throws IOException {
        if (!Files.exists(path, new LinkOption[0])) {
            Files.createDirectory(path, new FileAttribute[0]);
        }
        return new PathFileStorage(path);
    }

    public static FileStorage tempDirectory(Supplier<Scheduler> scheduler) {
        return new TempFileStorage(scheduler);
    }

    private static final class TempFileStorage
    extends FileStorage {
        private static final String IDENTIFIER = "spring-multipart-";
        private final Supplier<Scheduler> scheduler;
        private volatile Mono<Path> directory = TempFileStorage.tempDirectory();

        public TempFileStorage(Supplier<Scheduler> scheduler) {
            this.scheduler = scheduler;
        }

        @Override
        public Mono<Path> directory() {
            return this.directory.flatMap(this::createNewDirectoryIfDeleted).subscribeOn(this.scheduler.get());
        }

        private Mono<Path> createNewDirectoryIfDeleted(Path directory) {
            if (!Files.exists(directory, new LinkOption[0])) {
                Mono<Path> newDirectory = TempFileStorage.tempDirectory();
                this.directory = newDirectory;
                return newDirectory;
            }
            return Mono.just((Object)directory);
        }

        private static Mono<Path> tempDirectory() {
            return Mono.fromCallable(() -> {
                Path directory = Files.createTempDirectory(IDENTIFIER, new FileAttribute[0]);
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Created temporary storage directory: " + directory));
                }
                return directory;
            }).cache();
        }
    }

    private static final class PathFileStorage
    extends FileStorage {
        private final Mono<Path> directory;

        public PathFileStorage(Path directory) {
            this.directory = Mono.just((Object)directory);
        }

        @Override
        public Mono<Path> directory() {
            return this.directory;
        }
    }
}

