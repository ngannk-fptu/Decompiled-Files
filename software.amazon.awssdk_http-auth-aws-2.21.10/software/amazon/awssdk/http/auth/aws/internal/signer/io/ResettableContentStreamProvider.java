/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;

@SdkInternalApi
public class ResettableContentStreamProvider
implements ContentStreamProvider {
    private final Supplier<InputStream> streamSupplier;
    private InputStream currentStream;

    public ResettableContentStreamProvider(Supplier<InputStream> streamSupplier) {
        this.streamSupplier = streamSupplier;
    }

    public InputStream newStream() {
        try {
            this.reset();
        }
        catch (IOException e) {
            throw new RuntimeException("Could not create new stream: ", e);
        }
        return this.currentStream;
    }

    private void reset() throws IOException {
        if (this.currentStream != null) {
            this.currentStream.reset();
        } else {
            this.currentStream = this.streamSupplier.get();
        }
    }
}

