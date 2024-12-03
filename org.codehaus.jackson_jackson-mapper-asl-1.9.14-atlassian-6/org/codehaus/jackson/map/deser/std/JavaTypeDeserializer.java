/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JavaTypeDeserializer
extends StdScalarDeserializer<JavaType> {
    public JavaTypeDeserializer() {
        super(JavaType.class);
    }

    @Override
    public JavaType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken curr = jp.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (str.length() == 0) {
                return (JavaType)this.getEmptyValue();
            }
            return ctxt.getTypeFactory().constructFromCanonical(str);
        }
        if (curr == JsonToken.VALUE_EMBEDDED_OBJECT) {
            return (JavaType)jp.getEmbeddedObject();
        }
        throw ctxt.mappingException(this._valueClass);
    }
}

