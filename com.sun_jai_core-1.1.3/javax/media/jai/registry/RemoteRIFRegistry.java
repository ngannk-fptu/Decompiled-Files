/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.registry;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;
import javax.media.jai.remote.RemoteRIF;
import javax.media.jai.remote.RemoteRenderedImage;

public final class RemoteRIFRegistry {
    private static final String MODE_NAME = "remoteRendered";

    public static void register(OperationRegistry registry, String protocolName, RemoteRIF rrif) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.registerFactory(MODE_NAME, protocolName, null, rrif);
    }

    public static void unregister(OperationRegistry registry, String protocolName, RemoteRIF rrif) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        registry.unregisterFactory(MODE_NAME, protocolName, null, rrif);
    }

    public static RemoteRIF get(OperationRegistry registry, String protocolName) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        return (RemoteRIF)registry.getFactory(MODE_NAME, protocolName);
    }

    public static RemoteRenderedImage create(OperationRegistry registry, String protocolName, String serverName, String operationName, ParameterBlock paramBlock, RenderingHints renderHints) {
        registry = registry != null ? registry : JAI.getDefaultInstance().getOperationRegistry();
        Object[] args = new Object[]{serverName, operationName, paramBlock, renderHints};
        return (RemoteRenderedImage)registry.invokeFactory(MODE_NAME, protocolName, args);
    }
}

