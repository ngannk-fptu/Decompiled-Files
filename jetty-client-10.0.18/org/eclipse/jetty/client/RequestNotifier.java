/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.nio.ByteBuffer;
import java.util.List;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.ResponseNotifier;
import org.eclipse.jetty.client.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestNotifier {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseNotifier.class);
    private final HttpClient client;

    public RequestNotifier(HttpClient client) {
        this.client = client;
    }

    public void notifyQueued(Request request) {
        List requestListeners = request.getRequestListeners(null);
        for (int i = 0; i < requestListeners.size(); ++i) {
            Request.RequestListener listener = (Request.RequestListener)requestListeners.get(i);
            if (!(listener instanceof Request.QueuedListener)) continue;
            this.notifyQueued((Request.QueuedListener)listener, request);
        }
        List<Request.Listener> listeners = this.client.getRequestListeners();
        for (int i = 0; i < listeners.size(); ++i) {
            Request.Listener listener = listeners.get(i);
            this.notifyQueued(listener, request);
        }
    }

    private void notifyQueued(Request.QueuedListener listener, Request request) {
        try {
            listener.onQueued(request);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifyBegin(Request request) {
        List requestListeners = request.getRequestListeners(null);
        for (int i = 0; i < requestListeners.size(); ++i) {
            Request.RequestListener listener = (Request.RequestListener)requestListeners.get(i);
            if (!(listener instanceof Request.BeginListener)) continue;
            this.notifyBegin((Request.BeginListener)listener, request);
        }
        List<Request.Listener> listeners = this.client.getRequestListeners();
        for (int i = 0; i < listeners.size(); ++i) {
            Request.Listener listener = listeners.get(i);
            this.notifyBegin(listener, request);
        }
    }

    private void notifyBegin(Request.BeginListener listener, Request request) {
        try {
            listener.onBegin(request);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifyHeaders(Request request) {
        List requestListeners = request.getRequestListeners(null);
        for (int i = 0; i < requestListeners.size(); ++i) {
            Request.RequestListener listener = (Request.RequestListener)requestListeners.get(i);
            if (!(listener instanceof Request.HeadersListener)) continue;
            this.notifyHeaders((Request.HeadersListener)listener, request);
        }
        List<Request.Listener> listeners = this.client.getRequestListeners();
        for (int i = 0; i < listeners.size(); ++i) {
            Request.Listener listener = listeners.get(i);
            this.notifyHeaders(listener, request);
        }
    }

    private void notifyHeaders(Request.HeadersListener listener, Request request) {
        try {
            listener.onHeaders(request);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifyCommit(Request request) {
        List requestListeners = request.getRequestListeners(null);
        for (int i = 0; i < requestListeners.size(); ++i) {
            Request.RequestListener listener = (Request.RequestListener)requestListeners.get(i);
            if (!(listener instanceof Request.CommitListener)) continue;
            this.notifyCommit((Request.CommitListener)listener, request);
        }
        List<Request.Listener> listeners = this.client.getRequestListeners();
        for (int i = 0; i < listeners.size(); ++i) {
            Request.Listener listener = listeners.get(i);
            this.notifyCommit(listener, request);
        }
    }

    private void notifyCommit(Request.CommitListener listener, Request request) {
        try {
            listener.onCommit(request);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifyContent(Request request, ByteBuffer content) {
        if (!content.hasRemaining()) {
            return;
        }
        content = content.slice();
        List requestListeners = request.getRequestListeners(null);
        for (int i = 0; i < requestListeners.size(); ++i) {
            Request.RequestListener listener = (Request.RequestListener)requestListeners.get(i);
            if (!(listener instanceof Request.ContentListener)) continue;
            content.clear();
            this.notifyContent((Request.ContentListener)listener, request, content);
        }
        List<Request.Listener> listeners = this.client.getRequestListeners();
        for (int i = 0; i < listeners.size(); ++i) {
            Request.Listener listener = listeners.get(i);
            content.clear();
            this.notifyContent(listener, request, content);
        }
    }

    private void notifyContent(Request.ContentListener listener, Request request, ByteBuffer content) {
        try {
            listener.onContent(request, content);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifySuccess(Request request) {
        List requestListeners = request.getRequestListeners(null);
        for (int i = 0; i < requestListeners.size(); ++i) {
            Request.RequestListener listener = (Request.RequestListener)requestListeners.get(i);
            if (!(listener instanceof Request.SuccessListener)) continue;
            this.notifySuccess((Request.SuccessListener)listener, request);
        }
        List<Request.Listener> listeners = this.client.getRequestListeners();
        for (int i = 0; i < listeners.size(); ++i) {
            Request.Listener listener = listeners.get(i);
            this.notifySuccess(listener, request);
        }
    }

    private void notifySuccess(Request.SuccessListener listener, Request request) {
        try {
            listener.onSuccess(request);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }

    public void notifyFailure(Request request, Throwable failure) {
        List requestListeners = request.getRequestListeners(null);
        for (int i = 0; i < requestListeners.size(); ++i) {
            Request.RequestListener listener = (Request.RequestListener)requestListeners.get(i);
            if (!(listener instanceof Request.FailureListener)) continue;
            this.notifyFailure((Request.FailureListener)listener, request, failure);
        }
        List<Request.Listener> listeners = this.client.getRequestListeners();
        for (int i = 0; i < listeners.size(); ++i) {
            Request.Listener listener = listeners.get(i);
            this.notifyFailure(listener, request, failure);
        }
    }

    private void notifyFailure(Request.FailureListener listener, Request request, Throwable failure) {
        try {
            listener.onFailure(request, failure);
        }
        catch (Throwable x) {
            LOG.info("Exception while notifying listener {}", (Object)listener, (Object)x);
        }
    }
}

