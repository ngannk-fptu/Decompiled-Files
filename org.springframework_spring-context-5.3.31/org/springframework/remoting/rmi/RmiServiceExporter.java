/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.remoting.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.rmi.RmiBasedExporter;

@Deprecated
public class RmiServiceExporter
extends RmiBasedExporter
implements InitializingBean,
DisposableBean {
    private String serviceName;
    private int servicePort = 0;
    private RMIClientSocketFactory clientSocketFactory;
    private RMIServerSocketFactory serverSocketFactory;
    private Registry registry;
    private String registryHost;
    private int registryPort = 1099;
    private RMIClientSocketFactory registryClientSocketFactory;
    private RMIServerSocketFactory registryServerSocketFactory;
    private boolean alwaysCreateRegistry = false;
    private boolean replaceExistingBinding = true;
    private Remote exportedObject;
    private boolean createdRegistry = false;

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public void setClientSocketFactory(RMIClientSocketFactory clientSocketFactory) {
        this.clientSocketFactory = clientSocketFactory;
    }

    public void setServerSocketFactory(RMIServerSocketFactory serverSocketFactory) {
        this.serverSocketFactory = serverSocketFactory;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setRegistryHost(String registryHost) {
        this.registryHost = registryHost;
    }

    public void setRegistryPort(int registryPort) {
        this.registryPort = registryPort;
    }

    public void setRegistryClientSocketFactory(RMIClientSocketFactory registryClientSocketFactory) {
        this.registryClientSocketFactory = registryClientSocketFactory;
    }

    public void setRegistryServerSocketFactory(RMIServerSocketFactory registryServerSocketFactory) {
        this.registryServerSocketFactory = registryServerSocketFactory;
    }

    public void setAlwaysCreateRegistry(boolean alwaysCreateRegistry) {
        this.alwaysCreateRegistry = alwaysCreateRegistry;
    }

    public void setReplaceExistingBinding(boolean replaceExistingBinding) {
        this.replaceExistingBinding = replaceExistingBinding;
    }

    public void afterPropertiesSet() throws RemoteException {
        this.prepare();
    }

    public void prepare() throws RemoteException {
        this.checkService();
        if (this.serviceName == null) {
            throw new IllegalArgumentException("Property 'serviceName' is required");
        }
        if (this.clientSocketFactory instanceof RMIServerSocketFactory) {
            this.serverSocketFactory = (RMIServerSocketFactory)((Object)this.clientSocketFactory);
        }
        if (this.clientSocketFactory != null && this.serverSocketFactory == null || this.clientSocketFactory == null && this.serverSocketFactory != null) {
            throw new IllegalArgumentException("Both RMIClientSocketFactory and RMIServerSocketFactory or none required");
        }
        if (this.registryClientSocketFactory instanceof RMIServerSocketFactory) {
            this.registryServerSocketFactory = (RMIServerSocketFactory)((Object)this.registryClientSocketFactory);
        }
        if (this.registryClientSocketFactory == null && this.registryServerSocketFactory != null) {
            throw new IllegalArgumentException("RMIServerSocketFactory without RMIClientSocketFactory for registry not supported");
        }
        this.createdRegistry = false;
        if (this.registry == null) {
            this.registry = this.getRegistry(this.registryHost, this.registryPort, this.registryClientSocketFactory, this.registryServerSocketFactory);
            this.createdRegistry = true;
        }
        this.exportedObject = this.getObjectToExport();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Binding service '" + this.serviceName + "' to RMI registry: " + this.registry));
        }
        if (this.clientSocketFactory != null) {
            UnicastRemoteObject.exportObject(this.exportedObject, this.servicePort, this.clientSocketFactory, this.serverSocketFactory);
        } else {
            UnicastRemoteObject.exportObject(this.exportedObject, this.servicePort);
        }
        try {
            if (this.replaceExistingBinding) {
                this.registry.rebind(this.serviceName, this.exportedObject);
            } else {
                this.registry.bind(this.serviceName, this.exportedObject);
            }
        }
        catch (AlreadyBoundException ex) {
            this.unexportObjectSilently();
            throw new IllegalStateException("Already an RMI object bound for name '" + this.serviceName + "': " + ex.toString());
        }
        catch (RemoteException ex) {
            this.unexportObjectSilently();
            throw ex;
        }
    }

    protected Registry getRegistry(String registryHost, int registryPort, @Nullable RMIClientSocketFactory clientSocketFactory, @Nullable RMIServerSocketFactory serverSocketFactory) throws RemoteException {
        if (registryHost != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Looking for RMI registry at port '" + registryPort + "' of host [" + registryHost + "]"));
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
            if (this.alwaysCreateRegistry) {
                this.logger.debug((Object)"Creating new RMI registry");
                return LocateRegistry.createRegistry(registryPort, clientSocketFactory, serverSocketFactory);
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Looking for RMI registry at port '" + registryPort + "', using custom socket factory"));
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
                    this.logger.trace((Object)"RMI registry access threw exception", (Throwable)ex);
                    this.logger.debug((Object)"Could not detect RMI registry - creating new one");
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
        if (this.alwaysCreateRegistry) {
            this.logger.debug((Object)"Creating new RMI registry");
            return LocateRegistry.createRegistry(registryPort);
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Looking for RMI registry at port '" + registryPort + "'"));
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
                this.logger.trace((Object)"RMI registry access threw exception", (Throwable)ex);
                this.logger.debug((Object)"Could not detect RMI registry - creating new one");
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return LocateRegistry.createRegistry(registryPort);
            }
        }
    }

    protected void testRegistry(Registry registry) throws RemoteException {
        registry.list();
    }

    public void destroy() throws RemoteException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Unbinding RMI service '" + this.serviceName + "' from registry" + (this.createdRegistry ? " at port '" + this.registryPort + "'" : "")));
        }
        try {
            this.registry.unbind(this.serviceName);
        }
        catch (NotBoundException ex) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info((Object)("RMI service '" + this.serviceName + "' is not bound to registry" + (this.createdRegistry ? " at port '" + this.registryPort + "' anymore" : "")), (Throwable)ex);
            }
        }
        finally {
            this.unexportObjectSilently();
        }
    }

    private void unexportObjectSilently() {
        block2: {
            try {
                UnicastRemoteObject.unexportObject(this.exportedObject, true);
            }
            catch (NoSuchObjectException ex) {
                if (!this.logger.isInfoEnabled()) break block2;
                this.logger.info((Object)("RMI object for service '" + this.serviceName + "' is not exported anymore"), (Throwable)ex);
            }
        }
    }
}

