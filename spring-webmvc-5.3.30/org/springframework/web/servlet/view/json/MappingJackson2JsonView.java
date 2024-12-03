/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonView
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.ser.FilterProvider
 *  org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
 *  org.springframework.lang.Nullable
 *  org.springframework.util.CollectionUtils
 *  org.springframework.validation.BindingResult
 */
package org.springframework.web.servlet.view.json;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.json.AbstractJackson2View;

public class MappingJackson2JsonView
extends AbstractJackson2View {
    public static final String DEFAULT_CONTENT_TYPE = "application/json";
    @Nullable
    private String jsonPrefix;
    @Nullable
    private Set<String> modelKeys;
    private boolean extractValueFromSingleKeyModel = false;

    public MappingJackson2JsonView() {
        super(Jackson2ObjectMapperBuilder.json().build(), DEFAULT_CONTENT_TYPE);
    }

    public MappingJackson2JsonView(ObjectMapper objectMapper) {
        super(objectMapper, DEFAULT_CONTENT_TYPE);
    }

    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = prefixJson ? ")]}', " : null;
    }

    @Override
    public void setModelKey(String modelKey) {
        this.modelKeys = Collections.singleton(modelKey);
    }

    public void setModelKeys(@Nullable Set<String> modelKeys) {
        this.modelKeys = modelKeys;
    }

    @Nullable
    public final Set<String> getModelKeys() {
        return this.modelKeys;
    }

    public void setExtractValueFromSingleKeyModel(boolean extractValueFromSingleKeyModel) {
        this.extractValueFromSingleKeyModel = extractValueFromSingleKeyModel;
    }

    @Override
    protected Object filterModel(Map<String, Object> model) {
        HashMap result = CollectionUtils.newHashMap((int)model.size());
        Set<String> modelKeys = !CollectionUtils.isEmpty(this.modelKeys) ? this.modelKeys : model.keySet();
        model.forEach((clazz, value) -> {
            if (!(value instanceof BindingResult) && modelKeys.contains(clazz) && !clazz.equals(JsonView.class.getName()) && !clazz.equals(FilterProvider.class.getName())) {
                result.put(clazz, value);
            }
        });
        return this.extractValueFromSingleKeyModel && result.size() == 1 ? result.values().iterator().next() : result;
    }

    @Override
    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }
    }
}

