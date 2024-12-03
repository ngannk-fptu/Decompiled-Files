/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

public class BarcodeInter25
extends Barcode {
    private static final byte[][] BARS = new byte[][]{{0, 0, 1, 1, 0}, {1, 0, 0, 0, 1}, {0, 1, 0, 0, 1}, {1, 1, 0, 0, 0}, {0, 0, 1, 0, 1}, {1, 0, 1, 0, 0}, {0, 1, 1, 0, 0}, {0, 0, 0, 1, 1}, {1, 0, 0, 1, 0}, {0, 1, 0, 1, 0}};

    public BarcodeInter25() {
        try {
            this.x = 0.8f;
            this.n = 2.0f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.baseline = this.size = 8.0f;
            this.barHeight = this.size * 3.0f;
            this.textAlignment = 1;
            this.generateChecksum = false;
            this.checksumText = false;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public static String keepNumbers(String text) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < text.length(); ++k) {
            char c = text.charAt(k);
            if (c < '0' || c > '9') continue;
            sb.append(c);
        }
        return sb.toString();
    }

    public static char getChecksum(String text) {
        int mul = 3;
        int total = 0;
        for (int k = text.length() - 1; k >= 0; --k) {
            int n = text.charAt(k) - 48;
            total += mul * n;
            mul ^= 2;
        }
        return (char)((10 - total % 10) % 10 + 48);
    }

    public static byte[] getBarsInter25(String text) {
        if (((text = BarcodeInter25.keepNumbers(text)).length() & 1) != 0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.text.length.must.be.even"));
        }
        byte[] bars = new byte[text.length() * 5 + 7];
        int pb = 0;
        bars[pb++] = 0;
        bars[pb++] = 0;
        bars[pb++] = 0;
        bars[pb++] = 0;
        int len = text.length() / 2;
        for (int k = 0; k < len; ++k) {
            int c1 = text.charAt(k * 2) - 48;
            int c2 = text.charAt(k * 2 + 1) - 48;
            byte[] b1 = BARS[c1];
            byte[] b2 = BARS[c2];
            for (int j = 0; j < 5; ++j) {
                bars[pb++] = b1[j];
                bars[pb++] = b2[j];
            }
        }
        bars[pb++] = 1;
        bars[pb++] = 0;
        bars[pb++] = 0;
        return bars;
    }

    @Override
    public Rectangle getBarcodeSize() {
        String fullCode;
        float fontX = 0.0f;
        float fontY = 0.0f;
        if (this.font != null) {
            fontY = this.baseline > 0.0f ? this.baseline - this.font.getFontDescriptor(3, this.size) : -this.baseline + this.size;
            fullCode = this.code;
            if (this.generateChecksum && this.checksumText) {
                fullCode = fullCode + BarcodeInter25.getChecksum(fullCode);
            }
            fontX = this.font.getWidthPoint(this.altText != null ? this.altText : fullCode, this.size);
        }
        fullCode = BarcodeInter25.keepNumbers(this.code);
        int len = fullCode.length();
        if (this.generateChecksum) {
            ++len;
        }
        float fullWidth = (float)len * (3.0f * this.x + 2.0f * this.x * this.n) + (6.0f + this.n) * this.x;
        fullWidth = Math.max(fullWidth, fontX);
        float fullHeight = this.barHeight + fontY;
        return new Rectangle(fullWidth, fullHeight);
    }

    @Override
    public Rectangle placeBarcode(PdfContentByte cb, Color barColor, Color textColor) {
        String fullCode = this.code;
        float fontX = 0.0f;
        if (this.font != null) {
            if (this.generateChecksum && this.checksumText) {
                fullCode = fullCode + BarcodeInter25.getChecksum(fullCode);
            }
            fullCode = this.altText != null ? this.altText : fullCode;
            fontX = this.font.getWidthPoint(fullCode, this.size);
        }
        String bCode = BarcodeInter25.keepNumbers(this.code);
        if (this.generateChecksum) {
            bCode = bCode + BarcodeInter25.getChecksum(bCode);
        }
        int len = bCode.length();
        float fullWidth = (float)len * (3.0f * this.x + 2.0f * this.x * this.n) + (6.0f + this.n) * this.x;
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
        byte[] bars = BarcodeInter25.getBarsInter25(bCode);
        boolean print = true;
        if (barColor != null) {
            cb.setColorFill(barColor);
        }
        for (byte bar : bars) {
            float w;
            float f = w = bar == 0 ? this.x : this.x * this.n;
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
        int f = foreground.getRGB();
        int g = background.getRGB();
        Canvas canvas = new Canvas();
        String bCode = BarcodeInter25.keepNumbers(this.code);
        if (this.generateChecksum) {
            bCode = bCode + BarcodeInter25.getChecksum(bCode);
        }
        int len = bCode.length();
        int nn = (int)this.n;
        int fullWidth = len * (3 + 2 * nn) + (6 + nn);
        byte[] bars = BarcodeInter25.getBarsInter25(bCode);
        boolean print = true;
        int ptr = 0;
        int height = (int)this.barHeight;
        int[] pix = new int[fullWidth * height];
        for (byte bar : bars) {
            int w = bar == 0 ? 1 : nn;
            int c = g;
            if (print) {
                c = f;
            }
            print = !print;
            for (int j = 0; j < w; ++j) {
                pix[ptr++] = c;
            }
        }
        for (int k = fullWidth; k < pix.length; k += fullWidth) {
            System.arraycopy(pix, 0, pix, k, fullWidth);
        }
        Image img = canvas.createImage(new MemoryImageSource(fullWidth, height, pix, 0, fullWidth));
        return img;
    }
}

