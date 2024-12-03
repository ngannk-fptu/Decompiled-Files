/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.util.EventListener;
import javax.servlet.http.HttpSessionBindingEvent;

public interface HttpSessionBindingListener
extends EventListener {
    default public void valueBound(HttpSessionBindingEvent event) {
    }

    default public void valueUnbound(HttpSessionBindingEvent event) {
    }
}

