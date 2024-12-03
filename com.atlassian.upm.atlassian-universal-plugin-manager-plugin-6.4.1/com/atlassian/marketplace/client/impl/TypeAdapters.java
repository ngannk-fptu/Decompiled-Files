/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  io.atlassian.fugue.Option
 *  org.joda.time.DateTime
 *  org.joda.time.LocalDate
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.api.ApplicationKey;
import com.atlassian.marketplace.client.api.EnumWithKey;
import com.atlassian.marketplace.client.api.UriTemplate;
import com.atlassian.marketplace.client.encoding.DateFormats;
import com.atlassian.marketplace.client.encoding.InvalidFieldValue;
import com.atlassian.marketplace.client.impl.EntityValidator;
import com.atlassian.marketplace.client.impl.SchemaViolationException;
import com.atlassian.marketplace.client.model.HtmlString;
import com.atlassian.marketplace.client.model.Link;
import com.atlassian.marketplace.client.model.Links;
import com.atlassian.marketplace.client.model.ReadOnly;
import com.atlassian.marketplace.client.model.RequiredLink;
import com.atlassian.marketplace.client.util.Convert;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.atlassian.fugue.Option;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

abstract class TypeAdapters {
    private static TypeAdapterFactory factoryWithReadOnlyFields = TypeAdapters.makeTypeAdapterFactory(true);
    private static TypeAdapterFactory factoryWithoutReadOnlyFields = TypeAdapters.makeTypeAdapterFactory(false);
    private static final Function<ApplicationKey, String> appKeyToString = ApplicationKey::getKey;
    private static final Function<String, ApplicationKey> stringToAppKey = ApplicationKey::valueOf;
    private static final Function<DateTime, String> dateTimeToString = arg_0 -> ((DateTimeFormatter)DateFormats.DATE_TIME_FORMAT).print(arg_0);
    private static final Function<String, DateTime> stringToDateTime = arg_0 -> ((DateTimeFormatter)DateFormats.DATE_TIME_FORMAT).parseDateTime(arg_0);
    private static final Function<HtmlString, String> htmlStringToString = HtmlString::getHtml;
    private static final Function<String, HtmlString> stringToHtmlString = HtmlString::html;
    private static final Function<LocalDate, String> localDateToString = arg_0 -> ((DateTimeFormatter)DateFormats.DATE_FORMAT).print(arg_0);
    private static final Function<String, LocalDate> stringToLocalDate = value -> DateFormats.DATE_FORMAT.parseDateTime(value).toLocalDate();
    private static final Function<URI, String> uriToString = URI::toASCIIString;
    private static final Function<String, URI> stringToUri = URI::create;
    private static final Map<Class<?>, Object> ADAPTERS = ImmutableMap.builder().put(ApplicationKey.class, TypeAdapters.stringLikeTypeAdapter(appKeyToString, stringToAppKey)).put(DateTime.class, TypeAdapters.stringLikeTypeAdapter(dateTimeToString, stringToDateTime)).put(HtmlString.class, TypeAdapters.stringLikeTypeAdapter(htmlStringToString, stringToHtmlString)).put(LocalDate.class, TypeAdapters.stringLikeTypeAdapter(localDateToString, stringToLocalDate)).put(URI.class, TypeAdapters.stringLikeTypeAdapter(uriToString, stringToUri)).put(ImmutableList.class, (Object)new ListTypeAdapter()).put(ImmutableMap.class, (Object)new MapTypeAdapter()).put(Option.class, (Object)new OptionTypeAdapter()).put(Link.class, (Object)new LinkTypeAdapter()).put(Links.class, (Object)new LinksTypeAdapter()).build();

    private TypeAdapters() {
    }

    public static Map<Class<?>, Object> all() {
        return ADAPTERS;
    }

