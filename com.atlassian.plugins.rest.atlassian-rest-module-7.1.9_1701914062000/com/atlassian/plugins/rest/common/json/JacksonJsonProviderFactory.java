/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.json;

import com.atlassian.plugins.rest.common.json.GuavaIterableCapableModule;
import java.util.Collections;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class JacksonJsonProviderFactory {
    public JacksonJsonProvider create() {
        return this.create(Collections.emptyList());
    }

    public JacksonJsonProvider create(Iterable<? extends Module> modules) {
        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector intr = AnnotationIntrospector.pair(new JacksonAnnotationIntrospector(), new JaxbAnnotationIntrospector());
        mapper.setDeserializationConfig(mapper.getDeserializationConfig().withAnnotationIntrospector(intr));
        mapper.setSerializationConfig(mapper.getSerializationConfig().withAnnotationIntrospector(intr));
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        for (Module module : modules) {
            mapper.registerModule(module);
        }
        mapper.registerModule(new GuavaIterableCapableModule());
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider(mapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
        provider.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, false);
        provider.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS, false);
        provider.configure(DeserializationConfig.Feature.AUTO_DETECT_SETTERS, false);
        provider.configure(DeserializationConfig.Feature.AUTO_DETECT_FIELDS, false);
        return provider;
    }
}

