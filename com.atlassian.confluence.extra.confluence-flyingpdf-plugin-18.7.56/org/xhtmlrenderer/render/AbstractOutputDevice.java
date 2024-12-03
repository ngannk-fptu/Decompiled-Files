/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.List;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.BackgroundPosition;
import org.xhtmlrenderer.css.style.BackgroundSize;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.value.FontSpecification;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.OutputDevice;
import org.xhtmlrenderer.render.BorderPainter;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.FSFontMetrics;
import org.xhtmlrenderer.render.InlineLayoutBox;
import org.xhtmlrenderer.render.InlineText;
import org.xhtmlrenderer.render.JustificationInfo;
import org.xhtmlrenderer.render.LineBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.TextDecoration;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.Uu;

public abstract class AbstractOutputDevice
implements OutputDevice {
    private FontSpecification _fontSpec;

    protected abstract void drawLine(int var1, int var2, int var3, int var4);

    @Override
    public void drawText(RenderingContext c, InlineText inlineText) {
        InlineLayoutBox iB = inlineText.getParent();
        String text = inlineText.getSubstring();
        if (text != null && text.length() > 0) {
            this.setColor(iB.getStyle().getColor());
            this.setFont(iB.getStyle().getFSFont(c));
            this.setFontSpecification(iB.getStyle().getFontSpecification());
            if (inlineText.getParent().getStyle().isTextJustify()) {
                JustificationInfo info = inlineText.getParent().getLineBox().getJustificationInfo();
                if (info != null) {
                    c.getTextRenderer().drawString(c.getOutputDevice(), text, iB.getAbsX() + inlineText.getX(), iB.getAbsY() + iB.getBaseline(), info);
                } else {
                    c.getTextRenderer().drawString(c.getOutputDevice(), text, iB.getAbsX() + inlineText.getX(), iB.getAbsY() + iB.getBaseline());
                }
            } else {
                c.getTextRenderer().drawString(c.getOutputDevice(), text, iB.getAbsX() + inlineText.getX(), iB.getAbsY() + iB.getBaseline());
            }
        }
        if (c.debugDrawFontMetrics()) {
            this.drawFontMetrics(c, inlineText);
        }
    }

    private void drawFontMetrics(RenderingContext c, InlineText inlineText) {
        InlineLayoutBox iB = inlineText.getParent();
        String text = inlineText.getSubstring();
        this.setColor(new FSRGBColor(255, 51, 255));
        FSFontMetrics fm = iB.getStyle().getFSFontMetrics(null);
        int width = c.getTextRenderer().getWidth(c.getFontContext(), iB.getStyle().getFSFont(c), text);
        int x = iB.getAbsX() + inlineText.getX();
        int y = iB.getAbsY() + iB.getBaseline();
        this.drawLine(x, y, x + width, y);
        this.drawLine(x, y += (int)Math.ceil(fm.getDescent()), x + width, y);
        y -= (int)Math.ceil(fm.getDescent());
        this.drawLine(x, y -= (int)Math.ceil(fm.getAscent()), x + width, y);
    }

    @Override
    public void drawTextDecoration(RenderingContext c, InlineLayoutBox iB, TextDecoration decoration) {
        this.setColor(iB.getStyle().getColor());
        Rectangle edge = iB.getContentAreaEdge(iB.getAbsX(), iB.getAbsY(), c);
        this.fillRect(edge.x, iB.getAbsY() + decoration.getOffset(), edge.width, decoration.getThickness());
    }

    @Override
    public void drawTextDecoration(RenderingContext c, LineBox lineBox) {
        this.setColor(lineBox.getStyle().getColor());
        Box parent = lineBox.getParent();
        List decorations = lineBox.getTextDecorations();
        for (TextDecoration textDecoration : decorations) {
            if (parent.getStyle().isIdent(CSSName.FS_TEXT_DECORATION_EXTENT, IdentValue.BLOCK)) {
                this.fillRect(lineBox.getAbsX(), lineBox.getAbsY() + textDecoration.getOffset(), parent.getAbsX() + parent.getTx() + parent.getContentWidth() - lineBox.getAbsX(), textDecoration.getThickness());
                continue;
            }
            this.fillRect(lineBox.getAbsX(), lineBox.getAbsY() + textDecoration.getOffset(), lineBox.getContentWidth(), textDecoration.getThickness());
        }
    }

    @Override
    public void drawDebugOutline(RenderingContext c, Box box, FSColor color) {
        this.setColor(color);
        Rectangle rect = box.getMarginEdge(box.getAbsX(), box.getAbsY(), c, 0, 0);
        --rect.height;
        --rect.width;
        this.drawRect(rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public void paintCollapsedBorder(RenderingContext c, BorderPropertySet border, Rectangle bounds, int side) {
        BorderPainter.paint(bounds, side, border, c, 0, false);
    }

    @Override
    public void paintBorder(RenderingContext c, Box box) {
        if (!box.getStyle().isVisible()) {
            return;
        }
        Rectangle borderBounds = box.getPaintingBorderEdge(c);
        BorderPainter.paint(borderBounds, box.getBorderSides(), box.getBorder(c), c, 0, true);
    }

    @Override
    public void paintBorder(RenderingContext c, CalculatedStyle style, Rectangle edge, int sides) {
        BorderPainter.paint(edge, sides, style.getBorder(c), c, 0, true);
    }

    private FSImage getBackgroundImage(RenderingContext c, CalculatedStyle style) {
        if (!style.isIdent(CSSName.BACKGROUND_IMAGE, IdentValue.NONE)) {
            String uri = style.getStringProperty(CSSName.BACKGROUND_IMAGE);
            try {
                return c.getUac().getImageResource(uri).getImage();
            }
            catch (Exception ex) {
                Uu.p(ex);
            }
        }
        return null;
    }

    @Override
    public void paintBackground(RenderingContext c, CalculatedStyle style, Rectangle bounds, Rectangle bgImageContainer, BorderPropertySet border) {
        this.paintBackground0(c, style, bounds, bgImageContainer, border);
    }

    @Override
    public void paintBackground(RenderingContext c, Box box) {
        if (!box.getStyle().isVisible()) {
            return;
        }
        Rectangle backgroundBounds = box.getPaintingBorderEdge(c);
        BorderPropertySet border = box.getStyle().getBorder(c);
        this.paintBackground0(c, box.getStyle(), backgroundBounds, backgroundBounds, border);
    }

    private void paintBackground0(RenderingContext c, CalculatedStyle style, Rectangle backgroundBounds, Rectangle bgImageContainer, BorderPropertySet border) {
        if (!Configuration.isTrue("xr.renderer.draw.backgrounds", true)) {
            return;
        }
        FSColor backgroundColor = style.getBackgroundColor();
        FSImage backgroundImage = this.getBackgroundImage(c, style);
        if (backgroundImage == null || backgroundImage.getHeight() == 0 || backgroundImage.getWidth() == 0) {
            backgroundImage = null;
        }
        if ((backgroundColor == null || backgroundColor == FSRGBColor.TRANSPARENT) && backgroundImage == null) {
            return;
        }
        Area borderBounds = new Area(BorderPainter.generateBorderBounds(backgroundBounds, border, false));
        Shape oldclip = this.getClip();
        if (oldclip != null) {
            borderBounds.intersect(new Area(oldclip));
        }
        if (backgroundColor != null && backgroundColor != FSRGBColor.TRANSPARENT) {
            this.setColor(backgroundColor);
            this.fill(borderBounds);
        }
        if (backgroundImage != null) {
            Rectangle imageBounds;
            this.setClip(borderBounds);
            Rectangle localBGImageContainer = bgImageContainer;
            if (style.isFixedBackground()) {
                localBGImageContainer = c.getViewportRectangle();
            }
            int xoff = localBGImageContainer.x;
            int yoff = localBGImageContainer.y;
            if (border != null) {
                xoff += (int)border.left();
                yoff += (int)border.top();
            }
            this.scaleBackgroundImage(c, style, localBGImageContainer, backgroundImage);
            float imageWidth = backgroundImage.getWidth();
            float imageHeight = backgroundImage.getHeight();
            BackgroundPosition position = style.getBackgroundPosition();
            xoff += this.calcOffset(c, style, position.getHorizontal(), localBGImageContainer.width, imageWidth);
            yoff += this.calcOffset(c, style, position.getVertical(), localBGImageContainer.height, imageHeight);
            boolean hrepeat = style.isHorizontalBackgroundRepeat();
            boolean vrepeat = style.isVerticalBackgroundRepeat();
            if (!hrepeat && !vrepeat) {
                Rectangle imageBounds2 = new Rectangle(xoff, yoff, (int)imageWidth, (int)imageHeight);
                if (imageBounds2.intersects(backgroundBounds)) {
                    this.drawImage(backgroundImage, xoff, yoff);
                }
            } else if (hrepeat && vrepeat) {
                this.paintTiles(backgroundImage, this.adjustTo(backgroundBounds.x, xoff, (int)imageWidth), this.adjustTo(backgroundBounds.y, yoff, (int)imageHeight), backgroundBounds.x + backgroundBounds.width, backgroundBounds.y + backgroundBounds.height);
            } else if (hrepeat) {
                Rectangle imageBounds3 = new Rectangle(xoff = this.adjustTo(backgroundBounds.x, xoff, (int)imageWidth), yoff, (int)imageWidth, (int)imageHeight);
                if (imageBounds3.intersects(backgroundBounds)) {
                    this.paintHorizontalBand(backgroundImage, xoff, yoff, backgroundBounds.x + backgroundBounds.width);
                }
            } else if (vrepeat && (imageBounds = new Rectangle(xoff, yoff = this.adjustTo(backgroundBounds.y, yoff, (int)imageHeight), (int)imageWidth, (int)imageHeight)).intersects(backgroundBounds)) {
                this.paintVerticalBand(backgroundImage, xoff, yoff, backgroundBounds.y + backgroundBounds.height);
            }
            this.setClip(oldclip);
        }
    }

    private int adjustTo(int target, int current, int imageDim) {
        int result = current;
        if (result > target) {
            while (result > target) {
                result -= imageDim;
            }
        } else if (result < target) {
            while (result < target) {
                result += imageDim;
            }
            if (result != target) {
                result -= imageDim;
            }
        }
        return result;
    }

    private void paintTiles(FSImage image, int left, int top, int right, int bottom) {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int x = left; x < right; x += width) {
            for (int y = top; y < bottom; y += height) {
                this.drawImage(image, x, y);
            }
        }
    }

    private void paintVerticalBand(FSImage image, int left, int top, int bottom) {
        int height = image.getHeight();
        for (int y = top; y < bottom; y += height) {
            this.drawImage(image, left, y);
        }
    }

    private void paintHorizontalBand(FSImage image, int left, int top, int right) {
        int width = image.getWidth();
        for (int x = left; x < right; x += width) {
            this.drawImage(image, x, top);
        }
    }

    private int calcOffset(CssContext c, CalculatedStyle style, PropertyValue value, float boundsDim, float imageDim) {
        if (value.getPrimitiveType() == 2) {
            float percent = value.getFloatValue() / 100.0f;
            return Math.round(boundsDim * percent - imageDim * percent);
        }
        return (int)LengthValue.calcFloatProportionalValue(style, CSSName.BACKGROUND_POSITION, value.getCssText(), value.getFloatValue(), value.getPrimitiveType(), 0.0f, c);
    }

    private void scaleBackgroundImage(CssContext c, CalculatedStyle style, Rectangle backgroundContainer, FSImage image) {
        BackgroundSize backgroundSize = style.getBackgroundSize();
        if (!backgroundSize.isBothAuto()) {
            if (backgroundSize.isCover() || backgroundSize.isContain()) {
                int testHeight = (int)((double)image.getHeight() * (double)backgroundContainer.width / (double)image.getWidth());
                if (backgroundSize.isContain()) {
                    if (testHeight > backgroundContainer.height) {
                        image.scale(-1, backgroundContainer.height);
                    } else {
                        image.scale(backgroundContainer.width, -1);
                    }
                } else if (backgroundSize.isCover()) {
                    if (testHeight > backgroundContainer.height) {
                        image.scale(backgroundContainer.width, -1);
                    } else {
                        image.scale(-1, backgroundContainer.height);
                    }
                }
            } else {
                int scaledWidth = this.calcBackgroundSizeLength(c, style, backgroundSize.getWidth(), backgroundContainer.width);
                int scaledHeight = this.calcBackgroundSizeLength(c, style, backgroundSize.getHeight(), backgroundContainer.height);
                image.scale(scaledWidth, scaledHeight);
            }
        }
    }

    private int calcBackgroundSizeLength(CssContext c, CalculatedStyle style, PropertyValue value, float boundsDim) {
        if (value.getPrimitiveType() == 21) {
            return -1;
        }
        if (value.getPrimitiveType() == 2) {
            float percent = value.getFloatValue() / 100.0f;
            return Math.round(boundsDim * percent);
        }
        return (int)LengthValue.calcFloatProportionalValue(style, CSSName.BACKGROUND_SIZE, value.getCssText(), value.getFloatValue(), value.getPrimitiveType(), 0.0f, c);
    }

    public FontSpecification getFontSpecification() {
        return this._fontSpec;
    }

    public void setFontSpecification(FontSpecification fs) {
        this._fontSpec = fs;
    }
}

