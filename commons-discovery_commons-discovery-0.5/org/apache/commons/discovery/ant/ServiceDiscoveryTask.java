/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.discovery.ant;

import java.util.LinkedList;
import org.apache.commons.discovery.ResourceIterator;
import org.apache.commons.discovery.jdk.JDKHooks;
import org.apache.commons.discovery.resource.DiscoverResources;

public class ServiceDiscoveryTask {
    String name;
    int debug = 0;
    String[] drivers = null;

    public void setServiceName(String name) {
        this.name = name;
    }

    public void setDebug(int i) {
        this.debug = i;
    }

    public String[] getServiceInfo() {
        return this.drivers;
    }

    public void execute() throws Exception {
        System.out.printf("Discovering service '%s'...%n", this.name);
        DiscoverResources disc = new DiscoverResources();
        disc.addClassLoader(JDKHooks.getJDKHooks().getThreadContextClassLoader());
        disc.addClassLoader(this.getClass().getClassLoader());
        ResourceIterator iterator = disc.findResources(this.name);
        LinkedList<String> resources = new LinkedList<String>();
        while (iterator.hasNext()) {
            String resourceInfo = iterator.nextResourceName();
            resources.add(resourceInfo);
            if (this.debug <= 0) continue;
            System.out.printf("Found '%s'%n", resourceInfo);
        }
        this.drivers = new String[resources.size()];
        resources.toArray(this.drivers);
    }
}

