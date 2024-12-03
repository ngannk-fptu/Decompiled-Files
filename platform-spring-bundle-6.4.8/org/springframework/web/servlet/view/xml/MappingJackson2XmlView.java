/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonView
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.dataformat.xml.XmlMapper
 */
package org.springframework.web.servlet.view.xml;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.Map;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.view.json.AbstractJackson2View;

public class MappingJackson2XmlView
extends AbstractJackson2View {
    public static final String DEFAULT_CONTENT_TYPE = "application/xml";
    @Nullable
    private String modelKey;

    public MappingJackson2XmlView() {
        super((ObjectMapper)Jackson2ObjectMapperBuilder.xml().build(), DEFAULT_CONTENT_TYPE);
    }

    public MappingJackson2XmlView(XmlMapper xmlMapper) {
        super((ObjectMapper)xmlMapper, DEFAULT_CONTENT_TYPE);
    }

    @Override
    public void setModelKey(String modelKey) {
        this.modelKey = modelKey;
    }

    @Override
    protected Object filterModel(Map<String, Object> model) {
        Object value = null;
        if (this.modelKey != null) {
            value = model.get(this.modelKey);
            if (value == null) {
                throw new IllegalStateException("Model contains no object with key [" + this.modelKey + "]");
            }
        } else {
            for (Map.Entry<String, Object> entry : model.entrySet()) {
                if (entry.getValue() instanceof BindingResult || entry.getKey().equals(JsonView.class.getName())) continue;
                if (value != null) {
                    throw new IllegalStateException("Model contains more than one object to render, only one is supported");
                }
                value = entry.getValue();
            }
        }
        Assert.state(value != null, "Model contains no object to render");
        return value;
    }
}

