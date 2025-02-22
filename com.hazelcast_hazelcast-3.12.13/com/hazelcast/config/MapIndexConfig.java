/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.MapIndexConfigReadOnly;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.QueryConstants;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class MapIndexConfig
implements IdentifiedDataSerializable {
    private static final ILogger LOG = Logger.getLogger(MapIndexConfig.class);
    private String attribute;
    private boolean ordered;
    private transient MapIndexConfigReadOnly readOnly;

    public MapIndexConfig() {
    }

    public MapIndexConfig(String attribute, boolean ordered) {
        this.setAttribute(attribute);
        this.setOrdered(ordered);
    }

    public MapIndexConfig(MapIndexConfig config) {
        this.attribute = config.getAttribute();
        this.ordered = config.isOrdered();
    }

    public MapIndexConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new MapIndexConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public MapIndexConfig setAttribute(String attribute) {
        this.attribute = MapIndexConfig.validateIndexAttribute(attribute);
        return this;
    }

    public boolean isOrdered() {
        return this.ordered;
    }

    public MapIndexConfig setOrdered(boolean ordered) {
        this.ordered = ordered;
        return this;
    }

    public String toString() {
        return "MapIndexConfig{attribute='" + this.attribute + "', ordered=" + this.ordered + '}';
    }

    public static String validateIndexAttribute(String attribute) {
        Preconditions.checkHasText(attribute, "Map index attribute must contain text");
        String keyPrefix = QueryConstants.KEY_ATTRIBUTE_NAME.value();
        if (attribute.startsWith(keyPrefix) && attribute.length() > keyPrefix.length() && attribute.charAt(keyPrefix.length()) != '#') {
            LOG.warning(QueryConstants.KEY_ATTRIBUTE_NAME.value() + " used without a following '#' char in index attribute '" + attribute + "'. Don't you want to index a key?");
        }
        return attribute;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 16;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attribute);
        out.writeBoolean(this.ordered);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attribute = in.readUTF();
        this.ordered = in.readBoolean();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapIndexConfig)) {
            return false;
        }
        MapIndexConfig that = (MapIndexConfig)o;
        if (this.ordered != that.ordered) {
            return false;
        }
        return this.attribute != null ? this.attribute.equals(that.attribute) : that.attribute == null;
    }

    public final int hashCode() {
        int result = this.attribute != null ? this.attribute.hashCode() : 0;
        result = 31 * result + (this.ordered ? 1 : 0);
        return result;
    }
}

