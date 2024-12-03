/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.connector.Connector
 */
package org.apache.catalina.storeconfig;

import java.io.PrintWriter;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.catalina.storeconfig.StoreFactoryBase;

public class ConnectorSF
extends StoreFactoryBase {
    @Override
    public void storeChildren(PrintWriter aWriter, int indent, Object aConnector, StoreDescription parentDesc) throws Exception {
        if (aConnector instanceof Connector) {
            Connector connector = (Connector)aConnector;
            Object[] listeners = connector.findLifecycleListeners();
            this.storeElementArray(aWriter, indent, listeners);
            Object[] upgradeProtocols = connector.findUpgradeProtocols();
            this.storeElementArray(aWriter, indent, upgradeProtocols);
            if (Boolean.TRUE.equals(connector.getProperty("SSLEnabled"))) {
                Object[] hostConfigs = connector.findSslHostConfigs();
                this.storeElementArray(aWriter, indent, hostConfigs);
            }
        }
    }

    protected void printOpenTag(PrintWriter aWriter, int indent, Object bean, StoreDescription aDesc) throws Exception {
        aWriter.print("<");
        aWriter.print(aDesc.getTag());
        this.storeConnectorAttributes(aWriter, indent, bean, aDesc);
        aWriter.println(">");
    }

    protected void storeConnectorAttributes(PrintWriter aWriter, int indent, Object bean, StoreDescription aDesc) throws Exception {
        if (aDesc.isAttributes()) {
            this.getStoreAppender().printAttributes(aWriter, indent, false, bean, aDesc);
        }
    }

    protected void printTag(PrintWriter aWriter, int indent, Object bean, StoreDescription aDesc) throws Exception {
        aWriter.print("<");
        aWriter.print(aDesc.getTag());
        this.storeConnectorAttributes(aWriter, indent, bean, aDesc);
        aWriter.println("/>");
    }
}

