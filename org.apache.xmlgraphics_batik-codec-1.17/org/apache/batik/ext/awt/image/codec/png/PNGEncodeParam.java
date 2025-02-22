/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.codec.png;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.batik.ext.awt.image.codec.png.PNGSuggestedPaletteEntry;
import org.apache.batik.ext.awt.image.codec.util.ImageEncodeParam;
import org.apache.batik.ext.awt.image.codec.util.PropertyUtil;

public abstract class PNGEncodeParam
implements ImageEncodeParam {
    public static final int INTENT_PERCEPTUAL = 0;
    public static final int INTENT_RELATIVE = 1;
    public static final int INTENT_SATURATION = 2;
    public static final int INTENT_ABSOLUTE = 3;
    public static final int PNG_FILTER_NONE = 0;
    public static final int PNG_FILTER_SUB = 1;
    public static final int PNG_FILTER_UP = 2;
    public static final int PNG_FILTER_AVERAGE = 3;
    public static final int PNG_FILTER_PAETH = 4;
    protected int bitDepth;
    protected boolean bitDepthSet = false;
    private boolean useInterlacing = false;
    private float[] chromaticity = null;
    private boolean chromaticitySet = false;
    private float gamma;
    private boolean gammaSet = false;
    private int[] paletteHistogram = null;
    private boolean paletteHistogramSet = false;
    private byte[] ICCProfileData = null;
    private boolean ICCProfileDataSet = false;
    private int[] physicalDimension = null;
    private boolean physicalDimensionSet = false;
    private PNGSuggestedPaletteEntry[] suggestedPalette = null;
    private boolean suggestedPaletteSet = false;
    private int[] significantBits = null;
    private boolean significantBitsSet = false;
    private int SRGBIntent;
    private boolean SRGBIntentSet = false;
    private String[] text = null;
    private boolean textSet = false;
    private Date modificationTime;
    private boolean modificationTimeSet = false;
    boolean transparencySet = false;
    private String[] zText = null;
    private boolean zTextSet = false;
    List chunkType = new ArrayList();
    List chunkData = new ArrayList();

    public static PNGEncodeParam getDefaultEncodeParam(RenderedImage im) {
        ColorModel colorModel = im.getColorModel();
        if (colorModel instanceof IndexColorModel) {
            return new Palette();
        }
        SampleModel sampleModel = im.getSampleModel();
        int numBands = sampleModel.getNumBands();
        if (numBands == 1 || numBands == 2) {
            return new Gray();
        }
        return new RGB();
    }

    public abstract void setBitDepth(int var1);

    public int getBitDepth() {
        if (!this.bitDepthSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam11"));
        }
        return this.bitDepth;
    }

    public void unsetBitDepth() {
        this.bitDepthSet = false;
    }

    public void setInterlacing(boolean useInterlacing) {
        this.useInterlacing = useInterlacing;
    }

    public boolean getInterlacing() {
        return this.useInterlacing;
    }

    public void unsetBackground() {
        throw new RuntimeException(PropertyUtil.getString("PNGEncodeParam23"));
    }

    public boolean isBackgroundSet() {
        throw new RuntimeException(PropertyUtil.getString("PNGEncodeParam24"));
    }

    public void setChromaticity(float[] chromaticity) {
        if (chromaticity.length != 8) {
            throw new IllegalArgumentException();
        }
        this.chromaticity = (float[])chromaticity.clone();
        this.chromaticitySet = true;
    }

    public void setChromaticity(float whitePointX, float whitePointY, float redX, float redY, float greenX, float greenY, float blueX, float blueY) {
        float[] chroma = new float[]{whitePointX, whitePointY, redX, redY, greenX, greenY, blueX, blueY};
        this.setChromaticity(chroma);
    }

    public float[] getChromaticity() {
        if (!this.chromaticitySet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam12"));
        }
        return (float[])this.chromaticity.clone();
    }

    public void unsetChromaticity() {
        this.chromaticity = null;
        this.chromaticitySet = false;
    }

    public boolean isChromaticitySet() {
        return this.chromaticitySet;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
        this.gammaSet = true;
    }

    public float getGamma() {
        if (!this.gammaSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam13"));
        }
        return this.gamma;
    }

    public void unsetGamma() {
        this.gammaSet = false;
    }

    public boolean isGammaSet() {
        return this.gammaSet;
    }

    public void setPaletteHistogram(int[] paletteHistogram) {
        this.paletteHistogram = (int[])paletteHistogram.clone();
        this.paletteHistogramSet = true;
    }

    public int[] getPaletteHistogram() {
        if (!this.paletteHistogramSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam14"));
        }
        return this.paletteHistogram;
    }

    public void unsetPaletteHistogram() {
        this.paletteHistogram = null;
        this.paletteHistogramSet = false;
    }

    public boolean isPaletteHistogramSet() {
        return this.paletteHistogramSet;
    }

    public void setICCProfileData(byte[] ICCProfileData) {
        this.ICCProfileData = (byte[])ICCProfileData.clone();
        this.ICCProfileDataSet = true;
    }

    public byte[] getICCProfileData() {
        if (!this.ICCProfileDataSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam15"));
        }
        return (byte[])this.ICCProfileData.clone();
    }

    public void unsetICCProfileData() {
        this.ICCProfileData = null;
        this.ICCProfileDataSet = false;
    }

    public boolean isICCProfileDataSet() {
        return this.ICCProfileDataSet;
    }

    public void setPhysicalDimension(int[] physicalDimension) {
        this.physicalDimension = (int[])physicalDimension.clone();
        this.physicalDimensionSet = true;
    }

    public void setPhysicalDimension(int xPixelsPerUnit, int yPixelsPerUnit, int unitSpecifier) {
        int[] pd = new int[]{xPixelsPerUnit, yPixelsPerUnit, unitSpecifier};
        this.setPhysicalDimension(pd);
    }

    public int[] getPhysicalDimension() {
        if (!this.physicalDimensionSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam16"));
        }
        return (int[])this.physicalDimension.clone();
    }

    public void unsetPhysicalDimension() {
        this.physicalDimension = null;
        this.physicalDimensionSet = false;
    }

    public boolean isPhysicalDimensionSet() {
        return this.physicalDimensionSet;
    }

    public void setSuggestedPalette(PNGSuggestedPaletteEntry[] palette) {
        this.suggestedPalette = (PNGSuggestedPaletteEntry[])palette.clone();
        this.suggestedPaletteSet = true;
    }

    public PNGSuggestedPaletteEntry[] getSuggestedPalette() {
        if (!this.suggestedPaletteSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam17"));
        }
        return (PNGSuggestedPaletteEntry[])this.suggestedPalette.clone();
    }

    public void unsetSuggestedPalette() {
        this.suggestedPalette = null;
        this.suggestedPaletteSet = false;
    }

    public boolean isSuggestedPaletteSet() {
        return this.suggestedPaletteSet;
    }

    public void setSignificantBits(int[] significantBits) {
        this.significantBits = (int[])significantBits.clone();
        this.significantBitsSet = true;
    }

    public int[] getSignificantBits() {
        if (!this.significantBitsSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam18"));
        }
        return (int[])this.significantBits.clone();
    }

    public void unsetSignificantBits() {
        this.significantBits = null;
        this.significantBitsSet = false;
    }

    public boolean isSignificantBitsSet() {
        return this.significantBitsSet;
    }

    public void setSRGBIntent(int SRGBIntent) {
        this.SRGBIntent = SRGBIntent;
        this.SRGBIntentSet = true;
    }

    public int getSRGBIntent() {
        if (!this.SRGBIntentSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam19"));
        }
        return this.SRGBIntent;
    }

    public void unsetSRGBIntent() {
        this.SRGBIntentSet = false;
    }

    public boolean isSRGBIntentSet() {
        return this.SRGBIntentSet;
    }

    public void setText(String[] text) {
        this.text = text;
        this.textSet = true;
    }

    public String[] getText() {
        if (!this.textSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam20"));
        }
        return this.text;
    }

    public void unsetText() {
        this.text = null;
        this.textSet = false;
    }

    public boolean isTextSet() {
        return this.textSet;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
        this.modificationTimeSet = true;
    }

    public Date getModificationTime() {
        if (!this.modificationTimeSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam21"));
        }
        return this.modificationTime;
    }

    public void unsetModificationTime() {
        this.modificationTime = null;
        this.modificationTimeSet = false;
    }

    public boolean isModificationTimeSet() {
        return this.modificationTimeSet;
    }

    public void unsetTransparency() {
        this.transparencySet = false;
    }

    public boolean isTransparencySet() {
        return this.transparencySet;
    }

    public void setCompressedText(String[] text) {
        this.zText = text;
        this.zTextSet = true;
    }

    public String[] getCompressedText() {
        if (!this.zTextSet) {
            throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam22"));
        }
        return this.zText;
    }

    public void unsetCompressedText() {
        this.zText = null;
        this.zTextSet = false;
    }

    public boolean isCompressedTextSet() {
        return this.zTextSet;
    }

    public synchronized void addPrivateChunk(String type, byte[] data) {
        this.chunkType.add(type);
        this.chunkData.add(data.clone());
    }

    public synchronized int getNumPrivateChunks() {
        return this.chunkType.size();
    }

    public synchronized String getPrivateChunkType(int index) {
        return (String)this.chunkType.get(index);
    }

    public synchronized byte[] getPrivateChunkData(int index) {
        return (byte[])this.chunkData.get(index);
    }

    public synchronized void removeUnsafeToCopyPrivateChunks() {
        ArrayList<String> newChunkType = new ArrayList<String>();
        ArrayList<byte[]> newChunkData = new ArrayList<byte[]>();
        int len = this.getNumPrivateChunks();
        for (int i = 0; i < len; ++i) {
            String type = this.getPrivateChunkType(i);
            char lastChar = type.charAt(3);
            if (lastChar < 'a' || lastChar > 'z') continue;
            newChunkType.add(type);
            newChunkData.add(this.getPrivateChunkData(i));
        }
        this.chunkType = newChunkType;
        this.chunkData = newChunkData;
    }

    public synchronized void removeAllPrivateChunks() {
        this.chunkType = new ArrayList();
        this.chunkData = new ArrayList();
    }

    private static final int abs(int x) {
        return x < 0 ? -x : x;
    }

    public static final int paethPredictor(int a, int b, int c) {
        int p = a + b - c;
        int pa = PNGEncodeParam.abs(p - a);
        int pb = PNGEncodeParam.abs(p - b);
        int pc = PNGEncodeParam.abs(p - c);
        if (pa <= pb && pa <= pc) {
            return a;
        }
        if (pb <= pc) {
            return b;
        }
        return c;
    }

    public int filterRow(byte[] currRow, byte[] prevRow, byte[][] scratchRows, int bytesPerRow, int bytesPerPixel) {
        int[] badness = new int[]{0, 0, 0, 0, 0};
        for (int i = bytesPerPixel; i < bytesPerRow + bytesPerPixel; ++i) {
            int pc;
            int curr = currRow[i] & 0xFF;
            int left = currRow[i - bytesPerPixel] & 0xFF;
            int up = prevRow[i] & 0xFF;
            int upleft = prevRow[i - bytesPerPixel] & 0xFF;
            badness[0] = badness[0] + curr;
            int diff = curr - left;
            scratchRows[1][i] = (byte)diff;
            badness[1] = badness[1] + (diff > 0 ? diff : -diff);
            diff = curr - up;
            scratchRows[2][i] = (byte)diff;
            badness[2] = badness[2] + (diff >= 0 ? diff : -diff);
            diff = curr - (left + up >> 1);
            scratchRows[3][i] = (byte)diff;
            badness[3] = badness[3] + (diff >= 0 ? diff : -diff);
            int pa = up - upleft;
            int pb = left - upleft;
            if (pa < 0) {
                if (pb < 0) {
                    diff = pa >= pb ? curr - left : curr - up;
                } else {
                    pc = pa + pb;
                    diff = (pa = -pa) <= pb ? (pa <= pc ? curr - left : curr - upleft) : (pb <= -pc ? curr - up : curr - upleft);
                }
            } else {
                diff = pb < 0 ? (pa <= (pb = -pb) ? (pa <= (pc = pb - pa) ? curr - left : (pb == pc ? curr - up : curr - upleft)) : (pb <= (pc = pa - pb) ? curr - up : curr - upleft)) : (pa <= pb ? curr - left : curr - up);
            }
            scratchRows[4][i] = (byte)diff;
            badness[4] = badness[4] + (diff >= 0 ? diff : -diff);
        }
        int filterType = 0;
        int minBadness = badness[0];
        for (int i = 1; i < 5; ++i) {
            if (badness[i] >= minBadness) continue;
            minBadness = badness[i];
            filterType = i;
        }
        if (filterType == 0) {
            System.arraycopy(currRow, bytesPerPixel, scratchRows[0], bytesPerPixel, bytesPerRow);
        }
        return filterType;
    }

    public static class RGB
    extends PNGEncodeParam {
        private boolean backgroundSet = false;
        private int[] backgroundRGB;
        private int[] transparency;

        @Override
        public void unsetBackground() {
            this.backgroundSet = false;
        }

        @Override
        public boolean isBackgroundSet() {
            return this.backgroundSet;
        }

        @Override
        public void setBitDepth(int bitDepth) {
            if (bitDepth != 8 && bitDepth != 16) {
                throw new RuntimeException();
            }
            this.bitDepth = bitDepth;
            this.bitDepthSet = true;
        }

        public void setBackgroundRGB(int[] rgb) {
            if (rgb.length != 3) {
                throw new RuntimeException();
            }
            this.backgroundRGB = rgb;
            this.backgroundSet = true;
        }

        public int[] getBackgroundRGB() {
            if (!this.backgroundSet) {
                throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam9"));
            }
            return this.backgroundRGB;
        }

        public void setTransparentRGB(int[] transparentRGB) {
            this.transparency = (int[])transparentRGB.clone();
            this.transparencySet = true;
        }

        public int[] getTransparentRGB() {
            if (!this.transparencySet) {
                throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam10"));
            }
            return (int[])this.transparency.clone();
        }
    }

    public static class Gray
    extends PNGEncodeParam {
        private boolean backgroundSet = false;
        private int backgroundPaletteGray;
        private int[] transparency;
        private int bitShift;
        private boolean bitShiftSet = false;

        @Override
        public void unsetBackground() {
            this.backgroundSet = false;
        }

        @Override
        public boolean isBackgroundSet() {
            return this.backgroundSet;
        }

        @Override
        public void setBitDepth(int bitDepth) {
            if (bitDepth != 1 && bitDepth != 2 && bitDepth != 4 && bitDepth != 8 && bitDepth != 16) {
                throw new IllegalArgumentException();
            }
            this.bitDepth = bitDepth;
            this.bitDepthSet = true;
        }

        public void setBackgroundGray(int gray) {
            this.backgroundPaletteGray = gray;
            this.backgroundSet = true;
        }

        public int getBackgroundGray() {
            if (!this.backgroundSet) {
                throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam6"));
            }
            return this.backgroundPaletteGray;
        }

        public void setTransparentGray(int transparentGray) {
            this.transparency = new int[1];
            this.transparency[0] = transparentGray;
            this.transparencySet = true;
        }

        public int getTransparentGray() {
            if (!this.transparencySet) {
                throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam7"));
            }
            int gray = this.transparency[0];
            return gray;
        }

        public void setBitShift(int bitShift) {
            if (bitShift < 0) {
                throw new RuntimeException();
            }
            this.bitShift = bitShift;
            this.bitShiftSet = true;
        }

        public int getBitShift() {
            if (!this.bitShiftSet) {
                throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam8"));
            }
            return this.bitShift;
        }

        public void unsetBitShift() {
            this.bitShiftSet = false;
        }

        public boolean isBitShiftSet() {
            return this.bitShiftSet;
        }

        public boolean isBitDepthSet() {
            return this.bitDepthSet;
        }
    }

    public static class Palette
    extends PNGEncodeParam {
        private boolean backgroundSet = false;
        private int[] palette = null;
        private boolean paletteSet = false;
        private int backgroundPaletteIndex;
        private int[] transparency;

        @Override
        public void unsetBackground() {
            this.backgroundSet = false;
        }

        @Override
        public boolean isBackgroundSet() {
            return this.backgroundSet;
        }

        @Override
        public void setBitDepth(int bitDepth) {
            if (bitDepth != 1 && bitDepth != 2 && bitDepth != 4 && bitDepth != 8) {
                throw new IllegalArgumentException(PropertyUtil.getString("PNGEncodeParam2"));
            }
            this.bitDepth = bitDepth;
            this.bitDepthSet = true;
        }

        public void setPalette(int[] rgb) {
            if (rgb.length < 3 || rgb.length > 768) {
                throw new IllegalArgumentException(PropertyUtil.getString("PNGEncodeParam0"));
            }
            if (rgb.length % 3 != 0) {
                throw new IllegalArgumentException(PropertyUtil.getString("PNGEncodeParam1"));
            }
            this.palette = (int[])rgb.clone();
            this.paletteSet = true;
        }

        public int[] getPalette() {
            if (!this.paletteSet) {
                throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam3"));
            }
            return (int[])this.palette.clone();
        }

        public void unsetPalette() {
            this.palette = null;
            this.paletteSet = false;
        }

        public boolean isPaletteSet() {
            return this.paletteSet;
        }

        public void setBackgroundPaletteIndex(int index) {
            this.backgroundPaletteIndex = index;
            this.backgroundSet = true;
        }

        public int getBackgroundPaletteIndex() {
            if (!this.backgroundSet) {
                throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam4"));
            }
            return this.backgroundPaletteIndex;
        }

        public void setPaletteTransparency(byte[] alpha) {
            this.transparency = new int[alpha.length];
            for (int i = 0; i < alpha.length; ++i) {
                this.transparency[i] = alpha[i] & 0xFF;
            }
            this.transparencySet = true;
        }

        public byte[] getPaletteTransparency() {
            if (!this.transparencySet) {
                throw new IllegalStateException(PropertyUtil.getString("PNGEncodeParam5"));
            }
            byte[] alpha = new byte[this.transparency.length];
            for (int i = 0; i < alpha.length; ++i) {
                alpha[i] = (byte)this.transparency[i];
            }
            return alpha;
        }
    }
}

