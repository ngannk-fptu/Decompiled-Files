/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.media.jai.FloatDoubleColorModel;

public class ColorModelProxy
implements Serializable {
    private static final int COLORSPACE_UNKNOWN = 0;
    private static final int COLORSPACE_PREDEFINED = 1;
    private static final int COLORSPACE_ICC = 2;
    private static final int COLORMODEL_NULL = 0;
    private static final int COLORMODEL_FLOAT_DOUBLE_COMPONENT = 1;
    private static final int COLORMODEL_COMPONENT = 2;
    private static final int COLORMODEL_INDEX = 3;
    private static final int COLORMODEL_DIRECT = 4;
    private transient ColorModel colorModel = null;

    private static int[] getPredefinedColorSpace(ColorSpace cs) {
        int[] colorSpaces = new int[]{1001, 1003, 1004, 1002, 1000};
        for (int i = 0; i < colorSpaces.length; ++i) {
            try {
                if (!cs.equals(ColorSpace.getInstance(colorSpaces[i]))) continue;
                return new int[]{colorSpaces[i]};
            }
            catch (Throwable e) {
                // empty catch block
            }
        }
        int numComponents = cs.getNumComponents();
        int type = cs.getType();
        if (numComponents == 1 && type == 6) {
            return new int[]{1003};
        }
        if (numComponents == 3) {
            if (type == 5) {
                return new int[]{1000};
            }
            if (type == 0) {
                return new int[]{1001};
            }
        }
        return null;
    }

    private static boolean serializeColorSpace(ColorSpace cs, ObjectOutputStream out) throws IOException {
        int[] colorSpaceType = null;
        if (!(cs instanceof ICC_ColorSpace) && (colorSpaceType = ColorModelProxy.getPredefinedColorSpace(cs)) == null) {
            out.writeInt(0);
            out.writeInt(cs.getNumComponents());
            return false;
        }
        if (cs instanceof ICC_ColorSpace) {
            out.writeInt(2);
            ((ICC_ColorSpace)cs).getProfile().write(out);
        } else {
            out.writeInt(1);
            out.writeInt(colorSpaceType[0]);
        }
        return true;
    }

    private static ColorSpace deserializeColorSpace(ObjectInputStream in) throws IOException {
        ColorSpace cs = null;
        int colorSpaceType = in.readInt();
        if (colorSpaceType == 2) {
            cs = new ICC_ColorSpace(ICC_Profile.getInstance(in));
        } else if (colorSpaceType == 1) {
            cs = ColorSpace.getInstance(in.readInt());
        } else if (colorSpaceType == 0) {
            switch (in.readInt()) {
                case 1: {
                    cs = ColorSpace.getInstance(1003);
                    break;
                }
                case 3: {
                    cs = ColorSpace.getInstance(1000);
                    break;
                }
                default: {
                    cs = null;
                }
            }
        }
        return cs;
    }

    public ColorModelProxy(ColorModel source) {
        this.colorModel = source;
    }

    public ColorModel getColorModel() {
        return this.colorModel;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (this.colorModel == null) {
            out.writeInt(0);
        } else if (this.colorModel instanceof ComponentColorModel) {
            ComponentColorModel cm = (ComponentColorModel)this.colorModel;
            int type = 2;
            if (this.colorModel instanceof FloatDoubleColorModel) {
                type = 1;
            }
            out.writeInt(type);
            ColorModelProxy.serializeColorSpace(cm.getColorSpace(), out);
            if (type == 2) {
                out.writeObject(cm.getComponentSize());
            }
            out.writeBoolean(cm.hasAlpha());
            out.writeBoolean(cm.isAlphaPremultiplied());
            out.writeInt(cm.getTransparency());
            SampleModel sm = cm.createCompatibleSampleModel(1, 1);
            out.writeInt(sm.getTransferType());
        } else if (this.colorModel instanceof IndexColorModel) {
            IndexColorModel cm = (IndexColorModel)this.colorModel;
            out.writeInt(3);
            int size = cm.getMapSize();
            int[] cmap = new int[size];
            cm.getRGBs(cmap);
            out.writeInt(cm.getPixelSize());
            out.writeInt(size);
            out.writeObject(cmap);
            out.writeBoolean(cm.hasAlpha());
            out.writeInt(cm.getTransparentPixel());
            SampleModel sm = cm.createCompatibleSampleModel(1, 1);
            out.writeInt(sm.getTransferType());
        } else if (this.colorModel instanceof DirectColorModel) {
            DirectColorModel cm = (DirectColorModel)this.colorModel;
            out.writeInt(4);
            boolean csSerialized = ColorModelProxy.serializeColorSpace(cm.getColorSpace(), out);
            if (!csSerialized) {
                out.writeBoolean(cm.hasAlpha());
            }
            out.writeInt(cm.getPixelSize());
            out.writeInt(cm.getRedMask());
            out.writeInt(cm.getGreenMask());
            out.writeInt(cm.getBlueMask());
            if (csSerialized || cm.hasAlpha()) {
                out.writeInt(cm.getAlphaMask());
            }
            if (csSerialized) {
                out.writeBoolean(cm.isAlphaPremultiplied());
                SampleModel sm = cm.createCompatibleSampleModel(1, 1);
                out.writeInt(sm.getTransferType());
            }
        } else {
            throw new RuntimeException(JaiI18N.getString("ColorModelProxy0"));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ColorSpace cs = null;
        switch (in.readInt()) {
            case 0: {
                this.colorModel = null;
                break;
            }
            case 1: {
                cs = ColorModelProxy.deserializeColorSpace(in);
                if (cs == null) {
                    this.colorModel = null;
                    return;
                }
                this.colorModel = new FloatDoubleColorModel(cs, in.readBoolean(), in.readBoolean(), in.readInt(), in.readInt());
                break;
            }
            case 2: {
                cs = ColorModelProxy.deserializeColorSpace(in);
                if (cs == null) {
                    this.colorModel = null;
                    return;
                }
                this.colorModel = new ComponentColorModel(cs, (int[])in.readObject(), in.readBoolean(), in.readBoolean(), in.readInt(), in.readInt());
                break;
            }
            case 3: {
                this.colorModel = new IndexColorModel(in.readInt(), in.readInt(), (int[])in.readObject(), 0, in.readBoolean(), in.readInt(), in.readInt());
                break;
            }
            case 4: {
                cs = ColorModelProxy.deserializeColorSpace(in);
                if (cs != null) {
                    this.colorModel = new DirectColorModel(cs, in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readBoolean(), in.readInt());
                    break;
                }
                if (in.readBoolean()) {
                    this.colorModel = new DirectColorModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt());
                    break;
                }
                this.colorModel = new DirectColorModel(in.readInt(), in.readInt(), in.readInt(), in.readInt());
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("ColorModelProxy1"));
            }
        }
    }
}

