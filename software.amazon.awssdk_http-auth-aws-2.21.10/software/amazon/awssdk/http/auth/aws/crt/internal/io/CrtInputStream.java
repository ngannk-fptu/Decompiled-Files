/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.crt.http.HttpRequestBodyStream
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.utils.FunctionalUtils
 */
package software.amazon.awssdk.http.auth.aws.crt.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.crt.http.HttpRequestBodyStream;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
public final class CrtInputStream
implements HttpRequestBodyStream {
    private static final int READ_BUFFER_SIZE = 4096;
    private final ContentStreamProvider provider;
    private final int bufSize;
    private final byte[] readBuffer;
    private InputStream providerStream;

    public CrtInputStream(ContentStreamProvider provider) {
        this.provider = provider;
        this.bufSize = 4096;
        this.readBuffer = new byte[this.bufSize];
    }

    public boolean sendRequestBody(ByteBuffer bodyBytesOut) {
        int toRead;
        int read;
        if (this.providerStream == null) {
            FunctionalUtils.invokeSafely(this::createNewStream);
        }
        if ((read = ((Integer)FunctionalUtils.invokeSafely(() -> this.lambda$sendRequestBody$0(toRead = Math.min(this.bufSize, bodyBytesOut.remaining())))).intValue()) > 0) {
            bodyBytesOut.put(this.readBuffer, 0, read);
        } else {
            FunctionalUtils.invokeSafely(this.providerStream::close);
        }
        return read < 0;
    }

    public boolean resetPosition() {
        if (this.provider == null) {
            throw new IllegalStateException("Cannot reset position while provider is null");
        }
        FunctionalUtils.invokeSafely(this::createNewStream);
        return true;
    }

    private void createNewStream() throws IOException {
        if (this.provider == null) {
            throw new IllegalStateException("Cannot create a new stream while provider is null");
        }
        if (this.providerStream != null) {
            this.providerStream.close();
        }
        this.providerStream = this.provider.newStream();
    }

    private /* synthetic */ Integer lambda$sendRequestBody$0(int toRead) throws Exception {
        return this.providerStream.read(this.readBuffer, 0, toRead);
    }
}

