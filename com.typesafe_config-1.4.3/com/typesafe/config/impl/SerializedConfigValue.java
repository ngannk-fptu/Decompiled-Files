/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigBoolean;
import com.typesafe.config.impl.ConfigDouble;
import com.typesafe.config.impl.ConfigInt;
import com.typesafe.config.impl.ConfigLong;
import com.typesafe.config.impl.ConfigNull;
import com.typesafe.config.impl.ConfigNumber;
import com.typesafe.config.impl.ConfigString;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SimpleConfigList;
import com.typesafe.config.impl.SimpleConfigObject;
import com.typesafe.config.impl.SimpleConfigOrigin;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SerializedConfigValue
extends AbstractConfigValue
implements Externalizable {
    private static final long serialVersionUID = 1L;
    private ConfigValue value;
    private boolean wasConfig;

    public SerializedConfigValue() {
        super(null);
    }

    SerializedConfigValue(ConfigValue value) {
        this();
        this.value = value;
        this.wasConfig = false;
    }

    SerializedConfigValue(Config conf) {
        this(conf.root());
        this.wasConfig = true;
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.wasConfig) {
            return ((ConfigObject)this.value).toConfig();
        }
        return this.value;
    }

    private static void writeOriginField(DataOutput out, SerializedField code, Object v) throws IOException {
        switch (code) {
            case ORIGIN_DESCRIPTION: {
                out.writeUTF((String)v);
                break;
            }
            case ORIGIN_LINE_NUMBER: {
                out.writeInt((Integer)v);
                break;
            }
            case ORIGIN_END_LINE_NUMBER: {
                out.writeInt((Integer)v);
                break;
            }
            case ORIGIN_TYPE: {
                out.writeByte((Integer)v);
                break;
            }
            case ORIGIN_URL: {
                out.writeUTF((String)v);
                break;
            }
            case ORIGIN_RESOURCE: {
                out.writeUTF((String)v);
                break;
            }
            case ORIGIN_COMMENTS: {
                List list = (List)v;
                int size = list.size();
                out.writeInt(size);
                for (String s : list) {
                    out.writeUTF(s);
                }
                break;
            }
            case ORIGIN_NULL_URL: 
            case ORIGIN_NULL_RESOURCE: 
            case ORIGIN_NULL_COMMENTS: {
                break;
            }
            default: {
                throw new IOException("Unhandled field from origin: " + (Object)((Object)code));
            }
        }
    }

    static void writeOrigin(DataOutput out, SimpleConfigOrigin origin, SimpleConfigOrigin baseOrigin) throws IOException {
        Map<Object, Object> m = origin != null ? origin.toFieldsDelta(baseOrigin) : Collections.emptyMap();
        for (Map.Entry e : m.entrySet()) {
            FieldOut field = new FieldOut((SerializedField)((Object)e.getKey()));
            Object v = e.getValue();
            SerializedConfigValue.writeOriginField(field.data, field.code, v);
            SerializedConfigValue.writeField(out, field);
        }
        SerializedConfigValue.writeEndMarker(out);
    }

    static SimpleConfigOrigin readOrigin(DataInput in, SimpleConfigOrigin baseOrigin) throws IOException {
        EnumMap<SerializedField, Object> m = new EnumMap<SerializedField, Object>(SerializedField.class);
        while (true) {
            ArrayList<String> v = null;
            SerializedField field = SerializedConfigValue.readCode(in);
            switch (field) {
                case END_MARKER: {
                    return SimpleConfigOrigin.fromBase(baseOrigin, m);
                }
                case ORIGIN_DESCRIPTION: {
                    in.readInt();
                    v = in.readUTF();
                    break;
                }
                case ORIGIN_LINE_NUMBER: {
                    in.readInt();
                    v = in.readInt();
                    break;
                }
                case ORIGIN_END_LINE_NUMBER: {
                    in.readInt();
                    v = in.readInt();
                    break;
                }
                case ORIGIN_TYPE: {
                    in.readInt();
                    v = in.readUnsignedByte();
                    break;
                }
                case ORIGIN_URL: {
                    in.readInt();
                    v = in.readUTF();
                    break;
                }
                case ORIGIN_RESOURCE: {
                    in.readInt();
                    v = in.readUTF();
                    break;
                }
                case ORIGIN_COMMENTS: {
                    in.readInt();
                    int size = in.readInt();
                    ArrayList<String> list = new ArrayList<String>(size);
                    for (int i = 0; i < size; ++i) {
                        list.add(in.readUTF());
                    }
                    v = list;
                    break;
                }
                case ORIGIN_NULL_URL: 
                case ORIGIN_NULL_RESOURCE: 
                case ORIGIN_NULL_COMMENTS: {
                    in.readInt();
                    v = "";
                    break;
                }
                case ROOT_VALUE: 
                case ROOT_WAS_CONFIG: 
                case VALUE_DATA: 
                case VALUE_ORIGIN: {
                    throw new IOException("Not expecting this field here: " + (Object)((Object)field));
                }
                case UNKNOWN: {
                    SerializedConfigValue.skipField(in);
                }
            }
            if (v == null) continue;
            m.put(field, (Object)v);
        }
    }

    private static void writeValueData(DataOutput out, ConfigValue value) throws IOException {
        SerializedValueType st = SerializedValueType.forValue(value);
        out.writeByte(st.ordinal());
        switch (st) {
            case BOOLEAN: {
                out.writeBoolean(((ConfigBoolean)value).unwrapped());
                break;
            }
            case NULL: {
                break;
            }
            case INT: {
                out.writeInt(((ConfigInt)value).unwrapped());
                out.writeUTF(((ConfigNumber)value).transformToString());
                break;
            }
            case LONG: {
                out.writeLong(((ConfigLong)value).unwrapped());
                out.writeUTF(((ConfigNumber)value).transformToString());
                break;
            }
            case DOUBLE: {
                out.writeDouble(((ConfigDouble)value).unwrapped());
                out.writeUTF(((ConfigNumber)value).transformToString());
                break;
            }
            case STRING: {
                out.writeUTF(((ConfigString)value).unwrapped());
                break;
            }
            case LIST: {
                ConfigList list = (ConfigList)value;
                out.writeInt(list.size());
                for (ConfigValue v : list) {
                    SerializedConfigValue.writeValue(out, v, (SimpleConfigOrigin)list.origin());
                }
                break;
            }
            case OBJECT: {
                ConfigObject obj = (ConfigObject)value;
                out.writeInt(obj.size());
                for (Map.Entry e : obj.entrySet()) {
                    out.writeUTF((String)e.getKey());
                    SerializedConfigValue.writeValue(out, (ConfigValue)e.getValue(), (SimpleConfigOrigin)obj.origin());
                }
                break;
            }
        }
    }

    private static AbstractConfigValue readValueData(DataInput in, SimpleConfigOrigin origin) throws IOException {
        int stb = in.readUnsignedByte();
        SerializedValueType st = SerializedValueType.forInt(stb);
        if (st == null) {
            throw new IOException("Unknown serialized value type: " + stb);
        }
        switch (st) {
            case BOOLEAN: {
                return new ConfigBoolean(origin, in.readBoolean());
            }
            case NULL: {
                return new ConfigNull(origin);
            }
            case INT: {
                int vi = in.readInt();
                String si = in.readUTF();
                return new ConfigInt(origin, vi, si);
            }
            case LONG: {
                long vl = in.readLong();
                String sl = in.readUTF();
                return new ConfigLong(origin, vl, sl);
            }
            case DOUBLE: {
                double vd = in.readDouble();
                String sd = in.readUTF();
                return new ConfigDouble(origin, vd, sd);
            }
            case STRING: {
                return new ConfigString.Quoted(origin, in.readUTF());
            }
            case LIST: {
                int listSize = in.readInt();
                ArrayList<AbstractConfigValue> list = new ArrayList<AbstractConfigValue>(listSize);
                for (int i = 0; i < listSize; ++i) {
                    AbstractConfigValue v = SerializedConfigValue.readValue(in, origin);
                    list.add(v);
                }
                return new SimpleConfigList(origin, list);
            }
            case OBJECT: {
                int mapSize = in.readInt();
                HashMap<String, AbstractConfigValue> map = new HashMap<String, AbstractConfigValue>(mapSize);
                for (int i = 0; i < mapSize; ++i) {
                    String key = in.readUTF();
                    AbstractConfigValue v = SerializedConfigValue.readValue(in, origin);
                    map.put(key, v);
                }
                return new SimpleConfigObject(origin, map);
            }
        }
        throw new IOException("Unhandled serialized value type: " + (Object)((Object)st));
    }

    private static void writeValue(DataOutput out, ConfigValue value, SimpleConfigOrigin baseOrigin) throws IOException {
        FieldOut origin = new FieldOut(SerializedField.VALUE_ORIGIN);
        SerializedConfigValue.writeOrigin(origin.data, (SimpleConfigOrigin)value.origin(), baseOrigin);
        SerializedConfigValue.writeField(out, origin);
        FieldOut data = new FieldOut(SerializedField.VALUE_DATA);
        SerializedConfigValue.writeValueData(data.data, value);
        SerializedConfigValue.writeField(out, data);
        SerializedConfigValue.writeEndMarker(out);
    }

    private static AbstractConfigValue readValue(DataInput in, SimpleConfigOrigin baseOrigin) throws IOException {
        AbstractConfigValue value = null;
        SimpleConfigOrigin origin = null;
        while (true) {
            SerializedField code;
            if ((code = SerializedConfigValue.readCode(in)) == SerializedField.END_MARKER) {
                if (value == null) {
                    throw new IOException("No value data found in serialization of value");
                }
                return value;
            }
            if (code == SerializedField.VALUE_DATA) {
                if (origin == null) {
                    throw new IOException("Origin must be stored before value data");
                }
                in.readInt();
                value = SerializedConfigValue.readValueData(in, origin);
                continue;
            }
            if (code == SerializedField.VALUE_ORIGIN) {
                in.readInt();
                origin = SerializedConfigValue.readOrigin(in, baseOrigin);
                continue;
            }
            SerializedConfigValue.skipField(in);
        }
    }

    private static void writeField(DataOutput out, FieldOut field) throws IOException {
        byte[] bytes = field.bytes.toByteArray();
        out.writeByte(field.code.ordinal());
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private static void writeEndMarker(DataOutput out) throws IOException {
        out.writeByte(SerializedField.END_MARKER.ordinal());
    }

    private static SerializedField readCode(DataInput in) throws IOException {
        int c = in.readUnsignedByte();
        if (c == SerializedField.UNKNOWN.ordinal()) {
            throw new IOException("field code " + c + " is not supposed to be on the wire");
        }
        return SerializedField.forInt(c);
    }

    private static void skipField(DataInput in) throws IOException {
        int len = in.readInt();
        int skipped = in.skipBytes(len);
        if (skipped < len) {
            byte[] bytes = new byte[len - skipped];
            in.readFully(bytes);
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        if (((AbstractConfigValue)this.value).resolveStatus() != ResolveStatus.RESOLVED) {
            throw new NotSerializableException("tried to serialize a value with unresolved substitutions, need to Config#resolve() first, see API docs");
        }
        FieldOut field = new FieldOut(SerializedField.ROOT_VALUE);
        SerializedConfigValue.writeValue(field.data, this.value, null);
        SerializedConfigValue.writeField(out, field);
        field = new FieldOut(SerializedField.ROOT_WAS_CONFIG);
        field.data.writeBoolean(this.wasConfig);
        SerializedConfigValue.writeField(out, field);
        SerializedConfigValue.writeEndMarker(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        SerializedField code;
        while ((code = SerializedConfigValue.readCode(in)) != SerializedField.END_MARKER) {
            DataInput input = this.fieldIn(in);
            if (code == SerializedField.ROOT_VALUE) {
                this.value = SerializedConfigValue.readValue(input, null);
                continue;
            }
            if (code != SerializedField.ROOT_WAS_CONFIG) continue;
            this.wasConfig = input.readBoolean();
        }
        return;
    }

    private DataInput fieldIn(ObjectInput in) throws IOException {
        byte[] bytes = new byte[in.readInt()];
        in.readFully(bytes);
        return new DataInputStream(new ByteArrayInputStream(bytes));
    }

    private static ConfigException shouldNotBeUsed() {
        return new ConfigException.BugOrBroken(SerializedConfigValue.class.getName() + " should not exist outside of serialization");
    }

    @Override
    public ConfigValueType valueType() {
        throw SerializedConfigValue.shouldNotBeUsed();
    }

    @Override
    public Object unwrapped() {
        throw SerializedConfigValue.shouldNotBeUsed();
    }

    @Override
    protected SerializedConfigValue newCopy(ConfigOrigin origin) {
        throw SerializedConfigValue.shouldNotBeUsed();
    }

    @Override
    public final String toString() {
        return this.getClass().getSimpleName() + "(value=" + this.value + ",wasConfig=" + this.wasConfig + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SerializedConfigValue) {
            return this.canEqual(other) && this.wasConfig == ((SerializedConfigValue)other).wasConfig && this.value.equals(((SerializedConfigValue)other).value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 41 * (41 + this.value.hashCode());
        h = 41 * (h + (this.wasConfig ? 1 : 0));
        return h;
    }

    private static class FieldOut {
        final SerializedField code;
        final ByteArrayOutputStream bytes;
        final DataOutput data;

        FieldOut(SerializedField code) {
            this.code = code;
            this.bytes = new ByteArrayOutputStream();
            this.data = new DataOutputStream(this.bytes);
        }
    }

    private static enum SerializedValueType {
        NULL(ConfigValueType.NULL),
        BOOLEAN(ConfigValueType.BOOLEAN),
        INT(ConfigValueType.NUMBER),
        LONG(ConfigValueType.NUMBER),
        DOUBLE(ConfigValueType.NUMBER),
        STRING(ConfigValueType.STRING),
        LIST(ConfigValueType.LIST),
        OBJECT(ConfigValueType.OBJECT);

        ConfigValueType configType;

        private SerializedValueType(ConfigValueType configType) {
            this.configType = configType;
        }

        static SerializedValueType forInt(int b) {
            if (b < SerializedValueType.values().length) {
                return SerializedValueType.values()[b];
            }
            return null;
        }

        static SerializedValueType forValue(ConfigValue value) {
            ConfigValueType t = value.valueType();
            if (t == ConfigValueType.NUMBER) {
                if (value instanceof ConfigInt) {
                    return INT;
                }
                if (value instanceof ConfigLong) {
                    return LONG;
                }
                if (value instanceof ConfigDouble) {
                    return DOUBLE;
                }
            } else {
                for (SerializedValueType st : SerializedValueType.values()) {
                    if (st.configType != t) continue;
                    return st;
                }
            }
            throw new ConfigException.BugOrBroken("don't know how to serialize " + value);
        }
    }

    static enum SerializedField {
        UNKNOWN,
        END_MARKER,
        ROOT_VALUE,
        ROOT_WAS_CONFIG,
        VALUE_DATA,
        VALUE_ORIGIN,
        ORIGIN_DESCRIPTION,
        ORIGIN_LINE_NUMBER,
        ORIGIN_END_LINE_NUMBER,
        ORIGIN_TYPE,
        ORIGIN_URL,
        ORIGIN_COMMENTS,
        ORIGIN_NULL_URL,
        ORIGIN_NULL_COMMENTS,
        ORIGIN_RESOURCE,
        ORIGIN_NULL_RESOURCE;


        static SerializedField forInt(int b) {
            if (b < SerializedField.values().length) {
                return SerializedField.values()[b];
            }
            return UNKNOWN;
        }
    }
}

