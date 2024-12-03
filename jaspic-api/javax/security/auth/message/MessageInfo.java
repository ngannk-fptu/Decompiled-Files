/*
 * Decompiled with CFR 0.152.
 */
package javax.security.auth.message;

import java.util.Map;

public interface MessageInfo {
    public Object getRequestMessage();

    public Object getResponseMessage();

    public void setRequestMessage(Object var1);

    public void setResponseMessage(Object var1);

    public Map getMap();
}

