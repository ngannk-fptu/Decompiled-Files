/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

public class Barcode128
extends Barcode {
    private static final byte[][] BARS;
    private static final byte[] BARS_STOP;
    public static final char CODE_AB_TO_C = 'c';
    public static final char CODE_AC_TO_B = 'd';
    public static final char CODE_BC_TO_A = 'e';
    public static final char FNC1_INDEX = 'f';
    public static final char START_A = 'g';
    public static final char START_B = 'h';
    public static final char START_C = 'i';
    public static final char FNC1 = '\u00ca';
    public static final char DEL = '\u00c3';
    public static final char FNC3 = '\u00c4';
    public static final char FNC2 = '\u00c5';
    public static final char SHIFT = '\u00c6';
    public static final char CODE_C = '\u00c7';
    public static final char CODE_A = '\u00c8';
    public static final char FNC4 = '\u00c8';
    public static final char STARTA = '\u00cb';
    public static final char STARTB = '\u00cc';
    public static final char STARTC = '\u00cd';
    private static final IntHashtable ais;

    public Barcode128() {
        try {
            this.x = 0.8f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.baseline = this.size = 8.0f;
            this.barHeight = this.size * 3.0f;
            this.textAlignment = 1;
            this.codeType = 9;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public static String removeFNC1(String code) {
        int len = code.length();
        StringBuilder buf = new StringBuilder(len);
        for (int k = 0; k < len; ++k) {
            char c = code.charAt(k);
            if (c < ' ' || c > '~') continue;
            buf.append(c);
        }
        return buf.toString();
    }

    public static String getHumanReadableUCCEAN(String code) {
        StringBuilder buf = new StringBuilder();
        String fnc1 = String.valueOf('\u00ca');
        try {
            while (true) {
                if (code.startsWith(fnc1)) {
                    code = code.substring(1);
                    continue;
                }
                int n = 0;
                int idlen = 0;
                for (int k = 2; k < 5 && code.length() >= k; ++k) {
                    n = ais.get(Integer.parseInt(code.substring(0, k)));
                    if (n == 0) continue;
                    idlen = k;
                    break;
                }
                if (idlen != 0) {
                    buf.append('(').append(code, 0, idlen).append(')');
                    code = code.substring(idlen);
                    if (n > 0) {
                        if (code.length() > (n -= idlen)) {
                            buf.append(Barcode128.removeFNC1(code.substring(0, n)));
                            code = code.substring(n);
                            continue;
                        }
                    } else {
                        int idx = code.indexOf(202);
                        if (idx >= 0) {
                            buf.append(code, 0, idx);
                            code = code.substring(idx + 1);
                            continue;
                        }
                    }
                }
                break;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        buf.append(Barcode128.removeFNC1(code));
        return buf.toString();
    }

    static boolean isNextDigits(String text, int textIndex, int numDigits) {
        int len = text.length();
        while (textIndex < len && numDigits > 0) {
            if (text.charAt(textIndex) == '\u00ca') {
                ++textIndex;
                continue;
            }
            int n = Math.min(2, numDigits);
            if (textIndex + n > len) {
                return false;
            }
            while (n-- > 0) {
                char c;
                if ((c = text.charAt(textIndex++)) < '0' || c > '9') {
                    return false;
                }
                --numDigits;
            }
        }
        return numDigits == 0;
    }

    static String getPackedRawDigits(String text, int textIndex, int numDigits) {
        String out = "";
        int start = textIndex;
        while (numDigits > 0) {
            if (text.charAt(textIndex) == '\u00ca') {
                out = out + 'f';
                ++textIndex;
                continue;
            }
            numDigits -= 2;
            int c1 = text.charAt(textIndex++) - 48;
            int c2 = text.charAt(textIndex++) - 48;
            out = out + (char)(c1 * 10 + c2);
        }
        return (char)(textIndex - start) + out;
    }

    public static String getRawText(String text, boolean ucc) {
        String out2;
        String out = "";
        int tLen = text.length();
        if (tLen == 0) {
            out = out + 'h';
            if (ucc) {
                out = out + 'f';
            }
            return out;
        }
        char c = '\u0000';
        for (int k = 0; k < tLen; ++k) {
            c = text.charAt(k);
            if (c <= '\u007f' || c == '\u00ca') continue;
            throw new RuntimeException(MessageLocalization.getComposedMessage("there.are.illegal.characters.for.barcode.128.in.1", text));
        }
        c = text.charAt(0);
        int currentCode = 104;
        int index = 0;
        if (Barcode128.isNextDigits(text, index, 2)) {
            currentCode = 105;
            out = out + (char)currentCode;
            if (ucc) {
                out = out + 'f';
            }
            out2 = Barcode128.getPackedRawDigits(text, index, 2);
            index += out2.charAt(0);
            out = out + out2.substring(1);
        } else if (c < ' ') {
            currentCode = 103;
            out = out + (char)currentCode;
            if (ucc) {
                out = out + 'f';
            }
            out = out + (char)(c + 64);
            ++index;
        } else {
            out = out + (char)currentCode;
            if (ucc) {
                out = out + 'f';
            }
            out = c == '\u00ca' ? out + 'f' : out + (char)(c - 32);
            ++index;
        }
        while (index < tLen) {
            switch (currentCode) {
                case 103: {
                    if (Barcode128.isNextDigits(text, index, 4)) {
                        currentCode = 105;
                        out = out + 'c';
                        out2 = Barcode128.getPackedRawDigits(text, index, 4);
                        index += out2.charAt(0);
                        out = out + out2.substring(1);
                        break;
                    }
                    if ((c = text.charAt(index++)) == '\u00ca') {
                        out = out + 'f';
                        break;
                    }
                    if (c > '_') {
                        currentCode = 104;
                        out = out + 'd';
                        out = out + (char)(c - 32);
                        break;
                    }
                    if (c < ' ') {
                        out = out + (char)(c + 64);
                        break;
                    }
                    out = out + (char)(c - 32);
                    break;
                }
                case 104: {
                    if (Barcode128.isNextDigits(text, index, 4)) {
                        currentCode = 105;
                        out = out + 'c';
                        out2 = Barcode128.getPackedRawDigits(text, index, 4);
                        index += out2.charAt(0);
                        out = out + out2.substring(1);
                        break;
                    }
                    if ((c = text.charAt(index++)) == '\u00ca') {
                        out = out + 'f';
                        break;
                    }
                    if (c < ' ') {
                        currentCode = 103;
                        out = out + 'e';
                        out = out + (char)(c + 64);
                        break;
                    }
                    out = out + (char)(c - 32);
                    break;
                }
                case 105: {
                    if (Barcode128.isNextDigits(text, index, 2)) {
                        out2 = Barcode128.getPackedRawDigits(text, index, 2);
                        index += out2.charAt(0);
                        out = out + out2.substring(1);
                        break;
                    }
                    if ((c = text.charAt(index++)) == '\u00ca') {
                        out = out + 'f';
                        break;
                    }
                    if (c < ' ') {
                        currentCode = 103;
                        out = out + 'e';
                        out = out + (char)(c + 64);
                        break;
                    }
                    currentCode = 104;
                    out = out + 'd';
                    out = out + (char)(c - 32);
                }
            }
        }
        return out;
    }

    public static byte[] getBarsCode128Raw(String text) {
        int k;
        int idx = text.indexOf(65535);
        if (idx >= 0) {
            text = text.substring(0, idx);
        }
        int chk = text.charAt(0);
        for (int k2 = 1; k2 < text.length(); ++k2) {
            chk += k2 * text.charAt(k2);
        }
        text = text + (char)(chk %= 103);
        byte[] bars = new byte[(text.length() + 1) * 6 + 7];
        for (k = 0; k < text.length(); ++k) {
            System.arraycopy(BARS[text.charAt(k)], 0, bars, k * 6, 6);
        }
        System.arraycopy(BARS_STOP, 0, bars, k * 6, 7);
        return bars;
    }

    @Override
    public Rectangle getBarcodeSize() {
        String fullCode;
        int idx;
        float fontX = 0.0f;
        float fontY = 0.0f;
        if (this.font != null) {
            fontY = this.baseline > 0.0f ? this.baseline - this.font.getFontDescriptor(3, this.size) : -this.baseline + this.size;
            fullCode = this.codeType == 11 ? ((idx = this.code.indexOf(65535)) < 0 ? "" : this.code.substring(idx + 1)) : (this.codeType == 10 ? Barcode128.getHumanReadableUCCEAN(this.code) : Barcode128.removeFNC1(this.code));
            fontX = this.font.getWidthPoint(this.altText != null ? this.altText : fullCode, this.size);
        }
        fullCode = this.codeType == 11 ? ((idx = this.code.indexOf(65535)) >= 0 ? this.code.substring(0, idx) : this.code) : Barcode128.getRawText(this.code, this.codeType == 10);
        int len = fullCode.length();
        float fullWidth = (float)((len + 2) * 11) * this.x + 2.0f * this.x;
        fullWidth = Math.max(fullWidth, fontX);
        float fullHeight = this.barHeight + fontY;
        return new Rectangle(fullWidth, fullHeight);
    }

    @Override
    public Rectangle placeBarcode(PdfContentByte cb, Color barColor, Color textColor) {
        int idx;
        int idx2;
        String fullCode = this.codeType == 11 ? ((idx2 = this.code.indexOf(65535)) < 0 ? "" : this.code.substring(idx2 + 1)) : (this.codeType == 10 ? Barcode128.getHumanReadableUCCEAN(this.code) : Barcode128.removeFNC1(this.code));
        float fontX = 0.0f;
        if (this.font != null) {
            fullCode = this.altText != null ? this.altText : fullCode;
            fontX = this.font.getWidthPoint(fullCode, this.size);
        }
        String bCode = this.codeType == 11 ? ((idx = this.code.indexOf(65535)) >= 0 ? this.code.substring(0, idx) : this.code) : Barcode128.getRawText(this.code, this.codeType == 10);
        int len = bCode.length();
        float fullWidth = (float)((len + 2) * 11) * this.x + 2.0f * this.x;
        float barStartX = 0.0f;
        float textStartX = 0.0f;
        switch (this.textAlignment) {
            case 0: {
                break;
            }
            case 2: {
                if (fontX > fullWidth) {
                    barStartX = fontX - fullWidth;
                    break;
                }
                textStartX = fullWidth - fontX;
                break;
            }
            default: {
                if (fontX > fullWidth) {
                    barStartX = (fontX - fullWidth) / 2.0f;
                    break;
                }
                textStartX = (fullWidth - fontX) / 2.0f;
            }
        }
        float barStartY = 0.0f;
        float textStartY = 0.0f;
        if (this.font != null) {
            if (this.baseline <= 0.0f) {
                textStartY = this.barHeight - this.baseline;
            } else {
                textStartY = -this.font.getFontDescriptor(3, this.size);
                barStartY = textStartY + this.baseline;
            }
        }
        byte[] bars = Barcode128.getBarsCode128Raw(bCode);
        boolean print = true;
        if (barColor != null) {
            cb.setColorFill(barColor);
        }
        for (byte bar : bars) {
            float w = (float)bar * this.x;
            if (print) {
                cb.rectangle(barStartX, barStartY, w - this.inkSpreading, this.barHeight);
            }
            print = !print;
            barStartX += w;
        }
        cb.fill();
        if (this.font != null) {
            if (textColor != null) {
                cb.setColorFill(textColor);
            }
            cb.beginText();
            cb.setFontAndSize(this.font, this.size);
            cb.setTextMatrix(textStartX, textStartY);
            cb.showText(fullCode);
            cb.endText();
        }
        return this.getBarcodeSize();
    }

    @Override
    public Image createAwtImage(Color foreground, Color background) {
        int idx;
        int f = foreground.getRGB();
        int g = background.getRGB();
        Canvas canvas = new Canvas();
        String bCode = this.codeType == 11 ? ((idx = this.code.indexOf(65535)) >= 0 ? this.code.substring(0, idx) : this.code) : Barcode128.getRawText(this.code, this.codeType == 10);
        int len = bCode.length();
        int fullWidth = (len + 2) * 11 + 2;
        byte[] bars = Barcode128.getBarsCode128Raw(bCode);
        boolean print = true;
        int ptr = 0;
        int height = (int)this.barHeight;
        int[] pix = new int[fullWidth * height];
        for (int n : bars) {
            int c = g;
            if (print) {
                c = f;
            }
            print = !print;
            for (int j = 0; j < n; ++j) {
                pix[ptr++] = c;
            }
        }
        for (int k = fullWidth; k < pix.length; k += fullWidth) {
            System.arraycopy(pix, 0, pix, k, fullWidth);
        }
        Image img = canvas.createImage(new MemoryImageSource(fullWidth, height, pix, 0, fullWidth));
        return img;
    }

    @Override
    public void setCode(String code) {
        if (this.getCodeType() == 10 && code.startsWith("(")) {
            int idx = 0;
            String ret = "";
            while (idx >= 0) {
                int end = code.indexOf(41, idx);
                if (end < 0) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("badly.formed.ucc.string.1", code));
                }
                String sai = code.substring(idx + 1, end);
                if (sai.length() < 2) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("ai.too.short.1", sai));
                }
                int ai = Integer.parseInt(sai);
                int len = ais.get(ai);
                if (len == 0) {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("ai.not.found.1", sai));
                }
                sai = String.valueOf(ai);
                if (sai.length() == 1) {
                    sai = "0" + sai;
                }
                int next = (idx = code.indexOf(40, end)) < 0 ? code.length() : idx;
                ret = ret + sai + code.substring(end + 1, next);
                if (len < 0) {
                    if (idx < 0) continue;
                    ret = ret + '\u00ca';
                    continue;
                }
                if (next - end - 1 + sai.length() == len) continue;
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.ai.length.1", sai));
            }
            super.setCode(ret);
        } else {
            super.setCode(code);
        }
    }

    static {
        int k;
        BARS = new byte[][]{{2, 1, 2, 2, 2, 2}, {2, 2, 2, 1, 2, 2}, {2, 2, 2, 2, 2, 1}, {1, 2, 1, 2, 2, 3}, {1, 2, 1, 3, 2, 2}, {1, 3, 1, 2, 2, 2}, {1, 2, 2, 2, 1, 3}, {1, 2, 2, 3, 1, 2}, {1, 3, 2, 2, 1, 2}, {2, 2, 1, 2, 1, 3}, {2, 2, 1, 3, 1, 2}, {2, 3, 1, 2, 1, 2}, {1, 1, 2, 2, 3, 2}, {1, 2, 2, 1, 3, 2}, {1, 2, 2, 2, 3, 1}, {1, 1, 3, 2, 2, 2}, {1, 2, 3, 1, 2, 2}, {1, 2, 3, 2, 2, 1}, {2, 2, 3, 2, 1, 1}, {2, 2, 1, 1, 3, 2}, {2, 2, 1, 2, 3, 1}, {2, 1, 3, 2, 1, 2}, {2, 2, 3, 1, 1, 2}, {3, 1, 2, 1, 3, 1}, {3, 1, 1, 2, 2, 2}, {3, 2, 1, 1, 2, 2}, {3, 2, 1, 2, 2, 1}, {3, 1, 2, 2, 1, 2}, {3, 2, 2, 1, 1, 2}, {3, 2, 2, 2, 1, 1}, {2, 1, 2, 1, 2, 3}, {2, 1, 2, 3, 2, 1}, {2, 3, 2, 1, 2, 1}, {1, 1, 1, 3, 2, 3}, {1, 3, 1, 1, 2, 3}, {1, 3, 1, 3, 2, 1}, {1, 1, 2, 3, 1, 3}, {1, 3, 2, 1, 1, 3}, {1, 3, 2, 3, 1, 1}, {2, 1, 1, 3, 1, 3}, {2, 3, 1, 1, 1, 3}, {2, 3, 1, 3, 1, 1}, {1, 1, 2, 1, 3, 3}, {1, 1, 2, 3, 3, 1}, {1, 3, 2, 1, 3, 1}, {1, 1, 3, 1, 2, 3}, {1, 1, 3, 3, 2, 1}, {1, 3, 3, 1, 2, 1}, {3, 1, 3, 1, 2, 1}, {2, 1, 1, 3, 3, 1}, {2, 3, 1, 1, 3, 1}, {2, 1, 3, 1, 1, 3}, {2, 1, 3, 3, 1, 1}, {2, 1, 3, 1, 3, 1}, {3, 1, 1, 1, 2, 3}, {3, 1, 1, 3, 2, 1}, {3, 3, 1, 1, 2, 1}, {3, 1, 2, 1, 1, 3}, {3, 1, 2, 3, 1, 1}, {3, 3, 2, 1, 1, 1}, {3, 1, 4, 1, 1, 1}, {2, 2, 1, 4, 1, 1}, {4, 3, 1, 1, 1, 1}, {1, 1, 1, 2, 2, 4}, {1, 1, 1, 4, 2, 2}, {1, 2, 1, 1, 2, 4}, {1, 2, 1, 4, 2, 1}, {1, 4, 1, 1, 2, 2}, {1, 4, 1, 2, 2, 1}, {1, 1, 2, 2, 1, 4}, {1, 1, 2, 4, 1, 2}, {1, 2, 2, 1, 1, 4}, {1, 2, 2, 4, 1, 1}, {1, 4, 2, 1, 1, 2}, {1, 4, 2, 2, 1, 1}, {2, 4, 1, 2, 1, 1}, {2, 2, 1, 1, 1, 4}, {4, 1, 3, 1, 1, 1}, {2, 4, 1, 1, 1, 2}, {1, 3, 4, 1, 1, 1}, {1, 1, 1, 2, 4, 2}, {1, 2, 1, 1, 4, 2}, {1, 2, 1, 2, 4, 1}, {1, 1, 4, 2, 1, 2}, {1, 2, 4, 1, 1, 2}, {1, 2, 4, 2, 1, 1}, {4, 1, 1, 2, 1, 2}, {4, 2, 1, 1, 1, 2}, {4, 2, 1, 2, 1, 1}, {2, 1, 2, 1, 4, 1}, {2, 1, 4, 1, 2, 1}, {4, 1, 2, 1, 2, 1}, {1, 1, 1, 1, 4, 3}, {1, 1, 1, 3, 4, 1}, {1, 3, 1, 1, 4, 1}, {1, 1, 4, 1, 1, 3}, {1, 1, 4, 3, 1, 1}, {4, 1, 1, 1, 1, 3}, {4, 1, 1, 3, 1, 1}, {1, 1, 3, 1, 4, 1}, {1, 1, 4, 1, 3, 1}, {3, 1, 1, 1, 4, 1}, {4, 1, 1, 1, 3, 1}, {2, 1, 1, 4, 1, 2}, {2, 1, 1, 2, 1, 4}, {2, 1, 1, 2, 3, 2}};
        BARS_STOP = new byte[]{2, 3, 3, 1, 1, 1, 2};
        ais = new IntHashtable();
        ais.put(0, 20);
        ais.put(1, 16);
        ais.put(2, 16);
        ais.put(10, -1);
        ais.put(11, 9);
        ais.put(12, 8);
        ais.put(13, 8);
        ais.put(15, 8);
        ais.put(17, 8);
        ais.put(20, 4);
        ais.put(21, -1);
        ais.put(22, -1);
        ais.put(23, -1);
        ais.put(240, -1);
        ais.put(241, -1);
        ais.put(250, -1);
        ais.put(251, -1);
        ais.put(252, -1);
        ais.put(30, -1);
        for (k = 3100; k < 3700; ++k) {
            ais.put(k, 10);
        }
        ais.put(37, -1);
        for (k = 3900; k < 3940; ++k) {
            ais.put(k, -1);
        }
        ais.put(400, -1);
        ais.put(401, -1);
        ais.put(402, 20);
        ais.put(403, -1);
        for (k = 410; k < 416; ++k) {
            ais.put(k, 16);
        }
        ais.put(420, -1);
        ais.put(421, -1);
        ais.put(422, 6);
        ais.put(423, -1);
        ais.put(424, 6);
        ais.put(425, 6);
        ais.put(426, 6);
        ais.put(7001, 17);
        ais.put(7002, -1);
        for (k = 7030; k < 7040; ++k) {
            ais.put(k, -1);
        }
        ais.put(8001, 18);
        ais.put(8002, -1);
        ais.put(8003, -1);
        ais.put(8004, -1);
        ais.put(8005, 10);
        ais.put(8006, 22);
        ais.put(8007, -1);
        ais.put(8008, -1);
        ais.put(8018, 22);
        ais.put(8020, -1);
        ais.put(8100, 10);
        ais.put(8101, 14);
        ais.put(8102, 6);
        for (k = 90; k < 100; ++k) {
            ais.put(k, -1);
        }
    }
}

