/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpField
 *  org.eclipse.jetty.http.HttpFields
 *  org.eclipse.jetty.http.HttpVersion
 *  org.eclipse.jetty.util.Callback
 */
package org.eclipse.jetty.client.api;

import java.nio.ByteBuffer;
import java.util.EventListener;
import java.util.List;
import java.util.function.LongConsumer;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.util.Callback;

public interface Response {
    public Request getRequest();

    public <T extends ResponseListener> List<T> getListeners(Class<T> var1);

    public HttpVersion getVersion();

    public int getStatus();

    public String getReason();

    public HttpFields getHeaders();

    public boolean abort(Throwable var1);

    public static interface Listener
    extends BeginListener,
    HeaderListener,
    HeadersListener,
    ContentListener,
    SuccessListener,
    FailureListener,
    CompleteListener {
        @Override
        default public void onBegin(Response response) {
        }

        @Override
        default public boolean onHeader(Response response, HttpField field) {
            return true;
        }

        @Override
        default public void onHeaders(Response response) {
        }

        @Override
        default public void onContent(Response response, ByteBuffer content) {
        }

        @Override
        default public void onSuccess(Response response) {
        }

        @Override
        default public void onFailure(Response response, Throwable failure) {
        }

        @Override
        default public void onComplete(Result result) {
        }

        public static class Adapter
        implements Listener {
        }
    }

    public static interface CompleteListener
    extends ResponseListener {
        public void onComplete(Result var1);
    }

    public static interface FailureListener
    extends ResponseListener {
        public void onFailure(Response var1, Throwable var2);
    }

    public static interface SuccessListener
    extends ResponseListener {
        public void onSuccess(Response var1);
    }

    public static interface DemandedContentListener
    extends ResponseListener {
        default public void onBeforeContent(Response response, LongConsumer demand) {
            demand.accept(1L);
        }

        public void onContent(Response var1, LongConsumer var2, ByteBuffer var3, Callback var4);
    }

    public static interface AsyncContentListener
    extends DemandedContentListener {
        public void onContent(Response var1, ByteBuffer var2, Callback var3);

        @Override
        default public void onContent(Response response, LongConsumer demand, ByteBuffer content, Callback callback) {
            this.onContent(response, content, Callback.from(() -> {
                callback.succeeded();
                demand.accept(1L);
            }, arg_0 -> ((Callback)callback).failed(arg_0)));
        }
    }

    public static interface ContentListener
    extends AsyncContentListener {
        public void onContent(Response var1, ByteBuffer var2);

        @Override
        default public void onContent(Response response, ByteBuffer content, Callback callback) {
            try {
                this.onContent(response, content);
                callback.succeeded();
            }
            catch (Throwable x) {
                callback.failed(x);
            }
        }
    }

    public static interface HeadersListener
    extends ResponseListener {
        public void onHeaders(Response var1);
    }

    public static interface HeaderListener
    extends ResponseListener {
        public boolean onHeader(Response var1, HttpField var2);
    }

    public static interface BeginListener
    extends ResponseListener {
        public void onBegin(Response var1);
    }

    public static interface ResponseListener
    extends EventListener {
    }
}

