/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.HaltingThread
 */
package org.apache.batik.gvt;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.batik.gvt.AbstractGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.util.HaltingThread;

public class CompositeGraphicsNode
extends AbstractGraphicsNode
implements List {
    public static final Rectangle2D VIEWPORT = new Rectangle();
    public static final Rectangle2D NULL_RECT = new Rectangle();
    protected GraphicsNode[] children;
    protected volatile int count;
    protected volatile int modCount;
    protected Rectangle2D backgroundEnableRgn = null;
    private volatile Rectangle2D geometryBounds;
    private volatile Rectangle2D primitiveBounds;
    private volatile Rectangle2D sensitiveBounds;
    private Shape outline;

    public List getChildren() {
        return this;
    }

    public void setBackgroundEnable(Rectangle2D bgRgn) {
        this.backgroundEnableRgn = bgRgn;
    }

    public Rectangle2D getBackgroundEnable() {
        return this.backgroundEnableRgn;
    }

    @Override
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    @Override
    public void primitivePaint(Graphics2D g2d) {
        if (this.count == 0) {
            return;
        }
        Thread currentThread = Thread.currentThread();
        for (int i = 0; i < this.count; ++i) {
            if (HaltingThread.hasBeenHalted((Thread)currentThread)) {
                return;
            }
            GraphicsNode node = this.children[i];
            if (node == null) continue;
            node.paint(g2d);
        }
    }

    @Override
    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        this.geometryBounds = null;
        this.primitiveBounds = null;
        this.sensitiveBounds = null;
        this.outline = null;
    }

    @Override
    public Rectangle2D getPrimitiveBounds() {
        if (this.primitiveBounds != null) {
            if (this.primitiveBounds == NULL_RECT) {
                return null;
            }
            return this.primitiveBounds;
        }
        Thread currentThread = Thread.currentThread();
        int i = 0;
        Rectangle2D bounds = null;
        while (bounds == null && i < this.count) {
            bounds = this.children[i++].getTransformedBounds(IDENTITY);
            if ((i & 0xF) != 0 || !HaltingThread.hasBeenHalted((Thread)currentThread)) continue;
        }
        if (HaltingThread.hasBeenHalted((Thread)currentThread)) {
            this.invalidateGeometryCache();
            return null;
        }
        if (bounds == null) {
            this.primitiveBounds = NULL_RECT;
            return null;
        }
        this.primitiveBounds = bounds;
        while (i < this.count) {
            Rectangle2D ctb;
            if ((ctb = this.children[i++].getTransformedBounds(IDENTITY)) != null) {
                if (this.primitiveBounds == null) {
                    return null;
                }
                this.primitiveBounds.add(ctb);
            }
            if ((i & 0xF) != 0 || !HaltingThread.hasBeenHalted((Thread)currentThread)) continue;
            break;
        }
        if (HaltingThread.hasBeenHalted((Thread)currentThread)) {
            this.invalidateGeometryCache();
        }
        return this.primitiveBounds;
    }

    public static Rectangle2D getTransformedBBox(Rectangle2D r2d, AffineTransform t) {
        if (t == null || r2d == null) {
            return r2d;
        }
        double x = r2d.getX();
        double w = r2d.getWidth();
        double y = r2d.getY();
        double h = r2d.getHeight();
        double sx = t.getScaleX();
        double sy = t.getScaleY();
        if (sx < 0.0) {
            x = -(x + w);
            sx = -sx;
        }
        if (sy < 0.0) {
            y = -(y + h);
            sy = -sy;
        }
        return new Rectangle2D.Float((float)(x * sx + t.getTranslateX()), (float)(y * sy + t.getTranslateY()), (float)(w * sx), (float)(h * sy));
    }

    @Override
    public Rectangle2D getTransformedPrimitiveBounds(AffineTransform txf) {
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        if (t == null || t.getShearX() == 0.0 && t.getShearY() == 0.0) {
            return CompositeGraphicsNode.getTransformedBBox(this.getPrimitiveBounds(), t);
        }
        int i = 0;
        Rectangle2D tpb = null;
        while (tpb == null && i < this.count) {
            tpb = this.children[i++].getTransformedBounds(t);
        }
        while (i < this.count) {
            Rectangle2D ctb;
            if ((ctb = this.children[i++].getTransformedBounds(t)) == null) continue;
            tpb.add(ctb);
        }
        return tpb;
    }

    @Override
    public Rectangle2D getGeometryBounds() {
        if (this.geometryBounds == null) {
            int i = 0;
            while (this.geometryBounds == null && i < this.count) {
                this.geometryBounds = this.children[i++].getTransformedGeometryBounds(IDENTITY);
            }
            while (i < this.count) {
                Rectangle2D cgb;
                if ((cgb = this.children[i++].getTransformedGeometryBounds(IDENTITY)) == null) continue;
                if (this.geometryBounds == null) {
                    return this.getGeometryBounds();
                }
                this.geometryBounds.add(cgb);
            }
        }
        return this.geometryBounds;
    }

    @Override
    public Rectangle2D getTransformedGeometryBounds(AffineTransform txf) {
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        if (t == null || t.getShearX() == 0.0 && t.getShearY() == 0.0) {
            return CompositeGraphicsNode.getTransformedBBox(this.getGeometryBounds(), t);
        }
        Rectangle2D gb = null;
        int i = 0;
        while (gb == null && i < this.count) {
            gb = this.children[i++].getTransformedGeometryBounds(t);
        }
        Rectangle2D cgb = null;
        while (i < this.count) {
            if ((cgb = this.children[i++].getTransformedGeometryBounds(t)) == null) continue;
            gb.add(cgb);
        }
        return gb;
    }

    @Override
    public Rectangle2D getSensitiveBounds() {
        if (this.sensitiveBounds != null) {
            return this.sensitiveBounds;
        }
        int i = 0;
        while (this.sensitiveBounds == null && i < this.count) {
            this.sensitiveBounds = this.children[i++].getTransformedSensitiveBounds(IDENTITY);
        }
        while (i < this.count) {
            Rectangle2D cgb;
            if ((cgb = this.children[i++].getTransformedSensitiveBounds(IDENTITY)) == null) continue;
            if (this.sensitiveBounds == null) {
                return this.getSensitiveBounds();
            }
            this.sensitiveBounds.add(cgb);
        }
        return this.sensitiveBounds;
    }

    @Override
    public Rectangle2D getTransformedSensitiveBounds(AffineTransform txf) {
        AffineTransform t = txf;
        if (this.transform != null) {
            t = new AffineTransform(txf);
            t.concatenate(this.transform);
        }
        if (t == null || t.getShearX() == 0.0 && t.getShearY() == 0.0) {
            return CompositeGraphicsNode.getTransformedBBox(this.getSensitiveBounds(), t);
        }
        Rectangle2D sb = null;
        int i = 0;
        while (sb == null && i < this.count) {
            sb = this.children[i++].getTransformedSensitiveBounds(t);
        }
        while (i < this.count) {
            Rectangle2D csb;
            if ((csb = this.children[i++].getTransformedSensitiveBounds(t)) == null) continue;
            sb.add(csb);
        }
        return sb;
    }

    @Override
    public boolean contains(Point2D p) {
        Rectangle2D bounds = this.getSensitiveBounds();
        if (this.count > 0 && bounds != null && bounds.contains(p)) {
            Point2D pt = null;
            Point2D cp = null;
            for (int i = 0; i < this.count; ++i) {
                AffineTransform t = this.children[i].getInverseTransform();
                cp = t != null ? (pt = t.transform(p, pt)) : p;
                if (!this.children[i].contains(cp)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public GraphicsNode nodeHitAt(Point2D p) {
        Rectangle2D bounds = this.getSensitiveBounds();
        if (this.count > 0 && bounds != null && bounds.contains(p)) {
            Point2D pt = null;
            Point2D cp = null;
            for (int i = this.count - 1; i >= 0; --i) {
                AffineTransform t = this.children[i].getInverseTransform();
                cp = t != null ? (pt = t.transform(p, pt)) : p;
                GraphicsNode node = this.children[i].nodeHitAt(cp);
                if (node == null) continue;
                return node;
            }
        }
        return null;
    }

    @Override
    public Shape getOutline() {
        if (this.outline != null) {
            return this.outline;
        }
        this.outline = new GeneralPath();
        for (int i = 0; i < this.count; ++i) {
            Shape childOutline = this.children[i].getOutline();
            if (childOutline == null) continue;
            AffineTransform tr = this.children[i].getTransform();
            if (tr != null) {
                ((GeneralPath)this.outline).append(tr.createTransformedShape(childOutline), false);
                continue;
            }
            ((GeneralPath)this.outline).append(childOutline, false);
        }
        return this.outline;
    }

    @Override
    protected void setRoot(RootGraphicsNode newRoot) {
        super.setRoot(newRoot);
        for (int i = 0; i < this.count; ++i) {
            GraphicsNode node = this.children[i];
            ((AbstractGraphicsNode)node).setRoot(newRoot);
        }
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }

    @Override
    public boolean contains(Object node) {
        return this.indexOf(node) >= 0;
    }

    @Override
    public Iterator iterator() {
        return new Itr();
    }

    @Override
    public Object[] toArray() {
        Object[] result = new GraphicsNode[this.count];
        System.arraycopy(this.children, 0, result, 0, this.count);
        return result;
    }

    @Override
    public Object[] toArray(Object[] a) {
        if (a.length < this.count) {
            a = new GraphicsNode[this.count];
        }
        System.arraycopy(this.children, 0, a, 0, this.count);
        if (a.length > this.count) {
            a[this.count] = null;
        }
        return a;
    }

    public Object get(int index) {
        this.checkRange(index);
        return this.children[index];
    }

    public Object set(int index, Object o) {
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o + " is not a GraphicsNode");
        }
        this.checkRange(index);
        GraphicsNode node = (GraphicsNode)o;
        this.fireGraphicsNodeChangeStarted(node);
        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }
        GraphicsNode oldNode = this.children[index];
        this.children[index] = node;
        ((AbstractGraphicsNode)node).setParent(this);
        ((AbstractGraphicsNode)oldNode).setParent(null);
        ((AbstractGraphicsNode)node).setRoot(this.getRoot());
        ((AbstractGraphicsNode)oldNode).setRoot(null);
        this.invalidateGeometryCache();
        this.fireGraphicsNodeChangeCompleted();
        return oldNode;
    }

    @Override
    public boolean add(Object o) {
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o + " is not a GraphicsNode");
        }
        GraphicsNode node = (GraphicsNode)o;
        this.fireGraphicsNodeChangeStarted(node);
        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }
        this.ensureCapacity(this.count + 1);
        this.children[this.count++] = node;
        ((AbstractGraphicsNode)node).setParent(this);
        ((AbstractGraphicsNode)node).setRoot(this.getRoot());
        this.invalidateGeometryCache();
        this.fireGraphicsNodeChangeCompleted();
        return true;
    }

    public void add(int index, Object o) {
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o + " is not a GraphicsNode");
        }
        if (index > this.count || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.count);
        }
        GraphicsNode node = (GraphicsNode)o;
        this.fireGraphicsNodeChangeStarted(node);
        if (node.getParent() != null) {
            node.getParent().getChildren().remove(node);
        }
        this.ensureCapacity(this.count + 1);
        System.arraycopy(this.children, index, this.children, index + 1, this.count - index);
        this.children[index] = node;
        ++this.count;
        ((AbstractGraphicsNode)node).setParent(this);
        ((AbstractGraphicsNode)node).setRoot(this.getRoot());
        this.invalidateGeometryCache();
        this.fireGraphicsNodeChangeCompleted();
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        if (!(o instanceof GraphicsNode)) {
            throw new IllegalArgumentException(o + " is not a GraphicsNode");
        }
        GraphicsNode node = (GraphicsNode)o;
        if (node.getParent() != this) {
            return false;
        }
        int index = 0;
        while (node != this.children[index]) {
            ++index;
        }
        this.remove(index);
        return true;
    }

    public Object remove(int index) {
        this.checkRange(index);
        GraphicsNode oldNode = this.children[index];
        this.fireGraphicsNodeChangeStarted(oldNode);
        ++this.modCount;
        int numMoved = this.count - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.children, index + 1, this.children, index, numMoved);
        }
        this.children[--this.count] = null;
        if (this.count == 0) {
            this.children = null;
        }
        ((AbstractGraphicsNode)oldNode).setParent(null);
        ((AbstractGraphicsNode)oldNode).setRoot(null);
        this.invalidateGeometryCache();
        this.fireGraphicsNodeChangeCompleted();
        return oldNode;
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection c) {
        for (Object aC : c) {
            if (this.contains(aC)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int indexOf(Object node) {
        if (node == null || !(node instanceof GraphicsNode)) {
            return -1;
        }
        if (((GraphicsNode)node).getParent() == this) {
            int iCount = this.count;
            GraphicsNode[] workList = this.children;
            for (int i = 0; i < iCount; ++i) {
                if (node != workList[i]) continue;
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object node) {
        if (node == null || !(node instanceof GraphicsNode)) {
            return -1;
        }
        if (((GraphicsNode)node).getParent() == this) {
            for (int i = this.count - 1; i >= 0; --i) {
                if (node != this.children[i]) continue;
                return i;
            }
        }
        return -1;
    }

    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    public ListIterator listIterator(int index) {
        if (index < 0 || index > this.count) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        return new ListItr(index);
    }

    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    private void checkRange(int index) {
        if (index >= this.count || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.count);
        }
    }

    public void ensureCapacity(int minCapacity) {
        if (this.children == null) {
            this.children = new GraphicsNode[4];
        }
        ++this.modCount;
        int oldCapacity = this.children.length;
        if (minCapacity > oldCapacity) {
            GraphicsNode[] oldData = this.children;
            int newCapacity = oldCapacity + oldCapacity / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            this.children = new GraphicsNode[newCapacity];
            System.arraycopy(oldData, 0, this.children, 0, this.count);
        }
    }

    private class ListItr
    extends Itr
    implements ListIterator {
        ListItr(int index) {
            this.cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return this.cursor != 0;
        }

        public Object previous() {
            try {
                Object previous = CompositeGraphicsNode.this.get(--this.cursor);
                this.checkForComodification();
                this.lastRet = this.cursor;
                return previous;
            }
            catch (IndexOutOfBoundsException e) {
                this.checkForComodification();
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return this.cursor;
        }

        @Override
        public int previousIndex() {
            return this.cursor - 1;
        }

        public void set(Object o) {
            if (this.lastRet == -1) {
                throw new IllegalStateException();
            }
            this.checkForComodification();
            try {
                CompositeGraphicsNode.this.set(this.lastRet, o);
                this.expectedModCount = CompositeGraphicsNode.this.modCount;
            }
            catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(Object o) {
            this.checkForComodification();
            try {
                CompositeGraphicsNode.this.add(this.cursor++, o);
                this.lastRet = -1;
                this.expectedModCount = CompositeGraphicsNode.this.modCount;
            }
            catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class Itr
    implements Iterator {
        int cursor = 0;
        int lastRet = -1;
        int expectedModCount;

        private Itr() {
            this.expectedModCount = CompositeGraphicsNode.this.modCount;
        }

        @Override
        public boolean hasNext() {
            return this.cursor != CompositeGraphicsNode.this.count;
        }

        public Object next() {
            try {
                Object next = CompositeGraphicsNode.this.get(this.cursor);
                this.checkForComodification();
                this.lastRet = this.cursor++;
                return next;
            }
            catch (IndexOutOfBoundsException e) {
                this.checkForComodification();
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            if (this.lastRet == -1) {
                throw new IllegalStateException();
            }
            this.checkForComodification();
            try {
                CompositeGraphicsNode.this.remove(this.lastRet);
                if (this.lastRet < this.cursor) {
                    --this.cursor;
                }
                this.lastRet = -1;
                this.expectedModCount = CompositeGraphicsNode.this.modCount;
            }
            catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (CompositeGraphicsNode.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}

