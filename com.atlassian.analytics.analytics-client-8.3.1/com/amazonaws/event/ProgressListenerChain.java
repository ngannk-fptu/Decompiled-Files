/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.event;

import com.amazonaws.event.DeliveryMode;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventFilter;
import com.amazonaws.event.ProgressListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProgressListenerChain
implements ProgressListener,
DeliveryMode {
    private static final Log log = LogFactory.getLog(ProgressListenerChain.class);
    private final List<ProgressListener> listeners = new CopyOnWriteArrayList<ProgressListener>();
    private final ProgressEventFilter progressEventFilter;
    private volatile boolean syncCallSafe = true;

    public ProgressListenerChain(ProgressListener ... listeners) {
        this((ProgressEventFilter)null, listeners);
    }

    public ProgressListenerChain(ProgressEventFilter progressEventFilter, ProgressListener ... listeners) {
        if (listeners == null) {
            throw new IllegalArgumentException("Progress Listeners cannot be null.");
        }
        for (ProgressListener listener : listeners) {
            this.addProgressListener(listener);
        }
        this.progressEventFilter = progressEventFilter;
    }

    public synchronized void addProgressListener(ProgressListener listener) {
        if (listener == null) {
            return;
        }
        if (this.syncCallSafe) {
            this.syncCallSafe = DeliveryMode.Check.isSyncCallSafe(listener);
        }
        this.listeners.add(listener);
    }

    public synchronized void removeProgressListener(ProgressListener listener) {
        if (listener == null) {
            return;
        }
        this.listeners.remove(listener);
    }

    protected List<ProgressListener> getListeners() {
        return this.listeners;
    }

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        ProgressEvent filteredEvent = progressEvent;
        if (this.progressEventFilter != null && (filteredEvent = this.progressEventFilter.filter(progressEvent)) == null) {
            return;
        }
        for (ProgressListener listener : this.listeners) {
            try {
                listener.progressChanged(filteredEvent);
            }
            catch (RuntimeException e) {
                log.warn((Object)"Couldn't update progress listener", (Throwable)e);
            }
        }
    }

    @Override
    public boolean isSyncCallSafe() {
        return this.syncCallSafe;
    }
}

