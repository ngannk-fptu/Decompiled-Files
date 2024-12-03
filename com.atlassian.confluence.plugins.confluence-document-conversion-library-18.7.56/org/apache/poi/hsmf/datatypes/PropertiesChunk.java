/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hsmf.datatypes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hsmf.datatypes.Chunk;
import org.apache.poi.hsmf.datatypes.ChunkBasedPropertyValue;
import org.apache.poi.hsmf.datatypes.ChunkGroup;
import org.apache.poi.hsmf.datatypes.MAPIProperty;
import org.apache.poi.hsmf.datatypes.PropertyValue;
import org.apache.poi.hsmf.datatypes.Types;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

public abstract class PropertiesChunk
extends Chunk {
    public static final String NAME = "__properties_version1.0";
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;
    public static final String VARIABLE_LENGTH_PROPERTY_PREFIX = "__substg1.0_";
    public static final int PROPERTIES_FLAG_READABLE = 2;
    public static final int PROPERTIES_FLAG_WRITEABLE = 4;
    private static final Logger LOG = LogManager.getLogger(PropertiesChunk.class);
    private final Map<MAPIProperty, PropertyValue> properties = new HashMap<MAPIProperty, PropertyValue>();
    private final ChunkGroup parentGroup;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    protected PropertiesChunk(ChunkGroup parentGroup) {
        super(NAME, -1, Types.UNKNOWN);
        this.parentGroup = parentGroup;
    }

    @Override
    public String getEntryName() {
        return NAME;
    }

    public Map<MAPIProperty, PropertyValue> getRawProperties() {
        return this.properties;
    }

    public Map<MAPIProperty, List<PropertyValue>> getProperties() {
        HashMap<MAPIProperty, List<PropertyValue>> props = new HashMap<MAPIProperty, List<PropertyValue>>(this.properties.size());
        for (MAPIProperty prop : this.properties.keySet()) {
            props.put(prop, this.getValues(prop));
        }
        return props;
    }

    public void setProperty(PropertyValue value) {
        this.properties.put(value.getProperty(), value);
    }

    public List<PropertyValue> getValues(MAPIProperty property) {
        PropertyValue val = this.properties.get(property);
        if (val == null) {
            return null;
        }
        if (val instanceof ChunkBasedPropertyValue) {
            return Collections.emptyList();
        }
        return Collections.singletonList(val);
    }

    public PropertyValue getRawValue(MAPIProperty property) {
        return this.properties.get(property);
    }

    protected void matchVariableSizedPropertiesToChunks() {
        HashMap<Integer, Chunk> chunks = new HashMap<Integer, Chunk>();
        for (Chunk chunk : this.parentGroup.getChunks()) {
            chunks.put(chunk.getChunkId(), chunk);
        }
        for (PropertyValue val : this.properties.values()) {
            Chunk chunk;
            if (!(val instanceof ChunkBasedPropertyValue)) continue;
            ChunkBasedPropertyValue cVal = (ChunkBasedPropertyValue)val;
            chunk = (Chunk)chunks.get(cVal.getProperty().id);
            if (chunk != null) {
                cVal.setValue(chunk);
                continue;
            }
            LOG.atWarn().log("No chunk found matching Property {}", (Object)cVal);
        }
    }

    protected void readProperties(InputStream value) throws IOException {
        boolean going = true;
        while (going) {
            try {
                int typeID = LittleEndian.readUShort(value);
                int id = LittleEndian.readUShort(value);
                long flags = LittleEndian.readUInt(value);
                Types.MAPIType type = Types.getById(typeID);
                MAPIProperty prop = MAPIProperty.get(id);
                if (prop == MAPIProperty.UNKNOWN) {
                    prop = MAPIProperty.createCustom(id, type, "Unknown " + id);
                }
                if (type == null) {
                    LOG.atWarn().log("Invalid type found, expected {} but got {} for property {}", (Object)prop.usualType, (Object)Unbox.box(typeID), (Object)prop);
                    going = false;
                    break;
                }
                if (!(prop.usualType == type || type == Types.ASCII_STRING && prop.usualType == Types.UNICODE_STRING || type == Types.UNICODE_STRING && prop.usualType == Types.ASCII_STRING)) {
                    if (prop.usualType == Types.UNKNOWN) {
                        LOG.atInfo().log("Property definition for {} is missing a type definition, found a value with type {}", (Object)prop, (Object)type);
                    } else {
                        LOG.atWarn().log("Type mismatch, expected {} but got {} for property {}", (Object)prop.usualType, (Object)type, (Object)prop);
                        going = false;
                        break;
                    }
                }
                boolean isPointer = false;
                int length = type.getLength();
                if (!type.isFixedLength()) {
                    isPointer = true;
                    length = 8;
                }
                byte[] data = IOUtils.safelyAllocate(length, MAX_RECORD_LENGTH);
                IOUtils.readFully(value, data);
                if (length < 8) {
                    byte[] padding = new byte[8 - length];
                    IOUtils.readFully(value, padding);
                }
                PropertyValue propVal = isPointer ? new ChunkBasedPropertyValue(prop, flags, data, type) : (type == Types.NULL ? new PropertyValue.NullPropertyValue(prop, flags, data) : (type == Types.BOOLEAN ? new PropertyValue.BooleanPropertyValue(prop, flags, data) : (type == Types.SHORT ? new PropertyValue.ShortPropertyValue(prop, flags, data) : (type == Types.LONG ? new PropertyValue.LongPropertyValue(prop, flags, data) : (type == Types.LONG_LONG ? new PropertyValue.LongLongPropertyValue(prop, flags, data) : (type == Types.FLOAT ? new PropertyValue.FloatPropertyValue(prop, flags, data) : (type == Types.DOUBLE ? new PropertyValue.DoublePropertyValue(prop, flags, data) : (type == Types.CURRENCY ? new PropertyValue.CurrencyPropertyValue(prop, flags, data) : (type == Types.TIME ? new PropertyValue.TimePropertyValue(prop, flags, data) : new PropertyValue(prop, flags, data, type))))))))));
                if (this.properties.get(prop) != null) {
                    LOG.atWarn().log("Duplicate values found for {}", (Object)prop);
                }
                this.properties.put(prop, propVal);
            }
            catch (LittleEndian.BufferUnderrunException e) {
                going = false;
            }
        }
    }

    public void writeProperties(DirectoryEntry directory) throws IOException {
        try (UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();){
            List<PropertyValue> values = this.writeProperties((OutputStream)baos);
            try (InputStream is = baos.toInputStream();){
                directory.createDocument(NAME, is);
            }
            this.writeNodeData(directory, values);
        }
    }

    protected void writeNodeData(DirectoryEntry directory, List<PropertyValue> values) throws IOException {
        for (PropertyValue value : values) {
            byte[] bytes = value.getRawValue();
            String nodeName = VARIABLE_LENGTH_PROPERTY_PREFIX + this.getFileName(value.getProperty(), value.getActualType());
            directory.createDocument(nodeName, new ByteArrayInputStream(bytes));
        }
    }

    protected List<PropertyValue> writeProperties(OutputStream out) throws IOException {
        ArrayList<PropertyValue> variableLengthProperties = new ArrayList<PropertyValue>();
        for (Map.Entry<MAPIProperty, PropertyValue> entry : this.properties.entrySet()) {
            MAPIProperty property = entry.getKey();
            PropertyValue value = entry.getValue();
            if (value == null || property.id < 0) continue;
            long tag = Long.parseLong(this.getFileName(property, value.getActualType()), 16);
            LittleEndian.putUInt(tag, out);
            LittleEndian.putUInt(value.getFlags(), out);
            Types.MAPIType type = this.getTypeMapping(value.getActualType());
            if (type.isFixedLength()) {
                this.writeFixedLengthValueHeader(out, property, type, value);
                continue;
            }
            this.writeVariableLengthValueHeader(out, property, type, value);
            variableLengthProperties.add(value);
        }
        return variableLengthProperties;
    }

    private void writeFixedLengthValueHeader(OutputStream out, MAPIProperty property, Types.MAPIType type, PropertyValue value) throws IOException {
        int length;
        byte[] bytes = value.getRawValue();
        int n = length = bytes != null ? bytes.length : 0;
        if (bytes != null) {
            out.write(bytes);
        }
        out.write(new byte[8 - length]);
    }

    private void writeVariableLengthValueHeader(OutputStream out, MAPIProperty propertyEx, Types.MAPIType type, PropertyValue value) throws IOException {
        int length;
        byte[] bytes = value.getRawValue();
        int n = length = bytes != null ? bytes.length : 0;
        if (type == Types.UNICODE_STRING) {
            length += 2;
        } else if (type == Types.ASCII_STRING) {
            ++length;
        }
        LittleEndian.putUInt(length, out);
        LittleEndian.putUInt(0L, out);
    }

    private String getFileName(MAPIProperty property, Types.MAPIType actualType) {
        StringBuilder str = new StringBuilder(Integer.toHexString(property.id).toUpperCase(Locale.ROOT));
        int need0count = 4 - str.length();
        if (need0count > 0) {
            str.insert(0, StringUtil.repeat('0', need0count));
        }
        Types.MAPIType type = this.getTypeMapping(actualType);
        return str + type.asFileEnding();
    }

    private Types.MAPIType getTypeMapping(Types.MAPIType type) {
        return type == Types.ASCII_STRING ? Types.UNICODE_STRING : type;
    }
}

