/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.AxisEngine;
import org.apache.axis.ConfigurationException;
import org.apache.axis.Handler;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.soap.SOAPService;

public interface EngineConfiguration {
    public static final String PROPERTY_NAME = "engineConfig";

    public void configureEngine(AxisEngine var1) throws ConfigurationException;

    public void writeEngineConfig(AxisEngine var1) throws ConfigurationException;

    public Handler getHandler(QName var1) throws ConfigurationException;

    public SOAPService getService(QName var1) throws ConfigurationException;

    public SOAPService getServiceByNamespaceURI(String var1) throws ConfigurationException;

    public Handler getTransport(QName var1) throws ConfigurationException;

    public TypeMappingRegistry getTypeMappingRegistry() throws ConfigurationException;

    public Handler getGlobalRequest() throws ConfigurationException;

    public Handler getGlobalResponse() throws ConfigurationException;

    public Hashtable getGlobalOptions() throws ConfigurationException;

    public Iterator getDeployedServices() throws ConfigurationException;

    public List getRoles();
}

