/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceNAttributes;
import org.apache.pdfbox.pdmodel.graphics.color.PDSeparation;
import org.apache.pdfbox.pdmodel.graphics.color.PDSpecialColorSpace;

public class PDDeviceN
extends PDSpecialColorSpace {
    private static final int COLORANT_NAMES = 1;
    private static final int ALTERNATE_CS = 2;
    private static final int TINT_TRANSFORM = 3;
    private static final int DEVICEN_ATTRIBUTES = 4;
    private PDColorSpace alternateColorSpace = null;
    private PDFunction tintTransform = null;
    private PDDeviceNAttributes attributes;
    private PDColor initialColor;
    private int numColorants;
    private int[] colorantToComponent;
    private PDColorSpace processColorSpace;
    private PDSeparation[] spotColorSpaces;

    public PDDeviceN() {
        this.array = new COSArray();
        this.array.add(COSName.DEVICEN);
        this.array.add(COSNull.NULL);
        this.array.add(COSNull.NULL);
        this.array.add(COSNull.NULL);
    }

    public PDDeviceN(COSArray deviceN) throws IOException {
        this.array = deviceN;
        this.alternateColorSpace = PDColorSpace.create(this.array.getObject(2));
        this.tintTransform = PDFunction.create(this.array.getObject(3));
        if (this.array.size() > 4) {
            this.attributes = new PDDeviceNAttributes((COSDictionary)this.array.getObject(4));
        }
        List<String> colorantNames = this.getColorantNames();
        this.initColorConversionCache(colorantNames);
        int n = colorantNames.size();
        float[] initial = new float[n];
        for (int i = 0; i < n; ++i) {
            initial[i] = 1.0f;
        }
        this.initialColor = new PDColor(initial, (PDColorSpace)this);
    }

    private void initColorConversionCache(List<String> colorantNames) throws IOException {
        int c;
        if (this.attributes == null) {
            return;
        }
        this.numColorants = colorantNames.size();
        this.colorantToComponent = new int[this.numColorants];
        if (this.attributes.getProcess() != null) {
            List<String> components = this.attributes.getProcess().getComponents();
            for (c = 0; c < this.numColorants; ++c) {
                this.colorantToComponent[c] = components.indexOf(colorantNames.get(c));
            }
            this.processColorSpace = this.attributes.getProcess().getColorSpace();
        } else {
            for (int c2 = 0; c2 < this.numColorants; ++c2) {
                this.colorantToComponent[c2] = -1;
            }
        }
        this.spotColorSpaces = new PDSeparation[this.numColorants];
        Map<String, PDSeparation> spotColorants = this.attributes.getColorants();
        for (c = 0; c < this.numColorants; ++c) {
            String name = colorantNames.get(c);
            PDSeparation spot = spotColorants.get(name);
            if (spot != null) {
                this.spotColorSpaces[c] = spot;
                if (this.isNChannel()) continue;
                this.colorantToComponent[c] = -1;
                continue;
            }
            this.spotColorSpaces[c] = null;
        }
    }

    @Override
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        if (this.attributes != null) {
            return this.toRGBWithAttributes(raster);
        }
        return this.toRGBWithTintTransform(raster);
    }

    private BufferedImage toRGBWithAttributes(WritableRaster raster) throws IOException {
        int width = raster.getWidth();
        int height = raster.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, 1);
        WritableRaster rgbRaster = rgbImage.getRaster();
        Graphics2D g = rgbImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);
        g.dispose();
        for (int c = 0; c < this.numColorants; ++c) {
            PDColorSpace componentColorSpace;
            if (this.colorantToComponent[c] >= 0) {
                componentColorSpace = this.processColorSpace;
            } else {
                if (this.spotColorSpaces[c] == null) {
                    return this.toRGBWithTintTransform(raster);
                }
                componentColorSpace = this.spotColorSpaces[c];
            }
            int numberOfComponents = componentColorSpace.getNumberOfComponents();
            WritableRaster componentRaster = Raster.createBandedRaster(0, width, height, numberOfComponents, new Point(0, 0));
            int[] samples = new int[this.numColorants];
            int[] componentSamples = new int[numberOfComponents];
            boolean isProcessColorant = this.colorantToComponent[c] >= 0;
            int componentIndex = this.colorantToComponent[c];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    raster.getPixel(x, y, samples);
                    if (isProcessColorant) {
                        componentSamples[componentIndex] = samples[c];
                    } else {
                        componentSamples[0] = samples[c];
                    }
                    componentRaster.setPixel(x, y, componentSamples);
                }
            }
            BufferedImage rgbComponentImage = componentColorSpace.toRGBImage(componentRaster);
            WritableRaster rgbComponentRaster = rgbComponentImage.getRaster();
            int[] rgbChannel = new int[3];
            int[] rgbComposite = new int[3];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    rgbComponentRaster.getPixel(x, y, rgbChannel);
                    rgbRaster.getPixel(x, y, rgbComposite);
                    rgbChannel[0] = rgbChannel[0] * rgbComposite[0] >> 8;
                    rgbChannel[1] = rgbChannel[1] * rgbComposite[1] >> 8;
                    rgbChannel[2] = rgbChannel[2] * rgbComposite[2] >> 8;
                    rgbRaster.setPixel(x, y, rgbChannel);
                }
            }
        }
        return rgbImage;
    }

    private BufferedImage toRGBWithTintTransform(WritableRaster raster) throws IOException {
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        String key = null;
        StringBuilder keyBuilder = new StringBuilder();
        int width = raster.getWidth();
        int height = raster.getHeight();
        BufferedImage rgbImage = new BufferedImage(width, height, 1);
        WritableRaster rgbRaster = rgbImage.getRaster();
        int[] rgb = new int[3];
        int numSrcComponents = this.getColorantNames().size();
        float[] src = new float[numSrcComponents];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                raster.getPixel(x, y, src);
                keyBuilder.append(src[0]);
                for (int s = 1; s < numSrcComponents; ++s) {
                    keyBuilder.append('#').append(src[s]);
                }
                key = keyBuilder.toString();
                keyBuilder.setLength(0);
                int[] pxl = (int[])map1.get(key);
                if (pxl != null) {
                    rgbRaster.setPixel(x, y, pxl);
                    continue;
                }
                for (int s = 0; s < numSrcComponents; ++s) {
                    src[s] = src[s] / 255.0f;
                }
                float[] result = this.tintTransform.eval(src);
                float[] rgbFloat = this.alternateColorSpace.toRGB(result);
                rgb[0] = (int)(rgbFloat[0] * 255.0f);
                rgb[1] = (int)(rgbFloat[1] * 255.0f);
                rgb[2] = (int)(rgbFloat[2] * 255.0f);
                map1.put(key, rgb.clone());
                rgbRaster.setPixel(x, y, rgb);
            }
        }
        return rgbImage;
    }

    @Override
    public float[] toRGB(float[] value) throws IOException {
        if (this.attributes != null) {
            return this.toRGBWithAttributes(value);
        }
        return this.toRGBWithTintTransform(value);
    }

    private float[] toRGBWithAttributes(float[] value) throws IOException {
        float[] rgbValue = new float[]{1.0f, 1.0f, 1.0f};
        for (int c = 0; c < this.numColorants; ++c) {
            PDColorSpace componentColorSpace;
            boolean isProcessColorant;
            boolean bl = isProcessColorant = this.colorantToComponent[c] >= 0;
            if (isProcessColorant) {
                componentColorSpace = this.processColorSpace;
            } else {
                if (this.spotColorSpaces[c] == null) {
                    return this.toRGBWithTintTransform(value);
                }
                componentColorSpace = this.spotColorSpaces[c];
            }
            float[] componentSamples = new float[componentColorSpace.getNumberOfComponents()];
            if (isProcessColorant) {
                int componentIndex = this.colorantToComponent[c];
                componentSamples[componentIndex] = value[c];
            } else {
                componentSamples[0] = value[c];
            }
            float[] rgbComponent = componentColorSpace.toRGB(componentSamples);
            rgbValue[0] = rgbValue[0] * rgbComponent[0];
            rgbValue[1] = rgbValue[1] * rgbComponent[1];
            rgbValue[2] = rgbValue[2] * rgbComponent[2];
        }
        return rgbValue;
    }

    private float[] toRGBWithTintTransform(float[] value) throws IOException {
        float[] altValue = this.tintTransform.eval(value);
        return this.alternateColorSpace.toRGB(altValue);
    }

    @Override
    public BufferedImage toRawImage(WritableRaster raster) {
        return null;
    }

    public boolean isNChannel() {
        return this.attributes != null && this.attributes.isNChannel();
    }

    @Override
    public String getName() {
        return COSName.DEVICEN.getName();
    }

    @Override
    public final int getNumberOfComponents() {
        return this.getColorantNames().size();
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        int n = this.getNumberOfComponents();
        float[] decode = new float[n * 2];
        for (int i = 0; i < n; ++i) {
            decode[i * 2 + 1] = 1.0f;
        }
        return decode;
    }

    @Override
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    public final List<String> getColorantNames() {
        COSArray names = (COSArray)this.array.getObject(1);
        return COSArrayList.convertCOSNameCOSArrayToList(names);
    }

    public PDDeviceNAttributes getAttributes() {
        return this.attributes;
    }

    public void setColorantNames(List<String> names) {
        COSArray namesArray = COSArrayList.convertStringListToCOSNameCOSArray(names);
        this.array.set(1, namesArray);
    }

    public void setAttributes(PDDeviceNAttributes attributes) {
        this.attributes = attributes;
        if (attributes == null) {
            this.array.remove(4);
        } else {
            while (this.array.size() <= 4) {
                this.array.add(COSNull.NULL);
            }
            this.array.set(4, attributes.getCOSDictionary());
        }
    }

    public PDColorSpace getAlternateColorSpace() throws IOException {
        if (this.alternateColorSpace == null) {
            this.alternateColorSpace = PDColorSpace.create(this.array.getObject(2));
        }
        return this.alternateColorSpace;
    }

    public void setAlternateColorSpace(PDColorSpace cs) {
        this.alternateColorSpace = cs;
        COSBase space = null;
        if (cs != null) {
            space = cs.getCOSObject();
        }
        this.array.set(2, space);
    }

    public PDFunction getTintTransform() throws IOException {
        if (this.tintTransform == null) {
            this.tintTransform = PDFunction.create(this.array.getObject(3));
        }
        return this.tintTransform;
    }

    public void setTintTransform(PDFunction tint) {
        this.tintTransform = tint;
        this.array.set(3, tint);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getName());
        sb.append('{');
        for (String col : this.getColorantNames()) {
            sb.append('\"');
            sb.append(col);
            sb.append("\" ");
        }
        sb.append(this.alternateColorSpace.getName());
        sb.append(' ');
        sb.append(this.tintTransform);
        sb.append(' ');
        if (this.attributes != null) {
            sb.append(this.attributes);
        }
        sb.append('}');
        return sb.toString();
    }
}

