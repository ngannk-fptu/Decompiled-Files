/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;

public class ServletRequestAttributeEvent
extends ServletRequestEvent {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Object value;

    public ServletRequestAttributeEvent(ServletContext sc, ServletRequest request, String name, Object value) {
        super(sc, request);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }
}

