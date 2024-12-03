/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.SplitCharacter;
import com.lowagie.text.Utilities;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.HyphenationEvent;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.draw.DrawInterface;
import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class Chunk
implements Element {
    public static final String OBJECT_REPLACEMENT_CHARACTER = "\ufffc";
    public static final Chunk NEWLINE = new Chunk("\n");
    public static final Chunk NEXTPAGE = new Chunk("");
    protected StringBuffer content = null;
    protected Font font = null;
    protected Map<String, Object> attributes = null;
    public static final String SEPARATOR = "SEPARATOR";
    public static final String TAB = "TAB";
    public static final String HSCALE = "HSCALE";
    public static final String UNDERLINE = "UNDERLINE";
    public static final String SUBSUPSCRIPT = "SUBSUPSCRIPT";
    public static final String SKEW = "SKEW";
    public static final String BACKGROUND = "BACKGROUND";
    public static final String TEXTRENDERMODE = "TEXTRENDERMODE";
    public static final String SPLITCHARACTER = "SPLITCHARACTER";
    public static final String HYPHENATION = "HYPHENATION";
    public static final String REMOTEGOTO = "REMOTEGOTO";
    public static final String LOCALGOTO = "LOCALGOTO";
    public static final String LOCALDESTINATION = "LOCALDESTINATION";
    public static final String GENERICTAG = "GENERICTAG";
    public static final String IMAGE = "IMAGE";
    public static final String ACTION = "ACTION";
    public static final String NEWPAGE = "NEWPAGE";
    public static final String PDFANNOTATION = "PDFANNOTATION";
    public static final String COLOR = "COLOR";
    public static final String ENCODING = "ENCODING";
    public static final String CHAR_SPACING = "CHAR_SPACING";

    public Chunk() {
        this.content = new StringBuffer();
        this.font = new Font();
    }

    public Chunk(Chunk ck) {
        if (ck.content != null) {
            this.content = new StringBuffer(ck.content.toString());
        }
        if (ck.font != null) {
            this.font = new Font(ck.font);
        }
        if (ck.attributes != null) {
            this.attributes = new HashMap<String, Object>(ck.attributes);
        }
    }

    public Chunk(String content, Font font) {
        this.content = new StringBuffer(content);
        this.font = font;
    }

    public Chunk(String content) {
        this(content, new Font());
    }

    public Chunk(char c, Font font) {
        this.content = new StringBuffer();
        this.content.append(c);
        this.font = font;
    }

    public Chunk(char c) {
        this(c, new Font());
    }

    public Chunk(Image image, float offsetX, float offsetY) {
        this(OBJECT_REPLACEMENT_CHARACTER, new Font());
        Image copyImage = Image.getInstance(image);
        copyImage.setAbsolutePosition(Float.NaN, Float.NaN);
        this.setAttribute(IMAGE, new Object[]{copyImage, Float.valueOf(offsetX), Float.valueOf(offsetY), Boolean.FALSE});
    }

    public Chunk(DrawInterface separator) {
        this(separator, false);
    }

    public Chunk(DrawInterface separator, boolean vertical) {
        this(OBJECT_REPLACEMENT_CHARACTER, new Font());
        this.setAttribute(SEPARATOR, new Object[]{separator, vertical});
    }

    public Chunk(DrawInterface separator, float tabPosition) {
        this(separator, tabPosition, false);
    }

    public Chunk(DrawInterface separator, float tabPosition, boolean newline) {
        this(OBJECT_REPLACEMENT_CHARACTER, new Font());
        if (tabPosition < 0.0f) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("a.tab.position.may.not.be.lower.than.0.yours.is.1", String.valueOf(tabPosition)));
        }
        this.setAttribute(TAB, new Object[]{separator, Float.valueOf(tabPosition), newline, Float.valueOf(0.0f)});
    }

    public Chunk(Image image, float offsetX, float offsetY, boolean changeLeading) {
        this(OBJECT_REPLACEMENT_CHARACTER, new Font());
        this.setAttribute(IMAGE, new Object[]{image, Float.valueOf(offsetX), Float.valueOf(offsetY), changeLeading});
    }

    @Override
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (DocumentException de) {
            return false;
        }
    }

    @Override
    public int type() {
        return 10;
    }

    @Override
    public ArrayList<Element> getChunks() {
        ArrayList<Element> tmp = new ArrayList<Element>();
        tmp.add(this);
        return tmp;
    }

    public StringBuffer append(String string) {
        return this.content.append(string);
    }

    public void setFont(Font font) {
        this.font = font;
    }

    @Nullable
    public Font getFont() {
        return this.font;
    }

    public String getContent() {
        return this.content.toString();
    }

    @Override
    public String toString() {
        return this.getContent();
    }

    public boolean isEmpty() {
        return this.content.toString().trim().length() == 0 && !this.content.toString().contains("\n") && this.attributes == null;
    }

    public float getWidthPoint() {
        if (this.getImage() != null) {
            return this.getImage().getScaledWidth();
        }
        return this.font.getCalculatedBaseFont(true).getWidthPoint(this.getContent(), this.font.getCalculatedSize()) * this.getHorizontalScaling();
    }

    public boolean hasAttributes() {
        return this.attributes != null;
    }

    @Deprecated
    public HashMap getAttributes() {
        return (HashMap)this.attributes;
    }

    public Map<String, Object> getChunkAttributes() {
        return this.attributes;
    }

    @Deprecated
    public void setAttributes(HashMap attributes) {
        this.attributes = attributes;
    }

    public void setChunkAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    private Chunk setAttribute(String name, Object obj) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Object>();
        }
        this.attributes.put(name, obj);
        return this;
    }

    public Chunk setHorizontalScaling(float scale) {
        return this.setAttribute(HSCALE, Float.valueOf(scale));
    }

    public float getHorizontalScaling() {
        if (this.attributes == null) {
            return 1.0f;
        }
        Float f = (Float)this.attributes.get(HSCALE);
        if (f == null) {
            return 1.0f;
        }
        return f.floatValue();
    }

    public Chunk setUnderline(float thickness, float yPosition) {
        return this.setUnderline(null, thickness, 0.0f, yPosition, 0.0f, 0);
    }

    public Chunk setUnderline(Color color, float thickness, float thicknessMul, float yPosition, float yPositionMul, int cap) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Object>();
        }
        Object[] obj = new Object[]{color, new float[]{thickness, thicknessMul, yPosition, yPositionMul, cap}};
        Object[][] unders = Utilities.addToArray((Object[][])this.attributes.get(UNDERLINE), obj);
        return this.setAttribute(UNDERLINE, unders);
    }

    public Chunk setTextRise(float rise) {
        return this.setAttribute(SUBSUPSCRIPT, Float.valueOf(rise));
    }

    public float getTextRise() {
        if (this.attributes != null && this.attributes.containsKey(SUBSUPSCRIPT)) {
            return ((Float)this.attributes.get(SUBSUPSCRIPT)).floatValue();
        }
        return 0.0f;
    }

    public Chunk setSkew(float alpha, float beta) {
        alpha = (float)Math.tan((double)alpha * Math.PI / 180.0);
        beta = (float)Math.tan((double)beta * Math.PI / 180.0);
        return this.setAttribute(SKEW, new float[]{alpha, beta});
    }

    public Chunk setBackground(Color color) {
        return this.setBackground(color, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    public Chunk setBackground(Color color, float extraLeft, float extraBottom, float extraRight, float extraTop) {
        return this.setAttribute(BACKGROUND, new Object[]{color, new float[]{extraLeft, extraBottom, extraRight, extraTop}});
    }

    public Chunk setTextRenderMode(int mode, float strokeWidth, Color strokeColor) {
        return this.setAttribute(TEXTRENDERMODE, new Object[]{mode, Float.valueOf(strokeWidth), strokeColor});
    }

    public Chunk setSplitCharacter(SplitCharacter splitCharacter) {
        return this.setAttribute(SPLITCHARACTER, splitCharacter);
    }

    public Chunk setHyphenation(HyphenationEvent hyphenation) {
        return this.setAttribute(HYPHENATION, hyphenation);
    }

    public Chunk setRemoteGoto(String filename, String name) {
        return this.setAttribute(REMOTEGOTO, new Object[]{filename, name});
    }

    public Chunk setRemoteGoto(String filename, int page) {
        return this.setAttribute(REMOTEGOTO, new Object[]{filename, page});
    }

    public Chunk setLocalGoto(String name) {
        return this.setAttribute(LOCALGOTO, name);
    }

    public Chunk setLocalDestination(String name) {
        return this.setAttribute(LOCALDESTINATION, name);
    }

    public Chunk setGenericTag(String text) {
        return this.setAttribute(GENERICTAG, text);
    }

    public Image getImage() {
        if (this.attributes == null) {
            return null;
        }
        Object[] obj = (Object[])this.attributes.get(IMAGE);
        if (obj == null) {
            return null;
        }
        return (Image)obj[0];
    }

    public Chunk setAction(PdfAction action) {
        return this.setAttribute(ACTION, action);
    }

    public Chunk setAnchor(URL url) {
        return this.setAttribute(ACTION, new PdfAction(url.toExternalForm()));
    }

    public Chunk setAnchor(String url) {
        return this.setAttribute(ACTION, new PdfAction(url));
    }

    public Chunk setNewPage() {
        return this.setAttribute(NEWPAGE, null);
    }

    public Chunk setAnnotation(PdfAnnotation annotation) {
        return this.setAttribute(PDFANNOTATION, annotation);
    }

    @Override
    public boolean isContent() {
        return true;
    }

    @Override
    public boolean isNestable() {
        return true;
    }

    public HyphenationEvent getHyphenation() {
        if (this.attributes == null) {
            return null;
        }
        return (HyphenationEvent)this.attributes.get(HYPHENATION);
    }

    public Chunk setCharacterSpacing(float charSpace) {
        return this.setAttribute(CHAR_SPACING, Float.valueOf(charSpace));
    }

    public float getCharacterSpacing() {
        if (this.attributes != null && this.attributes.containsKey(CHAR_SPACING)) {
            return ((Float)this.attributes.get(CHAR_SPACING)).floatValue();
        }
        return 0.0f;
    }

    static {
        NEXTPAGE.setNewPage();
    }
}

