/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.registry.JaiI18N;
import javax.media.jai.util.ImagingListener;

public class TileEncoderRegistryMode
extends RegistryMode {
    public static final String MODE_NAME = "tileEncoder";
    private static Method factoryMethod = null;
    static /* synthetic */ Class class$javax$media$jai$tilecodec$TileEncoderFactory;
    static /* synthetic */ Class class$java$io$OutputStream;
    static /* synthetic */ Class class$javax$media$jai$tilecodec$TileCodecParameterList;
    static /* synthetic */ Class class$java$awt$image$SampleModel;
    static /* synthetic */ Class class$javax$media$jai$registry$TileEncoderRegistryMode;
    static /* synthetic */ Class class$javax$media$jai$tilecodec$TileCodecDescriptor;

    private static Method getThisFactoryMethod() {
        if (factoryMethod != null) {
            return factoryMethod;
        }
        Class factoryClass = class$javax$media$jai$tilecodec$TileEncoderFactory == null ? (class$javax$media$jai$tilecodec$TileEncoderFactory = TileEncoderRegistryMode.class$("javax.media.jai.tilecodec.TileEncoderFactory")) : class$javax$media$jai$tilecodec$TileEncoderFactory;
        try {
            Class[] paramTypes = new Class[]{class$java$io$OutputStream == null ? (class$java$io$OutputStream = TileEncoderRegistryMode.class$("java.io.OutputStream")) : class$java$io$OutputStream, class$javax$media$jai$tilecodec$TileCodecParameterList == null ? (class$javax$media$jai$tilecodec$TileCodecParameterList = TileEncoderRegistryMode.class$("javax.media.jai.tilecodec.TileCodecParameterList")) : class$javax$media$jai$tilecodec$TileCodecParameterList, class$java$awt$image$SampleModel == null ? (class$java$awt$image$SampleModel = TileEncoderRegistryMode.class$("java.awt.image.SampleModel")) : class$java$awt$image$SampleModel};
            factoryMethod = factoryClass.getMethod("createEncoder", paramTypes);
        }
        catch (NoSuchMethodException e) {
            ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
            String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
            listener.errorOccurred(message, e, class$javax$media$jai$registry$TileEncoderRegistryMode == null ? (class$javax$media$jai$registry$TileEncoderRegistryMode = TileEncoderRegistryMode.class$("javax.media.jai.registry.TileEncoderRegistryMode")) : class$javax$media$jai$registry$TileEncoderRegistryMode, false);
        }
        return factoryMethod;
    }

    public TileEncoderRegistryMode() {
        super(MODE_NAME, class$javax$media$jai$tilecodec$TileCodecDescriptor == null ? (class$javax$media$jai$tilecodec$TileCodecDescriptor = TileEncoderRegistryMode.class$("javax.media.jai.tilecodec.TileCodecDescriptor")) : class$javax$media$jai$tilecodec$TileCodecDescriptor, TileEncoderRegistryMode.getThisFactoryMethod().getReturnType(), TileEncoderRegistryMode.getThisFactoryMethod(), true, false);
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

