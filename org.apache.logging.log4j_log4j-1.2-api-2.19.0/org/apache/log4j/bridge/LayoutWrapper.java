/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.Layout
 */
package org.apache.log4j.bridge;

import org.apache.log4j.Layout;
import org.apache.log4j.bridge.LayoutAdapter;
import org.apache.log4j.bridge.LogEventAdapter;
import org.apache.log4j.spi.LoggingEvent;

public class LayoutWrapper
extends Layout {
    private final org.apache.logging.log4j.core.Layout<?> layout;

    public static Layout adapt(org.apache.logging.log4j.core.Layout<?> layout) {
        if (layout instanceof LayoutAdapter) {
            return ((LayoutAdapter)layout).getLayout();
        }
        if (layout != null) {
            return new LayoutWrapper(layout);
        }
        return null;
    }

    public LayoutWrapper(org.apache.logging.log4j.core.Layout<?> layout) {
        this.layout = layout;
    }

    @Override
    public String format(LoggingEvent event) {
        return this.layout.toSerializable(((LogEventAdapter)event).getEvent()).toString();
    }

    public org.apache.logging.log4j.core.Layout<?> getLayout() {
        return this.layout;
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    public String toString() {
        return String.format("LayoutWrapper [layout=%s]", this.layout);
    }
}

