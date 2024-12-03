/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import javax.media.jai.JAI;
import javax.media.jai.OperationNode;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertySource;
import javax.media.jai.RenderableOp;
import javax.media.jai.registry.JaiI18N;

public final class CRIFRegistry {
    private static final String MODE_NAME = "renderable";

    public static void register(OperationRegistry registry, String operationName, ContextualRenderedImageFactory crif) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.registerFactory(MODE_NAME, operationName, null, crif);
    }

    public static void unregister(OperationRegistry registry, String operationName, ContextualRenderedImageFactory crif) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.unregisterFactory(MODE_NAME, operationName, null, crif);
    }

    public static ContextualRenderedImageFactory get(OperationRegistry registry, String operationName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        return (ContextualRenderedImageFactory)registry.getFactory(MODE_NAME, operationName);
    }

    public static RenderedImage create(OperationRegistry registry, String operationName, RenderContext context, ParameterBlock paramBlock) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        Object[] args = new Object[]{context, paramBlock};
        return (RenderedImage)registry.invokeFactory(MODE_NAME, operationName, args);
    }

    public static PropertySource getPropertySource(RenderableOp op) {
        if (op == null) {
            throw new IllegalArgumentException("op - " + JaiI18N.getString("Generic0"));
        }
        return op.getRegistry().getPropertySource((OperationNode)op);
    }
}

