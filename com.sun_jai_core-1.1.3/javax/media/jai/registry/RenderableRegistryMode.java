/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.registry.JaiI18N;
import javax.media.jai.util.ImagingListener;

public class RenderableRegistryMode
extends RegistryMode {
    public static final String MODE_NAME = "renderable";
    private static Method factoryMethod = null;
    static /* synthetic */ Class class$java$awt$image$renderable$ContextualRenderedImageFactory;
    static /* synthetic */ Class class$java$awt$image$renderable$RenderContext;
    static /* synthetic */ Class class$java$awt$image$renderable$ParameterBlock;
    static /* synthetic */ Class class$javax$media$jai$registry$RenderableRegistryMode;
    static /* synthetic */ Class class$javax$media$jai$OperationDescriptor;
    static /* synthetic */ Class class$java$awt$image$renderable$RenderableImage;

    private static Method getThisFactoryMethod() {
        if (factoryMethod != null) {
            return factoryMethod;
        }
        Class factoryClass = class$java$awt$image$renderable$ContextualRenderedImageFactory == null ? (class$java$awt$image$renderable$ContextualRenderedImageFactory = RenderableRegistryMode.class$("java.awt.image.renderable.ContextualRenderedImageFactory")) : class$java$awt$image$renderable$ContextualRenderedImageFactory;
        try {
            Class[] paramTypes = new Class[]{class$java$awt$image$renderable$RenderContext == null ? (class$java$awt$image$renderable$RenderContext = RenderableRegistryMode.class$("java.awt.image.renderable.RenderContext")) : class$java$awt$image$renderable$RenderContext, class$java$awt$image$renderable$ParameterBlock == null ? (class$java$awt$image$renderable$ParameterBlock = RenderableRegistryMode.class$("java.awt.image.renderable.ParameterBlock")) : class$java$awt$image$renderable$ParameterBlock};
            factoryMethod = factoryClass.getMethod("create", paramTypes);
        }
        catch (NoSuchMethodException e) {
            ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
            String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
            listener.errorOccurred(message, e, class$javax$media$jai$registry$RenderableRegistryMode == null ? (class$javax$media$jai$registry$RenderableRegistryMode = RenderableRegistryMode.class$("javax.media.jai.registry.RenderableRegistryMode")) : class$javax$media$jai$registry$RenderableRegistryMode, false);
        }
        return factoryMethod;
    }

    public RenderableRegistryMode() {
        super(MODE_NAME, class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = RenderableRegistryMode.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor, class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = RenderableRegistryMode.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage, RenderableRegistryMode.getThisFactoryMethod(), false, true);
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

