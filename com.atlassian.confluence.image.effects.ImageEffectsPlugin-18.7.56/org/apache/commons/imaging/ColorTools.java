/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

public class ColorTools {
    public BufferedImage correctImage(BufferedImage src, File file) throws ImageReadException, IOException {
        ICC_Profile icc = Imaging.getICCProfile(file);
        if (icc == null) {
            return src;
        }
        ICC_ColorSpace cs = new ICC_ColorSpace(icc);
        return this.convertFromColorSpace(src, cs);
    }

    public BufferedImage relabelColorSpace(BufferedImage bi, ICC_Profile profile) throws ImagingOpException {
        ICC_ColorSpace cs = new ICC_ColorSpace(profile);
        return this.relabelColorSpace(bi, cs);
    }

    public BufferedImage relabelColorSpace(BufferedImage bi, ColorSpace cs) throws ImagingOpException {
        ColorModel cm = this.deriveColorModel(bi, cs);
        return this.relabelColorSpace(bi, cm);
    }

    public BufferedImage relabelColorSpace(BufferedImage bi, ColorModel cm) throws ImagingOpException {
        return new BufferedImage(cm, bi.getRaster(), false, null);
    }

    public ColorModel deriveColorModel(BufferedImage bi, ColorSpace cs) throws ImagingOpException {
        return this.deriveColorModel(bi, cs, false);
    }

    public ColorModel deriveColorModel(BufferedImage bi, ColorSpace cs, boolean forceNoAlpha) throws ImagingOpException {
        return this.deriveColorModel(bi.getColorModel(), cs, forceNoAlpha);
    }

    public ColorModel deriveColorModel(ColorModel colorModel, ColorSpace cs, boolean forceNoAlpha) throws ImagingOpException {
        if (colorModel instanceof ComponentColorModel) {
            ComponentColorModel ccm = (ComponentColorModel)colorModel;
            if (forceNoAlpha) {
                return new ComponentColorModel(cs, false, false, 1, ccm.getTransferType());
            }
            return new ComponentColorModel(cs, ccm.hasAlpha(), ccm.isAlphaPremultiplied(), ccm.getTransparency(), ccm.getTransferType());
        }
        if (colorModel instanceof DirectColorModel) {
            DirectColorModel dcm = (DirectColorModel)colorModel;
            int oldMask = dcm.getRedMask() | dcm.getGreenMask() | dcm.getBlueMask() | dcm.getAlphaMask();
            int oldBits = this.countBitsInMask(oldMask);
            return new DirectColorModel(cs, oldBits, dcm.getRedMask(), dcm.getGreenMask(), dcm.getBlueMask(), dcm.getAlphaMask(), dcm.isAlphaPremultiplied(), dcm.getTransferType());
        }
        throw new ImagingOpException("Could not clone unknown ColorModel Type.");
    }

    private int countBitsInMask(int i) {
        int count = 0;
        while (i != 0) {
            count += i & 1;
            i >>>= 1;
        }
        return count;
    }

    public BufferedImage convertToColorSpace(BufferedImage bi, ColorSpace to) {
        ColorSpace from = bi.getColorModel().getColorSpace();
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        ColorConvertOp op = new ColorConvertOp(from, to, hints);
        BufferedImage result = op.filter(bi, null);
        result = this.relabelColorSpace(result, to);
        return result;
    }

    public BufferedImage convertTosRGB(BufferedImage bi) {
        ColorModel srgbCM = ColorModel.getRGBdefault();
        return this.convertToColorSpace(bi, srgbCM.getColorSpace());
    }

    protected BufferedImage convertFromColorSpace(BufferedImage bi, ColorSpace from) {
        ColorModel srgbCM = ColorModel.getRGBdefault();
        return this.convertBetweenColorSpaces(bi, from, srgbCM.getColorSpace());
    }

    public BufferedImage convertBetweenICCProfiles(BufferedImage bi, ICC_Profile from, ICC_Profile to) {
        ICC_ColorSpace csFrom = new ICC_ColorSpace(from);
        ICC_ColorSpace csTo = new ICC_ColorSpace(to);
        return this.convertBetweenColorSpaces(bi, csFrom, csTo);
    }

    public BufferedImage convertToICCProfile(BufferedImage bi, ICC_Profile to) {
        ICC_ColorSpace csTo = new ICC_ColorSpace(to);
        return this.convertToColorSpace(bi, csTo);
    }

    public BufferedImage convertBetweenColorSpacesX2(BufferedImage bi, ColorSpace from, ColorSpace to) {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        bi = this.relabelColorSpace(bi, from);
        ColorConvertOp op = new ColorConvertOp(from, to, hints);
        bi = op.filter(bi, null);
        bi = this.relabelColorSpace(bi, from);
        bi = op.filter(bi, null);
        bi = this.relabelColorSpace(bi, to);
        return bi;
    }

    public BufferedImage convertBetweenColorSpaces(BufferedImage bi, ColorSpace from, ColorSpace to) {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        ColorConvertOp op = new ColorConvertOp(from, to, hints);
        bi = this.relabelColorSpace(bi, from);
        BufferedImage result = op.filter(bi, null);
        result = this.relabelColorSpace(result, to);
        return result;
    }
}

