/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.util.Iterator;
import java.util.List;
import javax.media.jai.CollectionImage;
import javax.media.jai.CollectionImageFactory;
import javax.media.jai.CollectionOp;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertySource;
import javax.media.jai.registry.JaiI18N;

public final class CIFRegistry {
    private static final String MODE_NAME = "collection";

    public static void register(OperationRegistry registry, String operationName, String productName, CollectionImageFactory cif) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.registerFactory(MODE_NAME, operationName, productName, cif);
    }

    public static void unregister(OperationRegistry registry, String operationName, String productName, CollectionImageFactory cif) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.unregisterFactory(MODE_NAME, operationName, productName, cif);
    }

    public static void setPreference(OperationRegistry registry, String operationName, String productName, CollectionImageFactory preferredCIF, CollectionImageFactory otherCIF) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.setFactoryPreference(MODE_NAME, operationName, productName, preferredCIF, otherCIF);
    }

    public static void unsetPreference(OperationRegistry registry, String operationName, String productName, CollectionImageFactory preferredCIF, CollectionImageFactory otherCIF) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.unsetFactoryPreference(MODE_NAME, operationName, productName, preferredCIF, otherCIF);
    }

    public static void clearPreferences(OperationRegistry registry, String operationName, String productName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.clearFactoryPreferences(MODE_NAME, operationName, productName);
    }

    public static List getOrderedList(OperationRegistry registry, String operationName, String productName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        return registry.getOrderedFactoryList(MODE_NAME, operationName, productName);
    }

    public static Iterator getIterator(OperationRegistry registry, String operationName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        return registry.getFactoryIterator(MODE_NAME, operationName);
    }

    public static CollectionImageFactory get(OperationRegistry registry, String operationName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        return (CollectionImageFactory)registry.getFactory(MODE_NAME, operationName);
    }

    public static CollectionImage create(OperationRegistry registry, String operationName, ParameterBlock paramBlock, RenderingHints renderHints) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        Object[] args = new Object[]{paramBlock, renderHints};
        return (CollectionImage)registry.invokeFactory(MODE_NAME, operationName, args);
    }

    public static PropertySource getPropertySource(CollectionOp op) {
        if (op == null) {
            throw new IllegalArgumentException("op - " + JaiI18N.getString("Generic0"));
        }
        if (op.isRenderable()) {
            throw new IllegalArgumentException("op - " + JaiI18N.getString("CIFRegistry0"));
        }
        return op.getRegistry().getPropertySource(op);
    }
}

