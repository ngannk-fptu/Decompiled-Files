/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.sync;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.internal.sync.FileContentStreamProvider;
import software.amazon.awssdk.core.internal.util.Mimetype;
import software.amazon.awssdk.core.io.ReleasableInputStream;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class RequestBody {
    private final ContentStreamProvider contentStreamProvider;
    private final Long contentLength;
    private final String contentType;

    private RequestBody(ContentStreamProvider contentStreamProvider, Long contentLength, String contentType) {
        this.contentStreamProvider = (ContentStreamProvider)Validate.paramNotNull((Object)contentStreamProvider, (String)"contentStreamProvider");
        this.contentLength = contentLength != null ? Long.valueOf(Validate.isNotNegative((long)contentLength, (String)"Content-length")) : null;
        this.contentType = (String)Validate.paramNotNull((Object)contentType, (String)"contentType");
    }

    public ContentStreamProvider contentStreamProvider() {
        return this.contentStreamProvider;
    }

    @Deprecated
    public long contentLength() {
        Validate.validState((this.contentLength != null ? 1 : 0) != 0, (String)"Content length is invalid, please use optionalContentLength() for your case.", (Object[])new Object[0]);
        return this.contentLength;
    }

    public Optional<Long> optionalContentLength() {
        return Optional.ofNullable(this.contentLength);
    }

    public String contentType() {
        return this.contentType;
    }

    public static RequestBody fromFile(Path path) {
        return new RequestBody(new FileContentStreamProvider(path), (Long)FunctionalUtils.invokeSafely(() -> Files.size(path)), Mimetype.getInstance().getMimetype(path));
    }

    public static RequestBody fromFile(File file) {
        return RequestBody.fromFile(file.toPath());
    }

    public static RequestBody fromInputStream(InputStream inputStream, long contentLength) {
        IoUtils.markStreamWithMaxReadLimit((InputStream)inputStream);
        InputStream nonCloseable = RequestBody.nonCloseableInputStream(inputStream);
        return RequestBody.fromContentProvider(() -> {
            if (nonCloseable.markSupported()) {
                FunctionalUtils.invokeSafely(nonCloseable::reset);
            }
            return nonCloseable;
        }, contentLength, "application/octet-stream");
    }

    public static RequestBody fromString(String contents, Charset cs) {
        return RequestBody.fromBytesDirect(contents.getBytes(cs), "text/plain; charset=" + cs.name());
    }

    public static RequestBody fromString(String contents) {
        return RequestBody.fromString(contents, StandardCharsets.UTF_8);
    }

    public static RequestBody fromBytes(byte[] bytes) {
        return RequestBody.fromBytesDirect(Arrays.copyOf(bytes, bytes.length));
    }

    public static RequestBody fromByteBuffer(ByteBuffer byteBuffer) {
        return RequestBody.fromBytesDirect(BinaryUtils.copyAllBytesFrom((ByteBuffer)byteBuffer));
    }

    public static RequestBody fromRemainingByteBuffer(ByteBuffer byteBuffer) {
        return RequestBody.fromBytesDirect(BinaryUtils.copyRemainingBytesFrom((ByteBuffer)byteBuffer));
    }

    public static RequestBody empty() {
        return RequestBody.fromBytesDirect(new byte[0]);
    }

    public static RequestBody fromContentProvider(ContentStreamProvider provider, long contentLength, String mimeType) {
        return new RequestBody(provider, contentLength, mimeType);
    }

    public static RequestBody fromContentProvider(ContentStreamProvider provider, String mimeType) {
        return new RequestBody(provider, null, mimeType);
    }

    private static RequestBody fromBytesDirect(byte[] bytes) {
        return RequestBody.fromBytesDirect(bytes, "application/octet-stream");
    }

    private static RequestBody fromBytesDirect(byte[] bytes, String mimetype) {
        return RequestBody.fromContentProvider(() -> new ByteArrayInputStream(bytes), bytes.length, mimetype);
    }

    private static InputStream nonCloseableInputStream(InputStream inputStream) {
        return inputStream != null ? (InputStream)ReleasableInputStream.wrap(inputStream).disableClose() : null;
    }
}

