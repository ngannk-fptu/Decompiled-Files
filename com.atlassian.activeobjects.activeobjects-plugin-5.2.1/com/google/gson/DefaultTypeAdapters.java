/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.DefaultConstructorAllocator;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializationContextDefault;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializerExceptionWrapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.MapTypeAdapter;
import com.google.gson.ObjectConstructor;
import com.google.gson.ParameterizedTypeHandlerMap;
import com.google.gson.internal.$Gson$Types;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class DefaultTypeAdapters {
    private static final DefaultDateTypeAdapter DATE_TYPE_ADAPTER = new DefaultDateTypeAdapter();
    private static final DefaultJavaSqlDateTypeAdapter JAVA_SQL_DATE_TYPE_ADAPTER = new DefaultJavaSqlDateTypeAdapter();
    private static final DefaultTimeTypeAdapter TIME_TYPE_ADAPTER = new DefaultTimeTypeAdapter();
    private static final DefaultTimestampDeserializer TIMESTAMP_DESERIALIZER = new DefaultTimestampDeserializer();
    private static final EnumTypeAdapter ENUM_TYPE_ADAPTER = new EnumTypeAdapter();
    private static final UrlTypeAdapter URL_TYPE_ADAPTER = new UrlTypeAdapter();
    private static final UriTypeAdapter URI_TYPE_ADAPTER = new UriTypeAdapter();
    private static final UuidTypeAdapter UUUID_TYPE_ADAPTER = new UuidTypeAdapter();
    private static final LocaleTypeAdapter LOCALE_TYPE_ADAPTER = new LocaleTypeAdapter();
    private static final DefaultInetAddressAdapter INET_ADDRESS_ADAPTER = new DefaultInetAddressAdapter();
    private static final CollectionTypeAdapter COLLECTION_TYPE_ADAPTER = new CollectionTypeAdapter();
    private static final MapTypeAdapter MAP_TYPE_ADAPTER = new MapTypeAdapter();
    private static final BigDecimalTypeAdapter BIG_DECIMAL_TYPE_ADAPTER = new BigDecimalTypeAdapter();
    private static final BigIntegerTypeAdapter BIG_INTEGER_TYPE_ADAPTER = new BigIntegerTypeAdapter();
    private static final BooleanTypeAdapter BOOLEAN_TYPE_ADAPTER = new BooleanTypeAdapter();
    private static final ByteTypeAdapter BYTE_TYPE_ADAPTER = new ByteTypeAdapter();
    private static final CharacterTypeAdapter CHARACTER_TYPE_ADAPTER = new CharacterTypeAdapter();
    private static final DoubleDeserializer DOUBLE_TYPE_ADAPTER = new DoubleDeserializer();
    private static final FloatDeserializer FLOAT_TYPE_ADAPTER = new FloatDeserializer();
    private static final IntegerTypeAdapter INTEGER_TYPE_ADAPTER = new IntegerTypeAdapter();
    private static final LongDeserializer LONG_DESERIALIZER = new LongDeserializer();
    private static final NumberTypeAdapter NUMBER_TYPE_ADAPTER = new NumberTypeAdapter();
    private static final ShortTypeAdapter SHORT_TYPE_ADAPTER = new ShortTypeAdapter();
    private static final StringTypeAdapter STRING_TYPE_ADAPTER = new StringTypeAdapter();
    private static final StringBuilderTypeAdapter STRING_BUILDER_TYPE_ADAPTER = new StringBuilderTypeAdapter();
    private static final StringBufferTypeAdapter STRING_BUFFER_TYPE_ADAPTER = new StringBufferTypeAdapter();
    private static final GregorianCalendarTypeAdapter GREGORIAN_CALENDAR_TYPE_ADAPTER = new GregorianCalendarTypeAdapter();
    private static final ParameterizedTypeHandlerMap<JsonSerializer<?>> DEFAULT_SERIALIZERS = DefaultTypeAdapters.createDefaultSerializers();
    static final ParameterizedTypeHandlerMap<JsonSerializer<?>> DEFAULT_HIERARCHY_SERIALIZERS = DefaultTypeAdapters.createDefaultHierarchySerializers();
    private static final ParameterizedTypeHandlerMap<JsonDeserializer<?>> DEFAULT_DESERIALIZERS = DefaultTypeAdapters.createDefaultDeserializers();
    static final ParameterizedTypeHandlerMap<JsonDeserializer<?>> DEFAULT_HIERARCHY_DESERIALIZERS = DefaultTypeAdapters.createDefaultHierarchyDeserializers();
    private static final ParameterizedTypeHandlerMap<InstanceCreator<?>> DEFAULT_INSTANCE_CREATORS = DefaultTypeAdapters.createDefaultInstanceCreators();

    DefaultTypeAdapters() {
    }

    private static ParameterizedTypeHandlerMap<JsonSerializer<?>> createDefaultSerializers() {
        ParameterizedTypeHandlerMap map = new ParameterizedTypeHandlerMap();
        map.register((Type)((Object)URL.class), URL_TYPE_ADAPTER);
        map.register((Type)((Object)URI.class), URI_TYPE_ADAPTER);
        map.register((Type)((Object)UUID.class), UUUID_TYPE_ADAPTER);
        map.register((Type)((Object)Locale.class), LOCALE_TYPE_ADAPTER);
        map.register((Type)((Object)Date.class), DATE_TYPE_ADAPTER);
        map.register((Type)((Object)java.sql.Date.class), JAVA_SQL_DATE_TYPE_ADAPTER);
        map.register((Type)((Object)Timestamp.class), DATE_TYPE_ADAPTER);
        map.register((Type)((Object)Time.class), TIME_TYPE_ADAPTER);
        map.register((Type)((Object)Calendar.class), GREGORIAN_CALENDAR_TYPE_ADAPTER);
        map.register((Type)((Object)GregorianCalendar.class), GREGORIAN_CALENDAR_TYPE_ADAPTER);
        map.register((Type)((Object)BigDecimal.class), BIG_DECIMAL_TYPE_ADAPTER);
        map.register((Type)((Object)BigInteger.class), BIG_INTEGER_TYPE_ADAPTER);
        map.register((Type)((Object)Boolean.class), BOOLEAN_TYPE_ADAPTER);
        map.register(Boolean.TYPE, BOOLEAN_TYPE_ADAPTER);
        map.register((Type)((Object)Byte.class), BYTE_TYPE_ADAPTER);
        map.register(Byte.TYPE, BYTE_TYPE_ADAPTER);
        map.register((Type)((Object)Character.class), CHARACTER_TYPE_ADAPTER);
        map.register(Character.TYPE, CHARACTER_TYPE_ADAPTER);
        map.register((Type)((Object)Integer.class), INTEGER_TYPE_ADAPTER);
        map.register(Integer.TYPE, INTEGER_TYPE_ADAPTER);
        map.register((Type)((Object)Number.class), NUMBER_TYPE_ADAPTER);
        map.register((Type)((Object)Short.class), SHORT_TYPE_ADAPTER);
        map.register(Short.TYPE, SHORT_TYPE_ADAPTER);
        map.register((Type)((Object)String.class), STRING_TYPE_ADAPTER);
        map.register((Type)((Object)StringBuilder.class), STRING_BUILDER_TYPE_ADAPTER);
        map.register((Type)((Object)StringBuffer.class), STRING_BUFFER_TYPE_ADAPTER);
        map.makeUnmodifiable();
        return map;
    }

    private static ParameterizedTypeHandlerMap<JsonSerializer<?>> createDefaultHierarchySerializers() {
        ParameterizedTypeHandlerMap<JsonSerializer<Object>> map = new ParameterizedTypeHandlerMap<JsonSerializer<Object>>();
        map.registerForTypeHierarchy(Enum.class, ENUM_TYPE_ADAPTER);
        map.registerForTypeHierarchy(InetAddress.class, INET_ADDRESS_ADAPTER);
        map.registerForTypeHierarchy(Collection.class, COLLECTION_TYPE_ADAPTER);
        map.registerForTypeHierarchy(Map.class, MAP_TYPE_ADAPTER);
        map.makeUnmodifiable();
        return map;
    }

    private static ParameterizedTypeHandlerMap<JsonDeserializer<?>> createDefaultDeserializers() {
        ParameterizedTypeHandlerMap map = new ParameterizedTypeHandlerMap();
        map.register((Type)((Object)URL.class), DefaultTypeAdapters.wrapDeserializer(URL_TYPE_ADAPTER));
        map.register((Type)((Object)URI.class), DefaultTypeAdapters.wrapDeserializer(URI_TYPE_ADAPTER));
        map.register((Type)((Object)UUID.class), DefaultTypeAdapters.wrapDeserializer(UUUID_TYPE_ADAPTER));
        map.register((Type)((Object)Locale.class), DefaultTypeAdapters.wrapDeserializer(LOCALE_TYPE_ADAPTER));
        map.register((Type)((Object)Date.class), DefaultTypeAdapters.wrapDeserializer(DATE_TYPE_ADAPTER));
        map.register((Type)((Object)java.sql.Date.class), DefaultTypeAdapters.wrapDeserializer(JAVA_SQL_DATE_TYPE_ADAPTER));
        map.register((Type)((Object)Timestamp.class), DefaultTypeAdapters.wrapDeserializer(TIMESTAMP_DESERIALIZER));
        map.register((Type)((Object)Time.class), DefaultTypeAdapters.wrapDeserializer(TIME_TYPE_ADAPTER));
        map.register((Type)((Object)Calendar.class), GREGORIAN_CALENDAR_TYPE_ADAPTER);
        map.register((Type)((Object)GregorianCalendar.class), GREGORIAN_CALENDAR_TYPE_ADAPTER);
        map.register((Type)((Object)BigDecimal.class), BIG_DECIMAL_TYPE_ADAPTER);
        map.register((Type)((Object)BigInteger.class), BIG_INTEGER_TYPE_ADAPTER);
        map.register((Type)((Object)Boolean.class), BOOLEAN_TYPE_ADAPTER);
        map.register(Boolean.TYPE, BOOLEAN_TYPE_ADAPTER);
        map.register((Type)((Object)Byte.class), BYTE_TYPE_ADAPTER);
        map.register(Byte.TYPE, BYTE_TYPE_ADAPTER);
        map.register((Type)((Object)Character.class), DefaultTypeAdapters.wrapDeserializer(CHARACTER_TYPE_ADAPTER));
        map.register(Character.TYPE, DefaultTypeAdapters.wrapDeserializer(CHARACTER_TYPE_ADAPTER));
        map.register((Type)((Object)Double.class), DOUBLE_TYPE_ADAPTER);
        map.register(Double.TYPE, DOUBLE_TYPE_ADAPTER);
        map.register((Type)((Object)Float.class), FLOAT_TYPE_ADAPTER);
        map.register(Float.TYPE, FLOAT_TYPE_ADAPTER);
        map.register((Type)((Object)Integer.class), INTEGER_TYPE_ADAPTER);
        map.register(Integer.TYPE, INTEGER_TYPE_ADAPTER);
        map.register((Type)((Object)Long.class), LONG_DESERIALIZER);
        map.register(Long.TYPE, LONG_DESERIALIZER);
        map.register((Type)((Object)Number.class), NUMBER_TYPE_ADAPTER);
        map.register((Type)((Object)Short.class), SHORT_TYPE_ADAPTER);
        map.register(Short.TYPE, SHORT_TYPE_ADAPTER);
        map.register((Type)((Object)String.class), DefaultTypeAdapters.wrapDeserializer(STRING_TYPE_ADAPTER));
        map.register((Type)((Object)StringBuilder.class), DefaultTypeAdapters.wrapDeserializer(STRING_BUILDER_TYPE_ADAPTER));
        map.register((Type)((Object)StringBuffer.class), DefaultTypeAdapters.wrapDeserializer(STRING_BUFFER_TYPE_ADAPTER));
        map.makeUnmodifiable();
        return map;
    }

    private static ParameterizedTypeHandlerMap<JsonDeserializer<?>> createDefaultHierarchyDeserializers() {
        ParameterizedTypeHandlerMap<JsonDeserializer<Object>> map = new ParameterizedTypeHandlerMap<JsonDeserializer<Object>>();
        map.registerForTypeHierarchy(Enum.class, DefaultTypeAdapters.wrapDeserializer(ENUM_TYPE_ADAPTER));
        map.registerForTypeHierarchy(InetAddress.class, DefaultTypeAdapters.wrapDeserializer(INET_ADDRESS_ADAPTER));
        map.registerForTypeHierarchy(Collection.class, DefaultTypeAdapters.wrapDeserializer(COLLECTION_TYPE_ADAPTER));
        map.registerForTypeHierarchy(Map.class, DefaultTypeAdapters.wrapDeserializer(MAP_TYPE_ADAPTER));
        map.makeUnmodifiable();
        return map;
    }

    private static ParameterizedTypeHandlerMap<InstanceCreator<?>> createDefaultInstanceCreators() {
        ParameterizedTypeHandlerMap<InstanceCreator<Object>> map = new ParameterizedTypeHandlerMap<InstanceCreator<Object>>();
        DefaultConstructorAllocator allocator = new DefaultConstructorAllocator(50);
        map.registerForTypeHierarchy(Map.class, new DefaultConstructorCreator<LinkedHashMap>(LinkedHashMap.class, allocator));
        DefaultConstructorCreator<ArrayList> listCreator = new DefaultConstructorCreator<ArrayList>(ArrayList.class, allocator);
        DefaultConstructorCreator<LinkedList> queueCreator = new DefaultConstructorCreator<LinkedList>(LinkedList.class, allocator);
        DefaultConstructorCreator<HashSet> setCreator = new DefaultConstructorCreator<HashSet>(HashSet.class, allocator);
        DefaultConstructorCreator<TreeSet> sortedSetCreator = new DefaultConstructorCreator<TreeSet>(TreeSet.class, allocator);
        map.registerForTypeHierarchy(Collection.class, listCreator);
        map.registerForTypeHierarchy(Queue.class, queueCreator);
        map.registerForTypeHierarchy(Set.class, setCreator);
        map.registerForTypeHierarchy(SortedSet.class, sortedSetCreator);
        map.makeUnmodifiable();
        return map;
    }

    private static JsonDeserializer<?> wrapDeserializer(JsonDeserializer<?> deserializer) {
        return new JsonDeserializerExceptionWrapper(deserializer);
    }

    static ParameterizedTypeHandlerMap<JsonSerializer<?>> getDefaultSerializers() {
        return DefaultTypeAdapters.getDefaultSerializers(false, LongSerializationPolicy.DEFAULT);
    }

    static ParameterizedTypeHandlerMap<JsonSerializer<?>> getAllDefaultSerializers() {
        ParameterizedTypeHandlerMap<JsonSerializer<?>> defaultSerializers = DefaultTypeAdapters.getDefaultSerializers(false, LongSerializationPolicy.DEFAULT);
        defaultSerializers.register(DEFAULT_HIERARCHY_SERIALIZERS);
        return defaultSerializers;
    }

    static ParameterizedTypeHandlerMap<JsonDeserializer<?>> getAllDefaultDeserializers() {
        ParameterizedTypeHandlerMap<JsonDeserializer<?>> defaultDeserializers = DefaultTypeAdapters.getDefaultDeserializers().copyOf();
        defaultDeserializers.register(DEFAULT_HIERARCHY_DESERIALIZERS);
        return defaultDeserializers;
    }

    static ParameterizedTypeHandlerMap<JsonSerializer<?>> getDefaultSerializers(boolean serializeSpecialFloatingPointValues, LongSerializationPolicy longSerializationPolicy) {
        ParameterizedTypeHandlerMap serializers = new ParameterizedTypeHandlerMap();
        DoubleSerializer doubleSerializer = new DoubleSerializer(serializeSpecialFloatingPointValues);
        serializers.registerIfAbsent((Type)((Object)Double.class), doubleSerializer);
        serializers.registerIfAbsent(Double.TYPE, doubleSerializer);
        FloatSerializer floatSerializer = new FloatSerializer(serializeSpecialFloatingPointValues);
        serializers.registerIfAbsent((Type)((Object)Float.class), floatSerializer);
        serializers.registerIfAbsent(Float.TYPE, floatSerializer);
        LongSerializer longSerializer = new LongSerializer(longSerializationPolicy);
        serializers.registerIfAbsent((Type)((Object)Long.class), longSerializer);
        serializers.registerIfAbsent(Long.TYPE, longSerializer);
        serializers.registerIfAbsent(DEFAULT_SERIALIZERS);
        return serializers;
    }

    static ParameterizedTypeHandlerMap<JsonDeserializer<?>> getDefaultDeserializers() {
        return DEFAULT_DESERIALIZERS;
    }

    static ParameterizedTypeHandlerMap<InstanceCreator<?>> getDefaultInstanceCreators() {
        return DEFAULT_INSTANCE_CREATORS;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class DefaultConstructorCreator<T>
    implements InstanceCreator<T> {
        private final Class<? extends T> defaultInstance;
        private final DefaultConstructorAllocator allocator;

        public DefaultConstructorCreator(Class<? extends T> defaultInstance, DefaultConstructorAllocator allocator) {
            this.defaultInstance = defaultInstance;
            this.allocator = allocator;
        }

        @Override
        public T createInstance(Type type) {
            Class<?> rawType = $Gson$Types.getRawType(type);
            try {
                Object specificInstance = this.allocator.newInstance(rawType);
                return (T)(specificInstance == null ? this.allocator.newInstance(this.defaultInstance) : specificInstance);
            }
            catch (Exception e) {
                throw new JsonIOException(e);
            }
        }

        public String toString() {
            return DefaultConstructorCreator.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class BooleanTypeAdapter
    implements JsonSerializer<Boolean>,
    JsonDeserializer<Boolean> {
        private BooleanTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Boolean src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }

        @Override
        public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsBoolean();
            }
            catch (UnsupportedOperationException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return BooleanTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class StringBufferTypeAdapter
    implements JsonSerializer<StringBuffer>,
    JsonDeserializer<StringBuffer> {
        private StringBufferTypeAdapter() {
        }

        @Override
        public JsonElement serialize(StringBuffer src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public StringBuffer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new StringBuffer(json.getAsString());
        }

        public String toString() {
            return StringBufferTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class StringBuilderTypeAdapter
    implements JsonSerializer<StringBuilder>,
    JsonDeserializer<StringBuilder> {
        private StringBuilderTypeAdapter() {
        }

        @Override
        public JsonElement serialize(StringBuilder src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public StringBuilder deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new StringBuilder(json.getAsString());
        }

        public String toString() {
            return StringBuilderTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class StringTypeAdapter
    implements JsonSerializer<String>,
    JsonDeserializer<String> {
        private StringTypeAdapter() {
        }

        @Override
        public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }

        @Override
        public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return json.getAsString();
        }

        public String toString() {
            return StringTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class CharacterTypeAdapter
    implements JsonSerializer<Character>,
    JsonDeserializer<Character> {
        private CharacterTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Character src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }

        @Override
        public Character deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Character.valueOf(json.getAsCharacter());
        }

        public String toString() {
            return CharacterTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class DoubleDeserializer
    implements JsonDeserializer<Double> {
        private DoubleDeserializer() {
        }

        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsDouble();
            }
            catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
            catch (UnsupportedOperationException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return DoubleDeserializer.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class DoubleSerializer
    implements JsonSerializer<Double> {
        private final boolean serializeSpecialFloatingPointValues;

        DoubleSerializer(boolean serializeSpecialDoubleValues) {
            this.serializeSpecialFloatingPointValues = serializeSpecialDoubleValues;
        }

        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            if (!this.serializeSpecialFloatingPointValues && (Double.isNaN(src) || Double.isInfinite(src))) {
                throw new IllegalArgumentException(src + " is not a valid double value as per JSON specification. To override this" + " behavior, use GsonBuilder.serializeSpecialDoubleValues() method.");
            }
            return new JsonPrimitive(src);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class FloatDeserializer
    implements JsonDeserializer<Float> {
        private FloatDeserializer() {
        }

        @Override
        public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return Float.valueOf(json.getAsFloat());
            }
            catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
            catch (UnsupportedOperationException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return FloatDeserializer.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class FloatSerializer
    implements JsonSerializer<Float> {
        private final boolean serializeSpecialFloatingPointValues;

        FloatSerializer(boolean serializeSpecialDoubleValues) {
            this.serializeSpecialFloatingPointValues = serializeSpecialDoubleValues;
        }

        @Override
        public JsonElement serialize(Float src, Type typeOfSrc, JsonSerializationContext context) {
            if (!this.serializeSpecialFloatingPointValues && (Float.isNaN(src.floatValue()) || Float.isInfinite(src.floatValue()))) {
                throw new IllegalArgumentException(src + " is not a valid float value as per JSON specification. To override this" + " behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
            }
            return new JsonPrimitive(src);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class ByteTypeAdapter
    implements JsonSerializer<Byte>,
    JsonDeserializer<Byte> {
        private ByteTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Byte src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }

        @Override
        public Byte deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsByte();
            }
            catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
            catch (UnsupportedOperationException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return ByteTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class ShortTypeAdapter
    implements JsonSerializer<Short>,
    JsonDeserializer<Short> {
        private ShortTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Short src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }

        @Override
        public Short deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsShort();
            }
            catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
            catch (UnsupportedOperationException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return ShortTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class IntegerTypeAdapter
    implements JsonSerializer<Integer>,
    JsonDeserializer<Integer> {
        private IntegerTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }

        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsInt();
            }
            catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
            catch (UnsupportedOperationException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return IntegerTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class LongDeserializer
    implements JsonDeserializer<Long> {
        private LongDeserializer() {
        }

        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsLong();
            }
            catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
            catch (UnsupportedOperationException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return LongDeserializer.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class LongSerializer
    implements JsonSerializer<Long> {
        private final LongSerializationPolicy longSerializationPolicy;

        private LongSerializer(LongSerializationPolicy longSerializationPolicy) {
            this.longSerializationPolicy = longSerializationPolicy;
        }

        @Override
        public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
            return this.longSerializationPolicy.serialize(src);
        }

        public String toString() {
            return LongSerializer.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class NumberTypeAdapter
    implements JsonSerializer<Number>,
    JsonDeserializer<Number> {
        private NumberTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Number src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }

        @Override
        public Number deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsNumber();
            }
            catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
            catch (UnsupportedOperationException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return NumberTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class BigIntegerTypeAdapter
    implements JsonSerializer<BigInteger>,
    JsonDeserializer<BigInteger> {
        private BigIntegerTypeAdapter() {
        }

        @Override
        public JsonElement serialize(BigInteger src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }

        @Override
        public BigInteger deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsBigInteger();
            }
            catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
            catch (UnsupportedOperationException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return BigIntegerTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class BigDecimalTypeAdapter
    implements JsonSerializer<BigDecimal>,
    JsonDeserializer<BigDecimal> {
        private BigDecimalTypeAdapter() {
        }

        @Override
        public JsonElement serialize(BigDecimal src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }

        @Override
        public BigDecimal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return json.getAsBigDecimal();
            }
            catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
            catch (UnsupportedOperationException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return BigDecimalTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class CollectionTypeAdapter
    implements JsonSerializer<Collection>,
    JsonDeserializer<Collection> {
        private CollectionTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Collection src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return JsonNull.createJsonNull();
            }
            JsonArray array = new JsonArray();
            Type childGenericType = null;
            if (typeOfSrc instanceof ParameterizedType) {
                Class<?> rawTypeOfSrc = $Gson$Types.getRawType(typeOfSrc);
                childGenericType = $Gson$Types.getCollectionElementType(typeOfSrc, rawTypeOfSrc);
            }
            for (Object child : src) {
                if (child == null) {
                    array.add(JsonNull.createJsonNull());
                    continue;
                }
                Type childType = childGenericType == null || childGenericType == Object.class ? child.getClass() : childGenericType;
                JsonElement element = context.serialize(child, childType);
                array.add(element);
            }
            return array;
        }

        @Override
        public Collection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull()) {
                return null;
            }
            Collection collection = this.constructCollectionType(typeOfT, context);
            Type childType = $Gson$Types.getCollectionElementType(typeOfT, $Gson$Types.getRawType(typeOfT));
            for (JsonElement childElement : json.getAsJsonArray()) {
                if (childElement == null || childElement.isJsonNull()) {
                    collection.add(null);
                    continue;
                }
                Object value = context.deserialize(childElement, childType);
                collection.add(value);
            }
            return collection;
        }

        private Collection constructCollectionType(Type collectionType, JsonDeserializationContext context) {
            JsonDeserializationContextDefault contextImpl = (JsonDeserializationContextDefault)context;
            ObjectConstructor objectConstructor = contextImpl.getObjectConstructor();
            return (Collection)objectConstructor.construct(collectionType);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class LocaleTypeAdapter
    implements JsonSerializer<Locale>,
    JsonDeserializer<Locale> {
        private LocaleTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Locale src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public Locale deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String locale = json.getAsString();
            StringTokenizer tokenizer = new StringTokenizer(locale, "_");
            String language = null;
            String country = null;
            String variant = null;
            if (tokenizer.hasMoreElements()) {
                language = tokenizer.nextToken();
            }
            if (tokenizer.hasMoreElements()) {
                country = tokenizer.nextToken();
            }
            if (tokenizer.hasMoreElements()) {
                variant = tokenizer.nextToken();
            }
            if (country == null && variant == null) {
                return new Locale(language);
            }
            if (variant == null) {
                return new Locale(language, country);
            }
            return new Locale(language, country, variant);
        }

        public String toString() {
            return LocaleTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class UuidTypeAdapter
    implements JsonSerializer<UUID>,
    JsonDeserializer<UUID> {
        private UuidTypeAdapter() {
        }

        @Override
        public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return UUID.fromString(json.getAsString());
        }

        public String toString() {
            return UuidTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class UriTypeAdapter
    implements JsonSerializer<URI>,
    JsonDeserializer<URI> {
        private UriTypeAdapter() {
        }

        @Override
        public JsonElement serialize(URI src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toASCIIString());
        }

        @Override
        public URI deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return new URI(json.getAsString());
            }
            catch (URISyntaxException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return UriTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class UrlTypeAdapter
    implements JsonSerializer<URL>,
    JsonDeserializer<URL> {
        private UrlTypeAdapter() {
        }

        @Override
        public JsonElement serialize(URL src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toExternalForm());
        }

        @Override
        public URL deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return new URL(json.getAsString());
            }
            catch (MalformedURLException e) {
                throw new JsonSyntaxException(e);
            }
        }

        public String toString() {
            return UrlTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class EnumTypeAdapter<T extends Enum<T>>
    implements JsonSerializer<T>,
    JsonDeserializer<T> {
        private EnumTypeAdapter() {
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(((Enum)src).name());
        }

        @Override
        public T deserialize(JsonElement json, Type classOfT, JsonDeserializationContext context) throws JsonParseException {
            return Enum.valueOf((Class)classOfT, json.getAsString());
        }

        public String toString() {
            return EnumTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class DefaultInetAddressAdapter
    implements JsonDeserializer<InetAddress>,
    JsonSerializer<InetAddress> {
        DefaultInetAddressAdapter() {
        }

        @Override
        public InetAddress deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return InetAddress.getByName(json.getAsString());
            }
            catch (UnknownHostException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(InetAddress src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getHostAddress());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class GregorianCalendarTypeAdapter
    implements JsonSerializer<GregorianCalendar>,
    JsonDeserializer<GregorianCalendar> {
        private static final String YEAR = "year";
        private static final String MONTH = "month";
        private static final String DAY_OF_MONTH = "dayOfMonth";
        private static final String HOUR_OF_DAY = "hourOfDay";
        private static final String MINUTE = "minute";
        private static final String SECOND = "second";

        private GregorianCalendarTypeAdapter() {
        }

        @Override
        public JsonElement serialize(GregorianCalendar src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty(YEAR, src.get(1));
            obj.addProperty(MONTH, src.get(2));
            obj.addProperty(DAY_OF_MONTH, src.get(5));
            obj.addProperty(HOUR_OF_DAY, src.get(11));
            obj.addProperty(MINUTE, src.get(12));
            obj.addProperty(SECOND, src.get(13));
            return obj;
        }

        @Override
        public GregorianCalendar deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            int year = obj.get(YEAR).getAsInt();
            int month = obj.get(MONTH).getAsInt();
            int dayOfMonth = obj.get(DAY_OF_MONTH).getAsInt();
            int hourOfDay = obj.get(HOUR_OF_DAY).getAsInt();
            int minute = obj.get(MINUTE).getAsInt();
            int second = obj.get(SECOND).getAsInt();
            return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second);
        }

        public String toString() {
            return GregorianCalendarTypeAdapter.class.getSimpleName();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class DefaultTimeTypeAdapter
    implements JsonSerializer<Time>,
    JsonDeserializer<Time> {
        private final DateFormat format = new SimpleDateFormat("hh:mm:ss a");

        DefaultTimeTypeAdapter() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public JsonElement serialize(Time src, Type typeOfSrc, JsonSerializationContext context) {
            DateFormat dateFormat = this.format;
            synchronized (dateFormat) {
                String dateFormatAsString = this.format.format(src);
                return new JsonPrimitive(dateFormatAsString);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Time deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }
            try {
                DateFormat dateFormat = this.format;
                synchronized (dateFormat) {
                    Date date = this.format.parse(json.getAsString());
                    return new Time(date.getTime());
                }
            }
            catch (ParseException e) {
                throw new JsonSyntaxException(e);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class DefaultTimestampDeserializer
    implements JsonDeserializer<Timestamp> {
        DefaultTimestampDeserializer() {
        }

        @Override
        public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Date date = (Date)context.deserialize(json, (Type)((Object)Date.class));
            return new Timestamp(date.getTime());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class DefaultJavaSqlDateTypeAdapter
    implements JsonSerializer<java.sql.Date>,
    JsonDeserializer<java.sql.Date> {
        private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");

        DefaultJavaSqlDateTypeAdapter() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public JsonElement serialize(java.sql.Date src, Type typeOfSrc, JsonSerializationContext context) {
            DateFormat dateFormat = this.format;
            synchronized (dateFormat) {
                String dateFormatAsString = this.format.format(src);
                return new JsonPrimitive(dateFormatAsString);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public java.sql.Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }
            try {
                DateFormat dateFormat = this.format;
                synchronized (dateFormat) {
                    Date date = this.format.parse(json.getAsString());
                    return new java.sql.Date(date.getTime());
                }
            }
            catch (ParseException e) {
                throw new JsonSyntaxException(e);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class DefaultDateTypeAdapter
    implements JsonSerializer<Date>,
    JsonDeserializer<Date> {
        private final DateFormat enUsFormat;
        private final DateFormat localFormat;
        private final DateFormat iso8601Format;

        DefaultDateTypeAdapter() {
            this(DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
        }

        DefaultDateTypeAdapter(String datePattern) {
            this(new SimpleDateFormat(datePattern, Locale.US), new SimpleDateFormat(datePattern));
        }

        DefaultDateTypeAdapter(int style) {
            this(DateFormat.getDateInstance(style, Locale.US), DateFormat.getDateInstance(style));
        }

        public DefaultDateTypeAdapter(int dateStyle, int timeStyle) {
            this(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US), DateFormat.getDateTimeInstance(dateStyle, timeStyle));
        }

        DefaultDateTypeAdapter(DateFormat enUsFormat, DateFormat localFormat) {
            this.enUsFormat = enUsFormat;
            this.localFormat = localFormat;
            this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            this.iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            DateFormat dateFormat = this.localFormat;
            synchronized (dateFormat) {
                String dateFormatAsString = this.enUsFormat.format(src);
                return new JsonPrimitive(dateFormatAsString);
            }
        }

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!(json instanceof JsonPrimitive)) {
                throw new JsonParseException("The date should be a string value");
            }
            Date date = this.deserializeToDate(json);
            if (typeOfT == Date.class) {
                return date;
            }
            if (typeOfT == Timestamp.class) {
                return new Timestamp(date.getTime());
            }
            if (typeOfT == java.sql.Date.class) {
                return new java.sql.Date(date.getTime());
            }
            throw new IllegalArgumentException(this.getClass() + " cannot deserialize to " + typeOfT);
        }

        private Date deserializeToDate(JsonElement json) {
            DateFormat dateFormat = this.localFormat;
            synchronized (dateFormat) {
                try {
                    return this.localFormat.parse(json.getAsString());
                }
                catch (ParseException ignored) {
                    try {
                        return this.enUsFormat.parse(json.getAsString());
                    }
                    catch (ParseException ignored2) {
                        try {
                            return this.iso8601Format.parse(json.getAsString());
                        }
                        catch (ParseException e) {
                            throw new JsonSyntaxException(json.getAsString(), e);
                        }
                    }
                }
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(DefaultDateTypeAdapter.class.getSimpleName());
            sb.append('(').append(this.localFormat.getClass().getSimpleName()).append(')');
            return sb.toString();
        }
    }
}

