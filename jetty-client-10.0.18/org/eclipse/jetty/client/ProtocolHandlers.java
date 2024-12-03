/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.component.Dumpable
 */
package org.eclipse.jetty.client;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.jetty.client.ProtocolHandler;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.util.component.Dumpable;

public class ProtocolHandlers
implements Dumpable {
    private final Map<String, ProtocolHandler> handlers = new LinkedHashMap<String, ProtocolHandler>();

    protected ProtocolHandlers() {
    }

    public ProtocolHandler put(ProtocolHandler protocolHandler) {
        return this.handlers.put(protocolHandler.getName(), protocolHandler);
    }

    public ProtocolHandler remove(String name) {
        return this.handlers.remove(name);
    }

    public void clear() {
        this.handlers.clear();
    }

    public ProtocolHandler find(Request request, Response response) {
        for (ProtocolHandler handler : this.handlers.values()) {
            if (!handler.accept(request, response)) continue;
            return handler;
        }
        return null;
    }

    public String dump() {
        return Dumpable.dump((Dumpable)this);
    }

    public void dump(Appendable out, String indent) throws IOException {
        Dumpable.dumpObjects((Appendable)out, (String)indent, (Object)this, (Object[])new Object[]{this.handlers});
    }
}

