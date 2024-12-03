/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.registry.JaiI18N;
import javax.media.jai.util.ImagingListener;

public class RemoteRenderableRegistryMode
extends RegistryMode {
    public static final String MODE_NAME = "remoteRenderable";
    private static Method factoryMethod = null;
    static /* synthetic */ Class class$javax$media$jai$remote$RemoteCRIF;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$awt$image$renderable$RenderContext;
    static /* synthetic */ Class class$java$awt$image$renderable$ParameterBlock;
    static /* synthetic */ Class class$javax$media$jai$registry$RemoteRenderableRegistryMode;
    static /* synthetic */ Class class$javax$media$jai$remote$RemoteDescriptor;

    private static Method getThisFactoryMethod() {
        if (factoryMethod != null) {
            return factoryMethod;
        }
        Class factoryClass = class$javax$media$jai$remote$RemoteCRIF == null ? (class$javax$media$jai$remote$RemoteCRIF = RemoteRenderableRegistryMode.class$("javax.media.jai.remote.RemoteCRIF")) : class$javax$media$jai$remote$RemoteCRIF;
        try {
            Class[] paramTypes = new Class[]{class$java$lang$String == null ? (class$java$lang$String = RemoteRenderableRegistryMode.class$("java.lang.String")) : class$java$lang$String, class$java$lang$String == null ? (class$java$lang$String = RemoteRenderableRegistryMode.class$("java.lang.String")) : class$java$lang$String, class$java$awt$image$renderable$RenderContext == null ? (class$java$awt$image$renderable$RenderContext = RemoteRenderableRegistryMode.class$("java.awt.image.renderable.RenderContext")) : class$java$awt$image$renderable$RenderContext, class$java$awt$image$renderable$ParameterBlock == null ? (class$java$awt$image$renderable$ParameterBlock = RemoteRenderableRegistryMode.class$("java.awt.image.renderable.ParameterBlock")) : class$java$awt$image$renderable$ParameterBlock};
            factoryMethod = factoryClass.getMethod("create", paramTypes);
        }
        catch (NoSuchMethodException e) {
            ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
            String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
            listener.errorOccurred(message, e, class$javax$media$jai$registry$RemoteRenderableRegistryMode == null ? (class$javax$media$jai$registry$RemoteRenderableRegistryMode = RemoteRenderableRegistryMode.class$("javax.media.jai.registry.RemoteRenderableRegistryMode")) : class$javax$media$jai$registry$RemoteRenderableRegistryMode, false);
        }
        return factoryMethod;
    }

    public RemoteRenderableRegistryMode() {
        super(MODE_NAME, class$javax$media$jai$remote$RemoteDescriptor == null ? (class$javax$media$jai$remote$RemoteDescriptor = RemoteRenderableRegistryMode.class$("javax.media.jai.remote.RemoteDescriptor")) : class$javax$media$jai$remote$RemoteDescriptor, RemoteRenderableRegistryMode.getThisFactoryMethod().getReturnType(), RemoteRenderableRegistryMode.getThisFactoryMethod(), false, false);
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

