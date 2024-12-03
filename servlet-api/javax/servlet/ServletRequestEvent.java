/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import java.util.EventObject;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

public class ServletRequestEvent
extends EventObject {
    private static final long serialVersionUID = 1L;
    private final transient ServletRequest request;

    public ServletRequestEvent(ServletContext sc, ServletRequest request) {
        super(sc);
        this.request = request;
    }

    public ServletRequest getServletRequest() {
        return this.request;
    }

    public ServletContext getServletContext() {
        return (ServletContext)super.getSource();
    }
}

