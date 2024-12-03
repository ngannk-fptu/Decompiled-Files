/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.util.Enumeration;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionContext;

public interface HttpSession {
    public long getCreationTime();

    public String getId();

    public long getLastAccessedTime();

    public ServletContext getServletContext();

    public void setMaxInactiveInterval(int var1);

    public int getMaxInactiveInterval();

    @Deprecated
    public HttpSessionContext getSessionContext();

    public Object getAttribute(String var1);

    @Deprecated
    public Object getValue(String var1);

    public Enumeration<String> getAttributeNames();

    @Deprecated
    public String[] getValueNames();

    public void setAttribute(String var1, Object var2);

    @Deprecated
    public void putValue(String var1, Object var2);

    public void removeAttribute(String var1);

    @Deprecated
    public void removeValue(String var1);

    public void invalidate();

    public boolean isNew();
}

