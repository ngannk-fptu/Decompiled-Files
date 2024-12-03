/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.writer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import net.minidev.asm.BeansAccess;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONUtil;
import net.minidev.json.writer.JsonReader;
import net.minidev.json.writer.JsonReaderI;

public class CollectionMapper {

    public static class ListClass<T>
    extends JsonReaderI<T> {
        final Class<?> type;
        final Class<?> instance;
        final BeansAccess<?> ba;
        JsonReaderI<?> subMapper;

        public ListClass(JsonReader base, Class<?> clazz) {
            super(base);
            this.type = clazz;
            this.instance = clazz.isInterface() ? JSONArray.class : clazz;
            this.ba = BeansAccess.get(this.instance, JSONUtil.JSON_SMART_FIELD_FILTER);
        }

        @Override
        public Object createArray() {
            return this.ba.newInstance();
        }

        @Override
        public JsonReaderI<?> startArray(String key) {
            return this.base.DEFAULT;
        }

        @Override
        public JsonReaderI<?> startObject(String key) {
            return this.base.DEFAULT;
        }

        @Override
        public void addValue(Object current, Object value) {
            ((List)current).add(value);
        }
    }

    public static class ListType<T>
    extends JsonReaderI<T> {
        final ParameterizedType type;
        final Class<?> rawClass;
        final Class<?> instance;
        final BeansAccess<?> ba;
        final Type valueType;
        final Class<?> valueClass;
        JsonReaderI<?> subMapper;

        public ListType(JsonReader base, ParameterizedType type) {
            super(base);
            this.type = type;
            this.rawClass = (Class)type.getRawType();
            this.instance = this.rawClass.isInterface() ? JSONArray.class : this.rawClass;
            this.ba = BeansAccess.get(this.instance, JSONUtil.JSON_SMART_FIELD_FILTER);
            this.valueType = type.getActualTypeArguments()[0];
            this.valueClass = this.valueType instanceof Class ? (Class)this.valueType : (Class)((ParameterizedType)this.valueType).getRawType();
        }

        @Override
        public Object createArray() {
            return this.ba.newInstance();
        }

        @Override
        public JsonReaderI<?> startArray(String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.type.getActualTypeArguments()[0]);
            }
            return this.subMapper;
        }

        @Override
        public JsonReaderI<?> startObject(String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.type.getActualTypeArguments()[0]);
            }
            return this.subMapper;
        }

        @Override
        public void addValue(Object current, Object value) {
            ((List)current).add(JSONUtil.convertToX(value, this.valueClass));
        }
    }

    public static class MapClass<T>
    extends JsonReaderI<T> {
        final Class<?> type;
        final Class<?> instance;
        final BeansAccess<?> ba;
        JsonReaderI<?> subMapper;

        public MapClass(JsonReader base, Class<?> type) {
            super(base);
            this.type = type;
            this.instance = type.isInterface() ? JSONObject.class : type;
            this.ba = BeansAccess.get(this.instance, JSONUtil.JSON_SMART_FIELD_FILTER);
        }

        @Override
        public Object createObject() {
            return this.ba.newInstance();
        }

        @Override
        public JsonReaderI<?> startArray(String key) {
            return this.base.DEFAULT;
        }

        @Override
        public JsonReaderI<?> startObject(String key) {
            return this.base.DEFAULT;
        }

        @Override
        public void setValue(Object current, String key, Object value) {
            ((Map)current).put(key, value);
        }

        @Override
        public Object getValue(Object current, String key) {
            return ((Map)current).get(key);
        }

        @Override
        public Type getType(String key) {
            return this.type;
        }
    }

    public static class MapType<T>
    extends JsonReaderI<T> {
        final ParameterizedType type;
        final Class<?> rawClass;
        final Class<?> instance;
        final BeansAccess<?> ba;
        final Type keyType;
        final Type valueType;
        final Class<?> keyClass;
        final Class<?> valueClass;
        JsonReaderI<?> subMapper;

        public MapType(JsonReader base, ParameterizedType type) {
            super(base);
            this.type = type;
            this.rawClass = (Class)type.getRawType();
            this.instance = this.rawClass.isInterface() ? JSONObject.class : this.rawClass;
            this.ba = BeansAccess.get(this.instance, JSONUtil.JSON_SMART_FIELD_FILTER);
            this.keyType = type.getActualTypeArguments()[0];
            this.valueType = type.getActualTypeArguments()[1];
            this.keyClass = this.keyType instanceof Class ? (Class)this.keyType : (Class)((ParameterizedType)this.keyType).getRawType();
            this.valueClass = this.valueType instanceof Class ? (Class)this.valueType : (Class)((ParameterizedType)this.valueType).getRawType();
        }

        @Override
        public Object createObject() {
            try {
                return this.instance.newInstance();
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public JsonReaderI<?> startArray(String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.valueType);
            }
            return this.subMapper;
        }

        @Override
        public JsonReaderI<?> startObject(String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.valueType);
            }
            return this.subMapper;
        }

        @Override
        public void setValue(Object current, String key, Object value) {
            ((Map)current).put(JSONUtil.convertToX(key, this.keyClass), JSONUtil.convertToX(value, this.valueClass));
        }

        @Override
        public Object getValue(Object current, String key) {
            return ((Map)current).get(JSONUtil.convertToX(key, this.keyClass));
        }

        @Override
        public Type getType(String key) {
            return this.type;
        }
    }
}

