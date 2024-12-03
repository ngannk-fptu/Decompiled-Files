/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.util.StringUtils
 *  org.jooq.ExecuteContext
 *  org.jooq.exception.DataAccessException
 *  org.jooq.impl.DefaultExecuteListener
 */
package io.micrometer.core.instrument.binder.db;

import io.micrometer.common.util.StringUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.jooq.ExecuteContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DefaultExecuteListener;

class JooqExecuteListener
extends DefaultExecuteListener {
    private final MeterRegistry registry;
    private final Iterable<Tag> tags;
    private final Supplier<Iterable<Tag>> queryTagsSupplier;
    private final Object sampleLock = new Object();
    private final Map<ExecuteContext, Timer.Sample> sampleByExecuteContext = new HashMap<ExecuteContext, Timer.Sample>();

    public JooqExecuteListener(MeterRegistry registry, Iterable<Tag> tags, Supplier<Iterable<Tag>> queryTags) {
        this.registry = registry;
        this.tags = tags;
        this.queryTagsSupplier = queryTags;
    }

    public void start(ExecuteContext ctx) {
        this.startTimer(ctx);
    }

    public void executeStart(ExecuteContext ctx) {
        this.startTimer(ctx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startTimer(ExecuteContext ctx) {
        Timer.Sample started = Timer.start(this.registry);
        Object object = this.sampleLock;
        synchronized (object) {
            this.sampleByExecuteContext.put(ctx, started);
        }
    }

    public void executeEnd(ExecuteContext ctx) {
        this.stopTimerIfStillRunning(ctx);
    }

    public void end(ExecuteContext ctx) {
        this.stopTimerIfStillRunning(ctx);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void stopTimerIfStillRunning(ExecuteContext ctx) {
        Timer.Sample sample;
        Iterable<Tag> queryTags = this.queryTagsSupplier.get();
        if (queryTags == null) {
            return;
        }
        Object object = this.sampleLock;
        synchronized (object) {
            sample = this.sampleByExecuteContext.remove(ctx);
        }
        if (sample == null) {
            return;
        }
        String exceptionName = "none";
        String exceptionSubclass = "none";
        RuntimeException exception = ctx.exception();
        if (exception != null) {
            if (exception instanceof DataAccessException) {
                DataAccessException dae = (DataAccessException)exception;
                exceptionName = dae.sqlStateClass().name().toLowerCase().replace('_', ' ');
                exceptionSubclass = dae.sqlStateSubclass().name().toLowerCase().replace('_', ' ');
                if (exceptionSubclass.contains("no subclass")) {
                    exceptionSubclass = "none";
                }
            } else {
                String simpleName = exception.getClass().getSimpleName();
                exceptionName = StringUtils.isNotBlank((String)simpleName) ? simpleName : exception.getClass().getName();
            }
        }
        sample.stop(((Timer.Builder)((Timer.Builder)Timer.builder("jooq.query").description("Execution time of a SQL query performed with JOOQ").tags((Iterable)queryTags)).tag("type", ctx.type().name().toLowerCase()).tag("exception", exceptionName).tag("exception.subclass", exceptionSubclass).tags((Iterable)this.tags)).register(this.registry));
    }
}

