/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.converter.support;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.ClassUtils;

public class AllEncompassingFormHttpMessageConverter
extends FormHttpMessageConverter {
    private static final boolean jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", AllEncompassingFormHttpMessageConverter.class.getClassLoader());
    private static final boolean jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", AllEncompassingFormHttpMessageConverter.class.getClassLoader()) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", AllEncompassingFormHttpMessageConverter.class.getClassLoader());
    private static final boolean jackson2XmlPresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.xml.XmlMapper", AllEncompassingFormHttpMessageConverter.class.getClassLoader());
    private static final boolean jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", AllEncompassingFormHttpMessageConverter.class.getClassLoader());
    private static final boolean gsonPresent = ClassUtils.isPresent("com.google.gson.Gson", AllEncompassingFormHttpMessageConverter.class.getClassLoader());
    private static final boolean jsonbPresent = ClassUtils.isPresent("javax.json.bind.Jsonb", AllEncompassingFormHttpMessageConverter.class.getClassLoader());

    public AllEncompassingFormHttpMessageConverter() {
        this.addPartConverter(new SourceHttpMessageConverter());
        if (jaxb2Present && !jackson2XmlPresent) {
            this.addPartConverter(new Jaxb2RootElementHttpMessageConverter());
        }
        if (jackson2Present) {
            this.addPartConverter(new MappingJackson2HttpMessageConverter());
        } else if (gsonPresent) {
            this.addPartConverter(new GsonHttpMessageConverter());
        } else if (jsonbPresent) {
            this.addPartConverter(new JsonbHttpMessageConverter());
        }
        if (jackson2XmlPresent) {
            this.addPartConverter(new MappingJackson2XmlHttpMessageConverter());
        }
        if (jackson2SmilePresent) {
            this.addPartConverter(new MappingJackson2SmileHttpMessageConverter());
        }
    }
}

