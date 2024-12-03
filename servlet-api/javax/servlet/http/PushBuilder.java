/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.util.Set;

public interface PushBuilder {
    public PushBuilder method(String var1);

    public PushBuilder queryString(String var1);

    public PushBuilder sessionId(String var1);

    public PushBuilder setHeader(String var1, String var2);

    public PushBuilder addHeader(String var1, String var2);

    public PushBuilder removeHeader(String var1);

    public PushBuilder path(String var1);

    public void push();

    public String getMethod();

    public String getQueryString();

    public String getSessionId();

    public Set<String> getHeaderNames();

    public String getHeader(String var1);

    public String getPath();
}

