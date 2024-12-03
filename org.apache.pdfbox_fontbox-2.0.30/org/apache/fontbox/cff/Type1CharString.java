/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.cff;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.cff.CharStringCommand;
import org.apache.fontbox.cff.CharStringHandler;
import org.apache.fontbox.encoding.StandardEncoding;
import org.apache.fontbox.type1.Type1CharStringReader;

public class Type1CharString {
    private static final Log LOG = LogFactory.getLog(Type1CharString.class);
    private Type1CharStringReader font;
    private final String fontName;
    private final String glyphName;
    private GeneralPath path = null;
    private int width = 0;
    private Point2D.Float leftSideBearing = null;
    private Point2D.Float current = null;
    private boolean isFlex = false;
    private final List<Point2D.Float> flexPoints = new ArrayList<Point2D.Float>();
    protected List<Object> type1Sequence;
    protected int commandCount;

    public Type1CharString(Type1CharStringReader font, String fontName, String glyphName, List<Object> sequence) {
        this(font, fontName, glyphName);
        this.type1Sequence = sequence;
    }

    protected Type1CharString(Type1CharStringReader font, String fontName, String glyphName) {
        this.font = font;
        this.fontName = fontName;
        this.glyphName = glyphName;
        this.current = new Point2D.Float(0.0f, 0.0f);
    }

