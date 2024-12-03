/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.digester.Digester
 */
package org.apache.tomcat.util.modeler.modules;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.modules.ModelerSource;

public class MbeansDescriptorsDigesterSource
extends ModelerSource {
    private static final Log log = LogFactory.getLog(MbeansDescriptorsDigesterSource.class);
    private static final Object dLock = new Object();
    private Registry registry;
    private final List<ObjectName> mbeans = new ArrayList<ObjectName>();
    private static Digester digester = null;

    private static Digester createDigester() {
        Digester digester = new Digester();
        digester.setNamespaceAware(false);
        digester.setValidating(false);
        URL url = Registry.getRegistry(null, null).getClass().getResource("/org/apache/tomcat/util/modeler/mbeans-descriptors.dtd");
        if (url != null) {
            digester.register("-//Apache Software Foundation//DTD Model MBeans Configuration File", url.toString());
        }
        digester.addObjectCreate("mbeans-descriptors/mbean", "org.apache.tomcat.util.modeler.ManagedBean");
        digester.addSetProperties("mbeans-descriptors/mbean");
        digester.addSetNext("mbeans-descriptors/mbean", "add", "java.lang.Object");
        digester.addObjectCreate("mbeans-descriptors/mbean/attribute", "org.apache.tomcat.util.modeler.AttributeInfo");
        digester.addSetProperties("mbeans-descriptors/mbean/attribute");
        digester.addSetNext("mbeans-descriptors/mbean/attribute", "addAttribute", "org.apache.tomcat.util.modeler.AttributeInfo");
        digester.addObjectCreate("mbeans-descriptors/mbean/notification", "org.apache.tomcat.util.modeler.NotificationInfo");
        digester.addSetProperties("mbeans-descriptors/mbean/notification");
        digester.addSetNext("mbeans-descriptors/mbean/notification", "addNotification", "org.apache.tomcat.util.modeler.NotificationInfo");
        digester.addObjectCreate("mbeans-descriptors/mbean/notification/descriptor/field", "org.apache.tomcat.util.modeler.FieldInfo");
        digester.addSetProperties("mbeans-descriptors/mbean/notification/descriptor/field");
        digester.addSetNext("mbeans-descriptors/mbean/notification/descriptor/field", "addField", "org.apache.tomcat.util.modeler.FieldInfo");
        digester.addCallMethod("mbeans-descriptors/mbean/notification/notification-type", "addNotifType", 0);
        digester.addObjectCreate("mbeans-descriptors/mbean/operation", "org.apache.tomcat.util.modeler.OperationInfo");
        digester.addSetProperties("mbeans-descriptors/mbean/operation");
        digester.addSetNext("mbeans-descriptors/mbean/operation", "addOperation", "org.apache.tomcat.util.modeler.OperationInfo");
        digester.addObjectCreate("mbeans-descriptors/mbean/operation/descriptor/field", "org.apache.tomcat.util.modeler.FieldInfo");
        digester.addSetProperties("mbeans-descriptors/mbean/operation/descriptor/field");
        digester.addSetNext("mbeans-descriptors/mbean/operation/descriptor/field", "addField", "org.apache.tomcat.util.modeler.FieldInfo");
        digester.addObjectCreate("mbeans-descriptors/mbean/operation/parameter", "org.apache.tomcat.util.modeler.ParameterInfo");
        digester.addSetProperties("mbeans-descriptors/mbean/operation/parameter");
        digester.addSetNext("mbeans-descriptors/mbean/operation/parameter", "addParameter", "org.apache.tomcat.util.modeler.ParameterInfo");
        return digester;
    }

    public void setRegistry(Registry reg) {
        this.registry = reg;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    @Override
    public List<ObjectName> loadDescriptors(Registry registry, String type, Object source) throws Exception {
        this.setRegistry(registry);
        this.setSource(source);
        this.execute();
        return this.mbeans;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute() throws Exception {
        if (this.registry == null) {
            this.registry = Registry.getRegistry(null, null);
        }
        InputStream stream = (InputStream)this.source;
        ArrayList loadedMbeans = new ArrayList();
        Iterator iterator = dLock;
        synchronized (iterator) {
            if (digester == null) {
                digester = MbeansDescriptorsDigesterSource.createDigester();
            }
            try {
                digester.push(loadedMbeans);
                digester.parse(stream);
            }
            catch (Exception e) {
                log.error((Object)sm.getString("modules.digesterParseError"), (Throwable)e);
                throw e;
            }
            finally {
                digester.reset();
            }
        }
        for (ManagedBean loadedMbean : loadedMbeans) {
            this.registry.addManagedBean(loadedMbean);
        }
    }
}

