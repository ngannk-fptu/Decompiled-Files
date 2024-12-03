/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.ser;

import java.util.HashSet;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
@JacksonStdImpl
public class MapSerializer
extends org.codehaus.jackson.map.ser.std.MapSerializer {
    protected MapSerializer() {
        this(null, null, null, false, null, null, null, null);
    }

    @Deprecated
    protected MapSerializer(HashSet<String> ignoredEntries, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts) {
        super(ignoredEntries, UNSPECIFIED_TYPE, valueType, valueTypeIsStatic, vts, null, null, null);
    }

    @Deprecated
    protected MapSerializer(HashSet<String> ignoredEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<Object> keySerializer, BeanProperty property) {
        super(ignoredEntries, keyType, valueType, valueTypeIsStatic, vts, keySerializer, null, property);
    }

    protected MapSerializer(HashSet<String> ignoredEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, BeanProperty property) {
        super(ignoredEntries, keyType, valueType, valueTypeIsStatic, vts, keySerializer, valueSerializer, property);
    }
}

