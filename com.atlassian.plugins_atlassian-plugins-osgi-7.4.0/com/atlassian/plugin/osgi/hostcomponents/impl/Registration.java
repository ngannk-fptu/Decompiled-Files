/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.osgi.hostcomponents.impl;

import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

class Registration
implements HostComponentRegistration {
    private final String[] mainInterfaces;
    private final Class<?>[] mainInterfaceClasses;
    private final Dictionary<String, String> properties = new Hashtable<String, String>();
    private Object instance;

    public Registration(Class<?>[] ifs) {
        this.mainInterfaceClasses = ifs;
        this.mainInterfaces = new String[ifs.length];
        for (int x = 0; x < ifs.length; ++x) {
            if (!ifs[x].isInterface()) {
                throw new IllegalArgumentException("Services can only be registered against interfaces");
            }
            this.mainInterfaces[x] = ifs[x].getName();
        }
    }

    @Override
    public Object getInstance() {
        return this.instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    @Override
    public Dictionary<String, String> getProperties() {
        return this.properties;
    }

    @Override
    public String[] getMainInterfaces() {
        return this.mainInterfaces;
    }

    @Override
    public Class<?>[] getMainInterfaceClasses() {
        return this.mainInterfaceClasses;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Registration that = (Registration)o;
        if (!this.instance.equals(that.instance)) {
            return false;
        }
        if (!Arrays.equals(this.mainInterfaces, that.mainInterfaces)) {
            return false;
        }
        return this.properties.equals(that.properties);
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.mainInterfaces);
        result = 31 * result + this.instance.hashCode();
        result = 31 * result + this.properties.hashCode();
        return result;
    }
}

