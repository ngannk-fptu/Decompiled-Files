/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.handler;

import java.util.List;
import java.util.Map;
import javax.xml.rpc.handler.MessageContext;

public interface HandlerChain
extends List {
    public boolean handleRequest(MessageContext var1);

    public boolean handleResponse(MessageContext var1);

    public boolean handleFault(MessageContext var1);

    public void init(Map var1);

    public void destroy();

    public void setRoles(String[] var1);

    public String[] getRoles();
}

