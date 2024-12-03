/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.MouseInputAdapter;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.FSMouseListener;

public class MouseTracker
extends MouseInputAdapter {
    private BasicPanel _panel;
    private Map _handlers;
    private Box _last;
    private boolean _enabled;

    public MouseTracker(BasicPanel panel) {
        this._panel = panel;
        this._handlers = new LinkedHashMap();
    }

    public void addListener(FSMouseListener l) {
        if (l == null) {
            return;
        }
        if (!this._handlers.containsKey(l)) {
            this._handlers.put(l, l);
        }
        if (!this._enabled && this._handlers.size() > 0) {
            this._panel.addMouseListener(this);
            this._panel.addMouseMotionListener(this);
            this._enabled = true;
        }
    }

    public void removeListener(FSMouseListener l) {
        if (l == null) {
            return;
        }
        if (this._handlers.containsKey(l)) {
            this._handlers.remove(l);
        }
        if (this._enabled && this._handlers.size() == 0) {
            this._panel.removeMouseListener(this);
            this._panel.removeMouseMotionListener(this);
            this._enabled = false;
        }
    }

    public List getListeners() {
        return new ArrayList(this._handlers.keySet());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.handleMouseMotion(this._panel.find(e));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.handleMouseMotion(this._panel.find(e));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.handleMouseMotion(this._panel.find(e));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.handleMouseUp(this._panel.find(e));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.fireMousePressed(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.fireMouseDragged(e);
    }

    public void reset() {
        this._last = null;
        Iterator iterator = this._handlers.keySet().iterator();
        while (iterator.hasNext()) {
            ((FSMouseListener)iterator.next()).reset();
        }
    }

    private void handleMouseMotion(Box box) {
        if (box == null || box.equals(this._last)) {
            return;
        }
        if (this._last != null) {
            this.fireMouseOut(this._last);
        }
        this.fireMouseOver(box);
        this._last = box;
    }

    private void handleMouseUp(Box box) {
        if (box == null) {
            return;
        }
        this.fireMouseUp(box);
    }

    private void fireMouseOver(Box box) {
        Iterator iterator = this._handlers.keySet().iterator();
        while (iterator.hasNext()) {
            ((FSMouseListener)iterator.next()).onMouseOver(this._panel, box);
        }
    }

    private void fireMouseOut(Box box) {
        Iterator iterator = this._handlers.keySet().iterator();
        while (iterator.hasNext()) {
            ((FSMouseListener)iterator.next()).onMouseOut(this._panel, box);
        }
    }

    private void fireMouseUp(Box box) {
        Iterator iterator = this._handlers.keySet().iterator();
        while (iterator.hasNext()) {
            ((FSMouseListener)iterator.next()).onMouseUp(this._panel, box);
        }
    }

    private void fireMousePressed(MouseEvent e) {
        Iterator iterator = this._handlers.keySet().iterator();
        while (iterator.hasNext()) {
            ((FSMouseListener)iterator.next()).onMousePressed(this._panel, e);
        }
    }

    private void fireMouseDragged(MouseEvent e) {
        Iterator iterator = this._handlers.keySet().iterator();
        while (iterator.hasNext()) {
            ((FSMouseListener)iterator.next()).onMouseDragged(this._panel, e);
        }
    }
}

