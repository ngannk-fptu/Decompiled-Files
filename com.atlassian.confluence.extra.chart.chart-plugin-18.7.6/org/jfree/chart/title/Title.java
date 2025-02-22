/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.title;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.swing.event.EventListenerList;
import org.jfree.chart.block.AbstractBlock;
import org.jfree.chart.block.Block;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.chart.event.TitleChangeListener;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.ObjectUtilities;

public abstract class Title
extends AbstractBlock
implements Block,
Cloneable,
Serializable {
    private static final long serialVersionUID = -6675162505277817221L;
    public static final RectangleEdge DEFAULT_POSITION = RectangleEdge.TOP;
    public static final HorizontalAlignment DEFAULT_HORIZONTAL_ALIGNMENT = HorizontalAlignment.CENTER;
    public static final VerticalAlignment DEFAULT_VERTICAL_ALIGNMENT = VerticalAlignment.CENTER;
    public static final RectangleInsets DEFAULT_PADDING = new RectangleInsets(1.0, 1.0, 1.0, 1.0);
    public boolean visible;
    private RectangleEdge position;
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;
    private transient EventListenerList listenerList;
    private boolean notify;
    static /* synthetic */ Class class$org$jfree$chart$event$TitleChangeListener;

    protected Title() {
        this(DEFAULT_POSITION, DEFAULT_HORIZONTAL_ALIGNMENT, DEFAULT_VERTICAL_ALIGNMENT, DEFAULT_PADDING);
    }

    protected Title(RectangleEdge position, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
        this(position, horizontalAlignment, verticalAlignment, DEFAULT_PADDING);
    }

    protected Title(RectangleEdge position, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, RectangleInsets padding) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        if (horizontalAlignment == null) {
            throw new IllegalArgumentException("Null 'horizontalAlignment' argument.");
        }
        if (verticalAlignment == null) {
            throw new IllegalArgumentException("Null 'verticalAlignment' argument.");
        }
        if (padding == null) {
            throw new IllegalArgumentException("Null 'spacer' argument.");
        }
        this.visible = true;
        this.position = position;
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.setPadding(padding);
        this.listenerList = new EventListenerList();
        this.notify = true;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        this.notifyListeners(new TitleChangeEvent(this));
    }

    public RectangleEdge getPosition() {
        return this.position;
    }

    public void setPosition(RectangleEdge position) {
        if (position == null) {
            throw new IllegalArgumentException("Null 'position' argument.");
        }
        if (this.position != position) {
            this.position = position;
            this.notifyListeners(new TitleChangeEvent(this));
        }
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(HorizontalAlignment alignment) {
        if (alignment == null) {
            throw new IllegalArgumentException("Null 'alignment' argument.");
        }
        if (this.horizontalAlignment != alignment) {
            this.horizontalAlignment = alignment;
            this.notifyListeners(new TitleChangeEvent(this));
        }
    }

    public VerticalAlignment getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(VerticalAlignment alignment) {
        if (alignment == null) {
            throw new IllegalArgumentException("Null 'alignment' argument.");
        }
        if (this.verticalAlignment != alignment) {
            this.verticalAlignment = alignment;
            this.notifyListeners(new TitleChangeEvent(this));
        }
    }

    public boolean getNotify() {
        return this.notify;
    }

    public void setNotify(boolean flag) {
        this.notify = flag;
        if (flag) {
            this.notifyListeners(new TitleChangeEvent(this));
        }
    }

    public abstract void draw(Graphics2D var1, Rectangle2D var2);

    public Object clone() throws CloneNotSupportedException {
        Title duplicate = (Title)super.clone();
        duplicate.listenerList = new EventListenerList();
        return duplicate;
    }

    public void addChangeListener(TitleChangeListener listener) {
        this.listenerList.add(class$org$jfree$chart$event$TitleChangeListener == null ? (class$org$jfree$chart$event$TitleChangeListener = Title.class$("org.jfree.chart.event.TitleChangeListener")) : class$org$jfree$chart$event$TitleChangeListener, listener);
    }

    public void removeChangeListener(TitleChangeListener listener) {
        this.listenerList.remove(class$org$jfree$chart$event$TitleChangeListener == null ? (class$org$jfree$chart$event$TitleChangeListener = Title.class$("org.jfree.chart.event.TitleChangeListener")) : class$org$jfree$chart$event$TitleChangeListener, listener);
    }

    protected void notifyListeners(TitleChangeEvent event) {
        if (this.notify) {
            Object[] listeners = this.listenerList.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] != (class$org$jfree$chart$event$TitleChangeListener == null ? Title.class$("org.jfree.chart.event.TitleChangeListener") : class$org$jfree$chart$event$TitleChangeListener)) continue;
                ((TitleChangeListener)listeners[i + 1]).titleChanged(event);
            }
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Title)) {
            return false;
        }
        Title that = (Title)obj;
        if (this.visible != that.visible) {
            return false;
        }
        if (this.position != that.position) {
            return false;
        }
        if (this.horizontalAlignment != that.horizontalAlignment) {
            return false;
        }
        if (this.verticalAlignment != that.verticalAlignment) {
            return false;
        }
        if (this.notify != that.notify) {
            return false;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        int result = 193;
        result = 37 * result + ObjectUtilities.hashCode(this.position);
        result = 37 * result + ObjectUtilities.hashCode(this.horizontalAlignment);
        result = 37 * result + ObjectUtilities.hashCode(this.verticalAlignment);
        return result;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
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

