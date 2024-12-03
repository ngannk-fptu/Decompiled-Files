/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.handler;

import java.util.Iterator;

public interface MessageContext {
    public void setProperty(String var1, Object var2);

    public Object getProperty(String var1);

    public void removeProperty(String var1);

    public boolean containsProperty(String var1);

    public Iterator getPropertyNames();
}

