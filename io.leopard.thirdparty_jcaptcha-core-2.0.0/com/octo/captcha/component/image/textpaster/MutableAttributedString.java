/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.CaptchaException
 */
package com.octo.captcha.component.image.textpaster;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.component.image.color.ColorGenerator;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Random;

public class MutableAttributedString {
    AttributedString originalAttributedString;
    AttributedString[] aStrings;
    Rectangle2D[] bounds;
    LineMetrics[] metrics;
    GlyphVector[] glyphVectors;
    private Random myRandom = new SecureRandom();
    private int kerning;

    protected MutableAttributedString(Graphics2D g2, AttributedString aString, int kerning) {
        this.kerning = kerning;
        this.originalAttributedString = aString;
        AttributedCharacterIterator iter = aString.getIterator();
        int n = iter.getEndIndex();
        this.aStrings = new AttributedString[n];
        this.bounds = new Rectangle2D[n];
        this.metrics = new LineMetrics[n];
        for (int i = iter.getBeginIndex(); i < iter.getEndIndex(); ++i) {
            iter.setIndex(i);
            this.aStrings[i] = new AttributedString(iter, i, i + 1);
            Font font = (Font)iter.getAttribute(TextAttribute.FONT);
            if (font != null) {
                g2.setFont(font);
            }
            FontRenderContext frc = g2.getFontRenderContext();
            this.bounds[i] = g2.getFont().getStringBounds(iter, i, i + 1, frc);
            this.metrics[i] = g2.getFont().getLineMetrics(new Character(iter.current()).toString(), frc);
        }
    }

    void drawString(Graphics2D g2) {
        for (int i = 0; i < this.length(); ++i) {
            g2.drawString(this.getIterator(i), (float)this.getX(i), (float)this.getY(i));
        }
    }

    void drawString(Graphics2D g2, ColorGenerator colorGenerator) {
        for (int i = 0; i < this.length(); ++i) {
            g2.setColor(colorGenerator.getNextColor());
            g2.drawString(this.getIterator(i), (float)this.getX(i), (float)this.getY(i));
        }
    }

    Point2D moveToRandomSpot(BufferedImage background) {
        return this.moveToRandomSpot(background, null);
    }

    Point2D moveToRandomSpot(BufferedImage background, Point2D startingPoint) {
        int maxHeight = (int)this.getMaxHeight();
        int arbitraryHorizontalPadding = 10;
        int arbitraryVerticalPadding = 5;
        double maxX = (double)background.getWidth() - this.getTotalWidth() - 10.0;
        double maxY = background.getHeight() - maxHeight - 5;
        int newY = startingPoint == null ? (int)this.getMaxAscent() + this.myRandom.nextInt(Math.max(1, (int)maxY)) : (int)(startingPoint.getY() + (double)this.myRandom.nextInt(10));
        if (maxX < 0.0 || maxY < 0.0) {
            String problem = "too tall:";
            if (maxX < 0.0 && maxY > 0.0) {
                problem = "too long:";
                this.useMinimumSpacing(this.kerning / 2);
                maxX = (double)background.getWidth() - this.getTotalWidth();
                if (maxX < 0.0) {
                    this.useMinimumSpacing(0.0);
                    maxX = (double)background.getWidth() - this.getTotalWidth();
                    if (maxX < 0.0) {
                        maxX = this.reduceHorizontalSpacing(background.getWidth(), 0.05);
                    }
                }
                if (maxX > 0.0) {
                    this.moveTo(0.0, newY);
                    return new Point2D.Float(0.0f, newY);
                }
            }
            throw new CaptchaException("word is " + problem + " try to use less letters, smaller font" + " or bigger background: " + " text bounds = " + this + " with fonts " + this.getFontListing() + " versus image width = " + background.getWidth() + ", height = " + background.getHeight());
        }
        int newX = startingPoint == null ? this.myRandom.nextInt(Math.max(1, (int)maxX)) : (int)(startingPoint.getX() + (double)this.myRandom.nextInt(10));
        this.moveTo(newX, newY);
        return new Point2D.Float(newX, newY);
    }

    String getFontListing() {
        StringBuffer buf = new StringBuffer();
        String RS = "\n\t";
        buf.append("{");
        for (int i = 0; i < this.length(); ++i) {
            AttributedCharacterIterator iter = this.aStrings[i].getIterator();
            Font font = (Font)iter.getAttribute(TextAttribute.FONT);
            if (font == null) continue;
            buf.append(font.toString()).append("\n\t");
        }
        buf.append("}");
        return buf.toString();
    }

    void useMonospacing(double kerning) {
        double maxWidth = this.getMaxWidth();
        for (int i = 1; i < this.bounds.length; ++i) {
            this.getBounds(i).setRect(this.getX(i - 1) + maxWidth + kerning, this.getY(i), this.getWidth(i), this.getHeight(i));
        }
    }

    void useMinimumSpacing(double kerning) {
        for (int i = 1; i < this.length(); ++i) {
            this.bounds[i].setRect(this.bounds[i - 1].getX() + this.bounds[i - 1].getWidth() + kerning, this.bounds[i].getY(), this.bounds[i].getWidth(), this.bounds[i].getHeight());
        }
    }

