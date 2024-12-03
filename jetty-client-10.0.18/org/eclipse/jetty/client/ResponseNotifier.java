/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.CountingCallback
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.function.LongConsumer;
import java.util.function.ObjLongConsumer;
import java.util.stream.Collectors;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.CountingCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseNotifier {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseNotifier.class);

    public void notifyBegin(List<Response.ResponseListener> listeners, Response response) {
        for (Response.ResponseListener listener : listeners) {
            if (!(listener instanceof Response.BeginListener)) continue;
            this.notifyBegin((Response.BeginListener)listener, response);
        }
    }

    private void notifyBegin(Response.BeginListener listener, Response response) {
        try {
            listener.onBegin(response);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public boolean notifyHeader(List<Response.ResponseListener> listeners, Response response, HttpField field) {
        boolean result = true;
        for (Response.ResponseListener listener : listeners) {
            if (!(listener instanceof Response.HeaderListener)) continue;
            result &= this.notifyHeader((Response.HeaderListener)listener, response, field);
        }
        return result;
    }

    private boolean notifyHeader(Response.HeaderListener listener, Response response, HttpField field) {
        try {
            return listener.onHeader(response, field);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
            return false;
        }
    }

    public void notifyHeaders(List<Response.ResponseListener> listeners, Response response) {
        for (Response.ResponseListener listener : listeners) {
            if (!(listener instanceof Response.HeadersListener)) continue;
            this.notifyHeaders((Response.HeadersListener)listener, response);
        }
    }

    private void notifyHeaders(Response.HeadersListener listener, Response response) {
        try {
            listener.onHeaders(response);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifyBeforeContent(Response response, ObjLongConsumer<Object> demand, List<Response.DemandedContentListener> contentListeners) {
        for (Response.DemandedContentListener listener : contentListeners) {
            this.notifyBeforeContent(listener, response, (long d) -> demand.accept(listener, d));
        }
    }

    private void notifyBeforeContent(Response.DemandedContentListener listener, Response response, LongConsumer demand) {
        try {
            listener.onBeforeContent(response, demand);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifyContent(Response response, ObjLongConsumer<Object> demand, ByteBuffer buffer, Callback callback, List<Response.DemandedContentListener> contentListeners) {
        int count = contentListeners.size();
        if (count == 0) {
            callback.succeeded();
            demand.accept(null, 1L);
        } else if (count == 1) {
            Response.DemandedContentListener listener = contentListeners.get(0);
            this.notifyContent(listener, response, (long d) -> demand.accept(listener, d), buffer.slice(), callback);
        } else {
            callback = new CountingCallback(callback, count);
            for (Response.DemandedContentListener listener : contentListeners) {
                this.notifyContent(listener, response, (long d) -> demand.accept(listener, d), buffer.slice(), callback);
            }
        }
    }

    private void notifyContent(Response.DemandedContentListener listener, Response response, LongConsumer demand, ByteBuffer buffer, Callback callback) {
        try {
            listener.onContent(response, demand, buffer, callback);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifySuccess(List<Response.ResponseListener> listeners, Response response) {
        for (Response.ResponseListener listener : listeners) {
            if (!(listener instanceof Response.SuccessListener)) continue;
            this.notifySuccess((Response.SuccessListener)listener, response);
        }
    }

    private void notifySuccess(Response.SuccessListener listener, Response response) {
        try {
            listener.onSuccess(response);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifyFailure(List<Response.ResponseListener> listeners, Response response, Throwable failure) {
        for (Response.ResponseListener listener : listeners) {
            if (!(listener instanceof Response.FailureListener)) continue;
            this.notifyFailure((Response.FailureListener)listener, response, failure);
        }
    }

    private void notifyFailure(Response.FailureListener listener, Response response, Throwable failure) {
        try {
            listener.onFailure(response, failure);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifyComplete(List<Response.ResponseListener> listeners, Result result) {
        for (Response.ResponseListener listener : listeners) {
            if (!(listener instanceof Response.CompleteListener)) continue;
            this.notifyComplete((Response.CompleteListener)listener, result);
        }
    }

    private void notifyComplete(Response.CompleteListener listener, Result result) {
        try {
            listener.onComplete(result);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void forwardSuccess(List<Response.ResponseListener> listeners, Response response) {
        this.forwardEvents(listeners, response);
        this.notifySuccess(listeners, response);
    }

    public void forwardSuccessComplete(List<Response.ResponseListener> listeners, Request request, Response response) {
        this.forwardSuccess(listeners, response);
        this.notifyComplete(listeners, new Result(request, response));
    }

    public void forwardFailure(List<Response.ResponseListener> listeners, Response response, Throwable failure) {
        this.forwardEvents(listeners, response);
        this.notifyFailure(listeners, response, failure);
    }

    private void forwardEvents(List<Response.ResponseListener> listeners, Response response) {
        byte[] content;
        this.notifyBegin(listeners, response);
        Iterator iterator = response.getHeaders().iterator();
        while (iterator.hasNext()) {
            HttpField field = (HttpField)iterator.next();
            if (this.notifyHeader(listeners, response, field)) continue;
            iterator.remove();
        }
        this.notifyHeaders(listeners, response);
        if (response instanceof ContentResponse && (content = ((ContentResponse)response).getContent()) != null && content.length > 0) {
            List<Response.DemandedContentListener> contentListeners = listeners.stream().filter(Response.DemandedContentListener.class::isInstance).map(Response.DemandedContentListener.class::cast).collect(Collectors.toList());
            ObjLongConsumer<Object> demand = (context, value) -> {};
            this.notifyBeforeContent(response, demand, contentListeners);
            this.notifyContent(response, demand, ByteBuffer.wrap(content), Callback.NOOP, contentListeners);
        }
    }

    public void forwardFailureComplete(List<Response.ResponseListener> listeners, Request request, Throwable requestFailure, Response response, Throwable responseFailure) {
        this.forwardFailure(listeners, response, responseFailure);
        this.notifyComplete(listeners, new Result(request, requestFailure, response, responseFailure));
    }
}

