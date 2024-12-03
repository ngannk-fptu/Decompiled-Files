/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerBuilder;
import javax.management.MBeanServerDelegate;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

public class TCKMBeanServerBuilder
extends MBeanServerBuilder {
    @Override
    public MBeanServer newMBeanServer(String defaultDomain, MBeanServer outer, MBeanServerDelegate delegate) {
        return super.newMBeanServer(defaultDomain, outer, new RIMBeanServerDelegate(delegate));
    }

    public static class RIMBeanServerDelegate
    extends MBeanServerDelegate {
        private MBeanServerDelegate delegate;

        public RIMBeanServerDelegate(MBeanServerDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        public String getSpecificationName() {
            return this.delegate.getSpecificationName();
        }

        @Override
        public String getSpecificationVersion() {
            return this.delegate.getSpecificationVersion();
        }

        @Override
        public String getSpecificationVendor() {
            return this.delegate.getSpecificationVendor();
        }

        @Override
        public String getImplementationName() {
            return this.delegate.getImplementationName();
        }

        @Override
        public String getImplementationVersion() {
            return this.delegate.getImplementationVersion();
        }

        @Override
        public String getImplementationVendor() {
            return this.delegate.getImplementationVendor();
        }

        @Override
        public MBeanNotificationInfo[] getNotificationInfo() {
            return this.delegate.getNotificationInfo();
        }

        @Override
        public synchronized void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
            this.delegate.addNotificationListener(listener, filter, handback);
        }

        @Override
        public synchronized void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException {
            this.delegate.removeNotificationListener(listener, filter, handback);
        }

        @Override
        public synchronized void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
            this.delegate.removeNotificationListener(listener);
        }

        @Override
        public void sendNotification(Notification notification) {
            this.delegate.sendNotification(notification);
        }

        @Override
        public synchronized String getMBeanServerId() {
            return System.getProperty("org.jsr107.tck.management.agentId");
        }
    }
}

