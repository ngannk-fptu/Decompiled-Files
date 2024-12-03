/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Pair
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.Chunk;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.ChunkExtensionProvider;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.ChunkHeaderProvider;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.Resettable;
import software.amazon.awssdk.http.auth.aws.internal.signer.chunkedencoding.TrailerProvider;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class ChunkedEncodedInputStream
extends InputStream {
    private static final Logger LOG = Logger.loggerFor(ChunkedEncodedInputStream.class);
    private static final byte[] CRLF = new byte[]{13, 10};
    private static final byte[] END = new byte[0];
    private final InputStream inputStream;
    private final int chunkSize;
    private final ChunkHeaderProvider header;
    private final List<ChunkExtensionProvider> extensions = new ArrayList<ChunkExtensionProvider>();
    private final List<TrailerProvider> trailers = new ArrayList<TrailerProvider>();
    private Chunk currentChunk;
    private boolean isFinished = false;

    private ChunkedEncodedInputStream(Builder builder) {
        this.inputStream = (InputStream)Validate.notNull((Object)builder.inputStream, (String)"Input-Stream cannot be null!", (Object[])new Object[0]);
        this.chunkSize = Validate.isPositive((int)builder.chunkSize, (String)"Chunk-size must be greater than 0!");
        this.header = (ChunkHeaderProvider)Validate.notNull((Object)builder.header, (String)"Header cannot be null!", (Object[])new Object[0]);
        this.extensions.addAll((Collection)Validate.notNull((Object)builder.extensions, (String)"Extensions cannot be null!", (Object[])new Object[0]));
        this.trailers.addAll((Collection)Validate.notNull((Object)builder.trailers, (String)"Trailers cannot be null!", (Object[])new Object[0]));
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int read() throws IOException {
        if (this.currentChunk == null || !this.currentChunk.hasRemaining() && !this.isFinished) {
            this.currentChunk = this.getChunk(this.inputStream);
        }
        return this.currentChunk.stream().read();
    }

    private Chunk getChunk(InputStream stream) throws IOException {
        byte[] chunkData;
        int read;
        LOG.debug(() -> "Reading next chunk.");
        if (this.currentChunk != null) {
            this.currentChunk.close();
        }
        if ((read = this.read(stream, chunkData = new byte[this.chunkSize], this.chunkSize)) > 0) {
            return this.getNextChunk(Arrays.copyOf(chunkData, read));
        }
        LOG.debug(() -> "End of backing stream reached. Reading final chunk.");
        this.isFinished = true;
        return this.getFinalChunk();
    }

    private int read(InputStream inputStream, byte[] buf, int maxBytesToRead) throws IOException {
        int read;
        int offset = 0;
        do {
            read = inputStream.read(buf, offset, maxBytesToRead - offset);
            assert (read != 0);
            if (read <= 0) continue;
            offset += read;
        } while (read > 0 && offset < maxBytesToRead);
        return offset;
    }

    private Chunk getNextChunk(byte[] data) throws IOException {
        ByteArrayOutputStream chunkStream = new ByteArrayOutputStream();
        this.writeChunk(data, chunkStream);
        chunkStream.write(CRLF);
        byte[] newChunkData = chunkStream.toByteArray();
        return Chunk.create(new ByteArrayInputStream(newChunkData), newChunkData.length);
    }

    private Chunk getFinalChunk() throws IOException {
        ByteArrayOutputStream chunkStream = new ByteArrayOutputStream();
        this.writeChunk(END, chunkStream);
        this.writeTrailers(chunkStream);
        chunkStream.write(CRLF);
        byte[] newChunkData = chunkStream.toByteArray();
        return Chunk.create(new ByteArrayInputStream(newChunkData), newChunkData.length);
    }

    private void writeChunk(byte[] chunk, ByteArrayOutputStream outputStream) throws IOException {
        this.writeHeader(chunk, outputStream);
        this.writeExtensions(chunk, outputStream);
        outputStream.write(CRLF);
        outputStream.write(chunk);
    }

    private void writeHeader(byte[] chunk, ByteArrayOutputStream outputStream) throws IOException {
        byte[] hdr = this.header.get(chunk);
        outputStream.write(hdr);
    }

    private void writeExtensions(byte[] chunk, ByteArrayOutputStream outputStream) throws IOException {
        for (ChunkExtensionProvider chunkExtensionProvider : this.extensions) {
            Pair<byte[], byte[]> ext = chunkExtensionProvider.get(chunk);
            outputStream.write(59);
            outputStream.write((byte[])ext.left());
            outputStream.write(61);
            outputStream.write((byte[])ext.right());
        }
    }

    private void writeTrailers(ByteArrayOutputStream outputStream) throws IOException {
        for (TrailerProvider trailer : this.trailers) {
            Pair<String, List<String>> tlr = trailer.get();
            outputStream.write(((String)tlr.left()).getBytes(StandardCharsets.UTF_8));
            outputStream.write(58);
            outputStream.write(String.join((CharSequence)",", (Iterable)tlr.right()).getBytes(StandardCharsets.UTF_8));
            outputStream.write(CRLF);
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        this.trailers.forEach(Resettable::reset);
        this.extensions.forEach(Resettable::reset);
        this.header.reset();
        this.inputStream.reset();
        this.isFinished = false;
        this.currentChunk = null;
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

    public static class Builder {
        private final List<ChunkExtensionProvider> extensions = new ArrayList<ChunkExtensionProvider>();
        private final List<TrailerProvider> trailers = new ArrayList<TrailerProvider>();
        private InputStream inputStream;
        private int chunkSize;
        private ChunkHeaderProvider header = chunk -> Integer.toHexString(chunk.length).getBytes(StandardCharsets.UTF_8);

        public InputStream inputStream() {
            return this.inputStream;
        }

        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder chunkSize(int chunkSize) {
            this.chunkSize = chunkSize;
            return this;
        }

        public Builder header(ChunkHeaderProvider header) {
            this.header = header;
            return this;
        }

        public Builder extensions(List<ChunkExtensionProvider> extensions) {
            this.extensions.clear();
            extensions.forEach(this::addExtension);
            return this;
        }

        public Builder addExtension(ChunkExtensionProvider extension) {
            this.extensions.add((ChunkExtensionProvider)Validate.notNull((Object)extension, (String)"ExtensionProvider cannot be null!", (Object[])new Object[0]));
            return this;
        }

        public List<TrailerProvider> trailers() {
            return new ArrayList<TrailerProvider>(this.trailers);
        }

        public Builder trailers(List<TrailerProvider> trailers) {
            this.trailers.clear();
            trailers.forEach(this::addTrailer);
            return this;
        }

        public Builder addTrailer(TrailerProvider trailer) {
            this.trailers.add((TrailerProvider)Validate.notNull((Object)trailer, (String)"TrailerProvider cannot be null!", (Object[])new Object[0]));
            return this;
        }

        public ChunkedEncodedInputStream build() {
            return new ChunkedEncodedInputStream(this);
        }
    }
}

