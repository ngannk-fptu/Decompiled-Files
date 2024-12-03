/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.ServletContext
 */
package com.atlassian.johnson.event;

import com.atlassian.event.api.EventListener;
import com.atlassian.johnson.Johnson;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.AddEvent;
import com.atlassian.johnson.event.RemoveEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;

public class JohnsonEventListener {
    private ServletContext servletContext;

    public JohnsonEventListener(@Nullable ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @EventListener
    public void onAdd(@Nonnull AddEvent e) {
        this.getContainer().addEvent(e.getEvent());
    }

    @EventListener
    public void onRemove(@Nonnull RemoveEvent e) {
        this.getContainer().removeEvent(e.getEvent());
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    private JohnsonEventContainer getContainer() {
        return this.servletContext == null ? Johnson.getEventContainer() : Johnson.getEventContainer(this.servletContext);
    }
}

