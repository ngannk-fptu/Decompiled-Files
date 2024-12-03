/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws.spi.http;

import java.io.IOException;
import javax.xml.ws.spi.http.HttpExchange;

public abstract class HttpHandler {
    public abstract void handle(HttpExchange var1) throws IOException;
}