    public String getName() {
        return this.glyphName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Rectangle2D getBounds() {
        Log log = LOG;
        synchronized (log) {
            if (this.path == null) {
                this.render();
            }
        }
        return this.path.getBounds2D();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getWidth() {
        Log log = LOG;
        synchronized (log) {
            if (this.path == null) {
                this.render();
            }
        }
        return this.width;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public GeneralPath getPath() {
        Log log = LOG;
        synchronized (log) {
            if (this.path == null) {
                this.render();
            }
        }
        return this.path;
    }

    public List<Object> getType1Sequence() {
        return this.type1Sequence;
    }

    private void render() {
        this.path = new GeneralPath();
        this.leftSideBearing = new Point2D.Float(0.0f, 0.0f);
        this.width = 0;
        CharStringHandler handler = new CharStringHandler(){

            @Override
            public List<Number> handleCommand(List<Number> numbers, CharStringCommand command) {
                return Type1CharString.this.handleCommand(numbers, command);
            }
        };
        handler.handleSequence(this.type1Sequence);
    }

    private List<Number> handleCommand(List<Number> numbers, CharStringCommand command) {
        ++this.commandCount;
        String name = CharStringCommand.TYPE1_VOCABULARY.get(command.getKey());
        if ("rmoveto".equals(name)) {
            if (numbers.size() >= 2) {
                if (this.isFlex) {
                    this.flexPoints.add(new Point2D.Float(numbers.get(0).floatValue(), numbers.get(1).floatValue()));
                } else {
                    this.rmoveTo(numbers.get(0), numbers.get(1));
                }
            }
        } else if ("vmoveto".equals(name)) {
            if (!numbers.isEmpty()) {
                if (this.isFlex) {
                    this.flexPoints.add(new Point2D.Float(0.0f, numbers.get(0).floatValue()));
                } else {
                    this.rmoveTo(0, numbers.get(0));
                }
            }
        } else if ("hmoveto".equals(name)) {
            if (!numbers.isEmpty()) {
                if (this.isFlex) {
                    this.flexPoints.add(new Point2D.Float(numbers.get(0).floatValue(), 0.0f));
                } else {
                    this.rmoveTo(numbers.get(0), 0);
                }
            }
        } else if ("rlineto".equals(name)) {
            if (numbers.size() >= 2) {
                this.rlineTo(numbers.get(0), numbers.get(1));
            }
        } else if ("hlineto".equals(name)) {
            if (!numbers.isEmpty()) {
                this.rlineTo(numbers.get(0), 0);
            }
        } else if ("vlineto".equals(name)) {
            if (!numbers.isEmpty()) {
                this.rlineTo(0, numbers.get(0));
            }
        } else if ("rrcurveto".equals(name)) {
            if (numbers.size() >= 6) {
                this.rrcurveTo(numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), numbers.get(4), numbers.get(5));
            }
        } else if ("closepath".equals(name)) {
            this.closeCharString1Path();
        } else if ("sbw".equals(name)) {
            if (numbers.size() >= 3) {
                this.leftSideBearing = new Point2D.Float(numbers.get(0).floatValue(), numbers.get(1).floatValue());
                this.width = numbers.get(2).intValue();
                this.current.setLocation(this.leftSideBearing);
            }
        } else if ("hsbw".equals(name)) {
            if (numbers.size() >= 2) {
                this.leftSideBearing = new Point2D.Float(numbers.get(0).floatValue(), 0.0f);
                this.width = numbers.get(1).intValue();
                this.current.setLocation(this.leftSideBearing);
            }
        } else if ("vhcurveto".equals(name)) {
            if (numbers.size() >= 4) {
                this.rrcurveTo(0, numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), 0);
            }
        } else if ("hvcurveto".equals(name)) {
            if (numbers.size() >= 4) {
                this.rrcurveTo(numbers.get(0), 0, numbers.get(1), numbers.get(2), 0, numbers.get(3));
            }
        } else if ("seac".equals(name)) {
            if (numbers.size() >= 5) {
                this.seac(numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), numbers.get(4));
            }
        } else if ("setcurrentpoint".equals(name)) {
            if (numbers.size() >= 2) {
                this.setcurrentpoint(numbers.get(0), numbers.get(1));
            }
        } else if ("callothersubr".equals(name)) {
            if (!numbers.isEmpty()) {
                this.callothersubr(numbers.get(0).intValue());
            }
        } else if ("div".equals(name)) {
            if (numbers.size() >= 2) {
                float b = numbers.get(numbers.size() - 1).floatValue();
                float a = numbers.get(numbers.size() - 2).floatValue();
                float result = a / b;
                ArrayList<Number> list = new ArrayList<Number>(numbers);
                list.remove(list.size() - 1);
                list.remove(list.size() - 1);
                list.add(Float.valueOf(result));
                return list;
            }
        } else if (!("hstem".equals(name) || "vstem".equals(name) || "hstem3".equals(name) || "vstem3".equals(name) || "dotsection".equals(name) || "endchar".equals(name))) {
            if ("return".equals(name) || "callsubr".equals(name)) {
                LOG.warn((Object)("Unexpected charstring command: " + name + " in glyph " + this.glyphName + " of font " + this.fontName));
            } else {
                if (name != null) {
                    throw new IllegalArgumentException("Unhandled command: " + name);
                }
                LOG.warn((Object)("Unknown charstring command: " + command.getKey() + " in glyph " + this.glyphName + " of font " + this.fontName));
            }
        }
        return null;
    }

    private void setcurrentpoint(Number x, Number y) {
        this.current.setLocation(x.floatValue(), y.floatValue());
    }

