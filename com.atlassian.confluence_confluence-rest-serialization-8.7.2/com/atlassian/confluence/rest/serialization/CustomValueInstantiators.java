/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.ApiEnum
 *  org.codehaus.jackson.map.BeanDescription
 *  org.codehaus.jackson.map.DeserializationConfig
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.deser.ValueInstantiator
 *  org.codehaus.jackson.map.deser.ValueInstantiators$Base
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.ApiEnum;
import java.io.IOException;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomValueInstantiators
extends ValueInstantiators.Base {
    private static final Logger log = LoggerFactory.getLogger(CustomValueInstantiators.class);

    public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator) {
        ValueInstantiator valueInstantiator = super.findValueInstantiator(config, beanDesc, defaultInstantiator);
        if (ApiEnum.class.isAssignableFrom(beanDesc.getBeanClass())) {
            return new ApiEnumValueInstantiator(valueInstantiator);
        }
        return valueInstantiator;
    }

    private static class ApiEnumValueInstantiator
    extends ValueInstantiator {
        private final ValueInstantiator valueInstantiator;

        public ApiEnumValueInstantiator(ValueInstantiator valueInstantiator) {
            this.valueInstantiator = valueInstantiator;
        }

        public Object createFromString(String value) throws IOException {
            try {
                return this.valueInstantiator.createFromString(value);
            }
            catch (JsonMappingException e) {
                log.warn("Unable to deserialize object from: {}, turn on debug-level logging for more detail.", (Object)value);
                log.debug("JsonMappingException stacktrace:", (Throwable)e);
                return null;
            }
        }

        public String getValueTypeDesc() {
            return this.valueInstantiator.getValueTypeDesc();
        }
    }
}

