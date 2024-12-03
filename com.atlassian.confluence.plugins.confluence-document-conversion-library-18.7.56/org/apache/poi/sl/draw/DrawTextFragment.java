/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import org.apache.poi.sl.draw.Drawable;

public class DrawTextFragment
implements Drawable {
    final TextLayout layout;
    final AttributedString str;
    double x;
    double y;

    public DrawTextFragment(TextLayout layout, AttributedString str) {
        this.layout = layout;
        this.str = str;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(Graphics2D graphics) {
        if (this.str == null) {
            return;
        }
        double yBaseline = this.y + (double)this.layout.getAscent();
        Integer textMode = (Integer)graphics.getRenderingHint(Drawable.TEXT_RENDERING_MODE);
        if (textMode != null && textMode == 2) {
            this.layout.draw(graphics, (float)this.x, (float)yBaseline);
        } else {
            try {
                graphics.drawString(this.str.getIterator(), (float)this.x, (float)yBaseline);
            }
            catch (ClassCastException e) {
                this.replaceForgroundPaintWithBlack(this.str);
                graphics.drawString(this.str.getIterator(), (float)this.x, (float)yBaseline);
            }
        }
    }

    private void replaceForgroundPaintWithBlack(AttributedString as) {
        AttributedCharacterIterator iter = as.getIterator(new TextAttribute[]{TextAttribute.FOREGROUND});
        char ch = iter.first();
        while (ch != '\uffff') {
            as.addAttribute(TextAttribute.FOREGROUND, Color.BLACK, iter.getBeginIndex(), iter.getEndIndex());
            ch = iter.next();
        }
    }

    @Override
    public void applyTransform(Graphics2D graphics) {
    }

    @Override
    public void drawContent(Graphics2D graphics) {
    }

    public TextLayout getLayout() {
        return this.layout;
    }

    public AttributedString getAttributedString() {
        return this.str;
    }

    public float getHeight() {
        double h = this.layout.getAscent() + this.layout.getDescent();
        return (float)h;
    }

    public float getLeading() {
        double l = this.layout.getLeading();
        if (l == 0.0) {
            l = (double)(this.layout.getAscent() + this.layout.getDescent()) * 0.15;
        }
        return (float)l;
    }

    public float getWidth() {
        return this.layout.getAdvance();
    }

    public String getString() {
        if (this.str == null) {
            return "";
        }
        AttributedCharacterIterator it = this.str.getIterator();
        StringBuilder buf = new StringBuilder();
        char c = it.first();
        while (c != '\uffff') {
            buf.append(c);
            c = it.next();
        }
        return buf.toString();
    }

    public String toString() {
        return "[" + this.getClass().getSimpleName() + "] " + this.getString();
    }
}

