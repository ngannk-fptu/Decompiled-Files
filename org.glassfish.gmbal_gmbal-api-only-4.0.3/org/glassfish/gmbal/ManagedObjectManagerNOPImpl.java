/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.gmbal;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.glassfish.gmbal.AMXClient;
import org.glassfish.gmbal.GmbalMBean;
import org.glassfish.gmbal.GmbalMBeanNOPImpl;
import org.glassfish.gmbal.ManagedObjectManager;
import org.glassfish.pfl.tf.timer.spi.ObjectRegistrationManager;

class ManagedObjectManagerNOPImpl
implements ManagedObjectManager {
    static final ManagedObjectManager self = new ManagedObjectManagerNOPImpl();
    private static final GmbalMBean gmb = new GmbalMBeanNOPImpl();

    private ManagedObjectManagerNOPImpl() {
    }

    @Override
    public void suspendJMXRegistration() {
    }

    @Override
    public void resumeJMXRegistration() {
    }

    @Override
    public boolean isManagedObject(Object obj) {
        return false;
    }

    @Override
    public GmbalMBean createRoot() {
        return gmb;
    }

    @Override
    public GmbalMBean createRoot(Object root) {
        return gmb;
    }

    @Override
    public GmbalMBean createRoot(Object root, String name) {
        return gmb;
    }

    @Override
    public Object getRoot() {
        return null;
    }

    @Override
    public GmbalMBean register(Object parent, Object obj, String name) {
        return gmb;
    }

    @Override
    public GmbalMBean register(Object parent, Object obj) {
        return gmb;
    }

    @Override
    public GmbalMBean registerAtRoot(Object obj, String name) {
        return gmb;
    }

    @Override
    public GmbalMBean registerAtRoot(Object obj) {
        return gmb;
    }

    @Override
    public void unregister(Object obj) {
    }

    @Override
    public ObjectName getObjectName(Object obj) {
        return null;
    }

    @Override
    public Object getObject(ObjectName oname) {
        return null;
    }

    @Override
    public void stripPrefix(String ... str) {
    }

    @Override
    public String getDomain() {
        return null;
    }

    @Override
    public void setMBeanServer(MBeanServer server) {
    }

    @Override
    public MBeanServer getMBeanServer() {
        return null;
    }

    @Override
    public void setResourceBundle(ResourceBundle rb) {
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return null;
    }

    @Override
    public void addAnnotation(AnnotatedElement element, Annotation annotation) {
    }

    @Override
    public void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel level) {
    }

    @Override
    public void setRuntimeDebug(boolean flag) {
    }

    @Override
    public String dumpSkeleton(Object obj) {
        return "";
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void setTypelibDebug(int level) {
    }

    @Override
    public void stripPackagePrefix() {
    }

    @Override
    public void suppressDuplicateRootReport(boolean suppressReport) {
    }

    @Override
    public AMXClient getAMXClient(Object obj) {
        return null;
    }

    @Override
    public void setJMXRegistrationDebug(boolean flag) {
    }

    @Override
    public void addInheritedAnnotations(Class<?> cls) {
    }

    @Override
    public ObjectRegistrationManager getObjectRegistrationManager() {
        return null;
    }
}

