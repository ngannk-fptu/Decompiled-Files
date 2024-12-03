/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class ServletContextAttributeEvent
extends ServletContextEvent {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final Object value;

    public ServletContextAttributeEvent(ServletContext source, String name, Object value) {
        super(source);
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

