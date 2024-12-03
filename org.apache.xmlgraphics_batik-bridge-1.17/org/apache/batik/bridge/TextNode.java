/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.AbstractGraphicsNode
 *  org.apache.batik.gvt.Selectable
 *  org.apache.batik.gvt.text.AttributedCharacterSpanIterator
 *  org.apache.batik.gvt.text.GVTAttributedCharacterIterator$TextAttribute
 *  org.apache.batik.gvt.text.TextPaintInfo
 */
package org.apache.batik.bridge;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.util.List;
import org.apache.batik.bridge.Mark;
import org.apache.batik.bridge.StrokingTextPainter;
import org.apache.batik.bridge.TextHit;
import org.apache.batik.bridge.TextPainter;
import org.apache.batik.bridge.TextSpanLayout;
import org.apache.batik.gvt.AbstractGraphicsNode;
import org.apache.batik.gvt.Selectable;
import org.apache.batik.gvt.text.AttributedCharacterSpanIterator;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPaintInfo;

public class TextNode
extends AbstractGraphicsNode
implements Selectable {
    public static final AttributedCharacterIterator.Attribute PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
    protected Point2D location = new Point2D.Float(0.0f, 0.0f);
    protected AttributedCharacterIterator aci;
    protected String text;
    protected Mark beginMark = null;
    protected Mark endMark = null;
    protected List textRuns;
    protected TextPainter textPainter = StrokingTextPainter.getInstance();
    private Rectangle2D geometryBounds;
    private Rectangle2D primitiveBounds;
    private Shape outline;

    public void setTextPainter(TextPainter textPainter) {
        this.textPainter = textPainter == null ? StrokingTextPainter.getInstance() : textPainter;
    }

    public TextPainter getTextPainter() {
        return this.textPainter;
    }

    public List getTextRuns() {
        return this.textRuns;
    }

    public void setTextRuns(List textRuns) {
        this.textRuns = textRuns;
    }

    public String getText() {
        if (this.text != null) {
            return this.text;
        }
        if (this.aci == null) {
            this.text = "";
        } else {
            StringBuffer buf = new StringBuffer(this.aci.getEndIndex());
            char c = this.aci.first();
            while (c != '\uffff') {
                buf.append(c);
                c = this.aci.next();
            }
            this.text = buf.toString();
        }
        return this.text;
    }

    public void setLocation(Point2D newLocation) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.location = newLocation;
        this.fireGraphicsNodeChangeCompleted();
    }

    public Point2D getLocation() {
        return this.location;
    }

    public void swapTextPaintInfo(TextPaintInfo newInfo, TextPaintInfo oldInfo) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        oldInfo.set(newInfo);
        this.fireGraphicsNodeChangeCompleted();
    }

    public void setAttributedCharacterIterator(AttributedCharacterIterator newAci) {
        this.fireGraphicsNodeChangeStarted();
        this.invalidateGeometryCache();
        this.aci = newAci;
        this.text = null;
        this.textRuns = null;
        this.fireGraphicsNodeChangeCompleted();
    }

    public AttributedCharacterIterator getAttributedCharacterIterator() {
        return this.aci;
    }

    protected void invalidateGeometryCache() {
        super.invalidateGeometryCache();
        this.primitiveBounds = null;
        this.geometryBounds = null;
        this.outline = null;
    }

    public Rectangle2D getPrimitiveBounds() {
        if (this.primitiveBounds == null && this.aci != null) {
            this.primitiveBounds = this.textPainter.getBounds2D(this);
        }
        return this.primitiveBounds;
    }

    public Rectangle2D getGeometryBounds() {
        if (this.geometryBounds == null && this.aci != null) {
            this.geometryBounds = this.textPainter.getGeometryBounds(this);
        }
        return this.geometryBounds;
    }

    public Rectangle2D getSensitiveBounds() {
        return this.getGeometryBounds();
    }

    public Shape getOutline() {
        if (this.outline == null && this.aci != null) {
            this.outline = this.textPainter.getOutline(this);
        }
        return this.outline;
    }

    public Mark getMarkerForChar(int index, boolean beforeChar) {
        return this.textPainter.getMark(this, index, beforeChar);
    }

    public void setSelection(Mark begin, Mark end) {
        if (begin.getTextNode() != this || end.getTextNode() != this) {
            throw new RuntimeException("Markers not from this TextNode");
        }
        this.beginMark = begin;
        this.endMark = end;
    }

    public boolean selectAt(double x, double y) {
        this.beginMark = this.textPainter.selectAt(x, y, this);
        return true;
    }

    public boolean selectTo(double x, double y) {
        Mark tmpMark = this.textPainter.selectTo(x, y, this.beginMark);
        if (tmpMark == null) {
            return false;
        }
        if (tmpMark != this.endMark) {
            this.endMark = tmpMark;
            return true;
        }
        return false;
    }

    public boolean selectAll(double x, double y) {
        this.beginMark = this.textPainter.selectFirst(this);
        this.endMark = this.textPainter.selectLast(this);
        return true;
    }

    public Object getSelection() {
        AttributedCharacterSpanIterator o = null;
        if (this.aci == null) {
            return o;
        }
        int[] ranges = this.textPainter.getSelected(this.beginMark, this.endMark);
        if (ranges != null && ranges.length > 1) {
            if (ranges[0] > ranges[1]) {
                int temp = ranges[1];
                ranges[1] = ranges[0];
                ranges[0] = temp;
            }
            o = new AttributedCharacterSpanIterator(this.aci, ranges[0], ranges[1] + 1);
        }
        return o;
    }

    public Shape getHighlightShape() {
        Shape highlightShape = this.textPainter.getHighlightShape(this.beginMark, this.endMark);
        AffineTransform t = this.getGlobalTransform();
        highlightShape = t.createTransformedShape(highlightShape);
        return highlightShape;
    }

    public void primitivePaint(Graphics2D g2d) {
        Shape clip = g2d.getClip();
        if (clip != null && !(clip instanceof GeneralPath)) {
            g2d.setClip(new GeneralPath(clip));
        }
        this.textPainter.paint(this, g2d);
    }

    public boolean contains(Point2D p) {
        if (!super.contains(p)) {
            return false;
        }
        List list = this.getTextRuns();
        for (Object aList : list) {
            float y;
            float x;
            StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            TextSpanLayout layout = run.getLayout();
            TextHit textHit = layout.hitTestChar(x = (float)p.getX(), y = (float)p.getY());
            if (textHit == null || !this.contains(p, layout.getBounds2D())) continue;
            return true;
        }
        return false;
    }

    protected boolean contains(Point2D p, Rectangle2D b) {
        if (b == null || !b.contains(p)) {
            return false;
        }
        switch (this.pointerEventType) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                return this.isVisible;
            }
            case 4: 
            case 5: 
            case 6: 
            case 7: {
                return true;
            }
            case 8: {
                return false;
            }
        }
        return false;
    }

    public static final class Anchor
    implements Serializable {
        public static final int ANCHOR_START = 0;
        public static final int ANCHOR_MIDDLE = 1;
        public static final int ANCHOR_END = 2;
        public static final Anchor START = new Anchor(0);
        public static final Anchor MIDDLE = new Anchor(1);
        public static final Anchor END = new Anchor(2);
        private int type;

        private Anchor(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        private Object readResolve() throws ObjectStreamException {
            switch (this.type) {
                case 0: {
                    return START;
                }
                case 1: {
                    return MIDDLE;
                }
                case 2: {
                    return END;
                }
            }
            throw new RuntimeException("Unknown Anchor type");
        }
    }
}

