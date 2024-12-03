/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.event;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.EventListener;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.EventDispatcher;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseListener;
import org.apache.batik.gvt.event.GraphicsNodeMouseWheelEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseWheelListener;

public class AWTEventDispatcher
implements EventDispatcher,
MouseListener,
MouseMotionListener,
MouseWheelListener,
KeyListener {
    protected GraphicsNode root;
    protected AffineTransform baseTransform;
    protected EventListenerList glisteners;
    protected GraphicsNode lastHit;
    protected GraphicsNode currentKeyEventTarget;
    protected List eventQueue = new LinkedList();
    protected boolean eventDispatchEnabled = true;
    protected int eventQueueMaxSize = 10;
    static final int MAX_QUEUE_SIZE = 10;
    private int nodeIncrementEventID = 401;
    private int nodeIncrementEventCode = 9;
    private int nodeIncrementEventModifiers = 0;
    private int nodeDecrementEventID = 401;
    private int nodeDecrementEventCode = 9;
    private int nodeDecrementEventModifiers = 64;

    @Override
    public void setRootNode(GraphicsNode root) {
        if (this.root != root) {
            this.eventQueue.clear();
        }
        this.root = root;
    }

    @Override
    public GraphicsNode getRootNode() {
        return this.root;
    }

    @Override
    public void setBaseTransform(AffineTransform t) {
        if (!(this.baseTransform == t || this.baseTransform != null && this.baseTransform.equals(t))) {
            this.eventQueue.clear();
        }
        this.baseTransform = t;
    }

    @Override
    public AffineTransform getBaseTransform() {
        return new AffineTransform(this.baseTransform);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void mouseEntered(MouseEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void mouseExited(MouseEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void keyTyped(KeyEvent evt) {
        this.dispatchEvent(evt);
    }

    @Override
    public void addGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
        if (this.glisteners == null) {
            this.glisteners = new EventListenerList();
        }
        this.glisteners.add(GraphicsNodeMouseListener.class, l);
    }

    @Override
    public void removeGraphicsNodeMouseListener(GraphicsNodeMouseListener l) {
        if (this.glisteners != null) {
            this.glisteners.remove(GraphicsNodeMouseListener.class, l);
        }
    }

    @Override
    public void addGraphicsNodeMouseWheelListener(GraphicsNodeMouseWheelListener l) {
        if (this.glisteners == null) {
            this.glisteners = new EventListenerList();
        }
        this.glisteners.add(GraphicsNodeMouseWheelListener.class, l);
    }

    @Override
    public void removeGraphicsNodeMouseWheelListener(GraphicsNodeMouseWheelListener l) {
        if (this.glisteners != null) {
            this.glisteners.remove(GraphicsNodeMouseWheelListener.class, l);
        }
    }

    @Override
    public void addGraphicsNodeKeyListener(GraphicsNodeKeyListener l) {
        if (this.glisteners == null) {
            this.glisteners = new EventListenerList();
        }
        this.glisteners.add(GraphicsNodeKeyListener.class, l);
    }

    @Override
    public void removeGraphicsNodeKeyListener(GraphicsNodeKeyListener l) {
        if (this.glisteners != null) {
            this.glisteners.remove(GraphicsNodeKeyListener.class, l);
        }
    }

    @Override
    public EventListener[] getListeners(Class listenerType) {
        Object array = Array.newInstance(listenerType, this.glisteners.getListenerCount(listenerType));
        Object[] pairElements = this.glisteners.getListenerList();
        int j = 0;
        for (int i = 0; i < pairElements.length - 1; i += 2) {
            if (!pairElements[i].equals(listenerType)) continue;
            Array.set(array, j, pairElements[i + 1]);
            ++j;
        }
        return (EventListener[])array;
    }

    public void setEventDispatchEnabled(boolean b) {
        this.eventDispatchEnabled = b;
        if (this.eventDispatchEnabled) {
            while (this.eventQueue.size() > 0) {
                EventObject evt = (EventObject)this.eventQueue.remove(0);
                this.dispatchEvent(evt);
            }
        }
    }

    public void setEventQueueMaxSize(int n) {
        this.eventQueueMaxSize = n;
        if (n == 0) {
            this.eventQueue.clear();
        }
        while (this.eventQueue.size() > this.eventQueueMaxSize) {
            this.eventQueue.remove(0);
        }
    }

    @Override
    public void dispatchEvent(EventObject evt) {
        if (this.root == null) {
            return;
        }
        if (!this.eventDispatchEnabled) {
            if (this.eventQueueMaxSize > 0) {
                this.eventQueue.add(evt);
                while (this.eventQueue.size() > this.eventQueueMaxSize) {
                    this.eventQueue.remove(0);
                }
            }
            return;
        }
        if (evt instanceof MouseWheelEvent) {
            this.dispatchMouseWheelEvent((MouseWheelEvent)evt);
        } else if (evt instanceof MouseEvent) {
            this.dispatchMouseEvent((MouseEvent)evt);
        } else if (evt instanceof KeyEvent) {
            InputEvent e = (InputEvent)evt;
            if (this.isNodeIncrementEvent(e)) {
                this.incrementKeyTarget();
            } else if (this.isNodeDecrementEvent(e)) {
                this.decrementKeyTarget();
            } else {
                this.dispatchKeyEvent((KeyEvent)evt);
            }
        }
    }

    protected int getCurrentLockState() {
        Toolkit t = Toolkit.getDefaultToolkit();
        int lockState = 0;
        try {
            if (t.getLockingKeyState(262)) {
                ++lockState;
            }
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        lockState <<= 1;
        try {
            if (t.getLockingKeyState(145)) {
                ++lockState;
            }
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        lockState <<= 1;
        try {
            if (t.getLockingKeyState(144)) {
                ++lockState;
            }
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        lockState <<= 1;
        try {
            if (t.getLockingKeyState(20)) {
                ++lockState;
            }
        }
        catch (UnsupportedOperationException unsupportedOperationException) {
            // empty catch block
        }
        return lockState;
    }

    protected void dispatchKeyEvent(KeyEvent evt) {
        this.currentKeyEventTarget = this.lastHit;
        GraphicsNode target = this.currentKeyEventTarget == null ? this.root : this.currentKeyEventTarget;
        this.processKeyEvent(new GraphicsNodeKeyEvent(target, evt.getID(), evt.getWhen(), evt.getModifiersEx(), this.getCurrentLockState(), evt.getKeyCode(), evt.getKeyChar(), evt.getKeyLocation()));
    }

    protected void dispatchMouseEvent(MouseEvent evt) {
        GraphicsNodeMouseEvent gvtevt;
        Point screenPos;
        GraphicsNode node;
        Point2D.Float p = new Point2D.Float(evt.getX(), evt.getY());
        Point2D gnp = p;
        if (this.baseTransform != null) {
            gnp = this.baseTransform.transform(p, null);
        }
        if ((node = this.root.nodeHitAt(gnp)) != null) {
            try {
                node.getGlobalTransform().createInverse().transform(gnp, gnp);
            }
            catch (NoninvertibleTransformException noninvertibleTransformException) {
                // empty catch block
            }
        }
        if (!evt.getComponent().isShowing()) {
            screenPos = new Point(0, 0);
        } else {
            screenPos = evt.getComponent().getLocationOnScreen();
            screenPos.x += evt.getX();
            screenPos.y += evt.getY();
        }
        int currentLockState = this.getCurrentLockState();
        if (this.lastHit != node) {
            if (this.lastHit != null) {
                gvtevt = new GraphicsNodeMouseEvent(this.lastHit, 505, evt.getWhen(), evt.getModifiersEx(), currentLockState, evt.getButton(), (float)gnp.getX(), (float)gnp.getY(), (int)Math.floor(((Point2D)p).getX()), (int)Math.floor(((Point2D)p).getY()), screenPos.x, screenPos.y, evt.getClickCount(), node);
                this.processMouseEvent(gvtevt);
            }
            if (node != null) {
                gvtevt = new GraphicsNodeMouseEvent(node, 504, evt.getWhen(), evt.getModifiersEx(), currentLockState, evt.getButton(), (float)gnp.getX(), (float)gnp.getY(), (int)Math.floor(((Point2D)p).getX()), (int)Math.floor(((Point2D)p).getY()), screenPos.x, screenPos.y, evt.getClickCount(), this.lastHit);
                this.processMouseEvent(gvtevt);
            }
        }
        if (node != null) {
            gvtevt = new GraphicsNodeMouseEvent(node, evt.getID(), evt.getWhen(), evt.getModifiersEx(), currentLockState, evt.getButton(), (float)gnp.getX(), (float)gnp.getY(), (int)Math.floor(((Point2D)p).getX()), (int)Math.floor(((Point2D)p).getY()), screenPos.x, screenPos.y, evt.getClickCount(), null);
            this.processMouseEvent(gvtevt);
        } else {
            gvtevt = new GraphicsNodeMouseEvent(this.root, evt.getID(), evt.getWhen(), evt.getModifiersEx(), currentLockState, evt.getButton(), (float)gnp.getX(), (float)gnp.getY(), (int)Math.floor(((Point2D)p).getX()), (int)Math.floor(((Point2D)p).getY()), screenPos.x, screenPos.y, evt.getClickCount(), null);
            this.processMouseEvent(gvtevt);
        }
        this.lastHit = node;
    }

    protected void dispatchMouseWheelEvent(MouseWheelEvent evt) {
        if (this.lastHit != null) {
            this.processMouseWheelEvent(new GraphicsNodeMouseWheelEvent(this.lastHit, evt.getID(), evt.getWhen(), evt.getModifiersEx(), this.getCurrentLockState(), evt.getWheelRotation()));
        }
    }

    protected void processMouseEvent(GraphicsNodeMouseEvent evt) {
        if (this.glisteners != null) {
            GraphicsNodeMouseListener[] listeners = (GraphicsNodeMouseListener[])this.getListeners(GraphicsNodeMouseListener.class);
            switch (evt.getID()) {
                case 503: {
                    for (GraphicsNodeMouseListener listener6 : listeners) {
                        listener6.mouseMoved(evt);
                    }
                    break;
                }
                case 506: {
                    for (GraphicsNodeMouseListener listener5 : listeners) {
                        listener5.mouseDragged(evt);
                    }
                    break;
                }
                case 504: {
                    for (GraphicsNodeMouseListener listener4 : listeners) {
                        listener4.mouseEntered(evt);
                    }
                    break;
                }
                case 505: {
                    for (GraphicsNodeMouseListener listener3 : listeners) {
                        listener3.mouseExited(evt);
                    }
                    break;
                }
                case 500: {
                    for (GraphicsNodeMouseListener listener2 : listeners) {
                        listener2.mouseClicked(evt);
                    }
                    break;
                }
                case 501: {
                    for (GraphicsNodeMouseListener listener1 : listeners) {
                        listener1.mousePressed(evt);
                    }
                    break;
                }
                case 502: {
                    for (GraphicsNodeMouseListener listener : listeners) {
                        listener.mouseReleased(evt);
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown Mouse Event type: " + evt.getID());
                }
            }
        }
    }

    protected void processMouseWheelEvent(GraphicsNodeMouseWheelEvent evt) {
        if (this.glisteners != null) {
            GraphicsNodeMouseWheelListener[] listeners;
            for (GraphicsNodeMouseWheelListener listener : listeners = (GraphicsNodeMouseWheelListener[])this.getListeners(GraphicsNodeMouseWheelListener.class)) {
                listener.mouseWheelMoved(evt);
            }
        }
    }

    public void processKeyEvent(GraphicsNodeKeyEvent evt) {
        if (this.glisteners != null) {
            GraphicsNodeKeyListener[] listeners = (GraphicsNodeKeyListener[])this.getListeners(GraphicsNodeKeyListener.class);
            switch (evt.getID()) {
                case 401: {
                    for (GraphicsNodeKeyListener listener2 : listeners) {
                        listener2.keyPressed(evt);
                    }
                    break;
                }
                case 402: {
                    for (GraphicsNodeKeyListener listener1 : listeners) {
                        listener1.keyReleased(evt);
                    }
                    break;
                }
                case 400: {
                    for (GraphicsNodeKeyListener listener : listeners) {
                        listener.keyTyped(evt);
                    }
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown Key Event type: " + evt.getID());
                }
            }
        }
        evt.consume();
    }

    private void incrementKeyTarget() {
        throw new UnsupportedOperationException("Increment not implemented.");
    }

    private void decrementKeyTarget() {
        throw new UnsupportedOperationException("Decrement not implemented.");
    }

    @Override
    public void setNodeIncrementEvent(InputEvent e) {
        this.nodeIncrementEventID = e.getID();
        if (e instanceof KeyEvent) {
            this.nodeIncrementEventCode = ((KeyEvent)e).getKeyCode();
        }
        this.nodeIncrementEventModifiers = e.getModifiersEx();
    }

    @Override
    public void setNodeDecrementEvent(InputEvent e) {
        this.nodeDecrementEventID = e.getID();
        if (e instanceof KeyEvent) {
            this.nodeDecrementEventCode = ((KeyEvent)e).getKeyCode();
        }
        this.nodeDecrementEventModifiers = e.getModifiersEx();
    }

    protected boolean isNodeIncrementEvent(InputEvent e) {
        if (e.getID() != this.nodeIncrementEventID) {
            return false;
        }
        if (e instanceof KeyEvent && ((KeyEvent)e).getKeyCode() != this.nodeIncrementEventCode) {
            return false;
        }
        return (e.getModifiersEx() & this.nodeIncrementEventModifiers) != 0;
    }

    protected boolean isNodeDecrementEvent(InputEvent e) {
        if (e.getID() != this.nodeDecrementEventID) {
            return false;
        }
        if (e instanceof KeyEvent && ((KeyEvent)e).getKeyCode() != this.nodeDecrementEventCode) {
            return false;
        }
        return (e.getModifiersEx() & this.nodeDecrementEventModifiers) != 0;
    }

    protected static boolean isMetaDown(int modifiers) {
        return (modifiers & 0x100) != 0;
    }
}

