/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.registry.JaiI18N;
import javax.media.jai.util.ImagingListener;

public class RenderableCollectionRegistryMode
extends RegistryMode {
    public static final String MODE_NAME = "renderableCollection";
    private static Method factoryMethod = null;
    static /* synthetic */ Class class$javax$media$jai$RenderableCollectionImageFactory;
    static /* synthetic */ Class class$java$awt$image$renderable$ParameterBlock;
    static /* synthetic */ Class class$javax$media$jai$registry$RenderableCollectionRegistryMode;
    static /* synthetic */ Class class$javax$media$jai$OperationDescriptor;

    private static Method getThisFactoryMethod() {
        if (factoryMethod != null) {
            return factoryMethod;
        }
        Class factoryClass = class$javax$media$jai$RenderableCollectionImageFactory == null ? (class$javax$media$jai$RenderableCollectionImageFactory = RenderableCollectionRegistryMode.class$("javax.media.jai.RenderableCollectionImageFactory")) : class$javax$media$jai$RenderableCollectionImageFactory;
        try {
            Class[] paramTypes = new Class[]{class$java$awt$image$renderable$ParameterBlock == null ? (class$java$awt$image$renderable$ParameterBlock = RenderableCollectionRegistryMode.class$("java.awt.image.renderable.ParameterBlock")) : class$java$awt$image$renderable$ParameterBlock};
            factoryMethod = factoryClass.getMethod("create", paramTypes);
        }
        catch (NoSuchMethodException e) {
            ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
            String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
            listener.errorOccurred(message, e, class$javax$media$jai$registry$RenderableCollectionRegistryMode == null ? (class$javax$media$jai$registry$RenderableCollectionRegistryMode = RenderableCollectionRegistryMode.class$("javax.media.jai.registry.RenderableCollectionRegistryMode")) : class$javax$media$jai$registry$RenderableCollectionRegistryMode, false);
        }
        return factoryMethod;
    }

    public RenderableCollectionRegistryMode() {
        super(MODE_NAME, class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = RenderableCollectionRegistryMode.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor, RenderableCollectionRegistryMode.getThisFactoryMethod().getReturnType(), RenderableCollectionRegistryMode.getThisFactoryMethod(), false, true);
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

