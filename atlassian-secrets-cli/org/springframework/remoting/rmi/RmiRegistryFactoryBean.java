/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

public class RmiRegistryFactoryBean
implements FactoryBean<Registry>,
InitializingBean,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String host;
    private int port = 1099;
    private RMIClientSocketFactory clientSocketFactory;
    private RMIServerSocketFactory serverSocketFactory;
    private Registry registry;
    private boolean alwaysCreate = false;
    private boolean created = false;

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public void setClientSocketFactory(RMIClientSocketFactory clientSocketFactory) {
        this.clientSocketFactory = clientSocketFactory;
    }

    public void setServerSocketFactory(RMIServerSocketFactory serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
    }

    public void setAlwaysCreate(boolean alwaysCreate) {
        this.alwaysCreate = alwaysCreate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.clientSocketFactory instanceof RMIServerSocketFactory) {
            this.serverSocketFactory = (RMIServerSocketFactory)((Object)this.clientSocketFactory);
        }
        if (this.clientSocketFactory != null && this.serverSocketFactory == null || this.clientSocketFactory == null && this.serverSocketFactory != null) {
            throw new IllegalArgumentException("Both RMIClientSocketFactory and RMIServerSocketFactory or none required");
        }
        this.registry = this.getRegistry(this.host, this.port, this.clientSocketFactory, this.serverSocketFactory);
    }

    protected Registry getRegistry(String registryHost, int registryPort, @Nullable RMIClientSocketFactory clientSocketFactory, @Nullable RMIServerSocketFactory serverSocketFactory) throws RemoteException {
        if (registryHost != null) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Looking for RMI registry at port '" + registryPort + "' of host [" + registryHost + "]");
            }
            Registry reg = LocateRegistry.getRegistry(registryHost, registryPort, clientSocketFactory);
            this.testRegistry(reg);
            return reg;
        }
        return this.getRegistry(registryPort, clientSocketFactory, serverSocketFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Registry getRegistry(int registryPort, @Nullable RMIClientSocketFactory clientSocketFactory, @Nullable RMIServerSocketFactory serverSocketFactory) throws RemoteException {
        if (clientSocketFactory != null) {
            if (this.alwaysCreate) {
                this.logger.info("Creating new RMI registry");
                this.created = true;
                return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
            }
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Looking for RMI registry at port '" + registryPort + "', using custom socket factory");
            }
            Class<LocateRegistry> clazz = LocateRegistry.class;
            synchronized (LocateRegistry.class) {
                try {
                    Registry reg = LocateRegistry.getRegistry(null, registryPort, clientSocketFactory);
                    this.testRegistry(reg);
                    // ** MonitorExit[var4_4] (shouldn't be in output)
                    return reg;
                }
                catch (RemoteException ex) {
                    this.logger.debug("RMI registry access threw exception", ex);
                    this.logger.info("Could not detect RMI registry - creating new one");
                    this.created = true;
                    // ** MonitorExit[var4_4] (shouldn't be in output)
                    return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
                }
            }
        }
        return this.getRegistry(registryPort);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Registry getRegistry(int registryPort) throws RemoteException {
        if (this.alwaysCreate) {
            this.logger.info("Creating new RMI registry");
            this.created = true;
            return LocateRegistry.createRegistry(registryPort);
        }
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Looking for RMI registry at port '" + registryPort + "'");
        }
        Class<LocateRegistry> clazz = LocateRegistry.class;
        synchronized (LocateRegistry.class) {
            try {
                Registry reg = LocateRegistry.getRegistry(registryPort);
                this.testRegistry(reg);
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return reg;
            }
            catch (RemoteException ex) {
                this.logger.debug("RMI registry access threw exception", ex);
                this.logger.info("Could not detect RMI registry - creating new one");
                this.created = true;
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return LocateRegistry.createRegistry(registryPort);
            }
        }
    }

    protected void testRegistry(Registry registry) throws RemoteException {
        registry.list();
    }

    @Override
    public Registry getObject() throws Exception {
        return this.registry;
    }

    @Override
    public Class<? extends Registry> getObjectType() {
        return this.registry != null ? this.registry.getClass() : Registry.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws RemoteException {
        if (this.created) {
            this.logger.info("Unexporting RMI registry");
            UnicastRemoteObject.unexportObject(this.registry, true);
        }
    }
}