    double reduceHorizontalSpacing(int imageWidth, double maxReductionPct) {
        double stepSize;
        double maxX = (double)imageWidth - this.getTotalWidth();
        double pct = 0.0;
        for (pct = stepSize = maxReductionPct / 25.0; pct < maxReductionPct && maxX < 0.0; pct += stepSize) {
            for (int i = 1; i < this.length(); ++i) {
                this.bounds[i].setRect((1.0 - pct) * this.bounds[i].getX(), this.bounds[i].getY(), this.bounds[i].getWidth(), this.bounds[i].getHeight());
            }
            maxX = (double)imageWidth - this.getTotalWidth();
        }
        return maxX;
    }

    public void overlap(double overlapPixs) {
        for (int i = 1; i < this.length(); ++i) {
            this.bounds[i].setRect(this.bounds[i - 1].getX() + this.bounds[i - 1].getWidth() - overlapPixs, this.bounds[i].getY(), this.bounds[i].getWidth(), this.bounds[i].getHeight());
        }
    }

    void moveTo(double newX, double newY) {
        this.bounds[0].setRect(newX, newY, this.bounds[0].getWidth(), this.bounds[0].getHeight());
        for (int i = 1; i < this.length(); ++i) {
            this.bounds[i].setRect(newX + this.bounds[i].getX(), newY, this.bounds[i].getWidth(), this.bounds[i].getHeight());
        }
    }

    protected void shiftBoundariesToNonLinearLayout(double backgroundWidth, double backgroundHeight) {
        double newX = backgroundWidth / 20.0;
        double middleY = backgroundHeight / 2.0;
        SecureRandom myRandom = new SecureRandom();
        this.bounds[0].setRect(newX, middleY, this.bounds[0].getWidth(), this.bounds[0].getHeight());
        for (int i = 1; i < this.length(); ++i) {
            double characterHeight = this.bounds[i].getHeight();
            double randomY = (double)myRandom.nextInt() % (backgroundHeight / 4.0);
            double currentY = middleY + (myRandom.nextBoolean() ? randomY : -randomY) + characterHeight / 4.0;
            this.bounds[i].setRect(newX + this.bounds[i].getX(), currentY, this.bounds[i].getWidth(), this.bounds[i].getHeight());
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{text=");
        for (int i = 0; i < this.length(); ++i) {
            buf.append(this.aStrings[i].getIterator().current());
        }
        String RS = "\n\t";
        buf.append("\n\t");
        for (int i = 0; i < this.length(); ++i) {
            buf.append(this.bounds[i].toString());
            String FS = " ";
            LineMetrics m = this.metrics[i];
            buf.append(" ascent=").append(m.getAscent()).append(" ");
            buf.append("descent=").append(m.getDescent()).append(" ");
            buf.append("leading=").append(m.getLeading()).append(" ");
            buf.append("\n\t");
        }
        buf.append("}");
        return buf.toString();
    }

    public int length() {
        return this.bounds.length;
    }

    public double getX(int index) {
        return this.getBounds(index).getX();
    }

    public double getY(int index) {
        return this.getBounds(index).getY();
    }

    public double getHeight(int index) {
        return this.getBounds(index).getHeight();
    }

    public double getTotalWidth() {
        return this.getX(this.length() - 1) + this.getWidth(this.length() - 1);
    }

    public double getWidth(int index) {
        return this.getBounds(index).getWidth();
    }

    public double getAscent(int index) {
        return this.getMetric(index).getAscent();
    }

    double getDescent(int index) {
        return this.getMetric(index).getDescent();
    }

    public double getMaxWidth() {
        double maxWidth = -1.0;
        for (int i = 0; i < this.bounds.length; ++i) {
            double w = this.getWidth(i);
            if (!(maxWidth < w)) continue;
            maxWidth = w;
        }
        return maxWidth;
    }

    public double getMaxAscent() {
        double maxAscent = -1.0;
        for (int i = 0; i < this.bounds.length; ++i) {
            double a = this.getAscent(i);
            if (!(maxAscent < a)) continue;
            maxAscent = a;
        }
        return maxAscent;
    }

    public double getMaxDescent() {
        double maxDescent = -1.0;
        for (int i = 0; i < this.bounds.length; ++i) {
            double d = this.getDescent(i);
            if (!(maxDescent < d)) continue;
            maxDescent = d;
        }
        return maxDescent;
    }

    public double getMaxHeight() {
        double maxHeight = -1.0;
        for (int i = 0; i < this.bounds.length; ++i) {
            double h = this.getHeight(i);
            if (!(maxHeight < h)) continue;
            maxHeight = h;
        }
        return maxHeight;
    }

    public double getMaxX() {
        return this.getX(0) + this.getTotalWidth();
    }

    public double getMaxY() {
        return this.getY(0) + this.getMaxHeight();
    }

    public Rectangle2D getBounds(int index) {
        return this.bounds[index];
    }

    public LineMetrics getMetric(int index) {
        return this.metrics[index];
    }

    public AttributedCharacterIterator getIterator(int i) {
        return this.aStrings[i].getIterator();
    }
}

