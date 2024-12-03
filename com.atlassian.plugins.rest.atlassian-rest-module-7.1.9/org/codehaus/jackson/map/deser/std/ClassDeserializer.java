/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;
import org.codehaus.jackson.map.util.ClassUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class ClassDeserializer
extends StdScalarDeserializer<Class<?>> {
    public ClassDeserializer() {
        super(Class.class);
    }

    @Override
    public Class<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken curr = jp.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING) {
            String className = jp.getText();
            try {
                return ClassUtil.findClass(className);
            }
            catch (ClassNotFoundException e) {
                throw ctxt.instantiationException(this._valueClass, e);
            }
        }
        throw ctxt.mappingException(this._valueClass, curr);
    }
}

