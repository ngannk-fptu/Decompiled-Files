/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.IoUtils
 */
package software.amazon.awssdk.http.auth.aws.internal.signer.io;

import java.io.Closeable;
import org.slf4j.Logger;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.IoUtils;

@SdkInternalApi
public interface Releasable {
    public static void release(Closeable is, Logger log) {
        IoUtils.closeQuietly((AutoCloseable)is, (Logger)log);
        if (is instanceof Releasable) {
            Releasable r = (Releasable)((Object)is);
            r.release();
        }
    }

    public void release();
}

