/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import java.util.EventListener;
import javax.servlet.ServletRequestEvent;

public interface ServletRequestListener
extends EventListener {
    default public void requestDestroyed(ServletRequestEvent sre) {
    }

    default public void requestInitialized(ServletRequestEvent sre) {
    }
}

