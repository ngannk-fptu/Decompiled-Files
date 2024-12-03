/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import java.util.EventListener;
import javax.servlet.ServletContextAttributeEvent;

public interface ServletContextAttributeListener
extends EventListener {
    default public void attributeAdded(ServletContextAttributeEvent scae) {
    }

    default public void attributeRemoved(ServletContextAttributeEvent scae) {
    }

    default public void attributeReplaced(ServletContextAttributeEvent scae) {
    }
}

