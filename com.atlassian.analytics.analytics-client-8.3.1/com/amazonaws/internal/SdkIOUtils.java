/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.internal;

import com.amazonaws.log.InternalLogApi;
import com.amazonaws.log.InternalLogFactory;
import java.io.Closeable;
import java.io.IOException;

enum SdkIOUtils {

    private static final InternalLogApi defaultLog = InternalLogFactory.getLog(SdkIOUtils.class);

    static void closeQuietly(Closeable is) {
        SdkIOUtils.closeQuietly(is, null);
    }

    static void closeQuietly(Closeable is, InternalLogApi log) {
        block3: {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException ex) {
                    InternalLogApi logger;
                    InternalLogApi internalLogApi = logger = log == null ? defaultLog : log;
                    if (!logger.isDebugEnabled()) break block3;
                    logger.debug("Ignore failure in closing the Closeable", ex);
                }
            }
        }
    }
}

