/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot.dial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.EventListener;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.plot.dial.DialLayer;
import org.jfree.chart.plot.dial.DialLayerChangeEvent;
import org.jfree.chart.plot.dial.DialLayerChangeListener;

public abstract class AbstractDialLayer
implements DialLayer {
    private boolean visible = true;
    private transient EventListenerList listenerList = new EventListenerList();
    static /* synthetic */ Class class$org$jfree$chart$plot$dial$DialLayerChangeListener;

    protected AbstractDialLayer() {
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        this.notifyListeners(new DialLayerChangeEvent(this));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractDialLayer)) {
            return false;
        }
        AbstractDialLayer that = (AbstractDialLayer)obj;
        return this.visible == that.visible;
    }

    public int hashCode() {
        int result = 23;
        result = HashUtilities.hashCode(result, this.visible);
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractDialLayer clone = (AbstractDialLayer)super.clone();
        clone.listenerList = new EventListenerList();
        return clone;
    }

    public void addChangeListener(DialLayerChangeListener listener) {
        this.listenerList.add(class$org$jfree$chart$plot$dial$DialLayerChangeListener == null ? (class$org$jfree$chart$plot$dial$DialLayerChangeListener = AbstractDialLayer.class$("org.jfree.chart.plot.dial.DialLayerChangeListener")) : class$org$jfree$chart$plot$dial$DialLayerChangeListener, listener);
    }

    public void removeChangeListener(DialLayerChangeListener listener) {
        this.listenerList.remove(class$org$jfree$chart$plot$dial$DialLayerChangeListener == null ? (class$org$jfree$chart$plot$dial$DialLayerChangeListener = AbstractDialLayer.class$("org.jfree.chart.plot.dial.DialLayerChangeListener")) : class$org$jfree$chart$plot$dial$DialLayerChangeListener, listener);
    }

    public boolean hasListener(EventListener listener) {
        List<Object> list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }

    protected void notifyListeners(DialLayerChangeEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$org$jfree$chart$plot$dial$DialLayerChangeListener == null ? AbstractDialLayer.class$("org.jfree.chart.plot.dial.DialLayerChangeListener") : class$org$jfree$chart$plot$dial$DialLayerChangeListener)) continue;
            ((DialLayerChangeListener)listeners[i + 1]).dialLayerChanged(event);
        }
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();
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

