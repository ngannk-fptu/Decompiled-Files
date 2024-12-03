/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import com.sun.media.jai.rmi.SerializableStateImpl;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import javax.media.jai.FloatDoubleColorModel;

public class ColorModelState
extends SerializableStateImpl {
    private static final int COLORSPACE_OTHERS = 0;
    private static final int COLORSPACE_PREDEFINED = 1;
    private static final int COLORSPACE_ICC = 2;
    private static final int COLORMODEL_NULL = 0;
    private static final int COLORMODEL_FLOAT_DOUBLE_COMPONENT = 1;
    private static final int COLORMODEL_COMPONENT = 2;
    private static final int COLORMODEL_INDEX = 3;
    private static final int COLORMODEL_DIRECT = 4;
    private transient ColorModel colorModel = null;
    static /* synthetic */ Class class$java$awt$image$ComponentColorModel;
    static /* synthetic */ Class class$javax$media$jai$FloatDoubleColorModel;
    static /* synthetic */ Class class$java$awt$image$IndexColorModel;
    static /* synthetic */ Class class$java$awt$image$DirectColorModel;
    static /* synthetic */ Class class$com$sun$media$jai$codecimpl$util$FloatDoubleColorModel;

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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean serializeColorSpace(ColorSpace cs, ObjectOutputStream out) throws IOException {
        int[] colorSpaceType = ColorModelState.getPredefinedColorSpace(cs);
        boolean isICCColorSpace = cs instanceof ICC_ColorSpace;
        if (colorSpaceType == null) {
            out.writeInt(0);
            Object object = cs;
            boolean flag = false;
            try {
                Class<?> cls = cs.getClass();
                Method getInstance = cls.getMethod("getInstance", null);
                if (!Modifier.isPublic(cls.getModifiers())) return true;
                flag = true;
                object = cls.getName();
                return true;
            }
            catch (Exception e) {
                return true;
            }
            finally {
                out.writeBoolean(flag);
                out.writeObject(object);
            }
        } else {
            out.writeInt(1);
            out.writeInt(colorSpaceType[0]);
        }
        return true;
    }

    private static ColorSpace deserializeColorSpace(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ColorSpace cs = null;
        int colorSpaceType = in.readInt();
        if (colorSpaceType == 0) {
            if (in.readBoolean()) {
                String name = (String)in.readObject();
                try {
                    Class<?> cls = Class.forName(name);
                    Method getInstance = cls.getMethod("getInstance", null);
                    cs = (ColorSpace)getInstance.invoke(null, null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                cs = (ColorSpace)in.readObject();
            }
        } else if (colorSpaceType == 1) {
            cs = ColorSpace.getInstance(in.readInt());
        }
        return cs;
    }

    public static Class[] getSupportedClasses() {
        return new Class[]{class$java$awt$image$ComponentColorModel == null ? (class$java$awt$image$ComponentColorModel = ColorModelState.class$("java.awt.image.ComponentColorModel")) : class$java$awt$image$ComponentColorModel, class$javax$media$jai$FloatDoubleColorModel == null ? (class$javax$media$jai$FloatDoubleColorModel = ColorModelState.class$("javax.media.jai.FloatDoubleColorModel")) : class$javax$media$jai$FloatDoubleColorModel, class$java$awt$image$IndexColorModel == null ? (class$java$awt$image$IndexColorModel = ColorModelState.class$("java.awt.image.IndexColorModel")) : class$java$awt$image$IndexColorModel, class$java$awt$image$DirectColorModel == null ? (class$java$awt$image$DirectColorModel = ColorModelState.class$("java.awt.image.DirectColorModel")) : class$java$awt$image$DirectColorModel, class$com$sun$media$jai$codecimpl$util$FloatDoubleColorModel == null ? (class$com$sun$media$jai$codecimpl$util$FloatDoubleColorModel = ColorModelState.class$("com.sun.media.jai.codecimpl.util.FloatDoubleColorModel")) : class$com$sun$media$jai$codecimpl$util$FloatDoubleColorModel};
    }

    public ColorModelState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        ColorModel colorModel = (ColorModel)this.theObject;
        if (colorModel == null) {
            out.writeInt(0);
        } else if (colorModel instanceof ComponentColorModel) {
            ComponentColorModel cm = (ComponentColorModel)colorModel;
            int type = 2;
            if (colorModel instanceof FloatDoubleColorModel) {
                type = 1;
            }
            out.writeInt(type);
            ColorModelState.serializeColorSpace(cm.getColorSpace(), out);
            if (type == 2) {
                out.writeObject(cm.getComponentSize());
            }
            out.writeBoolean(cm.hasAlpha());
            out.writeBoolean(cm.isAlphaPremultiplied());
            out.writeInt(cm.getTransparency());
            SampleModel sm = cm.createCompatibleSampleModel(1, 1);
            out.writeInt(sm.getTransferType());
        } else if (colorModel instanceof IndexColorModel) {
            IndexColorModel cm = (IndexColorModel)colorModel;
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
        } else if (colorModel instanceof DirectColorModel) {
            DirectColorModel cm = (DirectColorModel)colorModel;
            out.writeInt(4);
            boolean csSerialized = ColorModelState.serializeColorSpace(cm.getColorSpace(), out);
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
            throw new RuntimeException(JaiI18N.getString("ColorModelState0"));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ColorModel colorModel = null;
        ColorSpace cs = null;
        switch (in.readInt()) {
            case 0: {
                colorModel = null;
                break;
            }
            case 1: {
                cs = ColorModelState.deserializeColorSpace(in);
                if (cs == null) {
                    colorModel = null;
                    return;
                }
                colorModel = new FloatDoubleColorModel(cs, in.readBoolean(), in.readBoolean(), in.readInt(), in.readInt());
                break;
            }
            case 2: {
                cs = ColorModelState.deserializeColorSpace(in);
                if (cs == null) {
                    colorModel = null;
                    return;
                }
                colorModel = new ComponentColorModel(cs, (int[])in.readObject(), in.readBoolean(), in.readBoolean(), in.readInt(), in.readInt());
                break;
            }
            case 3: {
                colorModel = new IndexColorModel(in.readInt(), in.readInt(), (int[])in.readObject(), 0, in.readBoolean(), in.readInt(), in.readInt());
                break;
            }
            case 4: {
                cs = ColorModelState.deserializeColorSpace(in);
                if (cs != null) {
                    colorModel = new DirectColorModel(cs, in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readBoolean(), in.readInt());
                    break;
                }
                if (in.readBoolean()) {
                    colorModel = new DirectColorModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt());
                    break;
                }
                colorModel = new DirectColorModel(in.readInt(), in.readInt(), in.readInt(), in.readInt());
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("ColorModelState1"));
            }
        }
        this.theObject = colorModel;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

