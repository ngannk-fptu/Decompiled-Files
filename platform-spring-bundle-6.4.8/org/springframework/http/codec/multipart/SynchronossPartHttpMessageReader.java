/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.synchronoss.cloud.nio.multipart.DefaultPartBodyStreamStorageFactory
 *  org.synchronoss.cloud.nio.multipart.Multipart
 *  org.synchronoss.cloud.nio.multipart.MultipartContext
 *  org.synchronoss.cloud.nio.multipart.MultipartUtils
 *  org.synchronoss.cloud.nio.multipart.NioMultipartParser
 *  org.synchronoss.cloud.nio.multipart.NioMultipartParserListener
 *  org.synchronoss.cloud.nio.multipart.PartBodyStreamStorageFactory
 *  org.synchronoss.cloud.nio.stream.storage.NameAwarePurgableFileInputStream
 *  org.synchronoss.cloud.nio.stream.storage.StreamStorage
 *  reactor.core.CoreSubscriber
 *  reactor.core.publisher.BaseSubscriber
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.FluxSink
 *  reactor.core.publisher.Mono
 *  reactor.core.publisher.SignalType
 *  reactor.core.scheduler.Schedulers
 */
package org.springframework.http.codec.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.synchronoss.cloud.nio.multipart.DefaultPartBodyStreamStorageFactory;
import org.synchronoss.cloud.nio.multipart.Multipart;
import org.synchronoss.cloud.nio.multipart.MultipartContext;
import org.synchronoss.cloud.nio.multipart.MultipartUtils;
import org.synchronoss.cloud.nio.multipart.NioMultipartParser;
import org.synchronoss.cloud.nio.multipart.NioMultipartParserListener;
import org.synchronoss.cloud.nio.multipart.PartBodyStreamStorageFactory;
import org.synchronoss.cloud.nio.stream.storage.NameAwarePurgableFileInputStream;
import org.synchronoss.cloud.nio.stream.storage.StreamStorage;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

