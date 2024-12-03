/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.http.HttpServletRequest
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.WSHTTPConnection;
import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;

public class WSAsyncListener {
    private final WSHTTPConnection con;
    private final HttpAdapter.CompletionCallback callback;
    private static final Logger LOGGER = Logger.getLogger(WSAsyncListener.class.getName());

    WSAsyncListener(WSHTTPConnection con, HttpAdapter.CompletionCallback callback) {
        this.con = con;
        this.callback = callback;
    }

    public void addListenerTo(AsyncContext context, final ServletAdapter.AsyncCompletionCheck completionCheck) {
        context.addListener(new AsyncListener(){

            public void onComplete(AsyncEvent event) throws IOException {
                LOGGER.finer("Asynchronous Servlet Invocation completed for " + ((HttpServletRequest)event.getAsyncContext().getRequest()).getRequestURL());
                WSAsyncListener.this.callback.onCompletion();
            }

            public void onTimeout(AsyncEvent event) throws IOException {
                completionCheck.markComplete();
                LOGGER.fine("Time out on Request:" + ((HttpServletRequest)event.getAsyncContext().getRequest()).getRequestURL());
                WSAsyncListener.this.con.close();
            }

            public void onError(AsyncEvent event) throws IOException {
                LOGGER.fine("Error processing Request:" + ((HttpServletRequest)event.getAsyncContext().getRequest()).getRequestURL());
                WSAsyncListener.this.con.close();
            }

            public void onStartAsync(AsyncEvent event) throws IOException {
                LOGGER.finer("Asynchronous Servlet Invocation started for " + ((HttpServletRequest)event.getAsyncContext().getRequest()).getRequestURL());
            }
        });
    }
}

