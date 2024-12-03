/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CollectionImage;
import javax.media.jai.CollectionOp;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertySource;
import javax.media.jai.RenderableCollectionImageFactory;
import javax.media.jai.registry.JaiI18N;

public final class RCIFRegistry {
    private static final String MODE_NAME = "renderableCollection";

    public static void register(OperationRegistry registry, String operationName, RenderableCollectionImageFactory rcif) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.registerFactory(MODE_NAME, operationName, null, rcif);
    }

    public static void unregister(OperationRegistry registry, String operationName, RenderableCollectionImageFactory rcif) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.unregisterFactory(MODE_NAME, operationName, null, rcif);
    }

    public static RenderableCollectionImageFactory get(OperationRegistry registry, String operationName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        return (RenderableCollectionImageFactory)registry.getFactory(MODE_NAME, operationName);
    }

    public static CollectionImage create(OperationRegistry registry, String operationName, ParameterBlock paramBlock) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        Object[] args = new Object[]{paramBlock};
        return (CollectionImage)registry.invokeFactory(MODE_NAME, operationName, args);
    }

    public static PropertySource getPropertySource(CollectionOp op) {
        if (op == null) {
            throw new IllegalArgumentException("op - " + JaiI18N.getString("Generic0"));
        }
        if (!op.isRenderable()) {
            throw new IllegalArgumentException("op - " + JaiI18N.getString("CIFRegistry1"));
        }
        return op.getRegistry().getPropertySource(op);
    }
}

