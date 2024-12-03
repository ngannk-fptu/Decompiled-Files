/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.registry.JaiI18N;
import javax.media.jai.util.ImagingListener;

public class RenderedRegistryMode
extends RegistryMode {
    public static final String MODE_NAME = "rendered";
    private static Method factoryMethod = null;
    static /* synthetic */ Class class$java$awt$image$renderable$RenderedImageFactory;
    static /* synthetic */ Class class$java$awt$image$renderable$ParameterBlock;
    static /* synthetic */ Class class$java$awt$RenderingHints;
    static /* synthetic */ Class class$javax$media$jai$registry$RenderedRegistryMode;
    static /* synthetic */ Class class$javax$media$jai$OperationDescriptor;

    private static Method getThisFactoryMethod() {
        if (factoryMethod != null) {
            return factoryMethod;
        }
        Class factoryClass = class$java$awt$image$renderable$RenderedImageFactory == null ? (class$java$awt$image$renderable$RenderedImageFactory = RenderedRegistryMode.class$("java.awt.image.renderable.RenderedImageFactory")) : class$java$awt$image$renderable$RenderedImageFactory;
        try {
            Class[] paramTypes = new Class[]{class$java$awt$image$renderable$ParameterBlock == null ? (class$java$awt$image$renderable$ParameterBlock = RenderedRegistryMode.class$("java.awt.image.renderable.ParameterBlock")) : class$java$awt$image$renderable$ParameterBlock, class$java$awt$RenderingHints == null ? (class$java$awt$RenderingHints = RenderedRegistryMode.class$("java.awt.RenderingHints")) : class$java$awt$RenderingHints};
            factoryMethod = factoryClass.getMethod("create", paramTypes);
        }
        catch (NoSuchMethodException e) {
            ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
            String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
            listener.errorOccurred(message, e, class$javax$media$jai$registry$RenderedRegistryMode == null ? (class$javax$media$jai$registry$RenderedRegistryMode = RenderedRegistryMode.class$("javax.media.jai.registry.RenderedRegistryMode")) : class$javax$media$jai$registry$RenderedRegistryMode, false);
        }
        return factoryMethod;
    }

    public RenderedRegistryMode() {
        super(MODE_NAME, class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = RenderedRegistryMode.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor, RenderedRegistryMode.getThisFactoryMethod().getReturnType(), RenderedRegistryMode.getThisFactoryMethod(), true, true);
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

