/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.RootGraphicsNode
 *  org.apache.batik.gvt.Selectable
 *  org.apache.batik.gvt.Selector
 *  org.apache.batik.gvt.event.GraphicsNodeChangeEvent
 *  org.apache.batik.gvt.event.GraphicsNodeChangeListener
 *  org.apache.batik.gvt.event.GraphicsNodeEvent
 *  org.apache.batik.gvt.event.GraphicsNodeKeyEvent
 *  org.apache.batik.gvt.event.GraphicsNodeMouseEvent
 *  org.apache.batik.gvt.event.SelectionEvent
 *  org.apache.batik.gvt.event.SelectionListener
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.batik.bridge.Mark;
import org.apache.batik.bridge.TextNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.Selectable;
import org.apache.batik.gvt.Selector;
import org.apache.batik.gvt.event.GraphicsNodeChangeEvent;
import org.apache.batik.gvt.event.GraphicsNodeChangeListener;
import org.apache.batik.gvt.event.GraphicsNodeEvent;
import org.apache.batik.gvt.event.GraphicsNodeKeyEvent;
import org.apache.batik.gvt.event.GraphicsNodeMouseEvent;
import org.apache.batik.gvt.event.SelectionEvent;
import org.apache.batik.gvt.event.SelectionListener;

public class ConcreteTextSelector
implements Selector {
    private ArrayList listeners;
    private GraphicsNode selectionNode;
    private RootGraphicsNode selectionNodeRoot;

    public void mouseClicked(GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture((GraphicsNodeEvent)evt);
    }

    public void mouseDragged(GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture((GraphicsNodeEvent)evt);
    }

    public void mouseEntered(GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture((GraphicsNodeEvent)evt);
    }

    public void mouseExited(GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture((GraphicsNodeEvent)evt);
    }

    public void mouseMoved(GraphicsNodeMouseEvent evt) {
    }

    public void mousePressed(GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture((GraphicsNodeEvent)evt);
    }

    public void mouseReleased(GraphicsNodeMouseEvent evt) {
        this.checkSelectGesture((GraphicsNodeEvent)evt);
    }

    public void keyPressed(GraphicsNodeKeyEvent evt) {
        this.report((GraphicsNodeEvent)evt, "keyPressed");
    }

    public void keyReleased(GraphicsNodeKeyEvent evt) {
        this.report((GraphicsNodeEvent)evt, "keyReleased");
    }

    public void keyTyped(GraphicsNodeKeyEvent evt) {
        this.report((GraphicsNodeEvent)evt, "keyTyped");
    }

    public void changeStarted(GraphicsNodeChangeEvent gnce) {
    }

    public void changeCompleted(GraphicsNodeChangeEvent gnce) {
        if (this.selectionNode == null) {
            return;
        }
        Shape newShape = ((Selectable)this.selectionNode).getHighlightShape();
        this.dispatchSelectionEvent(new SelectionEvent(this.getSelection(), 1, newShape));
    }

    public void setSelection(Mark begin, Mark end) {
        TextNode node = begin.getTextNode();
        if (node != end.getTextNode()) {
            throw new RuntimeException("Markers not from same TextNode");
        }
        node.setSelection(begin, end);
        this.selectionNode = node;
        this.selectionNodeRoot = node.getRoot();
        Object selection = this.getSelection();
        Shape shape = node.getHighlightShape();
        this.dispatchSelectionEvent(new SelectionEvent(selection, 2, shape));
    }

    public void clearSelection() {
        if (this.selectionNode == null) {
            return;
        }
        this.dispatchSelectionEvent(new SelectionEvent(null, 3, null));
        this.selectionNode = null;
        this.selectionNodeRoot = null;
    }

    protected void checkSelectGesture(GraphicsNodeEvent evt) {
        GraphicsNodeMouseEvent mevt = null;
        if (evt instanceof GraphicsNodeMouseEvent) {
            mevt = (GraphicsNodeMouseEvent)evt;
        }
        GraphicsNode source = evt.getGraphicsNode();
        if (this.isDeselectGesture(evt)) {
            if (this.selectionNode != null) {
                this.selectionNodeRoot.removeTreeGraphicsNodeChangeListener((GraphicsNodeChangeListener)this);
            }
            this.clearSelection();
        } else if (mevt != null) {
            Point2D p = mevt.getPoint2D();
            if (source instanceof Selectable && this.isSelectStartGesture(evt)) {
                if (this.selectionNode != source) {
                    if (this.selectionNode != null) {
                        this.selectionNodeRoot.removeTreeGraphicsNodeChangeListener((GraphicsNodeChangeListener)this);
                    }
                    this.selectionNode = source;
                    if (source != null) {
                        this.selectionNodeRoot = source.getRoot();
                        this.selectionNodeRoot.addTreeGraphicsNodeChangeListener((GraphicsNodeChangeListener)this);
                    }
                }
                ((Selectable)source).selectAt(p.getX(), p.getY());
                this.dispatchSelectionEvent(new SelectionEvent(null, 4, null));
            } else if (this.isSelectEndGesture(evt)) {
                if (this.selectionNode == source) {
                    ((Selectable)source).selectTo(p.getX(), p.getY());
                }
                Object oldSelection = this.getSelection();
                if (this.selectionNode != null) {
                    Shape newShape = ((Selectable)this.selectionNode).getHighlightShape();
                    this.dispatchSelectionEvent(new SelectionEvent(oldSelection, 2, newShape));
                }
            } else if (this.isSelectContinueGesture(evt)) {
                boolean result;
                if (this.selectionNode == source && (result = ((Selectable)source).selectTo(p.getX(), p.getY()))) {
                    Shape newShape = ((Selectable)this.selectionNode).getHighlightShape();
                    this.dispatchSelectionEvent(new SelectionEvent(null, 1, newShape));
                }
            } else if (source instanceof Selectable && this.isSelectAllGesture(evt)) {
                if (this.selectionNode != source) {
                    if (this.selectionNode != null) {
                        this.selectionNodeRoot.removeTreeGraphicsNodeChangeListener((GraphicsNodeChangeListener)this);
                    }
                    this.selectionNode = source;
                    if (source != null) {
                        this.selectionNodeRoot = source.getRoot();
                        this.selectionNodeRoot.addTreeGraphicsNodeChangeListener((GraphicsNodeChangeListener)this);
                    }
                }
                ((Selectable)source).selectAll(p.getX(), p.getY());
                Object oldSelection = this.getSelection();
                Shape newShape = ((Selectable)source).getHighlightShape();
                this.dispatchSelectionEvent(new SelectionEvent(oldSelection, 2, newShape));
            }
        }
    }

    private boolean isDeselectGesture(GraphicsNodeEvent evt) {
        return evt.getID() == 500 && ((GraphicsNodeMouseEvent)evt).getClickCount() == 1;
    }

    private boolean isSelectStartGesture(GraphicsNodeEvent evt) {
        return evt.getID() == 501;
    }

    private boolean isSelectEndGesture(GraphicsNodeEvent evt) {
        return evt.getID() == 502;
    }

    private boolean isSelectContinueGesture(GraphicsNodeEvent evt) {
        return evt.getID() == 506;
    }

    private boolean isSelectAllGesture(GraphicsNodeEvent evt) {
        return evt.getID() == 500 && ((GraphicsNodeMouseEvent)evt).getClickCount() == 2;
    }

    public Object getSelection() {
        Object value = null;
        if (this.selectionNode instanceof Selectable) {
            value = ((Selectable)this.selectionNode).getSelection();
        }
        return value;
    }

    public boolean isEmpty() {
        return this.getSelection() == null;
    }

    public void dispatchSelectionEvent(SelectionEvent e) {
        if (this.listeners != null) {
            Iterator iter = this.listeners.iterator();
            switch (e.getID()) {
                case 2: {
                    while (iter.hasNext()) {
                        ((SelectionListener)iter.next()).selectionDone(e);
                    }
                    break;
                }
                case 1: {
                    while (iter.hasNext()) {
                        ((SelectionListener)iter.next()).selectionChanged(e);
                    }
                    break;
                }
                case 3: {
                    while (iter.hasNext()) {
                        ((SelectionListener)iter.next()).selectionCleared(e);
                    }
                    break;
                }
                case 4: {
                    while (iter.hasNext()) {
                        ((SelectionListener)iter.next()).selectionStarted(e);
                    }
                    break;
                }
            }
        }
    }

    public void addSelectionListener(SelectionListener l) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(l);
    }

    public void removeSelectionListener(SelectionListener l) {
        if (this.listeners != null) {
            this.listeners.remove(l);
        }
    }

    private void report(GraphicsNodeEvent evt, String message) {
        GraphicsNode source = evt.getGraphicsNode();
        String label = "(non-text node)";
        if (source instanceof TextNode) {
            AttributedCharacterIterator iter = ((TextNode)source).getAttributedCharacterIterator();
            char[] cbuff = new char[iter.getEndIndex()];
            if (cbuff.length > 0) {
                cbuff[0] = iter.first();
            }
            for (int i = 1; i < cbuff.length; ++i) {
                cbuff[i] = iter.next();
            }
            label = new String(cbuff);
        }
        System.out.println("Mouse " + message + " in " + label);
    }
}

