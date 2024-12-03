/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.naming.JavaBeanReferenceMaker
 */
package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.C3P0Registry;
import com.mchange.v2.c3p0.cfg.C3P0Config;
import com.mchange.v2.c3p0.impl.AuthMaskingProperties;
import com.mchange.v2.c3p0.impl.C3P0Defaults;
import com.mchange.v2.c3p0.impl.C3P0ImplUtils;
import com.mchange.v2.c3p0.impl.IdentityTokenResolvable;
import com.mchange.v2.naming.JavaBeanReferenceMaker;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

public abstract class DriverManagerDataSourceBase
extends IdentityTokenResolvable
implements Referenceable,
Serializable {
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    protected String description;
    protected String driverClass = C3P0Config.initializeStringPropertyVar("driverClass", C3P0Defaults.driverClass());
    protected String factoryClassLocation = C3P0Config.initializeStringPropertyVar("factoryClassLocation", C3P0Defaults.factoryClassLocation());
    protected boolean forceUseNamedDriverClass = C3P0Config.initializeBooleanPropertyVar("forceUseNamedDriverClass", C3P0Defaults.forceUseNamedDriverClass());
    private volatile String identityToken;
    protected String jdbcUrl = C3P0Config.initializeStringPropertyVar("jdbcUrl", C3P0Defaults.jdbcUrl());
    protected Properties properties = new AuthMaskingProperties();
    private static final long serialVersionUID = 1L;
    private static final short VERSION = 1;
    static final JavaBeanReferenceMaker referenceMaker = new JavaBeanReferenceMaker();

    protected PropertyChangeSupport getPropertyChangeSupport() {
        return this.pcs;
    }

    public synchronized String getDescription() {
        return this.description;
    }

    public synchronized void setDescription(String description) {
        this.description = description;
    }

    public synchronized String getDriverClass() {
        return this.driverClass;
    }

    public synchronized void setDriverClass(String driverClass) {
        String oldVal = this.driverClass;
        this.driverClass = driverClass;
        if (!this.eqOrBothNull(oldVal, driverClass)) {
            this.pcs.firePropertyChange("driverClass", oldVal, driverClass);
        }
    }

    public synchronized String getFactoryClassLocation() {
        return this.factoryClassLocation;
    }

    public synchronized void setFactoryClassLocation(String factoryClassLocation) {
        this.factoryClassLocation = factoryClassLocation;
    }

    public synchronized boolean isForceUseNamedDriverClass() {
        return this.forceUseNamedDriverClass;
    }

    public synchronized void setForceUseNamedDriverClass(boolean forceUseNamedDriverClass) {
        this.forceUseNamedDriverClass = forceUseNamedDriverClass;
    }

    @Override
    public String getIdentityToken() {
        return this.identityToken;
    }

    @Override
    public void setIdentityToken(String identityToken) {
        String oldVal = this.identityToken;
        this.identityToken = identityToken;
        if (!this.eqOrBothNull(oldVal, identityToken)) {
            this.pcs.firePropertyChange("identityToken", oldVal, identityToken);
        }
    }

    public synchronized String getJdbcUrl() {
        return this.jdbcUrl;
    }

    public synchronized void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public synchronized Properties getProperties() {
        return AuthMaskingProperties.fromAnyProperties(this.properties);
    }

    public synchronized void setProperties(Properties properties) {
        Properties oldVal = this.properties;
        this.properties = AuthMaskingProperties.fromAnyProperties(properties);
        if (!this.eqOrBothNull(oldVal, properties)) {
            this.pcs.firePropertyChange("properties", oldVal, properties);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        this.pcs.addPropertyChangeListener(pcl);
    }

    public void addPropertyChangeListener(String propName, PropertyChangeListener pcl) {
        this.pcs.addPropertyChangeListener(propName, pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        this.pcs.removePropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(String propName, PropertyChangeListener pcl) {
        this.pcs.removePropertyChangeListener(propName, pcl);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return this.pcs.getPropertyChangeListeners();
    }

    private boolean eqOrBothNull(Object a, Object b) {
        return a == b || a != null && a.equals(b);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeShort(1);
        oos.writeObject(this.description);
        oos.writeObject(this.driverClass);
        oos.writeObject(this.factoryClassLocation);
        oos.writeBoolean(this.forceUseNamedDriverClass);
        oos.writeObject(this.identityToken);
        oos.writeObject(this.jdbcUrl);
        oos.writeObject(this.properties);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        short version = ois.readShort();
        switch (version) {
            case 1: {
                this.description = (String)ois.readObject();
                this.driverClass = (String)ois.readObject();
                this.factoryClassLocation = (String)ois.readObject();
                this.forceUseNamedDriverClass = ois.readBoolean();
                this.identityToken = (String)ois.readObject();
                this.jdbcUrl = (String)ois.readObject();
                this.properties = (Properties)ois.readObject();
                this.pcs = new PropertyChangeSupport(this);
                break;
            }
            default: {
                throw new IOException("Unsupported Serialized Version: " + version);
            }
        }
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return C3P0ImplUtils.PARENT_LOGGER;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append(" [ ");
        sb.append("description -> " + this.description);
        sb.append(", ");
        sb.append("driverClass -> " + this.driverClass);
        sb.append(", ");
        sb.append("factoryClassLocation -> " + this.factoryClassLocation);
        sb.append(", ");
        sb.append("forceUseNamedDriverClass -> " + this.forceUseNamedDriverClass);
        sb.append(", ");
        sb.append("identityToken -> " + this.identityToken);
        sb.append(", ");
        sb.append("jdbcUrl -> " + this.jdbcUrl);
        sb.append(", ");
        sb.append("properties -> " + this.properties);
        String extraToStringInfo = this.extraToStringInfo();
        if (extraToStringInfo != null) {
            sb.append(extraToStringInfo);
        }
        sb.append(" ]");
        return sb.toString();
    }

    protected String extraToStringInfo() {
        return null;
    }

    @Override
    public Reference getReference() throws NamingException {
        return referenceMaker.createReference((Object)this);
    }

    private DriverManagerDataSourceBase() {
    }

    public DriverManagerDataSourceBase(boolean autoregister) {
        if (autoregister) {
            this.identityToken = C3P0ImplUtils.allocateIdentityToken(this);
            C3P0Registry.reregister(this);
        }
    }

    static {
        referenceMaker.setFactoryClassName("com.mchange.v2.c3p0.impl.C3P0JavaBeanObjectFactory");
        referenceMaker.addReferenceProperty("description");
        referenceMaker.addReferenceProperty("driverClass");
        referenceMaker.addReferenceProperty("factoryClassLocation");
        referenceMaker.addReferenceProperty("forceUseNamedDriverClass");
        referenceMaker.addReferenceProperty("identityToken");
        referenceMaker.addReferenceProperty("jdbcUrl");
        referenceMaker.addReferenceProperty("properties");
    }
}

