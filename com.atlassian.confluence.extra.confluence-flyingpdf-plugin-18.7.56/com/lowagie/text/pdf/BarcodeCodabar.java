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

public class BarcodeCodabar
extends Barcode {
    private static final byte[][] BARS = new byte[][]{{0, 0, 0, 0, 0, 1, 1}, {0, 0, 0, 0, 1, 1, 0}, {0, 0, 0, 1, 0, 0, 1}, {1, 1, 0, 0, 0, 0, 0}, {0, 0, 1, 0, 0, 1, 0}, {1, 0, 0, 0, 0, 1, 0}, {0, 1, 0, 0, 0, 0, 1}, {0, 1, 0, 0, 1, 0, 0}, {0, 1, 1, 0, 0, 0, 0}, {1, 0, 0, 1, 0, 0, 0}, {0, 0, 0, 1, 1, 0, 0}, {0, 0, 1, 1, 0, 0, 0}, {1, 0, 0, 0, 1, 0, 1}, {1, 0, 1, 0, 0, 0, 1}, {1, 0, 1, 0, 1, 0, 0}, {0, 0, 1, 0, 1, 0, 1}, {0, 0, 1, 1, 0, 1, 0}, {0, 1, 0, 1, 0, 0, 1}, {0, 0, 0, 1, 0, 1, 1}, {0, 0, 0, 1, 1, 1, 0}};
    private static final String CHARS = "0123456789-$:/.+ABCD";
    private static final int START_STOP_IDX = 16;

    public BarcodeCodabar() {
        try {
            this.x = 0.8f;
            this.n = 2.0f;
            this.font = BaseFont.createFont("Helvetica", "winansi", false);
            this.baseline = this.size = 8.0f;
            this.barHeight = this.size * 3.0f;
            this.textAlignment = 1;
            this.generateChecksum = false;
            this.checksumText = false;
            this.startStopText = false;
            this.codeType = 12;
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public static byte[] getBarsCodabar(String text) {
        int len = (text = text.toUpperCase()).length();
        if (len < 2) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("codabar.must.have.at.least.a.start.and.stop.character"));
        }
        if (CHARS.indexOf(text.charAt(0)) < 16 || CHARS.indexOf(text.charAt(len - 1)) < 16) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("codabar.must.have.one.of.abcd.as.start.stop.character"));
        }
        byte[] bars = new byte[text.length() * 8 - 1];
        for (int k = 0; k < len; ++k) {
            int idx = CHARS.indexOf(text.charAt(k));
            if (idx >= 16 && k > 0 && k < len - 1) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("in.codabar.start.stop.characters.are.only.allowed.at.the.extremes"));
            }
            if (idx < 0) {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.character.1.is.illegal.in.codabar", text.charAt(k)));
            }
            System.arraycopy(BARS[idx], 0, bars, k * 8, 7);
        }
        return bars;
    }

    public static String calculateChecksum(String code) {
        if (code.length() < 2) {
            return code;
        }
        String text = code.toUpperCase();
        int sum = 0;
        int len = text.length();
        for (int k = 0; k < len; ++k) {
            sum += CHARS.indexOf(text.charAt(k));
        }
        sum = (sum + 15) / 16 * 16 - sum;
        return code.substring(0, len - 1) + CHARS.charAt(sum) + code.substring(len - 1);
    }

    @Override
    public Rectangle getBarcodeSize() {
        float fontX = 0.0f;
        float fontY = 0.0f;
        String text = this.code;
        if (this.generateChecksum && this.checksumText) {
            text = BarcodeCodabar.calculateChecksum(this.code);
        }
        if (!this.startStopText) {
            text = text.substring(1, text.length() - 1);
        }
        if (this.font != null) {
            fontY = this.baseline > 0.0f ? this.baseline - this.font.getFontDescriptor(3, this.size) : -this.baseline + this.size;
            fontX = this.font.getWidthPoint(this.altText != null ? this.altText : text, this.size);
        }
        text = this.code;
        if (this.generateChecksum) {
            text = BarcodeCodabar.calculateChecksum(this.code);
        }
        byte[] bars = BarcodeCodabar.getBarsCodabar(text);
        int wide = 0;
        for (byte bar : bars) {
            wide += bar;
        }
        int narrow = bars.length - wide;
        float fullWidth = this.x * ((float)narrow + (float)wide * this.n);
        fullWidth = Math.max(fullWidth, fontX);
        float fullHeight = this.barHeight + fontY;
        return new Rectangle(fullWidth, fullHeight);
    }

    @Override
    public Rectangle placeBarcode(PdfContentByte cb, Color barColor, Color textColor) {
        String fullCode = this.code;
        if (this.generateChecksum && this.checksumText) {
            fullCode = BarcodeCodabar.calculateChecksum(this.code);
        }
        if (!this.startStopText) {
            fullCode = fullCode.substring(1, fullCode.length() - 1);
        }
        float fontX = 0.0f;
        if (this.font != null) {
            fullCode = this.altText != null ? this.altText : fullCode;
            fontX = this.font.getWidthPoint(fullCode, this.size);
        }
        byte[] bars = BarcodeCodabar.getBarsCodabar(this.generateChecksum ? BarcodeCodabar.calculateChecksum(this.code) : this.code);
        int wide = 0;
        for (byte bar1 : bars) {
            wide += bar1;
        }
        int narrow = bars.length - wide;
        float fullWidth = this.x * ((float)narrow + (float)wide * this.n);
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
        String fullCode = this.code;
        if (this.generateChecksum && this.checksumText) {
            fullCode = BarcodeCodabar.calculateChecksum(this.code);
        }
        if (!this.startStopText) {
            fullCode = fullCode.substring(1, fullCode.length() - 1);
        }
        byte[] bars = BarcodeCodabar.getBarsCodabar(this.generateChecksum ? BarcodeCodabar.calculateChecksum(this.code) : this.code);
        int wide = 0;
        for (byte bar1 : bars) {
            wide += bar1;
        }
        int narrow = bars.length - wide;
        int fullWidth = narrow + wide * (int)this.n;
        boolean print = true;
        int ptr = 0;
        int height = (int)this.barHeight;
        int[] pix = new int[fullWidth * height];
        for (byte bar : bars) {
            int w = bar == 0 ? 1 : (int)this.n;
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

