/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.configuration;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;

public class NullProvider
implements EngineConfiguration {
    public void configureEngine(AxisEngine engine) throws ConfigurationException {
    }

    public void writeEngineConfig(AxisEngine engine) throws ConfigurationException {
    }

    public Hashtable getGlobalOptions() throws ConfigurationException {
        return null;
    }

    public Handler getGlobalResponse() throws ConfigurationException {
        return null;
    }

    public Handler getGlobalRequest() throws ConfigurationException {
        return null;
    }

    public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException {
        return null;
    }

    public TypeMapping getTypeMapping(String encodingStyle) throws ConfigurationException {
        return null;
    }

    public Handler getTransport(QName qname) throws ConfigurationException {
        return null;
    }

    public SOAPService getService(QName qname) throws ConfigurationException {
        return null;
    }

    public SOAPService getServiceByNamespaceURI(String namespace) throws ConfigurationException {
        return null;
    }

    public Handler getHandler(QName qname) throws ConfigurationException {
        return null;
    }

    public Iterator getDeployedServices() throws ConfigurationException {
        return null;
    }

    public List getRoles() {
        return null;
    }
}

