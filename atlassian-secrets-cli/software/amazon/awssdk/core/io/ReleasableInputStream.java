/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.io;

import java.io.FileInputStream;
import java.io.InputStream;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.internal.io.Releasable;
import software.amazon.awssdk.core.io.ResettableInputStream;
import software.amazon.awssdk.core.io.SdkFilterInputStream;
import software.amazon.awssdk.utils.Logger;

@NotThreadSafe
@SdkProtectedApi
public class ReleasableInputStream
extends SdkFilterInputStream {
    private static final Logger log = Logger.loggerFor(ReleasableInputStream.class);
    private boolean closeDisabled;

    protected ReleasableInputStream(InputStream is) {
        super(is);
    }

    public static ReleasableInputStream wrap(InputStream is) {
        if (is instanceof ReleasableInputStream) {
            return (ReleasableInputStream)is;
        }
        if (is instanceof FileInputStream) {
            return ResettableInputStream.newResettableInputStream((FileInputStream)is);
        }
        return new ReleasableInputStream(is);
    }

    @Override
    public final void close() {
        if (!this.closeDisabled) {
            this.doRelease();
        }
    }

    @Override
    public final void release() {
        this.doRelease();
    }

    private void doRelease() {
        try {
            this.in.close();
        }
        catch (Exception ex) {
            log.debug(() -> "Ignore failure in closing the input stream", ex);
        }
        if (this.in instanceof Releasable) {
            Releasable r = (Releasable)((Object)this.in);
            r.release();
        }
        this.abortIfNeeded();
    }

    public final boolean isCloseDisabled() {
        return this.closeDisabled;
    }

    public final <T extends ReleasableInputStream> T disableClose() {
        this.closeDisabled = true;
        ReleasableInputStream t = this;
        return (T)t;
    }
}

