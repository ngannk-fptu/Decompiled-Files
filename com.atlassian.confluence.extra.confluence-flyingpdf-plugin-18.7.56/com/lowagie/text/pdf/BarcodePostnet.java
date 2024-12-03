/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

public class BarcodePostnet
extends Barcode {
    private static final byte[][] BARS = new byte[][]{{1, 1, 0, 0, 0}, {0, 0, 0, 1, 1}, {0, 0, 1, 0, 1}, {0, 0, 1, 1, 0}, {0, 1, 0, 0, 1}, {0, 1, 0, 1, 0}, {0, 1, 1, 0, 0}, {1, 0, 0, 0, 1}, {1, 0, 0, 1, 0}, {1, 0, 1, 0, 0}};

    public BarcodePostnet() {
        this.n = 3.2727273f;
        this.x = 1.4399999f;
        this.barHeight = 9.0f;
        this.size = 3.6000001f;
        this.codeType = 7;
    }

    public static byte[] getBarsPostnet(String text) {
        int total = 0;
        for (int k = text.length() - 1; k >= 0; --k) {
            int n = text.charAt(k) - 48;
            total += n;
        }
        text = text + (char)((10 - total % 10) % 10 + 48);
        byte[] bars = new byte[text.length() * 5 + 2];
        bars[0] = 1;
        bars[bars.length - 1] = 1;
        for (int k = 0; k < text.length(); ++k) {
            int c = text.charAt(k) - 48;
            System.arraycopy(BARS[c], 0, bars, k * 5 + 1, 5);
        }
        return bars;
    }

    @Override
    public Rectangle getBarcodeSize() {
        float width = (float)((this.code.length() + 1) * 5 + 1) * this.n + this.x;
        return new Rectangle(width, this.barHeight);
    }

    @Override
    public Rectangle placeBarcode(PdfContentByte cb, Color barColor, Color textColor) {
        if (barColor != null) {
            cb.setColorFill(barColor);
        }
        byte[] bars = BarcodePostnet.getBarsPostnet(this.code);
        byte flip = 1;
        if (this.codeType == 8) {
            flip = 0;
            bars[0] = 0;
            bars[bars.length - 1] = 0;
        }
        float startX = 0.0f;
        for (byte bar : bars) {
            cb.rectangle(startX, 0.0f, this.x - this.inkSpreading, bar == flip ? this.barHeight : this.size);
            startX += this.n;
        }
        cb.fill();
        return this.getBarcodeSize();
    }

    @Override
    public Image createAwtImage(Color foreground, Color background) {
        int k;
        int barTall;
        int barShort;
        int barDistance;
        int f = foreground.getRGB();
        int g = background.getRGB();
        Canvas canvas = new Canvas();
        int barWidth = (int)this.x;
        if (barWidth <= 0) {
            barWidth = 1;
        }
        if ((barDistance = (int)this.n) <= barWidth) {
            barDistance = barWidth + 1;
        }
        if ((barShort = (int)this.size) <= 0) {
            barShort = 1;
        }
        if ((barTall = (int)this.barHeight) <= barShort) {
            barTall = barShort + 1;
        }
        int width = ((this.code.length() + 1) * 5 + 1) * barDistance + barWidth;
        int[] pix = new int[width * barTall];
        byte[] bars = BarcodePostnet.getBarsPostnet(this.code);
        byte flip = 1;
        if (this.codeType == 8) {
            flip = 0;
            bars[0] = 0;
            bars[bars.length - 1] = 0;
        }
        int idx = 0;
        for (byte bar : bars) {
            boolean dot = bar == flip;
            for (int j = 0; j < barDistance; ++j) {
                pix[idx + j] = dot && j < barWidth ? f : g;
            }
            idx += barDistance;
        }
        int limit = width * (barTall - barShort);
        for (k = width; k < limit; k += width) {
            System.arraycopy(pix, 0, pix, k, width);
        }
        idx = limit;
        for (k = 0; k < bars.length; ++k) {
            for (int j = 0; j < barDistance; ++j) {
                pix[idx + j] = j < barWidth ? f : g;
            }
            idx += barDistance;
        }
        for (k = limit + width; k < pix.length; k += width) {
            System.arraycopy(pix, limit, pix, k, width);
        }
        Image img = canvas.createImage(new MemoryImageSource(width, barTall, pix, 0, width));
        return img;
    }
}