public class SynchronossPartHttpMessageReader
extends LoggingCodecSupport
implements HttpMessageReader<Part> {
    private static final String FILE_STORAGE_DIRECTORY_PREFIX = "synchronoss-file-upload-";
    private int maxInMemorySize = 262144;
    private long maxDiskUsagePerPart = -1L;
    private int maxParts = -1;
    private final AtomicReference<Path> fileStorageDirectory = new AtomicReference();

    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    public void setMaxDiskUsagePerPart(long maxDiskUsagePerPart) {
        this.maxDiskUsagePerPart = maxDiskUsagePerPart;
    }

    public long getMaxDiskUsagePerPart() {
        return this.maxDiskUsagePerPart;
    }

    public void setMaxParts(int maxParts) {
        this.maxParts = maxParts;
    }

    public int getMaxParts() {
        return this.maxParts;
    }

    public void setFileStorageDirectory(Path fileStorageDirectory) throws IOException {
        Assert.notNull((Object)fileStorageDirectory, "FileStorageDirectory must not be null");
        if (!Files.exists(fileStorageDirectory, new LinkOption[0])) {
            Files.createDirectory(fileStorageDirectory, new FileAttribute[0]);
        }
        this.fileStorageDirectory.set(fileStorageDirectory);
    }

    @Override
    public List<MediaType> getReadableMediaTypes() {
        return MultipartHttpMessageReader.MIME_TYPES;
    }

    @Override
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        if (Part.class.equals(elementType.toClass())) {
            if (mediaType == null) {
                return true;
            }
            for (MediaType supportedMediaType : this.getReadableMediaTypes()) {
                if (!supportedMediaType.isCompatibleWith(mediaType)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public Flux<Part> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return this.getFileStorageDirectory().flatMapMany(directory -> Flux.create((Consumer)new SynchronossPartGenerator(message, (Path)directory)).doOnNext(part -> {
            if (!Hints.isLoggingSuppressed(hints)) {
                LogFormatUtils.traceDebug(this.logger, traceOn -> Hints.getLogPrefix(hints) + "Parsed " + (this.isEnableLoggingRequestDetails() ? LogFormatUtils.formatValue(part, traceOn == false) : "parts '" + part.name() + "' (content masked)"));
            }
        }));
    }

    @Override
    public Mono<Part> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Mono.error((Throwable)new UnsupportedOperationException("Cannot read multipart request body into single Part"));
    }

    private Mono<Path> getFileStorageDirectory() {
        return Mono.defer(() -> {
            Path directory = this.fileStorageDirectory.get();
            if (directory != null) {
                return Mono.just((Object)directory);
            }
            return Mono.fromCallable(() -> {
                Path tempDirectory = Files.createTempDirectory(FILE_STORAGE_DIRECTORY_PREFIX, new FileAttribute[0]);
                if (this.fileStorageDirectory.compareAndSet(null, tempDirectory)) {
                    return tempDirectory;
                }
                try {
                    Files.delete(tempDirectory);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                return this.fileStorageDirectory.get();
            }).subscribeOn(Schedulers.boundedElastic());
        });
    }

    private static class SynchronossFormFieldPart
    extends AbstractSynchronossPart
    implements FormFieldPart {
        private final String content;

        SynchronossFormFieldPart(HttpHeaders headers, String content) {
            super(headers);
            this.content = content;
        }

        @Override
        public String value() {
            return this.content;
        }

        @Override
        public Flux<DataBuffer> content() {
            byte[] bytes = this.content.getBytes(this.getCharset());
            return Flux.just((Object)DefaultDataBufferFactory.sharedInstance.wrap(bytes));
        }

        private Charset getCharset() {
            String name = MultipartUtils.getCharEncoding((Map)this.headers());
            return name != null ? Charset.forName(name) : StandardCharsets.UTF_8;
        }

        @Override
        public String toString() {
            return "Part '" + this.name() + "=" + this.content + "'";
        }
    }

    private static class SynchronossFilePart
    extends SynchronossPart
    implements FilePart {
        private static final OpenOption[] FILE_CHANNEL_OPTIONS = new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE};
        private final String filename;

        SynchronossFilePart(HttpHeaders headers, String filename, StreamStorage storage) {
            super(headers, storage);
            this.filename = filename;
        }

        @Override
        public String filename() {
            return this.filename;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Mono<Void> transferTo(Path dest) {
            ReadableByteChannel input = null;
            FileChannel output = null;
            try {
                long written;
                input = Channels.newChannel(this.getStorage().getInputStream());
                output = FileChannel.open(dest, FILE_CHANNEL_OPTIONS);
                long size = input instanceof FileChannel ? ((FileChannel)input).size() : Long.MAX_VALUE;
                for (long totalWritten = 0L; totalWritten < size; totalWritten += written) {
                    written = output.transferFrom(input, totalWritten, size - totalWritten);
                    if (written > 0L) continue;
                    break;
                }
            }
            catch (IOException ex) {
                Mono mono = Mono.error((Throwable)ex);
                return mono;
            }
            finally {
                if (input != null) {
                    try {
                        input.close();
                    }
                    catch (IOException iOException) {}
                }
                if (output != null) {
                    try {
                        output.close();
                    }
                    catch (IOException iOException) {}
                }
            }
            return Mono.empty();
        }

        @Override
        public String toString() {
            return "Part '" + this.name() + "', filename='" + this.filename + "'";
        }
    }

    private static class SynchronossPart
    extends AbstractSynchronossPart {
        private final StreamStorage storage;

        SynchronossPart(HttpHeaders headers, StreamStorage storage) {
            super(headers);
            Assert.notNull((Object)storage, "StreamStorage is required");
            this.storage = storage;
        }

        @Override
        public Flux<DataBuffer> content() {
            return DataBufferUtils.readInputStream(() -> ((StreamStorage)this.getStorage()).getInputStream(), DefaultDataBufferFactory.sharedInstance, 4096);
        }

        protected StreamStorage getStorage() {
            return this.storage;
        }

        @Override
        public Mono<Void> delete() {
            return Mono.fromRunnable(() -> {
                File file = this.getFile();
                if (file != null) {
                    file.delete();
                }
            });
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Nullable
        private File getFile() {
            InputStream inputStream = null;
            try {
                inputStream = this.getStorage().getInputStream();
                if (inputStream instanceof NameAwarePurgableFileInputStream) {
                    NameAwarePurgableFileInputStream stream = (NameAwarePurgableFileInputStream)inputStream;
                    File file = stream.getFile();
                    return file;
                }
            }
            finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (IOException iOException) {}
                }
            }
            return null;
        }
    }

    private static abstract class AbstractSynchronossPart
    implements Part {
        private final String name;
        private final HttpHeaders headers;

        AbstractSynchronossPart(HttpHeaders headers) {
            Assert.notNull((Object)headers, "HttpHeaders is required");
            this.name = MultipartUtils.getFieldName((Map)headers);
            this.headers = headers;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public HttpHeaders headers() {
            return this.headers;
        }

        public String toString() {
            return "Part '" + this.name + "', headers=" + this.headers;
        }
    }

    private static class FluxSinkAdapterListener
    implements NioMultipartParserListener {
        private final FluxSink<Part> sink;
        private final MultipartContext context;
        private final LimitedPartBodyStreamStorageFactory storageFactory;
        private final AtomicInteger terminated = new AtomicInteger();

        FluxSinkAdapterListener(FluxSink<Part> sink, MultipartContext context, LimitedPartBodyStreamStorageFactory factory) {
            this.sink = sink;
            this.context = context;
            this.storageFactory = factory;
        }

        public void onPartFinished(StreamStorage storage, Map<String, List<String>> headers) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.putAll((Map<? extends String, ? extends List<String>>)headers);
            this.storageFactory.partFinished();
            this.sink.next((Object)this.createPart(storage, httpHeaders));
        }

        private Part createPart(StreamStorage storage, HttpHeaders httpHeaders) {
            String filename = MultipartUtils.getFileName((Map)httpHeaders);
            if (filename != null) {
                return new SynchronossFilePart(httpHeaders, filename, storage);
            }
            if (MultipartUtils.isFormField((Map)httpHeaders, (MultipartContext)this.context)) {
                String value = MultipartUtils.readFormParameterValue((StreamStorage)storage, (Map)httpHeaders);
                return new SynchronossFormFieldPart(httpHeaders, value);
            }
            return new SynchronossPart(httpHeaders, storage);
        }

        public void onError(String message, Throwable cause) {
            if (this.terminated.getAndIncrement() == 0) {
                this.sink.error((Throwable)new DecodingException(message, cause));
            }
        }

        public void onAllPartsFinished() {
            if (this.terminated.getAndIncrement() == 0) {
                this.sink.complete();
            }
        }

        public void onNestedPartStarted(Map<String, List<String>> headersFromParentPart) {
        }

        public void onNestedPartFinished() {
        }
    }

    private class LimitedPartBodyStreamStorageFactory
    implements PartBodyStreamStorageFactory {
        private final PartBodyStreamStorageFactory storageFactory;
        private int index;
        private boolean isFilePart;
        private long partSize;

        private LimitedPartBodyStreamStorageFactory() {
            this.storageFactory = SynchronossPartHttpMessageReader.this.maxInMemorySize > 0 ? new DefaultPartBodyStreamStorageFactory(SynchronossPartHttpMessageReader.this.maxInMemorySize) : new DefaultPartBodyStreamStorageFactory();
            this.index = 1;
        }

        public int getCurrentPartIndex() {
            return this.index;
        }

        public StreamStorage newStreamStorageForPartBody(Map<String, List<String>> headers, int index) {
            this.index = index;
            this.isFilePart = MultipartUtils.getFileName(headers) != null;
            this.partSize = 0L;
            if (SynchronossPartHttpMessageReader.this.maxParts > 0 && index > SynchronossPartHttpMessageReader.this.maxParts) {
                throw new DecodingException("Too many parts: Part[" + index + "] but maxParts=" + SynchronossPartHttpMessageReader.this.maxParts);
            }
            return this.storageFactory.newStreamStorageForPartBody(headers, index);
        }

        public void increaseByteCount(long byteCount) {
            this.partSize += byteCount;
            if (SynchronossPartHttpMessageReader.this.maxInMemorySize > 0 && !this.isFilePart && this.partSize >= (long)SynchronossPartHttpMessageReader.this.maxInMemorySize) {
                throw new DataBufferLimitException("Part[" + this.index + "] exceeded the in-memory limit of " + SynchronossPartHttpMessageReader.this.maxInMemorySize + " bytes");
            }
            if (SynchronossPartHttpMessageReader.this.maxDiskUsagePerPart > 0L && this.isFilePart && this.partSize > SynchronossPartHttpMessageReader.this.maxDiskUsagePerPart) {
                throw new DecodingException("Part[" + this.index + "] exceeded the disk usage limit of " + SynchronossPartHttpMessageReader.this.maxDiskUsagePerPart + " bytes");
            }
        }

        public void partFinished() {
            ++this.index;
            this.isFilePart = false;
            this.partSize = 0L;
        }
    }

    private class SynchronossPartGenerator
    extends BaseSubscriber<DataBuffer>
    implements Consumer<FluxSink<Part>> {
        private final ReactiveHttpInputMessage inputMessage;
        private final LimitedPartBodyStreamStorageFactory storageFactory;
        private final Path fileStorageDirectory;
        @Nullable
        private NioMultipartParserListener listener;
        @Nullable
        private NioMultipartParser parser;

        public SynchronossPartGenerator(ReactiveHttpInputMessage inputMessage, Path fileStorageDirectory) {
            this.storageFactory = new LimitedPartBodyStreamStorageFactory();
            this.inputMessage = inputMessage;
            this.fileStorageDirectory = fileStorageDirectory;
        }

        @Override
        public void accept(FluxSink<Part> sink) {
            HttpHeaders headers = this.inputMessage.getHeaders();
            MediaType mediaType = headers.getContentType();
            Assert.state(mediaType != null, "No content type set");
            int length = this.getContentLength(headers);
            Charset charset = Optional.ofNullable(mediaType.getCharset()).orElse(StandardCharsets.UTF_8);
            MultipartContext context = new MultipartContext(mediaType.toString(), length, charset.name());
            this.listener = new FluxSinkAdapterListener(sink, context, this.storageFactory);
            this.parser = Multipart.multipart((MultipartContext)context).saveTemporaryFilesTo(this.fileStorageDirectory.toString()).usePartBodyStreamStorageFactory((PartBodyStreamStorageFactory)this.storageFactory).forNIO(this.listener);
            this.inputMessage.getBody().subscribe((CoreSubscriber)this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void hookOnNext(DataBuffer buffer) {
            Assert.state(this.parser != null && this.listener != null, "Not initialized yet");
            int size = buffer.readableByteCount();
            this.storageFactory.increaseByteCount(size);
            byte[] resultBytes = new byte[size];
            buffer.read(resultBytes);
            try {
                this.parser.write(resultBytes);
            }
            catch (IOException ex) {
                this.cancel();
                int index = this.storageFactory.getCurrentPartIndex();
                this.listener.onError("Parser error for part [" + index + "]", (Throwable)ex);
            }
            finally {
                DataBufferUtils.release(buffer);
            }
        }

        protected void hookOnError(Throwable ex) {
            if (this.listener != null) {
                int index = this.storageFactory.getCurrentPartIndex();
                this.listener.onError("Failure while parsing part[" + index + "]", ex);
            }
        }

        protected void hookOnComplete() {
            if (this.listener != null) {
                this.listener.onAllPartsFinished();
            }
        }

        protected void hookFinally(SignalType type) {
            try {
                if (this.parser != null) {
                    this.parser.close();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }

        private int getContentLength(HttpHeaders headers) {
            long length = headers.getContentLength();
            return (long)((int)length) == length ? (int)length : -1;
        }
    }
}

