/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class EventDispatcher {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void fireEvent(final Dispatcher dispatcher, final List listeners, final Object evt, final boolean useEventQueue) {
        if (useEventQueue && !EventQueue.isDispatchThread()) {
            Runnable r = new Runnable(){

                @Override
                public void run() {
                    EventDispatcher.fireEvent(dispatcher, listeners, evt, useEventQueue);
                }
            };
            try {
                EventQueue.invokeAndWait(r);
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
            }
            catch (ThreadDeath td) {
                throw td;
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            return;
        }
        Object[] ll = null;
        Throwable err = null;
        int retryCount = 10;
        while (--retryCount != 0) {
            try {
                List list = listeners;
                synchronized (list) {
                    if (listeners.size() == 0) {
                        return;
                    }
                    ll = listeners.toArray();
                    break;
                }
            }
            catch (Throwable t) {
                err = t;
            }
        }
        if (ll == null) {
            if (err != null) {
                err.printStackTrace();
            }
            return;
        }
        EventDispatcher.dispatchEvent(dispatcher, ll, evt);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    protected static void dispatchEvent(Dispatcher dispatcher, Object[] ll, Object evt) {
        ThreadDeath td = null;
        try {
            for (int i = 0; i < ll.length; ++i) {
                try {
                    Object[] objectArray = ll;
                    // MONITORENTER : ll
                    Object l = ll[i];
                    if (l == null) {
                        // MONITOREXIT : objectArray
                        continue;
                    }
                    ll[i] = null;
                    // MONITOREXIT : objectArray
                    dispatcher.dispatch(l, evt);
                    continue;
                }
                catch (ThreadDeath t) {
                    td = t;
                    continue;
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        catch (ThreadDeath t) {
            td = t;
        }
        catch (Throwable t) {
            if (ll[ll.length - 1] != null) {
                EventDispatcher.dispatchEvent(dispatcher, ll, evt);
            }
            t.printStackTrace();
        }
        if (td == null) return;
        throw td;
    }

    public static interface Dispatcher {
        public void dispatch(Object var1, Object var2);
    }
}

