/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.panel;

import javax.swing.event.EventListenerList;
import org.jfree.chart.event.OverlayChangeEvent;
import org.jfree.chart.event.OverlayChangeListener;

public class AbstractOverlay {
    private transient EventListenerList changeListeners = new EventListenerList();
    static /* synthetic */ Class class$org$jfree$chart$event$OverlayChangeListener;

    public void addChangeListener(OverlayChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.changeListeners.add(class$org$jfree$chart$event$OverlayChangeListener == null ? (class$org$jfree$chart$event$OverlayChangeListener = AbstractOverlay.class$("org.jfree.chart.event.OverlayChangeListener")) : class$org$jfree$chart$event$OverlayChangeListener, listener);
    }

    public void removeChangeListener(OverlayChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.changeListeners.remove(class$org$jfree$chart$event$OverlayChangeListener == null ? (class$org$jfree$chart$event$OverlayChangeListener = AbstractOverlay.class$("org.jfree.chart.event.OverlayChangeListener")) : class$org$jfree$chart$event$OverlayChangeListener, listener);
    }

    public void fireOverlayChanged() {
        OverlayChangeEvent event = new OverlayChangeEvent(this);
        this.notifyListeners(event);
    }

    protected void notifyListeners(OverlayChangeEvent event) {
        Object[] listeners = this.changeListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$org$jfree$chart$event$OverlayChangeListener == null ? AbstractOverlay.class$("org.jfree.chart.event.OverlayChangeListener") : class$org$jfree$chart$event$OverlayChangeListener)) continue;
            ((OverlayChangeListener)listeners[i + 1]).overlayChanged(event);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

