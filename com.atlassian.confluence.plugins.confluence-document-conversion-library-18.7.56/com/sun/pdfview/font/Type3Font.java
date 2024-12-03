/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFParser;
import com.sun.pdfview.font.PDFFont;
import com.sun.pdfview.font.PDFFontDescriptor;
import com.sun.pdfview.font.PDFGlyph;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Type3Font
extends PDFFont {
    HashMap<String, PDFObject> rsrc = new HashMap();
    Map charProcs;
    Rectangle2D bbox;
    AffineTransform at;
    float[] widths;
    int firstChar;
    int lastChar;

    public Type3Font(String baseFont, PDFObject fontObj, HashMap<String, PDFObject> resources, PDFFontDescriptor descriptor) throws IOException {
        super(baseFont, descriptor);
        if (resources != null) {
            this.rsrc.putAll(resources);
        }
        PDFObject matrix = fontObj.getDictRef("FontMatrix");
        float[] matrixAry = new float[6];
        for (int i = 0; i < 6; ++i) {
            matrixAry[i] = matrix.getAt(i).getFloatValue();
        }
        this.at = new AffineTransform(matrixAry);
        float scale = matrixAry[0] + matrixAry[2];
        PDFObject rsrcObj = fontObj.getDictRef("Resources");
        if (rsrcObj != null) {
            this.rsrc.putAll(rsrcObj.getDictionary());
        }
        this.charProcs = fontObj.getDictRef("CharProcs").getDictionary();
        PDFObject[] bboxdef = fontObj.getDictRef("FontBBox").getArray();
        float[] bboxfdef = new float[4];
        for (int i = 0; i < 4; ++i) {
            bboxfdef[i] = bboxdef[i].getFloatValue();
        }
        this.bbox = new Rectangle2D.Float(bboxfdef[0], bboxfdef[1], bboxfdef[2] - bboxfdef[0], bboxfdef[3] - bboxfdef[1]);
        if (this.bbox.isEmpty()) {
            this.bbox = null;
        }
        PDFObject[] widthArray = fontObj.getDictRef("Widths").getArray();
        this.widths = new float[widthArray.length];
        for (int i = 0; i < widthArray.length; ++i) {
            this.widths[i] = widthArray[i].getFloatValue();
        }
        this.firstChar = fontObj.getDictRef("FirstChar").getIntValue();
        this.lastChar = fontObj.getDictRef("LastChar").getIntValue();
    }

    public int getFirstChar() {
        return this.firstChar;
    }

    public int getLastChar() {
        return this.lastChar;
    }

    @Override
    protected PDFGlyph getGlyph(char src, String name) {
        if (name == null) {
            throw new IllegalArgumentException("Glyph name required for Type3 font!Source character: " + src);
        }
        PDFObject pageObj = (PDFObject)this.charProcs.get(name);
        if (pageObj == null) {
            return new PDFGlyph(src, name, new GeneralPath(), new Point2D.Float(0.0f, 0.0f));
        }
        try {
            PDFPage page = new PDFPage(this.bbox, 0);
            page.addXform(this.at);
            PDFParser prc = new PDFParser(page, pageObj.getStream(), this.rsrc);
            prc.go(true);
            float width = this.widths[src - this.firstChar];
            Point2D advance = new Point2D.Float(width, 0.0f);
            advance = this.at.transform(advance, null);
            return new PDFGlyph(src, name, page, advance);
        }
        catch (IOException ioe) {
            System.out.println("IOException in Type3 font: " + ioe);
            ioe.printStackTrace();
            return null;
        }
    }
}

