/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.plugin.spring.scanner.annotation.component.ClasspathComponent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.soy.renderer.SoyDataMapper
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.restapi.soy;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.plugin.spring.scanner.annotation.component.ClasspathComponent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.soy.renderer.SoyDataMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={SoyDataMapper.class})
@Component(value="jackson2SoyDataTransformer")
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class Jackson2SoyPreDigester
implements SoyDataMapper<Object, Object> {
    private ObjectMapper objectMapper;
    private static final Function<JsonNode, Object> jsonNode2PrimitiveOrContainerTransformer = new Function<JsonNode, Object>(){

        @Override
        public @Nullable Object apply(JsonNode input) {
            if (input.isArray()) {
                ImmutableList.Builder listBuilder = ImmutableList.builder();
                Iterator values = input.getElements();
                while (values.hasNext()) {
                    Object value = this.apply((JsonNode)values.next());
                    if (value == null) continue;
                    listBuilder.add(value);
                }
                return listBuilder.build();
            }
            if (input.isContainerNode()) {
                ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
                Iterator fieldIterator = input.getFields();
                while (fieldIterator.hasNext()) {
                    Map.Entry field = (Map.Entry)fieldIterator.next();
                    Object value = this.apply((JsonNode)field.getValue());
                    if (value == null) continue;
                    mapBuilder.put((Object)((String)field.getKey()), value);
                }
                return mapBuilder.build();
            }
            if (input.isNull()) {
                return null;
            }
            return input.asText();
        }
    };

    @Autowired
    public Jackson2SoyPreDigester(@ClasspathComponent ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getName() {
        return "jackson2soy";
    }

    public Object convert(Object input) {
        JsonNode rootNode = this.objectMapper.valueToTree(input);
        Object preDigestedObject = jsonNode2PrimitiveOrContainerTransformer.apply(rootNode);
        return preDigestedObject == null ? "" : preDigestedObject;
    }
}

