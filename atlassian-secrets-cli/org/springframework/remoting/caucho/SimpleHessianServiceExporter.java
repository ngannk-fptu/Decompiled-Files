/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.caucho;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.lang.UsesSunHttpServer;
import org.springframework.remoting.caucho.HessianExporter;
import org.springframework.util.FileCopyUtils;

@UsesSunHttpServer
public class SimpleHessianServiceExporter
extends HessianExporter
implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange2) throws IOException {
        if (!"POST".equals(exchange2.getRequestMethod())) {
            exchange2.getResponseHeaders().set("Allow", "POST");
            exchange2.sendResponseHeaders(405, -1L);
            return;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        try {
            this.invoke(exchange2.getRequestBody(), output);
        }
        catch (Throwable ex) {
            exchange2.sendResponseHeaders(500, -1L);
            this.logger.error("Hessian skeleton invocation failed", ex);
            return;
        }
        exchange2.getResponseHeaders().set("Content-Type", "application/x-hessian");
        exchange2.sendResponseHeaders(200, output.size());
        FileCopyUtils.copy(output.toByteArray(), exchange2.getResponseBody());
    }
}

