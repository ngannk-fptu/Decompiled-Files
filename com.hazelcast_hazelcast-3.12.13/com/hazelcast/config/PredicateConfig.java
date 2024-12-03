/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.PredicateConfigReadOnly;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.Predicate;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class PredicateConfig
implements IdentifiedDataSerializable {
    protected String className;
    protected String sql;
    protected Predicate implementation;
    private transient PredicateConfigReadOnly readOnly;

    public PredicateConfig() {
    }

    public PredicateConfig(String className) {
        this.setClassName(className);
    }

    public PredicateConfig(PredicateConfig config) {
        this.implementation = config.getImplementation();
        this.className = config.getClassName();
        this.sql = config.getSql();
    }

    public PredicateConfig(Predicate implementation) {
        this.implementation = Preconditions.isNotNull(implementation, "implementation");
    }

    public PredicateConfig getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new PredicateConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public String getClassName() {
        return this.className;
    }

    public PredicateConfig setClassName(String className) {
        this.className = Preconditions.checkHasText(className, "className must contain text");
        this.implementation = null;
        this.sql = null;
        return this;
    }

    public Predicate getImplementation() {
        return this.implementation;
    }

    public PredicateConfig setImplementation(Predicate implementation) {
        this.implementation = Preconditions.isNotNull(implementation, "implementation");
        this.className = null;
        this.sql = null;
        return this;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
        this.className = null;
        this.implementation = null;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PredicateConfig)) {
            return false;
        }
        PredicateConfig that = (PredicateConfig)o;
        if (this.className != null ? !this.className.equals(that.className) : that.className != null) {
            return false;
        }
        if (this.sql != null ? !this.sql.equals(that.sql) : that.sql != null) {
            return false;
        }
        return !(this.implementation == null ? that.implementation != null : !this.implementation.equals(that.implementation));
    }

    public int hashCode() {
        int result = this.className != null ? this.className.hashCode() : 0;
        result = 31 * result + (this.sql != null ? this.sql.hashCode() : 0);
        result = 31 * result + (this.implementation != null ? this.implementation.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "PredicateConfig{className='" + this.className + '\'' + ", sql='" + this.sql + '\'' + ", implementation=" + this.implementation + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 19;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.className);
        out.writeUTF(this.sql);
        out.writeObject(this.implementation);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.className = in.readUTF();
        this.sql = in.readUTF();
        this.implementation = (Predicate)in.readObject();
    }
}

