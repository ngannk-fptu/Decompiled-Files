/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.gmbal;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.glassfish.gmbal.AMXClient;
import org.glassfish.gmbal.GmbalMBean;
import org.glassfish.pfl.tf.timer.spi.ObjectRegistrationManager;

public interface ManagedObjectManager
extends Closeable {
    public void suspendJMXRegistration();

    public void resumeJMXRegistration();

    public boolean isManagedObject(Object var1);

    public GmbalMBean createRoot();

    public GmbalMBean createRoot(Object var1);

    public GmbalMBean createRoot(Object var1, String var2);

    public Object getRoot();

    public GmbalMBean register(Object var1, Object var2, String var3);

    public GmbalMBean register(Object var1, Object var2);

    public GmbalMBean registerAtRoot(Object var1, String var2);

    public GmbalMBean registerAtRoot(Object var1);

    public void unregister(Object var1);

    public ObjectName getObjectName(Object var1);

    public AMXClient getAMXClient(Object var1);

    public Object getObject(ObjectName var1);

    public void stripPrefix(String ... var1);

    public void stripPackagePrefix();

    public String getDomain();

    public void setMBeanServer(MBeanServer var1);

    public MBeanServer getMBeanServer();

    public void setResourceBundle(ResourceBundle var1);

    public ResourceBundle getResourceBundle();

    public void addAnnotation(AnnotatedElement var1, Annotation var2);

    public void addInheritedAnnotations(Class<?> var1);

    public void setRegistrationDebug(RegistrationDebugLevel var1);

    public void setRuntimeDebug(boolean var1);

    public void setTypelibDebug(int var1);

    public void setJMXRegistrationDebug(boolean var1);

    public String dumpSkeleton(Object var1);

    public void suppressDuplicateRootReport(boolean var1);

    public ObjectRegistrationManager getObjectRegistrationManager();

    public static enum RegistrationDebugLevel {
        NONE,
        NORMAL,
        FINE;

    }
}