    static <A extends EnumWithKey> TypeAdapter<A> enumTypeAdapter(final Class<?> enumClass) {
        final EnumWithKey.Parser<?> parser = EnumWithKey.Parser.forType(enumClass);
        return new TypeAdapter<A>(){

            @Override
            public void write(JsonWriter out, A value) throws IOException {
                out.value(value.getKey());
            }

            @Override
            public A read(JsonReader in) throws IOException {
                String s = in.nextString();
                Iterator iterator = Convert.iterableOf(parser.safeValueForKey(s)).iterator();
                if (iterator.hasNext()) {
                    EnumWithKey v = (EnumWithKey)iterator.next();
                    return v;
                }
                throw new SchemaViolationException(new InvalidFieldValue(s, enumClass));
            }
        };
    }

    static <A> TypeAdapter<A> objectTypeAdapter(Gson gson, TypeToken<A> typeToken, boolean includeReadOnlyFields) {
        TypeAdapterFactory factory = includeReadOnlyFields ? factoryWithReadOnlyFields : factoryWithoutReadOnlyFields;
        final TypeAdapter<A> baseAdapter = factory.create(gson, typeToken);
        return new TypeAdapter<A>(){

            @Override
            public void write(JsonWriter out, A value) throws IOException {
                baseAdapter.write(out, value);
            }

            @Override
            public A read(JsonReader in) throws IOException {
                try {
                    return EntityValidator.validateInstance(baseAdapter.read(in));
                }
                catch (SchemaViolationException e) {
                    throw new JsonParseException(e);
                }
            }
        };
    }

