/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.util.EventListener;
import javax.servlet.http.HttpSessionEvent;

public interface HttpSessionActivationListener
extends EventListener {
    default public void sessionWillPassivate(HttpSessionEvent se) {
    }

    default public void sessionDidActivate(HttpSessionEvent se) {
    }
}

