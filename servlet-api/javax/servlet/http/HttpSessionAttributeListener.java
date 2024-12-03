/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import java.util.EventListener;
import javax.servlet.http.HttpSessionBindingEvent;

public interface HttpSessionAttributeListener
extends EventListener {
    default public void attributeAdded(HttpSessionBindingEvent se) {
    }

    default public void attributeRemoved(HttpSessionBindingEvent se) {
    }

    default public void attributeReplaced(HttpSessionBindingEvent se) {
    }
}

