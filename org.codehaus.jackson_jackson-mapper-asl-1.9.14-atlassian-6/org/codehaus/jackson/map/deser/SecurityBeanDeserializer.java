/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.deser;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SecurityBeanDeserializer
extends Deserializers.Base {
    protected static final Set<String> BLOCKED_CLASS_NAMES;

    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, DeserializerProvider provider, BeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
        if (property != null && type != null && property.getType() != null && type.getRawClass() != null) {
            this.checkType(type, property.getType());
            if (property.getType().isContainerType()) {
                this.checkType(type, property.getType().getContentType());
                if (type.isMapLikeType()) {
                    this.checkType(type, property.getType().getKeyType());
                }
            }
        }
        return super.findBeanDeserializer(type, config, provider, beanDesc, property);
    }

    protected void checkType(JavaType type, JavaType fromPropertyType) {
        if (BLOCKED_CLASS_NAMES.contains(fromPropertyType.getRawClass().getName()) && !BLOCKED_CLASS_NAMES.contains(type.getRawClass().getName())) {
            throw new SecurityException(String.format("Prevented for security reasons deserializing %s as %s is too general a type.", type.getRawClass().getName(), fromPropertyType.getRawClass().getName()));
        }
    }

    static {
        HashSet<String> s = new HashSet<String>();
        s.add(Object.class.getName());
        s.add(Comparable.class.getName());
        s.add(Serializable.class.getName());
        BLOCKED_CLASS_NAMES = s;
    }
}

