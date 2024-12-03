/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base.config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import org.jfree.base.config.ModifiableConfiguration;
import org.jfree.util.Configuration;
import org.jfree.util.PublicCloneable;

public class HierarchicalConfiguration
implements ModifiableConfiguration,
PublicCloneable {
    private Properties configuration = new Properties();
    private transient Configuration parentConfiguration;

    public HierarchicalConfiguration() {
    }

    public HierarchicalConfiguration(Configuration parentConfiguration) {
        this();
        this.parentConfiguration = parentConfiguration;
    }

    public String getConfigProperty(String key) {
        return this.getConfigProperty(key, null);
    }

    public String getConfigProperty(String key, String defaultValue) {
        String value = this.configuration.getProperty(key);
        if (value == null) {
            value = this.isRootConfig() ? defaultValue : this.parentConfiguration.getConfigProperty(key, defaultValue);
        }
        return value;
    }

    public void setConfigProperty(String key, String value) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (value == null) {
            this.configuration.remove(key);
        } else {
            this.configuration.setProperty(key, value);
        }
    }

    private boolean isRootConfig() {
        return this.parentConfiguration == null;
    }

    public boolean isLocallyDefined(String key) {
        return this.configuration.containsKey(key);
    }

    protected Properties getConfiguration() {
        return this.configuration;
    }

    public void insertConfiguration(HierarchicalConfiguration config) {
        config.setParentConfig(this.getParentConfig());
        this.setParentConfig(config);
    }

    protected void setParentConfig(Configuration config) {
        if (this.parentConfiguration == this) {
            throw new IllegalArgumentException("Cannot add myself as parent configuration.");
        }
        this.parentConfiguration = config;
    }

    protected Configuration getParentConfig() {
        return this.parentConfiguration;
    }

    public Enumeration getConfigProperties() {
        return this.configuration.keys();
    }

    public Iterator findPropertyKeys(String prefix) {
        TreeSet keys = new TreeSet();
        this.collectPropertyKeys(prefix, this, keys);
        return Collections.unmodifiableSet(keys).iterator();
    }

    private void collectPropertyKeys(String prefix, Configuration config, TreeSet collector) {
        Enumeration enum1 = config.getConfigProperties();
        while (enum1.hasMoreElements()) {
            String key = (String)enum1.nextElement();
            if (!key.startsWith(prefix) || collector.contains(key)) continue;
            collector.add(key);
        }
        if (config instanceof HierarchicalConfiguration) {
            HierarchicalConfiguration hconfig = (HierarchicalConfiguration)config;
            if (hconfig.parentConfiguration != null) {
                this.collectPropertyKeys(prefix, hconfig.parentConfiguration, collector);
            }
        }
    }

    protected boolean isParentSaved() {
        return true;
    }

    protected void configurationLoaded() {
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (!this.isParentSaved()) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(this.parentConfiguration);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        boolean readParent = in.readBoolean();
        this.parentConfiguration = readParent ? (ModifiableConfiguration)in.readObject() : null;
        this.configurationLoaded();
    }

    public Object clone() throws CloneNotSupportedException {
        HierarchicalConfiguration config = (HierarchicalConfiguration)super.clone();
        config.configuration = (Properties)this.configuration.clone();
        return config;
    }
}

