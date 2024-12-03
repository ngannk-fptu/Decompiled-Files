/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.UIManager;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.extend.FSGlyphVector;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.render.AbstractOutputDevice;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.render.JustificationInfo;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.swing.AWTFSFont;
import org.xhtmlrenderer.swing.AWTFSGlyphVector;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.swing.ImageReplacedElement;
import org.xhtmlrenderer.swing.RootPanel;
import org.xhtmlrenderer.swing.SwingReplacedElement;

public class Java2DOutputDevice
extends AbstractOutputDevice
implements OutputDevice {
    private Graphics2D _graphics;

    public Java2DOutputDevice(Graphics2D graphics) {
        this._graphics = graphics;
    }

    public Java2DOutputDevice(BufferedImage outputImage) {
        this(outputImage.createGraphics());
    }

    @Override
    public void drawSelection(RenderingContext c, InlineText inlineText) {
        if (inlineText.isSelected()) {
            InlineLayoutBox iB = inlineText.getParent();
            String text = inlineText.getSubstring();
            if (text != null && text.length() > 0) {
                FSFont font = iB.getStyle().getFSFont(c);
                FSGlyphVector glyphVector = c.getTextRenderer().getGlyphVector(c.getOutputDevice(), font, inlineText.getSubstring());
                Rectangle start = c.getTextRenderer().getGlyphBounds(c.getOutputDevice(), font, glyphVector, inlineText.getSelectionStart(), iB.getAbsX() + inlineText.getX(), iB.getAbsY() + iB.getBaseline());
                Rectangle end = c.getTextRenderer().getGlyphBounds(c.getOutputDevice(), font, glyphVector, inlineText.getSelectionEnd() - 1, iB.getAbsX() + inlineText.getX(), iB.getAbsY() + iB.getBaseline());
                Graphics2D graphics = this.getGraphics();
                double scaleX = graphics.getTransform().getScaleX();
                boolean allSelected = text.length() == inlineText.getSelectionEnd() - inlineText.getSelectionStart();
                int startX = inlineText.getSelectionStart() == inlineText.getStart() ? iB.getAbsX() + inlineText.getX() : (int)Math.round((double)start.x / scaleX);
                int endX = allSelected ? startX + inlineText.getWidth() : (int)Math.round((double)(end.x + end.width) / scaleX);
                this._graphics.setColor(UIManager.getColor("TextArea.selectionBackground"));
                this.fillRect(startX, iB.getAbsY(), endX - startX, iB.getHeight());
                this._graphics.setColor(Color.WHITE);
                this.setFont(iB.getStyle().getFSFont(c));
                this.drawSelectedText(c, inlineText, iB, glyphVector);
            }
        }
    }

    private void drawSelectedText(RenderingContext c, InlineText inlineText, InlineLayoutBox iB, FSGlyphVector glyphVector) {
        JustificationInfo info;
        int i;
        GlyphVector vector = ((AWTFSGlyphVector)glyphVector).getGlyphVector();
        for (i = 0; i < inlineText.getSelectionStart(); ++i) {
            vector.setGlyphPosition(i, new Point2D.Float(-100000.0f, -100000.0f));
        }
        for (i = inlineText.getSelectionEnd(); i < inlineText.getSubstring().length(); ++i) {
            vector.setGlyphPosition(i, new Point2D.Float(-100000.0f, -100000.0f));
        }
        if (inlineText.getParent().getStyle().isTextJustify() && (info = inlineText.getParent().getLineBox().getJustificationInfo()) != null) {
            String string = inlineText.getSubstring();
            float adjust = 0.0f;
            for (int i2 = inlineText.getSelectionStart(); i2 < inlineText.getSelectionEnd(); ++i2) {
                char ch = string.charAt(i2);
                if (i2 != 0) {
                    Point2D point = vector.getGlyphPosition(i2);
                    vector.setGlyphPosition(i2, new Point2D.Double(point.getX() + (double)adjust, point.getY()));
                }
                if (ch == ' ' || ch == '\u00a0' || ch == '\u3000') {
                    adjust += info.getSpaceAdjust();
                    continue;
                }
                adjust += info.getNonSpaceAdjust();
            }
        }
        c.getTextRenderer().drawGlyphVector(c.getOutputDevice(), glyphVector, iB.getAbsX() + inlineText.getX(), iB.getAbsY() + iB.getBaseline());
    }

    @Override
    public void drawBorderLine(Shape bounds, int side, int lineWidth, boolean solid) {
        this.draw(bounds);
    }

    @Override
    public void paintReplacedElement(RenderingContext c, BlockBox box) {
        ReplacedElement replaced = box.getReplacedElement();
        if (replaced instanceof SwingReplacedElement) {
            Rectangle contentBounds = box.getContentAreaEdge(box.getAbsX(), box.getAbsY(), c);
            JComponent component = ((SwingReplacedElement)box.getReplacedElement()).getJComponent();
            RootPanel canvas = (RootPanel)c.getCanvas();
            CellRendererPane pane = canvas.getCellRendererPane();
            pane.paintComponent(this._graphics, component, canvas, contentBounds.x, contentBounds.y, contentBounds.width, contentBounds.height, true);
        } else if (replaced instanceof ImageReplacedElement) {
            Image image = ((ImageReplacedElement)replaced).getImage();
            Point location = replaced.getLocation();
            this._graphics.drawImage(image, (int)location.getX(), (int)location.getY(), null);
        }
    }

    @Override
    public void setColor(FSColor color) {
        if (!(color instanceof FSRGBColor)) {
            throw new RuntimeException("internal error: unsupported color class " + color.getClass().getName());
        }
        FSRGBColor rgb = (FSRGBColor)color;
        this._graphics.setColor(new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
    }

    @Override
    protected void drawLine(int x1, int y1, int x2, int y2) {
        this._graphics.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        this._graphics.drawRect(x, y, width, height);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        this._graphics.fillRect(x, y, width, height);
    }

    @Override
    public void setClip(Shape s) {
        this._graphics.setClip(s);
    }

    @Override
    public Shape getClip() {
        return this._graphics.getClip();
    }

    @Override
    public void clip(Shape s) {
        this._graphics.clip(s);
    }

    @Override
    public void translate(double tx, double ty) {
        this._graphics.translate(tx, ty);
    }

    public Graphics2D getGraphics() {
        return this._graphics;
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        this._graphics.drawOval(x, y, width, height);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        this._graphics.fillOval(x, y, width, height);
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key key) {
        return this._graphics.getRenderingHint(key);
    }

    @Override
    public void setRenderingHint(RenderingHints.Key key, Object value) {
        this._graphics.setRenderingHint(key, value);
    }

    @Override
    public void setFont(FSFont font) {
        this._graphics.setFont(((AWTFSFont)font).getAWTFont());
    }

    @Override
    public void setStroke(Stroke s) {
        this._graphics.setStroke(s);
    }

    @Override
    public Stroke getStroke() {
        return this._graphics.getStroke();
    }

    @Override
    public void fill(Shape s) {
        this._graphics.fill(s);
    }

    @Override
    public void draw(Shape s) {
        this._graphics.draw(s);
    }

    @Override
    public void drawImage(FSImage image, int x, int y) {
        this._graphics.drawImage((Image)((AWTFSImage)image).getImage(), x, y, null);
    }

    @Override
    public boolean isSupportsSelection() {
        return true;
    }

    @Override
    public boolean isSupportsCMYKColors() {
        return true;
    }
}

