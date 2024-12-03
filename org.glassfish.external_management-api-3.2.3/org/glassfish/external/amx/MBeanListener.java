/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.amx;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.glassfish.external.amx.AMXUtil;
import org.glassfish.external.arc.Stability;
import org.glassfish.external.arc.Taxonomy;

@Taxonomy(stability=Stability.UNCOMMITTED)
public class MBeanListener<T extends Callback>
implements NotificationListener {
    private final String mJMXDomain;
    private final String mType;
    private final String mName;
    private final ObjectName mObjectName;
    private final MBeanServerConnection mMBeanServer;
    private final T mCallback;

    private static void debug(Object o) {
        System.out.println("" + o);
    }

    public String toString() {
        return "MBeanListener: ObjectName=" + this.mObjectName + ", type=" + this.mType + ", name=" + this.mName;
    }

    public String getType() {
        return this.mType;
    }

    public String getName() {
        return this.mName;
    }

    public MBeanServerConnection getMBeanServer() {
        return this.mMBeanServer;
    }

    public T getCallback() {
        return this.mCallback;
    }

    public MBeanListener(MBeanServerConnection server, ObjectName objectName, T callback) {
        this.mMBeanServer = server;
        this.mObjectName = objectName;
        this.mJMXDomain = null;
        this.mType = null;
        this.mName = null;
        this.mCallback = callback;
    }

    public MBeanListener(MBeanServerConnection server, String domain, String type, T callback) {
        this(server, domain, type, null, callback);
    }

    public MBeanListener(MBeanServerConnection server, String domain, String type, String name, T callback) {
        this.mMBeanServer = server;
        this.mJMXDomain = domain;
        this.mType = type;
        this.mName = name;
        this.mObjectName = null;
        this.mCallback = callback;
    }

    private boolean isRegistered(MBeanServerConnection conn, ObjectName objectName) {
        try {
            return conn.isRegistered(objectName);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startListening() {
        try {
            this.mMBeanServer.addNotificationListener(AMXUtil.getMBeanServerDelegateObjectName(), this, null, (Object)this);
        }
        catch (Exception e) {
            throw new RuntimeException("Can't add NotificationListener", e);
        }
        if (this.mObjectName != null) {
            if (this.isRegistered(this.mMBeanServer, this.mObjectName)) {
                this.mCallback.mbeanRegistered(this.mObjectName, this);
            }
        } else {
            String props = "type=" + this.mType;
            if (this.mName != null) {
                props = props + "," + "name" + this.mName;
            }
            ObjectName pattern = AMXUtil.newObjectName(this.mJMXDomain + ":" + props);
            try {
                Set<ObjectName> matched = this.mMBeanServer.queryNames(pattern, null);
                for (ObjectName objectName : matched) {
                    this.mCallback.mbeanRegistered(objectName, this);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stopListening() {
        try {
            this.mMBeanServer.removeNotificationListener(AMXUtil.getMBeanServerDelegateObjectName(), this);
        }
        catch (Exception e) {
            throw new RuntimeException("Can't remove NotificationListener " + this, e);
        }
    }

    @Override
    public void handleNotification(Notification notifIn, Object handback) {
        if (notifIn instanceof MBeanServerNotification) {
            MBeanServerNotification notif = (MBeanServerNotification)notifIn;
            ObjectName objectName = notif.getMBeanName();
            boolean match = false;
            if (this.mObjectName != null && this.mObjectName.equals(objectName)) {
                match = true;
            } else if (objectName.getDomain().equals(this.mJMXDomain) && this.mType != null && this.mType.equals(objectName.getKeyProperty("type"))) {
                String mbeanName = objectName.getKeyProperty("name");
                if (this.mName != null && this.mName.equals(mbeanName)) {
                    match = true;
                }
            }
            if (match) {
                String notifType = notif.getType();
                if ("JMX.mbean.registered".equals(notifType)) {
                    this.mCallback.mbeanRegistered(objectName, this);
                } else if ("JMX.mbean.unregistered".equals(notifType)) {
                    this.mCallback.mbeanUnregistered(objectName, this);
                }
            }
        }
    }

    public static class CallbackImpl
    implements Callback {
        private volatile ObjectName mRegistered = null;
        private volatile ObjectName mUnregistered = null;
        private final boolean mStopAtFirst;
        protected final CountDownLatch mLatch = new CountDownLatch(1);

        public CallbackImpl() {
            this(true);
        }

        public CallbackImpl(boolean stopAtFirst) {
            this.mStopAtFirst = stopAtFirst;
        }

        public ObjectName getRegistered() {
            return this.mRegistered;
        }

        public ObjectName getUnregistered() {
            return this.mUnregistered;
        }

        public void await() {
            try {
                this.mLatch.await();
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void mbeanRegistered(ObjectName objectName, MBeanListener listener) {
            this.mRegistered = objectName;
            if (this.mStopAtFirst) {
                listener.stopListening();
            }
        }

        @Override
        public void mbeanUnregistered(ObjectName objectName, MBeanListener listener) {
            this.mUnregistered = objectName;
            if (this.mStopAtFirst) {
                listener.stopListening();
            }
        }
    }

    public static interface Callback {
        public void mbeanRegistered(ObjectName var1, MBeanListener var2);

        public void mbeanUnregistered(ObjectName var1, MBeanListener var2);
    }
}

