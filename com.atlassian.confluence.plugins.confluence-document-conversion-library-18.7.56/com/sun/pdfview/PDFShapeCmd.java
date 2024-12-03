/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFRenderer;
import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

public class PDFShapeCmd
extends PDFCmd {
    public static final int STROKE = 1;
    public static final int FILL = 2;
    public static final int BOTH = 3;
    public static final int CLIP = 4;
    private final GeneralPath gp;
    private final int style;
    static final BasicStroke againstroke = new BasicStroke(2.0f, 0, 2);

    public PDFShapeCmd(GeneralPath gp, int style) {
        this.gp = gp;
        this.style = style;
    }

    @Override
    public Rectangle2D execute(PDFRenderer state) {
        Rectangle2D rect = null;
        if ((this.style & 2) != 0) {
            rect = state.fill(this.gp);
            GeneralPath strokeagain = this.checkOverlap(state);
            if (strokeagain != null) {
                state.draw(strokeagain, againstroke);
            }
            if (this.gp != null) {
                state.setLastShape(this.gp);
            }
        }
        if ((this.style & 1) != 0) {
            Rectangle2D strokeRect = state.stroke(this.gp);
            rect = rect == null ? strokeRect : rect.createUnion(strokeRect);
        }
        if ((this.style & 4) != 0) {
            state.clip(this.gp);
        }
        return rect;
    }

    private GeneralPath checkOverlap(PDFRenderer state) {
        if (this.style == 2 && this.gp != null && state.getLastShape() != null) {
            float[] mypoints = new float[16];
            float[] prevpoints = new float[16];
            int mycount = this.getPoints(this.gp, mypoints);
            int prevcount = this.getPoints(state.getLastShape(), prevpoints);
            for (int i = 0; i < prevcount; i += 4) {
                for (int j = 0; j < mycount; j += 4) {
                    if (!((double)Math.abs(mypoints[j + 2] - prevpoints[i]) < 0.01) || !((double)Math.abs(mypoints[j + 3] - prevpoints[i + 1]) < 0.01) || !((double)Math.abs(mypoints[j] - prevpoints[i + 2]) < 0.01) || !((double)Math.abs(mypoints[j + 1] - prevpoints[i + 3]) < 0.01)) continue;
                    GeneralPath strokeagain = new GeneralPath();
                    strokeagain.moveTo(mypoints[j], mypoints[j + 1]);
                    strokeagain.lineTo(mypoints[j + 2], mypoints[j + 3]);
                    return strokeagain;
                }
            }
        }
        return null;
    }

    private int getPoints(GeneralPath path, float[] mypoints) {
        int count = 0;
        float x = 0.0f;
        float y = 0.0f;
        float startx = 0.0f;
        float starty = 0.0f;
        float[] coords = new float[6];
        PathIterator pi = path.getPathIterator(new AffineTransform());
        while (!pi.isDone() && count < mypoints.length) {
            int pathtype = pi.currentSegment(coords);
            switch (pathtype) {
                case 0: {
                    startx = x = coords[0];
                    starty = y = coords[1];
                    break;
                }
                case 1: {
                    mypoints[count++] = x;
                    mypoints[count++] = y;
                    int n = count++;
                    float f = coords[0];
                    mypoints[n] = f;
                    x = f;
                    int n2 = count++;
                    float f2 = coords[1];
                    mypoints[n2] = f2;
                    y = f2;
                    break;
                }
                case 2: {
                    x = coords[2];
                    y = coords[3];
                    break;
                }
                case 3: {
                    x = mypoints[4];
                    y = mypoints[5];
                    break;
                }
                case 4: {
                    mypoints[count++] = x;
                    mypoints[count++] = y;
                    int n = count++;
                    float f = startx;
                    mypoints[n] = f;
                    x = f;
                    int n3 = count++;
                    float f3 = starty;
                    mypoints[n3] = f3;
                    y = f3;
                }
            }
            pi.next();
        }
        return count;
    }

    @Override
    public String getDetails() {
        StringBuffer sb = new StringBuffer();
        Rectangle2D b = this.gp.getBounds2D();
        sb.append("ShapeCommand at: " + b.getX() + ", " + b.getY() + "\n");
        sb.append("Size: ").append(b.getWidth()).append(" x ").append(b.getHeight()).append("\n");
        sb.append("Mode: ");
        if ((this.style & 2) != 0) {
            sb.append("FILL ");
        }
        if ((this.style & 1) != 0) {
            sb.append("STROKE ");
        }
        if ((this.style & 4) != 0) {
            sb.append("CLIP");
        }
        return sb.toString();
    }
}

