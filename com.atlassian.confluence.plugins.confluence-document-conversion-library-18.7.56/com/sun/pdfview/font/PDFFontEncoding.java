/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.FontSupport;
import com.sun.pdfview.font.PDFCMap;
import com.sun.pdfview.font.PDFFont;
import com.sun.pdfview.font.PDFGlyph;
import com.sun.pdfview.font.Type0Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFFontEncoding {
    private static final int TYPE_ENCODING = 0;
    private static final int TYPE_CMAP = 1;
    private int[] baseEncoding;
    private Map<Character, String> differences;
    private PDFCMap cmap;
    private int type;

    public PDFFontEncoding(String fontType, PDFObject encoding) throws IOException {
        if (encoding.getType() == 4) {
            if (fontType.equals("Type0")) {
                this.type = 1;
                this.cmap = PDFCMap.getCMap(encoding.getStringValue());
            } else {
                this.type = 0;
                this.differences = new HashMap<Character, String>();
                this.baseEncoding = this.getBaseEncoding(encoding.getStringValue());
            }
        } else {
            String typeStr = encoding.getDictRef("Type").getStringValue();
            if (typeStr.equals("Encoding")) {
                this.type = 0;
                this.parseEncoding(encoding);
            } else if (typeStr.equals("CMap")) {
                this.type = 1;
                this.cmap = PDFCMap.getCMap(encoding);
            } else {
                throw new IllegalArgumentException("Uknown encoding type: " + this.type);
            }
        }
    }

    public List<PDFGlyph> getGlyphs(PDFFont font, String text) {
        ArrayList<PDFGlyph> outList = new ArrayList<PDFGlyph>(text.length());
        char[] arry = text.toCharArray();
        block4: for (int i = 0; i < arry.length; ++i) {
            switch (this.type) {
                case 0: {
                    outList.add(this.getGlyphFromEncoding(font, arry[i]));
                    continue block4;
                }
                case 1: {
                    char c = (char)((arry[i] & 0xFF) << 8);
                    if (i < arry.length - 1) {
                        c = (char)(c | (char)(arry[++i] & 0xFF));
                    }
                    outList.add(this.getGlyphFromCMap(font, c));
                }
            }
        }
        return outList;
    }

    private PDFGlyph getGlyphFromEncoding(PDFFont font, char src) {
        String charName = null;
        if (this.differences.containsKey(new Character(src = (char)(src & 0xFF)))) {
            charName = this.differences.get(new Character(src));
        } else if (this.baseEncoding != null) {
            int charID = this.baseEncoding[src];
            charName = FontSupport.getName(charID);
        }
        return font.getCachedGlyph(src, charName);
    }

    private PDFGlyph getGlyphFromCMap(PDFFont font, char src) {
        int fontID = this.cmap.getFontID(src);
        char charID = this.cmap.map(src);
        if (font instanceof Type0Font) {
            font = ((Type0Font)font).getDescendantFont(fontID);
        }
        return font.getCachedGlyph(charID, null);
    }

    public void parseEncoding(PDFObject encoding) throws IOException {
        PDFObject diffArrayObj;
        this.differences = new HashMap<Character, String>();
        PDFObject baseEncObj = encoding.getDictRef("BaseEncoding");
        if (baseEncObj != null) {
            this.baseEncoding = this.getBaseEncoding(baseEncObj.getStringValue());
        }
        if ((diffArrayObj = encoding.getDictRef("Differences")) != null) {
            PDFObject[] diffArray = diffArrayObj.getArray();
            int curPosition = -1;
            for (int i = 0; i < diffArray.length; ++i) {
                if (diffArray[i].getType() == 2) {
                    curPosition = diffArray[i].getIntValue();
                    continue;
                }
                if (diffArray[i].getType() == 4) {
                    Character key = new Character((char)curPosition);
                    this.differences.put(key, diffArray[i].getStringValue());
                    ++curPosition;
                    continue;
                }
                throw new IllegalArgumentException("Unexpected type in diff array: " + diffArray[i]);
            }
        }
    }

    private int[] getBaseEncoding(String encodingName) {
        if (encodingName.equals("MacRomanEncoding")) {
            return FontSupport.macRomanEncoding;
        }
        if (encodingName.equals("MacExpertEncoding")) {
            return FontSupport.type1CExpertCharset;
        }
        if (encodingName.equals("WinAnsiEncoding")) {
            return FontSupport.winAnsiEncoding;
        }
        throw new IllegalArgumentException("Unknown encoding: " + encodingName);
    }
}

