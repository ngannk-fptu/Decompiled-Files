/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.internal;

import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.internal.Releasable;
import com.amazonaws.internal.ResettableInputStream;
import com.amazonaws.internal.SdkFilterInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@NotThreadSafe
public class ReleasableInputStream
extends SdkFilterInputStream
implements Releasable {
    private static final Log log = LogFactory.getLog(ReleasableInputStream.class);
    private boolean closeDisabled;

    protected ReleasableInputStream(InputStream is) {
        super(is);
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
        block3: {
            try {
                this.in.close();
            }
            catch (Exception ex) {
                if (!log.isDebugEnabled()) break block3;
                log.debug((Object)"FYI", (Throwable)ex);
            }
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

    public static ReleasableInputStream wrap(InputStream is) {
        if (is instanceof ReleasableInputStream) {
            return (ReleasableInputStream)is;
        }
        if (is instanceof FileInputStream) {
            return ResettableInputStream.newResettableInputStream((FileInputStream)is);
        }
        return new ReleasableInputStream(is);
    }
}

