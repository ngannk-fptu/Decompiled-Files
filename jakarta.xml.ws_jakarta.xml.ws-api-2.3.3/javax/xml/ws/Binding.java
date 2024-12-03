/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws;

import java.util.List;
import javax.xml.ws.handler.Handler;

public interface Binding {
    public List<Handler> getHandlerChain();

    public void setHandlerChain(List<Handler> var1);

    public String getBindingID();
}

