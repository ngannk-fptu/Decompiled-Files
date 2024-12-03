/*
 * Decompiled with CFR 0.152.
 */
package javax.websocket;

import java.util.Iterator;
import java.util.ServiceLoader;
import javax.websocket.WebSocketContainer;

public abstract class ContainerProvider {
    private static final String DEFAULT_PROVIDER_CLASS_NAME = "org.apache.tomcat.websocket.WsWebSocketContainer";

    public static WebSocketContainer getWebSocketContainer() {
        WebSocketContainer result = null;
        ServiceLoader<ContainerProvider> serviceLoader = ServiceLoader.load(ContainerProvider.class);
        Iterator<ContainerProvider> iter = serviceLoader.iterator();
        while (result == null && iter.hasNext()) {
            result = iter.next().getContainer();
        }
        if (result == null) {
            try {
                Class<?> clazz = Class.forName(DEFAULT_PROVIDER_CLASS_NAME);
                result = (WebSocketContainer)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (IllegalArgumentException | ReflectiveOperationException | SecurityException exception) {
                // empty catch block
            }
        }
        return result;
    }

    protected abstract WebSocketContainer getContainer();
}

