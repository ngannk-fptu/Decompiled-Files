/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.eclipse.jetty.server.AsyncContextEvent
 *  org.eclipse.jetty.server.Handler
 *  org.eclipse.jetty.server.HttpChannelState
 *  org.eclipse.jetty.server.Request
 *  org.eclipse.jetty.server.handler.HandlerWrapper
 *  org.eclipse.jetty.util.FutureCallback
 *  org.eclipse.jetty.util.component.Graceful
 *  org.eclipse.jetty.util.component.Graceful$Shutdown
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.http.DefaultHttpServletRequestTagsProvider;
import io.micrometer.core.instrument.binder.http.HttpServletRequestTagsProvider;
import io.micrometer.core.instrument.binder.jetty.OnCompletionAsyncListener;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.AsyncContextEvent;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.FutureCallback;
import org.eclipse.jetty.util.component.Graceful;

@NonNullApi
@NonNullFields
public class TimedHandler
extends HandlerWrapper
implements Graceful {
    private static final String SAMPLE_REQUEST_TIMER_ATTRIBUTE = "__micrometer_timer_sample";
    private static final String SAMPLE_REQUEST_LONG_TASK_TIMER_ATTRIBUTE = "__micrometer_ltt_sample";
    private final MeterRegistry registry;
    private final Iterable<Tag> tags;
    private final HttpServletRequestTagsProvider tagsProvider;
    private final Graceful.Shutdown shutdown = new Graceful.Shutdown(){

        protected FutureCallback newShutdownCallback() {
            return TimedHandler.this.newShutdownCallback();
        }
    };
    private final LongTaskTimer openRequests;
    private final Counter asyncDispatches;
    private final Counter asyncExpires;
    private final AtomicInteger asyncWaits = new AtomicInteger();
    private final AsyncListener onCompletion = new OnCompletionAsyncListener((Object)this);

    public TimedHandler(MeterRegistry registry, Iterable<Tag> tags) {
        this(registry, tags, new DefaultHttpServletRequestTagsProvider());
    }

    public TimedHandler(MeterRegistry registry, Iterable<Tag> tags, HttpServletRequestTagsProvider tagsProvider) {
        this.registry = registry;
        this.tags = tags;
        this.tagsProvider = tagsProvider;
        this.openRequests = LongTaskTimer.builder("jetty.server.dispatches.open").description("Jetty dispatches that are currently in progress").tags(tags).register(registry);
        this.asyncDispatches = Counter.builder("jetty.server.async.dispatches").description("Asynchronous dispatches").tags(tags).register(registry);
        this.asyncExpires = Counter.builder("jetty.server.async.expires").description("Asynchronous operations that timed out before completing").tags(tags).register(registry);
        Gauge.builder("jetty.server.async.waits", this.asyncWaits, AtomicInteger::doubleValue).description("Pending asynchronous wait operations").baseUnit("operations").tags(tags).register(registry);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void handle(String path, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LongTaskTimer.Sample requestSample;
        Timer.Sample sample = Timer.start(this.registry);
        HttpChannelState state = baseRequest.getHttpChannelState();
        if (state.isInitial()) {
            requestSample = this.openRequests.start();
            request.setAttribute(SAMPLE_REQUEST_TIMER_ATTRIBUTE, (Object)sample);
            request.setAttribute(SAMPLE_REQUEST_LONG_TASK_TIMER_ATTRIBUTE, (Object)requestSample);
        } else {
            this.asyncDispatches.increment();
            request.setAttribute(SAMPLE_REQUEST_TIMER_ATTRIBUTE, (Object)sample);
            requestSample = (LongTaskTimer.Sample)request.getAttribute(SAMPLE_REQUEST_LONG_TASK_TIMER_ATTRIBUTE);
        }
        try {
            Handler handler = this.getHandler();
            if (handler != null && !this.shutdown.isShutdown() && this.isStarted()) {
                handler.handle(path, baseRequest, request, response);
            } else {
                if (!baseRequest.isHandled()) {
                    baseRequest.setHandled(true);
                }
                if (!baseRequest.getResponse().isCommitted()) {
                    response.sendError(503);
                }
            }
        }
        finally {
            if (state.isSuspended()) {
                if (state.isInitial()) {
                    state.addListener(this.onCompletion);
                    this.asyncWaits.incrementAndGet();
                }
            } else if (state.isInitial()) {
                sample.stop(((Timer.Builder)((Timer.Builder)Timer.builder("jetty.server.requests").description("HTTP requests to the Jetty server").tags((Iterable)this.tagsProvider.getTags(request, response))).tags((Iterable)this.tags)).register(this.registry));
                requestSample.stop();
                FutureCallback shutdownCallback = this.shutdown.get();
                if (shutdownCallback != null) {
                    response.flushBuffer();
                    if (this.openRequests.activeTasks() == 0) {
                        shutdownCallback.succeeded();
                    }
                }
            }
        }
    }

    protected void doStart() throws Exception {
        this.shutdown.cancel();
        super.doStart();
    }

    protected void doStop() throws Exception {
        this.shutdown.cancel();
        super.doStop();
    }

    public Future<Void> shutdown() {
        return this.shutdown.shutdown();
    }

    public boolean isShutdown() {
        return this.shutdown.isShutdown();
    }

    void onAsyncTimeout(AsyncEvent event) {
        this.asyncExpires.increment();
        HttpChannelState state = ((AsyncContextEvent)event).getHttpChannelState();
        Request request = state.getBaseRequest();
        LongTaskTimer.Sample lttSample = (LongTaskTimer.Sample)request.getAttribute(SAMPLE_REQUEST_LONG_TASK_TIMER_ATTRIBUTE);
        lttSample.stop();
    }

    void onAsyncComplete(AsyncEvent event) {
        HttpChannelState state = ((AsyncContextEvent)event).getHttpChannelState();
        Request request = state.getBaseRequest();
        Timer.Sample sample = (Timer.Sample)request.getAttribute(SAMPLE_REQUEST_TIMER_ATTRIBUTE);
        LongTaskTimer.Sample lttSample = (LongTaskTimer.Sample)request.getAttribute(SAMPLE_REQUEST_LONG_TASK_TIMER_ATTRIBUTE);
        if (sample != null) {
            sample.stop(((Timer.Builder)((Timer.Builder)Timer.builder("jetty.server.requests").description("HTTP requests to the Jetty server").tags((Iterable)this.tagsProvider.getTags((HttpServletRequest)request, (HttpServletResponse)request.getResponse()))).tags((Iterable)this.tags)).register(this.registry));
            lttSample.stop();
        }
        this.asyncWaits.decrementAndGet();
        FutureCallback shutdownCallback = this.shutdown.get();
        if (shutdownCallback != null && this.openRequests.activeTasks() == 0) {
            shutdownCallback.succeeded();
        }
    }

    private FutureCallback newShutdownCallback() {
        return new FutureCallback(this.openRequests.activeTasks() == 0);
    }
}

