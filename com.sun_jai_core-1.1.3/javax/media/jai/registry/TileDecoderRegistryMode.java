/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.registry.JaiI18N;
import javax.media.jai.util.ImagingListener;

public class TileDecoderRegistryMode
extends RegistryMode {
    public static final String MODE_NAME = "tileDecoder";
    private static Method factoryMethod = null;
    static /* synthetic */ Class class$javax$media$jai$tilecodec$TileDecoderFactory;
    static /* synthetic */ Class class$java$io$InputStream;
    static /* synthetic */ Class class$javax$media$jai$tilecodec$TileCodecParameterList;
    static /* synthetic */ Class class$javax$media$jai$registry$TileDecoderRegistryMode;
    static /* synthetic */ Class class$javax$media$jai$tilecodec$TileCodecDescriptor;

    private static Method getThisFactoryMethod() {
        if (factoryMethod != null) {
            return factoryMethod;
        }
        Class factoryClass = class$javax$media$jai$tilecodec$TileDecoderFactory == null ? (class$javax$media$jai$tilecodec$TileDecoderFactory = TileDecoderRegistryMode.class$("javax.media.jai.tilecodec.TileDecoderFactory")) : class$javax$media$jai$tilecodec$TileDecoderFactory;
        try {
            Class[] paramTypes = new Class[]{class$java$io$InputStream == null ? (class$java$io$InputStream = TileDecoderRegistryMode.class$("java.io.InputStream")) : class$java$io$InputStream, class$javax$media$jai$tilecodec$TileCodecParameterList == null ? (class$javax$media$jai$tilecodec$TileCodecParameterList = TileDecoderRegistryMode.class$("javax.media.jai.tilecodec.TileCodecParameterList")) : class$javax$media$jai$tilecodec$TileCodecParameterList};
            factoryMethod = factoryClass.getMethod("createDecoder", paramTypes);
        }
        catch (NoSuchMethodException e) {
            ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
            String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
            listener.errorOccurred(message, e, class$javax$media$jai$registry$TileDecoderRegistryMode == null ? (class$javax$media$jai$registry$TileDecoderRegistryMode = TileDecoderRegistryMode.class$("javax.media.jai.registry.TileDecoderRegistryMode")) : class$javax$media$jai$registry$TileDecoderRegistryMode, false);
        }
        return factoryMethod;
    }

    public TileDecoderRegistryMode() {
        super(MODE_NAME, class$javax$media$jai$tilecodec$TileCodecDescriptor == null ? (class$javax$media$jai$tilecodec$TileCodecDescriptor = TileDecoderRegistryMode.class$("javax.media.jai.tilecodec.TileCodecDescriptor")) : class$javax$media$jai$tilecodec$TileCodecDescriptor, TileDecoderRegistryMode.getThisFactoryMethod().getReturnType(), TileDecoderRegistryMode.getThisFactoryMethod(), true, false);
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

