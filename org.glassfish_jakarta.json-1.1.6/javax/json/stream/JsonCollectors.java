/*
 * Decompiled with CFR 0.152.
 */
package javax.json.stream;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public final class JsonCollectors {
    private JsonCollectors() {
    }

    public static Collector<JsonValue, JsonArrayBuilder, JsonArray> toJsonArray() {
        return Collector.of(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::addAll, JsonArrayBuilder::build, new Collector.Characteristics[0]);
    }

    public static Collector<Map.Entry<String, JsonValue>, JsonObjectBuilder, JsonObject> toJsonObject() {
        return Collector.of(Json::createObjectBuilder, (b, v) -> b.add((String)v.getKey(), (JsonValue)v.getValue()), JsonObjectBuilder::addAll, JsonObjectBuilder::build, new Collector.Characteristics[0]);
    }

    public static Collector<JsonValue, JsonObjectBuilder, JsonObject> toJsonObject(Function<JsonValue, String> keyMapper, Function<JsonValue, JsonValue> valueMapper) {
        return Collector.of(Json::createObjectBuilder, (b, v) -> b.add((String)keyMapper.apply((JsonValue)v), (JsonValue)valueMapper.apply((JsonValue)v)), JsonObjectBuilder::addAll, JsonObjectBuilder::build, Collector.Characteristics.UNORDERED);
    }

    public static <T extends JsonArrayBuilder> Collector<JsonValue, Map<String, T>, JsonObject> groupingBy(Function<JsonValue, String> classifier, Collector<JsonValue, T, JsonArray> downstream) {
        BiConsumer<Map, JsonValue> accumulator = (map, value) -> {
            String key = (String)classifier.apply((JsonValue)value);
            if (key == null) {
                throw new JsonException("element cannot be mapped to a null key");
            }
            JsonArrayBuilder arrayBuilder = map.computeIfAbsent(key, v -> (JsonArrayBuilder)downstream.supplier().get());
            downstream.accumulator().accept(arrayBuilder, (JsonArrayBuilder)((Object)value));
        };
        Function<Map, JsonObject> finisher = map -> {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            map.forEach((k, v) -> {
                JsonArray array = (JsonArray)downstream.finisher().apply(v);
                objectBuilder.add((String)k, array);
            });
            return objectBuilder.build();
        };
        BinaryOperator combiner = (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        };
        return Collector.of(HashMap::new, accumulator, combiner, finisher, Collector.Characteristics.UNORDERED);
    }

    public static Collector<JsonValue, Map<String, JsonArrayBuilder>, JsonObject> groupingBy(Function<JsonValue, String> classifier) {
        return JsonCollectors.groupingBy(classifier, JsonCollectors.toJsonArray());
    }
}

