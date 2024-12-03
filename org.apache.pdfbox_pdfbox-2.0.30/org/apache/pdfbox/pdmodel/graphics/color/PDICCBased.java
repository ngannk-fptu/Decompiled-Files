/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ProfileDataException;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDCIEBasedColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.util.Charsets;

public final class PDICCBased
extends PDCIEBasedColorSpace {
    private static final Log LOG = LogFactory.getLog(PDICCBased.class);
    private final PDStream stream;
    private int numberOfComponents = -1;
    private ICC_Profile iccProfile;
    private PDColorSpace alternateColorSpace;
    private ICC_ColorSpace awtColorSpace;
    private PDColor initialColor;
    private boolean isRGB = false;
    private boolean useOnlyAlternateColorSpace = false;
    private static final boolean IS_KCMS;

    public PDICCBased(PDDocument doc) {
        this.array = new COSArray();
        this.array.add(COSName.ICCBASED);
        this.stream = new PDStream(doc);
        this.array.add(this.stream);
    }

    @Deprecated
    public PDICCBased(COSArray iccArray) throws IOException {
        PDICCBased.checkArray(iccArray);
        this.useOnlyAlternateColorSpace = System.getProperty("org.apache.pdfbox.rendering.UseAlternateInsteadOfICCColorSpace") != null;
        this.array = iccArray;
        this.stream = new PDStream((COSStream)iccArray.getObject(1));
        this.loadICCProfile();
    }

    public static PDICCBased create(COSArray iccArray, PDResources resources) throws IOException {
        ResourceCache resourceCache;
        PDICCBased.checkArray(iccArray);
        COSBase base = iccArray.get(1);
        if (base instanceof COSObject && resources != null && (resourceCache = resources.getResourceCache()) != null) {
            COSObject indirect = (COSObject)base;
            PDColorSpace space = resourceCache.getColorSpace(indirect);
            if (space instanceof PDICCBased) {
                return (PDICCBased)space;
            }
            PDICCBased newSpace = new PDICCBased(iccArray);
            resourceCache.put(indirect, newSpace);
            return newSpace;
        }
        return new PDICCBased(iccArray);
    }

    private static void checkArray(COSArray iccArray) throws IOException {
        if (iccArray.size() < 2) {
            throw new IOException("ICCBased colorspace array must have two elements");
        }
        if (!(iccArray.getObject(1) instanceof COSStream)) {
            throw new IOException("ICCBased colorspace array must have a stream as second element");
        }
    }

    @Override
    public String getName() {
        return COSName.ICCBASED.getName();
    }

    public PDStream getPDStream() {
        return this.stream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadICCProfile() throws IOException {
        if (this.useOnlyAlternateColorSpace) {
            try {
                this.fallbackToAlternateColorSpace(null);
                return;
            }
            catch (IOException e) {
                LOG.warn((Object)("Error initializing alternate color space: " + e.getLocalizedMessage()));
            }
        }
        COSInputStream input = null;
        try {
            input = this.stream.createInputStream();
            Log log = LOG;
            synchronized (log) {
                ICC_Profile profile = ICC_Profile.getInstance(input);
                if (this.is_sRGB(profile)) {
                    this.isRGB = true;
                    this.awtColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1000);
                    this.iccProfile = this.awtColorSpace.getProfile();
                } else {
                    profile = PDICCBased.ensureDisplayProfile(profile);
                    this.awtColorSpace = new ICC_ColorSpace(profile);
                    this.iccProfile = profile;
                }
                int numOfComponents = this.getNumberOfComponents();
                float[] initial = new float[numOfComponents];
                for (int c = 0; c < initial.length; ++c) {
                    initial[c] = Math.max(0.0f, this.getRangeForComponent(c).getMin());
                }
                this.initialColor = new PDColor(initial, (PDColorSpace)this);
                this.awtColorSpace.toRGB(new float[numOfComponents]);
                if (!IS_KCMS) {
                    new ComponentColorModel(this.awtColorSpace, false, false, 1, 0);
                }
            }
        }
        catch (ProfileDataException e) {
            this.fallbackToAlternateColorSpace(e);
        }
        catch (CMMException e) {
            this.fallbackToAlternateColorSpace(e);
        }
        catch (IllegalArgumentException e) {
            this.fallbackToAlternateColorSpace(e);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            this.fallbackToAlternateColorSpace(e);
        }
        catch (IOException e) {
            this.fallbackToAlternateColorSpace(e);
        }
        finally {
            IOUtils.closeQuietly(input);
        }
    }

    private void fallbackToAlternateColorSpace(Exception e) throws IOException {
        this.awtColorSpace = null;
        this.alternateColorSpace = this.getAlternateColorSpace();
        if (this.alternateColorSpace.equals(PDDeviceRGB.INSTANCE)) {
            this.isRGB = true;
        }
        if (e != null) {
            LOG.warn((Object)("Can't read embedded ICC profile (" + e.getLocalizedMessage() + "), using alternate color space: " + this.alternateColorSpace.getName()));
        }
        this.initialColor = this.alternateColorSpace.getInitialColor();
    }

    private boolean is_sRGB(ICC_Profile profile) {
        byte[] bytes = Arrays.copyOfRange(profile.getData(1751474532), 52, 59);
        String deviceModel = new String(bytes, Charsets.US_ASCII).trim();
        return deviceModel.equals("sRGB");
    }

    private static ICC_Profile ensureDisplayProfile(ICC_Profile profile) {
        byte[] profileData;
        if (profile.getProfileClass() != 1 && (profileData = profile.getData())[64] == 0) {
            LOG.warn((Object)"ICC profile is Perceptual, ignoring, treating as Display class");
            PDICCBased.intToBigEndian(1835955314, profileData, 12);
            return ICC_Profile.getInstance(profileData);
        }
        return profile;
    }

    private static void intToBigEndian(int value, byte[] array, int index) {
        array[index] = (byte)(value >> 24);
        array[index + 1] = (byte)(value >> 16);
        array[index + 2] = (byte)(value >> 8);
        array[index + 3] = (byte)value;
    }

    @Override
    public float[] toRGB(float[] value) throws IOException {
        if (this.isRGB) {
            return value;
        }
        if (this.awtColorSpace != null) {
            return this.awtColorSpace.toRGB(this.clampColors(this.awtColorSpace, value));
        }
        return this.alternateColorSpace.toRGB(value);
    }

    private float[] clampColors(ICC_ColorSpace cs, float[] value) {
        float[] result = new float[value.length];
        for (int i = 0; i < value.length; ++i) {
            float minValue = cs.getMinValue(i);
            float maxValue = cs.getMaxValue(i);
            result[i] = value[i] < minValue ? minValue : (value[i] > maxValue ? maxValue : value[i]);
        }
        return result;
    }

    @Override
    public BufferedImage toRGBImage(WritableRaster raster) throws IOException {
        if (this.awtColorSpace != null) {
            return this.toRGBImageAWT(raster, this.awtColorSpace);
        }
        return this.alternateColorSpace.toRGBImage(raster);
    }

    @Override
    public BufferedImage toRawImage(WritableRaster raster) throws IOException {
        if (this.awtColorSpace == null) {
            return this.alternateColorSpace.toRawImage(raster);
        }
        return this.toRawImage(raster, this.awtColorSpace);
    }

    @Override
    public int getNumberOfComponents() {
        if (this.numberOfComponents < 0) {
            int numIccComponents;
            this.numberOfComponents = this.stream.getCOSObject().getInt(COSName.N);
            if (this.iccProfile != null && (numIccComponents = this.iccProfile.getNumComponents()) != this.numberOfComponents) {
                LOG.warn((Object)("Using " + numIccComponents + " components from ICC profile info instead of " + this.numberOfComponents + " components from /N entry"));
                this.numberOfComponents = numIccComponents;
            }
        }
        return this.numberOfComponents;
    }

    @Override
    public float[] getDefaultDecode(int bitsPerComponent) {
        if (this.awtColorSpace != null) {
            int n = this.getNumberOfComponents();
            float[] decode = new float[n * 2];
            for (int i = 0; i < n; ++i) {
                decode[i * 2] = this.awtColorSpace.getMinValue(i);
                decode[i * 2 + 1] = this.awtColorSpace.getMaxValue(i);
            }
            return decode;
        }
        return this.alternateColorSpace.getDefaultDecode(bitsPerComponent);
    }

    @Override
    public PDColor getInitialColor() {
        return this.initialColor;
    }

    public PDColorSpace getAlternateColorSpace() throws IOException {
        COSArray alternateArray;
        COSBase alternate = this.stream.getCOSObject().getDictionaryObject(COSName.ALTERNATE);
        if (alternate == null) {
            COSName csName;
            alternateArray = new COSArray();
            int numComponents = this.getNumberOfComponents();
            switch (numComponents) {
                case 1: {
                    csName = COSName.DEVICEGRAY;
                    break;
                }
                case 3: {
                    csName = COSName.DEVICERGB;
                    break;
                }
                case 4: {
                    csName = COSName.DEVICECMYK;
                    break;
                }
                default: {
                    throw new IOException("Unknown color space number of components:" + numComponents);
                }
            }
            alternateArray.add(csName);
        } else if (alternate instanceof COSArray) {
            alternateArray = (COSArray)alternate;
        } else if (alternate instanceof COSName) {
            alternateArray = new COSArray();
            alternateArray.add(alternate);
        } else {
            throw new IOException("Error: expected COSArray or COSName and not " + alternate.getClass().getName());
        }
        return PDColorSpace.create(alternateArray);
    }

    public PDRange getRangeForComponent(int n) {
        COSArray rangeArray = (COSArray)this.stream.getCOSObject().getDictionaryObject(COSName.RANGE);
        if (rangeArray == null || rangeArray.size() < this.getNumberOfComponents() * 2) {
            return new PDRange();
        }
        return new PDRange(rangeArray, n);
    }

    public COSStream getMetadata() {
        return (COSStream)this.stream.getCOSObject().getDictionaryObject(COSName.METADATA);
    }

    public int getColorSpaceType() {
        if (this.iccProfile != null) {
            return this.iccProfile.getColorSpaceType();
        }
        switch (this.alternateColorSpace.getNumberOfComponents()) {
            case 1: {
                return 6;
            }
            case 3: {
                return 5;
            }
            case 4: {
                return 9;
            }
        }
        return -1;
    }

    @Deprecated
    public void setNumberOfComponents(int n) {
        this.numberOfComponents = n;
        this.stream.getCOSObject().setInt(COSName.N, n);
    }

    public void setAlternateColorSpaces(List<PDColorSpace> list) {
        COSArray altArray = null;
        if (list != null) {
            altArray = COSArrayList.converterToCOSArray(list);
        }
        this.stream.getCOSObject().setItem(COSName.ALTERNATE, (COSBase)altArray);
    }

    public void setRangeForComponent(PDRange range, int n) {
        COSArray rangeArray = (COSArray)this.stream.getCOSObject().getDictionaryObject(COSName.RANGE);
        if (rangeArray == null) {
            rangeArray = new COSArray();
            this.stream.getCOSObject().setItem(COSName.RANGE, (COSBase)rangeArray);
        }
        while (rangeArray.size() < (n + 1) * 2) {
            rangeArray.add(new COSFloat(0.0f));
            rangeArray.add(new COSFloat(1.0f));
        }
        rangeArray.set(n * 2, new COSFloat(range.getMin()));
        rangeArray.set(n * 2 + 1, new COSFloat(range.getMax()));
    }

    public void setMetadata(COSStream metadata) {
        this.stream.getCOSObject().setItem(COSName.METADATA, (COSBase)metadata);
    }

    boolean isSRGB() {
        return this.isRGB;
    }

    @Override
    public String toString() {
        return this.getName() + "{numberOfComponents: " + this.getNumberOfComponents() + "}";
    }

    private static boolean isMinJdk8() {
        String version = System.getProperty("java.specification.version");
        StringTokenizer st = new StringTokenizer(version, ".");
        try {
            int major = Integer.parseInt(st.nextToken());
            int minor = 0;
            if (st.hasMoreTokens()) {
                minor = Integer.parseInt(st.nextToken());
            }
            return major > 1 || major == 1 && minor >= 8;
        }
        catch (NumberFormatException nfe) {
            return true;
        }
    }

    static {
        String cmmProperty = System.getProperty("sun.java2d.cmm");
        boolean result = false;
        if (!PDICCBased.isMinJdk8()) {
            result = true;
        } else if ("sun.java2d.cmm.kcms.KcmsServiceProvider".equals(cmmProperty)) {
            try {
                Class.forName("sun.java2d.cmm.kcms.KcmsServiceProvider");
                result = true;
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
        IS_KCMS = result;
    }
}

