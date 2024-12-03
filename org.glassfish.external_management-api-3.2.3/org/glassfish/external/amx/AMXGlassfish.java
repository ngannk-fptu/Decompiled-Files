/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.amx;

import java.io.IOException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.glassfish.external.amx.AMXUtil;
import org.glassfish.external.amx.MBeanListener;

public final class AMXGlassfish {
    public static final String DEFAULT_JMX_DOMAIN = "amx";
    public static final AMXGlassfish DEFAULT = new AMXGlassfish("amx");
    private final String mJMXDomain;
    private final ObjectName mDomainRoot;

    public AMXGlassfish(String jmxDomain) {
        this.mJMXDomain = jmxDomain;
        this.mDomainRoot = this.newObjectName("", "domain-root", null);
    }

    public static String getGlassfishVersion() {
        String version = System.getProperty("glassfish.version");
        return version;
    }

    public String amxJMXDomain() {
        return this.mJMXDomain;
    }

    public String amxSupportDomain() {
        return this.amxJMXDomain() + "-support";
    }

    public String dasName() {
        return "server";
    }

    public String dasConfig() {
        return this.dasName() + "-config";
    }

    public ObjectName domainRoot() {
        return this.mDomainRoot;
    }

    public ObjectName monitoringRoot() {
        return this.newObjectName("/", "mon", null);
    }

    public ObjectName serverMon(String serverName) {
        return this.newObjectName("/mon", "server-mon", serverName);
    }

    public ObjectName serverMonForDAS() {
        return this.serverMon("server");
    }

    public ObjectName newObjectName(String pp, String type, String name) {
        String props = AMXGlassfish.prop("pp", pp) + "," + AMXGlassfish.prop("type", type);
        if (name != null) {
            props = props + "," + AMXGlassfish.prop("name", name);
        }
        return this.newObjectName(props);
    }

    public ObjectName newObjectName(String s) {
        String name = s;
        if (!name.startsWith(this.amxJMXDomain())) {
            name = this.amxJMXDomain() + ":" + name;
        }
        return AMXUtil.newObjectName(name);
    }

    private static String prop(String key, String value) {
        return key + "=" + value;
    }

    public ObjectName getBootAMXMBeanObjectName() {
        return AMXUtil.newObjectName(this.amxSupportDomain() + ":type=boot-amx");
    }

    public void invokeBootAMX(MBeanServerConnection conn) {
        try {
            conn.invoke(this.getBootAMXMBeanObjectName(), "bootAMX", null, null);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void invokeWaitAMXReady(MBeanServerConnection conn, ObjectName objectName) {
        try {
            conn.invoke(objectName, "waitAMXReady", null, null);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends MBeanListener.Callback> MBeanListener<T> listenForDomainRoot(MBeanServerConnection server, T callback) {
        MBeanListener<T> listener = new MBeanListener<T>(server, this.domainRoot(), callback);
        listener.startListening();
        return listener;
    }

    public ObjectName waitAMXReady(MBeanServerConnection server) {
        WaitForDomainRootListenerCallback callback = new WaitForDomainRootListenerCallback(server);
        this.listenForDomainRoot(server, callback);
        callback.await();
        return callback.getRegistered();
    }

    public <T extends MBeanListener.Callback> MBeanListener<T> listenForBootAMX(MBeanServerConnection server, T callback) {
        MBeanListener<T> listener = new MBeanListener<T>(server, this.getBootAMXMBeanObjectName(), callback);
        listener.startListening();
        return listener;
    }

    public ObjectName bootAMX(MBeanServerConnection conn) throws IOException {
        ObjectName domainRoot = this.domainRoot();
        if (!conn.isRegistered(domainRoot)) {
            BootAMXCallback callback = new BootAMXCallback(conn);
            this.listenForBootAMX(conn, callback);
            callback.await();
            this.invokeBootAMX(conn);
            WaitForDomainRootListenerCallback drCallback = new WaitForDomainRootListenerCallback(conn);
            this.listenForDomainRoot(conn, drCallback);
            drCallback.await();
            AMXGlassfish.invokeWaitAMXReady(conn, domainRoot);
        } else {
            AMXGlassfish.invokeWaitAMXReady(conn, domainRoot);
        }
        return domainRoot;
    }

    public ObjectName bootAMX(MBeanServer server) {
        try {
            return this.bootAMX((MBeanServerConnection)server);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class BootAMXCallback
    extends MBeanListener.CallbackImpl {
        private final MBeanServerConnection mConn;

        public BootAMXCallback(MBeanServerConnection conn) {
            this.mConn = conn;
        }

        @Override
        public void mbeanRegistered(ObjectName objectName, MBeanListener listener) {
            super.mbeanRegistered(objectName, listener);
            this.mLatch.countDown();
        }
    }

    private static final class WaitForDomainRootListenerCallback
    extends MBeanListener.CallbackImpl {
        private final MBeanServerConnection mConn;

        public WaitForDomainRootListenerCallback(MBeanServerConnection conn) {
            this.mConn = conn;
        }

        @Override
        public void mbeanRegistered(ObjectName objectName, MBeanListener listener) {
            super.mbeanRegistered(objectName, listener);
            AMXGlassfish.invokeWaitAMXReady(this.mConn, objectName);
            this.mLatch.countDown();
        }
    }
}

