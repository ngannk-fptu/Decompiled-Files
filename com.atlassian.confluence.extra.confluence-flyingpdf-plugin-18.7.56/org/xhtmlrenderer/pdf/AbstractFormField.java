/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.pdf.PdfTemplate;
import java.awt.Point;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.parser.FSCMYKColor;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.CssContext;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextReplacedElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.util.Util;

public abstract class AbstractFormField
implements ITextReplacedElement {
    protected static final String DEFAULT_CHECKED_STATE = "Yes";
    protected static final String OFF_STATE = "Off";
    private static final float FONT_SIZE_ADJUSTMENT = 0.8f;
    private int _x;
    private int _y;
    private int _width;
    private int _height;
    private String _fieldName;

    protected abstract String getFieldType();

    protected int getX() {
        return this._x;
    }

    protected void setX(int x) {
        this._x = x;
    }

    protected int getY() {
        return this._y;
    }

    protected void setY(int y) {
        this._y = y;
    }

    protected int getWidth() {
        return this._width;
    }

    protected void setWidth(int width) {
        this._width = width;
    }

    protected int getHeight() {
        return this._height;
    }

    protected void setHeight(int height) {
        this._height = height;
    }

    protected String getFieldName(ITextOutputDevice outputDevice, Element e) {
        if (this._fieldName == null) {
            String result = e.getAttribute("name");
            this._fieldName = Util.isNullOrEmpty(result) ? this.getFieldType() + outputDevice.getNextFormFieldIndex() : result;
        }
        return this._fieldName;
    }

    protected String getValue(Element e) {
        String result = e.getAttribute("value");
        if (Util.isNullOrEmpty(result)) {
            return DEFAULT_CHECKED_STATE;
        }
        return result;
    }

    protected boolean isChecked(Element e) {
        return !Util.isNullOrEmpty(e.getAttribute("checked"));
    }

    protected boolean isReadOnly(Element e) {
        return !Util.isNullOrEmpty(e.getAttribute("readonly"));
    }

    protected boolean isSelected(Element e) {
        return Util.isNullOrEmpty(e.getAttribute("selected"));
    }

    @Override
    public void detach(LayoutContext c) {
    }

    @Override
    public int getIntrinsicHeight() {
        return this.getHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        return this.getWidth();
    }

    @Override
    public Point getLocation() {
        return new Point(this.getX(), this.getY());
    }

    @Override
    public boolean isRequiresInteractivePaint() {
        return false;
    }

    @Override
    public void setLocation(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    protected void initDimensions(LayoutContext c, BlockBox box, int cssWidth, int cssHeight) {
        if (cssWidth != -1) {
            this.setWidth(cssWidth);
        } else if (cssHeight != -1) {
            this.setWidth(cssHeight);
        } else {
            this.setWidth((int)(box.getStyle().getFont((CssContext)c).size * 0.8f));
        }
        if (cssHeight != -1) {
            this.setHeight(cssHeight);
        } else if (cssWidth != -1) {
            this.setHeight(cssWidth);
        } else {
            this.setHeight((int)(box.getStyle().getFont((CssContext)c).size * 0.8f));
        }
    }

    protected String spaces(int count) {
        StringBuffer result = new StringBuffer(count);
        for (int i = 0; i < count; ++i) {
            result.append(' ');
        }
        return result.toString();
    }

    protected void setStrokeColor(PdfTemplate template, FSColor color) {
        if (color instanceof FSRGBColor) {
            FSRGBColor rgb = (FSRGBColor)color;
            template.setRGBColorStroke(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
        } else if (color instanceof FSCMYKColor) {
            FSCMYKColor cmyk = (FSCMYKColor)color;
            template.setCMYKColorStroke((int)(cmyk.getCyan() * 255.0f), (int)(cmyk.getMagenta() * 255.0f), (int)(cmyk.getYellow() * 255.0f), (int)(cmyk.getBlack() * 255.0f));
        }
    }

    protected void setFillColor(PdfTemplate template, FSColor color) {
        if (color instanceof FSRGBColor) {
            FSRGBColor rgb = (FSRGBColor)color;
            template.setRGBColorFill(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
        } else if (color instanceof FSCMYKColor) {
            FSCMYKColor cmyk = (FSCMYKColor)color;
            template.setCMYKColorFill((int)(cmyk.getCyan() * 255.0f), (int)(cmyk.getMagenta() * 255.0f), (int)(cmyk.getYellow() * 255.0f), (int)(cmyk.getBlack() * 255.0f));
        }
    }
}

