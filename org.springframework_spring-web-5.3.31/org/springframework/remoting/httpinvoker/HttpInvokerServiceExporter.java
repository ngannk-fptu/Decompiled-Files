/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.remoting.rmi.RemoteInvocationSerializingExporter
 *  org.springframework.remoting.support.RemoteInvocation
 *  org.springframework.remoting.support.RemoteInvocationResult
 */
package org.springframework.remoting.httpinvoker;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.util.NestedServletException;

@Deprecated
public class HttpInvokerServiceExporter
extends RemoteInvocationSerializingExporter
implements HttpRequestHandler {
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            RemoteInvocation invocation = this.readRemoteInvocation(request);
            RemoteInvocationResult result = this.invokeAndCreateResult(invocation, this.getProxy());
            this.writeRemoteInvocationResult(request, response, result);
        }
        catch (ClassNotFoundException ex) {
            throw new NestedServletException("Class not found during deserialization", ex);
        }
    }

    protected RemoteInvocation readRemoteInvocation(HttpServletRequest request) throws IOException, ClassNotFoundException {
        return this.readRemoteInvocation(request, (InputStream)request.getInputStream());
    }

    protected RemoteInvocation readRemoteInvocation(HttpServletRequest request, InputStream is) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = this.createObjectInputStream(this.decorateInputStream(request, is));){
            RemoteInvocation remoteInvocation = this.doReadRemoteInvocation(ois);
            return remoteInvocation;
        }
    }

    protected InputStream decorateInputStream(HttpServletRequest request, InputStream is) throws IOException {
        return is;
    }

    protected void writeRemoteInvocationResult(HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result) throws IOException {
        response.setContentType(this.getContentType());
        this.writeRemoteInvocationResult(request, response, result, (OutputStream)response.getOutputStream());
    }

    protected void writeRemoteInvocationResult(HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result, OutputStream os) throws IOException {
        try (ObjectOutputStream oos = this.createObjectOutputStream(new FlushGuardedOutputStream(this.decorateOutputStream(request, response, os)));){
            this.doWriteRemoteInvocationResult(result, oos);
        }
    }

    protected OutputStream decorateOutputStream(HttpServletRequest request, HttpServletResponse response, OutputStream os) throws IOException {
        return os;
    }

    private static class FlushGuardedOutputStream
    extends FilterOutputStream {
        public FlushGuardedOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void flush() throws IOException {
        }
    }
}

