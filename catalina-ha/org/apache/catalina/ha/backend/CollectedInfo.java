/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.modeler.Registry
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.ha.backend;

import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

public class CollectedInfo {
    private static final StringManager sm = StringManager.getManager(CollectedInfo.class);
    protected MBeanServer mBeanServer = null;
    protected ObjectName objName = null;
    int ready;
    int busy;
    int port = 0;
    String host = null;

    public CollectedInfo(String host, int port) throws Exception {
        this.init(host, port);
    }

    public void init(String host, int port) throws Exception {
        int iport = 0;
        String shost = null;
        this.mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        String onStr = "*:type=ThreadPool,*";
        ObjectName objectName = new ObjectName(onStr);
        Set<ObjectInstance> set = this.mBeanServer.queryMBeans(objectName, null);
        for (ObjectInstance oi : set) {
            this.objName = oi.getObjectName();
            String subtype = this.objName.getKeyProperty("subType");
            if (subtype != null && subtype.equals("SocketProperties")) {
                this.objName = null;
                continue;
            }
            String name = this.objName.getKeyProperty("name");
            name = name.replace("\"", "");
            String[] elenames = name.split("-");
            String sport = elenames[elenames.length - 1];
            iport = Integer.parseInt(sport);
            if (elenames.length == 4) {
                shost = elenames[2];
            }
            if (port == 0 && host == null || iport == port && (host == null || shost != null && shost.compareTo(host) == 0)) break;
            this.objName = null;
            shost = null;
        }
        if (this.objName == null) {
            throw new Exception(sm.getString("collectedInfo.noConnector", new Object[]{host, port}));
        }
        this.port = iport;
        this.host = shost;
    }

    public void refresh() throws Exception {
        if (this.mBeanServer == null || this.objName == null) {
            throw new Exception(sm.getString("collectedInfo.notInitialized"));
        }
        Integer imax = (Integer)this.mBeanServer.getAttribute(this.objName, "maxThreads");
        Integer ibusy = (Integer)this.mBeanServer.getAttribute(this.objName, "currentThreadsBusy");
        this.busy = ibusy;
        this.ready = imax - ibusy;
    }
}

