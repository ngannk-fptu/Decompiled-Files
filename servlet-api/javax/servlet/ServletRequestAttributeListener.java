/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet;

import java.util.EventListener;
import javax.servlet.ServletRequestAttributeEvent;

public interface ServletRequestAttributeListener
extends EventListener {
    default public void attributeAdded(ServletRequestAttributeEvent srae) {
    }

    default public void attributeRemoved(ServletRequestAttributeEvent srae) {
    }

    default public void attributeReplaced(ServletRequestAttributeEvent srae) {
    }
}

