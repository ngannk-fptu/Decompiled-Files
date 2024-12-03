/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.jsontype.impl;

import java.util.Collection;
import java.util.HashMap;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.impl.TypeIdResolverBase;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TypeNameIdResolver
extends TypeIdResolverBase {
    protected final MapperConfig<?> _config;
    protected final HashMap<String, String> _typeToId;
    protected final HashMap<String, JavaType> _idToType;

    protected TypeNameIdResolver(MapperConfig<?> config, JavaType baseType, HashMap<String, String> typeToId, HashMap<String, JavaType> idToType) {
        super(baseType, config.getTypeFactory());
        this._config = config;
        this._typeToId = typeToId;
        this._idToType = idToType;
    }

    public static TypeNameIdResolver construct(MapperConfig<?> config, JavaType baseType, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
        if (forSer == forDeser) {
            throw new IllegalArgumentException();
        }
        HashMap<String, String> typeToId = null;
        HashMap<String, JavaType> idToType = null;
        if (forSer) {
            typeToId = new HashMap<String, String>();
        }
        if (forDeser) {
            idToType = new HashMap<String, JavaType>();
        }
        if (subtypes != null) {
            for (NamedType t : subtypes) {
                JavaType prev;
                String id;
                Class<?> cls = t.getType();
                String string = id = t.hasName() ? t.getName() : TypeNameIdResolver._defaultTypeId(cls);
                if (forSer) {
                    typeToId.put(cls.getName(), id);
                }
                if (!forDeser || (prev = idToType.get(id)) != null && cls.isAssignableFrom(prev.getRawClass())) continue;
                idToType.put(id, config.constructType(cls));
            }
        }
        return new TypeNameIdResolver(config, baseType, typeToId, idToType);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String idFromValue(Object value) {
        String name;
        Class<?> cls = value.getClass();
        String key = cls.getName();
        HashMap<String, String> hashMap = this._typeToId;
        synchronized (hashMap) {
            name = this._typeToId.get(key);
            if (name == null) {
                if (this._config.isAnnotationProcessingEnabled()) {
                    BasicBeanDescription beanDesc = (BasicBeanDescription)this._config.introspectClassAnnotations(cls);
                    name = this._config.getAnnotationIntrospector().findTypeName(beanDesc.getClassInfo());
                }
                if (name == null) {
                    name = TypeNameIdResolver._defaultTypeId(cls);
                }
                this._typeToId.put(key, name);
            }
        }
        return name;
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> type) {
        return this.idFromValue(value);
    }

    @Override
    public JavaType typeFromId(String id) throws IllegalArgumentException {
        JavaType t = this._idToType.get(id);
        return t;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(this.getClass().getName());
        sb.append("; id-to-type=").append(this._idToType);
        sb.append(']');
        return sb.toString();
    }

    protected static String _defaultTypeId(Class<?> cls) {
        String n = cls.getName();
        int ix = n.lastIndexOf(46);
        return ix < 0 ? n : n.substring(ix + 1);
    }
}

