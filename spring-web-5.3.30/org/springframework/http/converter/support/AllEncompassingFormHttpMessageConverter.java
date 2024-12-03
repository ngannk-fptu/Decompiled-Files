/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.SpringProperties
 *  org.springframework.util.ClassUtils
 */
package org.springframework.http.converter.support;

import org.springframework.core.SpringProperties;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.smile.MappingJackson2SmileHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.util.ClassUtils;

public class AllEncompassingFormHttpMessageConverter
extends FormHttpMessageConverter {
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag((String)"spring.xml.ignore");
    private static final boolean jaxb2Present;
    private static final boolean jackson2Present;
    private static final boolean jackson2XmlPresent;
    private static final boolean jackson2SmilePresent;
    private static final boolean gsonPresent;
    private static final boolean jsonbPresent;
    private static final boolean kotlinSerializationJsonPresent;

    public AllEncompassingFormHttpMessageConverter() {
        if (!shouldIgnoreXml) {
            try {
                this.addPartConverter(new SourceHttpMessageConverter());
            }
            catch (Error error) {
                // empty catch block
            }
            if (jaxb2Present && !jackson2XmlPresent) {
                this.addPartConverter(new Jaxb2RootElementHttpMessageConverter());
            }
        }
        if (kotlinSerializationJsonPresent) {
            this.addPartConverter(new KotlinSerializationJsonHttpMessageConverter());
        }
        if (jackson2Present) {
            this.addPartConverter(new MappingJackson2HttpMessageConverter());
        } else if (gsonPresent) {
            this.addPartConverter(new GsonHttpMessageConverter());
        } else if (jsonbPresent) {
            this.addPartConverter(new JsonbHttpMessageConverter());
        }
        if (jackson2XmlPresent && !shouldIgnoreXml) {
            this.addPartConverter(new MappingJackson2XmlHttpMessageConverter());
        }
        if (jackson2SmilePresent) {
            this.addPartConverter(new MappingJackson2SmileHttpMessageConverter());
        }
    }

    static {
        ClassLoader classLoader = AllEncompassingFormHttpMessageConverter.class.getClassLoader();
        jaxb2Present = ClassUtils.isPresent((String)"javax.xml.bind.Binder", (ClassLoader)classLoader);
        jackson2Present = ClassUtils.isPresent((String)"com.fasterxml.jackson.databind.ObjectMapper", (ClassLoader)classLoader) && ClassUtils.isPresent((String)"com.fasterxml.jackson.core.JsonGenerator", (ClassLoader)classLoader);
        jackson2XmlPresent = ClassUtils.isPresent((String)"com.fasterxml.jackson.dataformat.xml.XmlMapper", (ClassLoader)classLoader);
        jackson2SmilePresent = ClassUtils.isPresent((String)"com.fasterxml.jackson.dataformat.smile.SmileFactory", (ClassLoader)classLoader);
        gsonPresent = ClassUtils.isPresent((String)"com.google.gson.Gson", (ClassLoader)classLoader);
        jsonbPresent = ClassUtils.isPresent((String)"javax.json.bind.Jsonb", (ClassLoader)classLoader);
        kotlinSerializationJsonPresent = ClassUtils.isPresent((String)"kotlinx.serialization.json.Json", (ClassLoader)classLoader);
    }
}

