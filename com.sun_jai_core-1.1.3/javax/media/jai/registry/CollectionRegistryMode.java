/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.lang.reflect.Method;
import javax.media.jai.JAI;
import javax.media.jai.RegistryMode;
import javax.media.jai.registry.JaiI18N;
import javax.media.jai.util.ImagingListener;

public class CollectionRegistryMode
extends RegistryMode {
    public static final String MODE_NAME = "collection";
    private static Method factoryMethod = null;
    static /* synthetic */ Class class$javax$media$jai$CollectionImageFactory;
    static /* synthetic */ Class class$java$awt$image$renderable$ParameterBlock;
    static /* synthetic */ Class class$java$awt$RenderingHints;
    static /* synthetic */ Class class$javax$media$jai$registry$CollectionRegistryMode;
    static /* synthetic */ Class class$javax$media$jai$OperationDescriptor;

    private static Method getThisFactoryMethod() {
        if (factoryMethod != null) {
            return factoryMethod;
        }
        Class factoryClass = class$javax$media$jai$CollectionImageFactory == null ? (class$javax$media$jai$CollectionImageFactory = CollectionRegistryMode.class$("javax.media.jai.CollectionImageFactory")) : class$javax$media$jai$CollectionImageFactory;
        try {
            Class[] paramTypes = new Class[]{class$java$awt$image$renderable$ParameterBlock == null ? (class$java$awt$image$renderable$ParameterBlock = CollectionRegistryMode.class$("java.awt.image.renderable.ParameterBlock")) : class$java$awt$image$renderable$ParameterBlock, class$java$awt$RenderingHints == null ? (class$java$awt$RenderingHints = CollectionRegistryMode.class$("java.awt.RenderingHints")) : class$java$awt$RenderingHints};
            factoryMethod = factoryClass.getMethod("create", paramTypes);
        }
        catch (NoSuchMethodException e) {
            ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
            String message = JaiI18N.getString("RegistryMode0") + " " + factoryClass.getName() + ".";
            listener.errorOccurred(message, e, class$javax$media$jai$registry$CollectionRegistryMode == null ? (class$javax$media$jai$registry$CollectionRegistryMode = CollectionRegistryMode.class$("javax.media.jai.registry.CollectionRegistryMode")) : class$javax$media$jai$registry$CollectionRegistryMode, false);
        }
        return factoryMethod;
    }

    public CollectionRegistryMode() {
        super(MODE_NAME, class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = CollectionRegistryMode.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor, CollectionRegistryMode.getThisFactoryMethod().getReturnType(), CollectionRegistryMode.getThisFactoryMethod(), true, true);
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

