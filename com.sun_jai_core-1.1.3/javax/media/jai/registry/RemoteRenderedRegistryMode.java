/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.registry.JaiI18N;
import javax.media.jai.util.ImagingListener;

public class RemoteRenderedRegistryMode
extends RegistryMode {
    public static final String MODE_NAME = "remoteRendered";
    private static Method factoryMethod = null;
    static /* synthetic */ Class class$javax$media$jai$remote$RemoteRIF;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$awt$image$renderable$ParameterBlock;
    static /* synthetic */ Class class$java$awt$RenderingHints;
    static /* synthetic */ Class class$javax$media$jai$registry$RemoteRenderedRegistryMode;
    static /* synthetic */ Class class$javax$media$jai$remote$RemoteDescriptor;

    private static Method getThisFactoryMethod() {
        if (factoryMethod != null) {
            return factoryMethod;
        }
        Class factoryClass = class$javax$media$jai$remote$RemoteRIF == null ? (class$javax$media$jai$remote$RemoteRIF = RemoteRenderedRegistryMode.class$("javax.media.jai.remote.RemoteRIF")) : class$javax$media$jai$remote$RemoteRIF;
        try {
            Class[] paramTypes = new Class[]{class$java$lang$String == null ? (class$java$lang$String = RemoteRenderedRegistryMode.class$("java.lang.String")) : class$java$lang$String, class$java$lang$String == null ? (class$java$lang$String = RemoteRenderedRegistryMode.class$("java.lang.String")) : class$java$lang$String, class$java$awt$image$renderable$ParameterBlock == null ? (class$java$awt$image$renderable$ParameterBlock = RemoteRenderedRegistryMode.class$("java.awt.image.renderable.ParameterBlock")) : class$java$awt$image$renderable$ParameterBlock, class$java$awt$RenderingHints == null ? (class$java$awt$RenderingHints = RemoteRenderedRegistryMode.class$("java.awt.RenderingHints")) : class$java$awt$RenderingHints};
            factoryMethod = factoryClass.getMethod("create", paramTypes);
        }
        catch (NoSuchMethodException e) {
            ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
            String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
            listener.errorOccurred(message, e, class$javax$media$jai$registry$RemoteRenderedRegistryMode == null ? (class$javax$media$jai$registry$RemoteRenderedRegistryMode = RemoteRenderedRegistryMode.class$("javax.media.jai.registry.RemoteRenderedRegistryMode")) : class$javax$media$jai$registry$RemoteRenderedRegistryMode, false);
        }
        return factoryMethod;
    }

    public RemoteRenderedRegistryMode() {
        super(MODE_NAME, class$javax$media$jai$remote$RemoteDescriptor == null ? (class$javax$media$jai$remote$RemoteDescriptor = RemoteRenderedRegistryMode.class$("javax.media.jai.remote.RemoteDescriptor")) : class$javax$media$jai$remote$RemoteDescriptor, RemoteRenderedRegistryMode.getThisFactoryMethod().getReturnType(), RemoteRenderedRegistryMode.getThisFactoryMethod(), false, false);
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

