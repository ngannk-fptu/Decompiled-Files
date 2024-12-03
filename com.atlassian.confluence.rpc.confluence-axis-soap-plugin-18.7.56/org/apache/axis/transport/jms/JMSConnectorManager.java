/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.jms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.apache.axis.AxisFault;
import org.apache.axis.components.jms.JMSVendorAdapter;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.transport.jms.JMSConnector;
import org.apache.axis.transport.jms.JMSConnectorFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class JMSConnectorManager {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$jms$JMSConnectorManager == null ? (class$org$apache$axis$transport$jms$JMSConnectorManager = JMSConnectorManager.class$("org.apache.axis.transport.jms.JMSConnectorManager")) : class$org$apache$axis$transport$jms$JMSConnectorManager).getName());
    private static JMSConnectorManager s_instance = new JMSConnectorManager();
    private static HashMap vendorConnectorPools = new HashMap();
    private int DEFAULT_WAIT_FOR_SHUTDOWN = 90000;
    static /* synthetic */ Class class$org$apache$axis$transport$jms$JMSConnectorManager;

    private JMSConnectorManager() {
    }

    public static JMSConnectorManager getInstance() {
        return s_instance;
    }

    public ShareableObjectPool getVendorPool(String vendorId) {
        return (ShareableObjectPool)vendorConnectorPools.get(vendorId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JMSConnector getConnector(HashMap connectorProperties, HashMap connectionFactoryProperties, String username, String password, JMSVendorAdapter vendorAdapter) throws AxisFault {
        JMSConnector connector = null;
        try {
            Object object;
            ShareableObjectPool vendorConnectors = this.getVendorPool(vendorAdapter.getVendorId());
            if (vendorConnectors == null) {
                object = vendorConnectorPools;
                synchronized (object) {
                    vendorConnectors = this.getVendorPool(vendorAdapter.getVendorId());
                    if (vendorConnectors == null) {
                        vendorConnectors = new ShareableObjectPool();
                        vendorConnectorPools.put(vendorAdapter.getVendorId(), vendorConnectors);
                    }
                }
            }
            object = vendorConnectors;
            synchronized (object) {
                try {
                    connector = JMSConnectorFactory.matchConnector(vendorConnectors.getElements(), connectorProperties, connectionFactoryProperties, username, password, vendorAdapter);
                }
                catch (Exception e) {
                    // empty catch block
                }
                if (connector == null) {
                    connector = JMSConnectorFactory.createClientConnector(connectorProperties, connectionFactoryProperties, username, password, vendorAdapter);
                    connector.start();
                }
            }
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("cannotConnectError"), (Throwable)e);
            if (e instanceof AxisFault) {
                throw (AxisFault)e;
            }
            throw new AxisFault("cannotConnect", e);
        }
        return connector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void closeAllConnectors() {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: JMSConnectorManager::closeAllConnectors");
        }
        HashMap hashMap = vendorConnectorPools;
        synchronized (hashMap) {
            Iterator iter = vendorConnectorPools.values().iterator();
            while (iter.hasNext()) {
                ShareableObjectPool pool;
                ShareableObjectPool shareableObjectPool = pool = (ShareableObjectPool)iter.next();
                synchronized (shareableObjectPool) {
                    Iterator connectors = pool.getElements().iterator();
                    while (connectors.hasNext()) {
                        JMSConnector conn = (JMSConnector)connectors.next();
                        try {
                            this.reserve(conn);
                            this.closeConnector(conn);
                        }
                        catch (Exception e) {}
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: JMSConnectorManager::closeAllConnectors");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void closeMatchingJMSConnectors(HashMap connectorProps, HashMap cfProps, String username, String password, JMSVendorAdapter vendorAdapter) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: JMSConnectorManager::closeMatchingJMSConnectors");
        }
        try {
            String vendorId = vendorAdapter.getVendorId();
            ShareableObjectPool vendorConnectors = null;
            Object object = vendorConnectorPools;
            synchronized (object) {
                vendorConnectors = this.getVendorPool(vendorId);
            }
            if (vendorConnectors == null) {
                return;
            }
            object = vendorConnectors;
            synchronized (object) {
                JMSConnector connector = null;
                while (vendorConnectors.size() > 0 && (connector = JMSConnectorFactory.matchConnector(vendorConnectors.getElements(), connectorProps, cfProps, username, password, vendorAdapter)) != null) {
                    this.closeConnector(connector);
                }
            }
        }
        catch (Exception e) {
            log.warn((Object)Messages.getMessage("failedJMSConnectorShutdown"), (Throwable)e);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: JMSConnectorManager::closeMatchingJMSConnectors");
        }
    }

    private void closeConnector(JMSConnector conn) {
        conn.stop();
        conn.shutdown();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addConnectorToPool(JMSConnector conn) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: JMSConnectorManager::addConnectorToPool");
        }
        ShareableObjectPool vendorConnectors = null;
        Object object = vendorConnectorPools;
        synchronized (object) {
            String vendorId = conn.getVendorAdapter().getVendorId();
            vendorConnectors = this.getVendorPool(vendorId);
            if (vendorConnectors == null) {
                vendorConnectors = new ShareableObjectPool();
                vendorConnectorPools.put(vendorId, vendorConnectors);
            }
        }
        object = vendorConnectors;
        synchronized (object) {
            vendorConnectors.addObject(conn);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: JMSConnectorManager::addConnectorToPool");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeConnectorFromPool(JMSConnector conn) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: JMSConnectorManager::removeConnectorFromPool");
        }
        ShareableObjectPool vendorConnectors = null;
        Object object = vendorConnectorPools;
        synchronized (object) {
            vendorConnectors = this.getVendorPool(conn.getVendorAdapter().getVendorId());
        }
        if (vendorConnectors == null) {
            return;
        }
        object = vendorConnectors;
        synchronized (object) {
            vendorConnectors.release(conn);
            vendorConnectors.removeObject(conn);
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: JMSConnectorManager::removeConnectorFromPool");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reserve(JMSConnector connector) throws Exception {
        ShareableObjectPool pool = null;
        HashMap hashMap = vendorConnectorPools;
        synchronized (hashMap) {
            pool = this.getVendorPool(connector.getVendorAdapter().getVendorId());
        }
        if (pool != null) {
            pool.reserve(connector);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void release(JMSConnector connector) {
        ShareableObjectPool pool = null;
        HashMap hashMap = vendorConnectorPools;
        synchronized (hashMap) {
            pool = this.getVendorPool(connector.getVendorAdapter().getVendorId());
        }
        if (pool != null) {
            pool.release(connector);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public class ShareableObjectPool {
        private HashMap m_elements = new HashMap();
        private HashMap m_expiring = new HashMap();
        private int m_numElements = 0;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addObject(Object obj) {
            ReferenceCountedObject ref = new ReferenceCountedObject(obj);
            HashMap hashMap = this.m_elements;
            synchronized (hashMap) {
                if (!this.m_elements.containsKey(obj) && !this.m_expiring.containsKey(obj)) {
                    this.m_elements.put(obj, ref);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void removeObject(Object obj, long waitTime) {
            ReferenceCountedObject ref = null;
            HashMap hashMap = this.m_elements;
            synchronized (hashMap) {
                ref = (ReferenceCountedObject)this.m_elements.get(obj);
                if (ref == null) {
                    return;
                }
                this.m_elements.remove(obj);
                if (ref.count() == 0) {
                    return;
                }
                this.m_expiring.put(obj, ref);
            }
            long expiration = System.currentTimeMillis() + waitTime;
            while (ref.count() > 0) {
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
                if (System.currentTimeMillis() <= expiration) continue;
            }
            this.m_expiring.remove(obj);
        }

        public void removeObject(Object obj) {
            this.removeObject(obj, JMSConnectorManager.this.DEFAULT_WAIT_FOR_SHUTDOWN);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void reserve(Object obj) throws Exception {
            HashMap hashMap = this.m_elements;
            synchronized (hashMap) {
                if (this.m_expiring.containsKey(obj)) {
                    throw new Exception("resourceUnavailable");
                }
                ReferenceCountedObject ref = (ReferenceCountedObject)this.m_elements.get(obj);
                ref.increment();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void release(Object obj) {
            HashMap hashMap = this.m_elements;
            synchronized (hashMap) {
                ReferenceCountedObject ref = (ReferenceCountedObject)this.m_elements.get(obj);
                ref.decrement();
            }
        }

        public synchronized Set getElements() {
            return this.m_elements.keySet();
        }

        public synchronized int size() {
            return this.m_elements.size();
        }

        public class ReferenceCountedObject {
            private Object m_object;
            private int m_refCount;

            public ReferenceCountedObject(Object obj) {
                this.m_object = obj;
                this.m_refCount = 0;
            }

            public synchronized void increment() {
                ++this.m_refCount;
            }

            public synchronized void decrement() {
                if (this.m_refCount > 0) {
                    --this.m_refCount;
                }
            }

            public synchronized int count() {
                return this.m_refCount;
            }
        }
    }
}

