/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.MapAttributeConfigReadOnly;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.QueryConstants;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.regex.Pattern;

public class MapAttributeConfig
implements IdentifiedDataSerializable {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_]*$");
    private String name;
    private String extractor;
    private transient MapAttributeConfigReadOnly readOnly;

    public MapAttributeConfig() {
    }

    public MapAttributeConfig(String name, String extractor) {
        this.setName(name);
        this.setExtractor(extractor);
    }

    public MapAttributeConfig(MapAttributeConfig config) {
        this.name = config.getName();
        this.extractor = config.getExtractor();
    }

    public MapAttributeConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new MapAttributeConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public String getName() {
        return this.name;
    }

    public MapAttributeConfig setName(String name) {
        this.name = MapAttributeConfig.checkName(name);
        return this;
    }

    private static String checkName(String name) {
        Preconditions.checkHasText(name, "Map attribute name must contain text");
        MapAttributeConfig.checkNameValid(name);
        MapAttributeConfig.checkNotQueryConstant(name);
        return name;
    }

    private static void checkNameValid(String name) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Map attribute name is invalid. It may contain upper-case and lower-case letters, digits and underscores but an underscore may not be located at the first position).");
        }
    }

    private static void checkNotQueryConstant(String name) {
        for (QueryConstants constant : QueryConstants.values()) {
            if (!name.equals(constant.value())) continue;
            throw new IllegalArgumentException(String.format("Map attribute name must not contain query constant '%s'", constant.value()));
        }
    }

    public String getExtractor() {
        return this.extractor;
    }

    public MapAttributeConfig setExtractor(String extractor) {
        this.extractor = Preconditions.checkHasText(extractor, "Map attribute extractor must contain text");
        return this;
    }

    public String toString() {
        return "MapAttributeConfig{name='" + this.name + '\'' + "extractor='" + this.extractor + '\'' + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 17;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.extractor);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.extractor = in.readUTF();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MapAttributeConfig that = (MapAttributeConfig)o;
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return this.extractor != null ? this.extractor.equals(that.extractor) : that.extractor == null;
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + (this.extractor != null ? this.extractor.hashCode() : 0);
        return result;
    }
}

