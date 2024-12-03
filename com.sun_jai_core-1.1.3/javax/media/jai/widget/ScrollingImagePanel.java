/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.widget;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.RenderedImage;
import java.util.Vector;
import javax.media.jai.widget.ImageCanvas;
import javax.media.jai.widget.ViewportListener;

public class ScrollingImagePanel
extends ScrollPane
implements AdjustmentListener,
ComponentListener,
MouseListener,
MouseMotionListener {
    protected ImageCanvas ic;
    protected RenderedImage im;
    protected int panelWidth;
    protected int panelHeight;
    protected Vector viewportListeners = new Vector();
    protected Point moveSource;
    protected boolean beingDragged = false;
    protected Cursor defaultCursor = null;

    public ScrollingImagePanel(RenderedImage im, int width, int height) {
        this.im = im;
        this.panelWidth = width;
        this.panelHeight = height;
        this.ic = new ImageCanvas(im);
        this.getHAdjustable().addAdjustmentListener(this);
        this.getVAdjustable().addAdjustmentListener(this);
        super.setSize(width, height);
        this.addComponentListener(this);
        this.add("Center", this.ic);
    }

    public void addViewportListener(ViewportListener l) {
        this.viewportListeners.addElement(l);
        l.setViewport(this.getXOrigin(), this.getYOrigin(), this.panelWidth, this.panelHeight);
    }

    public void removeViewportListener(ViewportListener l) {
        this.viewportListeners.removeElement(l);
    }

    private void notifyViewportListeners(int x, int y, int w, int h) {
        int numListeners = this.viewportListeners.size();
        for (int i = 0; i < numListeners; ++i) {
            ViewportListener l = (ViewportListener)this.viewportListeners.elementAt(i);
            l.setViewport(x, y, w, h);
        }
    }

    public ImageCanvas getImageCanvas() {
        return this.ic;
    }

    public int getXOrigin() {
        return this.ic.getXOrigin();
    }

    public int getYOrigin() {
        return this.ic.getYOrigin();
    }

    public void setOrigin(int x, int y) {
        this.ic.setOrigin(x, y);
        this.notifyViewportListeners(x, y, this.panelWidth, this.panelHeight);
    }

    public synchronized void setCenter(int x, int y) {
        int sx = 0;
        int sy = 0;
        int iw = this.im.getWidth();
        int ih = this.im.getHeight();
        int vw = this.getViewportSize().width;
        int vh = this.getViewportSize().height;
        int fx = this.getHAdjustable().getBlockIncrement();
        int fy = this.getVAdjustable().getBlockIncrement();
        sx = x < vw - iw / 2 ? 0 : (x > iw / 2 ? iw - vw : x + (iw - vw - fx) / 2);
        sy = y < vh - ih / 2 ? 0 : (y > ih / 2 ? ih - vh : y + (ih - vh - fy) / 2);
        this.getHAdjustable().setValue(sx);
        this.getVAdjustable().setValue(sy);
        this.notifyViewportListeners(this.getXOrigin(), this.getYOrigin(), this.panelWidth, this.panelHeight);
    }

    public void set(RenderedImage im) {
        this.im = im;
        this.ic.set(im);
    }

    public int getXCenter() {
        return this.getXOrigin() + this.panelWidth / 2;
    }

    public int getYCenter() {
        return this.getYOrigin() + this.panelHeight / 2;
    }

    public Dimension getPreferredSize() {
        return new Dimension(this.panelWidth, this.panelHeight);
    }

    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        int vpw = this.getViewportSize().width;
        int vph = this.getViewportSize().height;
        int imw = this.im.getWidth();
        int imh = this.im.getHeight();
        if (vpw >= imw && vph >= imh) {
            this.ic.setBounds(x, y, width, height);
        } else {
            this.ic.setBounds(x, y, vpw, vph);
        }
        this.panelWidth = width;
        this.panelHeight = height;
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
    }

    public void componentResized(ComponentEvent e) {
        this.notifyViewportListeners(this.getXOrigin(), this.getYOrigin(), this.panelWidth, this.panelHeight);
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    private synchronized void startDrag(Point p) {
        this.setCursor(Cursor.getPredefinedCursor(13));
        this.beingDragged = true;
        this.moveSource = p;
    }

    protected synchronized void updateDrag(Point moveTarget) {
        if (this.beingDragged) {
            int dx = this.moveSource.x - moveTarget.x;
            int dy = this.moveSource.y - moveTarget.y;
            this.moveSource = moveTarget;
            int x = this.getHAdjustable().getValue() + dx;
            int y = this.getVAdjustable().getValue() + dy;
            this.setOrigin(x, y);
        }
    }

    private synchronized void endDrag() {
        this.setCursor(Cursor.getPredefinedCursor(0));
        this.beingDragged = false;
    }

    public void mousePressed(MouseEvent me) {
        this.startDrag(me.getPoint());
    }

    public void mouseDragged(MouseEvent me) {
        this.updateDrag(me.getPoint());
    }

    public void mouseReleased(MouseEvent me) {
        this.endDrag();
    }

    public void mouseExited(MouseEvent me) {
        this.endDrag();
    }

    public void mouseClicked(MouseEvent me) {
    }

    public void mouseMoved(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
    }
}

