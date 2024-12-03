/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.io;

import java.io.IOException;
import java.io.InputStream;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.AbortedException;
import software.amazon.awssdk.core.internal.io.Releasable;
import software.amazon.awssdk.utils.IoUtils;

@SdkProtectedApi
public abstract class SdkInputStream
extends InputStream
implements Releasable {
    protected abstract InputStream getWrappedInputStream();

    protected final void abortIfNeeded() {
        if (Thread.currentThread().isInterrupted()) {
            try {
                this.abort();
            }
            catch (IOException e) {
                LoggerFactory.getLogger(this.getClass()).debug("FYI", e);
            }
            throw AbortedException.builder().build();
        }
    }

    protected void abort() throws IOException {
    }

    @Override
    public void release() {
        IoUtils.closeQuietly(this, null);
        InputStream in = this.getWrappedInputStream();
        if (in instanceof Releasable) {
            Releasable r = (Releasable)((Object)in);
            r.release();
        }
    }
}

