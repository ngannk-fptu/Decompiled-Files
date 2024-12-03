/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.event.DeliveryMode;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.event.SDKProgressPublisher;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener;
import java.util.concurrent.Future;
import org.apache.commons.logging.LogFactory;

public class S3ProgressPublisher
extends SDKProgressPublisher {
    public static Future<?> publishTransferPersistable(ProgressListener listener, PersistableTransfer persistableTransfer) {
        if (persistableTransfer == null || !(listener instanceof S3ProgressListener)) {
            return null;
        }
        S3ProgressListener s3listener = (S3ProgressListener)listener;
        return S3ProgressPublisher.deliverEvent(s3listener, persistableTransfer);
    }

    private static Future<?> deliverEvent(final S3ProgressListener listener, final PersistableTransfer persistableTransfer) {
        DeliveryMode mode;
        if (listener instanceof DeliveryMode && (mode = (DeliveryMode)((Object)listener)).isSyncCallSafe()) {
            return S3ProgressPublisher.quietlyCallListener(listener, persistableTransfer);
        }
        return S3ProgressPublisher.setLatestFutureTask(S3ProgressPublisher.getExecutorService().submit(new Runnable(){

            @Override
            public void run() {
                listener.onPersistableTransfer(persistableTransfer);
            }
        }));
    }

    private static Future<?> quietlyCallListener(S3ProgressListener listener, PersistableTransfer persistableTransfer) {
        try {
            listener.onPersistableTransfer(persistableTransfer);
        }
        catch (Throwable t) {
            LogFactory.getLog(S3ProgressPublisher.class).debug((Object)"Failure from the event listener", t);
        }
        return null;
    }
}

