/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.BeanPropertyFilter;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class SimpleBeanPropertyFilter
implements BeanPropertyFilter {
    protected SimpleBeanPropertyFilter() {
    }

    public static SimpleBeanPropertyFilter filterOutAllExcept(Set<String> properties) {
        return new FilterExceptFilter(properties);
    }

    public static SimpleBeanPropertyFilter filterOutAllExcept(String ... propertyArray) {
        HashSet<String> properties = new HashSet<String>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return new FilterExceptFilter(properties);
    }

    public static SimpleBeanPropertyFilter serializeAllExcept(Set<String> properties) {
        return new SerializeExceptFilter(properties);
    }

    public static SimpleBeanPropertyFilter serializeAllExcept(String ... propertyArray) {
        HashSet<String> properties = new HashSet<String>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return new SerializeExceptFilter(properties);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SerializeExceptFilter
    extends SimpleBeanPropertyFilter {
        protected final Set<String> _propertiesToExclude;

        public SerializeExceptFilter(Set<String> properties) {
            this._propertiesToExclude = properties;
        }

        @Override
        public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider provider, BeanPropertyWriter writer) throws Exception {
            if (!this._propertiesToExclude.contains(writer.getName())) {
                writer.serializeAsField(bean, jgen, provider);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class FilterExceptFilter
    extends SimpleBeanPropertyFilter {
        protected final Set<String> _propertiesToInclude;

        public FilterExceptFilter(Set<String> properties) {
            this._propertiesToInclude = properties;
        }

        @Override
        public void serializeAsField(Object bean, JsonGenerator jgen, SerializerProvider provider, BeanPropertyWriter writer) throws Exception {
            if (this._propertiesToInclude.contains(writer.getName())) {
                writer.serializeAsField(bean, jgen, provider);
            }
        }
    }
}

