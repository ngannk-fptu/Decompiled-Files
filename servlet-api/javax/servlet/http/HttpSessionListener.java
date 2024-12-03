/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.util.EventListener;
import javax.servlet.http.HttpSessionEvent;

public interface HttpSessionListener
extends EventListener {
    default public void sessionCreated(HttpSessionEvent se) {
    }

    default public void sessionDestroyed(HttpSessionEvent se) {
    }
}

