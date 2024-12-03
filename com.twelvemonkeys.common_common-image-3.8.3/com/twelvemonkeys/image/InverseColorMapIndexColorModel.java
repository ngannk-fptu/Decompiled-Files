/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.StringUtil
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.image.IndexImage;
import com.twelvemonkeys.image.InverseColorMap;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.lang.Validate;
import java.awt.Image;
import java.awt.image.IndexColorModel;

public class InverseColorMapIndexColorModel
extends IndexColorModel {
    protected int[] rgbs;
    protected int mapSize;
    protected InverseColorMap inverseMap = null;
    private static final int ALPHA_THRESHOLD = 128;
    private int whiteIndex = -1;
    private static final int WHITE = 0xFFFFFF;
    private static final int RGB_MASK = 0xFFFFFF;

    public InverseColorMapIndexColorModel(IndexColorModel indexColorModel) {
        this((IndexColorModel)Validate.notNull((Object)indexColorModel, (String)"color model"), InverseColorMapIndexColorModel.getRGBs(indexColorModel));
    }

    private InverseColorMapIndexColorModel(IndexColorModel indexColorModel, int[] nArray) {
        super(indexColorModel.getComponentSize()[0], indexColorModel.getMapSize(), nArray, 0, indexColorModel.getTransferType(), indexColorModel.getValidPixels());
        this.rgbs = nArray;
        this.mapSize = this.rgbs.length;
        this.inverseMap = new InverseColorMap(this.rgbs);
        this.whiteIndex = this.getWhiteIndex();
    }

    private static int[] getRGBs(IndexColorModel indexColorModel) {
        int[] nArray = new int[indexColorModel.getMapSize()];
        indexColorModel.getRGBs(nArray);
        return nArray;
    }

    public InverseColorMapIndexColorModel(int n, int n2, int[] nArray, int n3, boolean bl, int n4, int n5) {
        super(n, n2, nArray, n3, bl, n4, n5);
        this.rgbs = InverseColorMapIndexColorModel.getRGBs(this);
        this.mapSize = this.rgbs.length;
        this.inverseMap = new InverseColorMap(this.rgbs, n4);
        this.whiteIndex = this.getWhiteIndex();
    }

    public InverseColorMapIndexColorModel(int n, int n2, byte[] byArray, byte[] byArray2, byte[] byArray3, int n3) {
        super(n, n2, byArray, byArray2, byArray3, n3);
        this.rgbs = InverseColorMapIndexColorModel.getRGBs(this);
        this.mapSize = this.rgbs.length;
        this.inverseMap = new InverseColorMap(this.rgbs, n3);
        this.whiteIndex = this.getWhiteIndex();
    }

    public InverseColorMapIndexColorModel(int n, int n2, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        super(n, n2, byArray, byArray2, byArray3);
        this.rgbs = InverseColorMapIndexColorModel.getRGBs(this);
        this.mapSize = this.rgbs.length;
        this.inverseMap = new InverseColorMap(this.rgbs);
        this.whiteIndex = this.getWhiteIndex();
    }

    private int getWhiteIndex() {
        for (int i = 0; i < this.rgbs.length; ++i) {
            int n = this.rgbs[i];
            if ((n & 0xFFFFFF) != 0xFFFFFF) continue;
            return i;
        }
        return -1;
    }

    public static IndexColorModel create(Image image, int n, int n2) {
        IndexColorModel indexColorModel = IndexImage.getIndexColorModel(image, n, n2);
        InverseColorMapIndexColorModel inverseColorMapIndexColorModel = indexColorModel instanceof InverseColorMapIndexColorModel ? (InverseColorMapIndexColorModel)indexColorModel : new InverseColorMapIndexColorModel(indexColorModel);
        return inverseColorMapIndexColorModel;
    }

    @Override
    public Object getDataElements(int n, Object object) {
        int n2;
        int n3 = n >>> 24;
        int n4 = n3 < 128 && this.getTransparentPixel() != -1 ? this.getTransparentPixel() : ((n2 = n & 0xFFFFFF) == 0xFFFFFF && this.whiteIndex != -1 ? this.whiteIndex : this.inverseMap.getIndexNearest(n2));
        return this.installpixel(object, n4);
    }

    private Object installpixel(Object object, int n) {
        switch (this.transferType) {
            case 3: {
                int[] nArray;
                if (object == null) {
                    nArray = new int[1];
                    object = nArray;
                } else {
                    nArray = (int[])object;
                }
                nArray[0] = n;
                break;
            }
            case 0: {
                byte[] byArray;
                if (object == null) {
                    byArray = new byte[1];
                    object = byArray;
                } else {
                    byArray = (byte[])object;
                }
                byArray[0] = (byte)n;
                break;
            }
            case 1: {
                short[] sArray;
                if (object == null) {
                    sArray = new short[1];
                    object = sArray;
                } else {
                    sArray = (short[])object;
                }
                sArray[0] = (short)n;
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return object;
    }

    @Override
    public String toString() {
        return StringUtil.replace((String)super.toString(), (String)"IndexColorModel: ", (String)(this.getClass().getName() + ": "));
    }
}

