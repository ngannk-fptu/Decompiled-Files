/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ComponentColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.MissingResourceException;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDCalGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDCalRGB;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceN;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.apache.pdfbox.pdmodel.graphics.color.PDIndexed;
import org.apache.pdfbox.pdmodel.graphics.color.PDLab;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDSeparation;

public abstract class PDColorSpace
implements COSObjectable {
    private final ColorConvertOp colorConvertOp = new ColorConvertOp(null);
    protected COSArray array;

    public static PDColorSpace create(COSBase colorSpace) throws IOException {
        return PDColorSpace.create(colorSpace, null);
    }

    public static PDColorSpace create(COSBase colorSpace, PDResources resources) throws IOException {
        return PDColorSpace.create(colorSpace, resources, false);
    }

    public static PDColorSpace create(COSBase colorSpace, PDResources resources, boolean wasDefault) throws IOException {
        if (colorSpace instanceof COSObject) {
            return PDColorSpace.createFromCOSObject((COSObject)colorSpace, resources);
        }
        if (colorSpace instanceof COSName) {
            COSName name = (COSName)colorSpace;
            if (resources != null) {
                COSName defaultName = null;
                if (name.equals(COSName.DEVICECMYK) && resources.hasColorSpace(COSName.DEFAULT_CMYK)) {
                    defaultName = COSName.DEFAULT_CMYK;
                } else if (name.equals(COSName.DEVICERGB) && resources.hasColorSpace(COSName.DEFAULT_RGB)) {
                    defaultName = COSName.DEFAULT_RGB;
                } else if (name.equals(COSName.DEVICEGRAY) && resources.hasColorSpace(COSName.DEFAULT_GRAY)) {
                    defaultName = COSName.DEFAULT_GRAY;
                }
                if (resources.hasColorSpace(defaultName) && !wasDefault) {
                    return resources.getColorSpace(defaultName, true);
                }
            }
            if (name == COSName.DEVICECMYK) {
                return PDDeviceCMYK.INSTANCE;
            }
            if (name == COSName.DEVICERGB) {
                return PDDeviceRGB.INSTANCE;
            }
            if (name == COSName.DEVICEGRAY) {
                return PDDeviceGray.INSTANCE;
            }
            if (name == COSName.PATTERN) {
                return new PDPattern(resources);
            }
            if (resources != null) {
                if (!resources.hasColorSpace(name)) {
                    throw new MissingResourceException("Missing color space: " + name.getName());
                }
                return resources.getColorSpace(name);
            }
            throw new MissingResourceException("Unknown color space: " + name.getName());
        }
        if (colorSpace instanceof COSArray) {
            COSArray array = (COSArray)colorSpace;
            if (array.size() == 0) {
                throw new IOException("Colorspace array is empty");
            }
            COSBase base = array.getObject(0);
            if (!(base instanceof COSName)) {
                throw new IOException("First element in colorspace array must be a name");
            }
            COSName name = (COSName)base;
            if (name == COSName.CALGRAY) {
                return new PDCalGray(array);
            }
            if (name == COSName.CALRGB) {
                return new PDCalRGB(array);
            }
            if (name == COSName.DEVICEN) {
                return new PDDeviceN(array);
            }
            if (name == COSName.INDEXED) {
                return new PDIndexed(array, resources);
            }
            if (name == COSName.SEPARATION) {
                return new PDSeparation(array);
            }
            if (name == COSName.ICCBASED) {
                return PDICCBased.create(array, resources);
            }
            if (name == COSName.LAB) {
                return new PDLab(array);
            }
            if (name == COSName.PATTERN) {
                if (array.size() == 1) {
                    return new PDPattern(resources);
                }
                return new PDPattern(resources, PDColorSpace.create(array.get(1)));
            }
            if (name == COSName.DEVICECMYK || name == COSName.DEVICERGB || name == COSName.DEVICEGRAY) {
                return PDColorSpace.create(name, resources, wasDefault);
            }
            throw new IOException("Invalid color space kind: " + name);
        }
        if (colorSpace instanceof COSDictionary && ((COSDictionary)colorSpace).containsKey(COSName.COLORSPACE)) {
            COSBase base = ((COSDictionary)colorSpace).getDictionaryObject(COSName.COLORSPACE);
            if (base == colorSpace) {
                throw new IOException("Recursion in colorspace: " + ((COSDictionary)colorSpace).getItem(COSName.COLORSPACE) + " points to itself");
            }
            return PDColorSpace.create(base, resources, wasDefault);
        }
        throw new IOException("Expected a name or array but got: " + colorSpace);
    }

    private static PDColorSpace createFromCOSObject(COSObject colorSpace, PDResources resources) throws IOException {
        ResourceCache resourceCache;
        PDColorSpace cs;
        if (resources != null && resources.getResourceCache() != null && (cs = (resourceCache = resources.getResourceCache()).getColorSpace(colorSpace)) != null) {
            return cs;
        }
        cs = PDColorSpace.create(colorSpace.getObject(), resources);
        if (resources != null && resources.getResourceCache() != null && cs != null) {
            resourceCache = resources.getResourceCache();
            resourceCache.put(colorSpace, cs);
        }
        return cs;
    }

    public abstract String getName();

    public abstract int getNumberOfComponents();

    public abstract float[] getDefaultDecode(int var1);

    public abstract PDColor getInitialColor();

    public abstract float[] toRGB(float[] var1) throws IOException;

    public abstract BufferedImage toRGBImage(WritableRaster var1) throws IOException;

    public abstract BufferedImage toRawImage(WritableRaster var1) throws IOException;

    protected final BufferedImage toRawImage(WritableRaster raster, ColorSpace awtColorSpace) {
        ComponentColorModel colorModel = new ComponentColorModel(awtColorSpace, false, false, 1, raster.getDataBuffer().getDataType());
        return new BufferedImage(colorModel, raster, false, null);
    }

    protected BufferedImage toRGBImageAWT(WritableRaster raster, ColorSpace colorSpace) {
        ComponentColorModel colorModel = new ComponentColorModel(colorSpace, false, false, 1, raster.getDataBuffer().getDataType());
        BufferedImage src = new BufferedImage(colorModel, raster, false, null);
        BufferedImage dest = new BufferedImage(raster.getWidth(), raster.getHeight(), 1);
        if (src.getWidth() == 1 || src.getHeight() == 1) {
            Graphics g2d = dest.getGraphics();
            g2d.drawImage(src, 0, 0, null);
            g2d.dispose();
            return dest;
        }
        this.colorConvertOp.filter(src, dest);
        return dest;
    }

    @Override
    public COSBase getCOSObject() {
        return this.array;
    }
}