    private static <A> JsonSerDeser<A> stringLikeTypeAdapter(final Function<A, String> writer, final Function<String, A> reader) {
        return new JsonSerDeser<A>(){

            @Override
            public JsonElement serialize(A value, Type type, JsonSerializationContext context) {
                return new JsonPrimitive((String)writer.apply(value));
            }

            @Override
            public A deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return reader.apply(json.getAsString());
                }
                catch (IllegalArgumentException e) {
                    String s = json.isJsonPrimitive() ? json.getAsString() : json.toString();
                    throw new SchemaViolationException(new InvalidFieldValue(s, (Class)type));
                }
            }
        };
    }

    private static TypeAdapterFactory makeTypeAdapterFactory(boolean includeReadOnlyFields) {
        return new ReflectiveTypeAdapterFactory(new ConstructorConstructor((Map<Type, InstanceCreator<?>>)ImmutableMap.of()), FieldNamingPolicy.IDENTITY, new Excluder().withExclusionStrategy(new CustomExclusionStrategy(includeReadOnlyFields), true, true), new JsonAdapterAnnotationTypeAdapterFactory(new ConstructorConstructor((Map<Type, InstanceCreator<?>>)ImmutableMap.of())));
    }

    private static class OptionTypeAdapter
    implements JsonDeserializer<Option<?>>,
    JsonSerializer<Option<?>> {
        private OptionTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Option<?> o, Type type, JsonSerializationContext context) {
            Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
            Iterator iterator = o.iterator();
            if (iterator.hasNext()) {
                Object value = iterator.next();
                return context.serialize(value, typeArguments[0]);
            }
            return JsonNull.INSTANCE;
        }

        @Override
        public Option<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonNull()) {
                return Option.none();
            }
            Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
            return Option.some(context.deserialize(json, typeArguments[0]));
        }
    }

    private static class MapTypeAdapter
    implements JsonDeserializer<ImmutableMap<?, ?>>,
    JsonSerializer<ImmutableMap<?, ?>> {
        private MapTypeAdapter() {
        }

        @Override
        public ImmutableMap<?, ?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
            Type mapType = this.makeMapType(typeArguments[0], typeArguments[1]);
            return ImmutableMap.copyOf((Map)((Map)context.deserialize(json, mapType)));
        }

        @Override
        public JsonElement serialize(ImmutableMap<?, ?> map, Type type, JsonSerializationContext context) {
            Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
            Type mapType = this.makeMapType(typeArguments[0], typeArguments[1]);
            return context.serialize(map, mapType);
        }

        private <A, B> Type makeMapType(Type keyTypeParam, Type valueTypeParam) {
            return $Gson$Types.newParameterizedTypeWithOwner(null, Map.class, new Type[]{keyTypeParam, valueTypeParam});
        }
    }

    private static class ListTypeAdapter
    implements JsonDeserializer<ImmutableList<?>>,
    JsonSerializer<ImmutableList<?>> {
        private ListTypeAdapter() {
        }

        @Override
        public ImmutableList<?> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
            Type listType = this.makeListType(typeArguments[0]);
            return ImmutableList.copyOf((Collection)((List)context.deserialize(json, listType)));
        }

        @Override
        public JsonElement serialize(ImmutableList<?> list, Type type, JsonSerializationContext context) {
            Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
            Type listType = this.makeListType(typeArguments[0]);
            return context.serialize(list, listType);
        }

        private <T> Type makeListType(Type typeParam) {
            return $Gson$Types.newParameterizedTypeWithOwner(null, List.class, new Type[]{typeParam});
        }
    }

    private static class LinksTypeAdapter
    implements JsonDeserializer<Links>,
    JsonSerializer<Links> {
        private static final Type linkListType = new TypeToken<List<Link>>(){}.getType();

        private LinksTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Links links, Type type, JsonSerializationContext context) {
            JsonObject out = new JsonObject();
            for (Map.Entry<String, ImmutableList<Link>> e : links.getItems().entrySet()) {
                JsonElement value;
                if (e.getValue().size() == 1) {
                    value = context.serialize(e.getValue().get(0));
                } else {
                    JsonArray a = new JsonArray();
                    for (Link l : e.getValue()) {
                        a.add(context.serialize(l));
                    }
                    value = a;
                }
                out.add(e.getKey(), value);
            }
            return out;
        }

        @Override
        public Links deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            ImmutableMap.Builder links = ImmutableMap.builder();
            JsonObject o = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> e : o.entrySet()) {
                ImmutableList value = e.getValue().isJsonArray() ? ImmutableList.copyOf((Collection)((List)context.deserialize(e.getValue(), linkListType))) : ImmutableList.of(context.deserialize(e.getValue(), (Type)((Object)Link.class)));
                links.put((Object)e.getKey(), (Object)value);
            }
            return new Links((Map<String, ImmutableList<Link>>)links.build());
        }
    }

    private static class LinkTypeAdapter
    implements JsonDeserializer<Link>,
    JsonSerializer<Link> {
        private LinkTypeAdapter() {
        }

        @Override
        public JsonElement serialize(Link link, Type type, JsonSerializationContext context) {
            LinkInternal li = new LinkInternal();
            li.href = (String)link.getTemplateOrUri().fold(UriTemplate::getValue, URI::toASCIIString);
            li.type = link.getType();
            li.templated = link.getUriTemplate().isDefined() ? Option.some((Object)true) : Option.none(Boolean.class);
            return context.serialize(li);
        }

        @Override
        public Link deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            LinkInternal li = (LinkInternal)context.deserialize(json, (Type)((Object)LinkInternal.class));
            if (((Boolean)li.templated.getOrElse((Object)false)).booleanValue()) {
                return Link.fromUriTemplate(UriTemplate.create(li.href), li.type);
            }
            return Link.fromUri(URI.create(li.href), li.type);
        }

        private static class LinkInternal {
            String href;
            Option<String> type;
            Option<Boolean> templated;

            private LinkInternal() {
            }
        }
    }

    private static class CustomExclusionStrategy
    implements ExclusionStrategy {
        private final boolean includeReadOnlyFields;

        CustomExclusionStrategy(boolean includeReadOnlyFields) {
            this.includeReadOnlyFields = includeReadOnlyFields;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(RequiredLink.class) != null || !this.includeReadOnlyFields && f.getAnnotation(ReadOnly.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    private static interface JsonSerDeser<A>
    extends JsonSerializer<A>,
    JsonDeserializer<A> {
    }
}

