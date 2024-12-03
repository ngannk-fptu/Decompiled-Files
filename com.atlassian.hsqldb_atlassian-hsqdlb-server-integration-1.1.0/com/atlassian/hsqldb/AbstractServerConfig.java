/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Effect
 *  com.atlassian.fugue.Pair
 *  com.google.common.base.Supplier
 *  com.google.common.base.Throwables
 *  javax.annotation.concurrent.NotThreadSafe
 *  org.hsqldb.persist.HsqlProperties
 *  org.hsqldb.server.Server
 *  org.hsqldb.server.ServerAcl$AclFormatException
 *  org.hsqldb.server.ServerConfiguration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.hsqldb;

import com.atlassian.fugue.Effect;
import com.atlassian.fugue.Pair;
import com.atlassian.hsqldb.PrintLineEffect;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import javax.annotation.concurrent.NotThreadSafe;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerAcl;
import org.hsqldb.server.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NotThreadSafe
public abstract class AbstractServerConfig
implements Supplier<Server> {
    private Logger log = LoggerFactory.getLogger((String)this.getClass().getPackage().getName());
    private Pair<Integer, Boolean> port = new Pair((Object)ServerConfiguration.getDefaultPort((int)1, (boolean)false), (Object)Boolean.FALSE);
    private File accessControlList;

    public AbstractServerConfig setLog(Logger log) {
        this.log = log;
        return this;
    }

    public AbstractServerConfig setPort(int port, boolean required) {
        this.port = new Pair((Object)port, (Object)required);
        return this;
    }

    public void setAccessControlList(File accessControlList) {
        this.accessControlList = accessControlList;
    }

    public Server get() {
        Server server = new Server();
        server.setLogWriter((PrintWriter)new PrintLineEffect(new Effect<String>(){

            public void apply(String line) {
                AbstractServerConfig.this.log.info(line);
            }
        }));
        server.setErrWriter((PrintWriter)new PrintLineEffect(new Effect<String>(){

            public void apply(String line) {
                AbstractServerConfig.this.log.error(line);
            }
        }));
        try {
            server.setProperties(this.getProperties());
        }
        catch (IOException e) {
            Throwables.propagate((Throwable)e);
        }
        catch (ServerAcl.AclFormatException e) {
            Throwables.propagate((Throwable)e);
        }
        server.setPort((Boolean)this.port.right() != false ? (Integer)this.port.left() : AbstractServerConfig.pickFreePort((Integer)this.port.left()));
        server.setNoSystemExit(true);
        return server;
    }

    protected HsqlProperties getProperties() {
        HsqlProperties properties = new HsqlProperties();
        if (this.accessControlList != null) {
            properties.setProperty("server.acl", this.accessControlList.getAbsolutePath());
        }
        return properties;
    }

    /*
     * Loose catch block
     */
    private static int pickFreePort(int requestedPort) {
        int n;
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(requestedPort);
            n = requestedPort > 0 ? requestedPort : socket.getLocalPort();
        }
        catch (IOException e) {
            ServerSocket zeroSocket = null;
            zeroSocket = new ServerSocket(0);
            int n2 = zeroSocket.getLocalPort();
            AbstractServerConfig.closeSocket(zeroSocket);
            AbstractServerConfig.closeSocket(socket);
            return n2;
            {
                catch (IOException ex) {
                    try {
                        try {
                            throw new RuntimeException("Error opening socket", ex);
                        }
                        catch (Throwable throwable) {
                            AbstractServerConfig.closeSocket(zeroSocket);
                            throw throwable;
                        }
                    }
                    catch (Throwable throwable) {
                        AbstractServerConfig.closeSocket(socket);
                        throw throwable;
                    }
                }
            }
        }
        AbstractServerConfig.closeSocket(socket);
        return n;
    }

    private static void closeSocket(ServerSocket socket) {
        if (socket != null) {
            try {
                socket.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error closing socket", e);
            }
        }
    }
}

