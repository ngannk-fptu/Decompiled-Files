/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.coyote.Adapter;
import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.ajp.AjpAprProtocol;
import org.apache.coyote.ajp.AjpNioProtocol;
import org.apache.coyote.http11.Http11AprProtocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.net.SSLHostConfig;

public interface ProtocolHandler {
    public Adapter getAdapter();

    public void setAdapter(Adapter var1);

    public Executor getExecutor();

    public void setExecutor(Executor var1);

    public ScheduledExecutorService getUtilityExecutor();

    public void setUtilityExecutor(ScheduledExecutorService var1);

    public void init() throws Exception;

    public void start() throws Exception;

    public void pause() throws Exception;

    public void resume() throws Exception;

    public void stop() throws Exception;

    public void destroy() throws Exception;

    public void closeServerSocketGraceful();

    public long awaitConnectionsClose(long var1);

    @Deprecated
    public boolean isAprRequired();

    public boolean isSendfileSupported();

    public void addSslHostConfig(SSLHostConfig var1);

    public void addSslHostConfig(SSLHostConfig var1, boolean var2);

    public SSLHostConfig[] findSslHostConfigs();

    public void addUpgradeProtocol(UpgradeProtocol var1);

    public UpgradeProtocol[] findUpgradeProtocols();

    default public int getDesiredBufferSize() {
        return -1;
    }

    default public String getId() {
        return null;
    }

    public static ProtocolHandler create(String protocol, boolean apr) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        if (protocol == null || "HTTP/1.1".equals(protocol) || !apr && Http11NioProtocol.class.getName().equals(protocol) || apr && Http11AprProtocol.class.getName().equals(protocol)) {
            if (apr) {
                return new Http11AprProtocol();
            }
            return new Http11NioProtocol();
        }
        if ("AJP/1.3".equals(protocol) || !apr && AjpNioProtocol.class.getName().equals(protocol) || apr && AjpAprProtocol.class.getName().equals(protocol)) {
            if (apr) {
                return new AjpAprProtocol();
            }
            return new AjpNioProtocol();
        }
        Class<?> clazz = Class.forName(protocol);
        return (ProtocolHandler)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
    }
}

