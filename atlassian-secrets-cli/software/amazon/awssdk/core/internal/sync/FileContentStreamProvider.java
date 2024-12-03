/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.sync;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
public final class FileContentStreamProvider
implements ContentStreamProvider {
    private final Path filePath;
    private InputStream currentStream;

    public FileContentStreamProvider(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public InputStream newStream() {
        this.closeCurrentStream();
        this.currentStream = FunctionalUtils.invokeSafely(() -> Files.newInputStream(this.filePath, new OpenOption[0]));
        return this.currentStream;
    }

    private void closeCurrentStream() {
        if (this.currentStream != null) {
            FunctionalUtils.invokeSafely(this.currentStream::close);
            this.currentStream = null;
        }
    }
}

