/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSessionEvent
 *  javax.servlet.http.HttpSessionListener
 */
package org.springframework.web.util;

import java.io.Serializable;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.springframework.web.util.WebUtils;

public class HttpSessionMutexListener
implements HttpSessionListener {
    public void sessionCreated(HttpSessionEvent event) {
        event.getSession().setAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE, (Object)new Mutex());
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        event.getSession().removeAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE);
    }

    private static class Mutex
    implements Serializable {
        private Mutex() {
        }
    }
}

