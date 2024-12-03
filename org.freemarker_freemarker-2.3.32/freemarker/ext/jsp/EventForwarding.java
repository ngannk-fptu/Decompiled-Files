/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextAttributeEvent
 *  javax.servlet.ServletContextAttributeListener
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  javax.servlet.http.HttpSessionAttributeListener
 *  javax.servlet.http.HttpSessionBindingEvent
 *  javax.servlet.http.HttpSessionEvent
 *  javax.servlet.http.HttpSessionListener
 */
package freemarker.ext.jsp;

import freemarker.log.Logger;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class EventForwarding
implements ServletContextAttributeListener,
ServletContextListener,
HttpSessionListener,
HttpSessionAttributeListener {
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");
    private static final String ATTR_NAME = EventForwarding.class.getName();
    private final List servletContextAttributeListeners = new ArrayList();
    private final List servletContextListeners = new ArrayList();
    private final List httpSessionAttributeListeners = new ArrayList();
    private final List httpSessionListeners = new ArrayList();

    void addListeners(List listeners) {
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            this.addListener((EventListener)iter.next());
        }
    }

    private void addListener(EventListener listener) {
        boolean added = false;
        if (listener instanceof ServletContextAttributeListener) {
            this.addListener(this.servletContextAttributeListeners, listener);
            added = true;
        }
        if (listener instanceof ServletContextListener) {
            this.addListener(this.servletContextListeners, listener);
            added = true;
        }
        if (listener instanceof HttpSessionAttributeListener) {
            this.addListener(this.httpSessionAttributeListeners, listener);
            added = true;
        }
        if (listener instanceof HttpSessionListener) {
            this.addListener(this.httpSessionListeners, listener);
            added = true;
        }
        if (!added) {
            LOG.warn("Listener of class " + listener.getClass().getName() + "wasn't registered as it doesn't implement any of the recognized listener interfaces.");
        }
    }

    static EventForwarding getInstance(ServletContext context) {
        return (EventForwarding)context.getAttribute(ATTR_NAME);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addListener(List listeners, EventListener listener) {
        List list = listeners;
        synchronized (list) {
            listeners.add(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void attributeAdded(ServletContextAttributeEvent arg0) {
        List list = this.servletContextAttributeListeners;
        synchronized (list) {
            int s = this.servletContextAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((ServletContextAttributeListener)this.servletContextAttributeListeners.get(i)).attributeAdded(arg0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void attributeRemoved(ServletContextAttributeEvent arg0) {
        List list = this.servletContextAttributeListeners;
        synchronized (list) {
            int s = this.servletContextAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((ServletContextAttributeListener)this.servletContextAttributeListeners.get(i)).attributeRemoved(arg0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void attributeReplaced(ServletContextAttributeEvent arg0) {
        List list = this.servletContextAttributeListeners;
        synchronized (list) {
            int s = this.servletContextAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((ServletContextAttributeListener)this.servletContextAttributeListeners.get(i)).attributeReplaced(arg0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void contextInitialized(ServletContextEvent arg0) {
        arg0.getServletContext().setAttribute(ATTR_NAME, (Object)this);
        List list = this.servletContextListeners;
        synchronized (list) {
            int s = this.servletContextListeners.size();
            for (int i = 0; i < s; ++i) {
                ((ServletContextListener)this.servletContextListeners.get(i)).contextInitialized(arg0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        List list = this.servletContextListeners;
        synchronized (list) {
            int s = this.servletContextListeners.size();
            for (int i = s - 1; i >= 0; --i) {
                ((ServletContextListener)this.servletContextListeners.get(i)).contextDestroyed(arg0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sessionCreated(HttpSessionEvent arg0) {
        List list = this.httpSessionListeners;
        synchronized (list) {
            int s = this.httpSessionListeners.size();
            for (int i = 0; i < s; ++i) {
                ((HttpSessionListener)this.httpSessionListeners.get(i)).sessionCreated(arg0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sessionDestroyed(HttpSessionEvent arg0) {
        List list = this.httpSessionListeners;
        synchronized (list) {
            int s = this.httpSessionListeners.size();
            for (int i = s - 1; i >= 0; --i) {
                ((HttpSessionListener)this.httpSessionListeners.get(i)).sessionDestroyed(arg0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void attributeAdded(HttpSessionBindingEvent arg0) {
        List list = this.httpSessionAttributeListeners;
        synchronized (list) {
            int s = this.httpSessionAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((HttpSessionAttributeListener)this.httpSessionAttributeListeners.get(i)).attributeAdded(arg0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void attributeRemoved(HttpSessionBindingEvent arg0) {
        List list = this.httpSessionAttributeListeners;
        synchronized (list) {
            int s = this.httpSessionAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((HttpSessionAttributeListener)this.httpSessionAttributeListeners.get(i)).attributeRemoved(arg0);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void attributeReplaced(HttpSessionBindingEvent arg0) {
        List list = this.httpSessionAttributeListeners;
        synchronized (list) {
            int s = this.httpSessionAttributeListeners.size();
            for (int i = 0; i < s; ++i) {
                ((HttpSessionAttributeListener)this.httpSessionAttributeListeners.get(i)).attributeReplaced(arg0);
            }
        }
    }
}

