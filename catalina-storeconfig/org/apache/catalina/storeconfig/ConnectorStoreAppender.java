/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.catalina.connector.Connector
 *  org.apache.coyote.ProtocolHandler
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.net.SocketProperties
 */
package org.apache.catalina.storeconfig;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.storeconfig.StoreAppender;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.net.SocketProperties;

public class ConnectorStoreAppender
extends StoreAppender {
    protected static final HashMap<String, String> replacements = new HashMap();
    protected static final Set<String> internalExecutorAttributes = new HashSet<String>();

    @Override
    public void printAttributes(PrintWriter writer, int indent, boolean include, Object bean, StoreDescription desc) throws Exception {
        String executorName;
        if (include && !desc.isStandard()) {
            writer.print(" className=\"");
            writer.print(bean.getClass().getName());
            writer.print("\"");
        }
        Connector connector = (Connector)bean;
        String protocol = connector.getProtocol();
        List<String> propertyKeys = this.getPropertyKeys(connector);
        Connector bean2 = new Connector(protocol);
        for (String key : propertyKeys) {
            Object value2;
            Object value = IntrospectionUtils.getProperty((Object)bean, (String)key);
            if (desc.isTransientAttribute(key) || value == null || !this.isPersistable(value.getClass()) || value.equals(value2 = IntrospectionUtils.getProperty((Object)bean2, (String)key)) || !this.isPrintValue(bean, bean2, key, desc)) continue;
            this.printValue(writer, indent, key, value);
        }
        if (protocol != null && !"HTTP/1.1".equals(protocol)) {
            super.printValue(writer, indent, "protocol", protocol);
        }
        if (!"Internal".equals(executorName = connector.getExecutorName())) {
            super.printValue(writer, indent, "executor", executorName);
        }
    }

    protected List<String> getPropertyKeys(Connector bean) throws IntrospectionException {
        ArrayList<String> propertyKeys = new ArrayList<String>();
        ProtocolHandler protocolHandler = bean.getProtocolHandler();
        PropertyDescriptor[] descriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = new PropertyDescriptor[]{};
        }
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor instanceof IndexedPropertyDescriptor || !this.isPersistable(descriptor.getPropertyType()) || descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null || "protocol".equals(descriptor.getName()) || "protocolHandlerClassName".equals(descriptor.getName())) continue;
            propertyKeys.add(descriptor.getName());
        }
        descriptors = Introspector.getBeanInfo(protocolHandler.getClass()).getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = new PropertyDescriptor[]{};
        }
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor instanceof IndexedPropertyDescriptor || !this.isPersistable(descriptor.getPropertyType()) || descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) continue;
            String key = descriptor.getName();
            if (!"Internal".equals(bean.getExecutorName()) && internalExecutorAttributes.contains(key)) continue;
            if (replacements.get(key) != null) {
                key = replacements.get(key);
            }
            if (propertyKeys.contains(key)) continue;
            propertyKeys.add(key);
        }
        String socketName = "socket.";
        descriptors = Introspector.getBeanInfo(SocketProperties.class).getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = new PropertyDescriptor[]{};
        }
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor instanceof IndexedPropertyDescriptor || !this.isPersistable(descriptor.getPropertyType()) || descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) continue;
            String key = descriptor.getName();
            if (replacements.get(key) != null) {
                key = replacements.get(key);
            }
            if (propertyKeys.contains(key)) continue;
            propertyKeys.add("socket." + descriptor.getName());
        }
        return propertyKeys;
    }

    protected void storeConnectorAttributes(PrintWriter aWriter, int indent, Object bean, StoreDescription aDesc) throws Exception {
        if (aDesc.isAttributes()) {
            this.printAttributes(aWriter, indent, false, bean, aDesc);
        }
    }

    @Override
    public void printOpenTag(PrintWriter aWriter, int indent, Object bean, StoreDescription aDesc) throws Exception {
        aWriter.print("<");
        aWriter.print(aDesc.getTag());
        this.storeConnectorAttributes(aWriter, indent, bean, aDesc);
        aWriter.println(">");
    }

    @Override
    public void printTag(PrintWriter aWriter, int indent, Object bean, StoreDescription aDesc) throws Exception {
        aWriter.print("<");
        aWriter.print(aDesc.getTag());
        this.storeConnectorAttributes(aWriter, indent, bean, aDesc);
        aWriter.println("/>");
    }

    @Override
    public void printValue(PrintWriter writer, int indent, String name, Object value) {
        String repl = name;
        if (replacements.get(name) != null) {
            repl = replacements.get(name);
        }
        super.printValue(writer, indent, repl, value);
    }

    @Override
    public boolean isPrintValue(Object bean, Object bean2, String attrName, StoreDescription desc) {
        boolean isPrint = super.isPrintValue(bean, bean2, attrName, desc);
        if (isPrint && "jkHome".equals(attrName)) {
            File jkHomeBase;
            Connector connector = (Connector)bean;
            File catalinaBase = this.getCatalinaBase();
            isPrint = !catalinaBase.equals(jkHomeBase = this.getJkHomeBase((String)connector.getProperty("jkHome"), catalinaBase));
        }
        return isPrint;
    }

    protected File getCatalinaBase() {
        File file = new File(System.getProperty("catalina.base"));
        try {
            file = file.getCanonicalFile();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return file;
    }

    protected File getJkHomeBase(String jkHome, File appBase) {
        File jkHomeBase;
        File file = new File(jkHome);
        if (!file.isAbsolute()) {
            file = new File(appBase, jkHome);
        }
        try {
            jkHomeBase = file.getCanonicalFile();
        }
        catch (IOException e) {
            jkHomeBase = file;
        }
        return jkHomeBase;
    }

    static {
        replacements.put("timeout", "connectionUploadTimeout");
        replacements.put("clientauth", "clientAuth");
        replacements.put("keystore", "keystoreFile");
        replacements.put("randomfile", "randomFile");
        replacements.put("keypass", "keystorePass");
        replacements.put("keytype", "keystoreType");
        replacements.put("protocol", "sslProtocol");
        replacements.put("protocols", "sslProtocols");
        internalExecutorAttributes.add("maxThreads");
        internalExecutorAttributes.add("minSpareThreads");
        internalExecutorAttributes.add("threadPriority");
    }
}

