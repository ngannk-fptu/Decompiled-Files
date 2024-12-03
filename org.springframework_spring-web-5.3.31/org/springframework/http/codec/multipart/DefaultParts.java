/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.core.io.buffer.DefaultDataBufferFactory
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Scheduler
 */
package org.springframework.http.codec.multipart;

import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.MultipartUtils;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

abstract class DefaultParts {
    DefaultParts() {
    }

    public static FormFieldPart formFieldPart(HttpHeaders headers, String value) {
        Assert.notNull((Object)headers, (String)"Headers must not be null");
        Assert.notNull((Object)value, (String)"Value must not be null");
        return new DefaultFormFieldPart(headers, value);
    }

    public static Part part(HttpHeaders headers, Flux<DataBuffer> dataBuffers) {
        Assert.notNull((Object)headers, (String)"Headers must not be null");
        Assert.notNull(dataBuffers, (String)"DataBuffers must not be null");
        return DefaultParts.partInternal(headers, new FluxContent(dataBuffers));
    }

    public static Part part(HttpHeaders headers, Path file, Scheduler scheduler) {
        Assert.notNull((Object)headers, (String)"Headers must not be null");
        Assert.notNull((Object)file, (String)"File must not be null");
        Assert.notNull((Object)scheduler, (String)"Scheduler must not be null");
        return DefaultParts.partInternal(headers, new FileContent(file, scheduler));
    }

    private static Part partInternal(HttpHeaders headers, Content content) {
        String filename = headers.getContentDisposition().getFilename();
        if (filename != null) {
            return new DefaultFilePart(headers, content);
        }
        return new DefaultPart(headers, content);
    }

    private static final class FileContent
    implements Content {
        private final Path file;
        private final Scheduler scheduler;

        public FileContent(Path file, Scheduler scheduler) {
            this.file = file;
            this.scheduler = scheduler;
        }

        @Override
        public Flux<DataBuffer> content() {
            return DataBufferUtils.readByteChannel(() -> Files.newByteChannel(this.file, StandardOpenOption.READ), (DataBufferFactory)DefaultDataBufferFactory.sharedInstance, (int)1024).subscribeOn(this.scheduler);
        }

        @Override
        public Mono<Void> transferTo(Path dest) {
            return this.blockingOperation(() -> Files.copy(this.file, dest, StandardCopyOption.REPLACE_EXISTING));
        }

        @Override
        public Mono<Void> delete() {
            return this.blockingOperation(() -> {
                Files.delete(this.file);
                return null;
            });
        }

        private Mono<Void> blockingOperation(Callable<?> callable) {
            return Mono.create(sink -> {
                try {
                    callable.call();
                    sink.success();
                }
                catch (Exception ex) {
                    sink.error((Throwable)ex);
                }
            }).subscribeOn(this.scheduler);
        }
    }

    private static final class FluxContent
    implements Content {
        private final Flux<DataBuffer> content;

        public FluxContent(Flux<DataBuffer> content) {
            this.content = content;
        }

        @Override
        public Flux<DataBuffer> content() {
            return this.content;
        }

        @Override
        public Mono<Void> transferTo(Path dest) {
            return DataBufferUtils.write(this.content, (Path)dest, (OpenOption[])new OpenOption[0]);
        }

        @Override
        public Mono<Void> delete() {
            return Mono.empty();
        }
    }

    private static interface Content {
        public Flux<DataBuffer> content();

        public Mono<Void> transferTo(Path var1);

        public Mono<Void> delete();
    }

    private static final class DefaultFilePart
    extends DefaultPart
    implements FilePart {
        public DefaultFilePart(HttpHeaders headers, Content content) {
            super(headers, content);
        }

        @Override
        public String filename() {
            String filename = this.headers().getContentDisposition().getFilename();
            Assert.state((filename != null ? 1 : 0) != 0, (String)"No filename found");
            return filename;
        }

        @Override
        public Mono<Void> transferTo(Path dest) {
            return this.content.transferTo(dest);
        }

        @Override
        public String toString() {
            ContentDisposition contentDisposition = this.headers().getContentDisposition();
            String name = contentDisposition.getName();
            String filename = contentDisposition.getFilename();
            if (name != null) {
                return "DefaultFilePart{" + name + " (" + filename + ")}";
            }
            return "DefaultFilePart{(" + filename + ")}";
        }
    }

    private static class DefaultPart
    extends AbstractPart {
        protected final Content content;

        public DefaultPart(HttpHeaders headers, Content content) {
            super(headers);
            this.content = content;
        }

        @Override
        public Flux<DataBuffer> content() {
            return this.content.content();
        }

        @Override
        public Mono<Void> delete() {
            return this.content.delete();
        }

        public String toString() {
            String name = this.headers().getContentDisposition().getName();
            if (name != null) {
                return "DefaultPart{" + name + "}";
            }
            return "DefaultPart";
        }
    }

    private static class DefaultFormFieldPart
    extends AbstractPart
    implements FormFieldPart {
        private final String value;

        public DefaultFormFieldPart(HttpHeaders headers, String value) {
            super(headers);
            this.value = value;
        }

        @Override
        public Flux<DataBuffer> content() {
            return Flux.defer(() -> {
                byte[] bytes = this.value.getBytes(MultipartUtils.charset(this.headers()));
                return Flux.just((Object)DefaultDataBufferFactory.sharedInstance.wrap(bytes));
            });
        }

        @Override
        public String value() {
            return this.value;
        }

        public String toString() {
            String name = this.headers().getContentDisposition().getName();
            if (name != null) {
                return "DefaultFormFieldPart{" + this.name() + "}";
            }
            return "DefaultFormFieldPart";
        }
    }

    private static abstract class AbstractPart
    implements Part {
        private final HttpHeaders headers;

        protected AbstractPart(HttpHeaders headers) {
            Assert.notNull((Object)headers, (String)"HttpHeaders is required");
            this.headers = headers;
        }

        @Override
        public String name() {
            String name = this.headers().getContentDisposition().getName();
            Assert.state((name != null ? 1 : 0) != 0, (String)"No name available");
            return name;
        }

        @Override
        public HttpHeaders headers() {
            return this.headers;
        }
    }
}

