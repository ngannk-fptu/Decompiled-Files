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
 *  org.synchronoss.cloud.nio.stream.storage.StreamStorage
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.FluxSink
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.codec.multipart;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
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
import org.synchronoss.cloud.nio.stream.storage.StreamStorage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

public class SynchronossPartHttpMessageReader
implements HttpMessageReader<Part> {
    private final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
    private final PartBodyStreamStorageFactory streamStorageFactory = new DefaultPartBodyStreamStorageFactory();

    @Override
    public List<MediaType> getReadableMediaTypes() {
        return Collections.singletonList(MediaType.MULTIPART_FORM_DATA);
    }

    @Override
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return Part.class.equals(elementType.resolve(Object.class)) && (mediaType == null || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(mediaType));
    }

    @Override
    public Flux<Part> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Flux.create((Consumer)new SynchronossPartGenerator(message, this.bufferFactory, this.streamStorageFactory));
    }

    @Override
    public Mono<Part> readMono(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Mono.error((Throwable)new UnsupportedOperationException("Cannot read multipart request body into single Part"));
    }

    private static class SynchronossFormFieldPart
    extends AbstractSynchronossPart
    implements FormFieldPart {
        private final String content;

        SynchronossFormFieldPart(HttpHeaders headers, DataBufferFactory bufferFactory, String content) {
            super(headers, bufferFactory);
            this.content = content;
        }

        @Override
        public String value() {
            return this.content;
        }

        @Override
        public Flux<DataBuffer> content() {
            byte[] bytes = this.content.getBytes(this.getCharset());
            DataBuffer buffer = this.getBufferFactory().allocateBuffer(bytes.length);
            buffer.write(bytes);
            return Flux.just((Object)buffer);
        }

        private Charset getCharset() {
            String name = MultipartUtils.getCharEncoding((Map)this.headers());
            return name != null ? Charset.forName(name) : StandardCharsets.UTF_8;
        }
    }

    private static class SynchronossFilePart
    extends SynchronossPart
    implements FilePart {
        private static final OpenOption[] FILE_CHANNEL_OPTIONS = new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE};
        private final String filename;

        SynchronossFilePart(HttpHeaders headers, String filename, StreamStorage storage, DataBufferFactory factory) {
            super(headers, storage, factory);
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
        public Mono<Void> transferTo(File destination) {
            ReadableByteChannel input = null;
            AbstractInterruptibleChannel output = null;
            try {
                long written;
                input = Channels.newChannel(this.getStorage().getInputStream());
                output = FileChannel.open(destination.toPath(), FILE_CHANNEL_OPTIONS);
                long size = input instanceof FileChannel ? ((FileChannel)input).size() : Long.MAX_VALUE;
                for (long totalWritten = 0L; totalWritten < size; totalWritten += written) {
                    written = ((FileChannel)output).transferFrom(input, totalWritten, size - totalWritten);
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
    }

    private static class SynchronossPart
    extends AbstractSynchronossPart {
        private final StreamStorage storage;

        SynchronossPart(HttpHeaders headers, StreamStorage storage, DataBufferFactory factory) {
            super(headers, factory);
            Assert.notNull((Object)storage, "StreamStorage is required");
            this.storage = storage;
        }

        @Override
        public Flux<DataBuffer> content() {
            return DataBufferUtils.readInputStream(() -> ((StreamStorage)this.getStorage()).getInputStream(), this.getBufferFactory(), 4096);
        }

        protected StreamStorage getStorage() {
            return this.storage;
        }
    }

    private static abstract class AbstractSynchronossPart
    implements Part {
        private final String name;
        private final HttpHeaders headers;
        private final DataBufferFactory bufferFactory;

        AbstractSynchronossPart(HttpHeaders headers, DataBufferFactory bufferFactory) {
            Assert.notNull((Object)headers, "HttpHeaders is required");
            Assert.notNull((Object)bufferFactory, "DataBufferFactory is required");
            this.name = MultipartUtils.getFieldName((Map)headers);
            this.headers = headers;
            this.bufferFactory = bufferFactory;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public HttpHeaders headers() {
            return this.headers;
        }

        DataBufferFactory getBufferFactory() {
            return this.bufferFactory;
        }
    }

    private static class FluxSinkAdapterListener
    implements NioMultipartParserListener {
        private final FluxSink<Part> sink;
        private final DataBufferFactory bufferFactory;
        private final MultipartContext context;
        private final AtomicInteger terminated = new AtomicInteger(0);

        FluxSinkAdapterListener(FluxSink<Part> sink, DataBufferFactory factory, MultipartContext context) {
            this.sink = sink;
            this.bufferFactory = factory;
            this.context = context;
        }

        public void onPartFinished(StreamStorage storage, Map<String, List<String>> headers) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.putAll((Map<? extends String, ? extends List<String>>)headers);
            this.sink.next((Object)this.createPart(storage, httpHeaders));
        }

        private Part createPart(StreamStorage storage, HttpHeaders httpHeaders) {
            String filename = MultipartUtils.getFileName((Map)httpHeaders);
            if (filename != null) {
                return new SynchronossFilePart(httpHeaders, filename, storage, this.bufferFactory);
            }
            if (MultipartUtils.isFormField((Map)httpHeaders, (MultipartContext)this.context)) {
                String value = MultipartUtils.readFormParameterValue((StreamStorage)storage, (Map)httpHeaders);
                return new SynchronossFormFieldPart(httpHeaders, this.bufferFactory, value);
            }
            return new SynchronossPart(httpHeaders, storage, this.bufferFactory);
        }

        public void onError(String message, Throwable cause) {
            if (this.terminated.getAndIncrement() == 0) {
                this.sink.error((Throwable)new RuntimeException(message, cause));
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

    private static class SynchronossPartGenerator
    implements Consumer<FluxSink<Part>> {
        private final ReactiveHttpInputMessage inputMessage;
        private final DataBufferFactory bufferFactory;
        private final PartBodyStreamStorageFactory streamStorageFactory;

        SynchronossPartGenerator(ReactiveHttpInputMessage inputMessage, DataBufferFactory bufferFactory, PartBodyStreamStorageFactory streamStorageFactory) {
            this.inputMessage = inputMessage;
            this.bufferFactory = bufferFactory;
            this.streamStorageFactory = streamStorageFactory;
        }

        @Override
        public void accept(FluxSink<Part> emitter) {
            HttpHeaders headers = this.inputMessage.getHeaders();
            MediaType mediaType = headers.getContentType();
            Assert.state(mediaType != null, "No content type set");
            int length = this.getContentLength(headers);
            Charset charset = Optional.ofNullable(mediaType.getCharset()).orElse(StandardCharsets.UTF_8);
            MultipartContext context = new MultipartContext(mediaType.toString(), length, charset.name());
            FluxSinkAdapterListener listener = new FluxSinkAdapterListener(emitter, this.bufferFactory, context);
            NioMultipartParser parser = Multipart.multipart((MultipartContext)context).usePartBodyStreamStorageFactory(this.streamStorageFactory).forNIO((NioMultipartParserListener)listener);
            this.inputMessage.getBody().subscribe(buffer -> {
                byte[] resultBytes = new byte[buffer.readableByteCount()];
                buffer.read(resultBytes);
                try {
                    parser.write(resultBytes);
                }
                catch (IOException ex) {
                    listener.onError("Exception thrown providing input to the parser", ex);
                }
                finally {
                    DataBufferUtils.release(buffer);
                }
            }, ex -> {
                try {
                    listener.onError("Request body input error", (Throwable)ex);
                    parser.close();
                }
                catch (IOException ex2) {
                    listener.onError("Exception thrown while closing the parser", ex2);
                }
            }, () -> {
                try {
                    parser.close();
                }
                catch (IOException ex) {
                    listener.onError("Exception thrown while closing the parser", ex);
                }
            });
        }

        private int getContentLength(HttpHeaders headers) {
            long length = headers.getContentLength();
            return (long)((int)length) == length ? (int)length : -1;
        }
    }
}