    private void callothersubr(int num) {
        if (num == 0) {
            this.isFlex = false;
            if (this.flexPoints.size() < 7) {
                LOG.warn((Object)("flex without moveTo in font " + this.fontName + ", glyph " + this.glyphName + ", command " + this.commandCount));
                return;
            }
            Point2D.Float reference = this.flexPoints.get(0);
            reference.setLocation(this.current.getX() + reference.getX(), this.current.getY() + reference.getY());
            Point2D.Float first = this.flexPoints.get(1);
            first.setLocation(reference.getX() + first.getX(), reference.getY() + first.getY());
            first.setLocation(first.getX() - this.current.getX(), first.getY() - this.current.getY());
            Point2D.Float p1 = this.flexPoints.get(1);
            Point2D.Float p2 = this.flexPoints.get(2);
            Point2D.Float p3 = this.flexPoints.get(3);
            this.rrcurveTo(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
            Point2D.Float p4 = this.flexPoints.get(4);
            Point2D.Float p5 = this.flexPoints.get(5);
            Point2D.Float p6 = this.flexPoints.get(6);
            this.rrcurveTo(p4.getX(), p4.getY(), p5.getX(), p5.getY(), p6.getX(), p6.getY());
            this.flexPoints.clear();
        } else if (num == 1) {
            this.isFlex = true;
        } else {
            LOG.warn((Object)("Invalid callothersubr parameter: " + num));
        }
    }

    private void rmoveTo(Number dx, Number dy) {
        float x = (float)this.current.getX() + dx.floatValue();
        float y = (float)this.current.getY() + dy.floatValue();
        this.path.moveTo(x, y);
        this.current.setLocation(x, y);
    }

    private void rlineTo(Number dx, Number dy) {
        float x = (float)this.current.getX() + dx.floatValue();
        float y = (float)this.current.getY() + dy.floatValue();
        if (this.path.getCurrentPoint() == null) {
            LOG.warn((Object)("rlineTo without initial moveTo in font " + this.fontName + ", glyph " + this.glyphName));
            this.path.moveTo(x, y);
        } else {
            this.path.lineTo(x, y);
        }
        this.current.setLocation(x, y);
    }

    private void rrcurveTo(Number dx1, Number dy1, Number dx2, Number dy2, Number dx3, Number dy3) {
        float x1 = (float)this.current.getX() + dx1.floatValue();
        float y1 = (float)this.current.getY() + dy1.floatValue();
        float x2 = x1 + dx2.floatValue();
        float y2 = y1 + dy2.floatValue();
        float x3 = x2 + dx3.floatValue();
        float y3 = y2 + dy3.floatValue();
        if (this.path.getCurrentPoint() == null) {
            LOG.warn((Object)("rrcurveTo without initial moveTo in font " + this.fontName + ", glyph " + this.glyphName));
            this.path.moveTo(x3, y3);
        } else {
            this.path.curveTo(x1, y1, x2, y2, x3, y3);
        }
        this.current.setLocation(x3, y3);
    }

    private void closeCharString1Path() {
        if (this.path.getCurrentPoint() == null) {
            LOG.warn((Object)("closepath without initial moveTo in font " + this.fontName + ", glyph " + this.glyphName));
        } else {
            this.path.closePath();
        }
        this.path.moveTo(this.current.getX(), this.current.getY());
    }

    private void seac(Number asb, Number adx, Number ady, Number bchar, Number achar) {
        String baseName = StandardEncoding.INSTANCE.getName(bchar.intValue());
        try {
            Type1CharString base = this.font.getType1CharString(baseName);
            this.path.append(base.getPath().getPathIterator(null), false);
        }
        catch (IOException e) {
            LOG.warn((Object)("invalid seac character in glyph " + this.glyphName + " of font " + this.fontName));
        }
        String accentName = StandardEncoding.INSTANCE.getName(achar.intValue());
        try {
            Type1CharString accent = this.font.getType1CharString(accentName);
            GeneralPath accentPath = accent.getPath();
            if (this.path == accentPath) {
                LOG.warn((Object)("Path for " + baseName + " and for accent " + accentName + " are same, ignored"));
                return;
            }
            AffineTransform at = AffineTransform.getTranslateInstance(this.leftSideBearing.getX() + (double)adx.floatValue() - (double)asb.floatValue(), this.leftSideBearing.getY() + (double)ady.floatValue());
            this.path.append(accentPath.getPathIterator(at), false);
        }
        catch (IOException e) {
            LOG.warn((Object)("invalid seac character in glyph " + this.glyphName + " of font " + this.fontName));
        }
    }

    public String toString() {
        return this.type1Sequence.toString().replace("|", "\n").replace(",", " ");
    }
}

