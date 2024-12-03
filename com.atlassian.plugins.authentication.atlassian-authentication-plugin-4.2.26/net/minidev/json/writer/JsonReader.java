/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.writer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONAwareEx;
import net.minidev.json.JSONObject;
import net.minidev.json.writer.ArraysMapper;
import net.minidev.json.writer.BeansMapper;
import net.minidev.json.writer.CollectionMapper;
import net.minidev.json.writer.DefaultMapper;
import net.minidev.json.writer.DefaultMapperCollection;
import net.minidev.json.writer.DefaultMapperOrdered;
import net.minidev.json.writer.JsonReaderI;
import net.minidev.json.writer.MapperRemapped;

public class JsonReader {
    private final ConcurrentHashMap<Type, JsonReaderI<?>> cache = new ConcurrentHashMap(100);
    public JsonReaderI<JSONAwareEx> DEFAULT;
    public JsonReaderI<JSONAwareEx> DEFAULT_ORDERED;

    public JsonReader() {
        this.cache.put((Type)((Object)Date.class), BeansMapper.MAPPER_DATE);
        this.cache.put((Type)((Object)int[].class), ArraysMapper.MAPPER_PRIM_INT);
        this.cache.put((Type)((Object)Integer[].class), ArraysMapper.MAPPER_INT);
        this.cache.put((Type)((Object)short[].class), ArraysMapper.MAPPER_PRIM_INT);
        this.cache.put((Type)((Object)Short[].class), ArraysMapper.MAPPER_INT);
        this.cache.put((Type)((Object)long[].class), ArraysMapper.MAPPER_PRIM_LONG);
        this.cache.put((Type)((Object)Long[].class), ArraysMapper.MAPPER_LONG);
        this.cache.put((Type)((Object)byte[].class), ArraysMapper.MAPPER_PRIM_BYTE);
        this.cache.put((Type)((Object)Byte[].class), ArraysMapper.MAPPER_BYTE);
        this.cache.put((Type)((Object)char[].class), ArraysMapper.MAPPER_PRIM_CHAR);
        this.cache.put((Type)((Object)Character[].class), ArraysMapper.MAPPER_CHAR);
        this.cache.put((Type)((Object)float[].class), ArraysMapper.MAPPER_PRIM_FLOAT);
        this.cache.put((Type)((Object)Float[].class), ArraysMapper.MAPPER_FLOAT);
        this.cache.put((Type)((Object)double[].class), ArraysMapper.MAPPER_PRIM_DOUBLE);
        this.cache.put((Type)((Object)Double[].class), ArraysMapper.MAPPER_DOUBLE);
        this.cache.put((Type)((Object)boolean[].class), ArraysMapper.MAPPER_PRIM_BOOL);
        this.cache.put((Type)((Object)Boolean[].class), ArraysMapper.MAPPER_BOOL);
        this.DEFAULT = new DefaultMapper<JSONAwareEx>(this);
        this.DEFAULT_ORDERED = new DefaultMapperOrdered(this);
        this.cache.put((Type)((Object)JSONAwareEx.class), this.DEFAULT);
        this.cache.put((Type)((Object)JSONAware.class), this.DEFAULT);
        this.cache.put((Type)((Object)JSONArray.class), this.DEFAULT);
        this.cache.put((Type)((Object)JSONObject.class), this.DEFAULT);
    }

    public <T> void remapField(Class<T> type, String fromJson, String toJava) {
        JsonReaderI<T> map = this.getMapper(type);
        if (!(map instanceof MapperRemapped)) {
            map = new MapperRemapped<T>(map);
            this.registerReader(type, map);
        }
        ((MapperRemapped)map).renameField(fromJson, toJava);
    }

    public <T> void registerReader(Class<T> type, JsonReaderI<T> mapper) {
        this.cache.put(type, mapper);
    }

    public <T> JsonReaderI<T> getMapper(Type type) {
        if (type instanceof ParameterizedType) {
            return this.getMapper((ParameterizedType)type);
        }
        return this.getMapper((Class)type);
    }

    public <T> JsonReaderI<T> getMapper(Class<T> type) {
        JsonReaderI map = this.cache.get(type);
        if (map != null) {
            return map;
        }
        if (type instanceof Class) {
            if (Map.class.isAssignableFrom(type)) {
                map = new DefaultMapperCollection(this, type);
            } else if (List.class.isAssignableFrom(type)) {
                map = new DefaultMapperCollection(this, type);
            }
            if (map != null) {
                this.cache.put(type, map);
                return map;
            }
        }
        map = type.isArray() ? new ArraysMapper.GenericMapper(this, type) : (List.class.isAssignableFrom(type) ? new CollectionMapper.ListClass(this, type) : (Map.class.isAssignableFrom(type) ? new CollectionMapper.MapClass(this, type) : new BeansMapper.Bean(this, type)));
        this.cache.putIfAbsent(type, map);
        return map;
    }

    public <T> JsonReaderI<T> getMapper(ParameterizedType type) {
        JsonReaderI<?> map = this.cache.get(type);
        if (map != null) {
            return map;
        }
        Class clz = (Class)type.getRawType();
        if (List.class.isAssignableFrom(clz)) {
            map = new CollectionMapper.ListType(this, type);
        } else if (Map.class.isAssignableFrom(clz)) {
            map = new CollectionMapper.MapType(this, type);
        }
        this.cache.putIfAbsent(type, map);
        return map;
    }
}

