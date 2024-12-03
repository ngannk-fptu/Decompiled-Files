/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.palette;

import java.awt.image.BufferedImage;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.palette.Palette;

public final class Dithering {
    private Dithering() {
    }

    public static void applyFloydSteinbergDithering(BufferedImage image, Palette palette) throws ImageWriteException {
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                int update;
                int argb = image.getRGB(x, y);
                int index = palette.getPaletteIndex(argb);
                int nextArgb = palette.getEntry(index);
                image.setRGB(x, y, nextArgb);
                int a = argb >> 24 & 0xFF;
                int r = argb >> 16 & 0xFF;
                int g = argb >> 8 & 0xFF;
                int b = argb & 0xFF;
                int na = nextArgb >> 24 & 0xFF;
                int nr = nextArgb >> 16 & 0xFF;
                int ng = nextArgb >> 8 & 0xFF;
                int nb = nextArgb & 0xFF;
                int errA = a - na;
                int errR = r - nr;
                int errG = g - ng;
                int errB = b - nb;
                if (x + 1 < image.getWidth()) {
                    update = Dithering.adjustPixel(image.getRGB(x + 1, y), errA, errR, errG, errB, 7);
                    image.setRGB(x + 1, y, update);
                    if (y + 1 < image.getHeight()) {
                        update = Dithering.adjustPixel(image.getRGB(x + 1, y + 1), errA, errR, errG, errB, 1);
                        image.setRGB(x + 1, y + 1, update);
                    }
                }
                if (y + 1 >= image.getHeight()) continue;
                update = Dithering.adjustPixel(image.getRGB(x, y + 1), errA, errR, errG, errB, 5);
                image.setRGB(x, y + 1, update);
                if (x - 1 < 0) continue;
                update = Dithering.adjustPixel(image.getRGB(x - 1, y + 1), errA, errR, errG, errB, 3);
                image.setRGB(x - 1, y + 1, update);
            }
        }
    }

    private static int adjustPixel(int argb, int errA, int errR, int errG, int errB, int mul) {
        int a = argb >> 24 & 0xFF;
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        r += errR * mul / 16;
        g += errG * mul / 16;
        b += errB * mul / 16;
        if ((a += errA * mul / 16) < 0) {
            a = 0;
        } else if (a > 255) {
            a = 255;
        }
        if (r < 0) {
            r = 0;
        } else if (r > 255) {
            r = 255;
        }
        if (g < 0) {
            g = 0;
        } else if (g > 255) {
            g = 255;
        }
        if (b < 0) {
            b = 0;
        } else if (b > 255) {
            b = 255;
        }
        return a << 24 | r << 16 | g << 8 | b;
    }
}

