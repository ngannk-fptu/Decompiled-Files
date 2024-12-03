/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.util.Enumeration;
import javax.servlet.http.HttpSession;

@Deprecated
public interface HttpSessionContext {
    @Deprecated
    public HttpSession getSession(String var1);

    @Deprecated
    public Enumeration<String> getIds();
}

