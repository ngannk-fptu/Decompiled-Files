/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.font.NativeFont;
import com.sun.pdfview.font.PDFFontDescriptor;
import com.sun.pdfview.font.Type1Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class BuiltinFont
extends Type1Font {
    private static Properties props;
    private static Map fonts;
    private static final String[] baseFonts;
    private static final String[] mappedFonts;

    public BuiltinFont(String baseFont, PDFObject fontObj) throws IOException {
        super(baseFont, fontObj, null);
        this.parseFont(baseFont);
    }

    public BuiltinFont(String baseFont, PDFObject fontObj, PDFFontDescriptor descriptor) throws IOException {
        super(baseFont, fontObj, descriptor);
        int style;
        int i;
        String fontName = descriptor.getFontName();
        for (i = 0; i < baseFonts.length; ++i) {
            if (!fontName.equalsIgnoreCase(baseFonts[i])) continue;
            this.parseFont(fontName);
            return;
        }
        for (i = 0; i < mappedFonts.length; i += 2) {
            if (!fontName.equalsIgnoreCase(mappedFonts[i])) continue;
            this.parseFont(mappedFonts[i + 1]);
            return;
        }
        int flags = descriptor.getFlags();
        int n = style = (flags & 0x40000) != 0 ? 1 : 0;
        if (fontName.indexOf("Bold") > 0) {
            style |= 1;
        }
        if (descriptor.getItalicAngle() != 0 || (flags & 0x20) != 0) {
            style |= 2;
        }
        String name = null;
        name = (flags & 1) != 0 ? ((style & 1) > 0 && (style & 2) > 0 ? "Courier-BoldOblique" : ((style & 1) > 0 ? "Courier-Bold" : ((style & 2) > 0 ? "Courier-Oblique" : "Courier"))) : ((flags & 2) != 0 ? ((style & 1) > 0 && (style & 2) > 0 ? "Times-BoldItalic" : ((style & 1) > 0 ? "Times-Bold" : ((style & 2) > 0 ? "Times-Italic" : "Times-Roman"))) : ((style & 1) > 0 && (style & 2) > 0 ? "Helvetica-BoldOblique" : ((style & 1) > 0 ? "Helvetica-Bold" : ((style & 2) > 0 ? "Helvetica-Oblique" : "Helvetica"))));
        this.parseFont(name);
    }

    private void parseFont(String baseFont) throws IOException {
        if (props == null) {
            props = new Properties();
            props.load(BuiltinFont.class.getResourceAsStream("res/BaseFonts.properties"));
        }
        if (!props.containsKey(baseFont + ".file")) {
            throw new IllegalArgumentException("Unknown Base Font: " + baseFont);
        }
        String file = props.getProperty(baseFont + ".file");
        int length = Integer.parseInt(props.getProperty(baseFont + ".length"));
        int length1 = 0;
        int length2 = 0;
        byte[] data = new byte[length];
        InputStream fontStream = NativeFont.class.getResourceAsStream("res/" + file);
        for (int cur = 0; cur < length; cur += fontStream.read(data, cur, length - cur)) {
        }
        fontStream.close();
        if ((data[0] & 0xFF) == 128) {
            length1 = data[2] & 0xFF;
            length1 |= (data[3] & 0xFF) << 8;
            length1 |= (data[4] & 0xFF) << 16;
            length1 |= (data[5] & 0xFF) << 24;
            length2 = data[(length1 += 6) + 2] & 0xFF;
            length2 |= (data[length1 + 3] & 0xFF) << 8;
            length2 |= (data[length1 + 4] & 0xFF) << 16;
            length2 |= (data[length1 + 5] & 0xFF) << 24;
            length1 += 6;
        } else {
            length1 = Integer.parseInt(props.getProperty(baseFont + ".length1"));
            length2 = props.containsKey(baseFont + ".length2") ? Integer.parseInt(props.getProperty(baseFont + ".lenth2")) : length - length1;
        }
        this.parseFont(data, length1, length2);
    }

    static {
        baseFonts = new String[]{"Courier", "Courier-Bold", "Courier-BoldOblique", "Courier-Oblique", "Helvetica", "Helvetica-Bold", "Helvetica-BoldOblique", "Helvetica-Oblique", "Times-Roman", "Times-Bold", "Times-BoldItalic", "Times-Italic", "Symbol", "ZapfDingbats"};
        mappedFonts = new String[]{"Arial", "Helvetica", "Arial,Bold", "Helvetica-Bold", "Arial,BoldItalic", "Helvetica-BoldOblique", "Arial,Italic", "Helvetica-Oblique", "TimesNewRoman", "Times-Roman", "TimesNewRoman,Bold", "Times-Bold", "TimesNewRoman,BoldItalic", "Times-BoldItalic", "TimesNewRoman,Italic", "Times-Italic"};
    }
}

