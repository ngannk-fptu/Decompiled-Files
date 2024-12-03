/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfCopyFieldsImp;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BaseField {
    public static final float BORDER_WIDTH_THIN = 1.0f;
    public static final float BORDER_WIDTH_MEDIUM = 2.0f;
    public static final float BORDER_WIDTH_THICK = 3.0f;
    public static final int VISIBLE = 0;
    public static final int HIDDEN = 1;
    public static final int VISIBLE_BUT_DOES_NOT_PRINT = 2;
    public static final int HIDDEN_BUT_PRINTABLE = 3;
    public static final int READ_ONLY = 1;
    public static final int REQUIRED = 2;
    public static final int MULTILINE = 4096;
    public static final int DO_NOT_SCROLL = 0x800000;
    public static final int PASSWORD = 8192;
    public static final int FILE_SELECTION = 0x100000;
    public static final int DO_NOT_SPELL_CHECK = 0x400000;
    public static final int EDIT = 262144;
    public static final int MULTISELECT = 0x200000;
    public static final int COMB = 0x1000000;
    protected float borderWidth = 1.0f;
    protected int borderStyle = 0;
    protected Color borderColor;
    protected Color backgroundColor;
    protected Color textColor;
    protected BaseFont font;
    protected float fontSize = 0.0f;
    protected int alignment = 0;
    protected PdfWriter writer;
    protected String text;
    protected Rectangle box;
    protected int rotation = 0;
    protected int visibility;
    protected String fieldName;
    protected int options;
    protected int maxCharacterLength;
    private static final Map<PdfName, Integer> fieldKeys = new HashMap<PdfName, Integer>();

    public BaseField(PdfWriter writer, Rectangle box, String fieldName) {
        this.writer = writer;
        this.setBox(box);
        this.fieldName = fieldName;
    }

    protected BaseFont getRealFont() throws IOException, DocumentException {
        if (this.font == null) {
            return BaseFont.createFont("Helvetica", "Cp1252", false);
        }
        return this.font;
    }

    protected PdfAppearance getBorderAppearance() {
        PdfAppearance app = PdfAppearance.createAppearance(this.writer, this.box.getWidth(), this.box.getHeight());
        switch (this.rotation) {
            case 90: {
                app.setMatrix(0.0f, 1.0f, -1.0f, 0.0f, this.box.getHeight(), 0.0f);
                break;
            }
            case 180: {
                app.setMatrix(-1.0f, 0.0f, 0.0f, -1.0f, this.box.getWidth(), this.box.getHeight());
                break;
            }
            case 270: {
                app.setMatrix(0.0f, -1.0f, 1.0f, 0.0f, 0.0f, this.box.getWidth());
            }
        }
        app.saveState();
        if (this.backgroundColor != null) {
            app.setColorFill(this.backgroundColor);
            app.rectangle(0.0f, 0.0f, this.box.getWidth(), this.box.getHeight());
            app.fill();
        }
        if (this.borderStyle == 4) {
            if (this.borderWidth != 0.0f && this.borderColor != null) {
                app.setColorStroke(this.borderColor);
                app.setLineWidth(this.borderWidth);
                app.moveTo(0.0f, this.borderWidth / 2.0f);
                app.lineTo(this.box.getWidth(), this.borderWidth / 2.0f);
                app.stroke();
            }
        } else if (this.borderStyle == 2) {
            Color actual;
            if (this.borderWidth != 0.0f && this.borderColor != null) {
                app.setColorStroke(this.borderColor);
                app.setLineWidth(this.borderWidth);
                app.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
                app.stroke();
            }
            if ((actual = this.backgroundColor) == null) {
                actual = Color.white;
            }
            app.setGrayFill(1.0f);
            this.drawTopFrame(app);
            app.setColorFill(actual.darker());
            this.drawBottomFrame(app);
        } else if (this.borderStyle == 3) {
            if (this.borderWidth != 0.0f && this.borderColor != null) {
                app.setColorStroke(this.borderColor);
                app.setLineWidth(this.borderWidth);
                app.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
                app.stroke();
            }
            app.setGrayFill(0.5f);
            this.drawTopFrame(app);
            app.setGrayFill(0.75f);
            this.drawBottomFrame(app);
        } else if (this.borderWidth != 0.0f && this.borderColor != null) {
            if (this.borderStyle == 1) {
                app.setLineDash(3.0f, 0.0f);
            }
            app.setColorStroke(this.borderColor);
            app.setLineWidth(this.borderWidth);
            app.rectangle(this.borderWidth / 2.0f, this.borderWidth / 2.0f, this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
            app.stroke();
            if ((this.options & 0x1000000) != 0 && this.maxCharacterLength > 1) {
                float step = this.box.getWidth() / (float)this.maxCharacterLength;
                float yb = this.borderWidth / 2.0f;
                float yt = this.box.getHeight() - this.borderWidth / 2.0f;
                for (int k = 1; k < this.maxCharacterLength; ++k) {
                    float x = step * (float)k;
                    app.moveTo(x, yb);
                    app.lineTo(x, yt);
                }
                app.stroke();
            }
        }
        app.restoreState();
        return app;
    }

    @Deprecated
    protected static ArrayList getHardBreaks(String text) {
        return (ArrayList)BaseField.getAllHardBreaks(text);
    }

    protected static List<String> getAllHardBreaks(String text) {
        ArrayList<String> arr = new ArrayList<String>();
        char[] cs = text.toCharArray();
        int len = cs.length;
        StringBuffer buf = new StringBuffer();
        for (int k = 0; k < len; ++k) {
            char c = cs[k];
            if (c == '\r') {
                if (k + 1 < len && cs[k + 1] == '\n') {
                    ++k;
                }
                arr.add(buf.toString());
                buf = new StringBuffer();
                continue;
            }
            if (c == '\n') {
                arr.add(buf.toString());
                buf = new StringBuffer();
                continue;
            }
            buf.append(c);
        }
        arr.add(buf.toString());
        return arr;
    }

    protected static void trimRight(StringBuffer buf) {
        int len = buf.length();
        while (len != 0) {
            if (buf.charAt(--len) != ' ') {
                return;
            }
            buf.setLength(len);
        }
        return;
    }

    @Deprecated
    protected static ArrayList breakLines(ArrayList breaks, BaseFont font, float fontSize, float width) {
        return (ArrayList)BaseField.breakLines((List<String>)breaks, font, fontSize, width);
    }

    protected static List<String> breakLines(List<String> breaks, BaseFont font, float fontSize, float width) {
        ArrayList<String> lines = new ArrayList<String>();
        StringBuffer buf = new StringBuffer();
        for (String aBreak : breaks) {
            buf.setLength(0);
            float w = 0.0f;
            char[] cs = aBreak.toCharArray();
            int len = cs.length;
            int state = 0;
            int lastspace = -1;
            char c = '\u0000';
            int refk = 0;
            block6: for (int k = 0; k < len; ++k) {
                c = cs[k];
                switch (state) {
                    case 0: {
                        w += font.getWidthPoint(c, fontSize);
                        buf.append(c);
                        if (w > width) {
                            w = 0.0f;
                            if (buf.length() > 1) {
                                buf.setLength(buf.length() - 1);
                            }
                            lines.add(buf.toString());
                            buf.setLength(0);
                            refk = --k;
                            if (c == ' ') {
                                state = 2;
                                continue block6;
                            }
                            state = 1;
                            continue block6;
                        }
                        if (c == ' ') continue block6;
                        state = 1;
                        continue block6;
                    }
                    case 1: {
                        w += font.getWidthPoint(c, fontSize);
                        buf.append(c);
                        if (c == ' ') {
                            lastspace = k;
                        }
                        if (!(w > width)) continue block6;
                        w = 0.0f;
                        if (lastspace >= 0) {
                            k = lastspace;
                            buf.setLength(lastspace - refk);
                            BaseField.trimRight(buf);
                            lines.add(buf.toString());
                            buf.setLength(0);
                            refk = k;
                            lastspace = -1;
                            state = 2;
                            continue block6;
                        }
                        if (buf.length() > 1) {
                            buf.setLength(buf.length() - 1);
                        }
                        lines.add(buf.toString());
                        buf.setLength(0);
                        refk = --k;
                        if (c != ' ') continue block6;
                        state = 2;
                        continue block6;
                    }
                    case 2: {
                        if (c == ' ') continue block6;
                        w = 0.0f;
                        --k;
                        state = 1;
                    }
                }
            }
            BaseField.trimRight(buf);
            lines.add(buf.toString());
        }
        return lines;
    }

    private void drawTopFrame(PdfAppearance app) {
        app.moveTo(this.borderWidth, this.borderWidth);
        app.lineTo(this.borderWidth, this.box.getHeight() - this.borderWidth);
        app.lineTo(this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
        app.lineTo(this.box.getWidth() - 2.0f * this.borderWidth, this.box.getHeight() - 2.0f * this.borderWidth);
        app.lineTo(2.0f * this.borderWidth, this.box.getHeight() - 2.0f * this.borderWidth);
        app.lineTo(2.0f * this.borderWidth, 2.0f * this.borderWidth);
        app.lineTo(this.borderWidth, this.borderWidth);
        app.fill();
    }

    private void drawBottomFrame(PdfAppearance app) {
        app.moveTo(this.borderWidth, this.borderWidth);
        app.lineTo(this.box.getWidth() - this.borderWidth, this.borderWidth);
        app.lineTo(this.box.getWidth() - this.borderWidth, this.box.getHeight() - this.borderWidth);
        app.lineTo(this.box.getWidth() - 2.0f * this.borderWidth, this.box.getHeight() - 2.0f * this.borderWidth);
        app.lineTo(this.box.getWidth() - 2.0f * this.borderWidth, 2.0f * this.borderWidth);
        app.lineTo(2.0f * this.borderWidth, 2.0f * this.borderWidth);
        app.lineTo(this.borderWidth, this.borderWidth);
        app.fill();
    }

    public float getBorderWidth() {
        return this.borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getBorderStyle() {
        return this.borderStyle;
    }

    public void setBorderStyle(int borderStyle) {
        this.borderStyle = borderStyle;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getTextColor() {
        return this.textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public BaseFont getFont() {
        return this.font;
    }

    public void setFont(BaseFont font) {
        this.font = font;
    }

    public float getFontSize() {
        return this.fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Rectangle getBox() {
        return this.box;
    }

    public void setBox(Rectangle box) {
        if (box == null) {
            this.box = null;
        } else {
            this.box = new Rectangle(box);
            this.box.normalize();
        }
    }

    public int getRotation() {
        return this.rotation;
    }

    public void setRotation(int rotation) {
        if (rotation % 90 != 0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("rotation.must.be.a.multiple.of.90"));
        }
        if ((rotation %= 360) < 0) {
            rotation += 360;
        }
        this.rotation = rotation;
    }

    public void setRotationFromPage(Rectangle page) {
        this.setRotation(page.getRotation());
    }

    public int getVisibility() {
        return this.visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getOptions() {
        return this.options;
    }

    public void setOptions(int options) {
        this.options = options;
    }

    public int getMaxCharacterLength() {
        return this.maxCharacterLength;
    }

    public void setMaxCharacterLength(int maxCharacterLength) {
        this.maxCharacterLength = maxCharacterLength;
    }

    public PdfWriter getWriter() {
        return this.writer;
    }

    public void setWriter(PdfWriter writer) {
        this.writer = writer;
    }

    public static void moveFields(PdfDictionary from, PdfDictionary to) {
        Iterator<PdfName> i = from.getKeys().iterator();
        while (i.hasNext()) {
            PdfName key = i.next();
            if (!fieldKeys.containsKey(key)) continue;
            if (to != null) {
                to.put(key, from.get(key));
            }
            i.remove();
        }
    }

    static {
        fieldKeys.putAll(PdfCopyFieldsImp.fieldKeys);
        fieldKeys.put(PdfName.T, 1);
    }
}

