/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws.handler;

import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.PortInfo;

public interface HandlerResolver {
    public List<Handler> getHandlerChain(PortInfo var1);
}

