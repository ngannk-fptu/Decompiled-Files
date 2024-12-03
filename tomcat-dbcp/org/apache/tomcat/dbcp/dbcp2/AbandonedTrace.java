/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.pool2.TrackedUse;

public class AbandonedTrace
implements TrackedUse,
AutoCloseable {
    private final List<WeakReference<AbandonedTrace>> traceList = new ArrayList<WeakReference<AbandonedTrace>>();
    private volatile Instant lastUsedInstant = Instant.EPOCH;

    static void add(AbandonedTrace receiver, AbandonedTrace trace) {
        if (receiver != null) {
            receiver.addTrace(trace);
        }
    }

    public AbandonedTrace() {
        this.init(null);
    }

    public AbandonedTrace(AbandonedTrace parent) {
        this.init(parent);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addTrace(AbandonedTrace trace) {
        List<WeakReference<AbandonedTrace>> list = this.traceList;
        synchronized (list) {
            this.traceList.add(new WeakReference<AbandonedTrace>(trace));
        }
        this.setLastUsed();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void clearTrace() {
        List<WeakReference<AbandonedTrace>> list = this.traceList;
        synchronized (list) {
            this.traceList.clear();
        }
    }

    @Override
    public void close() throws SQLException {
    }

    protected void close(Consumer<Exception> exceptionHandler) {
        Utils.close(this, exceptionHandler);
    }

    @Override
    @Deprecated
    public long getLastUsed() {
        return this.lastUsedInstant.toEpochMilli();
    }

    @Override
    public Instant getLastUsedInstant() {
        return this.lastUsedInstant;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected List<AbandonedTrace> getTrace() {
        int size = this.traceList.size();
        if (size == 0) {
            return Collections.emptyList();
        }
        ArrayList<AbandonedTrace> result = new ArrayList<AbandonedTrace>(size);
        List<WeakReference<AbandonedTrace>> list = this.traceList;
        synchronized (list) {
            Iterator<WeakReference<AbandonedTrace>> iter = this.traceList.iterator();
            while (iter.hasNext()) {
                AbandonedTrace trace = (AbandonedTrace)iter.next().get();
                if (trace == null) {
                    iter.remove();
                    continue;
                }
                result.add(trace);
            }
        }
        return result;
    }

    private void init(AbandonedTrace parent) {
        AbandonedTrace.add(parent, this);
    }

    protected void removeThisTrace(Object source) {
        if (source instanceof AbandonedTrace) {
            ((AbandonedTrace)AbandonedTrace.class.cast(source)).removeTrace(this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeTrace(AbandonedTrace trace) {
        List<WeakReference<AbandonedTrace>> list = this.traceList;
        synchronized (list) {
            Iterator<WeakReference<AbandonedTrace>> iter = this.traceList.iterator();
            while (iter.hasNext()) {
                AbandonedTrace traceInList = (AbandonedTrace)iter.next().get();
                if (trace != null && trace.equals(traceInList)) {
                    iter.remove();
                    break;
                }
                if (traceInList != null) continue;
                iter.remove();
            }
        }
    }

    protected void setLastUsed() {
        this.lastUsedInstant = Instant.now();
    }

    protected void setLastUsed(Instant lastUsedInstant) {
        this.lastUsedInstant = lastUsedInstant;
    }

    @Deprecated
    protected void setLastUsed(long lastUsedMillis) {
        this.lastUsedInstant = Instant.ofEpochMilli(lastUsedMillis);
    }
}

