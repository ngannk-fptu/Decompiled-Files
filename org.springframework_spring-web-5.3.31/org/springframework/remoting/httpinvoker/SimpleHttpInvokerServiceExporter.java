/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.UsesSunHttpServer
 *  org.springframework.remoting.rmi.RemoteInvocationSerializingExporter
 *  org.springframework.remoting.support.RemoteInvocation
 *  org.springframework.remoting.support.RemoteInvocationResult
 */
package org.springframework.remoting.httpinvoker;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.springframework.lang.UsesSunHttpServer;
import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

@Deprecated
@UsesSunHttpServer
public class SimpleHttpInvokerServiceExporter
extends RemoteInvocationSerializingExporter
implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange2) throws IOException {
        try {
            RemoteInvocation invocation = this.readRemoteInvocation(exchange2);
            RemoteInvocationResult result = this.invokeAndCreateResult(invocation, this.getProxy());
            this.writeRemoteInvocationResult(exchange2, result);
            exchange2.close();
        }
        catch (ClassNotFoundException ex) {
            exchange2.sendResponseHeaders(500, -1L);
            this.logger.error((Object)"Class not found during deserialization", (Throwable)ex);
        }
    }

    protected RemoteInvocation readRemoteInvocation(HttpExchange exchange2) throws IOException, ClassNotFoundException {
        return this.readRemoteInvocation(exchange2, exchange2.getRequestBody());
    }

    protected RemoteInvocation readRemoteInvocation(HttpExchange exchange2, InputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = this.createObjectInputStream(this.decorateInputStream(exchange2, is));
        return this.doReadRemoteInvocation(ois);
    }

    protected InputStream decorateInputStream(HttpExchange exchange2, InputStream is) throws IOException {
        return is;
    }

    protected void writeRemoteInvocationResult(HttpExchange exchange2, RemoteInvocationResult result) throws IOException {
        exchange2.getResponseHeaders().set("Content-Type", this.getContentType());
        exchange2.sendResponseHeaders(200, 0L);
        this.writeRemoteInvocationResult(exchange2, result, exchange2.getResponseBody());
    }

    protected void writeRemoteInvocationResult(HttpExchange exchange2, RemoteInvocationResult result, OutputStream os) throws IOException {
        ObjectOutputStream oos = this.createObjectOutputStream(this.decorateOutputStream(exchange2, os));
        this.doWriteRemoteInvocationResult(result, oos);
        oos.flush();
    }

    protected OutputStream decorateOutputStream(HttpExchange exchange2, OutputStream os) throws IOException {
        return os;
    }
}

